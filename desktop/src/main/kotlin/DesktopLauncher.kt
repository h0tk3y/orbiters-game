package com.h0tk3y.orbiters.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.h0tk3y.orbiters.OrbitersGame

object DesktopLauncher {
    @JvmStatic fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration().apply {
            forceExit = true
            height = 1080
            width = 1920
        }
        LwjglApplication(OrbitersGame(), config)
    }
}