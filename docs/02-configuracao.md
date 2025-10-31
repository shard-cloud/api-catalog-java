## 🔐 Variáveis de Ambiente

### Arquivo `.env`

Copie `env.example` para `.env` e configure:

```env
# Database (obrigatório)
DATABASE=postgres://user:password@localhost:5432/catalog_db

# Server (opcional)
PORT=80

# Environment
ENVIRONMENT=development
```

### Variáveis Detalhadas

#### `DATABASE` (obrigatório)

String de conexão PostgreSQL (suporta formatos `postgres://` e `postgresql://`):

```env
DATABASE=postgres://USER:PASSWORD@HOST:PORT/DATABASE
```

Exemplos:

```env
# Local
DATABASE=postgres://cataloguser:catalogpass@localhost:5432/catalog_db

# Docker Compose
DATABASE=postgres://cataloguser:catalogpass@db:5432/catalog_db

# Supabase
DATABASE=postgres://user:pass@db.xxx.supabase.co:5432/postgres

# Railway
DATABASE=postgres://user:pass@containers-us-west-1.railway.app:5432/railway

# Shard Cloud (com SSL)
DATABASE=postgres://user:pass@postgres.shardatabases.app:5432/database?ssl=true
```

#### `PORT` (opcional, padrão: 80)

Porta onde o servidor escutará:

```env
PORT=80      # Produção (padrão)
PORT=8080    # Desenvolvimento
```

#### `ENVIRONMENT` (opcional, padrão: development)

Ambiente de execução:

```env
ENVIRONMENT=development   # Logs verbosos, debug
ENVIRONMENT=production    # Logs JSON, otimizações
```

## 🔧 Configuração Automática de Banco

A aplicação usa uma classe `DatabaseConfig` que:

- ✅ **Lê automaticamente** a variável `DATABASE` do arquivo `.env`
- ✅ **Converte formatos** `postgres://` → `jdbc:postgresql://`
- ✅ **Configura SSL** automaticamente quando `?ssl=true` está na URL
- ✅ **Separa credenciais** da URL para configuração segura
- ✅ **Configura connection pool** otimizado

### Como funciona

```java
@Configuration
public class DatabaseConfig {
    @Bean
    @Primary
    public DataSource dataSource() {
        // Lê DATABASE do .env
        // Converte postgres:// para jdbc:postgresql://
        // Configura SSL se necessário
        // Retorna DataSource configurado
    }
}
```

## 🗄️ Configuração do Banco de Dados

### Opção 1: Docker Compose (Recomendado)

```bash
docker-compose up -d db
```

Credenciais padrão:
- **User:** cataloguser
- **Password:** catalogpass
- **Database:** catalog_db
- **Port:** 5432

### Opção 2: PostgreSQL Local

```bash
# Criar usuário e banco
psql -U postgres
CREATE USER cataloguser WITH PASSWORD 'catalogpass';
CREATE DATABASE catalog_db OWNER cataloguser;
GRANT ALL PRIVILEGES ON DATABASE catalog_db TO cataloguser;
```

### Opção 3: PostgreSQL em Cloud

**Supabase (Grátis):**
1. Crie projeto em https://supabase.com
2. Vá em Settings > Database
3. Copie Connection String
4. Cole no `.env`

**Railway:**
1. Crie projeto em https://railway.app
2. Adicione PostgreSQL plugin
3. Copie `DATABASE_URL`

## 🔄 Migrations

### Configurar Flyway

O Flyway está configurado no `pom.xml`:

```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <version>9.22.3</version>
    <configuration>
        <url>${database.url}</url>
        <user>${database.username}</user>
        <password>${database.password}</password>
        <locations>
            <location>classpath:db/migration</location>
        </locations>
    </configuration>
</plugin>
```

### Comandos Maven

```bash
# Aplicar migrations
mvn flyway:migrate

# Ver status
mvn flyway:info

# Validar migrations
mvn flyway:validate

# Limpar banco (cuidado!)
mvn flyway:clean
```

### Criar Nova Migration

```bash
# 1. Criar arquivo manualmente em src/main/resources/db/migration/
# Nome: V3__Add_new_field.sql

# 2. Conteúdo do arquivo:
-- +flyway
ALTER TABLE products ADD COLUMN new_field VARCHAR(100);
-- -flyway

# 3. Aplicar
mvn flyway:migrate
```

### Rollback

```bash
# Flyway não suporta rollback automático
# Para reverter, crie uma nova migration:

-- V4__Remove_new_field.sql
ALTER TABLE products DROP COLUMN new_field;
```

## 🌱 Seeds

### Dados Iniciais

Os dados iniciais estão em `V2__Insert_sample_products.sql`:

```sql
INSERT INTO products (name, description, price, category, stock) VALUES
('Smartphone XYZ', 'Smartphone com tela de 6.1 polegadas', 999.99, 'Electronics', 50),
('Laptop ABC', 'Laptop com processador Intel i7', 1299.99, 'Electronics', 25),
-- ... mais produtos
```

### Customizar Seeds

Edite `src/main/resources/db/migration/V2__Insert_sample_products.sql`:

```sql
INSERT INTO products (name, description, price, category, stock) VALUES
('Seu Produto', 'Descrição do seu produto', 99.99, 'Sua Categoria', 10),
-- ...
```

## 🐳 Docker

### Build Customizado

```bash
# Build da imagem
docker build -t api-catalog-java .

# Run com variáveis
docker run -p 8080:8080 \
  -e DATABASE=postgresql://user:pass@host:5432/db \
  api-catalog-java
```

### Docker Compose Personalizado

```yaml
version: '3.8'
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_USER: ${DB_USER:-cataloguser}
      POSTGRES_PASSWORD: ${DB_PASSWORD:-catalogpass}
      POSTGRES_DB: ${DB_NAME:-catalog_db}
    volumes:
      - postgres_data:/var/lib/postgresql/data

  api:
    build: .
    environment:
      DATABASE: postgresql://${DB_USER:-cataloguser}:${DB_PASSWORD:-catalogpass}@db:5432/${DB_NAME:-catalog_db}
    depends_on:
      - db
```

## 🔧 Configuração Avançada

### Logs Estruturados

Editar `src/main/resources/application.yml`:

```yaml
logging:
  level:
    com.example.catalog: DEBUG
    org.springframework.web: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd'T'HH:mm:ss.SSS'Z'} [%thread] %-5level %logger{36} - %msg%n"
```

### Connection Pool

Editar `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### CORS

Editar `src/main/java/com/example/catalog/config/WebConfig.java`:

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOrigins("https://meusite.com", "https://app.meusite.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
}
```

### Rate Limiting (Opcional)

```xml
<!-- Adicionar no pom.xml -->
<dependency>
    <groupId>com.github.bucket4j</groupId>
    <artifactId>bucket4j-spring-boot-starter</artifactId>
    <version>7.6.0</version>
</dependency>
```

```java
// Controller
@RateLimited(bandwidths = @Bandwidth(capacity = 100, time = 1, unit = ChronoUnit.MINUTES))
@GetMapping("/api/products")
public ResponseEntity<Page<ProductDto>> getAllProducts(...) {
    // ...
}
```

## 🎯 Próximos passos

Continue para [Rodando](./03-rodando.md) para executar e testar a API.
