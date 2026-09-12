pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.xiaolin.build-autolibs") version "0.0.1"
}

autolibsConfig {
    libFiles.set(
        listOf(
            layout.rootDirectory.file("./gradle/libs.versions.toml"),
            layout.rootDirectory.file("./gradle/libs.versions.json")
        )
    )
}

rootProject.name = "build-logic"

include(":conventions")
