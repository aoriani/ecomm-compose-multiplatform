# E-Commerce Kotlin Multiplatform Client

This is the Kotlin Multiplatform client for an e-commerce application. It targets Android, iOS, Desktop, and Web (using Kotlin/Wasm).

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [GitHub](https://github.com/JetBrains/compose-multiplatform/issues).

## Configuration

This client application needs to connect to a GraphQL backend to fetch and display e-commerce data.

The GraphQL server URL is currently hardcoded in `client/composeApp/src/commonMain/kotlin/io/aoriani/ecomm/di/Deps.kt`.
For development, you might need to change this URL to point to your local server instance. In a production build, this URL should point to the deployed backend. Future improvements should make this URL configurable through build parameters or environment settings.

## Running the Application

Here's how to run the application on different platforms:

*   **Web (Wasm):** You can open the web application by running the `:composeApp:wasmJsBrowserDevelopmentRun` Gradle task.
*   **Android:** To run on Android, open the project in Android Studio and run the `composeApp` configuration on an emulator or connected device.
*   **iOS:** To run on iOS, open `client/iosApp/iosApp.xcodeproj` in Xcode and run the app on a simulator or connected device.
*   **Desktop:** To run on Desktop, execute the Gradle task `:composeApp:run` (or `:composeApp:desktopRun`, `:composeApp:runDesktop` - please check your available tasks).