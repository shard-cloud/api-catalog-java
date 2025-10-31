## 🚀 Desenvolvimento Local

### Pré-requisitos

```bash
# Verificar versões
java -version    # 17+
mvn -version     # 3.8+
docker --version # 20+ (opcional)
```

### Setup Inicial

```bash
# 1. Configurar .env
cp env.example .env
# Edite .env com suas configurações

# 2. Instalar dependências
mvn clean install

# 3. Rodar migrations (se necessário)
mvn flyway:migrate
```

### Iniciar Servidor

```bash
# Modo desenvolvimento (hot-reload)
mvn spring-boot:run

# Ou build e run
mvn clean package
java -jar target/catalog-1.0.0.jar
```

Servidor rodando em **http://localhost:80**

### Logs Esperados

```
2025-01-15 10:30:00.000  INFO 12345 --- [main] c.e.c.CatalogApplication : Started CatalogApplication in 3.456 seconds
2025-01-15 10:30:00.001  INFO 12345 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 80 (http)
```

## 🐳 Com Docker Compose

### Subir Tudo

```bash
# Subir banco + API
docker-compose up -d

# Ver logs
docker-compose logs -f api

# Status
docker-compose ps
```

### Migrations (Docker)

```bash
# As migrations rodam automaticamente na inicialização
# Para rodar manualmente:
docker-compose exec api mvn flyway:migrate

# Ver logs
docker-compose logs -f api
```

### Parar

```bash
# Parar containers
docker-compose down

# Parar e remover volumes
docker-compose down -v
```

## 🧪 Testando Endpoints

### Health Check

```bash
curl http://localhost:80/actuator/health

# Resposta esperada:
# {
#   "status": "UP",
#   "components": {
#     "db": {"status": "UP"},
#     "diskSpace": {"status": "UP"}
#   }
# }
```

### Produtos

**Listar produtos:**
```bash
curl http://localhost:80/api/products

# Com paginação
curl "http://localhost:80/api/products?page=0&size=5"

# Com ordenação
curl "http://localhost:80/api/products?sort=price,desc"
```

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

# Resposta (201 Created):
# {
#   "id": 1,
#   "name": "Smartphone XYZ",
#   "description": "Smartphone com tela de 6.1 polegadas",
#   "price": 999.99,
#   "category": "Electronics",
#   "stock": 50,
#   "createdAt": "2025-01-15T10:30:00",
#   "updatedAt": "2025-01-15T10:30:00"
# }
```

**Buscar por ID:**
```bash
curl http://localhost:80/api/products/1
```

**Atualizar produto:**
```bash
curl -X PUT http://localhost:80/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Smartphone XYZ Pro",
    "price": 1199.99
  }'
```

**Deletar produto:**
```bash
curl -X DELETE http://localhost:80/api/products/1

# Resposta: 204 No Content
```

**Buscar produtos:**
```bash
# Busca por nome ou descrição
curl "http://localhost:80/api/products/search?q=smartphone"

# Filtrar por categoria
curl "http://localhost:80/api/products/category/Electronics"

# Listar categorias
curl "http://localhost:80/api/products/categories"

# Produtos com estoque baixo
curl "http://localhost:80/api/products/low-stock?threshold=10"
```

## 🧪 Testes Automatizados

### Rodar Todos os Testes

```bash
# Testes básicos
mvn test

# Com coverage
mvn test jacoco:report

# Verbose
mvn test -Dtest=ProductControllerTest

# Testes específicos
mvn test -Dtest=ProductControllerTest#testCreateProduct
```

### Testes Disponíveis

- ✅ Health check endpoint
- ✅ CRUD de produtos
- ✅ Validação de dados
- ✅ Busca e paginação
- ✅ Filtros por categoria
- ✅ Error handling

### Output Esperado

```
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

## 🔍 Debug e Troubleshooting

### Ver Queries SQL

Editar `src/main/resources/application.yml`:

```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

### Verificar Conexão do Banco

```bash
# Testar conexão
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:postgresql://localhost:5432/catalog_db"
```

### Verificar Migrations

```bash
# Status atual
mvn flyway:info

# Histórico
mvn flyway:info -Dflyway.info=true
```

### Logs Detalhados

```bash
# Desenvolvimento
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.example.catalog=DEBUG"

# Docker
docker-compose logs -f api
```

### Verificar Porta em Uso

```bash
# Linux/Mac
lsof -i :80

# Windows
netstat -ano | findstr :80
```

## 📈 Performance Testing

### Simples (cURL)

```bash
# Medir latência
time curl http://localhost:80/api/products
```

### com Apache Bench

```bash
# 1000 requests, 10 concurrent
ab -n 1000 -c 10 http://localhost:80/api/products
```

### com Artillery

```bash
npm install -g artillery

# Criar config.yml
artillery quick --count 100 --num 10 http://localhost:80/actuator/health

# Resultados esperados:
# - p95 latency: < 100ms
# - Requests/sec: > 500
```

## 🔄 Hot Reload

O Spring Boot DevTools está configurado para hot-reload:

- Alterações em `src/main/java/**/*.java` recarregam automaticamente
- Alterações em `src/main/resources/**` recarregam automaticamente
- Não precisa reiniciar manualmente

## 🎯 Checklist de Validação

Antes de considerar pronto:

- [ ] `mvn clean install` sem erros
- [ ] `mvn flyway:migrate` aplica migrations
- [ ] `mvn spring-boot:run` inicia servidor
- [ ] `curl /actuator/health` retorna status UP
- [ ] `curl /api/products` retorna lista
- [ ] `mvn test` passa todos os testes
- [ ] Docker Compose sobe corretamente
- [ ] Logs estruturados aparecem
- [ ] Validação funciona
- [ ] Busca funciona

## 🚀 Próximos passos

Continue para [Deploy](./04-deploy.md) para colocar em produção.
