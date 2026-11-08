package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.component
import com.vahitkeskin.engeldenkacanrobotmobile.theme.EKRobotColor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.CylinderAxis
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.add3DBox
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.add3DCylinder
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Polygon3D

/**
 * ⚙️ UndersideMotors3D
 * Alt Gövde TT DC Dişli Motorları:
 * Sol ve sağ sarı TT plastik redüktör gövdeleri, çelik 130 DC motor silindirleri,
 * lehim kulakçıkları, kırmızı-siyah kablolar, beyaz D-şaft aksları ve montaj cıvataları.
 */
object UndersideMotors3D {

    fun build(polygons: ArrayList<Polygon3D>) {
        val yellowMotorColor = EKRobotColor.Status.Warning // Sarı plastik TT dişli kutusu
        val yellowTabColor = EKRobotColor.Status.WarningDark
        val steelMotorColor = EKRobotColor.Theme.Dark.TextMuted  // Çelik 130 DC motor silindiri
        val endCapColor = EKRobotColor.Theme.Dark.Border     // Arka plastik kapak
        val lugBrassColor = EKRobotColor.Status.WarningDark   // Pirinç lehim terminali
        val whiteShaftColor = EKRobotColor.Theme.Dark.TextPrimary // Beyaz aks D-şaft
        val redWireColor = EKRobotColor.Status.DangerDark
        val blackWireColor = EKRobotColor.Theme.Dark.BgPrimary
        val boltColor = EKRobotColor.Theme.Dark.TextSecondary

        // SOL TT MOTOR (X = -46f)
        // 1. Sarı Plastik Dişli Kutusu Gövdesi
        add3DBox(polygons, cx = -46f, cy = 11f, cz = -14f, width = 19f, height = 16f, depth = 32f, color = yellowMotorColor, renderLayer = 1)
        add3DBox(polygons, cx = -46f, cy = 5f, cz = -14f, width = 21f, height = 3f, depth = 20f, color = yellowTabColor, renderLayer = 1)
        // 2. Arka Yuvadaki Çelik Silindirik DC Motor Gövdesi
        add3DCylinder(polygons, cx = -46f, cy = 11f, cz = 11f, radius = 6.2f, height = 18f, segments = 16, color = steelMotorColor, axis = CylinderAxis.Z, renderLayer = 1)
        // 3. Arka Plastik Uç Kapağı
        add3DCylinder(polygons, cx = -46f, cy = 11f, cz = 20.5f, radius = 5.8f, height = 2f, segments = 16, color = endCapColor, axis = CylinderAxis.Z, renderLayer = 1)
        // 4. İki Adet Pirinç Terminal Kulakçığı (Solder Lugs)
        add3DBox(polygons, cx = -43f, cy = 11f, cz = 22f, width = 2f, height = 3f, depth = 2.5f, color = lugBrassColor, renderLayer = 1)
        add3DBox(polygons, cx = -49f, cy = 11f, cz = 22f, width = 2f, height = 3f, depth = 2.5f, color = lugBrassColor, renderLayer = 1)
        // 5. Kırmızı & Siyah Motor Telleri
        add3DBox(polygons, cx = -43f, cy = 11f, cz = 26f, width = 1.8f, height = 1.8f, depth = 8f, color = redWireColor, renderLayer = 1)
        add3DBox(polygons, cx = -49f, cy = 11f, cz = 26f, width = 1.8f, height = 1.8f, depth = 8f, color = blackWireColor, renderLayer = 1)
        // 6. Dışarıya Uzanan Beyaz D-Şaft Aksı
        add3DCylinder(polygons, cx = -58f, cy = 11f, cz = -18f, radius = 3.2f, height = 8f, segments = 8, color = whiteShaftColor, axis = CylinderAxis.X, renderLayer = 1)
        // 7. Şasiye Tutturan M3 Montaj Cıvataları
        add3DBox(polygons, cx = -46f, cy = 2f, cz = -20f, width = 2.5f, height = 6f, depth = 2.5f, color = boltColor, renderLayer = 1)
        add3DBox(polygons, cx = -46f, cy = 2f, cz = -8f, width = 2.5f, height = 6f, depth = 2.5f, color = boltColor, renderLayer = 1)

        // SAĞ TT MOTOR (X = +46f)
        // 1. Sarı Plastik Dişli Kutusu Gövdesi
        add3DBox(polygons, cx = 46f, cy = 11f, cz = -14f, width = 19f, height = 16f, depth = 32f, color = yellowMotorColor, renderLayer = 1)
        add3DBox(polygons, cx = 46f, cy = 5f, cz = -14f, width = 21f, height = 3f, depth = 20f, color = yellowTabColor, renderLayer = 1)
        // 2. Arka Yuvadaki Çelik Silindirik DC Motor Gövdesi
        add3DCylinder(polygons, cx = 46f, cy = 11f, cz = 11f, radius = 6.2f, height = 18f, segments = 16, color = steelMotorColor, axis = CylinderAxis.Z, renderLayer = 1)
        // 3. Arka Plastik Uç Kapağı
        add3DCylinder(polygons, cx = 46f, cy = 11f, cz = 20.5f, radius = 5.8f, height = 2f, segments = 16, color = endCapColor, axis = CylinderAxis.Z, renderLayer = 1)
        // 4. İki Adet Pirinç Terminal Kulakçığı (Solder Lugs)
        add3DBox(polygons, cx = 43f, cy = 11f, cz = 22f, width = 2f, height = 3f, depth = 2.5f, color = lugBrassColor, renderLayer = 1)
        add3DBox(polygons, cx = 49f, cy = 11f, cz = 22f, width = 2f, height = 3f, depth = 2.5f, color = lugBrassColor, renderLayer = 1)
        // 5. Kırmızı & Siyah Motor Telleri
        add3DBox(polygons, cx = 43f, cy = 11f, cz = 26f, width = 1.8f, height = 1.8f, depth = 8f, color = redWireColor, renderLayer = 1)
        add3DBox(polygons, cx = 49f, cy = 11f, cz = 26f, width = 1.8f, height = 1.8f, depth = 8f, color = blackWireColor, renderLayer = 1)
        // 6. Dışarıya Uzanan Beyaz D-Şaft Aksı
        add3DCylinder(polygons, cx = 58f, cy = 11f, cz = -18f, radius = 3.2f, height = 8f, segments = 8, color = whiteShaftColor, axis = CylinderAxis.X, renderLayer = 1)
        // 7. Şasiye Tutturan M3 Montaj Cıvataları
        add3DBox(polygons, cx = 46f, cy = 2f, cz = -20f, width = 2.5f, height = 6f, depth = 2.5f, color = boltColor, renderLayer = 1)
        add3DBox(polygons, cx = 46f, cy = 2f, cz = -8f, width = 2.5f, height = 6f, depth = 2.5f, color = boltColor, renderLayer = 1)

        // Ön Alt Şasiden Sarkan Şeffaf Mavi SG90 Servo Alt Tabanı
        add3DBox(polygons, cx = 0f, cy = 8f, cz = -105f, width = 20f, height = 8f, depth = 12f, color = EKRobotColor.Alpha.BlueGlass, renderLayer = 1)
    }

    fun buildStandalone(): List<Polygon3D> {
        val list = ArrayList<Polygon3D>(120)
        build(list)
        return list
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Composable
fun UndersideMotors3DView(modifier: Modifier = Modifier) {
    val polygons = UndersideMotors3D.buildStandalone()
    Part3DPreviewBox(
        title = "Alt TT Motorlar & DC Motorlar",
        subtitle = "Sarı Dişli Kutuları, Çelik Gövdeler & Akslar",
        polygons = polygons,
        modifier = modifier,
        yaw = -45f,
        pitch = -25f, // Alttan görünüm
        zoom = 2.6f
    )
}

@Preview
@Composable
private fun UndersideMotors3DPreview() {
    UndersideMotors3DView()
}

