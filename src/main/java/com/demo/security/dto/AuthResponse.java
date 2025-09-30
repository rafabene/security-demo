package com.demo.security.dto;

/**
 * ✅ JAVA 21: Record para resposta de autenticação
 */
public record AuthResponse(
    String token,
    UsuarioDTO usuario,
    String message,
    long expiresIn
) {

    /**
     * ✅ JAVA 21: Validação no construtor compacto
     */
    public AuthResponse {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token não pode ser nulo");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }
        if (message == null || message.isBlank()) {
            message = "Autenticação realizada com sucesso";
        }
        if (expiresIn <= 0) {
            expiresIn = 3600; // 1 hora por padrão
        }
    }

    /**
     * ✅ JAVA 21: Factory method para sucesso
     */
    public static AuthResponse success(String token, UsuarioDTO usuario, long expiresIn) {
        return new AuthResponse(
            token,
            usuario,
            "Login realizado com sucesso",
            expiresIn
        );
    }

    /**
     * ✅ JAVA 21: Factory method para token renovado
     */
    public static AuthResponse renewed(String newToken, UsuarioDTO usuario, long expiresIn) {
        return new AuthResponse(
            newToken,
            usuario,
            "Token renovado com sucesso",
            expiresIn
        );
    }

    /**
     * ✅ JAVA 21: Método para verificar se está próximo do vencimento
     */
    public boolean isNearExpiration() {
        long currentTime = System.currentTimeMillis() / 1000;
        long timeUntilExpiration = expiresIn - currentTime;
        return timeUntilExpiration < 300; // 5 minutos
    }
}