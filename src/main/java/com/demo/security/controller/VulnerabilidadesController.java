package com.demo.security.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
}