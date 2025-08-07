
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.expediagroup.graphql)
    alias(libs.plugins.kotlinx.serialization)
}

group = "dev.aoriani"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

graphql {
    schema {
        packages = listOf("dev.aoriani.ecomm.graphql")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.callLogging)
    implementation(ktorLibs.server.conditionalHeaders)
    implementation(ktorLibs.server.di)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.cors)
    implementation(ktorLibs.server.compression)
    implementation(ktorLibs.server.config.yaml)
    implementation(libs.logback.classic)
    implementation(libs.expedia.ktor.server.graphql)
    implementation(libs.expedia.hooks.provider)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.mcp.kotlin.sdk)
    implementation(libs.h2.jdbc)
    implementation(libs.sqlite.jdbc)
    implementation(libs.graphql.java.extended.scalars)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
