package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.add3DBox
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Geometric3DPrimitives.addChassisPlate
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Polygon3D
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Vector3D

/**
 * 🪵 MdfChassis3D
 * MDF Lazer Kesim Ahşap Şasi Gövdesi:
 * Üst ve alt çift cidarlı laminant katmanlar, kavisli kenar bantları,
 * servo ön burnu, tekerlek girintileri ve montaj donanımları.
 */
object MdfChassis3D {

    fun build(polygons: ArrayList<Polygon3D>) {
        val yTop = -3f
        val yBottom = 3f
        val mdfTopColor = Color(0xFFD4A373)
        val mdfBottomColor = Color(0xFFB08958)
        val mdfEdgeColor = Color(0xFF8B5A2B)

        // Şasiyi 5 yerel bölüme (Bands) ayırarak hem poligon boyut farkından kaynaklanan
        // derinlik sıralama hatalarını engelliyoruz, hem de alttan bakışta üstteki parçaların
        // şasinin içinden görünmesini 100% kesinlikle engelliyoruz!

        // BAND 1: Ön Burun / Servo Dili (z: -125f .. -100f, x: -24f .. +24f)
        addChassisPlate(
            polygons = polygons,
            vTop = listOf(
                Vector3D(-24f, yTop, -125f),
                Vector3D(24f, yTop, -125f),
                Vector3D(24f, yTop, -100f),
                Vector3D(-24f, yTop, -100f)
            ),
            vBottom = listOf(
                Vector3D(-24f, yBottom, -125f),
                Vector3D(24f, yBottom, -125f),
                Vector3D(24f, yBottom, -100f),
                Vector3D(-24f, yBottom, -100f)
            ),
            edges = listOf(
                Pair(0, 1), // Ön kenar
                Pair(1, 2), // Sağ kenar
                Pair(3, 0)  // Sol kenar
            ),
            topColor = mdfTopColor, bottomColor = mdfBottomColor, edgeColor = mdfEdgeColor
        )

        // BAND 2: Ön Omuzlar ve Taper (z: -100f .. -50f)
        addChassisPlate(
            polygons = polygons,
            vTop = listOf(
                Vector3D(-42f, yTop, -100f),
                Vector3D(42f, yTop, -100f),
                Vector3D(65f, yTop, -80f),
                Vector3D(65f, yTop, -50f),
                Vector3D(-65f, yTop, -50f),
                Vector3D(-65f, yTop, -80f)
            ),
            vBottom = listOf(
                Vector3D(-42f, yBottom, -100f),
                Vector3D(42f, yBottom, -100f),
                Vector3D(65f, yBottom, -80f),
                Vector3D(65f, yBottom, -50f),
                Vector3D(-65f, yBottom, -50f),
                Vector3D(-65f, yBottom, -80f)
            ),
            edges = listOf(
                Pair(0, 1), // Ön birleşim
                Pair(1, 2), // Sağ omuz kavis
                Pair(2, 3), // Sağ düz kenar
                Pair(4, 5), // Sol düz kenar
                Pair(5, 0)  // Sol omuz kavis
            ),
            topColor = mdfTopColor, bottomColor = mdfBottomColor, edgeColor = mdfEdgeColor
        )

        // BAND 3: L298N Sürücü Bölgesi (z: -50f .. -5f, x: -52f .. +52f)
        addChassisPlate(
            polygons = polygons,
            vTop = listOf(
                Vector3D(-52f, yTop, -50f),
                Vector3D(52f, yTop, -50f),
                Vector3D(52f, yTop, -5f),
                Vector3D(-52f, yTop, -5f)
            ),
            vBottom = listOf(
                Vector3D(-52f, yBottom, -50f),
                Vector3D(52f, yBottom, -50f),
                Vector3D(52f, yBottom, -5f),
                Vector3D(-52f, yBottom, -5f)
            ),
            edges = listOf(
                Pair(1, 2), // Sağ tekerlek yuvası iç kenarı
                Pair(3, 0)  // Sol tekerlek yuvası iç kenarı
            ),
            topColor = mdfTopColor, bottomColor = mdfBottomColor, edgeColor = mdfEdgeColor
        )

        // BAND 4A: Arduino Uno Ön Bölgesi (z: -5f .. +15f, x: -52f .. +52f)
        addChassisPlate(
            polygons = polygons,
            vTop = listOf(
                Vector3D(-52f, yTop, -5f),
                Vector3D(52f, yTop, -5f),
                Vector3D(52f, yTop, 15f),
                Vector3D(-52f, yTop, 15f)
            ),
            vBottom = listOf(
                Vector3D(-52f, yBottom, -5f),
                Vector3D(52f, yBottom, -5f),
                Vector3D(52f, yBottom, 15f),
                Vector3D(-52f, yBottom, 15f)
            ),
            edges = listOf(
                Pair(1, 2), // Sağ iç kenar
                Pair(3, 0)  // Sol iç kenar
            ),
            topColor = mdfTopColor, bottomColor = mdfBottomColor, edgeColor = mdfEdgeColor
        )

        // Tekerlek Yuvası Basamak Kenarları
        polygons.add(Polygon3D(listOf(Vector3D(-65f, yTop, -50f), Vector3D(-52f, yTop, -50f), Vector3D(-52f, yBottom, -50f), Vector3D(-65f, yBottom, -50f)), mdfEdgeColor, isDoubleSided = true, renderLayer = 1))
        polygons.add(Polygon3D(listOf(Vector3D(52f, yTop, -50f), Vector3D(65f, yTop, -50f), Vector3D(65f, yBottom, -50f), Vector3D(52f, yBottom, -50f)), mdfEdgeColor, isDoubleSided = true, renderLayer = 1))
        polygons.add(Polygon3D(listOf(Vector3D(-52f, yTop, 15f), Vector3D(-65f, yTop, 15f), Vector3D(-65f, yBottom, 15f), Vector3D(-52f, yBottom, 15f)), mdfEdgeColor, isDoubleSided = true, renderLayer = 1))
        polygons.add(Polygon3D(listOf(Vector3D(65f, yTop, 15f), Vector3D(52f, yTop, 15f), Vector3D(52f, yBottom, 15f), Vector3D(65f, yBottom, 15f)), mdfEdgeColor, isDoubleSided = true, renderLayer = 1))

        // BAND 4B: Arduino Uno Arka Bölgesi (z: +15f .. +55f, x: -65f .. +65f)
        addChassisPlate(
            polygons = polygons,
            vTop = listOf(
                Vector3D(-65f, yTop, 15f),
                Vector3D(65f, yTop, 15f),
                Vector3D(65f, yTop, 55f),
                Vector3D(-65f, yTop, 55f)
            ),
            vBottom = listOf(
                Vector3D(-65f, yBottom, 15f),
                Vector3D(65f, yBottom, 15f),
                Vector3D(65f, yBottom, 55f),
                Vector3D(-65f, yBottom, 55f)
            ),
            edges = listOf(
                Pair(1, 2), // Sağ kenar
                Pair(3, 0)  // Sol kenar
            ),
            topColor = mdfTopColor, bottomColor = mdfBottomColor, edgeColor = mdfEdgeColor
        )

        // BAND 5: 18650 Pil Bölgesi (z: +55f .. +95f, x: -65f .. +65f)
        addChassisPlate(
            polygons = polygons,
            vTop = listOf(
                Vector3D(-65f, yTop, 55f),
                Vector3D(65f, yTop, 55f),
                Vector3D(65f, yTop, 95f),
                Vector3D(-65f, yTop, 95f)
            ),
            vBottom = listOf(
                Vector3D(-65f, yBottom, 55f),
                Vector3D(65f, yBottom, 55f),
                Vector3D(65f, yBottom, 95f),
                Vector3D(-65f, yBottom, 95f)
            ),
            edges = listOf(
                Pair(1, 2), // Sağ kenar
                Pair(3, 0)  // Sol kenar
            ),
            topColor = mdfTopColor, bottomColor = mdfBottomColor, edgeColor = mdfEdgeColor
        )

        // BAND 6: Arka Sarhoş Tekerlek Kuyruğu (z: +95f .. +115f, x: -35f .. +35f)
        addChassisPlate(
            polygons = polygons,
            vTop = listOf(
                Vector3D(-65f, yTop, 95f),
                Vector3D(65f, yTop, 95f),
                Vector3D(35f, yTop, 115f),
                Vector3D(-35f, yTop, 115f)
            ),
            vBottom = listOf(
                Vector3D(-65f, yBottom, 95f),
                Vector3D(65f, yBottom, 95f),
                Vector3D(35f, yBottom, 115f),
                Vector3D(-35f, yBottom, 115f)
            ),
            edges = listOf(
                Pair(1, 2), // Sağ arka kavis
                Pair(2, 3), // En arka kenar
                Pair(3, 0)  // Sol arka kavis
            ),
            topColor = mdfTopColor, bottomColor = mdfBottomColor, edgeColor = mdfEdgeColor
        )

        // Şasi Üzerindeki Montaj Vidaları
        val screwColor = Color(0xFFCBD5E1)
        val screwPositions = listOf(
            Vector3D(-20f, yTop - 0.6f, -85f),
            Vector3D(20f, yTop - 0.6f, -85f),
            Vector3D(-24f, yTop - 0.6f, 50f),
            Vector3D(24f, yTop - 0.6f, 50f),
            Vector3D(0f, yTop - 0.6f, 65f)
        )
        for (sp in screwPositions) {
            add3DBox(polygons, cx = sp.x, cy = sp.y, cz = sp.z, width = 3.5f, height = 0.8f, depth = 3.5f, color = screwColor, renderLayer = 1)
        }
    }

    fun buildStandalone(): List<Polygon3D> {
        val list = ArrayList<Polygon3D>(80)
        build(list)
        return list
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Composable
fun MdfChassis3DView(modifier: Modifier = Modifier) {
    val polygons = MdfChassis3D.buildStandalone()
    Part3DPreviewBox(
        title = "MDF Ahşap Şasi",
        subtitle = "Lazer Kesim Çift Katman & Montaj Vidaları",
        polygons = polygons,
        modifier = modifier,
        yaw = -30f,
        pitch = 35f,
        zoom = 1.9f
    )
}

@Preview
@Composable
private fun MdfChassis3DPreview() {
    MdfChassis3DView()
}
