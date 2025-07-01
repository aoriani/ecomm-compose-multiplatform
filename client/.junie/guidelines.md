# eCommerceApp Development Guidelines

This document provides essential information for developers working on the eCommerceApp project. It includes build/configuration instructions, testing information, and additional development details specific to this project.

## Build/Configuration Instructions

### Project Overview

This is a Kotlin Multiplatform project using Compose Multiplatform for UI. It targets multiple platforms:
- Android
- iOS
- Desktop (JVM)
- Web (WebAssembly)

### Prerequisites

- JDK 11 or higher
- Android SDK
- Xcode (for iOS development)
- Node.js and npm (for WebAssembly target)

### Building the Project

#### From Command Line

```bash
# Build all platforms
./gradlew build

# Build specific platform
./gradlew composeApp:androidBuild  # Android
./gradlew composeApp:jvmJar        # Desktop
./gradlew composeApp:iosArm64XCFramework  # iOS
./gradlew composeApp:wasmJsBrowserProductionWebpack  # WebAssembly
```

#### Running the Application

```bash
# Run on Android
./gradlew composeApp:installDebug

# Run on Desktop
./gradlew composeApp:run

# Run on iOS (requires opening the Xcode project)
open iosApp/iosApp.xcodeproj

# Run on Browser
./gradlew composeApp:wasmJsBrowserDevelopmentRun
```

### Configuration

The project uses the Gradle Version Catalog for dependency management, located at `gradle/libs.versions.toml`. Key configurations:

- Android configuration is in `composeApp/build.gradle.kts`
- iOS configuration is in `iosApp/Configuration/Config.xcconfig`
- GraphQL configuration is in the Apollo section of `composeApp/build.gradle.kts`

## Testing Information

### Test Structure

The project follows Kotlin Multiplatform testing conventions:

- `commonTest`: Tests that run on all platforms
- `androidTest`: Android-specific tests
- `jvmTest`: JVM/Desktop-specific tests
- `iosTest`: iOS-specific tests
- `wasmJsTest`: WebAssembly-specific tests

### Writing Tests

Tests are written using the Kotlin Test library. Here's an example of a simple test:

```kotlin
class CartItemTest {

    @Test
    fun testTotalPriceCalculation() {
        // Arrange
        val cartItem = CartItem(
            id = "test-id",
            name = "Test Product",
            price = 10.0,
            quantity = 2
        )

        // Act
        val totalPrice = cartItem.totalPrice

        // Assert
        assertEquals(20.0, totalPrice, "Total price should be price * quantity")
    }
}
```

Note: In actual code, you would need to include the appropriate imports:
```
import kotlin.test.Test
import kotlin.test.assertEquals
```

### Running Tests

```bash
# Run all tests
./gradlew allTests

# Run tests for specific platform
./gradlew jvmTest
./gradlew androidTest
./gradlew iosTest
./gradlew wasmJsTest

# Run a specific test class
./gradlew jvmTest --tests "io.aoriani.ecomm.data.model.CartItemTest"
```

### Test Dependencies

Test dependencies are configured in the `build.gradle.kts` file:

```kotlin
commonTest.dependencies {
    implementation(libs.kotlin.test)
}

jvmTest.dependencies {
    implementation(libs.kotlin.test.junit)
}
```

Note: JUnit is not compatible with WebAssembly, so we only add it to JVM and Android test targets.

## Additional Development Information

### Project Architecture

The project follows a clean architecture approach with the following layers:

- **UI Layer**: Compose UI components in `screens` packages
- **ViewModel Layer**: ViewModels that handle UI logic and state
- **Repository Layer**: Repositories that abstract data sources
- **Data Layer**: Data models and network clients

### Code Style

- Follow Kotlin coding conventions
- Use meaningful names for variables, functions, and classes
- Write comments for complex logic
- Include KDoc for public APIs

### GraphQL Integration

The project uses Apollo Kotlin for GraphQL integration:

- GraphQL schema is in `composeApp/src/commonMain/graphql/aoriani/schema.graphqls`
- Queries are in `composeApp/src/commonMain/graphql/aoriani/queries/`
- Apollo generates Kotlin code for GraphQL operations

### Navigation

The project uses Compose Navigation:

- Routes are defined in `io.aoriani.ecomm.ui.navigation.Routes`
- Navigation graph is in `io.aoriani.ecomm.ui.navigation.Navigation`

### Dependency Injection

The project uses Koin for dependency injection:

- Modules are defined in `io.aoriani.ecomm.di.DI.kt`.
- ViewModels and other classes receive dependencies through constructor injection, managed by Koin.

### Logging

The project uses Kermit for logging:

```kotlin
private val logger = Logger.withTag("YourClassName")
logger.d { "Debug message" }
logger.i { "Info message" }
logger.w { "Warning message" }
logger.e { "Error message" }
```

### Hot Reload

The project supports Compose Hot Reload for faster development:

```bash
./gradlew :composeApp:enableComposeCompilerReports
```

This will generate reports in `build/compose_compiler` that can help optimize Compose performance.
