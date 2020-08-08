package com.h0tk3y.orbiters

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.FloatArray
import kotlin.math.round

internal fun renderCurrentUnitData(orbitalUnit: OrbitalUnit, center: Vector2, batch: SpriteBatch, font: BitmapFont) {
    val period = orbitalPeriod(orbitalUnit, center)
    val t = period / gameSecondsInRealSecond
    font.draw(batch, "T = ${round(t * 100) / 100}\nv = ${orbitalUnit.velocity.len()}", 20f, 100f)
}

internal fun renderCentralBody(shapeRenderer: ShapeRenderer, centralBody: CentralBody, center: Vector2) {
    shapeRenderer.color = centralBody.kind.fill
    shapeRenderer.circle(center.x, center.y, centralBody.radius, 32)
}

internal fun renderUnit(shapeRenderer: ShapeRenderer, orbitalUnit: OrbitalUnit, selectionKind: UnitSelectionKind?) {
    val size = orbitalUnit.kind.size
    val x = orbitalUnit.x
    val y = orbitalUnit.y

    val trianglePointAngle = MathUtils.PI / 5

    val vertexCoordinates = orbitalUnit.velocity.cpy().setLength(size).rotateRad(orbitalUnit.orientation)
    val px = x + vertexCoordinates.x
    val py = y + vertexCoordinates.y

    vertexCoordinates.rotateRad(MathUtils.PI - trianglePointAngle)
    val lx = x + vertexCoordinates.x
    val ly = y + vertexCoordinates.y

    vertexCoordinates.rotateRad(trianglePointAngle * 2)
    val rx = x + vertexCoordinates.x
    val ry = y + vertexCoordinates.y

    shapeRenderer.color = when (selectionKind) {
        UnitSelectionKind.CURRENT -> Color.GREEN
        UnitSelectionKind.TARGET -> Color.RED
        else -> Color.WHITE
    }
    shapeRenderer.polygon(floatArrayOf(px, py, lx, ly, rx, ry, px, py))
}

private const val curveRenderMinDistanceForCutOff = 10f
private val originalPositionVector = Vector2()
private val originalVelocityVector = Vector2()
private val predictionFloats = FloatArray(0)
