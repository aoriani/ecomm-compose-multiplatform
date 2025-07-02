[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/aoriani/ecomm-compose-multiplatform)

# E-Commerce Full-Stack Application

Welcome to the E-Commerce Full-Stack Application! This project demonstrates a modern approach to building a complete e-commerce platform using Kotlin for both the backend and the client-side applications.

## Table of Contents

- [Overview](#overview)
- [Documentation](#documentation)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Setup](#setup)
- [Running the Application](#running-the-application)
  - [Running the Backend Server](#running-the-backend-server)
  - [Running the Client Applications](#running-the-client-applications)
- [Backend API (GraphQL)](#backend-api-graphql)
- [Client Configuration](#client-configuration)
- [Building and Deployment](#building-and-deployment)
  - [Server](#server)
  - [Client](#client)
  - [Important Considerations for Deployment](#important-considerations-for-deployment)
- [Contributing](#contributing)
  - [Coding Conventions](#coding-conventions)
- [License](#license)

## Overview

The project consists of two main components:

*   **Backend Server:** A robust backend built with [Ktor](https://ktor.io/), a framework for building asynchronous servers and clients in Kotlin. It exposes a GraphQL API for managing products, orders, and other e-commerce functionalities.
*   **Client Application:** A [Kotlin Multiplatform](https://kotlinlang.org/lp/multiplatform/) application targeting Android, iOS, Desktop (Windows, macOS, Linux), and Web (via Kotlin/Wasm). The UI is built using [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/), allowing for a shared codebase across platforms while delivering native performance and look-and-feel.

## Documentation

Comprehensive documentation for this project, including detailed explanations of the architecture, modules, and setup, is available on [DeepWiki](https://deepwiki.com/aoriani/ecomm-compose-multiplatform).

## Technologies Used

*   **Backend:**
    *   Kotlin
    *   Ktor (Web framework)
    *   GraphQL Kotlin (for GraphQL API)
    *   Gradle (Build tool)
*   **Client:**
    *   Kotlin Multiplatform
    *   Compose Multiplatform (for UI)
    *   Kotlin/Wasm (for Web target)
    *   Gradle (Build tool)
*   **Database:** (Specify if known, otherwise omit or state as TBD)
*   **Other Tools:**
    *   Docker (for containerization of the server)

## Project Structure

The repository is organized into two main directories:

*   `./client/`: Contains the Kotlin Multiplatform client application.
    *   `composeApp/`: Shared UI and business logic written in Compose Multiplatform.
        *   `commonMain/`: Code common to all client targets (Android, iOS, Desktop, Web).
        *   `androidMain/`, `iosMain/`, `desktopMain/`, `wasmJsMain/`: Platform-specific implementations and entry points.
    *   `iosApp/`: Xcode project for the iOS application.
    *   See `client/README.md` for more detailed information on the client project structure.
*   `./server/`: Contains the Ktor backend application.
    *   `src/`: Source code for the server, including GraphQL schema, resolvers, and Ktor configuration.
    *   `Dockerfile`: For building a Docker image of the server.
    *   See `server/README.md` for more details on the server project.

## Getting Started

To get started with developing and running this project, you'll need to set up your environment with the following prerequisites.

### Prerequisites

*   **Java Development Kit (JDK):** Version 17 or higher is recommended for both the client and server. You can download it from [Oracle](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) or use an alternative distribution like [OpenJDK](https://openjdk.java.net/).
*   **IntelliJ IDEA:** The recommended IDE for Kotlin development. You can use the Community or Ultimate edition. Download it from [JetBrains](https://www.jetbrains.com/idea/download/).
*   **Android Studio:** Required for Android development. It can be downloaded from the [Android Developer website](https://developer.android.com/studio). Ensure you have the Android SDK and emulators set up.
*   **Xcode:** Required for iOS development (macOS only). Download it from the [Mac App Store](https://apps.apple.com/us/app/xcode/id497799835).
*   **Node.js:** Required for the Kotlin/Wasm web target if you plan to use JavaScript tooling (e.g., `yarn` or `npm` for managing JS dependencies, though the current project uses `kotlin-js-store`). Version 18.x or higher is recommended. Download from [nodejs.org](https://nodejs.org/).
*   **Docker:** (Optional, but recommended for running the server in a containerized environment). Download from [Docker's website](https://www.docker.com/products/docker-desktop).

### Setup

1.  **Clone the repository:**
    ```bash
    git clone your-repository-url
    cd your-repository-directory
    ```
    (Replace `your-repository-url` and `your-repository-directory` with the actual URL and the name of the directory created by git clone).

2.  **Open the project:**
    *   For the **server**, open the `./server` directory as a project in IntelliJ IDEA.
    *   For the **client**, open the `./client` directory as a project in IntelliJ IDEA or Android Studio.
3.  **Install Dependencies:**
    *   Gradle will automatically download most dependencies when you build or sync the project in your IDE.
    *   The `client/kotlin-js-store/yarn.lock` file indicates that Yarn is used for managing JavaScript dependencies for the Wasm target, typically handled via Gradle tasks. If you encounter issues with Wasm dependencies, ensure Node.js and Yarn are correctly installed and in your PATH.
    *   For iOS, open `client/iosApp/iosApp.xcodeproj` in Xcode. It might prompt you to install additional tools or dependencies.
4.  **Configure JDK:** Ensure your IDE is configured to use the correct JDK version (17+) for both the client and server modules.

## Running the Application

Once your environment is set up, you can run the backend server and the client applications.

### Running the Backend Server

Navigate to the `server` directory:
```bash
cd server/
```

You can run the server using Gradle:
```bash
./gradlew run
```
Or, if you have Docker installed, you can build and run the Docker image:
```bash
./gradlew buildImage
docker run -p 8080:8080 your-docker-image-name:tag # Replace with the actual image name/tag (see server/README.md)
```
(Refer to `server/README.md` for more details on image naming and other build tasks like `runDocker`).

The server will typically start on `http://0.0.0.0:8080`.

### Running the Client Applications

Navigate to the `client` directory for client-related commands:
```bash
cd client/
```

*   **Android:**
    *   Open the `./client` project in Android Studio.
    *   Select the `composeApp` run configuration.
    *   Choose an Android emulator or a connected physical device.
    *   Click the "Run" button.

*   **iOS (macOS only):**
    *   Open `client/iosApp/iosApp.xcodeproj` in Xcode.
    *   Select a simulator or a connected iOS device.
    *   Click the "Run" (play) button.

*   **Desktop (Windows, macOS, Linux):**
    *   In IntelliJ IDEA (with the `./client` project open), find the Gradle tasks under `client` > `Tasks` > `compose desktop`.
    *   Run the `run` or `desktopRun` Gradle task (e.g., `:composeApp:run` or `:composeApp:desktopRun`).
    *   Alternatively, from the command line in the `client/` directory:
        ```bash
        ./gradlew :composeApp:run
        ```
        (The exact task name might vary slightly, check your Gradle panel if unsure).

*   **Web (Kotlin/Wasm):**
    *   From the command line in the `client/` directory:
        ```bash
        ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
        ```
    *   This will start a development server. Access the web application in your browser, typically at `http://localhost:8080/` (the port might vary, check the console output).

**Note:** Ensure the backend server is running and accessible by the client applications. You might need to configure the backend URL in the client (see "Client Configuration" section).

## Backend API (GraphQL)

The backend server provides a GraphQL API for all e-commerce operations.

*   **GraphQL Endpoint:** `http://<server-host>:<port>/graphql`
    *   Default when running server locally: `http://localhost:8080/graphql`
*   **GraphiQL Console:** `http://<server-host>:<port>/graphiql`
    *   Default when running server locally: `http://localhost:8080/graphiql`
    *   This is an in-browser IDE for exploring the GraphQL schema, writing queries, and testing mutations.

For detailed information on the available queries, mutations, and types, please refer to the GraphiQL console or the `server/README.md` which may contain more specific examples.

An example query to fetch all products' names and prices:
```graphql
query {
  products {
    name
    price
  }
}
```

## Client Configuration

The client application needs to connect to the backend GraphQL API to function.

*   **Backend URL Configuration:** The GraphQL server URL is currently hardcoded in `client/composeApp/src/commonMain/kotlin/io/aoriani/ecomm/di/Deps.kt`.
*   **Development:** When running the server locally, the client should point to `http://localhost:8080/graphql` (or `http://10.0.2.2:8080/graphql` for Android emulators accessing the host machine's localhost). You may need to modify this URL in the `Deps.kt` file if your local server runs on a different port or address.
*   **Production:** For a production build, this URL must be updated to point to the deployed backend service.

Future improvements should make this URL configurable through build parameters or environment settings rather than hardcoding. Please refer to `client/README.md` for any updates on this topic.

## Building and Deployment

This section provides guidance on building the application components for production and general deployment strategies.

### Server

1.  **Build Executable JAR:**
    Navigate to the `server/` directory and run:
    ```bash
    ./gradlew buildFatJar
    ```
    This will create an executable JAR file (e.g., `server-all.jar`) in `server/build/libs/`. This JAR contains all dependencies and can be run using `java -jar server/build/libs/<jar-file-name>.jar`.

2.  **Build Docker Image:**
    A `Dockerfile` is provided in the `server/` directory. To build the image:
    ```bash
    cd server/
    ./gradlew buildImage
    # Or, using Docker directly after building the fat JAR:
    # docker build -t your-docker-image-name:latest .
    ```
    (Replace `your-docker-image-name` with your desired image name. Refer to `server/README.md` for more details on Docker image building and publishing.)

3.  **Deployment:**
    *   The executable JAR can be deployed to any environment with a Java runtime.
    *   The Docker image can be deployed to container orchestration platforms (like Kubernetes, Docker Swarm) or any service that supports Docker containers (e.g., AWS ECS, Google Cloud Run).
    *   Ensure to configure necessary environment variables for production (e.g., database connections, external service URLs, CORS settings if not handled via a gateway). The `server/README.md` mentions that CORS is currently hardcoded, which should be addressed for production.

### Client

Building the client for production varies by platform:

*   **Android:**
    *   In Android Studio, use the "Build" > "Generate Signed Bundle / APK..." option.
    *   Follow the standard Android app publishing process for the Google Play Store.
    *   Ensure you configure release signing keys.

*   **iOS:**
    *   In Xcode, select "Product" > "Archive".
    *   Follow the standard iOS app submission process for the Apple App Store.
    *   Ensure you have the correct provisioning profiles and certificates.

*   **Desktop (Windows, macOS, Linux):**
    *   Run the appropriate Gradle tasks to create distributable packages. For example, in the `client` directory:
        ```bash
        ./gradlew :composeApp:packageDistributionForCurrentOS # Or specific tasks like packageDmg, packageMsi, packageDeb
        ```
    *   The output will typically be in `client/composeApp/build/compose/binaries/main/dist/`.
    *   These packages can then be distributed to users.

*   **Web (Kotlin/Wasm):**
    *   Run the Gradle task to build the production Wasm bundle:
        ```bash
        cd client
        ./gradlew :composeApp:wasmJsBrowserDistribution
        ```
    *   The output will be in `client/composeApp/build/dist/wasmJs/productionExecutable/`.
    *   These static files (HTML, JS, Wasm) can be deployed to any static web hosting service (e.g., GitHub Pages, Netlify, AWS S3 with CloudFront).

**Important Considerations for Deployment:**

*   **Backend URL:** Ensure all client applications are built with the correct production backend URL.
*   **API Keys & Secrets:** Manage any API keys or sensitive configuration securely, preferably through environment variables or a secrets management system, not hardcoded in the source.
*   **CORS:** Properly configure Cross-Origin Resource Sharing (CORS) on the server for web clients.
*   **HTTPS:** Use HTTPS for all communication between client and server in production.

## Contributing

Contributions are welcome! If you'd like to contribute to this project, please follow these general guidelines:

1.  **Fork the repository.**
2.  **Create a new branch** for your feature or bug fix:
    ```bash
    git checkout -b feature/your-feature-name
    ```
    or
    ```bash
    git checkout -b fix/issue-description
    ```
3.  **Make your changes.** Adhere to the existing coding style and conventions.
    *   Write clear, concise, and well-documented code.
    *   Ensure your changes do not break existing functionality.
    *   Consider adding tests for new features or bug fixes.
4.  **Test your changes thoroughly.**
5.  **Commit your changes** with a clear and descriptive commit message:
    ```bash
    git commit -m "feat: Implement X feature" -m "Detailed description of changes."
    ```
    (Consider using [Conventional Commits](https://www.conventionalcommits.org/) for commit messages).
6.  **Push your branch** to your fork:
    ```bash
    git push origin feature/your-feature-name
    ```
7.  **Open a Pull Request (PR)** against the `main` branch of the original repository.
    *   Provide a clear title and description for your PR, explaining the changes and the problem it solves.
    *   Link any relevant issues.

If you're planning a larger contribution, it's a good idea to open an issue first to discuss your ideas.

### Coding Conventions

*   Follow standard Kotlin coding conventions (see [Kotlin Official Style Guide](https://kotlinlang.org/docs/coding-conventions.html)).
*   Use Detekt for static analysis (if configured in the project).
*   Maintain consistency with the existing codebase.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details.
