dependencyResolutionManagement {

    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:3.2.0")
        }
    }
}

rootProject.name = "ecomm"
