package com.h0tk3y.orbiters

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

enum class OrbitalUnitKind(val size: Float) {
    SHIP(5f);
}

class OrbitalUnit(val kind: OrbitalUnitKind) {
    val position: Vector2 = Vector2(0f, 0f)
    val velocity: Vector2 = Vector2(0f, 0f)
    var orientation: Float = 0f

    var x: Float
        get() = position.x
        set(value) {
            position.x = value
        }

    var y: Float
        get() = position.y
        set(value) {
            position.y = value
        }

    var vx: Float
        get() = velocity.x
        set(value) {
            velocity.x = value
        }

    var vy: Float
        get() = velocity.y
        set(value) {
            velocity.y = value
        }
}

enum class CentralBodyKind(val fill: Color, val radius: Float) {
    MOON(Color.GRAY, 80f)
}

class CentralBody(val kind: CentralBodyKind) {
    val radius: Float
        get() = kind.radius
}