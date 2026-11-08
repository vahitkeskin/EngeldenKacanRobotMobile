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
 * 🔋 BatteryPack3D
 * 2x 18650 Li-Ion Batarya Yuvası ve Yeşil Güç Pilleri:
 * Çiftli siyah ABS pil kutusu, 2 adet yüksek akımlı yeşil silindirik pil,
 * nikel pozitif (+) terminaller, yalıtım halkaları ve besleme kabloları.
 */
object BatteryPack3D {

    const val DEFAULT_Z = 80f

    fun build(
        polygons: ArrayList<Polygon3D>,
        batZ: Float = DEFAULT_Z
    ) {
        // Siyah Batarya Yuvası Kasası
        add3DBox(polygons, cx = 0f, cy = -8f, cz = batZ, width = 64f, height = 8f, depth = 34f, color = EKRobotColor.Theme.Dark.BgPrimary, renderLayer = 1)

        // Ön ve Arka Yeşil Piller
        add3DCylinder(polygons, cx = 0f, cy = -14f, cz = batZ - 8f, radius = 6.5f, height = 50f, segments = 16, color = EKRobotColor.Status.SuccessVibrant, axis = CylinderAxis.X, renderLayer = 1)
        add3DCylinder(polygons, cx = 0f, cy = -14f, cz = batZ + 8f, radius = 6.5f, height = 50f, segments = 16, color = EKRobotColor.Status.SuccessVibrant, axis = CylinderAxis.X, renderLayer = 1)

        // Pozitif (+) Metal Uç Başlıkları ve Yalıtım Halkaları
        add3DCylinder(polygons, cx = -26f, cy = -14f, cz = batZ - 8f, radius = 2.5f, height = 3f, segments = 8, color = EKRobotColor.Theme.Light.RadarBg, axis = CylinderAxis.X, renderLayer = 1)
        add3DCylinder(polygons, cx = -26f, cy = -14f, cz = batZ + 8f, radius = 2.5f, height = 3f, segments = 8, color = EKRobotColor.Theme.Light.RadarBg, axis = CylinderAxis.X, renderLayer = 1)
        add3DCylinder(polygons, cx = -24.5f, cy = -14f, cz = batZ - 8f, radius = 6.6f, height = 1.5f, segments = 8, color = EKRobotColor.Theme.Dark.BgPrimary, axis = CylinderAxis.X, renderLayer = 1)
        add3DCylinder(polygons, cx = -24.5f, cy = -14f, cz = batZ + 8f, radius = 6.6f, height = 1.5f, segments = 8, color = EKRobotColor.Theme.Dark.BgPrimary, axis = CylinderAxis.X, renderLayer = 1)

        // Kırmızı & Siyah Besleme Telleri
        add3DBox(polygons, cx = -23f, cy = -6f, cz = batZ - 20f, width = 2f, height = 2f, depth = 22f, color = EKRobotColor.Status.DangerDark, renderLayer = 1)
        add3DBox(polygons, cx = -26f, cy = -6f, cz = batZ - 20f, width = 2f, height = 2f, depth = 22f, color = EKRobotColor.Theme.Dark.BgPrimary, renderLayer = 1)
    }

    fun buildStandalone(): List<Polygon3D> {
        val list = ArrayList<Polygon3D>(90)
        build(list, batZ = 0f)
        return list
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Composable
fun BatteryPack3DView(modifier: Modifier = Modifier) {
    val polygons = BatteryPack3D.buildStandalone()
    Part3DPreviewBox(
        title = "18650 Batarya Paketi",
        subtitle = "Çiftli Pil Yuvası & 2x Li-Ion Yeşil Pil",
        polygons = polygons,
        modifier = modifier,
        yaw = -35f,
        pitch = 30f,
        zoom = 3.2f
    )
}

@Preview
@Composable
private fun BatteryPack3DPreview() {
    BatteryPack3DView()
}

