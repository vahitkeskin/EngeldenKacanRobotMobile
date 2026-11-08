package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.component
import com.vahitkeskin.engeldenkacanrobotmobile.theme.EKRobotColor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.CylinderAxis
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.add3DBox
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.add3DCylinder
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.addRotatedBox
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Polygon3D
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Vector3D
import kotlin.math.PI

/**
 * 🔄 Sg90Servo3D
 * SG90 9g Mikro Servo Motor & HC-SR04 Taşıyıcı U-Braket:
 * Şeffaf mavi TowerPro kasa, üst basamaklı dişli tareti, sarı etiket,
 * pirinç dişli şaft, 3-renkli kablo, beyaz naylon servo horn/kolu
 * ve siyah pleksi U-braket taşıyıcı kollar.
 */
object Sg90Servo3D {

    fun build(
        polygons: ArrayList<Polygon3D>,
        servoAngle: Int = 90
    ) {
        // SG90 Servo Pivot Konumu
        val servoPivot = Vector3D(0f, -28f, -107f)

        // A. SABİT SG90 MİKRO SERVO MOTOR GÖVDESİ (Şeffaf Mavi TowerPro Kasa)
        val servoBlueColor = EKRobotColor.Alpha.BlueGlass
        val servoLabelColor = EKRobotColor.Status.WarningBright
        val servoEarColor = EKRobotColor.Alpha.BlueGlass
        val brassGearColor = EKRobotColor.Status.WarningDark
        val screwColor = EKRobotColor.Theme.Dark.TextSecondary

        // 1. Ana Şeffaf Mavi Gövde (23 x 22 x 12 mm)
        add3DBox(polygons, cx = 0f, cy = -15f, cz = -105f, width = 23f, height = 22f, depth = 12f, color = servoBlueColor, renderLayer = 1)
        // 2. Üst Basamaklı Dişli Tareti
        add3DBox(polygons, cx = 0f, cy = -26.5f, cz = -107f, width = 12f, height = 3.5f, depth = 12f, color = servoBlueColor, renderLayer = 1)
        // 3. TowerPro SG90 Sarı Etiketi
        add3DBox(polygons, cx = 0f, cy = -15f, cz = -98.8f, width = 18f, height = 10f, depth = 0.5f, color = servoLabelColor, renderLayer = 1)
        // 4. İki Yandaki Montaj Kulakçıkları ve Şasiye Tutturan Vidalar
        add3DBox(polygons, cx = 0f, cy = -13f, cz = -105f, width = 32f, height = 2.5f, depth = 12f, color = servoEarColor, renderLayer = 1)
        add3DBox(polygons, cx = -13.5f, cy = -15f, cz = -105f, width = 3f, height = 3.5f, depth = 3f, color = screwColor, renderLayer = 1)
        add3DBox(polygons, cx = 13.5f, cy = -15f, cz = -105f, width = 3f, height = 3.5f, depth = 3f, color = screwColor, renderLayer = 1)
        // 5. Pirinç Çıkış Dişli Şaftı
        add3DCylinder(polygons, cx = 0f, cy = -28.5f, cz = -107f, radius = 2.5f, height = 2f, segments = 8, color = brassGearColor, axis = CylinderAxis.Y, renderLayer = 1)
        // 6. Alttan Çıkan 3 Renkli Servo Şerit Kablosu (Kahverengi, Kırmızı, Turuncu)
        add3DBox(polygons, cx = -2f, cy = -4f, cz = -105f, width = 1.8f, height = 2f, depth = 12f, color = EKRobotColor.Alpha.AmberDeep, renderLayer = 1)
        add3DBox(polygons, cx = 0f, cy = -4f, cz = -105f, width = 1.8f, height = 2f, depth = 12f, color = EKRobotColor.Status.DangerDark, renderLayer = 1)
        add3DBox(polygons, cx = 2f, cy = -4f, cz = -105f, width = 1.8f, height = 2f, depth = 12f, color = EKRobotColor.Status.Orange, renderLayer = 1)

        // B. DÖNEN KAFA MEKANİZMASI (0° .. 90° .. 180° SERVO AÇISI)
        val angleRad = (90 - servoAngle) * (PI.toFloat() / 180f)

        // 1. Beyaz Naylon Servo Kolu (Horn) ve Merkez Vidası
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(0f, -30f, -107f), width = 6f, height = 2f, depth = 16f, color = EKRobotColor.Theme.Dark.TextPrimary, angleRad = angleRad, renderLayer = 1)
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(0f, -31.2f, -107f), width = 2.5f, height = 0.8f, depth = 2.5f, color = screwColor, angleRad = angleRad, renderLayer = 1)

        // 2. Siyah Pleksi U-Braket (HC-SR04 Taşıyıcı Braket)
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(0f, -32.5f, -107f), width = 46f, height = 3f, depth = 8f, color = EKRobotColor.Theme.Dark.BgPrimary, angleRad = angleRad, renderLayer = 1)
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(-21f, -39f, -107f), width = 3f, height = 11f, depth = 8f, color = EKRobotColor.Theme.Dark.BgPrimary, angleRad = angleRad, renderLayer = 1)
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(21f, -39f, -107f), width = 3f, height = 11f, depth = 8f, color = EKRobotColor.Theme.Dark.BgPrimary, angleRad = angleRad, renderLayer = 1)
    }

    fun buildStandalone(servoAngle: Int = 90): List<Polygon3D> {
        val list = ArrayList<Polygon3D>(90)
        build(list, servoAngle = servoAngle)
        return list
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Composable
fun Sg90Servo3DView(
    modifier: Modifier = Modifier,
    servoAngle: Int = 90
) {
    val polygons = Sg90Servo3D.buildStandalone(servoAngle = servoAngle)
    Part3DPreviewBox(
        title = "SG90 Mikro Servo Motor",
        subtitle = "Şeffaf Mavi Kasa & Dönen U-Braket",
        polygons = polygons,
        modifier = modifier,
        yaw = -35f,
        pitch = 28f,
        zoom = 3.6f
    )
}

@Preview
@Composable
private fun Sg90Servo3DPreview() {
    Sg90Servo3DView()
}
