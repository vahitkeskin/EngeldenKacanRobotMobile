package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.*

/**
 * 📐 Math3D - Hafif, Yüksek Performanslı 3D Vektör ve Matris Motoru
 * KMP (Android, iOS, Desktop) üzerinde 60-120 FPS sıfır gecikmeli perspektif projeksiyon.
 */
data class Vector3D(
    val x: Float,
    val y: Float,
    val z: Float
) {
    operator fun plus(v: Vector3D) = Vector3D(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vector3D) = Vector3D(x - v.x, y - v.y, z - v.z)
    operator fun times(s: Float) = Vector3D(x * s, y * s, z * s)

    fun length(): Float = sqrt(x * x + y * y + z * z)

    fun normalized(): Vector3D {
        val len = length()
        return if (len > 0.0001f) Vector3D(x / len, y / len, z / len) else Vector3D(0f, 0f, 1f)
    }

    infix fun dot(v: Vector3D): Float = x * v.x + y * v.y + z * v.z

    infix fun cross(v: Vector3D): Vector3D = Vector3D(
        y * v.z - z * v.y,
        z * v.x - x * v.z,
        x * v.y - y * v.x
    )

    fun rotateX(rad: Float): Vector3D {
        val cos = cos(rad)
        val sin = sin(rad)
        return Vector3D(x, y * cos - z * sin, y * sin + z * cos)
    }

    fun rotateY(rad: Float): Vector3D {
        val cos = cos(rad)
        val sin = sin(rad)
        return Vector3D(x * cos + z * sin, y, -x * sin + z * cos)
    }

    fun rotateZ(rad: Float): Vector3D {
        val cos = cos(rad)
        val sin = sin(rad)
        return Vector3D(x * cos - y * sin, x * sin + y * cos, z)
    }
}

data class ProjectedPoint(
    val offset: Offset,
    val z: Float
)

data class Polygon3D(
    val vertices: List<Vector3D>,
    val color: Color,
    val strokeColor: Color? = null,
    val strokeWidth: Float = 0f,
    val isDoubleSided: Boolean = true,
    val renderLayer: Int = 1 // 0: Zemin Kesim Matı, 1: Katı Robot Modeli, 2: Saydam Sonar Işını
) {
    fun normal(): Vector3D {
        if (vertices.size < 3) return Vector3D(0f, 0f, 1f)
        val v0 = vertices[0]
        val v1 = vertices[1]
        val v2 = vertices[2]
        return ((v1 - v0) cross (v2 - v0)).normalized()
    }
}

data class ProjectedPolygon(
    val points: List<Offset>,
    val depthZ: Float,
    val color: Color,
    val strokeColor: Color?,
    val strokeWidth: Float,
    val renderLayer: Int = 1
)

class Camera3D(
    initialYaw: Float = -35f,
    initialPitch: Float = 32f,
    initialZoom: Float = 2.0f,
    initialPanX: Float = 0f,
    initialPanY: Float = 0f
) {
    var yaw by mutableFloatStateOf(initialYaw)
    var pitch by mutableFloatStateOf(initialPitch)
    var zoom by mutableFloatStateOf(initialZoom)
    var panX by mutableFloatStateOf(initialPanX)
    var panY by mutableFloatStateOf(initialPanY)

    fun reset() {
        yaw = -35f
        pitch = 32f
        zoom = 2.0f
        panX = 0f
        panY = 0f
    }

    fun project(
        polygons: List<Polygon3D>,
        screenWidth: Float,
        screenHeight: Float,
        lightDirection: Vector3D = Vector3D(0.4f, -0.7f, 0.6f).normalized(),
        pivot: Vector3D = Vector3D(0f, 6f, -10f)
    ): List<ProjectedPolygon> {
        val cx = screenWidth / 2f + panX
        val cy = screenHeight / 2f + panY

        val yawRad = yaw * (PI.toFloat() / 180f)
        val pitchRad = pitch * (PI.toFloat() / 180f)

        val focalDistance = 750f * zoom
        val cameraDistance = 650f

        val projectedList = ArrayList<ProjectedPolygon>(polygons.size)

        for (poly in polygons) {
            val transformedVerts = poly.vertices.map { v ->
                // Robotun tam geometrik merkezinde (0f, 6f, -10f) 360° kusursuz dönüş
                val rel = v - pivot
                val rot = rel.rotateY(yawRad).rotateX(pitchRad)
                rot + pivot
            }

            // Normal hesabı ve Lambertian gölgelendirme
            if (transformedVerts.size < 3) continue
            val v0 = transformedVerts[0]
            val v1 = transformedVerts[1]
            val v2 = transformedVerts[2]
            val normal = ((v1 - v0) cross (v2 - v0)).normalized()

            // Back-face culling (Tek taraflı poligonlar için)
            if (!poly.isDoubleSided && normal.z <= 0.05f) {
                continue
            }

            // Işık şiddeti (Ambient: 0.42, Diffuse: 0.58)
            val effectiveDot = if (poly.isDoubleSided) kotlin.math.abs(normal dot lightDirection) else max(0f, normal dot lightDirection)
            val shade = 0.42f + 0.58f * effectiveDot
            val shadedColor = Color(
                red = (poly.color.red * shade).coerceIn(0f, 1f),
                green = (poly.color.green * shade).coerceIn(0f, 1f),
                blue = (poly.color.blue * shade).coerceIn(0f, 1f),
                alpha = poly.color.alpha
            )

            // Perspektif Projeksiyon & Güvenli Z Kırpma (Anti-Disappearance / Anti-Clipping)
            var allBehind = true
            var avgZ = 0f
            val screenPoints = ArrayList<Offset>(transformedVerts.size)

            for (tv in transformedVerts) {
                val zCam = tv.z + cameraDistance
                if (zCam > 2f) {
                    allBehind = false
                }
                val safeZ = max(2f, zCam)
                val scale = focalDistance / safeZ
                val sx = cx + tv.x * scale
                val sy = cy + tv.y * scale
                screenPoints.add(Offset(sx, sy))
                avgZ += safeZ
            }

            if (allBehind || screenPoints.size < 3) continue

            avgZ /= transformedVerts.size

            projectedList.add(
                ProjectedPolygon(
                    points = screenPoints,
                    depthZ = avgZ,
                    color = shadedColor,
                    strokeColor = poly.strokeColor,
                    strokeWidth = poly.strokeWidth,
                    renderLayer = poly.renderLayer
                )
            )
        }

        // Katmanlı ve Derinlikli Sıralama (Painter's Algorithm):
        // Layer 0: Zemin Kesim Matı (Arka plan / ilk çizilir, robot parçalarını asla örtmez)
        // Layer 1: Katı Robot Modeli (MDF şasi, TT motorlar, tekerlekler, Arduino Uno, SG90 servo, HC-SR04)
        //          depthZ azalan sıra ile sıralanır. Arkadaki parçalar önce, öndeki parçalar sonra çizilir.
        //          360° yatay ve dikey dönüşte hiçbir parça kaybolmaz ve birbirini doğru örter!
        // Layer 2: Yarı Saydam Sonar Işını (En son çizilir)
        projectedList.sortWith(
            Comparator { a, b ->
                val groupDiff = a.renderLayer.compareTo(b.renderLayer)
                if (groupDiff != 0) groupDiff else b.depthZ.compareTo(a.depthZ)
            }
        )
        return projectedList
    }
}
