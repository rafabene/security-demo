package com.demo.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Substitui @EnableGlobalMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable) // Nova sintaxe Spring Boot 3.x
            .authorizeHttpRequests(auth -> auth // authorizeRequests → authorizeHttpRequests
                // VULNERABILIDADE: Endpoints sensíveis sem autenticação
                .requestMatchers("/api/auth/**").permitAll() // antMatchers → requestMatchers
                .requestMatchers("/api/usuarios/**").permitAll() // VULNERABILIDADE: Acesso livre aos usuários
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/contas/buscar-por-saldo/**").permitAll() // VULNERABILIDADE: Consulta de saldos sem auth
                .requestMatchers("/api/vulneravel/**").permitAll() // VULNERABILIDADE: Endpoints com SQL Injection real
                .requestMatchers("/error").permitAll() // Permite acesso ao endpoint de erro para evitar stack traces desnecessários
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers
                // VULNERABILIDADE: Headers de segurança desabilitados
                .frameOptions(frameOptions -> frameOptions.disable())
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig.disable())
                .contentTypeOptions(contentTypeOptions -> contentTypeOptions.disable())
            )
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // VULNERABILIDADE: PasswordEncoder customizado que não criptografa
        // Simula o comportamento do NoOpPasswordEncoder depreciado
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                // VULNERABILIDADE: Retorna senha em texto claro
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                // VULNERABILIDADE: Comparação simples sem criptografia
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }
}