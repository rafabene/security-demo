package com.demo.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String cpf = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);

            // VULNERABILIDADE: Log do token JWT completo
            logger.debug("Token JWT recebido: {}", jwt);

            try {
                cpf = jwtUtil.extractCpf(jwt);

                // VULNERABILIDADE: Log do CPF extraído do token
                logger.debug("CPF extraído do token: {}", cpf);

            } catch (Exception e) {
                // VULNERABILIDADE: Log detalhado do erro com token
                logger.warn("Erro ao extrair CPF do token: {} - Token: {}", e.getMessage(), jwt);
            }
        }

        if (cpf != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // VULNERABILIDADE: Usar validação insegura que não verifica expiração
            if (jwtUtil.validateTokenInsecure(jwt)) {

                String role = jwtUtil.extractRole(jwt);

                // VULNERABILIDADE: Log de informações de autenticação
                logger.info("Autenticando usuário - CPF: {}, Role: {}, Token: {}", cpf, role, jwt);

                UserDetails userDetails = User.builder()
                        .username(cpf)
                        .password("") // VULNERABILIDADE: Senha vazia
                        .authorities(new ArrayList<>()) // VULNERABILIDADE: Sem verificação de roles
                        .build();

                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // VULNERABILIDADE: Log de sucesso na autenticação com dados sensíveis
                logger.info("Usuário autenticado com sucesso - CPF: {}, IP: {}, User-Agent: {}",
                           cpf, request.getRemoteAddr(), request.getHeader("User-Agent"));
            } else {
                // VULNERABILIDADE: Log de falha com token inválido
                logger.warn("Token inválido para CPF: {} - Token: {}", cpf, jwt);
            }
        }

        filterChain.doFilter(request, response);
    }
}