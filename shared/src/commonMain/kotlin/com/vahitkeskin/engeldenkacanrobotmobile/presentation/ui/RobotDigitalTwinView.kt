package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.WheelDirection
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🤖 RobotDigitalTwinView
 * Kullanıcının fiziksel robotunun (MDF lazer kesim şasi, SG90 servo, HC-SR04 gözler,
 * sarı TT DC motorlar, L298N sürücü, Arduino Uno + Shield, 2x 18650 batarya)
 * birebir Canvas, Path ve Trigonometrik dönüşümlerle çizilmiş 2D Dijital İkizi.
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun RobotDigitalTwinView(
    telemetry: RobotTelemetry,
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    // Tekerlek ve sonar dalga animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "RobotTwinAnimation")
    
    val wheelPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WheelTreadPhase"
    )

    val sonarWavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SonarWavePhase"
    )

    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Robot merkez ekseni
        val cx = w / 2f
        val cy = h * 0.52f

        // 1. ZEMİN VE IZGARA (Grid & Cetvel)
        drawGrid(colors, w, h)

        scale(scale = 1.6f, pivot = Offset(cx, cy)) {
            // 2. SOL VE SAĞ KORİDOR ALGILAMA ÇİZGİLERİ (Hafıza Işınları)
            if (telemetry.hasSignal) {
                drawCorridorRays(cx, cy - 100f, telemetry, colors, textMeasurer)
            }

            // 3. ŞASİ (MDF Lazer Kesim Ahşap Gövde)
            drawMdfChassis(cx, cy)

            // 4. ARKA SARHOŞ TEKERLEK (Caster Ball Wheel)
            drawCasterWheel(cx, cy + 95f)

            // 5. SOL VE SAĞ TT MOTORLAR VE LASTİKLER (Yellow TT Gear Motors & Rubber Tires)
            drawTreadedWheels(
                cx = cx,
                cy = cy - 10f,
                telemetry = telemetry,
                wheelPhase = wheelPhase,
                colors = colors,
                textMeasurer = textMeasurer
            )

            // 6. GÖVDE ELEKTRONİĞİ: 2x 18650 Batarya, Arduino Uno + Shield, L298N
            drawElectronics(cx, cy)

            // 7. SG90 MİKRO SERVO VE HC-SR04 DÖNEN KAFA
            drawServoAndUltrasonicHead(
                cx = cx,
                cy = cy - 105f,
                telemetry = telemetry,
                sonarWavePhase = sonarWavePhase,
                colors = colors,
                textMeasurer = textMeasurer
            )
        }
    }
}

// -------------------------------------------------------------
// ÇİZİM YARDIMCI FONKSİYONLARI
// -------------------------------------------------------------

private fun DrawScope.drawGrid(colors: AppColors, w: Float, h: Float) {
    val step = 30f
    val gridColor = colors.border.copy(alpha = 0.25f)
    var x = 0f
    while (x <= w) {
        drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 0.5f)
        x += step
    }
    var y = 0f
    while (y <= h) {
        drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 0.5f)
        y += step
    }
}

private fun DrawScope.drawMdfChassis(cx: Float, cy: Float) {
    // Fotoğraftaki lazer kesim MDF ahşap gövde formu (Path)
    val chassisPath = Path().apply {
        // Ön Burun (Servo montaj dili)
        moveTo(cx - 38f, cy - 110f)
        cubicTo(cx - 38f, cy - 138f, cx + 38f, cy - 138f, cx + 38f, cy - 110f)
        
        // Sağ omuz
        lineTo(cx + 62f, cy - 90f)
        lineTo(cx + 62f, cy - 55f)

        // Sağ tekerlek yuvası girintisi
        lineTo(cx + 50f, cy - 55f)
        lineTo(cx + 50f, cy + 35f)
        lineTo(cx + 62f, cy + 35f)

        // Sağ arka etek
        lineTo(cx + 62f, cy + 95f)

        // Arka kavis
        cubicTo(cx + 50f, cy + 125f, cx - 50f, cy + 125f, cx - 62f, cy + 95f)

        // Sol arka etek
        lineTo(cx - 62f, cy + 35f)

        // Sol tekerlek yuvası girintisi
        lineTo(cx - 50f, cy + 35f)
        lineTo(cx - 50f, cy - 55f)
        lineTo(cx - 62f, cy - 55f)

        // Sol omuz
        lineTo(cx - 62f, cy - 90f)
        close()
    }

    // MDF Ahşap Doldurma
    drawPath(
        path = chassisPath,
        color = Color(0xFFC49A6C) // Sıcak MDF ahşap tonu
    )

    // MDF Lazer Kesim Kenar Kararması
    drawPath(
        path = chassisPath,
        color = Color(0xFF6E4723),
        style = Stroke(width = 2.5f)
    )

    // Gövde montaj vida delikleri (Fotoğraftaki gümüş delikler)
    val screwColor = Color(0xFF3E2723)
    val screwPositions = listOf(
        Offset(cx - 20f, cy - 120f), Offset(cx + 20f, cy - 120f),
        Offset(cx - 45f, cy - 80f),  Offset(cx + 45f, cy - 80f),
        Offset(cx - 35f, cy - 35f),  Offset(cx + 35f, cy - 35f),
        Offset(cx - 35f, cy + 45f),  Offset(cx + 35f, cy + 45f),
        Offset(cx - 20f, cy + 105f), Offset(cx + 20f, cy + 105f)
    )
    for (pos in screwPositions) {
        drawCircle(color = screwColor, radius = 2f, center = pos)
        drawCircle(color = Color(0xFFCBD5E1), radius = 1.2f, center = pos)
    }
}

private fun DrawScope.drawCasterWheel(cx: Float, cy: Float) {
    // Metal montaj plakası
    drawRoundRect(
        color = Color(0xFF94A3B8),
        topLeft = Offset(cx - 16f, cy - 14f),
        size = Size(32f, 28f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    // 4 metal vida
    drawCircle(Color(0xFF334155), radius = 1.5f, center = Offset(cx - 12f, cy - 10f))
    drawCircle(Color(0xFF334155), radius = 1.5f, center = Offset(cx + 12f, cy - 10f))
    drawCircle(Color(0xFF334155), radius = 1.5f, center = Offset(cx - 12f, cy + 10f))
    drawCircle(Color(0xFF334155), radius = 1.5f, center = Offset(cx + 12f, cy + 10f))

    // Beyaz naylon bilye tekerlek
    drawCircle(
        color = Color(0xFFF8FAFC),
        radius = 9f,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = Color(0xFF64748B),
        radius = 9f,
        center = Offset(cx, cy),
        style = Stroke(width = 1.5f)
    )
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawTreadedWheels(
    cx: Float,
    cy: Float,
    telemetry: RobotTelemetry,
    wheelPhase: Float,
    colors: AppColors,
    textMeasurer: TextMeasurer
) {
    val wheelW = 22f
    val wheelH = 78f
    val leftWheelX = cx - 74f
    val rightWheelX = cx + 52f
    val wheelY = cy - wheelH / 2f

    val leftState = telemetry.leftWheelState
    val rightState = telemetry.rightWheelState

    // 1. SOL TEKERLEK
    drawSingleWheel(
        x = leftWheelX,
        y = wheelY,
        w = wheelW,
        h = wheelH,
        direction = leftState.direction,
        wheelPhase = wheelPhase
    )

    // 2. SAĞ TEKERLEK
    drawSingleWheel(
        x = rightWheelX,
        y = wheelY,
        w = wheelW,
        h = wheelH,
        direction = rightState.direction,
        wheelPhase = wheelPhase
    )

    // Sol Tekerlek Durum Rozeti (HUD)
    val leftBadgeColor = when (leftState.direction) {
        WheelDirection.FORWARD -> Color(0xFF10B981)
        WheelDirection.BACKWARD -> Color(0xFFEF4444)
        WheelDirection.STOPPED -> colors.textMuted
    }
    val leftText = "${leftState.direction.symbol} ${leftState.speedPwm} PWM"
    val leftLayout = textMeasurer.measure(
        text = leftText,
        style = TextStyle(color = leftBadgeColor, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
    )
    val leftPillX = leftWheelX - leftLayout.size.width - 8f
    val leftPillY = cy - leftLayout.size.height / 2f
    drawRoundRect(
        color = Color(0xDD0F172A),
        topLeft = Offset(leftPillX - 4f, leftPillY - 2f),
        size = Size(leftLayout.size.width + 8f, leftLayout.size.height + 4f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawText(
        textLayoutResult = leftLayout,
        topLeft = Offset(leftPillX, leftPillY)
    )

    // Sağ Tekerlek Durum Rozeti (HUD)
    val rightBadgeColor = when (rightState.direction) {
        WheelDirection.FORWARD -> Color(0xFF10B981)
        WheelDirection.BACKWARD -> Color(0xFFEF4444)
        WheelDirection.STOPPED -> colors.textMuted
    }
    val rightText = "${rightState.speedPwm} PWM ${rightState.direction.symbol}"
    val rightLayout = textMeasurer.measure(
        text = rightText,
        style = TextStyle(color = rightBadgeColor, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
    )
    val rightPillX = rightWheelX + wheelW + 8f
    val rightPillY = cy - rightLayout.size.height / 2f
    drawRoundRect(
        color = Color(0xDD0F172A),
        topLeft = Offset(rightPillX - 4f, rightPillY - 2f),
        size = Size(rightLayout.size.width + 8f, rightLayout.size.height + 4f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawText(
        textLayoutResult = rightLayout,
        topLeft = Offset(rightPillX, rightPillY)
    )
}

private fun DrawScope.drawSingleWheel(
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    direction: WheelDirection,
    wheelPhase: Float
) {
    // Siyah Kauçuk Lastik Gövdesi
    drawRoundRect(
        color = Color(0xFF111827),
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = CornerRadius(5f, 5f)
    )

    // Sarı Jant (TT Motor sarı jantı)
    val rimInsetX = 4f
    val rimInsetY = 8f
    drawRoundRect(
        color = Color(0xFFFACC15),
        topLeft = Offset(x + rimInsetX, y + rimInsetY),
        size = Size(w - rimInsetX * 2f, h - rimInsetY * 2f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Siyah Merkez Göbek Aksı
    drawCircle(
        color = Color(0xFF1F2937),
        radius = 3.5f,
        center = Offset(x + w / 2f, y + h / 2f)
    )

    // Lastik Dişleri (Tread Patterns)
    val treadStep = 10f
    val phaseOffset = when (direction) {
        WheelDirection.FORWARD -> wheelPhase % treadStep
        WheelDirection.BACKWARD -> - (wheelPhase % treadStep)
        WheelDirection.STOPPED -> 0f
    }

    var ty = y + phaseOffset
    while (ty < y + h) {
        if (ty >= y) {
            drawLine(
                color = Color(0xFF374151),
                start = Offset(x + 1f, ty),
                end = Offset(x + rimInsetX, ty - 2f),
                strokeWidth = 1.5f
            )
            drawLine(
                color = Color(0xFF374151),
                start = Offset(x + w - rimInsetX, ty - 2f),
                end = Offset(x + w - 1f, ty),
                strokeWidth = 1.5f
            )
        }
        ty += treadStep
    }
}

private fun DrawScope.drawElectronics(cx: Float, cy: Float) {
    // 1. L298N KIRMIZI MOTOR SÜRÜCÜ KARTI
    val l298nY = cy - 40f
    drawRoundRect(
        color = Color(0xFFDC2626), // Kırmızı PCB
        topLeft = Offset(cx - 30f, l298nY - 22f),
        size = Size(60f, 44f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    // Siyah Alüminyum Soğutucu Blok (Finned Heatsink)
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(cx - 16f, l298nY - 16f),
        size = Size(32f, 32f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    // Soğutucu Kanatçıkları
    for (i in -12..12 step 6) {
        drawLine(
            color = Color(0xFF334155),
            start = Offset(cx + i.toFloat(), l298nY - 14f),
            end = Offset(cx + i.toFloat(), l298nY + 14f),
            strokeWidth = 1.5f
        )
    }
    // Mavi Klemens Blokları (VCC/GND/Motor Çıkışları)
    drawRect(Color(0xFF2563EB), topLeft = Offset(cx - 28f, l298nY + 14f), size = Size(14f, 6f))
    drawRect(Color(0xFF2563EB), topLeft = Offset(cx + 14f, l298nY + 14f), size = Size(14f, 6f))

    // 2. ARDUINO UNO + SENSOR SHIELD V5 (Mavi Kart)
    val arduinoY = cy + 22f
    drawRoundRect(
        color = Color(0xFF1D4ED8), // Arduino Mavi PCB
        topLeft = Offset(cx - 34f, arduinoY - 26f),
        size = Size(68f, 52f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    // Sensor Shield Header Grupları (Siyah 3'lü pin soketleri)
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(cx - 28f, arduinoY - 18f),
        size = Size(56f, 16f),
        cornerRadius = CornerRadius(1f, 1f)
    )
    // Gümüş Renkli Kristal ve MCU Çipi
    drawRect(Color(0xFF475569), topLeft = Offset(cx - 10f, arduinoY + 4f), size = Size(20f, 16f))
    // Kırmızı Güç LED'i (Aktif olduğunu gösterir)
    drawCircle(Color(0xFFEF4444), radius = 2f, center = Offset(cx - 24f, arduinoY + 16f))
    drawCircle(Color(0xFFEF4444).copy(alpha = 0.4f), radius = 5f, center = Offset(cx - 24f, arduinoY + 16f))

    // 3. 2x 18650 PİL YUVASI (Arka taraftaki iki yeşil pil)
    val batY = cy + 76f
    drawRoundRect(
        color = Color(0xFF0F172A), // Siyah Pil Yuvası
        topLeft = Offset(cx - 36f, batY - 14f),
        size = Size(72f, 28f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    // Üstteki 18650 Yeşil Pil
    drawRoundRect(
        color = Color(0xFF22C55E),
        topLeft = Offset(cx - 30f, batY - 11f),
        size = Size(60f, 10f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    // Alttaki 18650 Yeşil Pil
    drawRoundRect(
        color = Color(0xFF22C55E),
        topLeft = Offset(cx - 30f, batY + 2f),
        size = Size(60f, 10f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    // Pil Gümüş Kutupları
    drawRect(Color(0xFFCBD5E1), topLeft = Offset(cx + 30f, batY - 9f), size = Size(2.5f, 6f))
    drawRect(Color(0xFFCBD5E1), topLeft = Offset(cx + 30f, batY + 4f), size = Size(2.5f, 6f))
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawServoAndUltrasonicHead(
    cx: Float,
    cy: Float,
    telemetry: RobotTelemetry,
    sonarWavePhase: Float,
    colors: AppColors,
    textMeasurer: TextMeasurer
) {
    // 1. SG90 MİKRO SERVO TABANI (MDF Yükseltici & Mavi Şeffaf Servo Gövdesi)
    drawRoundRect(
        color = Color(0xFFB08958), // MDF Servo Yükseltici
        topLeft = Offset(cx - 22f, cy - 8f),
        size = Size(44f, 26f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    // 4 vida
    drawCircle(Color(0xFFCBD5E1), radius = 1.2f, center = Offset(cx - 18f, cy - 4f))
    drawCircle(Color(0xFFCBD5E1), radius = 1.2f, center = Offset(cx + 18f, cy - 4f))
    drawCircle(Color(0xFFCBD5E1), radius = 1.2f, center = Offset(cx - 18f, cy + 14f))
    drawCircle(Color(0xFFCBD5E1), radius = 1.2f, center = Offset(cx + 18f, cy + 14f))

    // Şeffaf Mavi SG90 Gövdesi
    drawRoundRect(
        color = Color(0xFF2563EB).copy(alpha = 0.85f),
        topLeft = Offset(cx - 14f, cy - 4f),
        size = Size(28f, 18f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // 2. ROTATING ASSEMBLY (Servo Açı Dönüşü: 0° = Sağ 90°, 90° = Merkez, 180° = Sol 90°)
    // Canvas dönüş açısı: 0 derece = robot yönünde düz ileri (90 - servoAngle)
    val rotationAngle = (90 - telemetry.servoAngle).toFloat()

    rotate(degrees = rotationAngle, pivot = Offset(cx, cy)) {
        // Beyaz Plastik Servo Kolu (Horn)
        drawCircle(color = Color(0xFFF8FAFC), radius = 5f, center = Offset(cx, cy))
        drawRoundRect(
            color = Color(0xFFF8FAFC),
            topLeft = Offset(cx - 4f, cy - 14f),
            size = Size(8f, 16f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // Siyah Pleksi U-Braket
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(cx - 26f, cy - 18f),
            size = Size(52f, 8f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // HC-SR04 MAVİ PCB KARTI
        val pcbW = 54f
        val pcbH = 22f
        val pcbY = cy - 36f
        drawRoundRect(
            color = Color(0xFF1E40AF),
            topLeft = Offset(cx - pcbW / 2f, pcbY),
            size = Size(pcbW, pcbH),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // Ortadaki Gümüş Kristal Rezonatör
        drawRoundRect(
            color = Color(0xFF94A3B8),
            topLeft = Offset(cx - 4f, pcbY + 7f),
            size = Size(8f, 8f),
            cornerRadius = CornerRadius(1f, 1f)
        )

        // SOL GÖZ (T - Transmitter / Verici Transdüser)
        val eyeRadius = 9f
        val leftEyeCenter = Offset(cx - 15f, pcbY + 11f)
        // Gümüş silindirik kovan
        drawCircle(color = Color(0xFFE2E8F0), radius = eyeRadius, center = leftEyeCenter)
        // Siyah petekli ızgara
        drawCircle(color = Color(0xFF0F172A), radius = eyeRadius - 2f, center = leftEyeCenter)
        // Beyaz 'T' yazısı
        drawText(
            textMeasurer = textMeasurer,
            text = "T",
            topLeft = Offset(leftEyeCenter.x - 3f, leftEyeCenter.y - 5.5f),
            style = TextStyle(color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
        )

        // SAĞ GÖZ (R - Receiver / Alıcı Transdüser)
        val rightEyeCenter = Offset(cx + 15f, pcbY + 11f)
        // Gümüş silindirik kovan
        drawCircle(color = Color(0xFFE2E8F0), radius = eyeRadius, center = rightEyeCenter)
        // Siyah petekli ızgara
        drawCircle(color = Color(0xFF0F172A), radius = eyeRadius - 2f, center = rightEyeCenter)
        // Beyaz 'R' yazısı
        drawText(
            textMeasurer = textMeasurer,
            text = "R",
            topLeft = Offset(rightEyeCenter.x - 3f, rightEyeCenter.y - 5.5f),
            style = TextStyle(color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
        )

        // 3. AKTİF ULTRASONİK SONAR IŞINI (CANLI DALGA YAYILIMI)
        if (telemetry.hasSignal && telemetry.frontDistance > 0) {
            drawLiveSonarBeam(
                origin = Offset(cx, pcbY),
                distanceCm = telemetry.frontDistance,
                sonarWavePhase = sonarWavePhase,
                textMeasurer = textMeasurer
            )
        } else {
            // Sinyal Yok Durumunda Statik Kesikli Çizgi
            drawLine(
                color = Color.Gray.copy(alpha = 0.4f),
                start = Offset(cx, pcbY),
                end = Offset(cx, pcbY - 40f),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawLiveSonarBeam(
    origin: Offset,
    distanceCm: Int,
    sonarWavePhase: Float,
    textMeasurer: TextMeasurer
) {
    // 1 cm = 2.2 px ölçekleme (Maksimum 180px sınır)
    val beamLen = (distanceCm * 2.2f).coerceIn(25f, 175f)

    val beamColor = when {
        distanceCm <= 15 -> Color(0xFFEF4444) // TEHLİKE / KIRMIZI
        distanceCm <= 30 -> Color(0xFFF59E0B) // UYARI / SARI
        else -> Color(0xFF10B981)             // GÜVENLİ / YEŞİL
    }

    // Yayılma konisi (HC-SR04'ün 30 derecelik yayılım açısı)
    val halfWidth = beamLen * 0.28f
    val conePath = Path().apply {
        moveTo(origin.x, origin.y)
        lineTo(origin.x - halfWidth, origin.y - beamLen)
        lineTo(origin.x + halfWidth, origin.y - beamLen)
        close()
    }

    // Şeffaf Gradyan Koni
    drawPath(
        path = conePath,
        brush = Brush.verticalGradient(
            colors = listOf(beamColor.copy(alpha = 0.45f), beamColor.copy(alpha = 0.05f)),
            startY = origin.y,
            endY = origin.y - beamLen
        )
    )

    // Konsantrik İlerleyen Sonar Dalgaları
    val waveCount = 3
    for (i in 0 until waveCount) {
        val waveFraction = ((sonarWavePhase + i / waveCount.toFloat()) % 1f)
        val currentR = beamLen * waveFraction
        val currentHalfW = currentR * 0.28f
        val waveY = origin.y - currentR

        if (currentR > 8f) {
            val arcPath = Path().apply {
                moveTo(origin.x - currentHalfW, waveY)
                quadraticTo(origin.x, waveY - 4f, origin.x + currentHalfW, waveY)
            }
            drawPath(
                path = arcPath,
                color = beamColor.copy(alpha = (1f - waveFraction) * 0.8f),
                style = Stroke(width = 1.5f)
            )
        }
    }

    // Mesafe Hedef Çizgisi ve Etiketi
    drawLine(
        color = beamColor,
        start = Offset(origin.x - halfWidth - 6f, origin.y - beamLen),
        end = Offset(origin.x + halfWidth + 6f, origin.y - beamLen),
        strokeWidth = 2.5f
    )

    // Mesafe Metin Baloncuğu
    val distText = "$distanceCm cm"
    drawText(
        textMeasurer = textMeasurer,
        text = distText,
        topLeft = Offset(origin.x - 18f, origin.y - beamLen - 16f),
        style = TextStyle(
            color = beamColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            background = Color(0xCC0F172A)
        )
    )
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawCorridorRays(
    cx: Float,
    cy: Float,
    telemetry: RobotTelemetry,
    colors: AppColors,
    textMeasurer: TextMeasurer
) {
    // Sol Hafıza Işını (Sol tarama açısı: 150 derece)
    if (telemetry.leftDistance > 0) {
        val leftLen = (telemetry.leftDistance * 1.5f).coerceIn(30f, 130f)
        val angleRad = 150.0 * kotlin.math.PI / 180.0
        val endX = cx + (leftLen * sin(angleRad)).toFloat()
        val endY = cy - (leftLen * cos(angleRad)).toFloat()

        drawLine(
            color = Color(0xFF3B82F6).copy(alpha = 0.5f),
            start = Offset(cx, cy),
            end = Offset(endX, endY),
            strokeWidth = 1.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
        )
        drawCircle(Color(0xFF3B82F6), radius = 3f, center = Offset(endX, endY))
        drawText(
            textMeasurer = textMeasurer,
            text = "Sol: ${telemetry.leftDistance}cm",
            topLeft = Offset(endX - 35f, endY - 14f),
            style = TextStyle(color = Color(0xFF60A5FA), fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
        )
    }

    // Sağ Hafıza Işını (Sağ tarama açısı: 30 derece)
    if (telemetry.rightDistance > 0) {
        val rightLen = (telemetry.rightDistance * 1.5f).coerceIn(30f, 130f)
        val angleRad = 30.0 * kotlin.math.PI / 180.0
        val endX = cx + (rightLen * sin(angleRad)).toFloat()
        val endY = cy - (rightLen * cos(angleRad)).toFloat()

        drawLine(
            color = Color(0xFF3B82F6).copy(alpha = 0.5f),
            start = Offset(cx, cy),
            end = Offset(endX, endY),
            strokeWidth = 1.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
        )
        drawCircle(Color(0xFF3B82F6), radius = 3f, center = Offset(endX, endY))
        drawText(
            textMeasurer = textMeasurer,
            text = "Sağ: ${telemetry.rightDistance}cm",
            topLeft = Offset(endX + 4f, endY - 14f),
            style = TextStyle(color = Color(0xFF60A5FA), fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
        )
    }
}
