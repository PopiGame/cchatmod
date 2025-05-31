pluginManagement {
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "cchatmod"