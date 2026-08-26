# Documentação do Projeto BancoFinanças

## 📋 Índice
1. [Visão Geral](#visão-geral)
2. [Tecnologias Utilizadas](#tecnologias-utilizadas)
3. [Arquitetura do Projeto](#arquitetura-do-projeto)
4. [Estrutura de Diretórios](#estrutura-de-diretórios)
5. [Módulos e Funcionalidades](#módulos-e-funcionalidades)
6. [Configurações](#configurações)
7. [Como Executar](#como-executar)
8. [Fluxo de Operações](#fluxo-de-operações)
9. [Autenticação e Segurança](#autenticação-e-segurança)
10. [Testes](#testes)

---

## 🎯 Visão Geral

**BancoFinanças** é uma aplicação full-stack desenvolvida para gerenciar operações bancárias com suporte a múltiplos papéis (Agent, Cliente, Loja). O sistema permite:

- Autenticação segura com JWT
- Gerenciamento de contas bancárias
- Processamento de operações financeiras
- Controle de agências e lojas
- Sistema de pedidos e reversões
- Controle de saldo e extratos

**Stack:**
- Backend: Spring Boot 4.1.1
- Frontend: React 19.2.8 com Vite
- Banco de Dados: MySQL
- Autenticação: JWT (JSON Web Tokens)

---

## 🛠️ Tecnologias Utilizadas

### Backend (Java/Spring Boot)

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| Spring Boot | 4.1.1 | Framework principal |
| Spring Data JPA | Latest | ORM e persistência de dados |
| Spring Security | Latest | Autenticação e autorização |
| Spring Validation | Latest | Validação de dados |
| JWT (JJWT) | 0.12.6 | Geração e validação de tokens |
| MySQL Connector | Latest | Driver para MySQL |
| SpringDoc OpenAPI | 3.0.0 | Documentação Swagger/OpenAPI |
| JaCoCo | 0.8.13 | Cobertura de testes (80% mínimo) |
| Java | 21 | Linguagem de programação |

### Frontend (React)

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| React | 19.2.8 | Framework UI |
| React DOM | 19.2.8 | Renderização DOM |
| React Router | 7.18.2 | Roteamento entre páginas |
| Vite | 8.2.1 | Build tool |
| Lucide React | 1.34.0 | Ícones |

### Database

| Sistema | Versão |
|--------|--------|
| MySQL | Latest |

---

## 🏗️ Arquitetura do Projeto

```
┌─────────────────────────────────────────────────────────────┐
│                    Cliente (Frontend)                        │
│              React 19.2.8 + Vite + React Router             │
└────────────────────────────┬────────────────────────────────┘
                             │
                    ┌────────▼────────┐
                    │  HTTP/REST API  │
                    │  (Port 8080)    │
                    └────────┬────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│              Backend (Spring Boot 4.1.1)                    │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Controllers (REST Endpoints)                        │   │
│  │  • AuthController    • ClienteController             │   │
│  │  • AgenciaController • OperacaoController            │   │
│  │  • AgenteController  • LojaController                │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Services (Lógica de Negócio)                        │   │
│  │  • AuthService      • ClienteService                 │   │
│  │  • AgenciaService   • OperacaoService                │   │
│  │  • AgenteService    • LojaService                    │   │
│  │  • ContaCorrenteService                              │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Repositories (Acesso a Dados - Spring Data JPA)    │   │
│  │  • ClienteRepository    • ContaCorrenteRepository    │   │
│  │  • AgenciaRepository    • OperacaoRepository         │   │
│  │  • UsuarioRepository    • PedidoLojaRepository       │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Security (JWT + Spring Security)                    │   │
│  │  • JwtService           • JwtAuthenticationFilter    │   │
│  │  • CustomUserDetailsService • SecurityConfig         │   │
│  └──────────────────────────────────────────────────────┘   │
└────────────────────────────┬────────────────────────────────┘
                             │
                    ┌────────▼────────┐
                    │     MySQL        │
                    │  localhost:3306  │
                    │  (Database)      │
                    └──────────────────┘
```

---

## 📁 Estrutura de Diretórios

```
bancofinancas/
├── src/
│   ├── main/
│   │   ├── java/acc/br/bancofinancas/
│   │   │   ├── BancofinancasApplication.java        # Classe principal
│   │   │   ├── config/                               # Configurações
│   │   │   │   ├── ApiExceptionHandler.java         # Tratamento de exceções
│   │   │   │   ├── CargaInicial.java                # Dados iniciais
│   │   │   │   ├── CargaInicialLoja.java            # Dados iniciais lojas
│   │   │   │   └── OpenApiConfig.java               # Swagger/OpenAPI
│   │   │   ├── Controller/                           # REST Controllers
│   │   │   │   ├── AuthController.java              # Autenticação
│   │   │   │   ├── ClienteController.java           # Gerenciamento de clientes
│   │   │   │   ├── AgenciaController.java           # Gerenciamento de agências
│   │   │   │   ├── AgenteController.java            # Gerenciamento de agentes
│   │   │   │   ├── ContaCorrenteController.java     # Gerenciamento de contas
│   │   │   │   ├── LojaController.java              # Gerenciamento de lojas
│   │   │   │   └── OperacaoController.java          # Operações financeiras
│   │   │   ├── service/                              # Lógica de negócio
│   │   │   │   ├── AuthService.java                 # Serviço de autenticação
│   │   │   │   ├── ClienteService.java              # Serviço de clientes
│   │   │   │   ├── AgenciaService.java              # Serviço de agências
│   │   │   │   ├── AgenteService.java               # Serviço de agentes
│   │   │   │   ├── ContaCorrenteService.java        # Serviço de contas
│   │   │   │   ├── LojaService.java                 # Serviço de lojas
│   │   │   │   └── OperacaoService.java             # Serviço de operações
│   │   │   ├── model/                                # Entidades JPA
│   │   │   │   ├── Usuario.java                      # Usuário do sistema
│   │   │   │   ├── Cliente.java                      # Cliente
│   │   │   │   ├── Agencia.java                      # Agência
│   │   │   │   ├── Agente.java                       # Agente da agência
│   │   │   │   ├── ContaCorrente.java                # Conta corrente
│   │   │   │   ├── Extrato.java                      # Extrato da conta
│   │   │   │   ├── PedidoLoja.java                   # Pedido da loja
│   │   │   │   ├── LojaItem.java                     # Item da loja
│   │   │   │   └── SolicitacaoReversao.java          # Solicitação de reversão
│   │   │   ├── repository/                           # Spring Data Repositories
│   │   │   │   ├── ClienteRepository.java
│   │   │   │   ├── AgenciaRepository.java
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   └── ... (demais repositories)
│   │   │   ├── dto/                                  # Data Transfer Objects
│   │   │   │   ├── AuthRequest.java                  # Requisição de login
│   │   │   │   ├── AuthResponse.java                 # Resposta com token JWT
│   │   │   │   ├── CreateClienteRequest.java         # Criar cliente
│   │   │   │   ├── ClienteResponse.java              # Resposta cliente
│   │   │   │   └── ... (demais DTOs)
│   │   │   └── security/                             # Segurança
│   │   │       ├── JwtService.java                   # Geração/validação JWT
│   │   │       ├── JwtAuthenticationFilter.java      # Filtro de autenticação
│   │   │       ├── CustomUserDetailsService.java     # Carregamento de usuários
│   │   │       ├── SecurityConfig.java               # Configuração de segurança
│   │   │       └── AuthenticatedUser.java            # Usuário autenticado
│   │   └── resources/
│   │       └── application.properties                 # Configurações da aplicação
│   └── test/                                          # Testes unitários
│       └── java/acc/br/bancofinancas/
│           └── [Testes das classes principais]
├── frontend/
│   ├── src/
│   │   ├── pages/                                     # Páginas da aplicação
│   │   │   ├── Login.jsx                              # Login
│   │   │   ├── ClienteHome/                           # Home do cliente
│   │   │   ├── ClienteLogin/                          # Login do cliente
│   │   │   ├── HomeAgente/                            # Home do agente
│   │   │   ├── clientes/                              # Gerenciamento de clientes
│   │   │   ├── Contas/                                # Gerenciamento de contas
│   │   │   ├── CriarConta/                            # Criar conta
│   │   │   ├── AdicionarSaldo/                        # Adicionar saldo
│   │   │   ├── Operacoes/                             # Operações
│   │   │   ├── Reversoes/                             # Reversões
│   │   │   └── Loja/                                  # Loja
│   │   ├── components/                                # Componentes reutilizáveis
│   │   │   ├── Button/                                # Componente botão
│   │   │   ├── Header/                                # Cabeçalho
│   │   │   ├── Login/                                 # Componente login
│   │   │   ├── SideBar/                               # Barra lateral
│   │   │   └── ModalMotivoConta/                      # Modal
│   │   ├── assets/                                    # Recursos estáticos
│   │   ├── App.jsx                                    # Componente raiz
│   │   ├── main.jsx                                   # Ponto de entrada
│   │   └── styles.css                                 # Estilos globais
│   ├── index.html                                     # HTML principal
│   ├── package.json                                   # Dependências
│   └── vite.config.js                                 # Configuração Vite
├── sql/
│   └── criar-usuarios-loja-master.sql                # Script de usuários iniciais
├── pom.xml                                            # Dependências Maven
├── mvnw e mvnw.cmd                                   # Maven Wrapper
└── target/                                            # Artefatos compilados

```

---

## 🔧 Módulos e Funcionalidades

### 1. **Autenticação (AuthController + AuthService)**

**Endpoints:**
- `POST /api/auth/login` - Login de usuários
- `POST /api/auth/register` - Registro de novos usuários

**Funcionalidades:**
- Autenticação com JWT
- Suporte a múltiplos papéis: CLIENTE, AGENTE, ADMIN, LOJA
- Validação de credenciais
- Geração de tokens com expiração

**Fluxo:**
```
Usuário faz login
        ↓
AuthController valida credenciais
        ↓
CustomUserDetailsService carrega usuário
        ↓
JwtService gera token JWT
        ↓
Token retornado ao cliente
```

---

### 2. **Gerenciamento de Clientes (ClienteController + ClienteService)**

**Endpoints:**
- `GET /api/clientes` - Listar todos os clientes
- `GET /api/clientes/{id}` - Obter cliente específico
- `POST /api/clientes` - Criar novo cliente
- `PUT /api/clientes/{id}` - Atualizar cliente
- `DELETE /api/clientes/{id}` - Deletar cliente

**Entidades Relacionadas:**
- `Cliente` - Dados do cliente (CPF, nome, email, etc)
- `Usuario` - Credenciais de login

**Funcionalidades:**
- Criação de clientes
- Validação de CPF
- Gerenciamento de dados pessoais
- Bloqueio/desbloqueio de clientes

---

### 3. **Gerenciamento de Agências (AgenciaController + AgenciaService)**

**Endpoints:**
- `GET /api/agencias` - Listar agências
- `GET /api/agencias/{id}` - Obter agência específica
- `POST /api/agencias` - Criar agência
- `PUT /api/agencias/{id}` - Atualizar agência
- `DELETE /api/agencias/{id}` - Deletar agência

**Funcionalidades:**
- Cadastro de agências bancárias
- Gerenciamento de dados (número, endereço, etc)
- Associação com agentes

---

### 4. **Gerenciamento de Contas Correntes (ContaCorrenteController + ContaCorrenteService)**

**Endpoints:**
- `GET /api/contas` - Listar contas
- `GET /api/contas/{id}` - Obter conta específica
- `POST /api/contas` - Criar conta
- `POST /api/contas/{id}/bloquear` - Bloquear conta
- `POST /api/contas/{id}/desbloquear` - Desbloquear conta

**Funcionalidades:**
- Criar conta corrente para cliente
- Gerenciar saldo
- Gerar número de conta única
- Controlar status (ativa, bloqueada)
- Gerar extratos

**Entidades:**
- `ContaCorrente` - Dados da conta
- `Extrato` - Histórico de operações

---

### 5. **Operações Financeiras (OperacaoController + OperacaoService)**

**Endpoints:**
- `POST /api/operacoes/deposito` - Realizar depósito
- `POST /api/operacoes/saque` - Realizar saque
- `POST /api/operacoes/transferencia` - Realizar transferência
- `GET /api/operacoes/extrato/{contaId}` - Obter extrato
- `POST /api/operacoes/credito-manual` - Adicionar crédito manual

**Tipos de Operação:**
- **DEPOSITO** - Entrada de valores
- **SAQUE** - Saída de valores
- **TRANSFERENCIA** - Movimentação entre contas
- **CREDITO** - Crédito manual

**Funcionalidades:**
- Validação de saldo
- Registro de operações
- Geração de extratos
- Histórico de transações

---

### 6. **Gerenciamento de Lojas (LojaController + LojaService)**

**Endpoints:**
- `GET /api/loja/pedidos` - Listar pedidos
- `POST /api/loja/pedidos` - Criar pedido
- `POST /api/loja/pedidos/{id}/pagar` - Pagar pedido
- `PUT /api/loja/pedidos/{id}/status` - Atualizar status
- `GET /api/loja/itens` - Listar itens disponíveis

**Entidades:**
- `PedidoLoja` - Pedido da loja
- `LojaItem` - Item disponível na loja

**Funcionalidades:**
- Catálogo de produtos/serviços
- Sistema de pedidos
- Controle de estoque
- Status de pedido (PENDENTE, PAGO, CANCELADO, etc)

---

### 7. **Reversões de Operações (OperacaoService + SolicitacaoReversao)**

**Funcionalidades:**
- Solicitar reversão de operação
- Análise de reversão
- Aprovação/rejeição de reversão
- Ajuste de saldo

**Entidades:**
- `SolicitacaoReversao` - Solicitação de reversão

---

## ⚙️ Configurações

### arquivo `application.properties`

```properties
# Nome da aplicação
spring.application.name=bancofinancas

# Banco de dados MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/bancofinancas?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update              # Auto-cria/atualiza tabelas
spring.jpa.show-sql=true                          # Log SQL
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Segurança JWT
security.jwt.secret=01234567890123456789012345678901   # Chave secreta
security.jwt.expiration-seconds=3600                    # 1 hora de expiração

# Carga inicial
app.carga-inicial.enabled=true
```

### Usuários Iniciais (SQL)

Dois usuários são criados automaticamente no arquivo `criar-usuarios-loja-master.sql`:

| Username | Password | Role | Propósito |
|----------|----------|------|-----------|
| LOJA | 1234 | LOJA | Gerencia a loja |
| MASTER | 1234 | AGENCIA | Administrador geral |

**Nota:** As senhas estão criptografadas com BCrypt.

---

## 🚀 Como Executar

### Pré-requisitos

1. **Java 21** instalado
2. **MySQL** rodando em `localhost:3306`
3. **Node.js** 16+ instalado
4. **Maven** instalado (ou usar `./mvnw`)

### Passo 1: Configurar Banco de Dados

```sql
CREATE DATABASE IF NOT EXISTS bancofinancas;
USE bancofinancas;
```

### Passo 2: Executar Backend

```bash
# Navigate ao diretório root
cd c:\Users\Mateus Alves\Documents\bancofinancas

# Executar com Maven
mvn spring-boot:run

# Ou se usar Maven Wrapper
./mvnw spring-boot:run
```

O backend estará disponível em: `http://localhost:8080`

**Swagger/OpenAPI:** `http://localhost:8080/swagger-ui.html`

### Passo 3: Executar Frontend

```bash
# Navigate ao diretório frontend
cd frontend

# Instalar dependências
npm install

# Iniciar servidor de desenvolvimento
npm run dev
```

O frontend estará disponível em: `http://localhost:5173` (padrão Vite)

### Passo 4: Acessar Aplicação

1. Abra `http://localhost:5173` no navegador
2. Faça login com:
   - **Username:** LOJA ou MASTER
   - **Password:** 1234

---

## 📊 Fluxo de Operações

### Fluxo de Login

```
┌─────────────────────────────────────────┐
│  1. Usuário acessa login page           │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│  2. Submete username/password           │
│     (POST /api/auth/login)              │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│  3. AuthService valida credenciais      │
│     contra banco de dados               │
└────────────────────┬────────────────────┘
                     │
          ┌──────────┴──────────┐
          │                     │
    ✓ Válido             ✗ Inválido
          │                     │
┌─────────▼──────────┐ ┌────────▼───────────┐
│ 4. JwtService gera │ │ Retorna erro 401   │
│    token JWT       │ │ Unauthorized       │
└─────────┬──────────┘ └────────────────────┘
          │
┌─────────▼──────────────────────────────┐
│  5. Token retornado ao cliente         │
│     {token: "eyJhbGc..."}             │
└─────────┬──────────────────────────────┘
          │
┌─────────▼──────────────────────────────┐
│  6. Cliente armazena token (localStorage)│
│     Redirectiona para dashboard        │
└────────────────────────────────────────┘
```

### Fluxo de Operação Financeira (Transferência)

```
┌─────────────────────────────────────┐
│ 1. Cliente solicita transferência   │
│    (POST /api/operacoes/transferencia)
└────────────────────┬────────────────┘
                     │
┌────────────────────▼────────────────┐
│ 2. OperacaoService valida:         │
│    - Saldo suficiente?             │
│    - Conta destino existe?         │
│    - Conta desbloqueada?           │
└────────────────────┬────────────────┘
                     │
          ┌──────────┴──────────┐
          │                     │
      ✓ OK                  ✗ Erro
          │                     │
┌─────────▼──────────┐  ┌───────▼────────┐
│ 3. Débito na      │  │ Retorna erro   │
│    conta origem   │  │ (422 ou 400)   │
└─────────┬──────────┘  └────────────────┘
          │
┌─────────▼──────────┐
│ 4. Crédito na     │
│    conta destino  │
└─────────┬──────────┘
          │
┌─────────▼──────────────────────┐
│ 5. Registro de Extrato para    │
│    ambas as contas            │
└─────────┬──────────────────────┘
          │
┌─────────▼──────────────────────┐
│ 6. Retorna sucesso ao cliente  │
│    com detalhes da transação   │
└────────────────────────────────┘
```

### Fluxo de Requisição HTTP com JWT

```
┌──────────────────────────────────────┐
│ Cliente faz requisição autenticada   │
│ (com token JWT no header)            │
└────────────────┬─────────────────────┘
                 │
         Header: Authorization: Bearer eyJhbGc...
                 │
┌────────────────▼─────────────────────┐
│ JwtAuthenticationFilter intercepta   │
│ a requisição                         │
└────────────────┬─────────────────────┘
                 │
┌────────────────▼─────────────────────┐
│ JwtService valida token:            │
│ - Assinatura válida?                │
│ - Não expirado?                     │
└────────────────┬─────────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
    ✓ Válido          ✗ Inválido
        │                 │
┌───────▼──────────┐  ┌───▼─────────────┐
│ Carrega usuário  │  │ Retorna erro 401│
│ em SecurityContext│  │ Unauthorized    │
└───────┬──────────┘  └─────────────────┘
        │
┌───────▼──────────────────────────────┐
│ Requisição prossegue para Controller │
│ com autenticação validada           │
└────────────────────────────────────────┘
```

---

## 🔐 Autenticação e Segurança

### JWT (JSON Web Tokens)

**O que é JWT?**
Um token JWT é composto por 3 partes separadas por pontos:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9 . eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ . SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

Partes:
1. **Header:** Algoritmo e tipo de token
2. **Payload:** Dados do usuário (sub, nome, exp, etc)
3. **Signature:** Assinatura para validar autenticidade

**Fluxo:**
1. Usuário faz login → Recebe JWT
2. Cliente inclui JWT em toda requisição: `Authorization: Bearer <token>`
3. JwtAuthenticationFilter valida token
4. SecurityContext carrega usuário autenticado
5. Requisição processada com autenticação

### Criptografia de Senha

- **Algoritmo:** BCrypt
- **Custo:** 10 rounds
- Senhas não são armazenadas em plaintext

**Exemplo de senha criptografada:**
```
$2b$10$7n0pyx3AACMAAKWkdWdRCehNkLj4VzTKU0zx9WPhJEZ5VcP2a1He.
```

### Papéis (Roles) e Autorização

| Role | Descrição | Permissões |
|------|-----------|-----------|
| CLIENTE | Cliente do banco | Ver própria conta, operações |
| AGENTE | Funcionário da agência | Gerenciar clientes, contas, operações |
| AGENCIA | Administrador de agência | Acesso total à agência |
| LOJA | Gerenciador da loja | Gerenciar pedidos, itens |
| ADMIN | Administrador geral | Acesso total ao sistema |

---

## 🧪 Testes

### Cobertura de Testes

O projeto usa **JaCoCo** para medir cobertura de código:
- **Mínimo obrigatório:** 80% de cobertura de linha
- **Relatório:** `target/site/jacoco/index.html`

### Testes Disponíveis

Todos os testes estão em `src/test/java/acc/br/bancofinancas/`

**Classes de teste:**
- `BancofinancasApplicationTests.java` - Teste de contexto
- `AuthServiceTest.java` - Autenticação
- `ClienteServiceTest.java` - Gerenciamento de clientes
- `ContaCorrenteServiceTest.java` - Gerenciamento de contas
- `OperacaoServiceTest.java` - Operações financeiras
- `JwtServiceTest.java` - Tokens JWT
- `*ControllerTest.java` - Testes de endpoints REST

### Executar Testes

```bash
# Executar todos os testes
mvn test

# Executar teste específico
mvn test -Dtest=ClienteServiceTest

# Executar com cobertura JaCoCo
mvn clean test jacoco:report

# Visualizar relatório
# Abrir: target/site/jacoco/index.html
```

### Exemplo de Teste

```java
@SpringBootTest
@AutoConfigureMockMvc
public class ClienteServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteRepository clienteRepository;

    @Test
    public void testCriarCliente() {
        // Arrange
        CreateClienteRequest request = new CreateClienteRequest();
        request.setCpf("12345678900");
        request.setNome("João Silva");
        
        // Act
        // Assert
    }
}
```

---

## 📝 Endpoints Principais

### Autenticação
```
POST   /api/auth/login         - Fazer login
POST   /api/auth/register      - Registrar novo usuário
```

### Clientes
```
GET    /api/clientes           - Listar clientes
GET    /api/clientes/{id}      - Obter cliente
POST   /api/clientes           - Criar cliente
PUT    /api/clientes/{id}      - Atualizar cliente
DELETE /api/clientes/{id}      - Deletar cliente
```

### Contas Correntes
```
GET    /api/contas             - Listar contas
GET    /api/contas/{id}        - Obter conta
POST   /api/contas             - Criar conta
POST   /api/contas/{id}/bloquear    - Bloquear conta
POST   /api/contas/{id}/desbloquear - Desbloquear conta
```

### Operações
```
POST   /api/operacoes/deposito        - Realizar depósito
POST   /api/operacoes/saque           - Realizar saque
POST   /api/operacoes/transferencia   - Realizar transferência
GET    /api/operacoes/extrato/{id}    - Obter extrato
POST   /api/operacoes/credito-manual  - Crédito manual
```

### Agências
```
GET    /api/agencias           - Listar agências
GET    /api/agencias/{id}      - Obter agência
POST   /api/agencias           - Criar agência
PUT    /api/agencias/{id}      - Atualizar agência
DELETE /api/agencias/{id}      - Deletar agência
```

### Lojas
```
GET    /api/loja/pedidos       - Listar pedidos
POST   /api/loja/pedidos       - Criar pedido
POST   /api/loja/pedidos/{id}/pagar   - Pagar pedido
PUT    /api/loja/pedidos/{id}/status  - Atualizar status
GET    /api/loja/itens         - Listar itens
```

---

## 🔍 Estrutura de Dados (Banco de Dados)

### Tabelas Principais

#### usuarios
```
- id (PK)
- username (UNIQUE)
- password (BCrypt)
- role (ENUM)
- cliente_id (FK)
- agencia_id (FK)
```

#### clientes
```
- id (PK)
- cpf (UNIQUE)
- nome
- email
- data_criacao
- bloqueado
```

#### agencias
```
- id (PK)
- numero (UNIQUE)
- nome
- endereco
- telefone
```

#### contas_correntes
```
- id (PK)
- numero_conta (UNIQUE)
- cliente_id (FK)
- saldo
- bloqueada
- data_criacao
```

#### operacoes
```
- id (PK)
- conta_id (FK)
- tipo (DEPOSITO, SAQUE, TRANSFERENCIA, CREDITO)
- valor
- descricao
- data_operacao
```

#### pedidos_loja
```
- id (PK)
- numero_pedido (UNIQUE)
- status
- total
- data_pedido
```

#### loja_itens
```
- id (PK)
- descricao
- preco
- quantidade
```

#### solicitacoes_reversao
```
- id (PK)
- operacao_id (FK)
- status
- motivo
- data_solicitacao
```

---

## 💡 Fluxo Completo de Exemplo

### Cenário: Cliente realiza transferência

**1. Login do Cliente**
```
Cliente acessa login
↓
Submete: username="cliente123", password="senha123"
↓
POST /api/auth/login
↓
Retorna: { token: "eyJhbGc..." }
↓
Token armazenado em localStorage
```

**2. Cliente acessa Dashboard**
```
Frontend carrega com token no header
↓
JwtAuthenticationFilter valida token
↓
Usuário autenticado pode acessar dados
↓
Interface mostra saldo e operações
```

**3. Cliente faz Transferência**
```
Clica em "Transferir"
↓
Preenche: valor=500, conta_destino=12345
↓
POST /api/operacoes/transferencia
  Headers: Authorization: Bearer <token>
  Body: { valor: 500, contaDestino: 12345 }
↓
OperacaoService valida:
  - Saldo >= 500? ✓
  - Conta destino existe? ✓
  - Desbloqueada? ✓
↓
Débito na conta origem: -500
↓
Crédito na conta destino: +500
↓
Registra extratos
↓
Retorna sucesso: { id: 123, status: "CONCLUÍDO" }
↓
Frontend exibe confirmação
```

---

## 🐛 Troubleshooting

### Backend não conecta ao MySQL
```
Erro: com.mysql.cj.jdbc.exceptions.CommunicationsException

Solução:
1. Verificar se MySQL está rodando: mysql -u root -p
2. Verificar url em application.properties
3. Verificar credenciais (username/password)
4. Criar banco de dados: CREATE DATABASE bancofinancas;
```

### JWT Token Expirado
```
Erro: 401 Unauthorized - Token has expired

Solução:
1. Fazer login novamente
2. Aumentar expiração em application.properties:
   security.jwt.expiration-seconds=7200 (2 horas)
```

### CORS Error no Frontend
```
Erro: No 'Access-Control-Allow-Origin' header

Solução:
1. Adicionar @CrossOrigin ao controller:
   @CrossOrigin(origins = "http://localhost:5173")
   public class ApiController { ... }
```

### Porta 8080 já em uso
```
Erro: Port 8080 already in use

Solução:
1. Usar outra porta em application.properties:
   server.port=8081
2. Ou matar processo: netstat -ano | findstr :8080
```

---

## 📚 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [React Documentation](https://react.dev)
- [Vite Documentation](https://vitejs.dev)
- [JWT.io](https://jwt.io)
- [MySQL Documentation](https://dev.mysql.com/doc/)

---

## 📞 Suporte

Para dúvidas ou problemas, consulte:
1. Swagger/OpenAPI: `http://localhost:8080/swagger-ui.html`
2. Logs do backend: Console do Maven
3. Console do navegador: F12 → Console
4. Relatório de testes: `target/site/jacoco/index.html`

---

**Última atualização:** 2026-08-26
**Versão:** 0.0.1-SNAPSHOT
**Status:** Em desenvolvimento

