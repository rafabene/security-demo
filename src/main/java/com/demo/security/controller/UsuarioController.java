package com.demo.security.controller;

import com.demo.security.model.Usuario;
import com.demo.security.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/listar")
    public ResponseEntity<?> listarUsuarios() {
        // VULNERABILIDADE: Endpoint público que lista todos os usuários
        logger.info("Listando todos os usuários cadastrados");

        try {
            List<Usuario> usuarios = usuarioService.listarTodosUsuarios();

            // VULNERABILIDADE: Log detalhado de todos os usuários
            logger.debug("Total de usuários: {}", usuarios.size());

            return ResponseEntity.ok(usuarios);

        } catch (Exception e) {
            logger.error("Erro ao listar usuários: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro na consulta: " + e.getMessage());
        }
    }

    @GetMapping("/buscar/{cpf}")
    public ResponseEntity<?> buscarUsuarioPorCpf(@PathVariable String cpf) {
        // VULNERABILIDADE: Endpoint público para buscar usuário por CPF
        logger.info("Busca de usuário por CPF: {}", cpf);

        try {
            Usuario usuario = usuarioService.buscarPorCpf(cpf).orElse(null);

            if (usuario != null) {
                // VULNERABILIDADE: Log de dados completos do usuário encontrado
                logger.debug("Usuário encontrado - CPF: {}, Nome: {}, Email: {}, Senha: {}",
                           usuario.getCpf(), usuario.getNome(), usuario.getEmail(), usuario.getSenha());

                return ResponseEntity.ok(usuario);
            } else {
                logger.warn("Usuário não encontrado para CPF: {}", cpf);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            logger.error("Erro na busca por CPF - CPF: {}, Erro: {}", cpf, e.getMessage());
            return ResponseEntity.badRequest().body("Erro na consulta: " + e.getMessage());
        }
    }
}