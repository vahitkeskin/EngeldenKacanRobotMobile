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
 * ⚡ L298NDriver3D
 * L298N Çift H-Köprüsü DC Motor Sürücüsü:
 * Kırmızı PCB kartı, siyah alüminyum kanatlı soğutucu,
 * iki adet filtre kondansatörü, vidalı mavi terminal klemensleri.
 */
object L298NDriver3D {

    const val DEFAULT_Z = -35f

    fun build(
        polygons: ArrayList<Polygon3D>,
        l298nZ: Float = DEFAULT_Z
    ) {
        // Kırmızı PCB Kartı
        add3DBox(polygons, cx = 0f, cy = -6f, cz = l298nZ, width = 50f, height = 4f, depth = 42f, color = EKRobotColor.Status.DangerDark, renderLayer = 1)

        // Alüminyum Siyah Soğutucu Gövdesi ve Kanatçıkları
        add3DBox(polygons, cx = 0f, cy = -16f, cz = l298nZ, width = 28f, height = 16f, depth = 22f, color = EKRobotColor.Theme.Dark.BgPrimary, renderLayer = 1)
        val finColor = EKRobotColor.Theme.Dark.Border
        for (finX in listOf(-9f, -3f, 3f, 9f)) {
            add3DBox(polygons, cx = finX, cy = -25f, cz = l298nZ, width = 2f, height = 4f, depth = 20f, color = finColor, renderLayer = 1)
        }

        // İki Adet Filtre Kondansatörü
        add3DCylinder(polygons, cx = -16f, cy = -12f, cz = l298nZ + 12f, radius = 4f, height = 10f, segments = 8, color = EKRobotColor.Theme.Dark.BgPrimary, axis = CylinderAxis.Y, renderLayer = 1)
        add3DCylinder(polygons, cx = 16f, cy = -12f, cz = l298nZ + 12f, radius = 4f, height = 10f, segments = 8, color = EKRobotColor.Theme.Dark.BgPrimary, axis = CylinderAxis.Y, renderLayer = 1)

        // Mavi Klemens Blokları ve Gümüş Vidaları
        val termBlue = EKRobotColor.Brand.BluePrimary
        val screwSilver = EKRobotColor.Theme.Dark.TextSecondary
        add3DBox(polygons, cx = -20f, cy = -10f, cz = l298nZ, width = 8f, height = 8f, depth = 14f, color = termBlue, renderLayer = 1)
        add3DBox(polygons, cx = 20f, cy = -10f, cz = l298nZ, width = 8f, height = 8f, depth = 14f, color = termBlue, renderLayer = 1)
        add3DBox(polygons, cx = 0f, cy = -10f, cz = l298nZ + 16f, width = 16f, height = 8f, depth = 8f, color = termBlue, renderLayer = 1)
        add3DBox(polygons, cx = -20f, cy = -14.5f, cz = l298nZ, width = 3f, height = 1f, depth = 3f, color = screwSilver, renderLayer = 1)
        add3DBox(polygons, cx = 20f, cy = -14.5f, cz = l298nZ, width = 3f, height = 1f, depth = 3f, color = screwSilver, renderLayer = 1)
    }

    fun buildStandalone(): List<Polygon3D> {
        val list = ArrayList<Polygon3D>(90)
        build(list, l298nZ = 0f)
        return list
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Composable
fun L298NDriver3DView(modifier: Modifier = Modifier) {
    val polygons = L298NDriver3D.buildStandalone()
    Part3DPreviewBox(
        title = "L298N Motor Sürücü",
        subtitle = "Alüminyum Soğutucu, Klemensler & Kondansatörler",
        polygons = polygons,
        modifier = modifier,
        yaw = -30f,
        pitch = 30f,
        zoom = 3.2f
    )
}

@Preview
@Composable
private fun L298NDriver3DPreview() {
    L298NDriver3DView()
}

