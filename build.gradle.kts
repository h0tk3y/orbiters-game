plugins {
    kotlin("jvm").apply(false)
    id("com.android.application").version("4.1.0-beta05").apply(false)
}
buildscript {
    val kotlin_version by extra("1.3.61")
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.android.tools.build:gradle:4.1.0-beta05")
    }
}

ext["gdxVersion"] = "1.9.11"
ext["ktxVersion"] = "1.9.11-b1"