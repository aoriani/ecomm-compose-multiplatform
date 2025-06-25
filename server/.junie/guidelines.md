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

#### Unit Tests
Unit tests focus on testing individual components in isolation. Example:

```kotlin
// Testing a repository
class ProductRepositoryTest {
    @Test
    fun testGetAllProducts() {
        val products = ProductRepository.getAll()
        assertEquals(16, products.size)
    }
}
```

#### GraphQL Tests
GraphQL endpoints can be tested using Ktor's `testApplication` function:

```kotlin
class ProductQueryTest {
    @Test
    fun testProductsQuery() = testApplication {
        application {
            module()
        }
        
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
- `src/main/kotlin/dev/aoriani/ecomm/` - Main application code
  - `Application.kt` - Application entry point and configuration
  - `Routing.kt` - HTTP routing configuration
  - `graphql/` - GraphQL-related code
    - `models/` - Data models
    - `queries/` - GraphQL query resolvers
    - `repository/` - Data repositories

### GraphQL Development
The project uses the ExpediaGroup GraphQL Kotlin library:

1. Define data models in `graphql/models/` with `@GraphQLDescription` annotations
2. Create query resolvers in `graphql/queries/` that implement the `Query` interface
3. Access the GraphiQL interface at `/graphiql` when running the application

Example of adding a new GraphQL query:
```kotlin
@GraphQLDescription("Root entry point for category-related queries")
class CategoryQuery : Query {
    @GraphQLDescription("Retrieve all categories")
    fun categories(): List<Category> = CategoryRepository.getAll()
}
```

Then register the query in `Application.kt`:
```kotlin
install(GraphQL) {
    schema {
        packages = listOf("dev.aoriani.ecomm.graphql.models")
        queries = listOf(ProductQuery(), CategoryQuery())
    }
}
```

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