package com.h0tk3y.orbiters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.FloatArray
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.color
import ktx.graphics.use

class OrbitersGame : KtxGame<KtxScreen>() {
    val batch by lazy { SpriteBatch() }
    val font by lazy { BitmapFont() }

    override fun create() {
        addScreen(MainMenuScreen(this))
        setScreen<MainMenuScreen>()
        super.create()
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        super.dispose()
    }
}

class GameScreen(private val game: OrbitersGame) : KtxScreen {
    private val height = 720f
    private val width = 1280f

    private val shapeRenderer: ShapeRenderer = ShapeRenderer(8192).apply { setAutoShapeType(true) }
    private val camera: OrthographicCamera =
        OrthographicCamera().apply { setToOrtho(false, width, height) }
    private val viewport = FitViewport(800f, 480f, camera)

    private val skyImage: Texture =
        Texture(Gdx.files.internal("sky.png")).apply { setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.MirroredRepeat) }

    private val state = GameState(width, height)

    override fun show() {
        Gdx.input.inputProcessor = GameInputAdapter(camera, state)
        super.show()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        super.resize(width, height)
    }

    override fun render(delta: Float) {
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined

        game.batch.use {
            drawBackground()
        }

        shapeRenderer.use(ShapeRenderer.ShapeType.Filled) {
            enableAlphaBlending()

            renderCentralBody(shapeRenderer, state.centralBody, state.center)
            state.ships.forEachIndexed { index, orbitalUnit ->
                val selectionKind = when (index) {
                    state.selectedShipIndex -> UnitSelectionKind.CURRENT
                    state.targetShipIndex -> UnitSelectionKind.TARGET
                    else -> null
                }
                renderUnit(shapeRenderer, orbitalUnit, selectionKind)

                if (selectionKind != null) {
                    calculateAndRenderOrbit(orbitalUnit, state.center, shapeRenderer, selectionKind)
                }
            }
        }

        game.batch.use {
            if (state.selectedShipIndex in 0 until state.ships.size) {
                renderCurrentUnitData(state.selectedShip, state.center, game.batch, game.font)
            }
        }

        if (!state.isPaused) {
            val selectedShip =
                if (state.selectedShipIndex in 0 until state.ships.size) state.selectedShip else null
            val targetShip =
                if (state.targetShipIndex in 0 until state.ships.size) state.ships[state.targetShipIndex] else null

            moveUnits(state.ships, state.center, state.centralBody, delta)
            state.selectedShipIndex =
                state.ships.indexOf(selectedShip, true).let { if (it == -1) 0 else it }
            state.targetShipIndex =
                state.ships.indexOf(targetShip, true).let { if (it == -1) 0 else it }
        }
        if (state.selectedShipIndex in 0 until state.ships.size) {
            handleShipMovementKeys(state.selectedShip, delta)
        }
        handlePauseKeyPress(state)
        handleSwitchingShips(state)
    }

    private fun drawBackground() {
        val x = camera.position.x
        val y = camera.position.y
        val parallax = 30f
        val px = x / parallax
        val py = -y / parallax
        val w = camera.viewportWidth
        val h = camera.viewportHeight

        game.batch.draw(
            skyImage,
            0f,
            0f,
            w,
            h,
            px.toInt(),
            py.toInt(),
            skyImage.width * 2,
            skyImage.height * 2,
            false,
            false
        )
    }

    private val orbitPointFloats = FloatArray(true, 0)
    private val maxOrbitSegments = 1000

    private fun calculateAndRenderOrbit(
        orbitalUnit: OrbitalUnit,
        center: Vector2,
        shapeRenderer: ShapeRenderer,
        unitSelectionKind: UnitSelectionKind
    ) {
        calculateOrbitPoints(
            orbitalUnit,
            state.centralBody,
            center,
            maxOrbitSegments,
            orbitPointFloats
        )

        shapeRenderer.color = when (unitSelectionKind) {
            UnitSelectionKind.CURRENT -> color(1f, 1f, 0f, 0.2f)
            UnitSelectionKind.TARGET -> color(1f, 0f, 0f, 0.2f)
        }
        if (orbitPointFloats.size > 2) {
            shapeRenderer.polyline(orbitPointFloats.toArray())
        }
    }
}

internal enum class UnitSelectionKind {
    CURRENT, TARGET
}

const val gameSecondsInRealSecond = 100f

