# API de Catálogo (Catalog API)

API REST completa para gerenciamento de catálogo de produtos com Spring Boot, JPA, Flyway e PostgreSQL. CRUD completo, validação, migrations e health check.

## 🎯 Características

- ✅ CRUD completo de produtos
- ✅ Validação de dados robusta
- ✅ Migrations com Flyway
- ✅ PostgreSQL com JPA/Hibernate
- ✅ Spring Boot 3.x
- ✅ Docker e Docker Compose
- ✅ Health check endpoint
- ✅ CORS configurado
- ✅ Logs estruturados
- ✅ Testes automatizados

## 📋 Requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL 14+ (ou use Docker Compose)

## 🚀 Como rodar

### Com Docker Compose (Recomendado)

```bash
# Copiar .env
cp .env.example .env

# Subir banco e aplicação
docker-compose up -d

# Acesse: http://localhost:80/health
```

### Sem Docker

```bash
# Configurar DATABASE no .env
cp .env.example .env

# Instalar dependências
mvn clean install

# Rodar aplicação
mvn spring-boot:run
```

## 📦 Scripts

```bash
mvn spring-boot:run     # Servidor de desenvolvimento
mvn clean install       # Build da aplicação
mvn test               # Executar testes
mvn flyway:migrate     # Aplicar migrations
mvn flyway:info        # Status das migrations
```

## 🔗 Endpoints

### Health Check
```
GET /actuator/health
```

### Produtos

```
GET    /api/products           # Listar todos (com paginação)
GET    /api/products/{id}      # Buscar por ID
POST   /api/products           # Criar novo
PUT    /api/products/{id}      # Atualizar
DELETE /api/products/{id}      # Deletar
GET    /api/products/search    # Buscar por nome/descrição
```

### Exemplos de uso

**Criar produto:**
```bash
curl -X POST http://localhost:80/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Smartphone XYZ",
    "description": "Smartphone com tela de 6.1 polegadas",
    "price": 999.99,
    "category": "Electronics",
    "stock": 50
  }'
```

**Listar com paginação:**
```bash
# Página 0, 10 itens
GET /api/products?page=0&size=10

# Busca por nome
GET /api/products/search?q=Smartphone
```

## 📂 Estrutura

```
api-catalog-java/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── catalog/
│   │   │               ├── CatalogApplication.java
│   │   │               ├── config/          # Configurações
│   │   │               ├── controller/      # REST controllers
│   │   │               ├── entity/          # JPA entities
│   │   │               ├── repository/      # JPA repositories
│   │   │               ├── service/         # Business logic
│   │   │               └── dto/             # Data Transfer Objects
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/    # Flyway migrations
│   └── test/                    # Testes
├── docker-compose.yml
└── docs/                        # Documentação
```

## 🗄️ Banco de dados

### Modelo de Product

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
    
    @DecimalMin("0.0")
    private BigDecimal price;
    
    @Size(max = 50)
    private String category;
    
    @Min(0)
    private Integer stock;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

### Conexão

A aplicação lê a string de conexão da variável `DATABASE` (suporta formatos `postgres://` e `postgresql://`):

```env
DATABASE=postgres://user:password@localhost:5432/catalog_db
```

## 🐳 Docker

### Build

```bash
docker build -t api-catalog-java .
```

### Run

```bash
docker run -p 80:80 \
  -e DATABASE=postgres://user:pass@host:5432/db \
  api-catalog-java
```

## 📊 Logs

A aplicação usa logs estruturados (JSON em produção):

```json
{
  "timestamp": "2025-01-15T10:30:00.000Z",
  "level": "INFO",
  "message": "Started CatalogApplication",
  "logger": "com.example.catalog.CatalogApplication"
}
```

## 🧪 Testes

```bash
# Rodar todos os testes
mvn test

# Com coverage
mvn test jacoco:report

# Testes específicos
mvn test -Dtest=ProductControllerTest
```

## 📄 Licença

MIT

---