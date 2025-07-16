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
./gradlew :composeApp:assembleDebug  # Android (Debug build)
# For a release build, use: ./gradlew :composeApp:assembleRelease
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
- SQLDelight configuration is in the SQLDelight section of `composeApp/build.gradle.kts`

### `build.gradle.kts`

The main build script for the `composeApp` module, located at `composeApp/build.gradle.kts`, is where all the magic happens. Here’s a breakdown of what it does:

- **Plugins**: It applies essential plugins for Android, Kotlin Multiplatform, Compose, Apollo (GraphQL), and SQLDelight.
- **Kotlin Configuration**: It sets up the different targets for the multiplatform project:
  - `androidTarget`: Configures Android-specific settings, including compiler options and dependencies.
  - `iosTarget`: Sets up iOS targets (X64, Arm64, Simulator) and creates the necessary framework.
  - `jvm("desktop")`: Configures the desktop target.
  - `wasmJs`: Configures the WebAssembly target, including webpack settings and NPM dependencies.
- **Source Sets**: It defines the dependencies for each source set (`commonMain`, `androidMain`, `iosMain`, etc.), ensuring that each platform gets the right libraries.
- **Android Configuration**: It includes standard Android settings like `namespace`, `compileSdk`, `defaultConfig`, and `buildTypes`.
- **Apollo**: It configures the Apollo service, pointing to the GraphQL endpoint and setting up scalar mappings.
- **SQLDelight**: It configures the SQLDelight database, setting the package name and enabling async code generation.
- **Compose Desktop**: It sets up the desktop application, defining the main class and native distribution formats.

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
# Run all tests (executes tests for all targets)
./gradlew check
# Alternatively, to run tests for the default target (often JVM):
# ./gradlew test

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
- **Data Layer**: Data models, network clients, and local database access

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

### Local Database with SQLDelight

The project uses SQLDelight for local database caching:

- Database schema and queries are defined in `.sq` files located in `composeApp/src/commonMain/sqldelight/`
- SQLDelight generates type-safe Kotlin APIs for interacting with the database.
- Platform-specific drivers for SQLite are provided in the respective `...Main` source sets.

### Navigation

The project uses [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation) for navigating between screens.

- **Routes**: Defined in `io.aoriani.ecomm.ui.navigation.Routes`.
- **Navigation Graph**: The navigation graph is set up in `io.aoriani.ecomm.ui.navigation.Navigation`, which defines all the screens and their transitions.

### Dependency Injection

The project uses [Koin](https://insert-koin.io/) for dependency injection, which helps in managing dependencies in a structured and testable way.

- **Modules**: Dependencies are organized into modules in `io.aoriani.ecomm.di.Deps`.
- **Initialization**: The Koin container is initialized in the `App` composable, making dependencies available throughout the application.
- **Injection**:
  - In Compose components, dependencies are injected using `koinInject()`.
  - ViewModels receive their dependencies through factory constructors, ensuring they are properly instantiated by Koin.

### Logging

The project uses [Kermit](https://kermit.touchlab.co/) for logging, which provides a flexible and powerful logging solution for Kotlin Multiplatform.

```kotlin
private val logger = Logger.withTag("YourClassName")
logger.d { "Debug message" }
logger.i { "Info message" }
logger.w { "Warning message" }
logger.e { "Error message" }
```

### Hot Reload

The project supports [Compose Hot Reload](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-hot-reload.html) for faster development cycles. To enable it, use the following command:

```bash
./gradlew -Pcompose.hotreload=true :composeApp:desktop:run
```

This will start the desktop application with Hot Reload enabled, allowing you to see changes in real-time without restarting the app.

### WebAssembly Target (WasmJs)

The project supports WebAssembly through the `wasmJs` target, allowing the application to run in modern web browsers.

- **Configuration**: The `wasmJs` target is configured in `composeApp/build.gradle.kts`, including the browser and webpack settings.
- **NPM Dependencies**: The project uses `npm` to manage JavaScript dependencies for the Wasm target, such as `sql.js` for the web worker.
- **SQLDelight for Web**: The project uses a `web-worker-driver` for SQLDelight to run the database in a separate thread, ensuring the UI remains responsive.
