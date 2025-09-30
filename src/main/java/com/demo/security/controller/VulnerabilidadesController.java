package com.demo.security.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vulneravel")
public class VulnerabilidadesController {

    private static final Logger logger = LoggerFactory.getLogger(VulnerabilidadesController.class);
    private static final org.apache.logging.log4j.Logger log4jLogger = LogManager.getLogger(VulnerabilidadesController.class);

    @Autowired
    private DataSource dataSource;

    @GetMapping("/sql-injection-saldo/{valor}")
    public ResponseEntity<?> sqlInjectionSaldo(@PathVariable String valor) {
        // VULNERABILIDADE: SQL Injection através de concatenação direta
        logger.info("Executando busca por saldo com valor: {}", valor);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // VULNERABILIDADE: Query SQL construída por concatenação
            String sql = "SELECT c.numero_conta, c.saldo, u.cpf, u.nome FROM contas_bancarias c " +
                        "JOIN usuarios u ON c.usuario_id = u.id " +
                        "WHERE c.saldo >= " + valor;

            logger.debug("Query SQL executada: {}", sql);

            ResultSet rs = stmt.executeQuery(sql);
            List<Map<String, Object>> resultados = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> conta = new HashMap<>();
                conta.put("numeroConta", rs.getString("numero_conta"));
                conta.put("saldo", rs.getBigDecimal("saldo"));
                conta.put("cpf", rs.getString("cpf"));
                conta.put("nome", rs.getString("nome"));
                resultados.add(conta);
            }

            return ResponseEntity.ok(resultados);

        } catch (Exception e) {
            logger.error("Erro na consulta SQL: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro na consulta: " + e.getMessage());
        }
    }

    @GetMapping("/sql-injection-conta/{numeroConta}")
    public ResponseEntity<?> sqlInjectionConta(@PathVariable String numeroConta) {
        // VULNERABILIDADE: SQL Injection em busca por número de conta
        logger.info("Executando busca por conta com número: {}", numeroConta);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // VULNERABILIDADE: Query SQL com concatenação direta
            String sql = "SELECT c.numero_conta, c.saldo, u.cpf, u.nome FROM contas_bancarias c " +
                        "JOIN usuarios u ON c.usuario_id = u.id " +
                        "WHERE c.numero_conta = '" + numeroConta + "'";

            logger.debug("Query SQL executada: {}", sql);

            ResultSet rs = stmt.executeQuery(sql);
            List<Map<String, Object>> resultados = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> conta = new HashMap<>();
                conta.put("numeroConta", rs.getString("numero_conta"));
                conta.put("saldo", rs.getBigDecimal("saldo"));
                conta.put("cpf", rs.getString("cpf"));
                conta.put("nome", rs.getString("nome"));
                resultados.add(conta);
            }

            return ResponseEntity.ok(resultados);

        } catch (Exception e) {
            logger.error("Erro na consulta SQL: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro na consulta: " + e.getMessage());
        }
    }

    @PostMapping("/sql-injection-login")
    public ResponseEntity<?> sqlInjectionLogin(@RequestBody Map<String, String> dados) {
        // VULNERABILIDADE: SQL Injection no login
        String cpf = dados.get("cpf");
        String senha = dados.get("senha");

        logger.info("Tentativa de login com CPF: {} e senha: {}", cpf, senha);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // VULNERABILIDADE: Query de login com concatenação direta
            String sql = "SELECT u.id, u.cpf, u.nome, u.email FROM usuarios u " +
                        "WHERE u.cpf = '" + cpf + "' AND u.senha = '" + senha + "'";

            logger.debug("Query SQL de login executada: {}", sql);

            ResultSet rs = stmt.executeQuery(sql);
            List<Map<String, Object>> usuarios = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> usuario = new HashMap<>();
                usuario.put("id", rs.getLong("id"));
                usuario.put("cpf", rs.getString("cpf"));
                usuario.put("nome", rs.getString("nome"));
                usuario.put("email", rs.getString("email"));
                usuarios.add(usuario);
            }

            if (!usuarios.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login realizado com sucesso via SQL Injection!");
                response.put("usuario", usuarios.get(0));
                response.put("totalUsuarios", usuarios.size());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Credenciais inválidas"));
            }

        } catch (Exception e) {
            logger.error("Erro na consulta de login: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro na consulta: " + e.getMessage());
        }
    }

    // VULNERABILIDADE: Log4Shell (CVE-2021-44228) - SIMULAÇÃO
    @PostMapping("/log4shell")
    public ResponseEntity<?> log4shellDemo(@RequestBody Map<String, String> payload) {
        String userInput = payload.getOrDefault("message", "");

        logger.warn("⚠️ SIMULAÇÃO LOG4SHELL - Payload: {}", userInput);

        // SIMULAÇÃO: Forçar lookup direto como em versão vulnerável
        try {
            // Em versão vulnerável, Log4j faria o lookup JNDI automaticamente
            log4jLogger.error("VULNERÁVEL - Processando: " + userInput);

            // Simular o que aconteceria em uma versão vulnerável
            if (userInput.contains("${jndi:")) {
                String jndiUrl = userInput.substring(userInput.indexOf("${jndi:") + 7, userInput.indexOf("}"));

                logger.error("🔴 LOG4SHELL: Tentando lookup JNDI para: {}", jndiUrl);

                // Simular delay de rede
                Thread.sleep(100);

                // Gerar exception realista baseada no protocolo
                if (jndiUrl.contains("ldap://")) {
                    // Simular exception LDAP real
                    throw new RuntimeException("javax.naming.CommunicationException: " + jndiUrl.split("/")[2] + " [Root exception is java.net.ConnectException: Connection refused]");
                } else if (jndiUrl.contains("rmi://")) {
                    // Simular exception RMI real com mais detalhes
                    String rmiHost = jndiUrl.split("/")[2];
                    logger.error("🔴 LOG4SHELL RMI: Tentando conectar ao RMI Registry em: {}", rmiHost);
                    Thread.sleep(50); // Simular tempo de lookup RMI
                    throw new RuntimeException("java.rmi.ConnectException: Connection refused to host: " + rmiHost + "; nested exception is: java.net.ConnectException: Connection refused");
                } else {
                    // Simular exception genérica
                    throw new RuntimeException("javax.naming.NoInitialContextException: Need to specify class name in environment or system property");
                }
            }
        } catch (Exception e) {
            // Esta é a exception que apareceria em logs reais!
            logger.error("🔴 LOG4SHELL EXCEPTION: {}", e.getMessage());

            if (userInput.contains("rmi://")) {
                logger.warn("⚠️ RMI EXPLOIT: Em versão vulnerável com RMI Registry ativo:");
                logger.warn("⚠️ 1. JNDI faria lookup no RMI Registry");
                logger.warn("⚠️ 2. Baixaria objeto Java malicioso serializado");
                logger.warn("⚠️ 3. Executaria código no processo de deserialização");
                logger.warn("⚠️ 4. RCE CRÍTICO alcançado!");
            } else {
                logger.warn("⚠️ Em versão vulnerável, se servidor existisse: RCE CRÍTICO!");
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Log4Shell simulado com exception realista");
        response.put("received", userInput);
        response.put("vulnerability", "CVE-2021-44228 (Log4Shell)");
        response.put("status", userInput.contains("${jndi:") ? "VULNERÁVEL - Exception gerada" : "Payload seguro");

        return ResponseEntity.ok(response);
    }
}