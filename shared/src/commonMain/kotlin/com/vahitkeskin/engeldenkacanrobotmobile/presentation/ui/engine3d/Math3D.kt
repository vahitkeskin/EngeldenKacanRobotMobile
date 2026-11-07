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
    val renderLayer: Int = 3 // 0: Zemin, 1: Alt Gövde, 2: MDF Şasi/Teker, 3: Üst Donanım (Pil, Arduino, Servo), 4: Sonar
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
    val renderLayer: Int = 3
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

            // Back-face culling (Kamera bakış doğrultusu Z eksenindedir)
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

            // Perspektif Bölme
            var avgZ = 0f
            val screenPoints = ArrayList<Offset>(transformedVerts.size)
            var allInFront = true

            for (tv in transformedVerts) {
                val zCam = tv.z + cameraDistance
                if (zCam <= 10f) {
                    allInFront = false
                    break
                }
                val scale = focalDistance / zCam
                val sx = cx + tv.x * scale
                val sy = cy + tv.y * scale
                screenPoints.add(Offset(sx, sy))
                avgZ += zCam
            }

            if (!allInFront || screenPoints.size < 3) continue

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

        // Katmanlı ve Derinlikli Sıralama (Layered Depth Sorting):
        // Kullanıcı İsteği: "Robotu döndürdüğümde çoğu zaman pil, arduino vs vs kayboluyor hiçbirşey kaybolmasın"
        // Normal ve üst bakışta (pitch >= -10f):
        //   Layer 0 (Kesim matı) -> Layer 1 (Alt gövde) -> Layer 2 (MDF Şasi & Tekerler) -> Layer 3 (Üst elektronik: Pil, Arduino) -> Layer 4 (Sonar)
        //   Böylece zemin matı veya MDF şasi pil ya da Arduino'nun üzerine ASLA çizilemez, bileşenler 360° dönüşte ASLA kaybolmaz!
        // Alttan bakışta (pitch < -10f):
        //   Layer 3 -> Layer 2 -> Layer 1 -> Layer 0
        val isLookingFromBelow = pitch < -10f
        projectedList.sortWith(
            Comparator { a, b ->
                val layerDiff = if (!isLookingFromBelow) {
                    a.renderLayer.compareTo(b.renderLayer)
                } else {
                    b.renderLayer.compareTo(a.renderLayer)
                }
                if (layerDiff != 0) layerDiff else b.depthZ.compareTo(a.depthZ)
            }
        )
        return projectedList
    }
}
