package com.demo.security.config;

import com.demo.security.model.ContaBancaria;
import com.demo.security.model.Usuario;
import com.demo.security.repository.ContaBancariaRepository;
import com.demo.security.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContaBancariaRepository contaBancariaRepository;

    @Override
    public void run(String... args) throws Exception {
        // VULNERABILIDADE: Log de dados de inicialização com informações sensíveis
        logger.info("Inicializando dados de teste...");

        // Criar usuários de teste
        Usuario usuario1 = new Usuario("12345678901", "João Silva", "senha123", "joao@email.com");
        Usuario usuario2 = new Usuario("98765432100", "Maria Santos", "minhasenha", "maria@email.com");
        Usuario usuario3 = new Usuario("11122233344", "Carlos Admin", "admin123", "admin@bank.com");

        // VULNERABILIDADE: Log dos dados dos usuários incluindo senhas
        logger.debug("Criando usuário: CPF {}, Nome: {}, Email: {}, Senha: {}",
                    usuario1.getCpf(), usuario1.getNome(), usuario1.getEmail(), usuario1.getSenha());
        logger.debug("Criando usuário: CPF {}, Nome: {}, Email: {}, Senha: {}",
                    usuario2.getCpf(), usuario2.getNome(), usuario2.getEmail(), usuario2.getSenha());
        logger.debug("Criando usuário: CPF {}, Nome: {}, Email: {}, Senha: {}",
                    usuario3.getCpf(), usuario3.getNome(), usuario3.getEmail(), usuario3.getSenha());

        usuarioRepository.save(usuario1);
        usuarioRepository.save(usuario2);
        usuarioRepository.save(usuario3);

        // Criar contas bancárias
        ContaBancaria conta1 = new ContaBancaria("12345-6", new BigDecimal("15000.50"), usuario1);
        ContaBancaria conta2 = new ContaBancaria("98765-4", new BigDecimal("25000.75"), usuario2);
        ContaBancaria conta3 = new ContaBancaria("11111-1", new BigDecimal("100000.00"), usuario3);

        // VULNERABILIDADE: Log dos dados das contas com saldos
        logger.debug("Criando conta: Número {}, Saldo R$ {}, Titular CPF: {}",
                    conta1.getNumeroConta(), conta1.getSaldo(), conta1.getUsuario().getCpf());
        logger.debug("Criando conta: Número {}, Saldo R$ {}, Titular CPF: {}",
                    conta2.getNumeroConta(), conta2.getSaldo(), conta2.getUsuario().getCpf());
        logger.debug("Criando conta: Número {}, Saldo R$ {}, Titular CPF: {}",
                    conta3.getNumeroConta(), conta3.getSaldo(), conta3.getUsuario().getCpf());

        contaBancariaRepository.save(conta1);
        contaBancariaRepository.save(conta2);
        contaBancariaRepository.save(conta3);

        // VULNERABILIDADE: Log consolidado com todos os dados
        logger.info("Dados de teste criados com sucesso!");
        logger.info("=== DADOS DE TESTE CRIADOS ===");
        logger.info("Usuário 1: CPF {}, Senha: {}, Conta: {}, Saldo: R$ {}",
                   usuario1.getCpf(), usuario1.getSenha(), conta1.getNumeroConta(), conta1.getSaldo());
        logger.info("Usuário 2: CPF {}, Senha: {}, Conta: {}, Saldo: R$ {}",
                   usuario2.getCpf(), usuario2.getSenha(), conta2.getNumeroConta(), conta2.getSaldo());
        logger.info("Usuário 3: CPF {}, Senha: {}, Conta: {}, Saldo: R$ {}",
                   usuario3.getCpf(), usuario3.getSenha(), conta3.getNumeroConta(), conta3.getSaldo());
        logger.info("==============================");
    }
}