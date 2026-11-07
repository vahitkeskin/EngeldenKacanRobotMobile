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
 * tüm parçalarıyla (MDF ahşap şasi, tekerlekler, servo, HC-SR04 gözler,
 * L298N, Arduino, 18650 piller) tam 3D poligon modelini inşa eder.
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
        val polygons = ArrayList<Polygon3D>(280)

        // 1. PROFESYONEL VE DÜZGÜN KARELİ KESİM MATI (Alttan bakıldığında saydam, açık yeşil tonlu)
        buildGreenCuttingMat(polygons, isDark, cameraPitch)

        // 2. MDF LAZER KESİM AHŞAP ŞASİ (3D Extruded Plate + Screws)
        buildMdfChassis(polygons)

        // 3. ALT GÖVDE: Sarı TT Motor Gövdeleri, Aks Kuplörleri, Motor Telleri ve Servo Boşluğu
        buildUndersideComponents(polygons)

        // 4. ARKA SARHOŞ TEKERLEK (Caster Ball Wheel with Swivel Cup & Screws)
        buildCasterWheel(polygons)

        // 5. SOL VE SAĞ TT MOTORLAR VE LASTİKLER (Bağımsız Fazlar ile Gerçek Arduino Hız ve Yönü)
        buildWheels(polygons, telemetry, leftWheelPhase, rightWheelPhase)

        // 6. GÖVDE ELEKTRONİĞİ: L298N (Kanatlı Soğutucu, Kondansatörler), Arduino Uno + Shield (USB, Jack), 2x 18650 Piller
        buildElectronics(polygons, telemetry)

        // 7. ELEKTRONİK JUMPER KABLO DEMETİ & SİYAH KELEPÇE (Standart Karışık Donanım Kabloları)
        buildJumperWireHarness(polygons)

        // 8. SG90 MİKRO SERVO VE DÖNEN HC-SR04 GÖZLER (Kristal Osilatör, Jumper Uçları, T & R)
        buildServoAndUltrasonicHead(polygons, telemetry, sonarWavePhase)

        return polygons
    }

    // -------------------------------------------------------------------------
    // 1. Profesyonel ve Düzgün Kareli Kesim Matı (Alttan Bakışta Saydam, Açık Yeşil)
    // -------------------------------------------------------------------------
    private fun buildGreenCuttingMat(polygons: ArrayList<Polygon3D>, isDark: Boolean, cameraPitch: Float) {
        val ySurface = 28f // Tekerleklerin ve bilyenin tam oturduğu üst zemin seviyesi
        val yBottom = 30.5f // Matın 2.5 mm kalınlığı

        // Kullanıcı İsteği: "Alttan baktığımda saydam olsun ki altını da görebileyim"
        // Kamera dikey eğimi negatifse (pitch < -5°), alttan bakılıyor demektir.
        val isLookingFromBelow = cameraPitch < -5f
        val surfaceAlpha = if (isLookingFromBelow) 0.18f else 0.88f
        val edgeAlpha = if (isLookingFromBelow) 0.22f else 0.95f
        val gridAlpha = if (isLookingFromBelow) 0.38f else 0.70f
        val accentAlpha = if (isLookingFromBelow) 0.50f else 0.90f

        // Kullanıcı İsteği: "yeşil renk biraz daha açık olsun" (Canlı, taze ve daha açık kesim matı yeşili)
        val matGreen = (if (isDark) Color(0xFF14532D) else Color(0xFF2E9450)).copy(alpha = surfaceAlpha)
        val edgeDarkGreen = (if (isDark) Color(0xFF0D361D) else Color(0xFF1B6334)).copy(alpha = edgeAlpha)
        val borderWhite = (if (isDark) Color(0xFF99F6E4) else Color(0xFFE2E8F0)).copy(alpha = accentAlpha)
        val gridMint = (if (isDark) Color(0xFF6EE7B7) else Color(0xFF86EFAC)).copy(alpha = gridAlpha)
        val accentWhite = (if (isDark) Color(0xFFE2E8F0) else Color(0xFFFFFFFF)).copy(alpha = accentAlpha)

        // Düzgün Muntazam Kareler (Merkez pivot Z = -10f, X = 0f etrafında 10x10 = 100 muntazam kare)
        val step = 30f
        val halfSquares = 5
        val minX = -halfSquares * step // -150f
        val maxX = halfSquares * step  // +150f
        val centerZ = -10f
        val minZ = centerZ - halfSquares * step // -160f
        val maxZ = centerZ + halfSquares * step // +140f

        val pad = 15f
        val matMinX = minX - pad // -165f
        val matMaxX = maxX + pad // +165f
        val matMinZ = minZ - pad // -175f
        val matMaxZ = maxZ + pad // +155f

        // A. Matın 3D Yeşil Katı Gövdesi (Üst Yüzey)
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
                strokeWidth = 1.2f,
                isDoubleSided = true,
                renderLayer = 0
            )
        )

        // Alttan bakılmıyorsa alt yüzey ve kalın kenarları ekle (Alttan bakışta görüşü engellemesin)
        if (!isLookingFromBelow) {
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
            // 4 Kenar Pahı
            polygons.add(Polygon3D(listOf(Vector3D(matMinX, ySurface, matMinZ), Vector3D(matMaxX, ySurface, matMinZ), Vector3D(matMaxX, yBottom, matMinZ), Vector3D(matMinX, yBottom, matMinZ)), edgeDarkGreen, isDoubleSided = true, renderLayer = 0))
            polygons.add(Polygon3D(listOf(Vector3D(matMaxX, ySurface, matMinZ), Vector3D(matMaxX, ySurface, matMaxZ), Vector3D(matMaxX, yBottom, matMaxZ), Vector3D(matMaxX, yBottom, matMinZ)), edgeDarkGreen, isDoubleSided = true, renderLayer = 0))
            polygons.add(Polygon3D(listOf(Vector3D(matMaxX, ySurface, matMaxZ), Vector3D(matMinX, ySurface, matMaxZ), Vector3D(matMinX, yBottom, matMaxZ), Vector3D(matMaxX, yBottom, matMaxZ)), edgeDarkGreen, isDoubleSided = true, renderLayer = 0))
            polygons.add(Polygon3D(listOf(Vector3D(matMinX, ySurface, matMaxZ), Vector3D(matMinX, ySurface, matMinZ), Vector3D(matMinX, yBottom, matMinZ), Vector3D(matMinX, yBottom, matMaxZ)), edgeDarkGreen, isDoubleSided = true, renderLayer = 0))
        }

        // B. Dış Cetvel Çerçevesi (Outer Border Line)
        val lineY = if (isLookingFromBelow) ySurface + 0.2f else ySurface - 0.25f
        polygons.add(
            Polygon3D(
                vertices = listOf(
                    Vector3D(minX, lineY, minZ),
                    Vector3D(maxX, lineY, minZ),
                    Vector3D(maxX, lineY, maxZ),
                    Vector3D(minX, lineY, maxZ)
                ),
                color = Color.Transparent,
                strokeColor = borderWhite,
                strokeWidth = 1.5f,
                isDoubleSided = true,
                renderLayer = 0
            )
        )

        // C. Düzgün Eşit Kareler - Dikey Izgara Çizgileri (Vertical Grid Lines)
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

        // D. Düzgün Eşit Kareler - Yatay Izgara Çizgileri (Horizontal Grid Lines)
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
    // 2. MDF Ahşap Şasi (3D Hacimli Lazer Kesim Gövde)
    // -------------------------------------------------------------------------
    private fun buildMdfChassis(polygons: ArrayList<Polygon3D>) {
        val yTop = -3f
        val yBottom = 3f
        val mdfTopColor = Color(0xFFD4A373) // Açık ahşap tonu
        val mdfBottomColor = Color(0xFFB08958)
        val mdfEdgeColor = Color(0xFF8B5A2B)  // Lazer kesim koyu kenar

        // Şasi kontur noktaları (X, Z) düzleminde
        val outline = listOf(
            Vector3D(-24f, 0f, -125f), // Ön burun sol
            Vector3D(24f, 0f, -125f),  // Ön burun sağ
            Vector3D(42f, 0f, -100f),  // Ön sağ köşe
            Vector3D(65f, 0f, -80f),   // Sağ ön omuz
            Vector3D(65f, 0f, -50f),   // Sağ tekerlek yuva başı
            Vector3D(52f, 0f, -50f),   // Sağ yuva iç köşe ön
            Vector3D(52f, 0f, 30f),    // Sağ yuva iç köşe arka
            Vector3D(65f, 0f, 30f),    // Sağ tekerlek yuva sonu
            Vector3D(65f, 0f, 95f),    // Sağ arka köşe
            Vector3D(35f, 0f, 115f),   // Arka kavis sağ
            Vector3D(-35f, 0f, 115f),  // Arka kavis sol
            Vector3D(-65f, 0f, 95f),   // Sol arka köşe
            Vector3D(-65f, 0f, 30f),   // Sol tekerlek yuva sonu
            Vector3D(-52f, 0f, 30f),   // Sol yuva iç köşe arka
            Vector3D(-52f, 0f, -50f),  // Sol yuva iç köşe ön
            Vector3D(-65f, 0f, -50f),  // Sol tekerlek yuva başı
            Vector3D(-65f, 0f, -80f),  // Sol ön omuz
            Vector3D(-42f, 0f, -100f)  // Ön sol köşe
        )

        // Üst yüzey (Y = yTop)
        val topVertices = outline.map { Vector3D(it.x, yTop, it.z) }
        polygons.add(
            Polygon3D(
                vertices = topVertices,
                color = mdfTopColor,
                strokeColor = mdfEdgeColor,
                strokeWidth = 1f,
                isDoubleSided = true,
                renderLayer = 2
            )
        )

        // Alt yüzey (Y = yBottom)
        val bottomVertices = outline.map { Vector3D(it.x, yBottom, it.z) }
        polygons.add(
            Polygon3D(
                vertices = bottomVertices,
                color = mdfBottomColor,
                strokeColor = mdfEdgeColor,
                strokeWidth = 1f,
                isDoubleSided = true,
                renderLayer = 2
            )
        )

        // Kenar yüzeyleri (Extruded Side Edges)
        val n = outline.size
        for (i in 0 until n) {
            val next = (i + 1) % n
            val p1Top = topVertices[i]
            val p2Top = topVertices[next]
            val p2Bottom = bottomVertices[next]
            val p1Bottom = bottomVertices[i]

            polygons.add(
                Polygon3D(
                    vertices = listOf(p1Top, p2Top, p2Bottom, p1Bottom),
                    color = mdfEdgeColor,
                    isDoubleSided = true,
                    renderLayer = 2
                )
            )
        }

        // Şasi Üzerindeki Donanım Montaj Vidaları (Gümüş Phillips Vida Başları)
        val screwColor = Color(0xFFE2E8F0)
        val screwPositions = listOf(
            Vector3D(-20f, yTop - 0.6f, -85f),
            Vector3D(20f, yTop - 0.6f, -85f),
            Vector3D(-24f, yTop - 0.6f, 50f),
            Vector3D(24f, yTop - 0.6f, 50f),
            Vector3D(0f, yTop - 0.6f, 65f)
        )
        for (sp in screwPositions) {
            add3DBox(polygons, cx = sp.x, cy = sp.y, cz = sp.z, width = 3f, height = 0.8f, depth = 3f, color = screwColor, renderLayer = 2)
        }
    }

    // -------------------------------------------------------------------------
    // 3. Alt Gövde Bileşenleri (Sarı TT Motor Gövdeleri, Aks Kuplörleri, Motor Kabloları)
    // -------------------------------------------------------------------------
    private fun buildUndersideComponents(polygons: ArrayList<Polygon3D>) {
        val yellowMotorColor = Color(0xFFFACC15)
        val metalBracketColor = Color(0xFF94A3B8)
        val whiteShaftColor = Color(0xFFF8FAFC)
        val redWireColor = Color(0xFFDC2626)
        val blackWireColor = Color(0xFF0F172A)

        // Sol TT Motor Gövdesi (Sarı Dişli Kutusu)
        add3DBox(
            polygons = polygons,
            cx = -46f, cy = 11f, cz = -10f,
            width = 19f, height = 16f, depth = 46f,
            color = yellowMotorColor,
            renderLayer = 1
        )
        // Sol Beyaz Motor Aks Kuplörü
        add3DCylinder(
            polygons = polygons,
            cx = -57f, cy = 11f, cz = -10f,
            radius = 3.5f, height = 6f, segments = 6,
            color = whiteShaftColor,
            axis = CylinderAxis.X,
            renderLayer = 1
        )
        // Sol Metal Montaj Braketi
        add3DBox(
            polygons = polygons,
            cx = -46f, cy = 4f, cz = -10f,
            width = 21f, height = 2f, depth = 26f,
            color = metalBracketColor,
            renderLayer = 1
        )
        // Sol Motor Terminal Telleri (Kırmızı & Siyah)
        add3DBox(polygons, cx = -43f, cy = 10f, cz = 18f, width = 2f, height = 2f, depth = 12f, color = redWireColor, renderLayer = 1)
        add3DBox(polygons, cx = -47f, cy = 10f, cz = 18f, width = 2f, height = 2f, depth = 12f, color = blackWireColor, renderLayer = 1)

        // Sağ TT Motor Gövdesi (Sarı Dişli Kutusu)
        add3DBox(
            polygons = polygons,
            cx = 46f, cy = 11f, cz = -10f,
            width = 19f, height = 16f, depth = 46f,
            color = yellowMotorColor,
            renderLayer = 1
        )
        // Sağ Beyaz Motor Aks Kuplörü
        add3DCylinder(
            polygons = polygons,
            cx = 57f, cy = 11f, cz = -10f,
            radius = 3.5f, height = 6f, segments = 6,
            color = whiteShaftColor,
            axis = CylinderAxis.X,
            renderLayer = 1
        )
        // Sağ Metal Montaj Braketi
        add3DBox(
            polygons = polygons,
            cx = 46f, cy = 4f, cz = -10f,
            width = 21f, height = 2f, depth = 26f,
            color = metalBracketColor,
            renderLayer = 1
        )
        // Sağ Motor Terminal Telleri (Kırmızı & Siyah)
        add3DBox(polygons, cx = 43f, cy = 10f, cz = 18f, width = 2f, height = 2f, depth = 12f, color = redWireColor, renderLayer = 1)
        add3DBox(polygons, cx = 47f, cy = 10f, cz = 18f, width = 2f, height = 2f, depth = 12f, color = blackWireColor, renderLayer = 1)

        // Ön Alt Şasi Boşluğundan Sarkan Şeffaf Mavi Servo Gövdesi (Fotoğraf 2'deki gibi)
        add3DBox(
            polygons = polygons,
            cx = 0f, cy = 8f, cz = -105f,
            width = 20f, height = 8f, depth = 12f,
            color = Color(0xCC2563EB),
            renderLayer = 1
        )
    }

    // -------------------------------------------------------------------------
    // 4. Arka Sarhoş Bilye Tekerlek (Caster Wheel - Metal Kadeh & Vidalar)
    // -------------------------------------------------------------------------
    private fun buildCasterWheel(polygons: ArrayList<Polygon3D>) {
        val cx = 0f
        val cz = 95f
        val metalColor = Color(0xFF94A3B8)
        val swivelColor = Color(0xFF64748B)
        val ballColor = Color(0xFFF8FAFC)
        val screwColor = Color(0xFFCBD5E1)

        // Metal montaj plakası (Kare metal taban)
        add3DBox(
            polygons = polygons,
            cx = cx, cy = 5f, cz = cz,
            width = 26f, height = 3f, depth = 26f,
            color = metalColor,
            renderLayer = 1
        )

        // 4 Köşe Montaj Vidası (Fotoğraf 2'deki metal vida başları)
        add3DBox(polygons, cx = cx - 9f, cy = 7f, cz = cz - 9f, width = 3f, height = 1.5f, depth = 3f, color = screwColor, renderLayer = 1)
        add3DBox(polygons, cx = cx + 9f, cy = 7f, cz = cz - 9f, width = 3f, height = 1.5f, depth = 3f, color = screwColor, renderLayer = 1)
        add3DBox(polygons, cx = cx - 9f, cy = 7f, cz = cz + 9f, width = 3f, height = 1.5f, depth = 3f, color = screwColor, renderLayer = 1)
        add3DBox(polygons, cx = cx + 9f, cy = 7f, cz = cz + 9f, width = 3f, height = 1.5f, depth = 3f, color = screwColor, renderLayer = 1)

        // Dönen metal bilye kadehi (Swivel Cup)
        add3DCylinder(
            polygons = polygons,
            cx = cx, cy = 10f, cz = cz,
            radius = 8f, height = 6f, segments = 8,
            color = swivelColor,
            axis = CylinderAxis.Y,
            renderLayer = 1
        )

        // Beyaz bilye / tekerlek (Fotoğraf 2'deki beyaz naylon teker)
        add3DCylinder(
            polygons = polygons,
            cx = cx, cy = 18f, cz = cz,
            radius = 9f, height = 8f, segments = 8,
            color = ballColor,
            axis = CylinderAxis.X,
            renderLayer = 1
        )
    }

    // -------------------------------------------------------------------------
    // 4. Sol ve Sağ 3D Tekerlekler (TT Motors & Yellow Hub Rubber Tires)
    // -------------------------------------------------------------------------
    private fun buildWheels(
        polygons: ArrayList<Polygon3D>,
        telemetry: RobotTelemetry,
        leftWheelPhase: Float,
        rightWheelPhase: Float
    ) {
        val leftX = -72f
        val rightX = 72f
        val wheelY = 8f
        val wheelZ = -10f
        val wheelRadius = 24f
        val wheelThickness = 16f

        val leftState = telemetry.leftWheelState
        val rightState = telemetry.rightWheelState

        // Sol Tekerlek (Bağımsız Faz ve Hız)
        buildSingle3DWheel(
            polygons = polygons,
            cx = leftX, cy = wheelY, cz = wheelZ,
            radius = wheelRadius, thickness = wheelThickness,
            isLeft = true,
            direction = leftState.direction,
            phase = leftWheelPhase
        )

        // Sağ Tekerlek (Bağımsız Faz ve Hız)
        buildSingle3DWheel(
            polygons = polygons,
            cx = rightX, cy = wheelY, cz = wheelZ,
            radius = wheelRadius, thickness = wheelThickness,
            isLeft = false,
            direction = rightState.direction,
            phase = rightWheelPhase
        )
    }

    private fun buildSingle3DWheel(
        polygons: ArrayList<Polygon3D>,
        cx: Float, cy: Float, cz: Float,
        radius: Float, thickness: Float,
        isLeft: Boolean,
        direction: WheelDirection,
        phase: Float
    ) {
        val segments = 8
        val tireColor = Color(0xFF1E293B)  // Siyah kauçuk lastik
        val hubColor = Color(0xFFFACC15)   // Sarı TT motor jantı
        val axleColor = Color(0xFF0F172A)  // Siyah göbek aksı

        val halfT = thickness / 2f
        val outerX = if (isLeft) cx - halfT else cx + halfT
        val innerX = if (isLeft) cx + halfT else cx - halfT

        // Dönüş açısı fazı (Radyan)
        val spinRad = phase * (PI.toFloat() / 180f)

        val outerPts = ArrayList<Vector3D>(segments)
        val innerPts = ArrayList<Vector3D>(segments)

        for (i in 0 until segments) {
            val angle = (i * (2f * PI.toFloat() / segments)) + spinRad
            val dy = radius * cos(angle)
            val dz = radius * sin(angle)
            outerPts.add(Vector3D(outerX, cy + dy, cz + dz))
            innerPts.add(Vector3D(innerX, cy + dy, cz + dz))
        }

        // Dış Jant ve Lastik Yüzeyi
        polygons.add(
            Polygon3D(
                vertices = outerPts,
                color = hubColor,
                strokeColor = tireColor,
                strokeWidth = 2f,
                isDoubleSided = true,
                renderLayer = 2
            )
        )

        // İç Yüzey
        polygons.add(
            Polygon3D(
                vertices = innerPts,
                color = tireColor,
                isDoubleSided = true,
                renderLayer = 2
            )
        )

        // Lastik Sırtı (Tread Faces)
        for (i in 0 until segments) {
            val next = (i + 1) % segments
            val p1Outer = outerPts[i]
            val p2Outer = outerPts[next]
            val p2Inner = innerPts[next]
            val p1Inner = innerPts[i]

            // Diş desenleri için iki tonlu kauçuk
            val treadTone = if (i % 2 == 0) tireColor else Color(0xFF0F172A)
            polygons.add(
                Polygon3D(
                    vertices = listOf(p1Outer, p2Outer, p2Inner, p1Inner),
                    color = treadTone,
                    strokeColor = Color(0xFF334155),
                    strokeWidth = 0.5f,
                    isDoubleSided = true,
                    renderLayer = 2
                )
            )
        }

        // Sarı Jant Merkezindeki Siyah Aks
        add3DBox(
            polygons = polygons,
            cx = outerX + (if (isLeft) -1f else 1f), cy = cy, cz = cz,
            width = 3f, height = 8f, depth = 8f,
            color = axleColor,
            renderLayer = 2
        )
    }

    // -------------------------------------------------------------------------
    // 6. Gövde Elektroniği (L298N, Arduino Uno + Shield, 2x 18650 Bataryalar)
    // -------------------------------------------------------------------------
    private fun buildElectronics(polygons: ArrayList<Polygon3D>, telemetry: RobotTelemetry) {
        // A. L298N MOTOR SÜRÜCÜ (Kırmızı Kart, Kanatlı Siyah Soğutucu, Klemensler ve Kondansatörler)
        val l298nZ = -35f
        add3DBox(polygons, cx = 0f, cy = -6f, cz = l298nZ, width = 50f, height = 4f, depth = 42f, color = Color(0xFFDC2626))
        
        // Siyah Alüminyum Soğutucu Gövdesi ve Dikey Soğutma Kanatçıkları (Fins)
        add3DBox(polygons, cx = 0f, cy = -16f, cz = l298nZ, width = 28f, height = 16f, depth = 22f, color = Color(0xFF0F172A))
        val finColor = Color(0xFF334155)
        for (finX in listOf(-9f, -3f, 3f, 9f)) {
            add3DBox(polygons, cx = finX, cy = -25f, cz = l298nZ, width = 2f, height = 4f, depth = 20f, color = finColor)
        }

        // İki Adet Siyah Elektrolitik Filtre Kondansatörü
        add3DCylinder(polygons, cx = -16f, cy = -12f, cz = l298nZ + 12f, radius = 4f, height = 10f, segments = 6, color = Color(0xFF0F172A), axis = CylinderAxis.Y)
        add3DCylinder(polygons, cx = 16f, cy = -12f, cz = l298nZ + 12f, radius = 4f, height = 10f, segments = 6, color = Color(0xFF0F172A), axis = CylinderAxis.Y)

        // Mavi Klemens Blokları ve Gümüş Terminal Vidaları
        val termBlue = Color(0xFF2563EB)
        val screwSilver = Color(0xFFCBD5E1)
        add3DBox(polygons, cx = -20f, cy = -10f, cz = l298nZ, width = 8f, height = 8f, depth = 14f, color = termBlue)
        add3DBox(polygons, cx = 20f, cy = -10f, cz = l298nZ, width = 8f, height = 8f, depth = 14f, color = termBlue)
        add3DBox(polygons, cx = 0f, cy = -10f, cz = l298nZ + 16f, width = 16f, height = 8f, depth = 8f, color = termBlue)
        // Klemens vidaları
        add3DBox(polygons, cx = -20f, cy = -14.5f, cz = l298nZ, width = 3f, height = 1f, depth = 3f, color = screwSilver)
        add3DBox(polygons, cx = 20f, cy = -14.5f, cz = l298nZ, width = 3f, height = 1f, depth = 3f, color = screwSilver)

        // B. ARDUINO UNO + SENSOR SHIELD V5 (Mavi Çift Kat Kart, USB, DC Jack, LED)
        val arduinoZ = 25f
        add3DBox(polygons, cx = 0f, cy = -7f, cz = arduinoZ, width = 58f, height = 5f, depth = 48f, color = Color(0xFF1D4ED8))
        add3DBox(polygons, cx = 0f, cy = -13f, cz = arduinoZ, width = 54f, height = 6f, depth = 44f, color = Color(0xFF1E40AF))
        // Header Pin Soket Grupları
        add3DBox(polygons, cx = -18f, cy = -18f, cz = arduinoZ, width = 10f, height = 5f, depth = 38f, color = Color(0xFF0F172A))
        add3DBox(polygons, cx = 18f, cy = -18f, cz = arduinoZ, width = 10f, height = 5f, depth = 38f, color = Color(0xFF0F172A))
        // Gümüş USB Type-B Port
        add3DBox(polygons, cx = -22f, cy = -15f, cz = arduinoZ - 18f, width = 10f, height = 8f, depth = 12f, color = Color(0xFFCBD5E1))
        // Siyah DC Güç Jackı
        add3DBox(polygons, cx = -22f, cy = -14f, cz = arduinoZ + 14f, width = 10f, height = 9f, depth = 14f, color = Color(0xFF0F172A))
        // Kırmızı Reset Butonu
        add3DBox(polygons, cx = -18f, cy = -18f, cz = arduinoZ - 6f, width = 5f, height = 3.5f, depth = 5f, color = Color(0xFFEF4444))
        // Kırmızı Güç LED'i
        val ledColor = if (telemetry.hasSignal) Color(0xFFEF4444) else Color(0xFF7F1D1D)
        add3DBox(polygons, cx = -14f, cy = -19f, cz = arduinoZ - 10f, width = 4f, height = 4f, depth = 4f, color = ledColor)

        // C. 2x 18650 LI-ION PİL YUVASI, YEŞİL PİLLER VE KABLOLAR
        val batZ = 80f
        add3DBox(polygons, cx = 0f, cy = -8f, cz = batZ, width = 64f, height = 8f, depth = 34f, color = Color(0xFF0F172A))
        // Ön Yeşil Pil (Z = batZ - 8)
        add3DCylinder(polygons, cx = 0f, cy = -14f, cz = batZ - 8f, radius = 6.5f, height = 50f, segments = 8, color = Color(0xFF22C55E), axis = CylinderAxis.X)
        // Arka Yeşil Pil (Z = batZ + 8)
        add3DCylinder(polygons, cx = 0f, cy = -14f, cz = batZ + 8f, radius = 6.5f, height = 50f, segments = 8, color = Color(0xFF22C55E), axis = CylinderAxis.X)
        // Pozitif (+) Başlıklar ve Siyah Yalıtım Halkaları
        add3DCylinder(polygons, cx = -26f, cy = -14f, cz = batZ - 8f, radius = 2.5f, height = 3f, segments = 6, color = Color(0xFFE2E8F0), axis = CylinderAxis.X)
        add3DCylinder(polygons, cx = -26f, cy = -14f, cz = batZ + 8f, radius = 2.5f, height = 3f, segments = 6, color = Color(0xFFE2E8F0), axis = CylinderAxis.X)
        add3DCylinder(polygons, cx = -24.5f, cy = -14f, cz = batZ - 8f, radius = 6.6f, height = 1.5f, segments = 6, color = Color(0xFF0F172A), axis = CylinderAxis.X)
        add3DCylinder(polygons, cx = -24.5f, cy = -14f, cz = batZ + 8f, radius = 6.6f, height = 1.5f, segments = 6, color = Color(0xFF0F172A), axis = CylinderAxis.X)
        // Bataryadan L298N'e Giden Kırmızı & Siyah Güç Kabloları
        add3DBox(polygons, cx = -23f, cy = -6f, cz = batZ - 20f, width = 2f, height = 2f, depth = 22f, color = Color(0xFFDC2626))
        add3DBox(polygons, cx = -26f, cy = -6f, cz = batZ - 20f, width = 2f, height = 2f, depth = 22f, color = Color(0xFF0F172A))
    }

    // -------------------------------------------------------------------------
    // 7. Elektronik Jumper Kablo Demeti & Siyah Kablo Bağı (Zip Tie)
    // -------------------------------------------------------------------------
    private fun buildJumperWireHarness(polygons: ArrayList<Polygon3D>) {
        // Gerçek Arduino ve elektronik donanım jumper renkleri (Karışık endüstriyel kablo dizilimi)
        // Kullanıcı İsteği: Renkler kesinlikle herhangi bir tarafı temsil etmeyen karışık donanım kabloları
        val wireColors = listOf(
            Color(0xFF0F172A), // Siyah (GND)
            Color(0xFF2563EB), // Mavi (Sinyal)
            Color(0xFFF8FAFC), // Beyaz (Logic)
            Color(0xFF78350F), // Kahverengi (Toprak)
            Color(0xFFDC2626), // Kırmızı (VCC +5V)
            Color(0xFF64748B)  // Gri (Veri)
        )

        val wireCount = wireColors.size
        val wireWidth = 2.2f
        val startX = -(wireCount * wireWidth) / 2f

        // HC-SR04/SG90'dan Arduino Shield'e uzanan kablo kemeri
        for (i in 0 until wireCount) {
            val wx = startX + i * wireWidth + 1.1f
            val wColor = wireColors[i]

            // Ön segment (HC-SR04 arkasından yukarı yükselen)
            add3DBox(polygons, cx = wx, cy = -18f, cz = -75f, width = 1.8f, height = 3f, depth = 25f, color = wColor)
            // Orta tepe yay segmenti (L298N üzerinden aşan kemer)
            add3DBox(polygons, cx = wx, cy = -26f, cz = -42f, width = 1.8f, height = 3f, depth = 38f, color = wColor)
            // Arka iniş segmenti (Arduino Sensor Shield pinlerine inen)
            add3DBox(polygons, cx = wx, cy = -19f, cz = -3f, width = 1.8f, height = 3f, depth = 36f, color = wColor)
        }

        // Siyah Kablo Bağı (Zip Tie / Cırt Kelepçe) - Fotoğraf 1'deki tam ortadaki toka
        add3DBox(
            polygons = polygons,
            cx = 0f, cy = -27f, cz = -42f,
            width = wireCount * wireWidth + 4f, height = 5f, depth = 5f,
            color = Color(0xFF0F172A)
        )
    }

    // -------------------------------------------------------------------------
    // 6. SG90 Mikro Servo ve Dönen HC-SR04 Başlığı (3D Rotating Head & Live Beam)
    // -------------------------------------------------------------------------
    private fun buildServoAndUltrasonicHead(
        polygons: ArrayList<Polygon3D>,
        telemetry: RobotTelemetry,
        sonarWavePhase: Float
    ) {
        val servoPivot = Vector3D(0f, -16f, -105f)

        // Sabit SG90 Mavi Şeffaf Servo Gövdesi
        add3DBox(
            polygons = polygons,
            cx = servoPivot.x, cy = servoPivot.y, cz = servoPivot.z,
            width = 24f, height = 22f, depth = 14f,
            color = Color(0xCC2563EB) // Şeffaf parlak mavi
        )
        // Beyaz Servo Kolu (Horn)
        add3DBox(
            polygons = polygons,
            cx = servoPivot.x, cy = servoPivot.y - 12f, cz = servoPivot.z,
            width = 8f, height = 4f, depth = 16f,
            color = Color(0xFFF8FAFC)
        )

        // ---------------------------------------------------------------------
        // DÖNEN KAFA MEKANİZMASI (HC-SR04 ve Braket)
        // Servo Açısı: 0° (Sağ) .. 90° (Merkez) .. 180° (Sol)
        // Y ekseninde radyan dönüş: angleRad = (90 - servoAngle) * PI / 180
        // ---------------------------------------------------------------------
        val angleRad = (90 - telemetry.servoAngle) * (PI.toFloat() / 180f)

        // Kafa bileşenlerini pivot etrafında döndüren yardımcı fonksiyon
        fun rotated(localPos: Vector3D): Vector3D {
            val rel = localPos - servoPivot
            val rot = rel.rotateY(angleRad)
            return servoPivot + rot
        }

        // Siyah Pleksi U-Braket
        addRotatedBox(
            polygons = polygons,
            pivot = servoPivot,
            center = Vector3D(0f, -22f, -105f),
            width = 46f, height = 8f, depth = 6f,
            color = Color(0xFF0F172A),
            angleRad = angleRad
        )

        // HC-SR04 Mavi PCB Kartı
        val pcbCenter = Vector3D(0f, -32f, -107f)
        addRotatedBox(
            polygons = polygons,
            pivot = servoPivot,
            center = pcbCenter,
            width = 52f, height = 22f, depth = 3f,
            color = Color(0xFF1E40AF),
            angleRad = angleRad
        )

        // Merkezi Gümüş Kristal Osilatör (16 MHz) - Fotoğraf 3'teki iki gözün tam ortası
        addRotatedBox(
            polygons = polygons,
            pivot = servoPivot,
            center = Vector3D(0f, -32f, -109.5f),
            width = 8f, height = 4.5f, depth = 2.5f,
            color = Color(0xFFCBD5E1),
            angleRad = angleRad
        )

        // Alt 4 Pinli Jumper Bağlantı Başlığı (VCC, Trig, Echo, GND)
        addRotatedBox(
            polygons = polygons,
            pivot = servoPivot,
            center = Vector3D(0f, -19f, -107f),
            width = 14f, height = 6f, depth = 3.5f,
            color = Color(0xFFF59E0B),
            angleRad = angleRad
        )

        // Sol Ultrasonik Göz (T - Transmitter)
        build3DTransducer(
            polygons = polygons,
            pivot = servoPivot,
            center = Vector3D(-14f, -32f, -112f),
            radius = 8f, depth = 12f,
            label = "T",
            angleRad = angleRad
        )

        // Sağ Ultrasonik Göz (R - Receiver)
        build3DTransducer(
            polygons = polygons,
            pivot = servoPivot,
            center = Vector3D(14f, -32f, -112f),
            radius = 8f, depth = 12f,
            label = "R",
            angleRad = angleRad
        )

        // ---------------------------------------------------------------------
        // CANLI 3D ULTRASONİK SONAR IŞINI
        // ---------------------------------------------------------------------
        if (telemetry.hasSignal && telemetry.frontDistance > 0) {
            build3DSonarCone(
                polygons = polygons,
                pivot = servoPivot,
                headCenter = Vector3D(0f, -32f, -118f),
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
        val segments = 8
        val silverColor = Color(0xFFE2E8F0)
        val meshColor = Color(0xFF0F172A)

        val frontZ = center.z - depth
        val backZ = center.z

        val frontPts = ArrayList<Vector3D>(segments)
        val backPts = ArrayList<Vector3D>(segments)

        for (i in 0 until segments) {
            val a = i * (2f * PI.toFloat() / segments)
            val dx = radius * cos(a)
            val dy = radius * sin(a)

            val rawBack = Vector3D(center.x + dx, center.y + dy, backZ)
            val rawFront = Vector3D(center.x + dx, center.y + dy, frontZ)

            // Servo pivotu etrafında döndür
            frontPts.add(pivot + (rawFront - pivot).rotateY(angleRad))
            backPts.add(pivot + (rawBack - pivot).rotateY(angleRad))
        }

        // Ön Siyah Izgara Kapağı
        polygons.add(
            Polygon3D(
                vertices = frontPts,
                color = meshColor,
                strokeColor = silverColor,
                strokeWidth = 1f,
                isDoubleSided = true,
                renderLayer = 3
            )
        )

        // Gümüş Silindir Gövdesi
        for (i in 0 until segments) {
            val next = (i + 1) % segments
            polygons.add(
                Polygon3D(
                    vertices = listOf(frontPts[i], frontPts[next], backPts[next], backPts[i]),
                    color = silverColor,
                    isDoubleSided = true,
                    renderLayer = 3
                )
            )
        }
    }

    private fun build3DSonarCone(
        polygons: ArrayList<Polygon3D>,
        pivot: Vector3D,
        headCenter: Vector3D,
        distanceCm: Int,
        angleRad: Float,
        sonarWavePhase: Float
    ) {
        // 1 cm = 2.4 birim ölçekleme (Maksimum 220 birim)
        val beamLength = (distanceCm * 2.4f).coerceIn(30f, 220f)
        val spreadRadius = beamLength * 0.32f

        val beamColor = when {
            distanceCm <= 15 -> Color(0xFFEF4444) // TEHLİKE / KIRMIZI
            distanceCm <= 30 -> Color(0xFFF59E0B) // UYARI / SARI
            else -> Color(0xFF10B981)             // GÜVENLİ / YEŞİL
        }

        val apexRaw = headCenter
        val targetZRaw = headCenter.z - beamLength

        val coneSegments = 8
        val basePts = ArrayList<Vector3D>(coneSegments)

        for (i in 0 until coneSegments) {
            val a = i * (2f * PI.toFloat() / coneSegments)
            val dx = spreadRadius * cos(a)
            val dy = spreadRadius * sin(a)
            val rawPt = Vector3D(headCenter.x + dx, headCenter.y + dy, targetZRaw)
            basePts.add(pivot + (rawPt - pivot).rotateY(angleRad))
        }

        val apexTransformed = pivot + (apexRaw - pivot).rotateY(angleRad)

        // 3D Volumetrik Koni Yüzeyleri (Şeffaf Renkli)
        for (i in 0 until coneSegments) {
            val next = (i + 1) % coneSegments
            polygons.add(
                Polygon3D(
                    vertices = listOf(apexTransformed, basePts[i], basePts[next]),
                    color = beamColor.copy(alpha = 0.22f),
                    isDoubleSided = true,
                    renderLayer = 4
                )
            )
        }

        // Hedef Düzlem Halkası (Mesafe Engel Çizgisi)
        polygons.add(
            Polygon3D(
                vertices = basePts,
                color = beamColor.copy(alpha = 0.35f),
                strokeColor = beamColor,
                strokeWidth = 1.5f,
                isDoubleSided = true,
                renderLayer = 4
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
        renderLayer: Int = 3
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

        // 6 Yüzey
        polygons.add(Polygon3D(listOf(v0, v1, v2, v3), color, isDoubleSided = true, renderLayer = renderLayer)) // Ön (-Z)
        polygons.add(Polygon3D(listOf(v5, v4, v7, v6), color, isDoubleSided = true, renderLayer = renderLayer)) // Arka (+Z)
        polygons.add(Polygon3D(listOf(v4, v0, v3, v7), color, isDoubleSided = true, renderLayer = renderLayer)) // Sol (-X)
        polygons.add(Polygon3D(listOf(v1, v5, v6, v2), color, isDoubleSided = true, renderLayer = renderLayer)) // Sağ (+X)
        polygons.add(Polygon3D(listOf(v4, v5, v1, v0), color, isDoubleSided = true, renderLayer = renderLayer)) // Üst (-Y)
        polygons.add(Polygon3D(listOf(v3, v2, v6, v7), color, isDoubleSided = true, renderLayer = renderLayer)) // Alt (+Y)
    }

    private fun addRotatedBox(
        polygons: ArrayList<Polygon3D>,
        pivot: Vector3D,
        center: Vector3D,
        width: Float, height: Float, depth: Float,
        color: Color,
        angleRad: Float,
        renderLayer: Int = 3
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
        renderLayer: Int = 3
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

