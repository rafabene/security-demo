package com.demo.security.controller;

import com.demo.security.config.JwtUtil;
import com.demo.security.model.Usuario;
import com.demo.security.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String cpf = credentials.get("cpf");
        String senha = credentials.get("senha");

        // VULNERABILIDADE: Log de credenciais de login
        logger.info("Tentativa de login - CPF: {}, Senha: {}, IP: {}",
                   cpf, senha, "REQUEST_IP_NOT_AVAILABLE");

        try {
            Usuario usuario = usuarioService.autenticarUsuario(cpf, senha);

            // VULNERABILIDADE: Role hardcoded sem verificação
            String token = jwtUtil.generateToken(cpf, "USER");

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuario", usuario); // VULNERABILIDADE: Retorna dados completos do usuário
            response.put("message", "Login realizado com sucesso");

            // VULNERABILIDADE: Log do token gerado
            logger.info("Login bem-sucedido - CPF: {}, Token gerado: {}", cpf, token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // VULNERABILIDADE: Log detalhado de erro com credenciais
            logger.error("Falha no login - CPF: {}, Senha tentada: {}, Erro: {}",
                        cpf, senha, e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Credenciais inválidas");
            errorResponse.put("details", e.getMessage()); // VULNERABILIDADE: Exposição de detalhes internos

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        try {
            String cpf = userData.get("cpf");
            String nome = userData.get("nome");
            String senha = userData.get("senha");
            String email = userData.get("email");

            // VULNERABILIDADE: Log dos dados de registro incluindo senha
            logger.info("Novo registro - CPF: {}, Nome: {}, Email: {}, Senha: {}",
                       cpf, nome, email, senha);

            Usuario usuario = usuarioService.criarUsuario(cpf, nome, senha, email);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuário criado com sucesso");
            response.put("usuario", usuario); // VULNERABILIDADE: Retorna dados completos incluindo senha

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // VULNERABILIDADE: Log de erro com dados sensíveis
            logger.error("Erro no registro - Dados: {}, Erro: {}", userData, e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao criar usuário");
            errorResponse.put("details", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}