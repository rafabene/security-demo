package com.demo.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
                // VULNERABILIDADE: Endpoints sensíveis sem autenticação
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/usuarios/**").permitAll() // VULNERABILIDADE: Acesso livre aos usuários
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/api/contas/buscar-por-saldo/**").permitAll() // VULNERABILIDADE: Consulta de saldos sem auth
                .antMatchers("/api/vulneravel/**").permitAll() // VULNERABILIDADE: Endpoints com SQL Injection real
                .antMatchers("/error").permitAll() // Permite acesso ao endpoint de erro para evitar stack traces desnecessários
                .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // VULNERABILIDADE: Headers de segurança desabilitados
        http.headers().frameOptions().disable();
        http.headers().httpStrictTransportSecurity().disable();
        http.headers().contentTypeOptions().disable();

        return http.build();
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