package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.add3DBox
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Polygon3D

/**
 * 🪢 WireHarness3D
 * Robot Elektronik Jumper Kablo Demeti & Siyah Kelepçe:
 * 6 renkli esnek sinyal kablosu şeridi ve düzenleyici siyah kablo bağı.
 */
object WireHarness3D {

    fun build(polygons: ArrayList<Polygon3D>) {
        val wireColors = listOf(
            Color(0xFF0F172A), // Siyah
            Color(0xFF2563EB), // Mavi
            Color(0xFFF8FAFC), // Beyaz
            Color(0xFF78350F), // Kahverengi
            Color(0xFFDC2626), // Kırmızı
            Color(0xFF64748B)  // Gri
        )

        val wireCount = wireColors.size
        val wireWidth = 2.2f
        val startX = -(wireCount * wireWidth) / 2f

        for (i in 0 until wireCount) {
            val wx = startX + i * wireWidth + 1.1f
            val wColor = wireColors[i]

            add3DBox(polygons, cx = wx, cy = -18f, cz = -75f, width = 1.8f, height = 3f, depth = 25f, color = wColor, renderLayer = 1)
            add3DBox(polygons, cx = wx, cy = -26f, cz = -42f, width = 1.8f, height = 3f, depth = 38f, color = wColor, renderLayer = 1)
            add3DBox(polygons, cx = wx, cy = -19f, cz = -3f, width = 1.8f, height = 3f, depth = 36f, color = wColor, renderLayer = 1)
        }

        // Siyah Kablo Bağı (Zip Tie)
        add3DBox(polygons, cx = 0f, cy = -27f, cz = -42f, width = wireCount * wireWidth + 4f, height = 5f, depth = 5f, color = Color(0xFF0F172A), renderLayer = 1)
    }

    fun buildStandalone(): List<Polygon3D> {
        val list = ArrayList<Polygon3D>(40)
        build(list)
        return list
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Composable
fun WireHarness3DView(modifier: Modifier = Modifier) {
    val polygons = WireHarness3D.buildStandalone()
    Part3DPreviewBox(
        title = "Jumper Kablo Demeti",
        subtitle = "6-Kanallı Renkli Şerit & Kablo Bağı Kelepçesi",
        polygons = polygons,
        modifier = modifier,
        yaw = -45f,
        pitch = 30f,
        zoom = 3.2f
    )
}

@Preview
@Composable
private fun WireHarness3DPreview() {
    WireHarness3DView()
}

