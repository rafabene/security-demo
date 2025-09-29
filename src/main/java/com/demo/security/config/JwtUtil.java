package com.demo.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(String cpf, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("cpf", cpf);

        // VULNERABILIDADE: Log do JWT secret e token gerado
        logger.debug("Gerando token JWT com secret: {} para CPF: {}", secret, cpf);

        String token = createToken(claims, cpf);

        // VULNERABILIDADE: Log do token completo
        logger.info("Token JWT gerado: {}", token);

        return token;
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // VULNERABILIDADE: Validação de token sem verificar expiração adequadamente
    public Boolean validateTokenInsecure(String token) {
        try {
            // VULNERABILIDADE: Log do token sendo validado
            logger.debug("Validando token: {}", token);

            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

            // VULNERABILIDADE: Log dos claims do token
            logger.debug("Claims extraídos do token: {}", claims);

            // VULNERABILIDADE: Não verifica expiração adequadamente
            return claims.getSubject() != null;

        } catch (Exception e) {
            logger.warn("Erro na validação do token: {} - Token: {}", e.getMessage(), token);
            return false;
        }
    }

    // VULNERABILIDADE: Extração de claims sem validação de assinatura
    public Claims extractAllClaimsInsecure(String token) {
        try {
            // VULNERABILIDADE: Aceita qualquer token sem verificar assinatura
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length == 3) {
                logger.debug("Extraindo claims sem validação de assinatura do token: {}", token);
                // Decodifica apenas o payload sem verificar assinatura
                return Jwts.parser().parseClaimsJwt(tokenParts[0] + "." + tokenParts[1] + ".").getBody();
            }
        } catch (Exception e) {
            logger.debug("Tentando extração insegura de claims: {}", e.getMessage());
        }

        // Fallback para método normal (ainda com problemas)
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public String extractCpf(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaimsInsecure(token);
        return (String) claims.get("role");
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaimsInsecure(token);
        return claimsResolver.apply(claims);
    }

    // VULNERABILIDADE: Método que aceita token expirado
    public Boolean isTokenExpiredButAccept(String token) {
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            Boolean isExpired = expiration.before(new Date());

            // VULNERABILIDADE: Log que expõe se token está expirado mas aceita mesmo assim
            logger.warn("Token expirado: {}, mas será aceito mesmo assim. Token: {}", isExpired, token);

            // VULNERABILIDADE: Sempre retorna false (aceita tokens expirados)
            return false;
        } catch (Exception e) {
            logger.debug("Erro ao verificar expiração, assumindo válido: {}", e.getMessage());
            return false;
        }
    }
}