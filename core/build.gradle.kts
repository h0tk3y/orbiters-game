plugins {
    kotlin("jvm")
}

kotlin.sourceSets.all {
    languageSettings.enableLanguageFeature("InlineClasses")
}

dependencies {
    val gdxVersion: String by project
    val ktxVersion: String by project
    api("com.badlogicgames.gdx:gdx:$gdxVersion")
    api("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
    api("io.github.libktx:ktx-app:$ktxVersion")
    api("io.github.libktx:ktx-graphics:$ktxVersion")
}