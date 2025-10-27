#!/bin/bash

# Script para baixar o heapdump e instruir a análise com VisualVM

echo "================================================================="
echo "||           DOWNLOAD E ANÁLISE DE HEAPDUMP COM VISUALVM         ||"
echo "================================================================="
echo ""
echo "Este script irá:"
echo "1. Verificar se o VisualVM está instalado e, se não, instalá-lo via Homebrew."
echo "2. Baixar o heapdump da memória da aplicação ('heapdump.hprof')."
echo "3. Instruir como abrir o arquivo no VisualVM para análise."
echo ""
echo "Certifique-se de que a aplicação 'security-demo' está em execução."
echo ""

# Passo 1: Verificar e instalar o Homebrew e o VisualVM
if ! command -v brew &> /dev/null; then
    echo "Homebrew (brew) não encontrado. Por favor, instale-o para continuar."
    echo "Visite https://brew.sh para instruções de instalação."
    exit 1
fi

# Verifica se o VisualVM está instalado
if ! brew list --cask visualvm &> /dev/null; then
    echo "VisualVM não encontrado. Tentando instalar via Homebrew (pode pedir sua senha)..."
    brew install --cask visualvm
    if ! brew list --cask visualvm &> /dev/null; then
        echo "Falha ao instalar o VisualVM. Por favor, instale-o manualmente e tente novamente."
        exit 1
    fi
    echo "VisualVM instalado com sucesso."
else
    echo "VisualVM já está instalado."
fi
echo ""

# Passo 2: Baixar o heapdump
echo "-----------------------------------------------------------------"
echo "Acessando o endpoint: /actuator/heapdump"
echo "-----------------------------------------------------------------"

echo "Baixando o heapdump (pode levar alguns segundos)..."
curl -o heapdump.hprof http://localhost:8080/actuator/heapdump

if [ -f "heapdump.hprof" ]; then
    FILE_SIZE=$(du -h "heapdump.hprof" | cut -f1)
    echo "Heapdump baixado com sucesso!"
    echo "   - Arquivo: heapdump.hprof"
    echo "   - Tamanho: $FILE_SIZE"
    echo ""
    
    # Passo 3: Instruir a abertura no VisualVM
    echo "================================================================="
    echo "||                      AÇÃO RECOMENDADA                     ||"
    echo "================================================================="
    echo ""
    echo "Para encontrar a senha do banco de dados no heap dump:"
    echo ""
    echo "1. Abra o 'VisualVM' (procure no Spotlight ou na sua pasta de Aplicativos)."
    echo "2. No menu, vá em 'File' -> 'Load...'."
    echo "3. Selecione o arquivo: $(pwd)/heapdump.hprof"
    echo ""
    echo "4. Após carregar, clique na aba 'Classes'."
    echo "5. No filtro de busca, digite 'HikariConfig' e pressione Enter."
    echo "   - Classe alvo: com.zaxxer.hikari.HikariConfig"
    echo ""
    echo "6. Clique com o botão direito na classe encontrada e selecione 'Show in Instances View'."
    echo "7. Na aba 'Fields' à direita, você verá os campos 'username' e 'password' em texto claro."
    echo ""
    echo "Alternativa: Procure por 'com.zaxxer.hikari.HikariDataSource', inspecione suas instâncias"
    echo "e procure pelo campo 'hikariConfig' para encontrar as mesmas informações."
    echo "================================================================="

else
    echo "Falha ao baixar o heapdump."
    echo "Verifique se a aplicação está rodando e se o endpoint /actuator/heapdump está habilitado e acessível."
fi