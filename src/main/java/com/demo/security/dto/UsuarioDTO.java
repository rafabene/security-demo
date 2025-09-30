package com.demo.security.dto;

import com.demo.security.model.Usuario;

/**
 * ✅ JAVA 21: Record para DTO de usuário
 *
 * Substitui classes tradicionais com getters/setters por um record imutável
 * que automaticamente gera equals(), hashCode(), toString() e getters.
 */
public record UsuarioDTO(
    Long id,
    String nome,
    String cpfMascarado,
    String emailMascarado,
    String status
) {

    /**
     * ✅ JAVA 21: Construtor compacto com validação
     */
    public UsuarioDTO {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        // ✅ JAVA 21: Valores padrão para campos opcionais
        if (cpfMascarado == null) {
            cpfMascarado = "***.***.***-**";
        }
        if (emailMascarado == null) {
            emailMascarado = "***@***.***";
        }
        if (status == null) {
            status = "ATIVO";
        }
    }

    /**
     * ✅ JAVA 21: Factory method para criação a partir de entidade
     */
    public static UsuarioDTO from(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }

        return new UsuarioDTO(
            usuario.getId(),
            usuario.getNome(),
            maskCpf(usuario.getCpf()),
            maskEmail(usuario.getEmail()),
            "ATIVO"
        );
    }

    /**
     * ✅ JAVA 21: Pattern matching para mascaramento de CPF
     */
    private static String maskCpf(String cpf) {
        if (cpf == null) {
            return "***.***.***-**";
        }
        if (cpf.length() == 11) {
            return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
        }
        return "***.***.***-**";
    }

    /**
     * ✅ JAVA 21: Pattern matching para mascaramento de email
     */
    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***@***.***";
        }

        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);

        String maskedLocal = localPart.length() > 1 ?
            localPart.charAt(0) + "***" : "***";
        String maskedDomain = domainPart.length() > 4 ?
            domainPart.charAt(0) + "***." + domainPart.substring(domainPart.lastIndexOf('.')) : "***";

        return maskedLocal + "@" + maskedDomain;
    }
}