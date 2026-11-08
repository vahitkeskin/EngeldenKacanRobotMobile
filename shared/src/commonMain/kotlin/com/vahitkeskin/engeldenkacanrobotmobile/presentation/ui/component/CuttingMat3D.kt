package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Polygon3D
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Vector3D
import kotlin.math.roundToInt

/**
 * 🟩 CuttingMat3D
 * Gerçek A3 (45cm x 30cm) Yeşil Çalışma & Kesim Matı:
 * - 45cm x 30cm (1.5:1 A3 oranı) kavisli 4 köşeli yeşil zemin tablası (Layer -1)
 * - 1cm aralıklı nane yeşili kılavuz çizgileri, 5cm aralıklı ana ızgaralar (Layer 0)
 * - Milimetrik cetvel çerçevesi ve kenar çentikleri (ticks) (Layer 0)
 * - Sağ üst köşede fotoğraftaki gibi özel "A3" kılavuz kutusu ve A3 logosu (Layer 0)
 * - Alttan bakışta robotun altını %100 berrak gösteren akıllı yarı saydamlık (alpha = 0.22f)
 * - 360° dönüş ve kamera hareketinde Z-fighting / bozulmayı engelleyen katmanlı mimari
 */
object CuttingMat3D {

    fun build(
        polygons: ArrayList<Polygon3D>,
        isDark: Boolean = true,
        cameraPitch: Float = 32f
    ) {
        val ySurface = 28f // Tekerleklerin ve bilyenin oturduğu üst zemin seviyesi
        val yLine = 27.95f // Çizgilerin yüzeyin hemen üzerindeki seviyesi

        val isLookingFromBelow = cameraPitch < -5f
        // Alttan bakışta robotun altı tam görünsün diye şeffaf (0.22f), üstten bakışta zengin ve dolgun (0.90f - 0.94f)
        val surfaceAlpha = if (isLookingFromBelow) 0.22f else (if (isDark) 0.90f else 0.94f)
        val rimAlpha = if (isLookingFromBelow) 0.28f else 0.95f
        val rulerAlpha = if (isLookingFromBelow) 0.40f else 0.95f
        val majorGridAlpha = if (isLookingFromBelow) 0.45f else 0.85f
        val minorGridAlpha = if (isLookingFromBelow) 0.35f else 0.65f
        val a3BoxAlpha = if (isLookingFromBelow) 0.40f else 0.80f

        // Fotoğraftaki gerçek A3 kesme matı yeşili
        val matGreen = (if (isDark) Color(0xFF0F5132) else Color(0xFF15803D)).copy(alpha = surfaceAlpha)
        val matRimGreen = (if (isDark) Color(0xFF0A3622) else Color(0xFF166534)).copy(alpha = rimAlpha)
        val borderWhite = (if (isDark) Color(0xFFF0FDF4) else Color(0xFFFFFFFF)).copy(alpha = rulerAlpha)
        val majorGridColor = (if (isDark) Color(0xFFD1FAE5) else Color(0xFFECFDF5)).copy(alpha = majorGridAlpha)
        val minorGridColor = (if (isDark) Color(0xFF6EE7B7) else Color(0xFF86EFAC)).copy(alpha = minorGridAlpha)
        val a3BoxColor = (if (isDark) Color(0xFF6EE7B7) else Color(0xFF86EFAC)).copy(alpha = a3BoxAlpha)

        // A3 Ölçüleri: 45 cm genişlik (X) x 30 cm derinlik (Z) -> 450f x 300f
        val matWidth = 450f
        val matDepth = 300f
        val halfW = matWidth / 2f // 225f
        val minX = -halfW // -225f
        val maxX = halfW  // +225f

        val centerZ = -10f // Robotun ağırlık merkezi
        val halfD = matDepth / 2f // 150f
        val minZ = centerZ - halfD // -160f
        val maxZ = centerZ + halfD // +140f

        val cornerR = 14f // Kavisli köşe yarıçapı (~1.4 cm)

        // ==========================================
        // 1. A3 MAT TABAN YÜZEYİ (Kavisli 4 Köşe - Layer -1)
        // ==========================================
        // 8 köşeli yuvarlatılmış poligon: Mat yüzeyi ilk çizilir, asla çizgileri örtmez!
        polygons.add(
            Polygon3D(
                vertices = listOf(
                    Vector3D(minX + cornerR, ySurface, minZ),
                    Vector3D(maxX - cornerR, ySurface, minZ),
                    Vector3D(maxX, ySurface, minZ + cornerR),
                    Vector3D(maxX, ySurface, maxZ - cornerR),
                    Vector3D(maxX - cornerR, ySurface, maxZ),
                    Vector3D(minX + cornerR, ySurface, maxZ),
                    Vector3D(minX, ySurface, maxZ - cornerR),
                    Vector3D(minX, ySurface, minZ + cornerR)
                ),
                color = matGreen,
                strokeColor = matRimGreen,
                strokeWidth = 2.0f,
                isDoubleSided = true,
                renderLayer = -1
            )
        )

        // ==========================================
        // 2. MİLİMETRİK ÇALIŞMA ALANI & CETVEL ÇERÇEVESİ (Layer 0)
        // ==========================================
        val margin = 15f // 1.5 cm kenar payı
        val gridMinX = minX + margin // -210f (42 cm)
        val gridMaxX = maxX - margin // +210f
        val gridMinZ = minZ + margin // -145f (27 cm)
        val gridMaxZ = maxZ - margin // +125f

        // Dış Çerçeve Sınır Çizgileri
        polygons.add(Polygon3D(listOf(Vector3D(gridMinX, yLine, gridMinZ), Vector3D(gridMaxX, yLine, gridMinZ)), borderWhite, strokeWidth = 1.8f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(gridMaxX, yLine, gridMinZ), Vector3D(gridMaxX, yLine, gridMaxZ)), borderWhite, strokeWidth = 1.8f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(gridMaxX, yLine, gridMaxZ), Vector3D(gridMinX, yLine, gridMaxZ)), borderWhite, strokeWidth = 1.8f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(gridMinX, yLine, gridMaxZ), Vector3D(gridMinX, yLine, gridMinZ)), borderWhite, strokeWidth = 1.8f, isDoubleSided = true, renderLayer = 0))

        // Cetvel Kenar Çentikleri (Ticks - 1cm ve 5cm)
        val step = 10f // 1 cm = 10 units
        val xCount = ((gridMaxX - gridMinX) / step).roundToInt()
        for (i in 0..xCount) {
            val gx = gridMinX + i * step
            val isMajor5 = (i % 5 == 0)
            val isMajor10 = (i % 10 == 0)
            val tickLen = if (isMajor10) 5.5f else if (isMajor5) 3.5f else 2.0f
            val tickColor = if (isMajor5) borderWhite else minorGridColor

            // Üst Cetvel Çentiği
            polygons.add(Polygon3D(listOf(Vector3D(gx, yLine, gridMinZ), Vector3D(gx, yLine, gridMinZ - tickLen)), tickColor, strokeWidth = 1.2f, isDoubleSided = true, renderLayer = 0))
            // Alt Cetvel Çentiği
            polygons.add(Polygon3D(listOf(Vector3D(gx, yLine, gridMaxZ), Vector3D(gx, yLine, gridMaxZ + tickLen)), tickColor, strokeWidth = 1.2f, isDoubleSided = true, renderLayer = 0))
        }

        val zCount = ((gridMaxZ - gridMinZ) / step).roundToInt()
        for (j in 0..zCount) {
            val gz = gridMinZ + j * step
            val isMajor5 = (j % 5 == 0)
            val isMajor10 = (j % 10 == 0)
            val tickLen = if (isMajor10) 5.5f else if (isMajor5) 3.5f else 2.0f
            val tickColor = if (isMajor5) borderWhite else minorGridColor

            // Sol Cetvel Çentiği
            polygons.add(Polygon3D(listOf(Vector3D(gridMinX, yLine, gz), Vector3D(gridMinX - tickLen, yLine, gz)), tickColor, strokeWidth = 1.2f, isDoubleSided = true, renderLayer = 0))
            // Sağ Cetvel Çentiği
            polygons.add(Polygon3D(listOf(Vector3D(gridMaxX, yLine, gz), Vector3D(gridMaxX + tickLen, yLine, gz)), tickColor, strokeWidth = 1.2f, isDoubleSided = true, renderLayer = 0))
        }

        // ==========================================
        // 3. 1 CM & 5 CM IZGARA ÇİZGİLERİ (Layer 0)
        // ==========================================
        // Dikey Çizgiler (X ekseni boyunca)
        for (i in 1 until xCount) {
            val gx = gridMinX + i * step
            val isMajor = (i % 5 == 0)
            val lineColor = if (isMajor) majorGridColor else minorGridColor
            val width = if (isMajor) 1.4f else 0.8f
            polygons.add(
                Polygon3D(
                    vertices = listOf(Vector3D(gx, yLine, gridMinZ), Vector3D(gx, yLine, gridMaxZ)),
                    color = lineColor,
                    strokeColor = lineColor,
                    strokeWidth = width,
                    isDoubleSided = true,
                    renderLayer = 0
                )
            )
        }

        // Yatay Çizgiler (Z ekseni boyunca)
        for (j in 1 until zCount) {
            val gz = gridMinZ + j * step
            val isMajor = (j % 5 == 0)
            val lineColor = if (isMajor) majorGridColor else minorGridColor
            val width = if (isMajor) 1.4f else 0.8f
            polygons.add(
                Polygon3D(
                    vertices = listOf(Vector3D(gridMinX, yLine, gz), Vector3D(gridMaxX, yLine, gz)),
                    color = lineColor,
                    strokeColor = lineColor,
                    strokeWidth = width,
                    isDoubleSided = true,
                    renderLayer = 0
                )
            )
        }

        // ==========================================
        // 4. SAĞ ÜST KÖŞEDEKİ "A3" KILAVUZ KUTUSU & "A3" LOGOSU (Layer 0)
        // ==========================================
        // Fotoğraftaki gibi sağ üst köşede yer alan kağıt boyutu kılavuzu
        val a3BoxMinX = 130f
        val a3BoxMaxX = 200f
        val a3BoxMinZ = -135f
        val a3BoxMaxZ = -65f

        // A3 Kılavuz Çerçevesi
        polygons.add(Polygon3D(listOf(Vector3D(a3BoxMinX, yLine, a3BoxMinZ), Vector3D(a3BoxMaxX, yLine, a3BoxMinZ)), a3BoxColor, strokeWidth = 1.4f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(a3BoxMaxX, yLine, a3BoxMinZ), Vector3D(a3BoxMaxX, yLine, a3BoxMaxZ)), a3BoxColor, strokeWidth = 1.4f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(a3BoxMaxX, yLine, a3BoxMaxZ), Vector3D(a3BoxMinX, yLine, a3BoxMaxZ)), a3BoxColor, strokeWidth = 1.4f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(a3BoxMinX, yLine, a3BoxMaxZ), Vector3D(a3BoxMinX, yLine, a3BoxMinZ)), a3BoxColor, strokeWidth = 1.4f, isDoubleSided = true, renderLayer = 0))

        // "A3" Metin Logosu (Vektörel Çizim)
        val ax = 175f
        val az = -118f
        // 'A' Harfi
        polygons.add(Polygon3D(listOf(Vector3D(ax - 5f, yLine, az + 6f), Vector3D(ax - 2f, yLine, az - 6f)), a3BoxColor, strokeWidth = 1.5f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(ax - 2f, yLine, az - 6f), Vector3D(ax + 2f, yLine, az - 6f)), a3BoxColor, strokeWidth = 1.5f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(ax + 2f, yLine, az - 6f), Vector3D(ax + 5f, yLine, az + 6f)), a3BoxColor, strokeWidth = 1.5f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(ax - 3.5f, yLine, az), Vector3D(ax + 3.5f, yLine, az)), a3BoxColor, strokeWidth = 1.5f, isDoubleSided = true, renderLayer = 0))

        // '3' Rakamı
        val bx = ax + 12f
        polygons.add(Polygon3D(listOf(Vector3D(bx - 4f, yLine, az - 6f), Vector3D(bx + 4f, yLine, az - 6f)), a3BoxColor, strokeWidth = 1.5f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(bx + 4f, yLine, az - 6f), Vector3D(bx + 4f, yLine, az - 0.5f)), a3BoxColor, strokeWidth = 1.5f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(bx - 1f, yLine, az - 0.5f), Vector3D(bx + 4f, yLine, az - 0.5f)), a3BoxColor, strokeWidth = 1.5f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(bx + 4f, yLine, az - 0.5f), Vector3D(bx + 4f, yLine, az + 6f)), a3BoxColor, strokeWidth = 1.5f, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(bx - 4f, yLine, az + 6f), Vector3D(bx + 4f, yLine, az + 6f)), a3BoxColor, strokeWidth = 1.5f, isDoubleSided = true, renderLayer = 0))

        // ==========================================
        // 5. 45° DİYAGONAL AÇI ÇİZGİLERİ (Teknik Kesme Matı Kılavuzları)
        // ==========================================
        val diagColor = minorGridColor.copy(alpha = minorGridColor.alpha * 0.7f)
        polygons.add(
            Polygon3D(
                listOf(Vector3D(gridMinX, yLine, gridMaxZ), Vector3D(gridMinX + 270f, yLine, gridMinZ)),
                diagColor,
                strokeWidth = 1.0f,
                isDoubleSided = true,
                renderLayer = 0
            )
        )
        polygons.add(
            Polygon3D(
                listOf(Vector3D(gridMaxX, yLine, gridMaxZ), Vector3D(gridMaxX - 270f, yLine, gridMinZ)),
                diagColor,
                strokeWidth = 1.0f,
                isDoubleSided = true,
                renderLayer = 0
            )
        )
    }

    fun buildStandalone(isDark: Boolean = true): List<Polygon3D> {
        val list = ArrayList<Polygon3D>(150)
        build(list, isDark = isDark, cameraPitch = 35f)
        return list
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Composable
fun CuttingMat3DView(
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    val polygons = CuttingMat3D.buildStandalone(isDark = isDark)
    Part3DPreviewBox(
        title = "A3 Zemin Kesim Matı (45cm x 30cm)",
        subtitle = "Milimetrik Cetvel & Izgara Koordinatları",
        polygons = polygons,
        modifier = modifier,
        yaw = -30f,
        pitch = 45f,
        zoom = 1.3f
    )
}

@Preview
@Composable
private fun CuttingMat3DPreview() {
    CuttingMat3DView()
}
