pluginManagement {
    fun RepositoryHandler.setupRepositories() {
        jcenter()
        google()
        if (this == repositories) {
            gradlePluginPortal()
        }
    }
    repositories.setupRepositories()
    gradle.allprojects { repositories.setupRepositories() }

    plugins {
        kotlin("jvm").version("1.4.0-rc")
        id("com.android.application").version("4.1.0-beta05")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}

rootProject.name = "orbiters-game"

include(":core")
include(":desktop")
include(":android")
