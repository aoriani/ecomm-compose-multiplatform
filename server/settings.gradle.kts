dependencyResolutionManagement {

    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:3.5.1")
        }
    }
}

rootProject.name = "ecomm"
