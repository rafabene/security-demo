package com.demo.security.service;

import com.demo.security.model.Usuario;
import com.demo.security.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario criarUsuario(String cpf, String nome, String senha, String email) {
        // VULNERABILIDADE: Logging de PII (CPF, email, e até mesmo senha!)
        logger.info("Criando novo usuário - CPF: {}, Nome: {}, Email: {}, Senha: {}",
                    cpf, nome, email, senha);

        Usuario usuario = new Usuario(cpf, nome, senha, email);
        Usuario savedUsuario = usuarioRepository.save(usuario);

        // VULNERABILIDADE: Log de dados completos do usuário incluindo senha
        logger.debug("Usuário criado com sucesso - ID: {}, CPF: {}, Nome: {}, Email: {}, Senha: {}",
                    savedUsuario.getId(), savedUsuario.getCpf(),
                    savedUsuario.getNome(), savedUsuario.getEmail(), savedUsuario.getSenha());

        return savedUsuario;
    }

    public Usuario autenticarUsuario(String cpf, String senha) {
        // VULNERABILIDADE: Log de tentativa de login com credenciais
        logger.info("Tentativa de login - CPF: {}, Senha fornecida: {}", cpf, senha);

        // VULNERABILIDADE: Usando método com SQL Injection
        List<Usuario> usuarios = usuarioRepository.loginVulneravel(cpf, senha);

        if (usuarios.isEmpty()) {
            // VULNERABILIDADE: Log de falha de autenticação com dados sensíveis
            logger.warn("Falha na autenticação - CPF: {}, Senha tentada: {}", cpf, senha);
            throw new RuntimeException("Credenciais inválidas");
        }

        Usuario usuario = usuarios.get(0);

        // VULNERABILIDADE: Log de sucesso com dados completos
        logger.info("Login realizado com sucesso - CPF: {}, Nome: {}, Email: {}",
                   usuario.getCpf(), usuario.getNome(), usuario.getEmail());

        return usuario;
    }

    public Optional<Usuario> buscarPorCpf(String cpf) {
        // VULNERABILIDADE: Log de busca com CPF
        logger.debug("Buscando usuário por CPF: {}", cpf);
        return usuarioRepository.findByCpf(cpf);
    }

    public List<Usuario> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        // VULNERABILIDADE: Log de lista completa de usuários com dados sensíveis
        for (Usuario usuario : usuarios) {
            logger.debug("Usuário encontrado - ID: {}, CPF: {}, Nome: {}, Email: {}",
                        usuario.getId(), usuario.getCpf(), usuario.getNome(), usuario.getEmail());
        }

        return usuarios;
    }
}