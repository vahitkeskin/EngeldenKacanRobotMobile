package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.SetSystemBarsTheme
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotState
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.WheelDirection
import com.vahitkeskin.engeldenkacanrobotmobile.domain.repository.ITelemetryRepository
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.LanguageMode
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Camera3D
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Robot3DViewport
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors
import com.vahitkeskin.engeldenkacanrobotmobile.theme.ThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 🤖 Robot3DScreen
 * Tam ekranı kaplayan, iki parmakla zoom, çevirme ve gezinme yapılabilen
 * 60-120 FPS akıcı 3D Dijital İkiz Ekranı.
 *
 * Sıfırla, Üstten, Alttan ve İzometrik butonları animasyonlu kamera geçişi yapar.
 * 3D Pasif/Aktif butonu ile ekranın 3D dokunma yakalama özelliği kilitlenebilir,
 * böylece BNV/HorizontalPager ekranları arasında parmakla kaydırma serbest kalır.
 */
@Composable
fun Robot3DScreen(
    repository: ITelemetryRepository,
    is3dActive: Boolean = false,
    onToggle3dActive: () -> Unit = {},
    onNavigateToTelemetry: () -> Unit = {},
    themeMode: ThemeMode,
    languageMode: LanguageMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onLanguageSelected: (LanguageMode) -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    SetSystemBarsTheme(isDark)
    val colors = if (isDark) AppColors.Dark else AppColors.Light
    Strings.currentLanguage = languageMode

    val telemetry by repository.telemetry.collectAsState()
    val isConnected by repository.isConnected.collectAsState()

    // 3D Kamera Durumu
    val camera = remember { Camera3D() }

    val coroutineScope = rememberCoroutineScope()
    var animationJob by remember { mutableStateOf<Job?>(null) }
    var isTourPlaying by remember { mutableStateOf(false) }

    fun stopTour() {
        if (isTourPlaying) {
            animationJob?.cancel()
            animationJob = null
            isTourPlaying = false
        }
    }

    // Animasyonlu yumuşak kamera geçiş motoru (Kullanıcı İsteği: Bulunduğu konumdan eski konumuna animasyonla geçsin)
    fun animateCameraTo(
        targetYaw: Float,
        targetPitch: Float,
        targetZoom: Float,
        targetPanX: Float = 0f,
        targetPanY: Float = 0f,
        durationMs: Int = 600
    ) {
        stopTour()
        animationJob?.cancel()
        animationJob = coroutineScope.launch {
            val startYaw = camera.yaw
            val startPitch = camera.pitch
            val startZoom = camera.zoom
            val startPanX = camera.panX
            val startPanY = camera.panY

            // En kısa açısal rotasyon yönü hesabı
            val deltaYaw = run {
                var d = (targetYaw - startYaw) % 360f
                if (d > 180f) d -= 360f
                if (d < -180f) d += 360f
                d
            }

            val anim = Animatable(0f)
            anim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = durationMs, easing = FastOutSlowInEasing)
            ) {
                val t = value
                camera.yaw = startYaw + deltaYaw * t
                camera.pitch = startPitch + (targetPitch - startPitch) * t
                camera.zoom = startZoom + (targetZoom - startZoom) * t
                camera.panX = startPanX + (targetPanX - startPanX) * t
                camera.panY = startPanY + (targetPanY - startPanY) * t
            }
        }
    }

    // Kullanıcı İsteği: "Animasyonlu bir şekilde oynat bakalım, baştan sona animasyonlu olsun"
    // Sinematik 3D Tur: 360° dönüş, yakınlaşma (zoom in), üstten, alttan saydam zemin ve başlangıç
    fun playTour() {
        animationJob?.cancel()
        isTourPlaying = true
        if (!is3dActive) {
            onToggle3dActive()
        }

        animationJob = coroutineScope.launch {
            try {
                // 1. 360 Derece Tam Eksen Turu (4 saniye)
                val fullYawStart = camera.yaw
                val startPitch = camera.pitch
                val animOrbit = Animatable(0f)
                animOrbit.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 4200, easing = LinearEasing)
                ) {
                    camera.yaw = fullYawStart + 360f * value
                    camera.pitch = startPitch + 10f * kotlin.math.sin(value * 2f * kotlin.math.PI.toFloat())
                }

                // 2. Elektronik ve Servo Kafaya Yakınlaşma (Zoom-In) (2 saniye)
                val animZoom = Animatable(0f)
                val zoomStartYaw = camera.yaw
                val zoomStartPitch = camera.pitch
                val zoomStartZoom = camera.zoom
                animZoom.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
                ) {
                    val t = value
                    camera.yaw = zoomStartYaw + (20f - zoomStartYaw) * t
                    camera.pitch = zoomStartPitch + (25f - zoomStartPitch) * t
                    camera.zoom = zoomStartZoom + (3.3f - zoomStartZoom) * t
                    camera.panY = 15f * t
                }

                // 3. Üstten Görünüm (Top View) (2 saniye)
                val animTop = Animatable(0f)
                val topStartYaw = camera.yaw
                val topStartPitch = camera.pitch
                val topStartZoom = camera.zoom
                val topStartPanY = camera.panY
                animTop.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
                ) {
                    val t = value
                    camera.yaw = topStartYaw + (0f - topStartYaw) * t
                    camera.pitch = topStartPitch + (80f - topStartPitch) * t
                    camera.zoom = topStartZoom + (2.0f - topStartZoom) * t
                    camera.panY = topStartPanY * (1f - t)
                }

                // 4. Alttan Saydam Zemin & Motorlar Görünümü (Bottom View) (2.5 saniye)
                val animBottom = Animatable(0f)
                animBottom.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 2500, easing = FastOutSlowInEasing)
                ) {
                    val t = value
                    camera.yaw = 0f
                    camera.pitch = 80f - 155f * t // 80 -> -75f
                    camera.zoom = 2.0f
                }

                // 5. Başlangıç Konumuna Yumuşak Dönüş (Reset Home) (1.8 saniye)
                val animHome = Animatable(0f)
                val homeStartPitch = camera.pitch
                animHome.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
                ) {
                    val t = value
                    camera.yaw = -35f * t
                    camera.pitch = homeStartPitch + (32f - homeStartPitch) * t
                    camera.zoom = 2.0f
                    camera.panX = 0f
                    camera.panY = 0f
                }
            } finally {
                isTourPlaying = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPrimary)
    ) {
        // 1. TAM EKRANI KAPLAYAN 3D VİEWPORT (Robot Tam Ortada ve Ekrana Hakim)
        Robot3DViewport(
            telemetry = telemetry,
            camera = camera,
            is3dActive = is3dActive,
            isDark = isDark,
            modifier = Modifier.fillMaxSize(),
            onDoubleTapReset = {
                animateCameraTo(-35f, 32f, 2.0f, 0f, 0f)
            },
            onUserInteraction = {
                stopTour()
            }
        )

        // 2. ÜST YÜZEN BİLGİ VE KAMERA KONTROL KARTI
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Başlık & FSM Rozeti
            FloatingHeader(telemetry, colors, onNavigateToTelemetry)

            // Kamera & Hızlı Bakış Çubuğu
            FloatingCameraBar(
                is3dActive = is3dActive,
                onToggle3dActive = onToggle3dActive,
                onReset = { animateCameraTo(-35f, 32f, 2.0f, 0f, 0f) },
                onTopView = { animateCameraTo(0f, 80f, 2.0f, 0f, 0f) },
                onBottomView = { animateCameraTo(0f, -80f, 2.0f, 0f, 0f) },
                isTourPlaying = isTourPlaying,
                onPlayTour = { playTour() },
                onStopTour = { stopTour() },
                themeMode = themeMode,
                languageMode = languageMode,
                colors = colors,
                onThemeSelected = onThemeSelected,
                onLanguageSelected = onLanguageSelected
            )
        }

        // 3. ALT YÜZEN TELEMETRİ VE BİLEŞEN METRİK PANELİ + JEST İPUCU
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 76.dp) // Bottom Navigation Bar için güvenli boşluk
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Jest / Mod İpucu Rozeti
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isTourPlaying) Color(0xCC78350F) else if (is3dActive) Color(0xCC0F172A) else Color(0xB31E293B))
                    .border(
                        0.6.dp,
                        if (isTourPlaying) Color(0xFFF59E0B) else if (is3dActive) Color(0x6610B981) else Color(0x33FFFFFF),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isTourPlaying) {
                        "🎬 Sinematik Animasyon Oynatılıyor... (Durdurmak için ekrana veya butona dokunun)"
                    } else if (is3dActive) {
                        "🎮 3D Aktif: 👆 Tek Parmak: 360° Döndür • ✌️ İki Parmak: Gezin (Pan) & Zoom • Çift Dokun: Sıfırla"
                    } else {
                        "👆 Sayfa Kaydırma Modu: Ekranı kaydırabilirsiniz (3D için butona dokunun)"
                    },
                    color = if (isTourPlaying) Color(0xFFFDE047) else if (is3dActive) Color(0xFF6EE7B7) else Color(0xFFE2E8F0),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            FloatingHardwareMetrics(telemetry, colors)
        }
    }
}

@Composable
private fun FloatingHeader(
    telemetry: RobotTelemetry,
    colors: AppColors,
    onNavigateToTelemetry: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.bgSecondary.copy(alpha = 0.92f))
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Geri Butonu: Tıklandığında doğrudan Telemetri ekranına geri döner
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.bgPrimary)
                    .border(0.6.dp, colors.border, RoundedCornerShape(6.dp))
                    .clickable(onClick = onNavigateToTelemetry)
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⬅️ " + Strings.navTelemetry,
                    color = colors.textPrimary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (telemetry.hasSignal) Color(0xFF10B981) else Color(0xFFEF4444))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "🤖 " + Strings.robotTwinTitle,
                    color = colors.textPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "3D Etkileşimli Motor • I2C (0x08)",
                    color = colors.textMuted,
                    fontSize = 8.5.sp
                )
            }
        }

        val badgeText = if (!telemetry.hasSignal) {
            Strings.noSignal.uppercase()
        } else {
            when (telemetry.state) {
                RobotState.CRUISING -> "SEYİR (CRUISE)"
                RobotState.AVOIDING -> "KAÇIŞ (AVOID)"
                RobotState.STOPPED -> "DURDURULDU"
                else -> Strings.noSignal.uppercase()
            }
        }
        val badgeColor = if (telemetry.hasSignal) telemetry.state.badgeColor else Color(0xFF6B7280)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(badgeColor)
                .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Text(
                text = badgeText,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FloatingCameraBar(
    is3dActive: Boolean,
    onToggle3dActive: () -> Unit,
    onReset: () -> Unit,
    onTopView: () -> Unit,
    onBottomView: () -> Unit,
    isTourPlaying: Boolean,
    onPlayTour: () -> Unit,
    onStopTour: () -> Unit,
    themeMode: ThemeMode,
    languageMode: LanguageMode,
    colors: AppColors,
    onThemeSelected: (ThemeMode) -> Unit,
    onLanguageSelected: (LanguageMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.bgSecondary.copy(alpha = 0.90f))
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 6.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hızlı Kamera ve 3D Mod Butonları (Yatay Kaydırılabilir)
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 3D Etkileşim Kilidi / Pasif-Aktif Butonu
            QuickPill(
                label = if (is3dActive) "🔓 3D Aktif" else "🔒 3D Pasif",
                colors = colors,
                bgColor = if (is3dActive) Color(0xFF10B981) else colors.bgPrimary,
                textColor = if (is3dActive) Color.White else colors.textPrimary,
                borderColor = if (is3dActive) Color(0xFF059669) else colors.border,
                onClick = onToggle3dActive
            )

            // Sinematik Animasyon Oynat / Durdur Butonu (Kullanıcı İsteği: "baştan sona animasyonlu olsun")
            QuickPill(
                label = if (isTourPlaying) "⏸️ Durdur" else "🎬 Oynat",
                colors = colors,
                bgColor = if (isTourPlaying) Color(0xFFF59E0B) else colors.bgPrimary,
                textColor = if (isTourPlaying) Color.White else colors.textPrimary,
                borderColor = if (isTourPlaying) Color(0xFFD97706) else colors.border,
                onClick = {
                    if (isTourPlaying) onStopTour() else onPlayTour()
                }
            )

            // Animasyonlu Sıfırla
            QuickPill("🎯 Sıfırla", colors, onClick = onReset)
            // Animasyonlu Üstten
            QuickPill("🔝 Üstten", colors, onClick = onTopView)
            // Animasyonlu Alttan
            QuickPill("🔽 Alttan", colors, onClick = onBottomView)
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Tema ve Dil Mini Butonları
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            QuickPill(
                label = if (themeMode == ThemeMode.DARK) "🌙" else if (themeMode == ThemeMode.LIGHT) "☀️" else "💻",
                colors = colors
            ) {
                val next = when (themeMode) {
                    ThemeMode.SYSTEM -> ThemeMode.DARK
                    ThemeMode.DARK -> ThemeMode.LIGHT
                    ThemeMode.LIGHT -> ThemeMode.SYSTEM
                }
                onThemeSelected(next)
            }

            QuickPill(
                label = if (languageMode == LanguageMode.TR) "🇹🇷" else if (languageMode == LanguageMode.EN) "🇬🇧" else "🌐",
                colors = colors
            ) {
                val next = when (languageMode) {
                    LanguageMode.SYSTEM -> LanguageMode.TR
                    LanguageMode.TR -> LanguageMode.EN
                    LanguageMode.EN -> LanguageMode.SYSTEM
                }
                onLanguageSelected(next)
            }
        }
    }
}

@Composable
private fun QuickPill(
    label: String,
    colors: AppColors,
    bgColor: Color = colors.bgPrimary,
    textColor: Color = colors.textPrimary,
    borderColor: Color = colors.border,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(0.6.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
private fun FloatingHardwareMetrics(
    telemetry: RobotTelemetry,
    colors: AppColors
) {
    val leftState = telemetry.leftWheelState
    val rightState = telemetry.rightWheelState

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Sol Tekerlek
        MiniMetricPill(
            title = "🏎️ " + Strings.leftWheel,
            value = if (telemetry.hasSignal) "${leftState.direction.symbol} ${leftState.speedPwm}" else "--",
            color = if (leftState.direction == WheelDirection.FORWARD) Color(0xFF10B981) else if (leftState.direction == WheelDirection.BACKWARD) Color(0xFFEF4444) else colors.textMuted,
            colors = colors,
            modifier = Modifier.weight(1f)
        )

        // Sağ Tekerlek
        MiniMetricPill(
            title = "🏎️ " + Strings.rightWheel,
            value = if (telemetry.hasSignal) "${rightState.speedPwm} ${rightState.direction.symbol}" else "--",
            color = if (rightState.direction == WheelDirection.FORWARD) Color(0xFF10B981) else if (rightState.direction == WheelDirection.BACKWARD) Color(0xFFEF4444) else colors.textMuted,
            colors = colors,
            modifier = Modifier.weight(1f)
        )

        // SG90 Servo
        MiniMetricPill(
            title = "🔄 Servo",
            value = if (telemetry.hasSignal) "${telemetry.servoAngle}°" else "--°",
            color = Color(0xFF3B82F6),
            colors = colors,
            modifier = Modifier.weight(1f)
        )

        // HC-SR04 Sensör
        val distColor = when {
            !telemetry.hasSignal -> colors.textMuted
            telemetry.isDanger -> Color(0xFFEF4444)
            telemetry.isWarning -> Color(0xFFF59E0B)
            else -> Color(0xFF10B981)
        }
        MiniMetricPill(
            title = "🎯 HC-SR04",
            value = if (telemetry.hasSignal && telemetry.frontDistance > 0) "${telemetry.frontDistance}cm" else "--",
            color = distColor,
            colors = colors,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MiniMetricPill(
    title: String,
    value: String,
    color: Color,
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.bgSecondary.copy(alpha = 0.92f))
            .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = colors.textMuted,
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Text(
            text = value,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}
