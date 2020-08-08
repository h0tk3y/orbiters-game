plugins {
    kotlin("jvm")
    application
}

val assetsDir = rootDir.resolve("assets")
kotlin.sourceSets["main"].resources.srcDir(assetsDir)

dependencies {
    implementation(project(":core"))

    val gdxVersion: String by project
    api("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
    api("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    api("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop")
}

application.mainClassName = "com.h0tk3y.orbiters.desktop.DesktopLauncher"

val run: JavaExec by tasks.withType<JavaExec>()
run.workingDir = assetsDir