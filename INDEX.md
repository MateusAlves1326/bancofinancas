# 📚 Índice de Documentação - BancoFinanças

**Bem-vindo!** Este é o guia de navegação para toda a documentação do projeto.

---

## 🎯 Escolha Seu Ponto de Partida

### 👶 "Sou novo no projeto - quero começar RÁPIDO"
**→ Leia:** [QUICK_START.md](QUICK_START.md)
- ⏱️ Tempo: 10 minutos
- 📍 O que faz: Setup inicial + primeiros passos
- 🎯 Resultado: Sistema rodando em 2 terminais

---

### 📖 "Quero entender o projeto inteiro"
**→ Leia:** [README.md](README.md) + [DOCUMENTACAO.md](DOCUMENTACAO.md)
- ⏱️ Tempo: 30-40 minutos
- 📍 O que faz: Visão completa, tecnologias, funcionalidades
- 🎯 Resultado: Conhecimento profundo da arquitetura

---

### 💻 "Vou desenvolver novas funcionalidades"
**→ Leia:** [GUIA_DESENVOLVIMENTO.md](GUIA_DESENVOLVIMENTO.md)
- ⏱️ Tempo: 20 minutos (consulta contínua)
- 📍 O que faz: Padrões de código, como adicionar features
- 🎯 Resultado: Começar a programar seguindo conventions

---

### 🏗️ "Preciso entender a arquitetura e fluxos"
**→ Leia:** [ARQUITETURA.md](ARQUITETURA.md)
- ⏱️ Tempo: 30 minutos
- 📍 O que faz: Diagramas, fluxos de dados, autenticação
- 🎯 Resultado: Dominar os fluxos técnicos

---

## 📚 Mapa Completo de Documentação

```
DOCUMENTAÇÃO/
│
├─ README.md (COMECE AQUI!)
│  ├─ Características
│  ├─ Tech Stack
│  ├─ Quick Start
│  ├─ Endpoints
│  └─ Troubleshooting
│
├─ QUICK_START.md ⚡ (10 MINUTOS)
│  ├─ Pré-requisitos
│  ├─ Setup inicial
│  ├─ Rodar aplicação
│  ├─ Login
│  └─ Troubleshooting rápido
│
├─ DOCUMENTACAO.md (VISÃO COMPLETA)
│  ├─ Visão Geral
│  ├─ Tecnologias (tabelas detalhadas)
│  ├─ Arquitetura
│  ├─ Estrutura de Diretórios
│  ├─ Módulos e Funcionalidades
│  ├─ Configurações
│  ├─ Como Executar
│  ├─ Fluxos de Operações
│  ├─ Autenticação e Segurança
│  └─ Testes
│
├─ GUIA_DESENVOLVIMENTO.md (PROGRAMAÇÃO)
│  ├─ Configuração do Ambiente
│  ├─ Adicionando Novas Funcionalidades (7 passos)
│  ├─ Padrões de Código
│  ├─ Debugging
│  ├─ Git Workflow
│  └─ Performance e Otimizações
│
├─ ARQUITETURA.md (DIAGRAMAS)
│  ├─ Diagrama de Arquitetura Geral
│  ├─ Fluxo de Requisição HTTP
│  ├─ Fluxo de Operação Financeira
│  ├─ Fluxo de Autenticação JWT
│  ├─ Modelo de Dados (ER Diagram)
│  ├─ Fluxo de Estados
│  └─ Comunicação Frontend-Backend
│
└─ INDEX.md (ESTE ARQUIVO)
   └─ Navegação e referência cruzada
```

---

## 🔍 Guia Rápido por Tópico

### 🔐 Autenticação & Segurança
- **Começar:** DOCUMENTACAO.md → Autenticação e Segurança
- **Implementar:** GUIA_DESENVOLVIMENTO.md → Padrões de Código
- **Entender fluxo:** ARQUITETURA.md → Fluxo de Autenticação JWT

### 💰 Operações Financeiras
- **Visão geral:** DOCUMENTACAO.md → Módulos e Funcionalidades → Operações
- **Implementar:** GUIA_DESENVOLVIMENTO.md → Adicionando Novas Funcionalidades
- **Fluxo:** ARQUITETURA.md → Fluxo de Operação Financeira

### 📱 Frontend/React
- **Setup:** QUICK_START.md → Instalar Dependências Frontend
- **Estrutura:** DOCUMENTACAO.md → Estrutura de Diretórios → Frontend
- **Desenvolvimento:** GUIA_DESENVOLVIMENTO.md → Padrões

### 🗄️ Banco de Dados
- **Modelo:** ARQUITETURA.md → Modelo de Dados (ER Diagram)
- **Configurar:** QUICK_START.md ou DOCUMENTACAO.md → Configurações
- **Tabelas:** DOCUMENTACAO.md → Endpoints Principais

### 🧪 Testes
- **Como rodar:** QUICK_START.md ou DOCUMENTACAO.md → Testes
- **Escrever testes:** GUIA_DESENVOLVIMENTO.md → Adicionando Novas Funcionalidades (Passo 6)
- **Cobertura:** Todos os docs mencionam JaCoCo (80%)

### 🚀 Deployment
- **Preparar:** GUIA_DESENVOLVIMENTO.md → Checklist de Deployment
- **Build:** `mvn clean package -DskipTests`
- **Produção:** Documentação de deployment (consulte DOCUMENTACAO.md)

---

## 📋 Tabela de Referência Rápida

| Preciso de | Arquivo | Seção | Tempo |
|-----------|---------|-------|--------|
| Setup rápido | QUICK_START.md | Seções 1-4 | 10 min |
| Entender projeto | README.md | Tudo | 5 min |
| Visão completa | DOCUMENTACAO.md | Seções 1-10 | 40 min |
| Adicionar feature | GUIA_DESENVOLVIMENTO.md | Passo 1-7 | 20 min |
| Arquitetura | ARQUITETURA.md | Tudo | 30 min |
| API reference | Swagger | http://localhost:8080/swagger-ui.html | - |
| Troubleshooting | QUICK_START.md | Troubleshooting Rápido | 5 min |

---

## 🎯 Jornada Comum de Desenvolvimento

```
DIA 1: Onboarding
├─ Leia: QUICK_START.md
├─ Rode: Backend + Frontend
├─ Teste: Login com LOJA/1234
└─ Tempo: 15 min

DIA 2-3: Aprendizado
├─ Leia: README.md + DOCUMENTACAO.md
├─ Explore: Endpoints no Swagger
├─ Estude: ARQUITETURA.md
└─ Tempo: 2-3 horas

DIA 4+: Desenvolvimento
├─ Leia: GUIA_DESENVOLVIMENTO.md (quando precisar)
├─ Use: Padrões de Código
├─ Escreva: Testes (80%+ cobertura)
├─ Rodar: mvn test jacoco:report
└─ Commit: Seguindo conventions
```

---

## 🔗 Links Úteis

### Documentação Online
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [React Docs](https://react.dev)
- [Vite Docs](https://vitejs.dev)
- [JWT.io](https://jwt.io)

### Ferramentas
- **Swagger/OpenAPI:** http://localhost:8080/swagger-ui.html
- **JaCoCo Report:** `target/site/jacoco/index.html`
- **MySQL Workbench:** Para gerenciar BD visualmente
- **Postman/Insomnia:** Para testar endpoints

### Comandos Frequentes

```bash
# Desenvolvimento
mvn spring-boot:run           # Rodar backend
cd frontend && npm run dev    # Rodar frontend
mvn test                      # Rodar testes
mvn jacoco:report            # Gerar cobertura

# Build
mvn clean install            # Compilar tudo
mvn clean package            # Build para produção

# Git
git checkout -b feature/nome  # Nova branch
git commit -m "feat: msg"     # Commit
git push origin branch        # Push
```

---

## ❓ Perguntas Frequentes

### "Por onde começo?"
→ [QUICK_START.md](QUICK_START.md) (10 min)

### "Como funciona a autenticação?"
→ [ARQUITETURA.md](ARQUITETURA.md) - Fluxo de Autenticação JWT

### "Como adiciono nova funcionalidade?"
→ [GUIA_DESENVOLVIMENTO.md](GUIA_DESENVOLVIMENTO.md) - Passo 1-7

### "Qual é a estrutura do banco?"
→ [ARQUITETURA.md](ARQUITETURA.md) - Modelo de Dados

### "Como executo testes?"
→ [QUICK_START.md](QUICK_START.md) - Rodar Testes

### "Não consegui fazer login"
→ [QUICK_START.md](QUICK_START.md) - Troubleshooting Rápido

### "Qual é a porta do backend?"
→ 8080 (http://localhost:8080)

### "Qual é a porta do frontend?"
→ 5173 (http://localhost:5173)

### "Como vejo a API?"
→ http://localhost:8080/swagger-ui.html

### "Preciso mudar algo no banco"
→ [GUIA_DESENVOLVIMENTO.md](GUIA_DESENVOLVIMENTO.md) - Passo 7

---

## 📊 Estatísticas da Documentação

| Documento | Linhas | Seções | Código | Diagramas |
|-----------|--------|--------|--------|-----------|
| README.md | 300+ | 12 | ✅ | ✅ |
| QUICK_START.md | 280+ | 11 | ✅ | - |
| DOCUMENTACAO.md | 850+ | 20 | ✅ | ✅ |
| GUIA_DESENVOLVIMENTO.md | 700+ | 7 | ✅ | - |
| ARQUITETURA.md | 600+ | 8 | ✅ | ✅ |
| **TOTAL** | **2.630+** | **58** | **✅** | **✅** |

---

## ✅ Checklist de Leitura

- [ ] README.md (5 min) - Para entender o projeto
- [ ] QUICK_START.md (10 min) - Para ter rodando
- [ ] DOCUMENTACAO.md (40 min) - Para conhecer tudo
- [ ] GUIA_DESENVOLVIMENTO.md (20 min) - Para começar a codar
- [ ] ARQUITETURA.md (30 min) - Para dominar os fluxos

**Tempo total:** ~105 minutos (~1.75 horas)

---

## 🎓 Curva de Aprendizado

```
Tempo de Leitura vs. Conhecimento Adquirido

100% ├─────────────────────────────────●
      │                                 /│
80%  ├──────────────────────────────●──┤ Após GUIA + ARQUITETURA
      │                          /     │
60%  ├────────────────────────●────────┤ Após DOCUMENTACAO
      │                  /            │
40%  ├────────────●──────────────────┤ Após QUICK_START
      │      /                       │
20%  ├──●─────────────────────────────┤ Após README
      │                               │
0%   ├─────────────────────────────────
      0    30   60   90   120  150  180
              Minutos de Leitura
```

---

## 📞 Onde Pedir Ajuda

### Se você está **confuso**
- Leia [README.md](README.md) - Visão geral em 5 min

### Se não consegue **rodar** o projeto
- Siga [QUICK_START.md](QUICK_START.md) passo a passo
- Verifique a seção "Troubleshooting Rápido"

### Se quer **adicionar uma feature**
- Consulte [GUIA_DESENVOLVIMENTO.md](GUIA_DESENVOLVIMENTO.md) - Passo 1-7

### Se precisa **entender fluxos**
- Estude [ARQUITETURA.md](ARQUITETURA.md) - Especialmente os diagramas

### Se tem **dúvidas específicas**
- Procure na [DOCUMENTACAO.md](DOCUMENTACAO.md) (Ctrl+F)
- Teste no [Swagger UI](http://localhost:8080/swagger-ui.html)
- Consulte os [Testes Unitários](src/test/java)

---

## 🎉 Parabéns!

Você acabou de encontrar a documentação completa do projeto BancoFinanças! 

**Próximo passo:** Escolha uma das opções no topo da página e comece a explorar! 🚀

---

**Última atualização:** 2026-08-26  
**Versão:** 0.0.1-SNAPSHOT  
**Documentação:** ✅ Completa e Estruturada

