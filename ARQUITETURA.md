# Arquitetura Técnica - BancoFinanças

## 📊 Diagrama de Arquitetura Geral

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CAMADA DE APRESENTAÇÃO                             │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  Frontend - React 19.2.8 + Vite                                      │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │  Login.jsx      ClienteHome.jsx    HomeAgente.jsx  Loja.jsx    │  │   │
│  │  │  CriarConta/    Contas/            Operacoes/      Reversoes/  │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │  Componentes Reutilizáveis: Button, Header, SideBar, Modal      │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ↓ HTTP/REST                               │
│                        (com JWT no Authorization Header)                    │
└─────────────────────────────┬───────────────────────────────────────────────┘
                              │
                              │  PORT 8080
                              ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│                      CAMADA DE CONTROLE (Controllers)                       │
│                                                                              │
│  AuthController  ClienteController  AgenciaController  AgenteController     │
│  ContaCorrenteController            OperacaoController  LojaController      │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Responsabilidades:                                                 │   │
│  │  • Recebe requisições HTTP                                          │   │
│  │  • Valida entrada (anotações @Valid)                                │   │
│  │  • Delega para camada de serviço                                    │   │
│  │  • Retorna respostas HTTP com DTOs                                  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ↓                                         │
└─────────────────────────────┬───────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────────────────┐
│              CAMADA DE SEGURANÇA (Authentication & Authorization)           │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  JwtAuthenticationFilter                                             │   │
│  │  • Intercepta todas as requisições                                   │   │
│  │  • Extrai token JWT do header Authorization                         │   │
│  │  • Valida assinatura e expiração                                    │   │
│  │  • Carrega usuário em SecurityContext                               │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ↓                                         │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  SecurityConfig                                                      │   │
│  │  • Configura quais endpoints requerem autenticação                  │   │
│  │  • Define regras de autorização por role                            │   │
│  │  • Ativa CORS para frontend                                         │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ↓                                         │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  JwtService                                                          │   │
│  │  • Gera tokens JWT (encoding)                                       │   │
│  │  • Valida tokens JWT (decoding)                                     │   │
│  │  • Extrai claims (subject, expiration)                              │   │
│  │  • Usa HMAC-SHA256 com chave secreta                                │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ↓                                         │
└─────────────────────────────┬───────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────────────────┐
│                    CAMADA DE NEGÓCIO (Services)                             │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  AuthService          ClienteService        AgenciaService           │   │
│  │  AgenteService        ContaCorrenteService  OperacaoService          │   │
│  │  LojaService                                                         │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │  Responsabilidades:                                            │  │   │
│  │  │  • Implementa lógica de negócio                                │  │   │
│  │  │  • Valida regras de negócio                                    │  │   │
│  │  │  • Coordena operações entre repositories                      │  │   │
│  │  │  • Gerencia transações (@Transactional)                       │  │   │
│  │  │  • Converte entre Models e DTOs                               │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ↓                                         │
└─────────────────────────────┬───────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────────────────┐
│                  CAMADA DE PERSISTÊNCIA (Repositories)                      │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  ClienteRepository      AgenciaRepository     ContaCorrenteRepository │   │
│  │  UsuarioRepository      OperacaoRepository    PedidoLojaRepository    │   │
│  │  LojaItemRepository     SolicitacaoReversaoRepository                 │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │  Responsabilidades (Spring Data JPA):                          │  │   │
│  │  │  • Operações CRUD automáticas                                  │  │   │
│  │  │  • Queries customizadas (@Query)                               │  │   │
│  │  │  • Paginação e ordenação                                       │  │   │
│  │  │  • Mapeamento ORM automático                                   │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ↓ Dialeto SQL: MySQLDialect              │
└─────────────────────────────┬───────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────────────────┐
│                           BANCO DE DADOS                                    │
│                                                                              │
│                         MySQL 5.7+ / 8.0                                    │
│                     localhost:3306/bancofinancas                            │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  Tabelas Principais:                                                 │   │
│  │  usuarios  │ clientes  │ agencias  │ agentes  │ contas_correntes    │   │
│  │  operacoes │ extratos  │ pedidos_loja │ loja_itens                  │   │
│  │  solicitacoes_reversao                                              │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Fluxo de Requisição HTTP

```
┌────────────────────────────────────────────────────────────┐
│  1. CLIENTE faz requisição HTTP                           │
│     GET /api/contas/1                                      │
│     Headers:                                               │
│       Authorization: Bearer eyJhbGc...                    │
│       Content-Type: application/json                      │
└────────────────────┬───────────────────────────────────────┘
                     │
┌────────────────────▼───────────────────────────────────────┐
│  2. SPRING DISPATCHERSERVLET recebe requisição            │
│     (Spring MVC core)                                      │
└────────────────────┬───────────────────────────────────────┘
                     │
┌────────────────────▼───────────────────────────────────────┐
│  3. JWTAUTHENTICATIONFILTER intercepta                    │
│     • Extrai token do header                              │
│     • JwtService.validateToken(token)                     │
│     • Carrega usuário via CustomUserDetailsService        │
│     • SecurityContext.setAuthentication(...)              │
└────────────────────┬───────────────────────────────────────┘
                     │
          ┌──────────┴──────────┐
          │                     │
      ✓ Válido             ✗ Inválido
          │                     │
┌─────────▼──────────┐  ┌───────▼────────────┐
│ Requisição segue   │  │ Retorna 401        │
│ para Handler       │  │ Unauthorized       │
└─────────┬──────────┘  └────────────────────┘
          │
┌─────────▼──────────────────────────────────┐
│  4. CONTROLLER Handler Mapping             │
│     • Encontra método correspondente       │
│     • ContaCorrenteController.obter(1)    │
└─────────┬──────────────────────────────────┘
          │
┌─────────▼──────────────────────────────────┐
│  5. ARGUMENTRESOLVER processa argumentos  │
│     • Converte "1" (String) para Long     │
│     • Injeta dependências (@Autowired)    │
└─────────┬──────────────────────────────────┘
          │
┌─────────▼──────────────────────────────────┐
│  6. CONTROLLER valida autorização         │
│     • SecurityContext.getPrincipal()      │
│     • Verifica role do usuário            │
└─────────┬──────────────────────────────────┘
          │
          │ Chamada da lógica de negócio
          │
┌─────────▼──────────────────────────────────┐
│  7. SERVICE executa lógica                │
│     • ContaCorrenteService.obterPorId(1) │
│     • Validações de negócio               │
│     • @Transactional gerencia transação  │
└─────────┬──────────────────────────────────┘
          │
          │ Acesso aos dados
          │
┌─────────▼──────────────────────────────────┐
│  8. REPOSITORY acessa banco               │
│     • ContaCorrenteRepository             │
│     • JPA traduz para SQL                 │
│     • Hibernate executa query             │
│     • ResultSet processado                │
└─────────┬──────────────────────────────────┘
          │
┌─────────▼──────────────────────────────────┐
│  9. DATABASE retorna dados                │
│     • MySQL executa SELECT                │
│     • Retorna Entidade                    │
└─────────┬──────────────────────────────────┘
          │
          │ Conversão DTO + Serialização JSON
          │
┌─────────▼──────────────────────────────────┐
│ 10. SERVICE converte para DTO             │
│     • ContaCorrente → ContaCorrenteResponse│
└─────────┬──────────────────────────────────┘
          │
┌─────────▼──────────────────────────────────┐
│ 11. CONTROLLER retorna ResponseEntity     │
│     • Status: 200 OK                      │
│     • Body: DTO em JSON                   │
└─────────┬──────────────────────────────────┘
          │
┌─────────▼──────────────────────────────────┐
│ 12. MESSAGECONVERTER serializa JSON       │
│     • Usa Jackson ObjectMapper             │
│     • DTO → JSON                           │
│     • Content-Type: application/json      │
└─────────┬──────────────────────────────────┘
          │
┌─────────▼──────────────────────────────────┐
│ 13. HTTP RESPONSE enviado ao cliente      │
│     Status: 200                            │
│     Headers:                               │
│       Content-Type: application/json      │
│     Body:                                  │
│     {                                      │
│       "id": 1,                            │
│       "numeroConta": "0001-5",            │
│       "saldo": 1000.00,                   │
│       "bloqueada": false                  │
│     }                                      │
└────────────────────────────────────────────┘
          │
┌─────────▼──────────────────────────────────┐
│ 14. BROWSER recebe resposta               │
│     • Processa JSON                        │
│     • Renderiza UI                         │
│     • Exibe dados na tela                  │
└────────────────────────────────────────────┘
```

---

## Fluxo de Operação Financeira (Exemplo: Transferência)

```
CLIENTE INTERFACE
│
└─ onClick Transfer
   └─ POST /api/operacoes/transferencia
      ├─ contaOrigem: 123
      ├─ contaDestino: 456
      ├─ valor: 500.00
      └─ descricao: "Pagamento"
      
      ↓
      
OPERACAO CONTROLLER
│
└─ @PostMapping("/transferencia")
   └─ validaAutenticacao()
      │
      ├─ JWT validado ✓
      └─ SecurityContext carregado
      
      ↓
      
OPERACAO SERVICE
│
└─ criar(CreateOperacaoRequest)
   │
   ├─ 1. Validações Preliminares
   │  ├─ contaOrigem existe?
   │  ├─ contaDestino existe?
   │  ├─ valor > 0?
   │  └─ valor <= limiteTransferencia?
   │
   ├─ 2. Validações de Negócio
   │  ├─ contaOrigem está desbloqueada?
   │  ├─ contaDestino está desbloqueada?
   │  ├─ Saldo >= valor? (incluindo limites)
   │  └─ Mesmo titular? (verificar regra)
   │
   ├─ 3. Processamento da Operação
   │  │
   │  ├─ Início de Transação (@Transactional)
   │  │
   │  ├─ PASSO 1: Débito na conta origem
   │  │  ├─ ContaCorrente origem = find(123)
   │  │  ├─ origem.saldo -= 500.00
   │  │  ├─ origem.save()
   │  │  │
   │  │  └─ Cria Extrato débito
   │  │     ├─ Extrato.tipo = SAQUE
   │  │     ├─ Extrato.valor = 500.00
   │  │     ├─ Extrato.saldoAnterior = 1000.00
   │  │     ├─ Extrato.saldoAtual = 500.00
   │  │     └─ Extrato.save()
   │  │
   │  ├─ PASSO 2: Crédito na conta destino
   │  │  ├─ ContaCorrente destino = find(456)
   │  │  ├─ destino.saldo += 500.00
   │  │  ├─ destino.save()
   │  │  │
   │  │  └─ Cria Extrato crédito
   │  │     ├─ Extrato.tipo = DEPOSITO
   │  │     ├─ Extrato.valor = 500.00
   │  │     ├─ Extrato.saldoAnterior = 2000.00
   │  │     ├─ Extrato.saldoAtual = 2500.00
   │  │     └─ Extrato.save()
   │  │
   │  ├─ PASSO 3: Cria registro de Operação
   │  │  ├─ Operacao.contaOrigem = 123
   │  │  ├─ Operacao.contaDestino = 456
   │  │  ├─ Operacao.tipo = TRANSFERENCIA
   │  │  ├─ Operacao.valor = 500.00
   │  │  ├─ Operacao.status = CONCLUÍDA
   │  │  ├─ Operacao.dataOperacao = now()
   │  │  └─ Operacao.save()
   │  │
   │  └─ Commit Transação (se tudo OK)
   │     Ou Rollback (se erro)
   │
   ├─ 4. Retorna OperacaoResponse
   │  ├─ id: 1001
   │  ├─ tipo: TRANSFERENCIA
   │  ├─ valor: 500.00
   │  ├─ status: CONCLUÍDA
   │  └─ dataCriacao: 2026-08-26T14:30:00
   │
   └─ Se erro: lança IllegalArgumentException
      └─ ApiExceptionHandler captura
         └─ Retorna HTTP 400/422 com mensagem
         
      ↓
      
CONTROLLER RESPONSE
│
└─ Status: 200 OK
   Body: { operacaoResponse }
   
   ↓
   
FRONTEND
│
└─ Recebe sucesso
   └─ Exibe "Transferência realizada"
      └─ Recarrega saldo
         └─ Atualiza interface
```

---

## Fluxo de Autenticação (JWT)

```
CLIENTE SUBMETE LOGIN
│
├─ username: "cliente123"
└─ password: "senha123"
   
   POST /api/auth/login
   
   ↓
   
AUTH CONTROLLER
│
└─ recebeCredenciais(username, password)
   └─ AuthService.autenticar(username, password)
   
   ↓
   
AUTH SERVICE
│
├─ 1. Busca usuário no banco
│  │
│  └─ CustomUserDetailsService
│     └─ loadUserByUsername("cliente123")
│        │
│        ├─ SELECT * FROM usuarios WHERE username = "cliente123"
│        │
│        └─ Retorna: Usuario(id, username, password_hash, role)
│
├─ 2. Valida senha
│  │
│  ├─ BCrypt.matches(senha_entrada, senha_hash)
│  │
│  └─ ✓ Senha correta OR ✗ Senha incorreta?
│
├─ 3. Se credenciais OK
│  │
│  └─ JwtService.generateToken(usuario)
│     │
│     ├─ HEADER: { "alg": "HS256", "typ": "JWT" }
│     │ └─ Base64Encode(HEADER)
│     │
│     ├─ PAYLOAD (claims):
│     │  ├─ "sub": usuario.username    (Subject)
│     │  ├─ "iat": now()               (Issued At)
│     │  ├─ "exp": now + 3600s         (Expiration)
│     │  ├─ "role": usuario.role       (Custom claim)
│     │  ├─ "userId": usuario.id       (Custom claim)
│     │  └─ Base64Encode(PAYLOAD)
│     │
│     └─ SIGNATURE:
│        ├─ HMACSHA256(
│        │   base64Encode(header) + "." + base64Encode(payload),
│        │   "01234567890123456789012345678901"  // secret key
│        │ )
│        └─ Base64Encode(SIGNATURE)
│     
│     ↓
│     
│     JWT = HEADER.PAYLOAD.SIGNATURE
│        = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjbGllbnRlMTIzIiwiaWF0IjoxNjkzMDEyNjAwLCJleHAiOjE2OTMwMTYyMDB9.xyz..."
│
└─ 4. Retorna ao cliente
   │
   └─ HTTP 200 OK
      {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "tipo": "Bearer",
        "expiraEm": 3600
      }
      
      ↓
      
CLIENTE ARMAZENA TOKEN
│
└─ localStorage.setItem("token", token)

═══════════════════════════════════════════════════════════════

CLIENTE USA TOKEN EM REQUISIÇÃO PROTEGIDA
│
├─ GET /api/clientes
│ Header: Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
│
│  ↓
│  
JWT AUTHENTICATION FILTER
│
├─ 1. Extrai token do header
│  │
│  └─ Authorization: "Bearer " + token
│
├─ 2. Valida token
│  │
│  └─ JwtService.validateToken(token)
│     │
│     ├─ Verifica assinatura
│     │  └─ HMACSHA256(header.payload, secret) == signature?
│     │
│     ├─ Verifica expiração
│     │  └─ exp > now()?
│     │
│     └─ Extrai claims
│        └─ sub: username
│           exp: tempo_expiracao
│           role: permissão
│
├─ 3. Carrega usuário autenticado
│  │
│  └─ CustomUserDetailsService
│     └─ loadUserByUsername(token.getSubject())
│        │
│        └─ SecurityContext.setAuthentication(
│             new UsernamePasswordAuthenticationToken(
│               usuario,
│               null,
│               usuario.getAuthorities()
│             )
│           )
│
└─ 4. Requisição prossegue autenticada
   │
   └─ Controller/Service podem acessar:
      │
      ├─ SecurityContext.getAuthentication()
      ├─ SecurityContext.getPrincipal()
      └─ Usuario autenticado está disponível
         
         ↓
         
REQUISIÇÃO PROCESSADA COM AUTENTICAÇÃO
│
└─ Resposta retornada ao cliente
```

---

## Modelo de Dados (ER Diagram)

```
┌────────────────┐
│    USUARIOS    │
├────────────────┤
│ id (PK)        │
│ username (UQ)  │
│ password       │
│ role           │
│ cliente_id (FK)├──────┐
│ agencia_id (FK)├──┐   │
└────────────────┘  │   │
                    │   │
         ┌──────────┘   │
         │              │
    ┌────▼────────────┐ │
    │   AGENCIAS      │ │
    ├─────────────────┤ │
    │ id (PK)         │ │
    │ numero (UQ)     │ │
    │ nome            │ │
    │ endereco        │ │
    └─────────────────┘ │
         ▲               │
         │               │
         │        ┌──────▼────────┐
         │        │   CLIENTES    │
         │        ├───────────────┤
         │        │ id (PK)       │
         │        │ cpf (UQ)      │
         │        │ nome          │
         │        │ email         │
         │        │ bloqueado     │
    ┌────┴────────┤ data_criacao  │
    │             └───────────────┘
    │                   │
    │        ┌──────────┘
    │        │
    │     ┌──▼──────────────────┐
    │     │  CONTAS_CORRENTES   │
    │     ├─────────────────────┤
    │     │ id (PK)             │
    │     │ numero_conta (UQ)   │
    │     │ cliente_id (FK)     │
    │     │ saldo               │
    │     │ bloqueada           │
    │     │ data_criacao        │
    │     └──┬──────────────────┘
    │        │
    │        │ 1 para muitos
    │        │
    │     ┌──▼─────────────┐
    │     │   OPERACOES    │
    │     ├────────────────┤
    │     │ id (PK)        │
    │     │ conta_id (FK)  │
    │     │ tipo           │
    │     │ valor          │
    │     │ descricao      │
    │     │ data_operacao  │
    │     └────┬───────────┘
    │          │
    │          │
    │     ┌────▼──────────┐
    │     │   EXTRATOS    │
    │     ├───────────────┤
    │     │ id (PK)       │
    │     │ conta_id (FK) │
    │     │ tipo          │
    │     │ valor         │
    │     │ saldo_ant     │
    │     │ saldo_atual   │
    │     │ data          │
    │     └───────────────┘
    │
    └────────────────┐
                     │
              ┌──────▼────────────┐
              │     AGENTES       │
              ├───────────────────┤
              │ id (PK)           │
              │ agencia_id (FK)   │
              │ nome              │
              │ cargo             │
              │ email             │
              │ ativo             │
              └───────────────────┘


┌──────────────────────────────────────────────────┐
│            LOJA (Sistema Separado)              │
├──────────────────────────────────────────────────┤
│                                                  │
│  ┌────────────────────────────────────────────┐  │
│  │        PEDIDOS_LOJA                        │  │
│  ├────────────────────────────────────────────┤  │
│  │ id (PK)                                    │  │
│  │ numero_pedido (UQ)                         │  │
│  │ status (PENDENTE, PAGO, CANCELADO)        │  │
│  │ total                                      │  │
│  │ data_pedido                                │  │
│  └────┬─────────────────────────────────────┬─┘  │
│       │                                     │     │
│       │ 1 para muitos                       │     │
│       │                                     │     │
│  ┌────▼────────────────────────────────────┐│     │
│  │      LOJA_ITENS                         ││     │
│  ├────────────────────────────────────────┤│     │
│  │ id (PK)                                ││     │
│  │ pedido_id (FK)                         ││     │
│  │ descricao                              ││     │
│  │ preco                                  ││     │
│  │ quantidade                             ││     │
│  └────────────────────────────────────────┘│     │
│                                             │     │
└─────────────────────────────────────────────┘     │
                                                    │
┌──────────────────────────────────────────────────┐
│  SOLICITACOES_REVERSAO (Reversão de Operações)  │
├──────────────────────────────────────────────────┤
│ id (PK)                                          │
│ operacao_id (FK) → OPERACOES                    │
│ status (PENDENTE, APROVADA, REJEITADA)         │
│ motivo                                           │
│ data_solicitacao                                 │
│ data_resposta                                    │
└──────────────────────────────────────────────────┘
```

---

## Fluxo de Estados (State Machine)

### Estado da Conta

```
           CREATE
             │
             ▼
        ┌─────────┐
        │  ATIVA  │
        └────┬────┘
             │
    ┌────────┼────────┐
    │                 │
BLOQUEAR        DELETER
    │                 │
    ▼                 ▼
┌──────────┐    ┌──────────┐
│BLOQUEADA │    │ DELETADA │
└──────────┘    └──────────┘
    │
DESBLOQUEAR
    │
    ▼
  ATIVA
```

### Estado de Operação

```
             CREATE
              │
              ▼
         ┌─────────────┐
         │  PENDENTE   │
         └─────┬───────┘
               │
         ┌─────▼────────────┐
         │ VALIDAÇÃO & AUTH │
         └─────┬────────────┘
               │
      ┌────────┴────────┐
      │                 │
   ✓OK              ✗ERRO
      │                 │
      ▼                 ▼
┌──────────┐      ┌─────────┐
│CONCLUÍDA │      │ REJEITADA
└──────────┘      └─────────┘
      │
   REVERSÃO?
   SOLICITADA
      │
      ▼
┌──────────────┐
│  REVERTIDA   │
└──────────────┘
```

### Estado de Pedido Loja

```
         CREATE
           │
           ▼
    ┌────────────┐
    │  PENDENTE  │
    └─────┬──────┘
          │
       PAGAR
          │
          ▼
    ┌────────────┐
    │    PAGO    │
    └────────────┘
          │
    ou CANCELAR
          │
          ▼
    ┌────────────┐
    │ CANCELADO  │
    └────────────┘
```

---

## 📡 Comunicação Frontend-Backend

### Headers HTTP Padrão

```
REQUEST:
├─ Authorization: Bearer eyJhbGc...
├─ Content-Type: application/json
├─ Accept: application/json
└─ User-Agent: Mozilla/5.0...

RESPONSE:
├─ Content-Type: application/json; charset=UTF-8
├─ Content-Length: 1234
├─ X-Content-Type-Options: nosniff
├─ X-Frame-Options: SAMEORIGIN
├─ X-XSS-Protection: 1; mode=block
└─ Cache-Control: no-cache, no-store, must-revalidate
```

### Status HTTP Utilizados

```
✓ Sucesso
├─ 200 OK              - Requisição bem-sucedida
├─ 201 Created         - Recurso criado com sucesso
└─ 204 No Content      - Sucesso sem corpo na resposta

✗ Erro do Cliente
├─ 400 Bad Request     - Entrada inválida
├─ 401 Unauthorized    - Token inválido/expirado
├─ 403 Forbidden       - Sem permissão
├─ 404 Not Found       - Recurso não encontrado
└─ 422 Unprocessable   - Validação falhou

✗ Erro do Servidor
├─ 500 Internal Error  - Erro genérico
├─ 503 Unavailable     - Serviço indisponível
└─ 504 Gateway Timeout - Timeout na requisição
```

---

**Última atualização:** 2026-08-26

