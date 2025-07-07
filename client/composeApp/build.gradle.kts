import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.apolloKotlin)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.sqlDelight)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets.all {
        languageSettings.enableLanguageFeature("ExplicitBackingFields")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.sqldelight.android.driver)
        }
        commonMain.dependencies {
//            implementation(libs.kermit.koin)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.apollo.adapters.core)
            implementation(libs.apollo.kotlin)
            implementation(libs.apollo.ktor)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.kermit.main)
            implementation(libs.sqldelight.coroutines.extension)
            implementation(libs.koin.compose)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.ktor.client.java)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.sqldelight.jvm.driver)
        }
        val desktopTest by getting {
            dependencies {
                implementation(libs.kotlin.test.junit)
            }
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(npm("big.js", "7.0.1"))
            implementation(libs.sqldelight.webworker.driver)
//            implementation(libs.sqldelight.runtime.wasmjs)
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
            implementation(npm("sql.js", "1.12.0")) // Ensure this version is compatible or newer
            implementation(devNpm("copy-webpack-plugin", "9.1.0")) // For copying the .wasm file
            //TODO
            // Reference for sqldelight wasm setup
            // https://github.com/DrUlysses/Kristine/blob/main/composeApp/build.gradle.kts
            // https://kotlinlang.slack.com/archives/C5HT9AL7Q/p1748911587936999
            // https://kotlinlang.slack.com/archives/C5HT9AL7Q/p1749181253399889
            // https://github.com/dellisd/sqldelight-sqlite-wasm
            // https://kotlinlang.slack.com/archives/C5HT9AL7Q/p1735354252983189
            // https://github.com/sqldelight/sqldelight/pull/5534
            // It seems that DB will be in memory, which is useless for my purpose
            // Research suggest using IndexDB or OPFS (https://webkit.org/blog/12257/the-file-system-access-api-with-origin-private-file-system/)
            // https://g.co/gemini/share/997e74da6816 -> My personal gemini search on the topic
        }
    }
}

android {
    namespace = "io.aoriani.ecomm"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.aoriani.ecomm"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

apollo {
    service("aoriani") {
        packageName.set("io.aoriani.ecomm.data.graphql")
        introspection {
            endpointUrl.set("https://aoriani.dev/graphql")
        }
        mapScalar(
            "BigDecimal",
            "com.apollographql.adapter.core.BigDecimal",
            "com.apollographql.adapter.core.BigDecimalAdapter"
        )
    }
}

sqldelight {
    databases {
        create("ProductDatabase") {
            packageName.set("io.aoriani.ecomm.data.repositories.db")
            generateAsync.set(true)
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.aoriani.ecomm.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.aoriani.ecomm"
            packageVersion = "1.0.0"
        }
    }
}