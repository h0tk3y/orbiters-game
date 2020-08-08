package com.h0tk3y.orbiters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

fun handleSwitchingShips(state: GameState) = with(state) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
        selectedShipIndex = (selectedShipIndex + 1) % ships.size
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
        selectedShipIndex--
        if (selectedShipIndex < 0) selectedShipIndex += ships.size
    }
}

fun handlePauseKeyPress(state: GameState) = with(state) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
        isPaused = !isPaused
    }
}

fun handleShipMovementKeys(orbitalUnit: OrbitalUnit, delta: Float) {
    if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
        val direction = Vector2().apply {
            set(orbitalUnit.velocity).setLength(0.5f * delta).rotateRad(orbitalUnit.orientation)
        }
        orbitalUnit.velocity.add(direction)
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
        orbitalUnit.orientation = 0f
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
        orbitalUnit.orientation = MathUtils.PI
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
        orbitalUnit.orientation = MathUtils.PI / 2
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
        orbitalUnit.orientation = 3 * MathUtils.PI / 2
    }
    if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
        val sign = if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) 1 else -1
        orbitalUnit.orientation = orbitalUnit.orientation + sign * 4f * delta
    }
}

class GameInputAdapter(
    private val camera: OrthographicCamera,
    private val state: GameState
) : InputAdapter() {
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val x = Gdx.input.deltaX.toFloat()
        val y = Gdx.input.deltaY.toFloat()

        camera.translate(-x * camera.zoom, y * camera.zoom)
        return true
    }

    private var sx: Int = 0
    private var sy: Int = 0

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (Vector2(sx.toFloat(), sy.toFloat()).dst(Vector2(screenX.toFloat(), screenY.toFloat())) >= 5f)
            return true

        val p = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val p2 = Vector2(p.x, p.y)
        val nearestShip = state.ships.minByOrNull { it.position.dst2(p2) }
        state.targetShipIndex = state.ships.indexOf(nearestShip)
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        sx = screenX
        sy = screenY
        return true
    }

    override fun scrolled(amount: Int): Boolean {
        val dz = amount / 15f
        if (dz > 0 && camera.zoom < 5.0 || dz < 0 && camera.zoom > 0.1) {
            camera.zoom += dz
        }
        return true
    }
}