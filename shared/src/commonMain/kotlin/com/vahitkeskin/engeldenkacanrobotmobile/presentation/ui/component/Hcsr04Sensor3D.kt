package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.component
import com.vahitkeskin.engeldenkacanrobotmobile.theme.EKRobotColor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.addRotatedBox
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Polygon3D
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Vector3D
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🎯 Hcsr04Sensor3D
 * HC-SR04 Ultrasonik Mesafe Sensörü: Kraliyet mavisi PCB kartı,
 * 4 montaj deliği, 16MHz kristal osilatör, 4 altın pin header,
 * 2x 24-segment fırçalanmış alüminyum silindirik ultrasonik gözler
 * (T - Transmitter & R - Receiver), akustik petek ızgara, merkez piezo
 * noktaları ve canlı 3D sonar dalga konisi.
 */
object Hcsr04Sensor3D {

    fun build(
        polygons: ArrayList<Polygon3D>,
        telemetry: RobotTelemetry,
        sonarWavePhase: Float = 0f
    ) {
        val servoPivot = Vector3D(0f, -28f, -107f)
        val angleRad = (90 - telemetry.servoAngle) * (PI.toFloat() / 180f)

        // C. HC-SR04 ULTRASONİK SENSÖRÜ (Kraliyet Mavisi PCB, 4 Köşe Delikleri, Kristal Osilatör, 4-Pin Header, 2x 24-Segment Göz)
        val pcbCenter = Vector3D(0f, -42f, -111f)
        val pcbBlueColor = EKRobotColor.Brand.Indigo // Parlak Kraliyet Mavisi PCB
        val holeCopperColor = EKRobotColor.Status.WarningDark
        val crystalSilverColor = EKRobotColor.Theme.Dark.TextSecondary
        val pinGoldColor = EKRobotColor.Status.Warning

        // 1. Mavi PCB Kartı
        addRotatedBox(polygons, pivot = servoPivot, center = pcbCenter, width = 50f, height = 22f, depth = 2.2f, color = pcbBlueColor, angleRad = angleRad, renderLayer = 1)

        // 2. 4 Köşe Montaj Delikleri (Pirinç/Bakır Halka Vurgusu)
        val holeOffsets = listOf(
            Vector3D(-22f, -50f, -112.2f),
            Vector3D(22f, -50f, -112.2f),
            Vector3D(-22f, -34f, -112.2f),
            Vector3D(22f, -34f, -112.2f)
        )
        for (hp in holeOffsets) {
            addRotatedBox(polygons, pivot = servoPivot, center = hp, width = 2.5f, height = 2.5f, depth = 0.5f, color = holeCopperColor, angleRad = angleRad, renderLayer = 1)
        }

        // 3. Merkezi 16 MHz Kristal Osilatör (İki gözün tam ortasındaki gümüş metal tüp)
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(0f, -42f, -112.5f), width = 8f, height = 4f, depth = 2f, color = crystalSilverColor, angleRad = angleRad, renderLayer = 1)

        // 4. Alt 4 Pinli Header (VCC, TRIG, ECHO, GND)
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(0f, -30f, -111f), width = 12f, height = 3.5f, depth = 3.5f, color = EKRobotColor.Theme.Dark.BgPrimary, angleRad = angleRad, renderLayer = 1)
        for (pinX in listOf(-4.5f, -1.5f, 1.5f, 4.5f)) {
            addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(pinX, -27f, -111f), width = 1.2f, height = 3.5f, depth = 1.2f, color = pinGoldColor, angleRad = angleRad, renderLayer = 1)
        }

        // 5. İki Adet 24-Segment Alüminyum Ultrasonik Dönüştürücü (Transducer T & R)
        // Sol Göz (Transmitter - T)
        build3DTransducer(
            polygons = polygons,
            pivot = servoPivot,
            center = Vector3D(-13.5f, -42f, -112f),
            radius = 8.5f,
            depth = 12.5f,
            label = "T",
            angleRad = angleRad
        )

        // Sağ Göz (Receiver - R)
        build3DTransducer(
            polygons = polygons,
            pivot = servoPivot,
            center = Vector3D(13.5f, -42f, -112f),
            radius = 8.5f,
            depth = 12.5f,
            label = "R",
            angleRad = angleRad
        )

        // D. CANLI 3D ULTRASONİK SONAR IŞINI (Layer 2 - En Son Çizilir)
        if (telemetry.hasSignal && telemetry.frontDistance > 0) {
            build3DSonarCone(
                polygons = polygons,
                pivot = servoPivot,
                headCenter = Vector3D(0f, -42f, -125f),
                distanceCm = telemetry.frontDistance,
                angleRad = angleRad,
                sonarWavePhase = sonarWavePhase
            )
        }
    }

    private fun build3DTransducer(
        polygons: ArrayList<Polygon3D>,
        pivot: Vector3D,
        center: Vector3D,
        radius: Float,
        depth: Float,
        label: String,
        angleRad: Float
    ) {
        val segments = 24
        val silverAlumColor = EKRobotColor.Theme.Light.RadarBg // Fırçalanmış parlak alüminyum gövde
        val rimColor = EKRobotColor.Theme.Dark.TextSecondary        // Ön çerçeve bileziği
        val meshColor = EKRobotColor.Theme.Dark.BgPrimary       // Akustik siyah ızgara kapağı
        val piezoDotColor = EKRobotColor.Theme.Light.RadarBg   // Merkez piezo seramik sensör noktası

        val frontZ = center.z - depth
        val backZ = center.z

        val frontPts = ArrayList<Vector3D>(segments)
        val backPts = ArrayList<Vector3D>(segments)
        val piezoPts = ArrayList<Vector3D>(segments)

        val piezoRadius = radius * 0.28f

        for (i in 0 until segments) {
            val a = i * (2f * PI.toFloat() / segments)
            val dx = radius * cos(a)
            val dy = radius * sin(a)

            val rawBack = Vector3D(center.x + dx, center.y + dy, backZ)
            val rawFront = Vector3D(center.x + dx, center.y + dy, frontZ)
            val rawPiezo = Vector3D(center.x + piezoRadius * cos(a), center.y + piezoRadius * sin(a), frontZ - 0.2f)

            frontPts.add(pivot + (rawFront - pivot).rotateY(angleRad))
            backPts.add(pivot + (rawBack - pivot).rotateY(angleRad))
            piezoPts.add(pivot + (rawPiezo - pivot).rotateY(angleRad))
        }

        // 1. Ön Akustik Siyah Izgara Kapağı
        polygons.add(
            Polygon3D(
                vertices = frontPts,
                color = meshColor,
                strokeColor = rimColor,
                strokeWidth = 0.8f,
                isDoubleSided = true,
                renderLayer = 1
            )
        )

        // 2. Merkez Piezo Seramik Sensör Noktası
        polygons.add(
            Polygon3D(
                vertices = piezoPts,
                color = piezoDotColor,
                isDoubleSided = true,
                renderLayer = 1
            )
        )

        // 3. Parlak Alüminyum Silindir Gövdesi
        for (i in 0 until segments) {
            val next = (i + 1) % segments
            polygons.add(
                Polygon3D(
                    vertices = listOf(frontPts[i], frontPts[next], backPts[next], backPts[i]),
                    color = silverAlumColor,
                    isDoubleSided = true,
                    renderLayer = 1
                )
            )
        }

        // 4. PCB Üzerindeki "T" / "R" Beyaz/Gümüş Gösterge İşareti
        val labelOffset = if (label == "T") -radius - 3f else radius + 3f
        addRotatedBox(
            polygons = polygons,
            pivot = pivot,
            center = Vector3D(center.x + labelOffset, center.y, center.z - 0.5f),
            width = 2.5f, height = 2.5f, depth = 0.5f,
            color = EKRobotColor.Theme.Dark.TextSecondary,
            angleRad = angleRad,
            renderLayer = 1
        )
    }

    private fun build3DSonarCone(
        polygons: ArrayList<Polygon3D>,
        pivot: Vector3D,
        headCenter: Vector3D,
        distanceCm: Int,
        angleRad: Float,
        sonarWavePhase: Float
    ) {
        val beamLength = (distanceCm * 2.4f).coerceIn(30f, 220f)
        val spreadRadius = beamLength * 0.32f

        val beamColor = when {
            distanceCm <= 15 -> EKRobotColor.Status.Danger // TEHLİKE / KIRMIZI
            distanceCm <= 30 -> EKRobotColor.Status.Warning // UYARI / SARI
            else -> EKRobotColor.Status.Success             // GÜVENLİ / YEŞİL
        }

        val apexRaw = headCenter
        val targetZRaw = headCenter.z - beamLength

        val coneSegments = 12
        val basePts = ArrayList<Vector3D>(coneSegments)

        for (i in 0 until coneSegments) {
            val a = i * (2f * PI.toFloat() / coneSegments)
            val dx = spreadRadius * cos(a)
            val dy = spreadRadius * sin(a)
            val rawPt = Vector3D(headCenter.x + dx, headCenter.y + dy, targetZRaw)
            basePts.add(pivot + (rawPt - pivot).rotateY(angleRad))
        }

        val apexTransformed = pivot + (apexRaw - pivot).rotateY(angleRad)

        // 3D Volumetrik Koni Yüzeyleri (Şeffaf)
        for (i in 0 until coneSegments) {
            val next = (i + 1) % coneSegments
            polygons.add(
                Polygon3D(
                    vertices = listOf(apexTransformed, basePts[i], basePts[next]),
                    color = beamColor.copy(alpha = 0.22f),
                    isDoubleSided = true,
                    renderLayer = 2
                )
            )
        }

        // Hedef Düzlem Halkası
        polygons.add(
            Polygon3D(
                vertices = basePts,
                color = beamColor.copy(alpha = 0.35f),
                strokeColor = beamColor,
                strokeWidth = 1.5f,
                isDoubleSided = true,
                renderLayer = 2
            )
        )
    }

    fun buildStandalone(
        telemetry: RobotTelemetry = RobotTelemetry(hasSignal = true, frontDistance = 35, servoAngle = 90),
        sonarWavePhase: Float = 0.5f
    ): List<Polygon3D> {
        val list = ArrayList<Polygon3D>(200)
        build(
            polygons = list,
            telemetry = telemetry,
            sonarWavePhase = sonarWavePhase
        )
        return list
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Composable
fun Hcsr04Sensor3DView(
    modifier: Modifier = Modifier,
    telemetry: RobotTelemetry = RobotTelemetry(hasSignal = true, frontDistance = 35, servoAngle = 90)
) {
    val polygons = Hcsr04Sensor3D.buildStandalone(telemetry = telemetry)
    Part3DPreviewBox(
        title = "HC-SR04 Ultrasonik Sensör",
        subtitle = "2x 24-Segment Alüminyum Transdüser (T/R) & Sonar",
        polygons = polygons,
        modifier = modifier,
        yaw = -25f,
        pitch = 18f,
        zoom = 3.2f
    )
}

@Preview
@Composable
private fun Hcsr04Sensor3DPreview() {
    Hcsr04Sensor3DView()
}
