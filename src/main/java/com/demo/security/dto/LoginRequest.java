package com.demo.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * ✅ JAVA 21: Record para request de login com validação
 */
public record LoginRequest(
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    String cpf,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    String senha
) {

    /**
     * ✅ JAVA 21: Validação adicional no construtor compacto
     */
    public LoginRequest {
        if (cpf != null && !isValidCpf(cpf)) {
            throw new IllegalArgumentException("CPF inválido");
        }
    }

    /**
     * ✅ JAVA 21: Validação de CPF
     */
    private static boolean isValidCpf(String cpf) {
        if (cpf == null) {
            return false;
        }
        return cpf.matches("\\d{11}") && !cpf.equals("00000000000");
    }

    /**
     * ✅ JAVA 21: Método para obter CPF mascarado para logs
     */
    public String getCpfMascarado() {
        if (cpf == null) {
            return "***.***.***-**";
        }
        if (cpf.length() == 11) {
            return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
        }
        return "***.***.***-**";
    }
}