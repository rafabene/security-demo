#!/bin/bash

# Script principal para executar todas as demonstrações
# ⚠️  APENAS PARA FINS EDUCATIVOS ⚠️

echo "========================================"
echo "    SECURITY DEMO - TODAS AS VULNERABILIDADES"
echo "    ⚠️  APENAS PARA FINS EDUCATIVOS ⚠️"
echo "========================================"
echo

BASE_URL="http://localhost:8080"

# Verificar se a aplicação está rodando
echo "🔍 Verificando se a aplicação está rodando..."
if curl -s "$BASE_URL/api/usuarios/listar" > /dev/null 2>&1; then
    echo "✅ Aplicação está rodando em $BASE_URL"
    echo
else
    echo "❌ Aplicação não está acessível em $BASE_URL"
    echo "Por favor, inicie a aplicação com: mvn spring-boot:run"
    echo
    exit 1
fi

# Função para aguardar input do usuário
aguardar_continuar() {
    echo
    echo "Pressione ENTER para continuar para a próxima demonstração..."
    read -r
    echo
}

# Função para executar script com tratamento de erro
executar_script() {
    local script_path="$1"
    local script_name="$2"

    if [ -f "$script_path" ]; then
        echo "🚀 Executando: $script_name"
        echo "Script: $script_path"
        echo
        chmod +x "$script_path"
        "$script_path"

        if [ $? -eq 0 ]; then
            echo "✅ $script_name executado com sucesso!"
        else
            echo "⚠️  $script_name terminou com warnings"
        fi
    else
        echo "❌ Script não encontrado: $script_path"
    fi
}

# Menu de opções
echo "Escolha uma opção:"
echo "1. Executar todas as demonstrações sequencialmente"
echo "2. Executar demonstração específica"
echo "3. Executar análise completa (resumo)"
echo "4. Sair"
echo
read -p "Digite sua opção (1-4): " opcao

case $opcao in
    1)
        echo
        echo "🎯 EXECUTANDO TODAS AS DEMONSTRAÇÕES"
        echo "===================================="
        echo

        # 1. SQL Injection
        executar_script "scripts/1_sql_injection.sh" "SQL Injection"
        aguardar_continuar

        # 2. PII em Logs
        executar_script "scripts/2_pii_logs.sh" "PII em Logs"
        aguardar_continuar

        # 3. Endpoints sem Proteção
        executar_script "scripts/3_endpoints_sem_protecao.sh" "Endpoints sem Proteção"
        aguardar_continuar

        # 4. Dados Expostos
        executar_script "scripts/4_dados_expostos.sh" "Dados Expostos"
        aguardar_continuar

        # 5. Dependência Vulnerável
        executar_script "scripts/5_dependencia_vulneravel.sh" "Dependência Vulnerável"

        echo
        echo "🎉 TODAS AS DEMONSTRAÇÕES CONCLUÍDAS!"
        ;;

    2)
        echo
        echo "Escolha a demonstração específica:"
        echo "1. SQL Injection"
        echo "2. PII em Logs"
        echo "3. Endpoints sem Proteção"
        echo "4. Dados Expostos"
        echo "5. Dependência Vulnerável"
        echo
        read -p "Digite sua opção (1-5): " demo_opcao

        case $demo_opcao in
            1) executar_script "scripts/1_sql_injection.sh" "SQL Injection" ;;
            2) executar_script "scripts/2_pii_logs.sh" "PII em Logs" ;;
            3) executar_script "scripts/3_endpoints_sem_protecao.sh" "Endpoints sem Proteção" ;;
            4) executar_script "scripts/4_dados_expostos.sh" "Dados Expostos" ;;
            5) executar_script "scripts/5_dependencia_vulneravel.sh" "Dependência Vulnerável" ;;
            *) echo "❌ Opção inválida" ;;
        esac
        ;;

    3)
        echo
        echo "🔍 ANÁLISE COMPLETA - RESUMO EXECUTIVO"
        echo "======================================"
        echo

        # Análise rápida de cada vulnerabilidade
        echo "1. 💉 SQL INJECTION:"
        echo "   Status: $(curl -s "$BASE_URL/api/contas/buscar-por-saldo/0%20OR%201=1%20--" | jq length 2>/dev/null && echo "VULNERÁVEL" || echo "ERRO")"
        echo

        echo "2. 👥 DADOS EXPOSTOS:"
        USUARIOS_COUNT=$(curl -s "$BASE_URL/api/usuarios/listar" | jq length 2>/dev/null || echo "0")
        echo "   Usuários expostos: $USUARIOS_COUNT"
        echo

        echo "3. 🔓 ENDPOINTS SEM PROTEÇÃO:"
        echo "   Status: permitAll() em endpoints sensíveis"
        echo

        echo "4. 📝 PII EM LOGS:"
        echo "   Status: Dados sensíveis sendo logados (verificar logs)"
        echo

        echo "5. 📦 DEPENDÊNCIA VULNERÁVEL:"
        LOG4J_VERSION=$(grep -A 2 "log4j-core" pom.xml 2>/dev/null | grep "<version>" | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | tr -d ' ')
        if [ ! -z "$LOG4J_VERSION" ]; then
            echo "   Log4j versão: $LOG4J_VERSION (VULNERÁVEL se < 2.15.0)"
        else
            echo "   Log4j: Não detectado no pom.xml"
        fi
        echo

        echo "🚨 RESUMO DE IMPACTO:"
        echo "• Exposição completa de dados bancários"
        echo "• Vazamento de informações pessoais (PII)"
        echo "• Bypasse de autenticação possível"
        echo "• Execução remota de código (RCE) via Log4Shell"
        echo "• Violação de regulamentações (LGPD)"
        echo

        echo "🛡️  AÇÕES RECOMENDADAS:"
        echo "• Corrigir todas as queries SQL usando prepared statements"
        echo "• Implementar autenticação adequada em todos os endpoints"
        echo "• Remover informações sensíveis dos logs"
        echo "• Atualizar Log4j para versão segura (>= 2.17.0)"
        echo "• Implementar validação adequada de JWT"
        echo "• Configurar headers de segurança"
        echo "• Realizar auditoria completa de segurança"
        ;;

    4)
        echo "Saindo..."
        exit 0
        ;;

    *)
        echo "❌ Opção inválida"
        exit 1
        ;;
esac

echo
echo "========================================"
echo "         INFORMAÇÕES IMPORTANTES"
echo "========================================"
echo
echo "⚠️  IMPORTANTE:"
echo "• Esta aplicação é INTENCIONALMENTE vulnerável"
echo "• Use APENAS para fins educativos"
echo "• NÃO exponha na internet"
echo "• NÃO use código em produção"
echo
echo "📚 RECURSOS ADICIONAIS:"
echo "• README.md - Documentação completa"
echo "• scripts/ - Scripts individuais por vulnerabilidade"
echo "• src/ - Código fonte com vulnerabilidades comentadas"
echo
echo "🎓 PRÓXIMOS PASSOS:"
echo "• Analise o código fonte para entender as vulnerabilidades"
echo "• Compare com implementações seguras"
echo "• Pratique correção das vulnerabilidades"
echo "• Use ferramentas de escaneamento de segurança"
echo
echo "========================================"
echo "    DEMONSTRAÇÃO CONCLUÍDA COM SUCESSO"
echo "========================================"