package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.component
import com.vahitkeskin.engeldenkacanrobotmobile.theme.EKRobotColor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.CylinderAxis
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.add3DBox
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.add3DCylinder
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Polygon3D
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Vector3D

/**
 * 💻 ArduinoUno3D
 * Arduino Uno R3: Otantik İtalyan Teal PCB kartı, DIP-28 ATmega328P entegresi
 * ve 28 bacağı, 16MHz kristal osilatör, USB Type-B portu, 2.1mm DC jack,
 * reset butonu, çift dişi header pin soketleri, ICSP pinleri ve durum LED'leri.
 */
object ArduinoUno3D {

    const val DEFAULT_Z = 25f

    fun build(
        polygons: ArrayList<Polygon3D>,
        telemetry: RobotTelemetry = RobotTelemetry(hasSignal = true),
        arduinoZ: Float = DEFAULT_Z
    ) {
        val brassStandoffColor = EKRobotColor.Status.WarningDark
        val arduinoTealColor = EKRobotColor.Robot3D.ArduinoPcb // Orijinal Arduino Teal / Cyan mavi
        val icColor = EKRobotColor.Theme.Dark.BgPrimary
        val icPinColor = EKRobotColor.Theme.Dark.TextSecondary
        val headerBlackColor = EKRobotColor.Theme.Dark.BgSecondary
        val usbSilverColor = EKRobotColor.Theme.Dark.TextSecondary
        val jackBlackColor = EKRobotColor.Theme.Dark.BgPrimary
        val screwSilver = EKRobotColor.Theme.Dark.TextSecondary

        // 1. 4 Köşe Pirinç Yükseltme Ayakları (Brass Hexagonal Standoffs)
        val standoffPositions = listOf(
            Vector3D(-22f, -4.5f, arduinoZ - 26f),
            Vector3D(22f, -4.5f, arduinoZ - 26f),
            Vector3D(-22f, -4.5f, arduinoZ + 26f),
            Vector3D(22f, -4.5f, arduinoZ + 26f)
        )
        for (sp in standoffPositions) {
            add3DCylinder(polygons, cx = sp.x, cy = sp.y, cz = sp.z, radius = 2.2f, height = 3f, segments = 6, color = brassStandoffColor, axis = CylinderAxis.Y, renderLayer = 1)
            add3DBox(polygons, cx = sp.x, cy = sp.y - 1.8f, cz = sp.z, width = 2.5f, height = 0.6f, depth = 2.5f, color = screwSilver, renderLayer = 1)
        }

        // 2. Arduino Uno Teal PCB Kartı
        add3DBox(polygons, cx = 0f, cy = -7.5f, cz = arduinoZ, width = 54f, height = 3f, depth = 64f, color = arduinoTealColor, renderLayer = 1)

        // 3. Soketli ATmega328P DIP-28 Mikrodenetleyici Entegresi ve 28 Bacağı
        val icCx = 6f
        val icCz = arduinoZ + 8f
        add3DBox(polygons, cx = icCx, cy = -11.5f, cz = icCz, width = 10f, height = 4.5f, depth = 34f, color = icColor, renderLayer = 1)
        // Çip Çentiği (Pin 1 Notch)
        add3DBox(polygons, cx = icCx, cy = -13.5f, cz = icCz - 17f, width = 3f, height = 0.8f, depth = 1.5f, color = EKRobotColor.Theme.Dark.Border, renderLayer = 1)
        // Entegrenin 28 Adet Gümüş Bacağı (14 Sol, 14 Sağ)
        add3DBox(polygons, cx = icCx - 5.5f, cy = -10f, cz = icCz, width = 1.5f, height = 2f, depth = 32f, color = icPinColor, renderLayer = 1)
        add3DBox(polygons, cx = icCx + 5.5f, cy = -10f, cz = icCz, width = 1.5f, height = 2f, depth = 32f, color = icPinColor, renderLayer = 1)

        // 4. 16.000 MHz Kristal Osilatör (Gümüş Metal Kutu)
        add3DBox(polygons, cx = -12f, cy = -10.5f, cz = arduinoZ - 12f, width = 7f, height = 3.5f, depth = 3.5f, color = EKRobotColor.Theme.Light.RadarBg, renderLayer = 1)

        // 5. Gümüş Metal USB Type-B Portu & İç Kılavuz
        add3DBox(polygons, cx = -21f, cy = -13f, cz = arduinoZ - 26f, width = 11f, height = 9.5f, depth = 13f, color = usbSilverColor, renderLayer = 1)
        add3DBox(polygons, cx = -21f, cy = -13f, cz = arduinoZ - 32.6f, width = 6f, height = 5f, depth = 0.6f, color = EKRobotColor.Theme.Dark.BgPrimary, renderLayer = 1)

        // 6. Siyah 2.1mm DC Güç Jackı & Merkez Pimi
        add3DBox(polygons, cx = -21f, cy = -13.5f, cz = arduinoZ + 24f, width = 10f, height = 10.5f, depth = 14f, color = jackBlackColor, renderLayer = 1)
        add3DBox(polygons, cx = -21f, cy = -13.5f, cz = arduinoZ + 31.2f, width = 2.2f, height = 2.2f, depth = 2f, color = screwSilver, renderLayer = 1)

        // 7. Kırmızı Reset Butonu
        add3DBox(polygons, cx = -21f, cy = -10f, cz = arduinoZ - 12f, width = 6f, height = 2f, depth = 6f, color = EKRobotColor.Theme.Dark.TextMuted, renderLayer = 1)
        add3DBox(polygons, cx = -21f, cy = -12f, cz = arduinoZ - 12f, width = 3.5f, height = 2.5f, depth = 3.5f, color = EKRobotColor.Status.Danger, renderLayer = 1)

        // 8. İki Yan Dişi Header Pin Soket Dizileri (Power/Analog & Digital)
        add3DBox(polygons, cx = -23.5f, cy = -12.5f, cz = arduinoZ + 2f, width = 4f, height = 7.5f, depth = 46f, color = headerBlackColor, renderLayer = 1)
        add3DBox(polygons, cx = 23.5f, cy = -12.5f, cz = arduinoZ + 2f, width = 4f, height = 7.5f, depth = 46f, color = headerBlackColor, renderLayer = 1)
        // Soket Üst Pin Yuvaları
        add3DBox(polygons, cx = -23.5f, cy = -16.4f, cz = arduinoZ + 2f, width = 2.2f, height = 0.4f, depth = 44f, color = EKRobotColor.Theme.Dark.BgPrimary, renderLayer = 1)
        add3DBox(polygons, cx = 23.5f, cy = -16.4f, cz = arduinoZ + 2f, width = 2.2f, height = 0.4f, depth = 44f, color = EKRobotColor.Theme.Dark.BgPrimary, renderLayer = 1)

        // 9. 2x3 ICSP Erkek Header Pinleri
        add3DBox(polygons, cx = icCx, cy = -11f, cz = arduinoZ + 28f, width = 6f, height = 4f, depth = 4f, color = EKRobotColor.Status.WarningDark, renderLayer = 1)

        // 10. Yeşil ON Güç LED'i ve Sarı L LED'i
        val powerLedColor = if (telemetry.hasSignal) EKRobotColor.Status.SuccessVibrant else EKRobotColor.CuttingMat.ForestDeep
        add3DBox(polygons, cx = -10f, cy = -9.5f, cz = arduinoZ - 4f, width = 2.5f, height = 1.2f, depth = 2.5f, color = powerLedColor, renderLayer = 1)
        add3DBox(polygons, cx = 10f, cy = -9.5f, cz = arduinoZ - 4f, width = 2.5f, height = 1.2f, depth = 2.5f, color = EKRobotColor.Status.Warning, renderLayer = 1)
    }

    fun buildStandalone(telemetry: RobotTelemetry = RobotTelemetry(hasSignal = true)): List<Polygon3D> {
        val list = ArrayList<Polygon3D>(150)
        build(list, telemetry = telemetry, arduinoZ = 0f)
        return list
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Composable
fun ArduinoUno3DView(
    modifier: Modifier = Modifier,
    telemetry: RobotTelemetry = RobotTelemetry(hasSignal = true)
) {
    val polygons = ArduinoUno3D.buildStandalone(telemetry = telemetry)
    Part3DPreviewBox(
        title = "Arduino Uno R3",
        subtitle = "ATmega328P, USB-B, DC Jack & Headers",
        polygons = polygons,
        modifier = modifier,
        yaw = -30f,
        pitch = 38f,
        zoom = 2.8f
    )
}

@Preview
@Composable
private fun ArduinoUno3DPreview() {
    ArduinoUno3DView()
}

