#!/bin/bash

# Script para demonstrar PII em logs
# APENAS PARA FINS EDUCATIVOS

echo "========================================"
echo "   VULNERABILIDADE: PII EM LOGS"
echo "========================================"
echo

BASE_URL="http://localhost:8080"

echo "DEMONSTRAÇÃO: PII (Dados Pessoais) em Logs"
echo

# Função para orientar o usuário sobre onde ver os logs
show_logs_info() {
    echo "VERIFIQUE OS LOGS NO CONSOLE DA APLICAÇÃO:"
    echo "Os logs com dados sensíveis aparecerão no console onde a aplicação está rodando"
    echo "Procure por linhas contendo: CPF, Senha, Token, Login, Usuario"
    echo
}

# 1. Gerar logs através de registro de usuário
echo "1. Gerando logs através de registro de usuário"
echo "-----------------------------------------------"
echo "Endpoint: POST /api/auth/register"
echo

echo "Enviando requisição de registro..."
curl -s -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "99988877766",
    "nome": "Teste Usuário",
    "senha": "minha_senha_secreta",
    "email": "teste@vulneravel.com"
  }' > /dev/null

echo "Requisição enviada"
echo
show_logs_info

# 2. Gerar logs através de login
echo "2. Gerando logs através de tentativa de login"
echo "----------------------------------------------"
echo "Endpoint: POST /api/auth/login"
echo

echo "Enviando tentativa de login..."
curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "12345678901",
    "senha": "senha_teste"
  }' > /dev/null

echo "Tentativa de login enviada"
echo
show_logs_info

# 3. Gerar logs através de transferência
echo "3. Gerando logs através de transferência bancária"
echo "-------------------------------------------------"
echo "Endpoint: POST /api/transacoes/transferir"
echo

echo "Enviando requisição de transferência..."
curl -s -X POST "$BASE_URL/api/transacoes/transferir" \
  -H "Content-Type: application/json" \
  -d '{
    "contaOrigem": "12345-6",
    "contaDestino": "98765-4",
    "valor": "1000.00",
    "cpfUsuario": "12345678901"
  }' > /dev/null

echo "Transferência enviada"
echo
show_logs_info

echo "========================================"
echo "  PII EM LOGS - DEMONSTRAÇÃO COMPLETA"
echo "========================================"