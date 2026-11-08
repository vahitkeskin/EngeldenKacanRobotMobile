package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d

import androidx.compose.ui.graphics.Color
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.WheelDirection
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🛠️ Robot3DModelBuilder
 * Kullanıcının fiziksel robotunun fotoğraflarından esinlenilmiş,
 * tüm parçalarıyla (MDF ahşap şasi, tekerlekler, SG90 servo motor, HC-SR04 ultrasonik sensör,
 * L298N, Arduino Uno R3, 18650 piller, TT motorlar) ultra gerçekçi ve kusursuz 3D poligon modelini inşa eder.
 */
object Robot3DModelBuilder {

    fun buildRobotModel(
        telemetry: RobotTelemetry,
        leftWheelPhase: Float,
        rightWheelPhase: Float,
        sonarWavePhase: Float,
        isDark: Boolean = true,
        cameraPitch: Float = 32f
    ): List<Polygon3D> {
        val polygons = ArrayList<Polygon3D>(950)

        // 1. Zemin Kesim Matı (Layer 0 - Arka plan, robot parçalarını asla örtmez)
        buildGreenCuttingMat(polygons, isDark, cameraPitch)

        // 2. MDF Ahşap Şasi (Layer 1 - Katı Robot Modeli)
        buildMdfChassis(polygons)

        // 3. Alt Gövde: Sarı TT Motor Gövdeleri, Çelik DC Motorlar, Terminal Tabları, Kablolar (Layer 1)
        buildUndersideComponents(polygons)

        // 4. Arka Sarhoş Bilye Tekerlek (Layer 1)
        buildCasterWheel(polygons)

        // 5. Sol ve Sağ 32-Segment Yuvarlak Lastikler ve Sarı TT Jantlar (Layer 1)
        buildWheels(polygons, telemetry, leftWheelPhase, rightWheelPhase)

        // 6. Gövde Elektroniği: L298N Sürücü, Arduino Uno R3 (Teal PCB, ATmega328P, USB-B, DC Jack), 18650 Piller (Layer 1)
        buildElectronics(polygons, telemetry)

        // 7. Elektronik Jumper Kablo Demeti & Siyah Kelepçe (Layer 1)
        buildJumperWireHarness(polygons)

        // 8. SG90 Mikro Servo & Dönen HC-SR04 Ultrasonik Başlık (Layer 1) ve Canlı Sonar Işını (Layer 2)
        buildServoAndUltrasonicHead(polygons, telemetry, sonarWavePhase)

        return polygons
    }

    // -------------------------------------------------------------------------
    // 1. Zemin Kesim Matı (Layer 0 - Arka Plan)
    // -------------------------------------------------------------------------
    private fun buildGreenCuttingMat(polygons: ArrayList<Polygon3D>, isDark: Boolean, cameraPitch: Float) {
        val ySurface = 28f // Tekerleklerin ve bilyenin oturduğu üst zemin seviyesi
        val yBottom = 30.5f

        val isLookingFromBelow = cameraPitch < -5f
        val surfaceAlpha = if (isLookingFromBelow) 0.20f else 0.88f
        val edgeAlpha = if (isLookingFromBelow) 0.25f else 0.95f
        val gridAlpha = if (isLookingFromBelow) 0.40f else 0.70f
        val accentAlpha = if (isLookingFromBelow) 0.55f else 0.90f

        val matGreen = (if (isDark) Color(0xFF14532D) else Color(0xFF2E9450)).copy(alpha = surfaceAlpha)
        val edgeDarkGreen = (if (isDark) Color(0xFF0D361D) else Color(0xFF1B6334)).copy(alpha = edgeAlpha)
        val borderWhite = (if (isDark) Color(0xFF99F6E4) else Color(0xFFE2E8F0)).copy(alpha = accentAlpha)
        val gridMint = (if (isDark) Color(0xFF6EE7B7) else Color(0xFF86EFAC)).copy(alpha = gridAlpha)
        val accentWhite = (if (isDark) Color(0xFFE2E8F0) else Color(0xFFFFFFFF)).copy(alpha = accentAlpha)

        val step = 30f
        val halfSquares = 5
        val minX = -halfSquares * step // -150f
        val maxX = halfSquares * step  // +150f
        val centerZ = -10f
        val minZ = centerZ - halfSquares * step // -160f
        val maxZ = centerZ + halfSquares * step // +140f

        val pad = 15f
        val matMinX = minX - pad
        val matMaxX = maxX + pad
        val matMinZ = minZ - pad
        val matMaxZ = maxZ + pad

        // Mat Üst Yüzeyi
        polygons.add(
            Polygon3D(
                vertices = listOf(
                    Vector3D(matMinX, ySurface, matMinZ),
                    Vector3D(matMaxX, ySurface, matMinZ),
                    Vector3D(matMaxX, ySurface, matMaxZ),
                    Vector3D(matMinX, ySurface, matMaxZ)
                ),
                color = matGreen,
                strokeColor = edgeDarkGreen,
                strokeWidth = 1f,
                isDoubleSided = true,
                renderLayer = 0
            )
        )

        // Mat Alt Yüzeyi
        polygons.add(
            Polygon3D(
                vertices = listOf(
                    Vector3D(matMinX, yBottom, matMaxZ),
                    Vector3D(matMaxX, yBottom, matMaxZ),
                    Vector3D(matMaxX, yBottom, matMinZ),
                    Vector3D(matMinX, yBottom, matMinZ)
                ),
                color = edgeDarkGreen,
                isDoubleSided = true,
                renderLayer = 0
            )
        )

        // Mat Yan Kenarları
        polygons.add(Polygon3D(listOf(Vector3D(matMinX, ySurface, matMinZ), Vector3D(matMaxX, ySurface, matMinZ), Vector3D(matMaxX, yBottom, matMinZ), Vector3D(matMinX, yBottom, matMinZ)), edgeDarkGreen, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(matMaxX, ySurface, matMinZ), Vector3D(matMaxX, ySurface, matMaxZ), Vector3D(matMaxX, yBottom, matMaxZ), Vector3D(matMaxX, yBottom, matMinZ)), edgeDarkGreen, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(matMaxX, ySurface, matMaxZ), Vector3D(matMinX, ySurface, matMaxZ), Vector3D(matMinX, yBottom, matMaxZ), Vector3D(matMaxX, yBottom, matMaxZ)), edgeDarkGreen, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(matMinX, ySurface, matMaxZ), Vector3D(matMinX, ySurface, matMinZ), Vector3D(matMinX, yBottom, matMinZ), Vector3D(matMinX, yBottom, matMaxZ)), edgeDarkGreen, isDoubleSided = true, renderLayer = 0))

        // Dış Beyaz Çerçeve Cetveli
        val borderY = ySurface - 0.1f
        val borderThick = 2.5f
        polygons.add(Polygon3D(listOf(Vector3D(minX, borderY, minZ), Vector3D(maxX, borderY, minZ), Vector3D(maxX, borderY, minZ + borderThick), Vector3D(minX, borderY, minZ + borderThick)), borderWhite, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(minX, borderY, maxZ - borderThick).let { listOf(Vector3D(minX, borderY, maxZ - borderThick), Vector3D(maxX, borderY, maxZ - borderThick), Vector3D(maxX, borderY, maxZ), Vector3D(minX, borderY, maxZ)) }, borderWhite, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(minX, borderY, minZ), Vector3D(minX + borderThick, borderY, minZ), Vector3D(minX + borderThick, borderY, maxZ), Vector3D(minX, borderY, maxZ)), borderWhite, isDoubleSided = true, renderLayer = 0))
        polygons.add(Polygon3D(listOf(Vector3D(maxX - borderThick, borderY, minZ), Vector3D(maxX, borderY, minZ), Vector3D(maxX, borderY, maxZ), Vector3D(maxX - borderThick, borderY, maxZ)), borderWhite, isDoubleSided = true, renderLayer = 0))

        // Izgara Çizgileri
        val lineY = ySurface - 0.15f
        for (i in 0..10) {
            val gx = minX + i * step
            val isMajor = (i == 0 || i == 5 || i == 10)
            val thickness = if (isMajor) 1.2f else 0.6f
            polygons.add(
                Polygon3D(
                    vertices = listOf(
                        Vector3D(gx - thickness / 2f, lineY, minZ),
                        Vector3D(gx + thickness / 2f, lineY, minZ),
                        Vector3D(gx + thickness / 2f, lineY, maxZ),
                        Vector3D(gx - thickness / 2f, lineY, maxZ)
                    ),
                    color = if (isMajor) accentWhite else gridMint,
                    isDoubleSided = true,
                    renderLayer = 0
                )
            )
        }
        for (j in 0..10) {
            val gz = minZ + j * step
            val isMajor = (j == 0 || j == 5 || j == 10)
            val thickness = if (isMajor) 1.2f else 0.6f
            polygons.add(
                Polygon3D(
                    vertices = listOf(
                        Vector3D(minX, lineY, gz - thickness / 2f),
                        Vector3D(maxX, lineY, gz - thickness / 2f),
                        Vector3D(maxX, lineY, gz + thickness / 2f),
                        Vector3D(minX, lineY, gz + thickness / 2f)
                    ),
                    color = if (isMajor) accentWhite else gridMint,
                    isDoubleSided = true,
                    renderLayer = 0
                )
            )
        }
    }

    // -------------------------------------------------------------------------
    // 2. MDF Ahşap Şasi (Layer 1 - Katı Robot Modeli, Segmentli Bantlar)
    // -------------------------------------------------------------------------
    private fun buildMdfChassis(polygons: ArrayList<Polygon3D>) {
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

    private fun addChassisPlate(
        polygons: ArrayList<Polygon3D>,
        vTop: List<Vector3D>,
        vBottom: List<Vector3D>,
        edges: List<Pair<Int, Int>>,
        topColor: Color,
        bottomColor: Color,
        edgeColor: Color
    ) {
        polygons.add(Polygon3D(vTop, topColor, strokeColor = edgeColor, strokeWidth = 0.8f, isDoubleSided = true, renderLayer = 1))
        polygons.add(Polygon3D(vBottom, bottomColor, strokeColor = edgeColor, strokeWidth = 0.8f, isDoubleSided = true, renderLayer = 1))
        for (edge in edges) {
            val i1 = edge.first
            val i2 = edge.second
            polygons.add(
                Polygon3D(
                    vertices = listOf(vTop[i1], vTop[i2], vBottom[i2], vBottom[i1]),
                    color = edgeColor,
                    isDoubleSided = true,
                    renderLayer = 1
                )
            )
        }
    }

    // -------------------------------------------------------------------------
    // 3. Alt Gövde: Sarı TT Motorlar, Çelik DC Motor Gövdeleri, Kablolar (Layer 1)
    // -------------------------------------------------------------------------
    private fun buildUndersideComponents(polygons: ArrayList<Polygon3D>) {
        val yellowMotorColor = Color(0xFFF59E0B) // Sarı plastik TT dişli kutusu
        val yellowTabColor = Color(0xFFD97706)
        val steelMotorColor = Color(0xFF94A3B8)  // Çelik 130 DC motor silindiri
        val endCapColor = Color(0xFF334155)     // Arka plastik kapak
        val lugBrassColor = Color(0xFFD97706)   // Pirinç lehim terminali
        val whiteShaftColor = Color(0xFFF8FAFC) // Beyaz aks D-şaft
        val redWireColor = Color(0xFFDC2626)
        val blackWireColor = Color(0xFF0F172A)
        val boltColor = Color(0xFFCBD5E1)

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
        add3DBox(polygons, cx = 0f, cy = 8f, cz = -105f, width = 20f, height = 8f, depth = 12f, color = Color(0xEE1E40AF), renderLayer = 1)
    }

    // -------------------------------------------------------------------------
    // 4. Arka Sarhoş Bilye Tekerlek (Layer 1)
    // -------------------------------------------------------------------------
    private fun buildCasterWheel(polygons: ArrayList<Polygon3D>) {
        val cx = 0f
        val cz = 95f
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

    // -------------------------------------------------------------------------
    // 5. Sol ve Sağ 3D Tekerlekler (Layer 1 - 32 Segment Yuvarlak Kauçuk Lastik)
    // -------------------------------------------------------------------------
    private fun buildWheels(
        polygons: ArrayList<Polygon3D>,
        telemetry: RobotTelemetry,
        leftWheelPhase: Float,
        rightWheelPhase: Float
    ) {
        val leftX = -72f
        val rightX = 72f
        val wheelY = 6f      // radius = 22f ile 6 + 22 = 28f (Zemine tam temas)
        val wheelZ = -18f    // TT Motor aksı ile tam hizalı
        val wheelRadius = 22f
        val wheelThickness = 16f

        val leftState = telemetry.leftWheelState
        val rightState = telemetry.rightWheelState

        buildSingle3DWheel(polygons, cx = leftX, cy = wheelY, cz = wheelZ, radius = wheelRadius, thickness = wheelThickness, isLeft = true, direction = leftState.direction, phase = leftWheelPhase)
        buildSingle3DWheel(polygons, cx = rightX, cy = wheelY, cz = wheelZ, radius = wheelRadius, thickness = wheelThickness, isLeft = false, direction = rightState.direction, phase = rightWheelPhase)
    }

    private fun buildSingle3DWheel(
        polygons: ArrayList<Polygon3D>,
        cx: Float, cy: Float, cz: Float,
        radius: Float, thickness: Float,
        isLeft: Boolean,
        direction: WheelDirection,
        phase: Float
    ) {
        val segments = 32
        val tireColor = Color(0xFF1E293B)
        val tireDarkColor = Color(0xFF0F172A)
        val hubColor = Color(0xFFFACC15)
        val hubBorderColor = Color(0xFFEAB308)
        val axleColor = Color(0xFF0F172A)

        val halfT = thickness / 2f
        val outerX = if (isLeft) cx - halfT else cx + halfT
        val innerX = if (isLeft) cx + halfT else cx - halfT

        val hubRadius = radius * 0.73f
        val axleRadius = radius * 0.22f
        val spinRad = phase * (PI.toFloat() / 180f)

        val outerTirePts = ArrayList<Vector3D>(segments)
        val outerHubPts = ArrayList<Vector3D>(segments)
        val outerAxlePts = ArrayList<Vector3D>(segments)
        val innerTirePts = ArrayList<Vector3D>(segments)

        for (i in 0 until segments) {
            val angle = (i * (2f * PI.toFloat() / segments)) + spinRad
            val cosA = cos(angle)
            val sinA = sin(angle)

            outerTirePts.add(Vector3D(outerX, cy + radius * cosA, cz + radius * sinA))
            outerHubPts.add(Vector3D(outerX, cy + hubRadius * cosA, cz + hubRadius * sinA))
            outerAxlePts.add(Vector3D(outerX, cy + axleRadius * cosA, cz + axleRadius * sinA))
            innerTirePts.add(Vector3D(innerX, cy + radius * cosA, cz + radius * sinA))
        }

        // 1. Dış Lastik Yanağı
        for (i in 0 until segments) {
            val next = (i + 1) % segments
            polygons.add(Polygon3D(listOf(outerTirePts[i], outerTirePts[next], outerHubPts[next], outerHubPts[i]), tireColor, isDoubleSided = true, renderLayer = 1))
        }

        // 2. Dış Sarı Jant
        for (i in 0 until segments) {
            val next = (i + 1) % segments
            polygons.add(Polygon3D(listOf(outerHubPts[i], outerHubPts[next], outerAxlePts[next], outerAxlePts[i]), hubColor, strokeColor = hubBorderColor, strokeWidth = 0.4f, isDoubleSided = true, renderLayer = 1))
        }

        // 3. Dış Merkez Aks Kapağı
        polygons.add(Polygon3D(outerAxlePts, axleColor, isDoubleSided = true, renderLayer = 1))

        // 4. Lastik Sırtı (32 Eksenli Pürüzsüz Yuvarlak Çevre)
        for (i in 0 until segments) {
            val next = (i + 1) % segments
            val treadTone = if (i % 2 == 0) tireColor else tireDarkColor
            polygons.add(Polygon3D(listOf(outerTirePts[i], outerTirePts[next], innerTirePts[next], innerTirePts[i]), treadTone, strokeColor = Color(0xFF334155), strokeWidth = 0.3f, isDoubleSided = true, renderLayer = 1))
        }

        // 5. İç Yüzey
        polygons.add(Polygon3D(innerTirePts, tireColor, isDoubleSided = true, renderLayer = 1))

        // 6. D-Şaft Aks Başlığı
        add3DBox(polygons, cx = outerX + (if (isLeft) -1f else 1f), cy = cy, cz = cz, width = 2.5f, height = 6.5f, depth = 6.5f, color = axleColor, renderLayer = 1)
    }

    // -------------------------------------------------------------------------
    // 6. Gövde Elektroniği: L298N Sürücü, Arduino Uno R3, 18650 Piller (Layer 1)
    // -------------------------------------------------------------------------
    private fun buildElectronics(polygons: ArrayList<Polygon3D>, telemetry: RobotTelemetry) {
        // A. L298N MOTOR SÜRÜCÜSÜ (Kırmızı Kart, Siyah Kanatlı Soğutucu, Klemensler)
        val l298nZ = -35f
        add3DBox(polygons, cx = 0f, cy = -6f, cz = l298nZ, width = 50f, height = 4f, depth = 42f, color = Color(0xFFDC2626), renderLayer = 1)
        // Alüminyum Siyah Soğutucu Gövdesi ve Kanatçıkları
        add3DBox(polygons, cx = 0f, cy = -16f, cz = l298nZ, width = 28f, height = 16f, depth = 22f, color = Color(0xFF0F172A), renderLayer = 1)
        val finColor = Color(0xFF334155)
        for (finX in listOf(-9f, -3f, 3f, 9f)) {
            add3DBox(polygons, cx = finX, cy = -25f, cz = l298nZ, width = 2f, height = 4f, depth = 20f, color = finColor, renderLayer = 1)
        }
        // İki Adet Filtre Kondansatörü
        add3DCylinder(polygons, cx = -16f, cy = -12f, cz = l298nZ + 12f, radius = 4f, height = 10f, segments = 8, color = Color(0xFF0F172A), axis = CylinderAxis.Y, renderLayer = 1)
        add3DCylinder(polygons, cx = 16f, cy = -12f, cz = l298nZ + 12f, radius = 4f, height = 10f, segments = 8, color = Color(0xFF0F172A), axis = CylinderAxis.Y, renderLayer = 1)
        // Mavi Klemens Blokları ve Gümüş Vidaları
        val termBlue = Color(0xFF2563EB)
        val screwSilver = Color(0xFFCBD5E1)
        add3DBox(polygons, cx = -20f, cy = -10f, cz = l298nZ, width = 8f, height = 8f, depth = 14f, color = termBlue, renderLayer = 1)
        add3DBox(polygons, cx = 20f, cy = -10f, cz = l298nZ, width = 8f, height = 8f, depth = 14f, color = termBlue, renderLayer = 1)
        add3DBox(polygons, cx = 0f, cy = -10f, cz = l298nZ + 16f, width = 16f, height = 8f, depth = 8f, color = termBlue, renderLayer = 1)
        add3DBox(polygons, cx = -20f, cy = -14.5f, cz = l298nZ, width = 3f, height = 1f, depth = 3f, color = screwSilver, renderLayer = 1)
        add3DBox(polygons, cx = 20f, cy = -14.5f, cz = l298nZ, width = 3f, height = 1f, depth = 3f, color = screwSilver, renderLayer = 1)

        // B. ARDUINO UNO R3 (Otantik İtalyan Teal Kartı, DIP-28 ATmega328P, USB-B, DC Jack)
        val arduinoZ = 25f
        val brassStandoffColor = Color(0xFFD97706)
        val arduinoTealColor = Color(0xFF00878F) // Orijinal Arduino Teal / Cyan mavi
        val icColor = Color(0xFF0F172A)
        val icPinColor = Color(0xFFCBD5E1)
        val headerBlackColor = Color(0xFF1E293B)
        val usbSilverColor = Color(0xFFCBD5E1)
        val jackBlackColor = Color(0xFF0F172A)

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
        add3DBox(polygons, cx = icCx, cy = -13.5f, cz = icCz - 17f, width = 3f, height = 0.8f, depth = 1.5f, color = Color(0xFF334155), renderLayer = 1)
        // Entegrenin 28 Adet Gümüş Bacağı (14 Sol, 14 Sağ)
        add3DBox(polygons, cx = icCx - 5.5f, cy = -10f, cz = icCz, width = 1.5f, height = 2f, depth = 32f, color = icPinColor, renderLayer = 1)
        add3DBox(polygons, cx = icCx + 5.5f, cy = -10f, cz = icCz, width = 1.5f, height = 2f, depth = 32f, color = icPinColor, renderLayer = 1)

        // 4. 16.000 MHz Kristal Osilatör (Gümüş Metal Kutu)
        add3DBox(polygons, cx = -12f, cy = -10.5f, cz = arduinoZ - 12f, width = 7f, height = 3.5f, depth = 3.5f, color = Color(0xFFE2E8F0), renderLayer = 1)

        // 5. Gümüş Metal USB Type-B Portu & İç Kılavuz
        add3DBox(polygons, cx = -21f, cy = -13f, cz = arduinoZ - 26f, width = 11f, height = 9.5f, depth = 13f, color = usbSilverColor, renderLayer = 1)
        add3DBox(polygons, cx = -21f, cy = -13f, cz = arduinoZ - 32.6f, width = 6f, height = 5f, depth = 0.6f, color = Color(0xFF0F172A), renderLayer = 1)

        // 6. Siyah 2.1mm DC Güç Jackı & Merkez Pimi
        add3DBox(polygons, cx = -21f, cy = -13.5f, cz = arduinoZ + 24f, width = 10f, height = 10.5f, depth = 14f, color = jackBlackColor, renderLayer = 1)
        add3DBox(polygons, cx = -21f, cy = -13.5f, cz = arduinoZ + 31.2f, width = 2.2f, height = 2.2f, depth = 2f, color = screwSilver, renderLayer = 1)

        // 7. Kırmızı Reset Butonu
        add3DBox(polygons, cx = -21f, cy = -10f, cz = arduinoZ - 12f, width = 6f, height = 2f, depth = 6f, color = Color(0xFF94A3B8), renderLayer = 1)
        add3DBox(polygons, cx = -21f, cy = -12f, cz = arduinoZ - 12f, width = 3.5f, height = 2.5f, depth = 3.5f, color = Color(0xFFEF4444), renderLayer = 1)

        // 8. İki Yan Dişi Header Pin Soket Dizileri (Power/Analog & Digital)
        add3DBox(polygons, cx = -23.5f, cy = -12.5f, cz = arduinoZ + 2f, width = 4f, height = 7.5f, depth = 46f, color = headerBlackColor, renderLayer = 1)
        add3DBox(polygons, cx = 23.5f, cy = -12.5f, cz = arduinoZ + 2f, width = 4f, height = 7.5f, depth = 46f, color = headerBlackColor, renderLayer = 1)
        // Soket Üst Pin Yuvaları
        add3DBox(polygons, cx = -23.5f, cy = -16.4f, cz = arduinoZ + 2f, width = 2.2f, height = 0.4f, depth = 44f, color = Color(0xFF0F172A), renderLayer = 1)
        add3DBox(polygons, cx = 23.5f, cy = -16.4f, cz = arduinoZ + 2f, width = 2.2f, height = 0.4f, depth = 44f, color = Color(0xFF0F172A), renderLayer = 1)

        // 9. 2x3 ICSP Erkek Header Pinleri
        add3DBox(polygons, cx = icCx, cy = -11f, cz = arduinoZ + 28f, width = 6f, height = 4f, depth = 4f, color = Color(0xFFD97706), renderLayer = 1)

        // 10. Yeşil ON Güç LED'i ve Sarı L LED'i
        val powerLedColor = if (telemetry.hasSignal) Color(0xFF22C55E) else Color(0xFF14532D)
        add3DBox(polygons, cx = -10f, cy = -9.5f, cz = arduinoZ - 4f, width = 2.5f, height = 1.2f, depth = 2.5f, color = powerLedColor, renderLayer = 1)
        add3DBox(polygons, cx = 10f, cy = -9.5f, cz = arduinoZ - 4f, width = 2.5f, height = 1.2f, depth = 2.5f, color = Color(0xFFF59E0B), renderLayer = 1)

        // C. 2x 18650 LI-ION PİL YUVASI VE YEŞİL PİLLER (Layer 1)
        val batZ = 80f
        add3DBox(polygons, cx = 0f, cy = -8f, cz = batZ, width = 64f, height = 8f, depth = 34f, color = Color(0xFF0F172A), renderLayer = 1)
        // Ön ve Arka Yeşil Piller
        add3DCylinder(polygons, cx = 0f, cy = -14f, cz = batZ - 8f, radius = 6.5f, height = 50f, segments = 16, color = Color(0xFF22C55E), axis = CylinderAxis.X, renderLayer = 1)
        add3DCylinder(polygons, cx = 0f, cy = -14f, cz = batZ + 8f, radius = 6.5f, height = 50f, segments = 16, color = Color(0xFF22C55E), axis = CylinderAxis.X, renderLayer = 1)
        // Pozitif (+) Metal Uç Başlıkları ve Yalıtım Halkaları
        add3DCylinder(polygons, cx = -26f, cy = -14f, cz = batZ - 8f, radius = 2.5f, height = 3f, segments = 8, color = Color(0xFFE2E8F0), axis = CylinderAxis.X, renderLayer = 1)
        add3DCylinder(polygons, cx = -26f, cy = -14f, cz = batZ + 8f, radius = 2.5f, height = 3f, segments = 8, color = Color(0xFFE2E8F0), axis = CylinderAxis.X, renderLayer = 1)
        add3DCylinder(polygons, cx = -24.5f, cy = -14f, cz = batZ - 8f, radius = 6.6f, height = 1.5f, segments = 8, color = Color(0xFF0F172A), axis = CylinderAxis.X, renderLayer = 1)
        add3DCylinder(polygons, cx = -24.5f, cy = -14f, cz = batZ + 8f, radius = 6.6f, height = 1.5f, segments = 8, color = Color(0xFF0F172A), axis = CylinderAxis.X, renderLayer = 1)
        // Kırmızı & Siyah Besleme Telleri
        add3DBox(polygons, cx = -23f, cy = -6f, cz = batZ - 20f, width = 2f, height = 2f, depth = 22f, color = Color(0xFFDC2626), renderLayer = 1)
        add3DBox(polygons, cx = -26f, cy = -6f, cz = batZ - 20f, width = 2f, height = 2f, depth = 22f, color = Color(0xFF0F172A), renderLayer = 1)
    }

    // -------------------------------------------------------------------------
    // 7. Elektronik Jumper Kablo Demeti & Siyah Kelepçe (Layer 1)
    // -------------------------------------------------------------------------
    private fun buildJumperWireHarness(polygons: ArrayList<Polygon3D>) {
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

    // -------------------------------------------------------------------------
    // 8. SG90 Mikro Servo & Dönen HC-SR04 Ultrasonik Başlık (Layer 1) & Sonar (Layer 2)
    // -------------------------------------------------------------------------
    private fun buildServoAndUltrasonicHead(
        polygons: ArrayList<Polygon3D>,
        telemetry: RobotTelemetry,
        sonarWavePhase: Float
    ) {
        // SG90 Servo Pivot Konumu
        val servoPivot = Vector3D(0f, -28f, -107f)

        // A. SABİT SG90 MİKRO SERVO MOTOR GÖVDESİ (Şeffaf Mavi TowerPro Kasa)
        val servoBlueColor = Color(0xEE1E40AF)
        val servoLabelColor = Color(0xFFFACC15)
        val servoEarColor = Color(0xEE1E40AF)
        val brassGearColor = Color(0xFFD97706)
        val screwColor = Color(0xFFCBD5E1)

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
        add3DBox(polygons, cx = -2f, cy = -4f, cz = -105f, width = 1.8f, height = 2f, depth = 12f, color = Color(0xFF78350F), renderLayer = 1)
        add3DBox(polygons, cx = 0f, cy = -4f, cz = -105f, width = 1.8f, height = 2f, depth = 12f, color = Color(0xFFDC2626), renderLayer = 1)
        add3DBox(polygons, cx = 2f, cy = -4f, cz = -105f, width = 1.8f, height = 2f, depth = 12f, color = Color(0xFFF97316), renderLayer = 1)

        // B. DÖNEN KAFA MEKANİZMASI (0° .. 90° .. 180° SERVO AÇISI)
        val angleRad = (90 - telemetry.servoAngle) * (PI.toFloat() / 180f)

        // 1. Beyaz Naylon Servo Kolu (Horn) ve Merkez Vidası
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(0f, -30f, -107f), width = 6f, height = 2f, depth = 16f, color = Color(0xFFF8FAFC), angleRad = angleRad, renderLayer = 1)
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(0f, -31.2f, -107f), width = 2.5f, height = 0.8f, depth = 2.5f, color = screwColor, angleRad = angleRad, renderLayer = 1)

        // 2. Siyah Pleksi U-Braket (HC-SR04 Taşıyıcı Braket)
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(0f, -32.5f, -107f), width = 46f, height = 3f, depth = 8f, color = Color(0xFF0F172A), angleRad = angleRad, renderLayer = 1)
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(-21f, -39f, -107f), width = 3f, height = 11f, depth = 8f, color = Color(0xFF0F172A), angleRad = angleRad, renderLayer = 1)
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(21f, -39f, -107f), width = 3f, height = 11f, depth = 8f, color = Color(0xFF0F172A), angleRad = angleRad, renderLayer = 1)

        // C. HC-SR04 ULTRASONİK SENSÖRÜ (Kraliyet Mavisi PCB, 4 Köşe Delikleri, Kristal Osilatör, 4-Pin Header, 2x 24-Segment Göz)
        val pcbCenter = Vector3D(0f, -42f, -111f)
        val pcbBlueColor = Color(0xFF1565C0) // Parlak Kraliyet Mavisi PCB
        val holeCopperColor = Color(0xFFD97706)
        val crystalSilverColor = Color(0xFFCBD5E1)
        val pinGoldColor = Color(0xFFF59E0B)

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
        addRotatedBox(polygons, pivot = servoPivot, center = Vector3D(0f, -30f, -111f), width = 12f, height = 3.5f, depth = 3.5f, color = Color(0xFF0F172A), angleRad = angleRad, renderLayer = 1)
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
        // Kusursuz pürüzsüz yuvarlak dairesel alüminyum silindir için 24 radyal segment
        val segments = 24
        val silverAlumColor = Color(0xFFE2E8F0) // Fırçalanmış parlak alüminyum gövde
        val rimColor = Color(0xFFCBD5E1)        // Ön çerçeve bileziği
        val meshColor = Color(0xFF0F172A)       // Akustik siyah ızgara kapağı
        val piezoDotColor = Color(0xFFE2E8F0)   // Merkez piezo seramik sensör noktası

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
            color = Color(0xFFCBD5E1),
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
            distanceCm <= 15 -> Color(0xFFEF4444) // TEHLİKE / KIRMIZI
            distanceCm <= 30 -> Color(0xFFF59E0B) // UYARI / SARI
            else -> Color(0xFF10B981)             // GÜVENLİ / YEŞİL
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

    // -------------------------------------------------------------------------
    // Geometrik İlkel Fonksiyonlar (Primitives)
    // -------------------------------------------------------------------------
    private fun add3DBox(
        polygons: ArrayList<Polygon3D>,
        cx: Float, cy: Float, cz: Float,
        width: Float, height: Float, depth: Float,
        color: Color,
        renderLayer: Int = 1
    ) {
        val hw = width / 2f
        val hh = height / 2f
        val hd = depth / 2f

        val v0 = Vector3D(cx - hw, cy - hh, cz - hd)
        val v1 = Vector3D(cx + hw, cy - hh, cz - hd)
        val v2 = Vector3D(cx + hw, cy + hh, cz - hd)
        val v3 = Vector3D(cx - hw, cy + hh, cz - hd)
        val v4 = Vector3D(cx - hw, cy - hh, cz + hd)
        val v5 = Vector3D(cx + hw, cy - hh, cz + hd)
        val v6 = Vector3D(cx + hw, cy + hh, cz + hd)
        val v7 = Vector3D(cx - hw, cy + hh, cz + hd)

        polygons.add(Polygon3D(listOf(v0, v1, v2, v3), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v5, v4, v7, v6), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v4, v0, v3, v7), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v1, v5, v6, v2), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v4, v5, v1, v0), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v3, v2, v6, v7), color, isDoubleSided = true, renderLayer = renderLayer))
    }

    private fun addRotatedBox(
        polygons: ArrayList<Polygon3D>,
        pivot: Vector3D,
        center: Vector3D,
        width: Float, height: Float, depth: Float,
        color: Color,
        angleRad: Float,
        renderLayer: Int = 1
    ) {
        val hw = width / 2f
        val hh = height / 2f
        val hd = depth / 2f

        val raw = listOf(
            Vector3D(center.x - hw, center.y - hh, center.z - hd),
            Vector3D(center.x + hw, center.y - hh, center.z - hd),
            Vector3D(center.x + hw, center.y + hh, center.z - hd),
            Vector3D(center.x - hw, center.y + hh, center.z - hd),
            Vector3D(center.x - hw, center.y - hh, center.z + hd),
            Vector3D(center.x + hw, center.y - hh, center.z + hd),
            Vector3D(center.x + hw, center.y + hh, center.z + hd),
            Vector3D(center.x - hw, center.y + hh, center.z + hd)
        )

        val v = raw.map { pivot + (it - pivot).rotateY(angleRad) }

        polygons.add(Polygon3D(listOf(v[0], v[1], v[2], v[3]), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v[5], v[4], v[7], v[6]), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v[4], v[0], v[3], v[7]), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v[1], v[5], v[6], v[2]), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v[4], v[5], v[1], v[0]), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v[3], v[2], v[6], v[7]), color, isDoubleSided = true, renderLayer = renderLayer))
    }

    enum class CylinderAxis { X, Y, Z }

    private fun add3DCylinder(
        polygons: ArrayList<Polygon3D>,
        cx: Float, cy: Float, cz: Float,
        radius: Float, height: Float, segments: Int,
        color: Color,
        axis: CylinderAxis,
        renderLayer: Int = 1
    ) {
        val halfH = height / 2f
        val cap1 = ArrayList<Vector3D>(segments)
        val cap2 = ArrayList<Vector3D>(segments)

        for (i in 0 until segments) {
            val a = i * (2f * PI.toFloat() / segments)
            val d1 = radius * cos(a)
            val d2 = radius * sin(a)

            when (axis) {
                CylinderAxis.X -> {
                    cap1.add(Vector3D(cx - halfH, cy + d1, cz + d2))
                    cap2.add(Vector3D(cx + halfH, cy + d1, cz + d2))
                }
                CylinderAxis.Y -> {
                    cap1.add(Vector3D(cx + d1, cy - halfH, cz + d2))
                    cap2.add(Vector3D(cx + d1, cy + halfH, cz + d2))
                }
                CylinderAxis.Z -> {
                    cap1.add(Vector3D(cx + d1, cy + d2, cz - halfH))
                    cap2.add(Vector3D(cx + d1, cy + d2, cz + halfH))
                }
            }
        }

        polygons.add(Polygon3D(cap1, color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(cap2, color, isDoubleSided = true, renderLayer = renderLayer))

        for (i in 0 until segments) {
            val next = (i + 1) % segments
            polygons.add(
                Polygon3D(
                    vertices = listOf(cap1[i], cap1[next], cap2[next], cap2[i]),
                    color = color,
                    isDoubleSided = true,
                    renderLayer = renderLayer
                )
            )
        }
    }
}
