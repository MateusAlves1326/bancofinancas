# BancoFinanças 🏦

![Status: Active Development](https://img.shields.io/badge/Status-Active%20Development-green)
![Version: 0.0.1](https://img.shields.io/badge/Version-0.0.1--SNAPSHOT-blue)
![Java: 21](https://img.shields.io/badge/Java-21-orange)
![React: 19.2](https://img.shields.io/badge/React-19.2.8-61DAFB?logo=react)
![License: ISC](https://img.shields.io/badge/License-ISC-lightgrey)

Sistema completo de gerenciamento bancário com autenticação segura, operações financeiras e múltiplos papéis de usuário.

---

## 📋 Sumário

- [Características](#-características)
- [Tech Stack](#-tech-stack)
- [Quick Start](#-quick-start)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Documentação](#-documentação)
- [Endpoints](#-endpoints-principais)
- [Contribuindo](#-contribuindo)

---

## ✨ Características

### 🔐 Autenticação & Segurança
- ✅ Autenticação JWT (JSON Web Tokens)
- ✅ Criptografia de senha com BCrypt
- ✅ Controle de papéis (CLIENTE, AGENTE, ADMIN, LOJA)
- ✅ Validação de token em todas requisições protegidas

### 💰 Operações Financeiras
- ✅ Depósito, Saque, Transferência
- ✅ Crédito manual
- ✅ Extrato por conta
- ✅ Sistema de reversão de operações

### 👥 Gerenciamento
- ✅ Cadastro de clientes com validação de CPF
- ✅ Gerenciamento de contas bancárias
- ✅ Gerenciamento de agências
- ✅ Gerenciamento de agentes
- ✅ Bloqueio/desbloqueio de contas

### 🛍️ Sistema de Loja
- ✅ Catálogo de produtos/serviços
- ✅ Gerenciamento de pedidos
- ✅ Controle de status de pedido
- ✅ Processamento de pagamento

### 📊 Testes & Qualidade
- ✅ JUnit 5 para testes unitários
- ✅ Cobertura mínima 80% (JaCoCo)
- ✅ Mockito para mocks
- ✅ Testes de integração

---

## 🛠️ Tech Stack

### Backend
```
Spring Boot 4.1.1
├─ Spring Security (Autenticação)
├─ Spring Data JPA (Persistência)
├─ Spring Validation (Validação)
├─ JWT (Token Management)
├─ MySQL 5.7+ (Database)
└─ Maven (Build Tool)
```

### Frontend
```
React 19.2.8
├─ React Router 7.18.2 (Roteamento)
├─ Vite 8.2.1 (Build Tool)
└─ Lucide React 1.34.0 (Ícones)
```

### Database
```
MySQL 5.7+ / 8.0
├─ InnoDB Engine
├─ UTF-8 Encoding
└─ Suporte a Transações
```

### Testing
```
JUnit 5
├─ Mockito
├─ JaCoCo (Code Coverage)
└─ Spring Boot Test
```

---

## 🚀 Quick Start

### Pré-requisitos
- Java 21+
- MySQL 5.7+
- Node.js 16+
- Maven (ou Maven Wrapper)

### 1. Setup Inicial (3 min)

```bash
# Clone o repositório
git clone <repositorio-url>
cd bancofinancas

# Criar banco de dados
mysql -u root -p -e "CREATE DATABASE bancofinancas;"

# Compilar backend
mvn clean install

# Instalar dependências frontend
cd frontend
npm install
cd ..
```

### 2. Rodar a Aplicação (2 terminais)

**Terminal 1: Backend**
```bash
mvn spring-boot:run
# Backend pronto em http://localhost:8080
```

**Terminal 2: Frontend**
```bash
cd frontend
npm run dev
# Frontend pronto em http://localhost:5173
```

### 3. Login

Acesse: http://localhost:5173

```
Username: LOJA
Password: 1234
```

---

## 📁 Estrutura do Projeto

```
bancofinancas/
├── src/
│   ├── main/
│   │   ├── java/acc/br/bancofinancas/
│   │   │   ├── Controller/          # REST Endpoints
│   │   │   ├── service/             # Lógica de Negócio
│   │   │   ├── model/               # Entidades JPA
│   │   │   ├── repository/          # Acesso a Dados
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── security/            # JWT & Autenticação
│   │   │   └── config/              # Configurações
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/acc/br/bancofinancas/
├── frontend/
│   ├── src/
│   │   ├── pages/      # Páginas da aplicação
│   │   ├── components/ # Componentes reutilizáveis
│   │   ├── App.jsx
│   │   └── main.jsx
│   └── package.json
├── sql/
│   └── criar-usuarios-loja-master.sql
├── pom.xml             # Dependências Maven
├── DOCUMENTACAO.md     # Documentação completa
├── GUIA_DESENVOLVIMENTO.md  # Guia para desenvolvedores
├── ARQUITETURA.md      # Diagramas técnicos
├── QUICK_START.md      # Quick start guide
└── README.md           # Este arquivo
```

---

## 📚 Documentação

| Documento | Propósito |
|-----------|-----------|
| [QUICK_START.md](QUICK_START.md) | **Comece aqui!** Guia de 10 minutos |
| [DOCUMENTACAO.md](DOCUMENTACAO.md) | Visão geral completa do projeto |
| [GUIA_DESENVOLVIMENTO.md](GUIA_DESENVOLVIMENTO.md) | Como adicionar novas funcionalidades |
| [ARQUITETURA.md](ARQUITETURA.md) | Diagramas e fluxos técnicos |
| [Swagger UI](http://localhost:8080/swagger-ui.html) | Documentação interativa da API |

---

## 🔌 Endpoints Principais

### Autenticação
```
POST   /api/auth/login         - Fazer login
POST   /api/auth/register      - Registrar novo usuário
```

### Clientes
```
GET    /api/clientes           - Listar todos
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

### Operações Financeiras
```
POST   /api/operacoes/deposito        - Fazer depósito
POST   /api/operacoes/saque           - Fazer saque
POST   /api/operacoes/transferencia   - Fazer transferência
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

**Ver documentação completa:** http://localhost:8080/swagger-ui.html

---

## 🧪 Testes

### Executar Todos os Testes
```bash
mvn test
```

### Executar Teste Específico
```bash
mvn test -Dtest=ClienteServiceTest
```

### Gerar Relatório de Cobertura
```bash
mvn clean test jacoco:report
# Abrir: target/site/jacoco/index.html
```

**Cobertura Mínima:** 80% (configurado em pom.xml)

---

## 🔧 Configuração

### application.properties

```properties
# Banco de dados
spring.datasource.url=jdbc:mysql://localhost:3306/bancofinancas
spring.datasource.username=root
spring.datasource.password=root

# JWT
security.jwt.secret=01234567890123456789012345678901
security.jwt.expiration-seconds=3600  # 1 hora

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Alterar Porta
```properties
# application.properties
server.port=8081
```

---

## 🐛 Troubleshooting

### Erro: "Port 8080 already in use"
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

### Erro: "Cannot connect to MySQL"
```bash
# Verificar se MySQL está rodando
mysql -u root -p

# Criar banco de dados se não existir
CREATE DATABASE bancofinancas;
```

### Erro: "401 Unauthorized"
- Token expirado? → Faça login novamente
- Token não no header? → Verificar DevTools (F12)

---

## 📊 Diagrama de Arquitetura

```
┌─────────────────────────────────┐
│    Frontend (React + Vite)      │
│    http://localhost:5173        │
└────────────┬────────────────────┘
             │ HTTP/REST + JWT
             ▼
┌─────────────────────────────────┐
│  Backend (Spring Boot 4.1.1)    │
│  http://localhost:8080          │
├─────────────────────────────────┤
│ Controllers │ Services │ Models │
└────────────┬────────────────────┘
             │ JDBC
             ▼
┌─────────────────────────────────┐
│   MySQL 5.7+                    │
│   localhost:3306/bancofinancas  │
└─────────────────────────────────┘
```

---

## 👥 Papéis de Usuário

| Papel | Permissões | Acesso |
|-------|-----------|--------|
| CLIENTE | Ver própria conta, fazer operações | App cliente |
| AGENTE | Gerenciar clientes, contas, operações | Dashboard agente |
| AGENCIA | Acesso total à agência | Admin painel |
| LOJA | Gerenciar pedidos, itens | Painel loja |
| ADMIN | Acesso total ao sistema | SuperUser |

---

## 🔄 Fluxo de Transferência (Exemplo)

```
1. Cliente submete requisição de transferência
2. Controller valida autenticação JWT
3. Service valida regras de negócio
4. Repository executa transação no banco
5. Sistema registra na tabela de operações
6. Extratos são gerados para ambas contas
7. Resposta retorna ao cliente
```

---

## 📈 Performance

### Otimizações Implementadas
- Lazy Loading para relacionamentos
- Paginação de resultados
- Índices no banco de dados
- Cache quando apropriado
- Queries otimizadas

### Monitoramento
```bash
# Ver SQL executado (já configurado)
# Console do Maven mostra SQL real

# Profiler (JaCoCo)
mvn clean test jacoco:report
```

---

## 🤝 Contribuindo

### Workflow Git
1. Crie uma branch: `git checkout -b feature/sua-feature`
2. Faça as mudanças
3. Escreva testes (cobertura >= 80%)
4. Commit: `git commit -m "feat: descrição"`
5. Push: `git push origin feature/sua-feature`
6. Abra um Pull Request

### Padrão de Commit
```
feat:  Nova funcionalidade
fix:   Correção de bug
docs:  Documentação
style: Formatação/estilo
refactor: Refatoração
test:  Testes
```

---

## 📞 Suporte

- 📖 Documentação: [DOCUMENTACAO.md](DOCUMENTACAO.md)
- ⚡ Quick Start: [QUICK_START.md](QUICK_START.md)
- 🛠️ Desenvolvimento: [GUIA_DESENVOLVIMENTO.md](GUIA_DESENVOLVIMENTO.md)
- 📐 Arquitetura: [ARQUITETURA.md](ARQUITETURA.md)
- 🔌 API Interativa: http://localhost:8080/swagger-ui.html

---

## 📄 Licença

ISC License

---

## 📝 Status do Projeto

- ✅ Backend: Em desenvolvimento ativo
- ✅ Frontend: Em desenvolvimento ativo
- ✅ Banco de dados: Estável
- ✅ Testes: 80%+ cobertura
- ✅ Documentação: Completa

---

## 🗓️ Histórico de Versões

### v0.0.1-SNAPSHOT (Atual)
- Setup inicial do projeto
- Autenticação JWT
- CRUD de clientes, contas, operações
- Sistema de lojas
- Testes com 80%+ cobertura
- Documentação completa

---

**Última atualização:** 2026-08-26

**Desenvolvido com fogo e dragões**

