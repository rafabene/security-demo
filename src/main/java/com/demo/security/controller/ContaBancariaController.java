package com.demo.security.controller;

import com.demo.security.model.ContaBancaria;
import com.demo.security.repository.ContaBancariaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
public class ContaBancariaController {

    private static final Logger logger = LoggerFactory.getLogger(ContaBancariaController.class);

    @Autowired
    private ContaBancariaRepository contaBancariaRepository;

    @GetMapping("/buscar-por-numero/{numeroConta}")
    public ResponseEntity<?> buscarPorNumeroConta(@PathVariable String numeroConta) {
        // VULNERABILIDADE: Endpoint sem autenticação + SQL Injection
        logger.info("Busca por número de conta: {}", numeroConta);

        try {
            // VULNERABILIDADE: Usar método com SQL Injection
            List<ContaBancaria> contas = contaBancariaRepository.findByNumeroContaVulneravel(numeroConta);

            // VULNERABILIDADE: Log dos dados encontrados
            for (ContaBancaria conta : contas) {
                logger.debug("Conta encontrada - Número: {}, Saldo: R$ {}, Titular CPF: {}",
                           conta.getNumeroConta(), conta.getSaldo(), conta.getUsuario().getCpf());
            }

            return ResponseEntity.ok(contas);

        } catch (Exception e) {
            logger.error("Erro na busca por número de conta: {} - Parâmetro: {}", e.getMessage(), numeroConta);
            return ResponseEntity.badRequest().body("Erro na consulta: " + e.getMessage());
        }
    }

    @GetMapping("/buscar-por-saldo/{saldoMinimo}")
    public ResponseEntity<?> buscarContasComSaldoMaiorQue(@PathVariable String saldoMinimo) {
        // VULNERABILIDADE: Endpoint público que expõe informações financeiras + SQL Injection
        logger.info("Busca por contas com saldo maior que: {}", saldoMinimo);

        try {
            // VULNERABILIDADE: SQL Injection na consulta de saldo
            List<ContaBancaria> contas = contaBancariaRepository.findContasComSaldoMaiorQue(saldoMinimo);

            // VULNERABILIDADE: Log de informações financeiras sensíveis
            for (ContaBancaria conta : contas) {
                logger.info("Conta com saldo alto - CPF: {}, Conta: {}, Saldo: R$ {}",
                           conta.getUsuario().getCpf(), conta.getNumeroConta(), conta.getSaldo());
            }

            // VULNERABILIDADE: Retorna dados financeiros completos sem autenticação
            return ResponseEntity.ok(contas);

        } catch (Exception e) {
            logger.error("Erro na busca por saldo - Parâmetro: {}, Erro: {}", saldoMinimo, e.getMessage());
            return ResponseEntity.badRequest().body("Erro na consulta: " + e.getMessage());
        }
    }

    @GetMapping("/listar-todas")
    public ResponseEntity<?> listarTodasContas() {
        // VULNERABILIDADE: Endpoint que lista todas as contas sem autenticação
        logger.info("Listando todas as contas bancárias");

        try {
            List<ContaBancaria> contas = contaBancariaRepository.findAll();

            // VULNERABILIDADE: Log de todas as contas
            logger.debug("Total de contas encontradas: {}", contas.size());
            for (ContaBancaria conta : contas) {
                logger.debug("Conta: {} - Titular: {} (CPF: {}) - Saldo: R$ {}",
                           conta.getNumeroConta(), conta.getUsuario().getNome(),
                           conta.getUsuario().getCpf(), conta.getSaldo());
            }

            return ResponseEntity.ok(contas);

        } catch (Exception e) {
            logger.error("Erro ao listar contas: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro na consulta: " + e.getMessage());
        }
    }
}