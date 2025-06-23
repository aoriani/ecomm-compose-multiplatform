
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.expediagroup.graphql)
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
//    implementation(libs.ktor.server.host.common)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.cors)
    implementation(ktorLibs.server.config.yaml)
    implementation(libs.logback.classic)
    implementation(libs.expedia.ktor.server.graphql)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test.junit)
}
