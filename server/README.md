# eCommerceApp Development Guidelines

This document provides essential information for developers working on the eCommerceApp project.

## Build/Configuration Instructions

### Prerequisites
- JDK 11 or newer (Amazon Corretto 22 recommended for production)
- Gradle 8.4 or newer (included via Gradle wrapper)

### Building the Project
The project uses Gradle as its build system. The Gradle wrapper is included in the repository, so you don't need to install Gradle separately.

#### Basic Build Commands
```bash
# Build the project
./gradlew build

# Run the application locally
./gradlew run

# Build a fat JAR for deployment
./gradlew buildFatJar
```

### Docker Build
The project includes a multi-stage Dockerfile for containerized deployment:

```bash
# Build the Docker image
docker build -t ecommerceapp:latest .

# Run the container
docker run -p 8080:8080 ecommerceapp:latest
```

The Dockerfile uses a three-stage build process:
1. Cache Gradle dependencies
2. Build the application
3. Create a minimal runtime image with Amazon Corretto 22

### Configuration
The application is configured via `src/main/resources/application.yaml`. Key configuration options:

- Server port: Set via the `PORT` environment variable or defaults to 8080
- Application module: `dev.aoriani.ecomm.ApplicationKt.module`
- Database configuration:
  - URL: "jdbc:h2:./data/products"
  - Driver: "org.h2.Driver"
- Logging level: Set to `DEBUG` by default

Example configuration:
```yaml
ktor:
  application:
    modules:
      - dev.aoriani.ecomm.ApplicationKt.module
  deployment:
    port: "$PORT:8080"

ecomm:
  database:
    url: "jdbc:sqlite:./data/products.db"
    driver: "org.sqlite.JDBC"
  logging:
    level: "DEBUG"
```

For development, you can enable development mode:
```bash
./gradlew run -Pdevelopment=true
```

## Testing Information

### Running Tests
The project uses Kotlin's built-in testing framework with JUnit.

```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "dev.aoriani.ecomm.graphql.repository.ProductRepositoryTest"

# Run a specific test method
./gradlew test --tests "dev.aoriani.ecomm.graphql.repository.ProductRepositoryTest.testGetAllProducts"
```

### Test Structure
Tests are organized in the `src/test/kotlin` directory, mirroring the structure of the main source code.

#### Application Tests
The project currently includes basic application tests that verify the HTTP endpoints are working correctly. Example:

```kotlin
class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ecomm.database.url" to "jdbc:sqlite:./data/products.db",
                "ecomm.database.driver" to "org.sqlite.JDBC"
            )
        }
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
```

#### Repository Tests (Example)
When testing repositories, you should focus on testing individual components in isolation. Example:

```kotlin
// Example of how to test a repository
class ProductRepositoryTest {
    @Test
    fun testGetAllProducts() = testApplication {
        // Set up test environment
        environment {
            config = MapApplicationConfig(
                "ecomm.database.url" to "jdbc:sqlite:file::memory:?cache=shared",
                "ecomm.database.driver" to "org.sqlite.JDBC"
            )
        }

        // Initialize application
        application {
            module()
        }

        // Test repository methods
        val repository: ProductRepository by dependencies
        val products = repository.getAll()
        assertEquals(16, products.size)
    }
}
```

#### GraphQL Tests (Example)
GraphQL endpoints can be tested using Ktor's `testApplication` function:

```kotlin
// Example of how to test GraphQL endpoints
class ProductQueryTest {
    @Test
    fun testProductsQuery() = testApplication {
        // Set up test environment
        environment {
            config = MapApplicationConfig(
                "ecomm.database.url" to "jdbc:sqlite:file::memory:?cache=shared",
                "ecomm.database.driver" to "org.sqlite.JDBC"
            )
        }

        // Initialize application
        application {
            module()
        }

        // Test GraphQL endpoint
        val response = client.post("/graphql") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "query": "{ products { id name price } }"
                }
            """.trimIndent())
        }

        assertEquals(HttpStatusCode.OK, response.status)
        // Additional assertions...
    }
}
```

### Adding New Tests
When adding new tests:

1. Create a test class in the appropriate package under `src/test/kotlin`
2. Use the `@Test` annotation for test methods
3. Follow the Given-When-Then pattern for clarity
4. Use descriptive method names (e.g., `testGetProductById_nonExistingProduct`)
5. Include assertions to verify expected behavior

## Development Guidelines

### Project Structure
The project follows a clean architecture approach with clear separation of concerns:

- `src/main/kotlin/dev/aoriani/ecomm/` - Main application code
  - `Application.kt` - Application entry point
  - `config/` - Application configuration
    - `cache.kt` - Cache configuration
    - `call_logging.kt` - Request logging configuration
    - `compression.kt` - Response compression configuration
    - `cors.kt` - CORS configuration
    - `database.kt` - Database connection configuration
    - `graphql.kt` - GraphQL server configuration
    - `mcp.kt` - Model Context Protocol configuration
    - `routes.kt` - HTTP routing configuration
    - `status_pages.kt` - Error handling configuration
  - `domain/` - Domain layer (business logic)
    - `models/` - Domain entities
    - `repositories/` - Repository interfaces
    - `usecases/` - Business logic use cases
  - `data/` - Data layer (repository implementations)
    - `database/` - Database-specific code
    - `repositories/` - Repository implementations
  - `presentation/` - Presentation layer
    - `graphql/` - GraphQL-related code
      - `models/` - GraphQL data models
      - `queries/` - GraphQL query resolvers
      - `hooks/` - Custom schema generator hooks
    - `mcp/` - Model Context Protocol implementation
      - `models/` - MCP data models
      - `tools/` - MCP tool implementations

### GraphQL Development
The project uses the ExpediaGroup GraphQL Kotlin library for implementing GraphQL functionality:

#### GraphQL Models
GraphQL models are defined in the `presentation/graphql/models` package. These models are annotated with GraphQL-specific annotations to provide schema documentation:

```kotlin
@GraphQLDescription("Represents a product available in the e-commerce catalog")
data class Product(
    @property:GraphQLDescription("Unique identifier of the product")
    val id: ID,

    @property:GraphQLDescription("Name of the product")
    val name: String,
    
    // Other properties...
)
```

Models also include mapping functions to convert domain models to GraphQL-specific models:

```kotlin
fun DomainProduct.toGraphQlProduct(): Product = Product(
    id = ID(this.id),
    name = this.name,
    // Other properties...
)
```

#### GraphQL Queries
Query resolvers are implemented in the `presentation/graphql/queries` package. Each query class implements the `Query` interface and is injected with the necessary use cases:

```kotlin
@GraphQLDescription("Root entry point for product-related queries")
class ProductQuery(
    private val getAllProducts: GetAllProductsUseCase,
    private val getProductById: GetProductByIdUseCase
) : Query {
    @GraphQLDescription("Retrieve all products available in the catalog")
    suspend fun products(): List<Product> {
        // Implementation...
    }

    @GraphQLDescription("Fetch a single product by its unique identifier")
    suspend fun product(
        @GraphQLDescription("The unique ID of the product to retrieve")
        id: ID
    ): Product {
        // Implementation...
    }
}
```

#### Schema Generator Hooks
Custom schema generator hooks are implemented in the `presentation/graphql/hooks` package. These hooks allow for customization of the GraphQL schema generation process:

```kotlin
class ProductSchemaGeneratorHooks : SchemaGeneratorHooks {
    override fun willGenerateGraphQLType(type: KType): GraphQLType? {
        // Custom type mapping logic...
    }
}
```

#### GraphQL Configuration
The GraphQL server is configured in the `config/graphql.kt` file, which registers the query resolvers and schema generator hooks:

```kotlin
fun Application.configureGraphQL() {
    install(GraphQL) {
        schema {
            packages = listOf("dev.aoriani.ecomm.presentation.graphql.models")
            queries = listOf(
                ProductQuery(
                    getAllProducts = dependencies.get(),
                    getProductById = dependencies.get()
                )
            )
            hooks = ProductSchemaGeneratorHooks()
        }
        playground = true
    }
}
```

### Model Context Protocol (MCP) Server

The Model Context Protocol (MCP) server allows AI models like Gemini to interact with the application's data in a structured way. The MCP implementation follows a clean architecture approach:

#### MCP Tools
MCP tools are implemented in the `presentation/mcp/tools` package. Each tool extends the `McpTool` interface and is responsible for handling a specific type of request:

```kotlin
class GetProductByIdTool(
    private val getProductByIdUseCase: GetProductByIdUseCase
) : McpTool<GetProductByIdRequest, Product> {
    override val name = "get_product_by_id"
    override val description = "Retrieves details of a single product identified by a unique product ID"
    
    override suspend fun execute(request: GetProductByIdRequest): CallToolResult {
        // Implementation...
    }
}
```

#### MCP Models
MCP models are defined in the `presentation/mcp/models` package. These models represent the data structures used by the MCP tools:

```kotlin
@Serializable
data class Product(
    val id: String,
    val name: String,
    val price: BigDecimal,
    // Other properties...
)
```

#### Available MCP Tools

1. **get_products_list**
   - Description: Retrieves all products in the catalog
   - Input: None
   - Output: An object containing a `products` array

2. **get_product_by_id**
   - Description: Retrieves details of a single product identified by a unique product ID
   - Input: An object with a required `id` property (string)
   - Output: A product object

#### MCP Configuration
The MCP server is configured in the `config/mcp.kt` file, which registers the available tools:

```kotlin
fun Application.configureMcp() {
    mcp {
        Server(
            serverInfo = Implementation("eCommerceApp", "1.0.0"),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = null)
                )
            )
        ).apply {
            addTool(GetAllProductsTool(dependencies.get()))
            addTool(GetProductByIdTool(dependencies.get()))
        }
    }
}
```

For detailed documentation on the MCP server, its architecture, and the available tools, please see [MCP.md](MCP.md).

### Code Style
- Follow Kotlin conventions and idioms
- Use data classes for models
- Prefer immutability (val over var)
- Use descriptive names and add GraphQL descriptions for schema documentation
- Include comments for complex logic

### Debugging
- GraphiQL interface is available at `/graphiql` for testing GraphQL queries
- Development mode provides additional logging and hot reloading
- Use the `[DEBUG_LOG]` prefix in test logs for better visibility