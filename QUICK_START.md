# Quick Start Guide - BancoFinanças

**⚡ Comece em 10 minutos!**

---

## 🚀 Setup Inicial (Primeira Vez)

### 1️⃣ Pré-requisitos Rápidos

```bash
# Verificar Java
java -version
# Deve retornar Java 21+

# Verificar MySQL rodando
mysql -u root -p -e "SELECT 1;"
# Digite a senha: root

# Verificar Node.js
node -v   # Deve ser 16+
npm -v    # npm vem com Node
```

**Não tem instalado?** → [Ver Configuração Completa](GUIA_DESENVOLVIMENTO.md#configuração-do-ambiente)

---

### 2️⃣ Criar Banco de Dados (Primeira Vez)

```bash
# Abrir MySQL
mysql -u root -p

# Digite senha: root

# No prompt MySQL, executar:
CREATE DATABASE IF NOT EXISTS bancofinancas;
EXIT;
```

---

### 3️⃣ Compilar Backend

```bash
# Ir para raiz do projeto
cd c:\Users\SeuPC\Documents\bancofinancas

# Compilar e instalar dependências (1-2 min)
mvn clean install

# Se usar Maven Wrapper (já vem no projeto)
./mvnw clean install
```

✓ Se terminar com "BUILD SUCCESS", tudo OK!

---

### 4️⃣ Instalar Dependências Frontend

```bash
# Ir para pasta frontend
cd frontend

# Instalar pacotes npm (30-60 sec)
npm install

# Volta à pasta raiz depois
cd ..
```

---

## ▶️ Rodar a Aplicação

### Terminal 1: Backend

```bash
# Na pasta raiz (c:\Users\Mateus Alves\Documents\bancofinancas)
mvn spring-boot:run

# Aguarde aparecer:
# Tomcat started on port(s): 8080 (http)
# ✓ Backend pronto em http://localhost:8080
```

### Terminal 2: Frontend

```bash
# Em nova aba do terminal
cd frontend
npm run dev

# Aguarde aparecer:
# Local: http://localhost:5173/
# ✓ Frontend pronto em http://localhost:5173
```

---

## 🔓 Login Inicial

Acesse: **http://localhost:5173**

| Campo | Valor |
|-------|-------|
| Username | LOJA |
| Password | 1234 |

Ou tente:
- Username: **MASTER**
- Password: **1234**

---

## 📋 Próximos Passos

### Explorar Funcionalidades

1. **Clientes** → Ver lista de clientes cadastrados
2. **Contas** → Criar nova conta bancária
3. **Operações** → Fazer depósito/saque/transferência
4. **Lojas** → Gerenciar pedidos

### API Testing

Abra em novo navegador:
**http://localhost:8080/swagger-ui.html**

↑ Documentação interativa de todos os endpoints

### Visualizar Banco de Dados

```bash
# No MySQL
mysql -u root -p
# Senha: root

USE bancofinancas;
SHOW TABLES;
SELECT * FROM usuarios;
SELECT * FROM clientes;
```

---

## 🧪 Rodar Testes

```bash
# Terminal na pasta raiz
mvn test

# Aguarde: BUILD SUCCESS
# Testes passando ✓
```

### Cobertura de Testes

```bash
mvn clean test jacoco:report

# Abrir relatório no navegador:
# file:///c:/Users/Mateus Alves/Documents/bancofinancas/target/site/jacoco/index.html
```

---

## 🔧 Troubleshooting Rápido

### ❌ "Port 8080 already in use"

```bash
# Windows PowerShell
netstat -ano | findstr :8080

# Mata processo (substitua PID)
taskkill /PID 12345 /F

# Ou muda porta em application.properties:
# server.port=8081
```

### ❌ "Can't connect to MySQL"

```bash
# Verificar se MySQL está rodando
# Windows: Services → Search "MySQL" → Start
# Ou via terminal:
mysql -u root -p
# Se conectar = OK
```

### ❌ "Cannot find module 'react'"

```bash
# Na pasta frontend
rm -r node_modules
npm install
```

### ❌ "401 Unauthorized"

- Token expirado? → Faça login novamente
- Token não enviado? → Verificar localStorage no DevTools (F12)
- Token inválido? → Fazer logout + login

---

## 📝 Estrutura Básica de Arquivos

```
Onde está cada coisa:

BACKEND
├─ Controllers    → src/main/java/.../Controller/
├─ Services       → src/main/java/.../service/
├─ Models         → src/main/java/.../model/
├─ DTOs          → src/main/java/.../dto/
├─ Config        → src/main/java/.../config/
└─ application.properties → src/main/resources/

FRONTEND
├─ Pages         → frontend/src/pages/
├─ Components    → frontend/src/components/
├─ Styles        → frontend/src/styles.css
└─ main.jsx      → Ponto de entrada
```

---

## 🎯 Fluxo Típico de Desenvolvimento

### 1. Fazer Alteração no Código

```bash
# Editar arquivo (ex: src/main/java/.../service/ClienteService.java)
# IDE recarrega automaticamente
```

### 2. Testar no Backend

```bash
# Swagger já atualizado
# http://localhost:8080/swagger-ui.html
# Testar endpoint diretamente
```

### 3. Testar no Frontend

```bash
# Frontend já recarrega em tempo real (HMR - Hot Module Reload)
# http://localhost:5173
# Mudanças aparecem automaticamente
```

### 4. Rodar Testes

```bash
# Se criou nova classe/função
mvn test -Dtest=NomeDaClasseTest
```

### 5. Fazer Commit

```bash
git add .
git commit -m "feat: descrição da mudança"
git push origin main
```

---

## 📊 Endpoints Mais Usados

### Login
```bash
POST http://localhost:8080/api/auth/login
Body:
{
  "username": "LOJA",
  "password": "1234"
}
```

### Listar Clientes
```bash
GET http://localhost:8080/api/clientes
Headers:
Authorization: Bearer <seu_token>
```

### Criar Cliente
```bash
POST http://localhost:8080/api/clientes
Headers:
Authorization: Bearer <seu_token>
Body:
{
  "cpf": "12345678900",
  "nome": "João Silva",
  "email": "joao@email.com"
}
```

### Listar Contas
```bash
GET http://localhost:8080/api/contas
```

### Fazer Transferência
```bash
POST http://localhost:8080/api/operacoes/transferencia
Body:
{
  "contaOrigem": 1,
  "contaDestino": 2,
  "valor": 500.00,
  "descricao": "Pagamento"
}
```

**Ver todos?** → http://localhost:8080/swagger-ui.html

---

## 🎮 Console Developer (Frontend)

Abra no navegador: **F12 → Console**

### Ver Token Armazenado
```javascript
console.log(localStorage.getItem('token'))
```

### Ver Usuário
```javascript
console.log(JSON.parse(localStorage.getItem('user')))
```

### Fazer Requisição Teste
```javascript
fetch('http://localhost:8080/api/clientes', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  }
}).then(r => r.json()).then(d => console.log(d))
```

---

## 📚 Documentação Completa

- **[DOCUMENTACAO.md](DOCUMENTACAO.md)** - Visão geral completa
- **[GUIA_DESENVOLVIMENTO.md](GUIA_DESENVOLVIMENTO.md)** - Como desenvolver
- **[ARQUITETURA.md](ARQUITETURA.md)** - Diagramas e fluxos
- **Swagger** - http://localhost:8080/swagger-ui.html (API interativa)

---

## 🆘 Precisa de Ajuda?

### 1. Verificar Logs
```bash
# Backend - terminal rodando mvn spring-boot:run
# Procure por "ERROR" ou "WARN"

# Frontend - DevTools do navegador (F12)
# Procure por mensagens vermelhas
```

### 2. Verificar Database
```bash
mysql -u root -p
USE bancofinancas;
SHOW TABLES;
SELECT * FROM usuarios;
```

### 3. Testar Endpoint
```bash
curl -X GET http://localhost:8080/api/clientes \
  -H "Authorization: Bearer <token>"
```

### 4. Ver Relatório de Testes
```bash
mvn clean test jacoco:report
# Abrir: target/site/jacoco/index.html
```

---

## ✅ Checklist de Configuração

- [ ] Java 21 instalado
- [ ] MySQL rodando
- [ ] Node.js 16+ instalado
- [ ] Database `bancofinancas` criado
- [ ] `mvn clean install` executado com sucesso
- [ ] `npm install` executado na pasta frontend
- [ ] Backend rodando em http://localhost:8080
- [ ] Frontend rodando em http://localhost:5173
- [ ] Login funciona com LOJA/1234
- [ ] Swagger acessível em http://localhost:8080/swagger-ui.html

---

## 🚀 Pronto para Começar?

```bash
# Abra 2 terminais:

# Terminal 1
cd c:\Users\Mateus Alves\Documents\bancofinancas
mvn spring-boot:run

# Terminal 2
cd c:\Users\Mateus Alves\Documents\bancofinancas\frontend
npm run dev

# Acesse no navegador
# http://localhost:5173
```

**Boa sorte! 🎉**

---

**Última atualização:** 2026-08-26
**Versão:** 0.0.1-SNAPSHOT

