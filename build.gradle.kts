plugins {
    id("java")
    id("fabric-loom") version "1.10-SNAPSHOT"
}

group = "net.cchat.cchatmod"
version = "1.2.0"

repositories {
    maven("https://maven.parchmentmc.org/")
}

dependencies {
    minecraft(libs.minecraft)

    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21.4:2025.03.23@zip")
    })

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
    options.encoding = "UTF-8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    processResources {
        filteringCharset = "UTF-8"

        inputs.property("version", project.version)
        inputs.property("minecraft_version", libs.versions.minecraft.get())
        inputs.property("loader_version", libs.versions.fabric.loader.get())
        inputs.property("fabric_version", libs.versions.fabric.api.get())

        filesMatching("fabric.mod.json") {
            expand(mutableMapOf(
                "version" to project.version,
                "minecraft_version" to libs.versions.minecraft.get(),
                "loader_version" to libs.versions.fabric.loader.get(),
                "fabric_version" to libs.versions.fabric.api.get()
            ))
        }
    }
}