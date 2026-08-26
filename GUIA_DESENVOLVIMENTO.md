# Guia de Desenvolvimento - BancoFinanças

## 📖 Índice
1. [Configuração do Ambiente](#configuração-do-ambiente)
2. [Adicionando Novas Funcionalidades](#adicionando-novas-funcionalidades)
3. [Padrões de Código](#padrões-de-código)
4. [Debugging](#debugging)
5. [Git Workflow](#git-workflow)
6. [Performance e Otimizações](#performance-e-otimizações)

---

## 🔧 Configuração do Ambiente

### Windows Setup

#### 1. Instalar Java 21

```bash
# Verificar versão
java -version

# Deve retornar algo como:
# java version "21.0.x" 2024-09-17
```

#### 2. Instalar MySQL

```bash
# Download: https://dev.mysql.com/downloads/mysql/
# Após instalação, verificar:
mysql -u root -p
```

#### 3. Instalar Node.js

```bash
# Verificar versão
node -v    # Deve ser >= 16
npm -v     # npm vem com Node.js
```

#### 4. Clonar Projeto e Instalar Dependências

```bash
# Backend
cd c:\Users\Mateus Alves\Documents\bancofinancas
mvn clean install

# Frontend
cd frontend
npm install
```

### IDE Recomendada

**IntelliJ IDEA** (Recomendado)
- Download: https://www.jetbrains.com/idea/
- Plugins obrigatórios:
  - Spring Boot
  - Database
  - Git

**VS Code** (Alternativa)
- Extensões recomendadas:
  - Extension Pack for Java
  - Spring Boot Extension Pack
  - MySQL
  - REST Client

---

## ✨ Adicionando Novas Funcionalidades

### Passo 1: Criar a Entidade (Model)

**Arquivo:** `src/main/java/acc/br/bancofinancas/model/NovaEntidade.java`

```java
package acc.br.bancofinancas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "nova_entidade")
@Getter
@Setter
public class NovaEntidade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true)
    private String codigo;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    // Relacionamentos
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}
```

### Passo 2: Criar o Repository

**Arquivo:** `src/main/java/acc/br/bancofinancas/repository/NovaEntidadeRepository.java`

```java
package acc.br.bancofinancas.repository;

import acc.br.bancofinancas.model.NovaEntidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NovaEntidadeRepository extends JpaRepository<NovaEntidade, Long> {
    
    // Queries customizadas (se necessário)
    Optional<NovaEntidade> findByCodigo(String codigo);
    
    // Queries nativas (se necessário)
    // @Query("SELECT n FROM NovaEntidade n WHERE n.ativo = true")
    // List<NovaEntidade> findAllAtivos();
}
```

### Passo 3: Criar DTOs

**Arquivo Request:** `src/main/java/acc/br/bancofinancas/dto/CreateNovaEntidadeRequest.java`

```java
package acc.br.bancofinancas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNovaEntidadeRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    @NotBlank(message = "Código é obrigatório")
    private String codigo;
    
    private String descricao;
}
```

**Arquivo Response:** `src/main/java/acc/br/bancofinancas/dto/NovaEntidadeResponse.java`

```java
package acc.br.bancofinancas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class NovaEntidadeResponse {
    
    private Long id;
    private String nome;
    private String codigo;
    private String descricao;
    private LocalDateTime dataCriacao;
}
```

### Passo 4: Criar o Service

**Arquivo:** `src/main/java/acc/br/bancofinancas/service/NovaEntidadeService.java`

```java
package acc.br.bancofinancas.service;

import acc.br.bancofinancas.model.NovaEntidade;
import acc.br.bancofinancas.dto.CreateNovaEntidadeRequest;
import acc.br.bancofinancas.dto.NovaEntidadeResponse;
import acc.br.bancofinancas.repository.NovaEntidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NovaEntidadeService {
    
    @Autowired
    private NovaEntidadeRepository repository;
    
    @Transactional(readOnly = true)
    public List<NovaEntidadeResponse> listarTodas() {
        return repository.findAll()
            .stream()
            .map(this::converter)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public NovaEntidadeResponse obterPorId(Long id) {
        return repository.findById(id)
            .map(this::converter)
            .orElseThrow(() -> new IllegalArgumentException("Entidade não encontrada"));
    }
    
    @Transactional
    public NovaEntidadeResponse criar(CreateNovaEntidadeRequest request) {
        // Validações
        if (repository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Código já existe");
        }
        
        NovaEntidade entidade = new NovaEntidade();
        entidade.setNome(request.getNome());
        entidade.setCodigo(request.getCodigo());
        entidade.setDescricao(request.getDescricao());
        
        NovaEntidade salva = repository.save(entidade);
        return converter(salva);
    }
    
    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Entidade não encontrada");
        }
        repository.deleteById(id);
    }
    
    private NovaEntidadeResponse converter(NovaEntidade entidade) {
        return new NovaEntidadeResponse(
            entidade.getId(),
            entidade.getNome(),
            entidade.getCodigo(),
            entidade.getDescricao(),
            entidade.getDataCriacao()
        );
    }
}
```

### Passo 5: Criar o Controller

**Arquivo:** `src/main/java/acc/br/bancofinancas/Controller/NovaEntidadeController.java`

```java
package acc.br.bancofinancas.Controller;

import acc.br.bancofinancas.dto.CreateNovaEntidadeRequest;
import acc.br.bancofinancas.dto.NovaEntidadeResponse;
import acc.br.bancofinancas.service.NovaEntidadeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/nova-entidade")
@CrossOrigin(origins = "http://localhost:5173")
public class NovaEntidadeController {
    
    @Autowired
    private NovaEntidadeService service;
    
    @GetMapping
    public ResponseEntity<List<NovaEntidadeResponse>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NovaEntidadeResponse> obter(@PathVariable Long id) {
        return ResponseEntity.ok(service.obterPorId(id));
    }
    
    @PostMapping
    public ResponseEntity<NovaEntidadeResponse> criar(
        @Valid @RequestBody CreateNovaEntidadeRequest request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.criar(request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Passo 6: Criar Testes

**Arquivo:** `src/test/java/acc/br/bancofinancas/service/NovaEntidadeServiceTest.java`

```java
package acc.br.bancofinancas.service;

import acc.br.bancofinancas.model.NovaEntidade;
import acc.br.bancofinancas.dto.CreateNovaEntidadeRequest;
import acc.br.bancofinancas.dto.NovaEntidadeResponse;
import acc.br.bancofinancas.repository.NovaEntidadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NovaEntidadeServiceTest {
    
    private NovaEntidadeService service;
    
    @Mock
    private NovaEntidadeRepository repository;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new NovaEntidadeService();
        service.repository = repository;
    }
    
    @Test
    void testCriarNovaEntidade() {
        // Arrange
        CreateNovaEntidadeRequest request = new CreateNovaEntidadeRequest();
        request.setNome("Test");
        request.setCodigo("TEST001");
        
        NovaEntidade entidade = new NovaEntidade();
        entidade.setId(1L);
        entidade.setNome("Test");
        
        when(repository.findByCodigo("TEST001")).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(entidade);
        
        // Act
        NovaEntidadeResponse response = service.criar(request);
        
        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test", response.getNome());
        verify(repository, times(1)).save(any());
    }
}
```

### Passo 7: Migrar Banco de Dados (se necessário)

Se precisar de uma tabela mais complexa, crie um arquivo SQL:

**Arquivo:** `sql/V001__criar_nova_entidade.sql`

```sql
CREATE TABLE nova_entidade (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    codigo VARCHAR(100) UNIQUE NOT NULL,
    descricao TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cliente_id BIGINT,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 📐 Padrões de Código

### Estrutura de Camadas

```
Controller
    ↓
Service (Lógica de Negócio)
    ↓
Repository (Acesso a Dados)
    ↓
Database
```

### Anotações Importantes

```java
// Controller
@RestController          // Indica que é um REST controller
@RequestMapping("/api")  // Define prefixo da URL
@CrossOrigin            // Permite requisições de outro domínio
@PostMapping             // Requisição POST
@GetMapping              // Requisição GET
@PathVariable            // Variável na URL

// Service
@Service                 // Indica que é uma classe de serviço
@Transactional          // Gerencia transações do banco
@Transactional(readOnly = true)  // Apenas leitura

// Repository
@Repository             // Herda de JpaRepository

// Model
@Entity                 // Entidade JPA
@Table                  // Define nome da tabela
@Id                     // Chave primária
@GeneratedValue         // Auto-incremento
@Column                 // Define coluna
@ManyToOne              // Relacionamento N:1
@OneToMany              // Relacionamento 1:N
@Transient              // Campo que não é persistido
```

### Validação

```java
// DTOs com validação
public class MeuRequest {
    
    @NotNull(message = "Campo obrigatório")
    private String campo1;
    
    @NotBlank(message = "Não pode estar em branco")
    private String campo2;
    
    @Size(min = 3, max = 50, message = "Entre 3 e 50 caracteres")
    private String campo3;
    
    @Email(message = "Email inválido")
    private String email;
    
    @Min(value = 1, message = "Mínimo 1")
    private Integer quantidade;
}

// Usar em controller
@PostMapping
public ResponseEntity<Response> criar(@Valid @RequestBody MeuRequest request) {
    // request será validado automaticamente
}
```

### Tratamento de Exceções

```java
// Usar IllegalArgumentException para validações
throw new IllegalArgumentException("Mensagem de erro");

// ApiExceptionHandler captura e retorna automaticamente:
// Status: 400 (Bad Request)
// Body: { "erro": "Mensagem de erro" }

// Exemplo em Service:
public void deletar(Long id) {
    if (!repository.existsById(id)) {
        throw new IllegalArgumentException("Cliente não encontrado");
    }
    repository.deleteById(id);
}
```

---

## 🐛 Debugging

### Logging

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MeuService {
    
    private static final Logger log = LoggerFactory.getLogger(MeuService.class);
    
    public void fazer() {
        log.debug("Debug: iniciando operação");
        log.info("Info: operação em progresso");
        log.warn("Aviso: algo anormal");
        log.error("Erro: falha na operação", exception);
    }
}
```

### Debugger (IntelliJ)

1. Colocar breakpoint (click na linha)
2. Run → Debug (Shift + F9)
3. Executar ação que vai fazer parar no breakpoint
4. Inspecionar variáveis

### Verificar Requisição/Resposta

```bash
# Terminal - usar curl para testar endpoints
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"cpf":"12345678900","nome":"João"}'

# Ou usar REST Client no VS Code
# Arquivo: test.rest
POST http://localhost:8080/api/clientes
Content-Type: application/json
Authorization: Bearer eyJhbGc...

{
  "cpf": "12345678900",
  "nome": "João Silva"
}
```

### Ver SQL Executado

Já está ativado em `application.properties`:
```properties
spring.jpa.show-sql=true
```

Saída no console do Maven:
```
Hibernate: select cliente0_.id as id1_2_, cliente0_.bloqueado as bloqueado2_2_, cliente0_.cpf as cpf3_2_, cliente0_.data_criacao as data_cri4_2_, cliente0_.email as email5_2_, cliente0_.nome as nome6_2_ from clientes cliente0_
```

---

## 🔄 Git Workflow

### Criar Branch

```bash
# Atualizar main
git checkout main
git pull origin main

# Criar branch de feature
git checkout -b feature/nome-da-funcionalidade

# Exemplo:
git checkout -b feature/novo-relatorio
```

### Commit

```bash
# Adicionar arquivos
git add .

# Commit com mensagem descritiva
git commit -m "feat: adicionado novo módulo de relatórios"

# Padrão de mensagem:
# feat:  para novas funcionalidades
# fix:   para correção de bugs
# docs:  para documentação
# style: para formatação
# refactor: para refatoração
# test:  para testes
# chore: para tarefas gerais
```

### Pull Request

```bash
# Enviar branch para servidor
git push origin feature/novo-relatorio

# No GitHub/GitLab:
# 1. Ir para a página do repositório
# 2. Clicar em "New Pull Request"
# 3. Comparar branch com main
# 4. Escrever descrição
# 5. Submeter PR
```

---

## ⚡ Performance e Otimizações

### 1. Lazy Loading vs Eager Loading

```java
// ❌ Eager Loading (carrega relacionamentos)
@ManyToOne(fetch = FetchType.EAGER)
private Cliente cliente;

// ✅ Lazy Loading (carrega sob demanda)
@ManyToOne(fetch = FetchType.LAZY)
private Cliente cliente;
```

### 2. Paginação

```java
// Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}

// Service
@Autowired
private ClienteRepository repository;

public Page<ClienteResponse> listar(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("nome"));
    return repository.findAll(pageable)
        .map(this::converter);
}

// Controller
@GetMapping
public ResponseEntity<Page<ClienteResponse>> listar(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(service.listar(page, size));
}
```

### 3. Cache

```java
@Service
public class ClienteService {
    
    @Cacheable("clientes")
    public ClienteResponse obterPorId(Long id) {
        return repository.findById(id)
            .map(this::converter)
            .orElseThrow();
    }
    
    @CacheEvict(value = "clientes", key = "#id")
    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
```

### 4. Índices no Banco

```sql
-- Adicionar índice
CREATE INDEX idx_cliente_cpf ON clientes(cpf);
CREATE INDEX idx_conta_numero ON contas_correntes(numero_conta);

-- Melhorando queries frequentes
CREATE INDEX idx_operacao_conta_data ON operacoes(conta_id, data_operacao);
```

### 5. Batch Processing

```java
// Inserir múltiplos registros
@Transactional
public void criarEmLote(List<CreateClienteRequest> requests) {
    List<Cliente> clientes = requests.stream()
        .map(this::converter)
        .collect(Collectors.toList());
    
    repository.saveAll(clientes); // Mais eficiente que save individual
}
```

### 6. Query Optimization

```java
// Repository
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    // ❌ Sem índice - lento
    List<Cliente> findByNome(String nome);
    
    // ✅ Com Query customizada
    @Query("SELECT c FROM Cliente c WHERE c.cpf = :cpf")
    Optional<Cliente> findByCpf(@Param("cpf") String cpf);
    
    // ✅ Query nativa
    @Query(value = "SELECT * FROM clientes WHERE bloqueado = false LIMIT :limit", 
           nativeQuery = true)
    List<Cliente> findAtivos(@Param("limit") int limit);
}
```

---

## 📋 Checklist de Deployment

- [ ] Todos os testes passando (`mvn test`)
- [ ] Cobertura >= 80% (`mvn jacoco:report`)
- [ ] Sem warnings do compilador
- [ ] Documentação atualizada
- [ ] Migrations SQL aplicadas
- [ ] Variáveis de ambiente configuradas
- [ ] Endpoints testados com Postman/Insomnia
- [ ] Review do código realizado
- [ ] Build de produção: `mvn clean package -DskipTests`

---

**Última atualização:** 2026-08-26

