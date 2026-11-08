package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.CylinderAxis
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.add3DBox
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.add3DCylinder
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Polygon3D

/**
 * ⚪ CasterWheel3D
 * Arka Sarhoş Bilye Tekerlek (Omni-Directional Caster Ball):
 * Çelik montaj flanşı, 4 adet M3 montaj vidası,
 * döner çelik yatak kadehi ve serbest dönen beyaz bilye.
 */
object CasterWheel3D {

    const val DEFAULT_CX = 0f
    const val DEFAULT_CZ = 95f

    fun build(
        polygons: ArrayList<Polygon3D>,
        cx: Float = DEFAULT_CX,
        cz: Float = DEFAULT_CZ
    ) {
        val metalColor = Color(0xFF94A3B8)
        val swivelColor = Color(0xFF64748B)
        val ballColor = Color(0xFFF8FAFC)
        val screwColor = Color(0xFFCBD5E1)

        // Metal montaj taban plakası
        add3DBox(polygons, cx = cx, cy = 5f, cz = cz, width = 26f, height = 3f, depth = 26f, color = metalColor, renderLayer = 1)

        // 4 Köşe Montaj Vidası
        add3DBox(polygons, cx = cx - 9f, cy = 7f, cz = cz - 9f, width = 3f, height = 1.5f, depth = 3f, color = screwColor, renderLayer = 1)
        add3DBox(polygons, cx = cx + 9f, cy = 7f, cz = cz - 9f, width = 3f, height = 1.5f, depth = 3f, color = screwColor, renderLayer = 1)
        add3DBox(polygons, cx = cx - 9f, cy = 7f, cz = cz + 9f, width = 3f, height = 1.5f, depth = 3f, color = screwColor, renderLayer = 1)
        add3DBox(polygons, cx = cx + 9f, cy = 7f, cz = cz + 9f, width = 3f, height = 1.5f, depth = 3f, color = screwColor, renderLayer = 1)

        // Dönen metal kadeh
        add3DCylinder(polygons, cx = cx, cy = 10f, cz = cz, radius = 8f, height = 6f, segments = 16, color = swivelColor, axis = CylinderAxis.Y, renderLayer = 1)

        // Beyaz bilye tekerlek (Altı Y = 28f zemine tam oturur: 19f + 9f = 28f)
        add3DCylinder(polygons, cx = cx, cy = 19f, cz = cz, radius = 9f, height = 8f, segments = 16, color = ballColor, axis = CylinderAxis.X, renderLayer = 1)
    }

    fun buildStandalone(): List<Polygon3D> {
        val list = ArrayList<Polygon3D>(60)
        build(list, cx = 0f, cz = 0f)
        return list
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Composable
fun CasterWheel3DView(modifier: Modifier = Modifier) {
    val polygons = CasterWheel3D.buildStandalone()
    Part3DPreviewBox(
        title = "Arka Sarhoş Bilye Tekerlek",
        subtitle = "Metal Flanş, Yatak Kadehi & Döner Bilye",
        polygons = polygons,
        modifier = modifier,
        yaw = -40f,
        pitch = 30f,
        zoom = 3.8f
    )
}

@Preview
@Composable
private fun CasterWheel3DPreview() {
    CasterWheel3DView()
}

