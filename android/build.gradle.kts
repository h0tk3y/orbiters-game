plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 29
    buildToolsVersion = "29.0.3"

    defaultConfig {
        applicationId = "com.h0tk3y.orbiters.android"
        minSdkVersion(26)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        val release by getting {
            minifyEnabled(false)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlinOptions.jvmTarget = "1.8"

    sourceSets.getByName("main") {
        assets.srcDir(rootDir.resolve("assets"))
        jniLibs.srcDir("libs")
    }
}

val natives: Configuration by configurations.creating {
    isCanBeConsumed = false
}

val nativePlatforms = listOf("armeabi", "armeabi-v7a", "arm64-v8a", "x86", "x86_64")

dependencies {
    implementation(project(":core"))

    val gdxVersion: String by project
    api("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    nativePlatforms.forEach {
        natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-$it")
    }
    api("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
    nativePlatforms.forEach {
        natives("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-$it")
    }

    implementation("com.android.support:appcompat-v7:28.0.0")

    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")

    testImplementation("junit:junit:4.13")
}

// called every time gradle gets executed, takes the native dependencies of
// the natives configuration, and extracts them to the proper libs/ folders
// so they get packed with the APK.
val copyAndroidNatives by tasks.registering {
    outputs.dir("libs")

    doFirst {
        file("libs/armeabi/").mkdirs()
        file("libs/armeabi-v7a/").mkdirs()
        file("libs/arm64-v8a/").mkdirs()
        file("libs/x86_64/").mkdirs()
        file("libs/x86/").mkdirs()

        configurations["natives"].files.forEach { jar ->
            val outputDir = nativePlatforms
                .find { jar.name.endsWith("$it.jar") }
                ?.let { projectDir.resolve("libs/$it").also(File::mkdirs) }

            if (outputDir != null) {
                println("expanding $jar to $outputDir")
                val files = zipTree(jar).toList()
                files.forEach { it.copyTo(outputDir.resolve(it.name), overwrite = true) }
            }
        }
    }
}

tasks.matching { it.name.contains("package") }.all {
    dependsOn(copyAndroidNatives)
}