package com.demo.security.controller;

import com.demo.security.model.Transacao;
import com.demo.security.service.TransacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private static final Logger logger = LoggerFactory.getLogger(TransacaoController.class);

    @Autowired
    private TransacaoService transacaoService;

    @PostMapping("/transferir")
    public ResponseEntity<?> realizarTransferencia(@RequestBody Map<String, String> dados) {
        try {
            String contaOrigem = dados.get("contaOrigem");
            String contaDestino = dados.get("contaDestino");
            BigDecimal valor = new BigDecimal(dados.get("valor"));
            String cpfUsuario = dados.get("cpfUsuario");

            // VULNERABILIDADE: Log de dados da transferência com informações sensíveis
            logger.info("Solicitação de transferência - CPF: {}, Origem: {}, Destino: {}, Valor: R$ {}",
                       cpfUsuario, contaOrigem, contaDestino, valor);

            Transacao transacao = transacaoService.realizarTransferencia(contaOrigem, contaDestino, valor, cpfUsuario);

            return ResponseEntity.ok(transacao);

        } catch (Exception e) {
            // VULNERABILIDADE: Log detalhado de erro com dados da transação
            logger.error("Erro na transferência - Dados: {}, Erro: {}", dados, e.getMessage());
            return ResponseEntity.badRequest().body("Erro na transferência: " + e.getMessage());
        }
    }

    @GetMapping("/buscar-por-tipo/{tipo}")
    public ResponseEntity<?> buscarTransacoesPorTipo(@PathVariable String tipo) {
        // VULNERABILIDADE: Endpoint sem autenticação que usa SQL Injection
        logger.info("Busca de transações por tipo: {}", tipo);

        try {
            // VULNERABILIDADE: Usar método com SQL Injection
            List<Transacao> transacoes = transacaoService.buscarTransacoesPorTipo(tipo);

            // VULNERABILIDADE: Log de dados das transações
            for (Transacao transacao : transacoes) {
                logger.debug("Transação encontrada - ID: {}, Tipo: {}, Valor: R$ {}, " +
                           "Conta Origem: {}, Conta Destino: {}",
                           transacao.getId(), transacao.getTipo(), transacao.getValor(),
                           transacao.getContaOrigem() != null ? transacao.getContaOrigem().getNumeroConta() : "N/A",
                           transacao.getContaDestino() != null ? transacao.getContaDestino().getNumeroConta() : "N/A");
            }

            return ResponseEntity.ok(transacoes);

        } catch (Exception e) {
            logger.error("Erro na busca por tipo - Tipo: {}, Erro: {}", tipo, e.getMessage());
            return ResponseEntity.badRequest().body("Erro na consulta: " + e.getMessage());
        }
    }

    @GetMapping("/conta/{contaId}")
    public ResponseEntity<?> buscarTransacoesPorConta(@PathVariable Long contaId) {
        try {
            List<Transacao> transacoes = transacaoService.buscarTransacoesPorConta(contaId);

            // VULNERABILIDADE: Log de transações específicas de uma conta
            logger.info("Transações da conta {}: {} transações encontradas", contaId, transacoes.size());

            return ResponseEntity.ok(transacoes);

        } catch (Exception e) {
            logger.error("Erro na busca por conta - Conta ID: {}, Erro: {}", contaId, e.getMessage());
            return ResponseEntity.badRequest().body("Erro na consulta: " + e.getMessage());
        }
    }
}