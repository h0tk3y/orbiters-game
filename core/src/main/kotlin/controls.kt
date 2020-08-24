package com.h0tk3y.orbiters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.ui.Button

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
        val throttleModifier = if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) 0.05f else 0.5f
        val direction = Vector2().apply {
            set(orbitalUnit.velocity).setLength(throttleModifier * delta).rotateRad(orbitalUnit.orientation)
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

private class ZoomGestureListener(
    private val camera: OrthographicCamera
) : GestureDetector.GestureAdapter() {
    override fun zoom(initialDistance: Float, distance: Float): Boolean {
        val dz = -(distance - initialDistance) / 2000
        updateCameraZoom(camera, dz)
        return true
    }
}

private fun updateCameraZoom(camera: OrthographicCamera, dz: Float) {
    if (dz > 0 && camera.zoom < 5.0 || dz < 0 && camera.zoom > 0.3) {
        camera.zoom += dz
    }
}

class GameInputAdapter(
    private val camera: OrthographicCamera,
    private val state: GameState
) : InputAdapter() {
    private val gestures = GestureDetector(ZoomGestureListener(camera))

    var lastTouchDown = Vector3()

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (gestures.touchDragged(screenX, screenY, pointer)) {
            return true
        }

        val x = Gdx.input.deltaX.toFloat()
        val y = Gdx.input.deltaY.toFloat()

        camera.translate(-x * camera.zoom / 2, y * camera.zoom / 2)
        return true
    }

    private var sx: Int = 0
    private var sy: Int = 0

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (gestures.touchUp(screenX, screenY, pointer, button)) {
            return true
        }

        if (Vector2(sx.toFloat(), sy.toFloat()).dst(Vector2(screenX.toFloat(), screenY.toFloat())) >= 5f)
            return true

        val p = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val p2 = Vector2(p.x, p.y)
        val nearestShip = state.ships.minByOrNull { it.position.dst2(p2) }
        state.targetShipIndex = state.ships.indexOf(nearestShip)
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button != Input.Buttons.LEFT || pointer != 1) {

        }
        if (gestures.touchDown(screenX, screenY, pointer, button)) {
            return true
        }
        sx = screenX
        sy = screenY
        return true
    }

    override fun scrolled(amount: Int): Boolean {
        if (gestures.scrolled(amount)) {
            return true
        }

        val dz = amount / 15f
        updateCameraZoom(camera, dz)
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return gestures.mouseMoved(screenX, screenY)
    }
}