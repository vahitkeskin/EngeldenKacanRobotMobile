package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.WheelDirection
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.add3DBox
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Polygon3D
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Vector3D
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🏎️ RightWheel3D
 * Robotun sağ tekerleği: 32-segment yuvarlak kauçuk lastik,
 * sarı TT jant, merkez aks kapağı ve D-şaft mekanizması.
 */
object RightWheel3D {

    const val DEFAULT_CX = 72f
    const val DEFAULT_CY = 6f
    const val DEFAULT_CZ = -18f
    const val DEFAULT_RADIUS = 22f
    const val DEFAULT_THICKNESS = 16f

    fun build(
        polygons: ArrayList<Polygon3D>,
        direction: WheelDirection = WheelDirection.STOPPED,
        phase: Float = 0f,
        cx: Float = DEFAULT_CX,
        cy: Float = DEFAULT_CY,
        cz: Float = DEFAULT_CZ,
        radius: Float = DEFAULT_RADIUS,
        thickness: Float = DEFAULT_THICKNESS
    ) {
        val segments = 32
        val tireColor = Color(0xFF1E293B)
        val tireDarkColor = Color(0xFF0F172A)
        val hubColor = Color(0xFFFACC15)
        val hubBorderColor = Color(0xFFEAB308)
        val axleColor = Color(0xFF0F172A)

        val halfT = thickness / 2f
        val outerX = cx + halfT // Sağ tekerlek: dış yüzey +X yönünde
        val innerX = cx - halfT

        val hubRadius = radius * 0.73f
        val axleRadius = radius * 0.22f
        val spinRad = phase * (PI.toFloat() / 180f)

        val outerTirePts = ArrayList<Vector3D>(segments)
        val outerHubPts = ArrayList<Vector3D>(segments)
        val outerAxlePts = ArrayList<Vector3D>(segments)
        val innerTirePts = ArrayList<Vector3D>(segments)

        for (i in 0 until segments) {
            val angle = (i * (2f * PI.toFloat() / segments)) + spinRad
            val cosA = cos(angle)
            val sinA = sin(angle)

            outerTirePts.add(Vector3D(outerX, cy + radius * cosA, cz + radius * sinA))
            outerHubPts.add(Vector3D(outerX, cy + hubRadius * cosA, cz + hubRadius * sinA))
            outerAxlePts.add(Vector3D(outerX, cy + axleRadius * cosA, cz + axleRadius * sinA))
            innerTirePts.add(Vector3D(innerX, cy + radius * cosA, cz + radius * sinA))
        }

        // 1. Dış Lastik Yanağı
        for (i in 0 until segments) {
            val next = (i + 1) % segments
            polygons.add(Polygon3D(listOf(outerTirePts[i], outerTirePts[next], outerHubPts[next], outerHubPts[i]), tireColor, isDoubleSided = true, renderLayer = 1))
        }

        // 2. Dış Sarı Jant
        for (i in 0 until segments) {
            val next = (i + 1) % segments
            polygons.add(Polygon3D(listOf(outerHubPts[i], outerHubPts[next], outerAxlePts[next], outerAxlePts[i]), hubColor, strokeColor = hubBorderColor, strokeWidth = 0.4f, isDoubleSided = true, renderLayer = 1))
        }

        // 3. Dış Merkez Aks Kapağı
        polygons.add(Polygon3D(outerAxlePts, axleColor, isDoubleSided = true, renderLayer = 1))

        // 4. Lastik Sırtı (32 Eksenli Pürüzsüz Yuvarlak Çevre)
        for (i in 0 until segments) {
            val next = (i + 1) % segments
            val treadTone = if (i % 2 == 0) tireColor else tireDarkColor
            polygons.add(Polygon3D(listOf(outerTirePts[i], outerTirePts[next], innerTirePts[next], innerTirePts[i]), treadTone, strokeColor = Color(0xFF334155), strokeWidth = 0.3f, isDoubleSided = true, renderLayer = 1))
        }

        // 5. İç Yüzey
        polygons.add(Polygon3D(innerTirePts, tireColor, isDoubleSided = true, renderLayer = 1))

        // 6. D-Şaft Aks Başlığı (+X yönünde hafif çıkıntı)
        add3DBox(polygons, cx = outerX + 1f, cy = cy, cz = cz, width = 2.5f, height = 6.5f, depth = 6.5f, color = axleColor, renderLayer = 1)
    }

    fun buildStandalone(
        direction: WheelDirection = WheelDirection.FORWARD,
        phase: Float = 0f
    ): List<Polygon3D> {
        val list = ArrayList<Polygon3D>(120)
        build(list, direction = direction, phase = phase, cx = 0f, cy = 0f, cz = 0f)
        return list
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Composable
fun RightWheel3DView(
    modifier: Modifier = Modifier,
    phase: Float = 25f,
    direction: WheelDirection = WheelDirection.FORWARD
) {
    val polygons = RightWheel3D.buildStandalone(direction = direction, phase = phase)
    Part3DPreviewBox(
        title = "Sağ Tekerlek (Right Wheel)",
        subtitle = "32-Segment Kauçuk Lastik & Sarı Jant",
        polygons = polygons,
        modifier = modifier,
        yaw = 45f,
        pitch = 20f,
        zoom = 3.6f
    )
}

@Preview
@Composable
private fun RightWheel3DPreview() {
    RightWheel3DView()
}

