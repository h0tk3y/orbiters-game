pluginManagement {
    fun RepositoryHandler.setupRepositories() {
        jcenter()
        if (this == repositories) {
            gradlePluginPortal()
        }
    }
    repositories.setupRepositories()
    gradle.allprojects { repositories.setupRepositories() }

    plugins {
        kotlin("jvm").version("1.4.0-rc")
    }
}

rootProject.name = "orbiters-game"

include(":core")
include(":desktop")