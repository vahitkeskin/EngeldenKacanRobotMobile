package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.LanguageMode
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.theme.ThemeMode
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.tooling.preview.Preview

/**
 * 🚀 SplashScreen - Professional Aerospace & Robotics Diagnostic Boot Loader
 * KMP (Android, iOS, Desktop) için %100 özgün, tüm tema (Koyu/Açık/Sistem)
 * ve tüm dil (Türkçe/English/Sistem) desteklerini barındıran başlangıç ekranı.
 *
 * En altta "vahitkeskin tarafından geliştirildi" imzasını taşır.
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    languageMode: LanguageMode = LanguageMode.SYSTEM,
    onThemeSelected: (ThemeMode) -> Unit = {},
    onLanguageSelected: (LanguageMode) -> Unit = {}
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    // 0f -> 1f Arası İlerleme (2000 ms)
    var progress by remember { mutableStateOf(0f) }
    var stepIndex by remember { mutableStateOf(0) }

    // Sürekli dönen radar ışını animasyonu (360 derece)
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

    // Ultrasonik ses dalgası yayılma animasyonu (0f -> 1f)
    val wavePulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePulse"
    )

    // Dinamik dil destekli donanım adımları
    val bootSteps = listOf(
        Strings.splashStep1, // [1/5] I2C Veri Yolu (0x08) Başlatılıyor...
        Strings.splashStep2, // [2/5] HC-SR04 Ultrasonik Sonar Testi...
        Strings.splashStep3, // [3/5] SG90 Mikro Servo Sıfırlanıyor (90°)...
        Strings.splashStep4, // [4/5] L298N Çift Motor Sürücü Hazır...
        Strings.splashStep5  // [5/5] Telemetri & Dijital İkiz Hazır!
    )

    // Donanım önyükleme zamanlayıcısı (Boot sequence)
    LaunchedEffect(Unit) {
        val totalDurationMs = 2000L
        val intervalMs = 25L
        val totalSteps = totalDurationMs / intervalMs
        var currentStep = 0

        while (currentStep <= totalSteps) {
            val currentProgress = currentStep.toFloat() / totalSteps.toFloat()
            progress = currentProgress
            stepIndex = when {
                currentProgress < 0.25f -> 0
                currentProgress < 0.50f -> 1
                currentProgress < 0.75f -> 2
                currentProgress < 0.95f -> 3
                else -> 4
            }
            delay(intervalMs)
            currentStep++
        }
        delay(180)
        onFinished()
    }

    val bgGradient = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF070B14),
                Color(0xFF0F172A),
                Color(0xFF090D1A)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFF8FAFC),
                Color(0xFFF1F5F9),
                Color(0xFFE2E8F0)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onFinished()
            }
    ) {
        // ÜST ALAN: TEMA VE DİL DEĞİŞTİRME KONTROLLERİ (Hızlı Tercih)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dil Seçim Çipi (TR / EN / SYS)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF1E293B).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.9f))
                    .border(0.8.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf(
                    LanguageMode.SYSTEM to "🌐",
                    LanguageMode.TR to "🇹🇷",
                    LanguageMode.EN to "🇬🇧"
                ).forEach { (mode, flag) ->
                    val isSelected = languageMode == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF2563EB) else Color.Transparent)
                            .clickable {
                                onLanguageSelected(mode)
                                Strings.currentLanguage = mode
                            }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = flag,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Tema Seçim Çipi (Sys / Dark / Light)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF1E293B).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.9f))
                    .border(0.8.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf(
                    ThemeMode.SYSTEM to "💻",
                    ThemeMode.DARK to "🌙",
                    ThemeMode.LIGHT to "☀️"
                ).forEach { (mode, icon) ->
                    val isSelected = themeMode == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF2563EB) else Color.Transparent)
                            .clickable { onThemeSelected(mode) }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = icon,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // ORTA ALAN: AMBLEM, BAŞLIK, ADIM VE İLERLEME ÇUBUĞU
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            // 1. EMBLEM: ALTIGEN KALKAN + HC-SR04 TRANSDÜSERLERİ + DÖNEN RADAR IŞINI
            HardwareRobotEmblem(
                sweepAngle = sweepAngle,
                wavePulse = wavePulse,
                isDark = isDark,
                modifier = Modifier.size(145.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. PROJE BAŞLIĞI & TEKNİK ETİKET
            Text(
                text = Strings.splashTitle,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = Strings.splashSubtitle,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                letterSpacing = 1.3.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 3. DONANIM TEŞHİS ADIMI (HARDWARE STEPPER PILL)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isDark) Color(0xFF1E293B).copy(alpha = 0.92f)
                        else Color.White.copy(alpha = 0.95f)
                    )
                    .border(
                        1.dp,
                        if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                // Yanıp sönen durum noktası
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(
                            if (stepIndex == 4) Color(0xFF10B981)
                            else if (isDark) Color(0xFF38BDF8)
                            else Color(0xFF0284C7)
                        )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "[${stepIndex + 1}/5] ${bootSteps.getOrElse(stepIndex) { Strings.splashStep5 }}",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (stepIndex == 4) {
                        if (isDark) Color(0xFF6EE7B7) else Color(0xFF059669)
                    } else {
                        if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                    },
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. HASSAS İLERLEME ÇUBUĞU (PRECISION HARDWARE PROGRESS BAR)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                    .border(
                        0.6.dp,
                        if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                        RoundedCornerShape(3.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.coerceIn(0.01f, 1f))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF2563EB),
                                    Color(0xFF06B6D4),
                                    Color(0xFF10B981)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            // Yüzde & Durum Bilgisi
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "%${(progress * 100).toInt()}",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = if (progress >= 0.99f) {
                        if (Strings.isTurkish()) "SİSTEM ÇEVRİMİÇİ" else "SYSTEM ONLINE"
                    } else {
                        if (Strings.isTurkish()) "ÖNYÜKLEME..." else "BOOTING..."
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (progress >= 0.99f) Color(0xFF10B981) else Color(0xFF94A3B8),
                    letterSpacing = 0.5.sp
                )
            }
        }

        // EN ALT ALAN: GELİŞTİRİCİ İMZASI VE DONANIM KİMLİĞİ
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = Strings.splashHardwareBadge,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color(0xFF64748B) else Color(0xFF64748B),
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${Strings.splashVersion} • ${Strings.splashSkipHint}",
                fontSize = 8.5.sp,
                color = if (isDark) Color(0xFF475569) else Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // "vahitkeskin tarafından geliştirildi" İMZASI (Geliştirici Rozeti)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isDark) Color(0xFF1E293B).copy(alpha = 0.7f)
                        else Color.White.copy(alpha = 0.85f)
                    )
                    .border(
                        0.7.dp,
                        if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "⚡",
                    fontSize = 10.sp,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = Strings.splashDevelopedBy,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                    letterSpacing = 0.4.sp
                )
            }
        }
    }
}

/**
 * 🛡️ HardwareRobotEmblem:
 * Canvas ile çizilen altıgen gövde kalkanı, HC-SR04 sonar transdüserleri,
 * dairesel radar ızgarası ve döner tarama ışını.
 */
@Composable
private fun HardwareRobotEmblem(
    sweepAngle: Float,
    wavePulse: Float,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = (w.coerceAtMost(h) / 2f) - 6.dp.toPx()

        // 1. DÜZGÜN ALTIGEN KALKAN YOLU (HEXAGON SHIELD)
        val hexPath = Path().apply {
            for (i in 0 until 6) {
                val angleRad = (60.0 * i - 30.0) * (kotlin.math.PI / 180.0)
                val x = cx + (radius * cos(angleRad)).toFloat()
                val y = cy + (radius * sin(angleRad)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }

        // Kalkan İç Arka Planı (Radial Degrade)
        val shieldColors = if (isDark) {
            listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF060913))
        } else {
            listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9), Color(0xFFE2E8F0))
        }

        drawPath(
            path = hexPath,
            brush = Brush.radialGradient(
                colors = shieldColors,
                center = Offset(cx, cy),
                radius = radius
            )
        )

        // Kalkan Dış Çerçevesi
        drawPath(
            path = hexPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF2563EB),
                    Color(0xFF38BDF8),
                    Color(0xFF1D4ED8)
                )
            ),
            style = Stroke(width = 2.2.dp.toPx())
        )

        // 2. RADAR KONSANTRİK HALKALARI
        val ring1 = radius * 0.45f
        val ring2 = radius * 0.75f
        val ringColor = if (isDark) Color(0xFF38BDF8).copy(alpha = 0.12f) else Color(0xFF0284C7).copy(alpha = 0.15f)

        drawCircle(
            color = ringColor,
            radius = ring1,
            center = Offset(cx, cy),
            style = Stroke(width = 1.dp.toPx())
        )
        drawCircle(
            color = ringColor,
            radius = ring2,
            center = Offset(cx, cy),
            style = Stroke(width = 1.dp.toPx())
        )

        // 3. DÖNEN RADAR IŞINI (SWEEPING BEAM)
        rotate(degrees = sweepAngle, pivot = Offset(cx, cy)) {
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        if (isDark) Color(0xFF38BDF8).copy(alpha = 0.8f) else Color(0xFF0284C7).copy(alpha = 0.8f)
                    ),
                    start = Offset(cx, cy),
                    end = Offset(cx, cy - radius * 0.85f)
                ),
                start = Offset(cx, cy),
                end = Offset(cx, cy - radius * 0.85f),
                strokeWidth = 2.dp.toPx()
            )
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF0284C7).copy(alpha = if (isDark) 0.25f else 0.18f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy)
                ),
                startAngle = 230f,
                sweepAngle = 40f,
                useCenter = true,
                topLeft = Offset(cx - radius * 0.85f, cy - radius * 0.85f),
                size = Size(radius * 1.7f, radius * 1.7f)
            )
        }

        // 4. ULTRASONİK SES DALGASI YAYILMASI (SONAR PULSE ARCS)
        val maxWaveR = radius * 0.9f
        val waveR = radius * 0.35f + (wavePulse * (maxWaveR - radius * 0.35f))
        val waveAlpha = (1f - wavePulse).coerceIn(0f, 1f) * 0.5f

        drawArc(
            color = (if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)).copy(alpha = waveAlpha),
            startAngle = 210f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(cx - waveR, cy - waveR),
            size = Size(waveR * 2, waveR * 2),
            style = Stroke(width = 2.dp.toPx())
        )

        // 5. HC-SR04 MODÜL GÖVDESİ (PCB BOARD)
        val pcbW = 76.dp.toPx()
        val pcbH = 34.dp.toPx()
        val pcbTop = cy - pcbH / 2f + 4.dp.toPx()
        val pcbLeft = cx - pcbW / 2f

        drawRoundRect(
            color = Color(0xFF0369A1), // Klasik HC-SR04 Mavi PCB rengi
            topLeft = Offset(pcbLeft, pcbTop),
            size = Size(pcbW, pcbH),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        drawRoundRect(
            color = Color(0xFF38BDF8).copy(alpha = 0.5f),
            topLeft = Offset(pcbLeft, pcbTop),
            size = Size(pcbW, pcbH),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )

        // 6. ÇİFT TRANSDÜSER KOVANLARI: SOL (T), SAĞ (R)
        val barrelR = 12.dp.toPx()
        val leftX = cx - 21.dp.toPx()
        val rightX = cx + 21.dp.toPx()
        val barrelY = cy + 4.dp.toPx()

        listOf(leftX, rightX).forEach { bx ->
            drawCircle(
                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF94A3B8),
                radius = barrelR,
                center = Offset(bx, barrelY)
            )
            drawCircle(
                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                radius = barrelR - 1.8.dp.toPx(),
                center = Offset(bx, barrelY)
            )
            drawCircle(
                color = Color(0xFF0284C7),
                radius = 2.dp.toPx(),
                center = Offset(bx, barrelY)
            )
        }

        // Merkez Kristal Osilatör (4.000 MHz)
        val oscW = 8.dp.toPx()
        val oscH = 14.dp.toPx()
        drawRoundRect(
            color = Color(0xFF94A3B8),
            topLeft = Offset(cx - oscW / 2f, barrelY - oscH / 2f),
            size = Size(oscW, oscH),
            cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
        )
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Preview
@Composable
private fun SplashScreenDarkPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp) // Ekranın en altı bnv üstünde
    ) {
        SplashScreen(
            onFinished = {},
            themeMode = ThemeMode.DARK,
            languageMode = LanguageMode.TR
        )
    }
}

@Preview
@Composable
private fun SplashScreenLightPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp) // Ekranın en altı bnv üstünde
    ) {
        SplashScreen(
            onFinished = {},
            themeMode = ThemeMode.LIGHT,
            languageMode = LanguageMode.EN
        )
    }
}
