package com.h0tk3y.orbiters

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import kotlin.math.sqrt

class GameState(width: Float, height: Float) {
    val centralBody = CentralBody(CentralBodyKind.MOON)
    val center = Vector2(width / 2, height / 2)

    val ships = Array<OrbitalUnit>().apply {
        ordered = false
        addAll(*initShips(centralBody, center, 10).toTypedArray())
    }

    var selectedShipIndex = 0
    var targetShipIndex = 1

    val selectedShip: OrbitalUnit
        get() = ships[selectedShipIndex]

    var isPaused = false
}

fun initShips(centralBody: CentralBody, center: Vector2, nShips: Int) = (1..nShips).map { i ->
    val totalShift = 150f
    val shift = totalShift / nShips
    OrbitalUnit(OrbitalUnitKind.SHIP).apply {
        x = center.x
        val dy = centralBody.radius * 3 + i * shift
        y = center.y + dy
        vx = sqrt(gConst / dy)
        vy = 0f
    }
}
