package com.demo.security.service;

import com.demo.security.model.ContaBancaria;
import com.demo.security.model.Transacao;
import com.demo.security.repository.ContaBancariaRepository;
import com.demo.security.repository.TransacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {

    private static final Logger logger = LoggerFactory.getLogger(TransacaoService.class);

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaBancariaRepository contaBancariaRepository;

    public Transacao realizarTransferencia(String contaOrigemNum, String contaDestinoNum, BigDecimal valor,
            String cpfUsuario) {
        // VULNERABILIDADE: Logging de PII (CPF) em claro
        logger.info("Iniciando transferência para usuário CPF: {} de conta {} para conta {} no valor de R$ {}",
                cpfUsuario, contaOrigemNum, contaDestinoNum, valor);

        Optional<ContaBancaria> contaOrigem = contaBancariaRepository.findByNumeroConta(contaOrigemNum);
        Optional<ContaBancaria> contaDestino = contaBancariaRepository.findByNumeroConta(contaDestinoNum);

        if (!contaOrigem.isPresent() || !contaDestino.isPresent()) {
            // VULNERABILIDADE: Logging detalhado de erro com informações sensíveis
            logger.error("Falha na transferência - CPF: {}, Conta origem: {}, Conta destino: {}, " +
                    "Origem existe: {}, Destino existe: {}",
                    cpfUsuario, contaOrigemNum, contaDestinoNum,
                    contaOrigem.isPresent(), contaDestino.isPresent());
            throw new RuntimeException("Contas inválidas");
        }

        ContaBancaria origem = contaOrigem.get();
        ContaBancaria destino = contaDestino.get();

        // VULNERABILIDADE: Log com informações financeiras sensíveis
        logger.debug("Dados da conta origem - Número: {}, Saldo atual: {}, Titular CPF: {}",
                origem.getNumeroConta(), origem.getSaldo(), origem.getUsuario().getCpf());

        if (origem.getSaldo().compareTo(valor) < 0) {
            // VULNERABILIDADE: Log expondo saldo insuficiente com valores exatos
            logger.warn("Saldo insuficiente para CPF: {} - Saldo: R$ {}, Tentativa de saque: R$ {}",
                    cpfUsuario, origem.getSaldo(), valor);
            throw new RuntimeException("Saldo insuficiente");
        }

        // Realizar transferência
        origem.setSaldo(origem.getSaldo().subtract(valor));
        destino.setSaldo(destino.getSaldo().add(valor));

        contaBancariaRepository.save(origem);
        contaBancariaRepository.save(destino);

        Transacao transacao = new Transacao("TRANSFERENCIA", valor, origem, destino,
                "Transferência entre contas");

        Transacao savedTransacao = transacaoRepository.save(transacao);

        // VULNERABILIDADE: Log de sucesso com dados completos da transação
        logger.info("Transferência realizada com sucesso - ID: {}, CPF origem: {}, " +
                "Conta origem: {}, Conta destino: {}, Valor: R$ {}, " +
                "Novo saldo origem: R$ {}, Novo saldo destino: R$ {}",
                savedTransacao.getId(), cpfUsuario, contaOrigemNum, contaDestinoNum,
                valor, origem.getSaldo(), destino.getSaldo());

        return savedTransacao;
    }

    public List<Transacao> buscarTransacoesPorTipo(String tipo) {
        // VULNERABILIDADE: Usando método com SQL Injection
        logger.info("Buscando transações do tipo: {}", tipo);
        return transacaoRepository.findByTipoVulneravel(tipo);
    }

    public List<Transacao> buscarTransacoesPorConta(Long contaId) {
        return transacaoRepository.findByContaOrigemId(contaId);
    }
}