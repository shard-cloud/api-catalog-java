## 📖 O que é este template?

API REST completa para gerenciamento de catálogo de produtos construída com **Spring Boot**, **JPA/Hibernate**, **Flyway** e **PostgreSQL**. Inclui CRUD completo, validação, migrations, busca e paginação.

## 🎯 Casos de uso

- **E-commerce:** Catálogo de produtos para loja online
- **Inventário:** Sistema de gestão de estoque
- **Marketplace:** Base para plataforma de vendas
- **Aprendizado:** Exemplo de API REST com Spring Boot
- **Base para projetos:** Ponto de partida para sistemas maiores
- **Microserviço:** Componente de sistema de produtos

## ✨ Características principais

### API REST Completa

- ✅ CRUD completo de produtos (Create, Read, Update, Delete)
- ✅ Busca por nome e descrição
- ✅ Filtros por categoria
- ✅ Paginação de resultados
- ✅ Validação robusta de dados
- ✅ Ordenação customizável

### Performance e Escalabilidade

- ⚡ Spring Boot 3.x (framework enterprise)
- 🗄️ JPA/Hibernate ORM (type-safe e otimizado)
- 📊 Queries otimizadas com índices
- 🔄 Connection pooling automático
- 📦 Build otimizado com Docker

### Qualidade de Código

- ✅ Validação com Bean Validation
- ✅ Tratamento robusto de erros
- ✅ Logs estruturados
- ✅ Type safety com Java
- ✅ Testes automatizados (JUnit 5)

### DevOps

- 🐳 Docker e Docker Compose
- 🔄 Migrations com Flyway
- 🌱 Seeds para desenvolvimento
- 🏥 Health check endpoint
- 📊 Actuator para monitoramento

## 🏗️ Arquitetura

```
api-catalog-java/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/catalog/
│   │   │       ├── CatalogApplication.java
│   │   │       ├── config/          # Configurações
│   │   │       ├── controller/      # REST controllers
│   │   │       ├── entity/          # JPA entities
│   │   │       ├── repository/      # JPA repositories
│   │   │       ├── service/         # Business logic
│   │   │       ├── dto/             # Data Transfer Objects
│   │   │       └── exception/       # Exception handling
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/        # Flyway migrations
│   └── test/                        # Testes
└── docs/                            # Documentação
```

### Stack Tecnológica

- **Runtime:** Java 17+
- **Framework:** Spring Boot 3.2
- **ORM:** JPA/Hibernate
- **Migrations:** Flyway
- **Database:** PostgreSQL
- **Validation:** Bean Validation
- **Tests:** JUnit 5 + MockMvc
- **Container:** Docker + Docker Compose

## 📊 Modelo de Dados

### Product (Produto)
```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;
    
    @Size(max = 50)
    private String category;
    
    @NotNull
    @Min(0)
    private Integer stock;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

### Campos

- **id:** ID único auto-incremento
- **name:** Nome do produto (obrigatório, max 100 chars)
- **description:** Descrição opcional (max 500 chars)
- **price:** Preço obrigatório (decimal, > 0)
- **category:** Categoria opcional (max 50 chars)
- **stock:** Estoque obrigatório (inteiro, ≥ 0)
- **created_at:** Data de criação automática
- **updated_at:** Data de última atualização automática

## 🔗 Endpoints Disponíveis

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/actuator/health` | Health check + status do banco |
| GET | `/api/products` | Listar produtos (paginado) |
| GET | `/api/products/{id}` | Buscar produto por ID |
| POST | `/api/products` | Criar novo produto |
| PUT | `/api/products/{id}` | Atualizar produto |
| DELETE | `/api/products/{id}` | Deletar produto |
| GET | `/api/products/search` | Buscar produtos por texto |
| GET | `/api/products/category/{category}` | Filtrar por categoria |
| GET | `/api/products/categories` | Listar categorias |
| GET | `/api/products/low-stock` | Produtos com estoque baixo |

## 🚀 Quick Start

```bash
# Clone e acesse
cd api-catalog-java

# Suba com Docker Compose
docker-compose up -d

# Teste
curl http://localhost:80/actuator/health
curl http://localhost:80/api/products
```

## 📈 Performance Esperada

- **Latência:** < 50ms para queries simples
- **Throughput:** > 3k requests/segundo
- **Memória:** ~200MB em idle
- **Startup:** < 10 segundos

## 🔄 Próximos passos

Continue para [Configuração](./02-configuracao.md) para setup detalhado.
