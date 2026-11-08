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
import com.vahitkeskin.engeldenkacanrobotmobile.data.MockTelemetryRepository
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
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
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

    // İki açı arasındaki en kısa rotasyon farkını hesaplar (-180°..+180°)
    fun shortestDeltaYaw(from: Float, to: Float): Float {
        var d = (to - from) % 360f
        if (d > 180f) d -= 360f
        if (d < -180f) d += 360f
        return d
    }

    // Animasyonlu yumuşak kamera geçiş motoru
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

            val deltaYaw = shortestDeltaYaw(startYaw, targetYaw)

            val anim = Animatable(0f)
            anim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = durationMs, easing = FastOutSlowInEasing)
            ) {
                val t = value
                camera.yaw = (startYaw + deltaYaw * t) % 360f
                camera.pitch = startPitch + (targetPitch - startPitch) * t
                camera.zoom = startZoom + (targetZoom - startZoom) * t
                camera.panX = startPanX + (targetPanX - startPanX) * t
                camera.panY = startPanY + (targetPanY - startPanY) * t
            }
        }
    }

    // Kullanıcı İsteği: "Oynat butonu sonsuz çalışsın her açıdan bakacak şekilde olsun"
    // Sinematik Sonsuz 3D Tur: 360° orbital süzülme, ön sonar/servo yakın çekimi, üst kuşbakışı,
    // alttan şasi & motorlar ve yan profil arasında kesintisiz döngü (while isActive).
    fun playTour() {
        stopTour()
        isTourPlaying = true
        if (!is3dActive) {
            onToggle3dActive()
        }

        animationJob = coroutineScope.launch {
            try {
                while (isActive) {
                    // 1. AŞAMA: 360° Kesintisiz Çevre Turu (Panoramik Orbital Gezinti)
                    val orbitStartYaw = camera.yaw
                    val orbitAnim = Animatable(0f)
                    orbitAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 5500, easing = LinearEasing)
                    ) {
                        camera.yaw = (orbitStartYaw + 360f * value) % 360f
                        camera.pitch = 32f + 12f * kotlin.math.sin(value * 2f * kotlin.math.PI.toFloat())
                        camera.zoom = 2.0f + 0.2f * kotlin.math.cos(value * 2f * kotlin.math.PI.toFloat())
                        camera.panX = 0f
                        camera.panY = 0f
                    }

                    if (!isActive) break

                    // 2. AŞAMA: Ön Cephe & HC-SR04 Sensör + SG90 Servo Yakın Detay Açısı
                    val fStartYaw = camera.yaw
                    val fStartPitch = camera.pitch
                    val fStartZoom = camera.zoom
                    val fDeltaYaw = shortestDeltaYaw(fStartYaw, 0f)
                    val frontAnim = Animatable(0f)
                    frontAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 2400, easing = FastOutSlowInEasing)
                    ) {
                        val t = value
                        camera.yaw = (fStartYaw + fDeltaYaw * t) % 360f
                        camera.pitch = fStartPitch + (16f - fStartPitch) * t
                        camera.zoom = fStartZoom + (2.8f - fStartZoom) * t
                        camera.panX = 0f
                        camera.panY = 14f * t
                    }

                    if (!isActive) break

                    // 3. AŞAMA: Üst Kuşbakışı Mühendislik Planı (Top-Down 80°)
                    val tStartYaw = camera.yaw
                    val tStartPitch = camera.pitch
                    val tStartZoom = camera.zoom
                    val tStartPanY = camera.panY
                    val tDeltaYaw = shortestDeltaYaw(tStartYaw, 45f)
                    val topAnim = Animatable(0f)
                    topAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 2600, easing = FastOutSlowInEasing)
                    ) {
                        val t = value
                        camera.yaw = (tStartYaw + tDeltaYaw * t) % 360f
                        camera.pitch = tStartPitch + (80f - tStartPitch) * t
                        camera.zoom = tStartZoom + (2.0f - tStartZoom) * t
                        camera.panY = tStartPanY * (1f - t)
                    }

                    if (!isActive) break

                    // 4. AŞAMA: Alttan Şasi, Sarhoş Teker ve DC Motorlar İncelemesi (Underbody -70°)
                    val bStartYaw = camera.yaw
                    val bStartPitch = camera.pitch
                    val bStartZoom = camera.zoom
                    val bDeltaYaw = shortestDeltaYaw(bStartYaw, 180f)
                    val bottomAnim = Animatable(0f)
                    bottomAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 2800, easing = FastOutSlowInEasing)
                    ) {
                        val t = value
                        camera.yaw = (bStartYaw + bDeltaYaw * t) % 360f
                        camera.pitch = bStartPitch + (-70f - bStartPitch) * t
                        camera.zoom = bStartZoom + (2.1f - bStartZoom) * t
                        camera.panY = 0f
                    }

                    if (!isActive) break

                    // 5. AŞAMA: Yan Profil, Sarı Tahrik Tekerlekleri ve 2S Batarya Paketi (Side 90°)
                    val sStartYaw = camera.yaw
                    val sStartPitch = camera.pitch
                    val sStartZoom = camera.zoom
                    val sDeltaYaw = shortestDeltaYaw(sStartYaw, 90f)
                    val sideAnim = Animatable(0f)
                    sideAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 2500, easing = FastOutSlowInEasing)
                    ) {
                        val t = value
                        camera.yaw = (sStartYaw + sDeltaYaw * t) % 360f
                        camera.pitch = sStartPitch + (24f - sStartPitch) * t
                        camera.zoom = sStartZoom + (2.3f - sStartZoom) * t
                        camera.panY = 0f
                    }

                    if (!isActive) break

                    // 6. AŞAMA: Yumuşak İzometrik Standart Duruşa Dönüş
                    val hStartYaw = camera.yaw
                    val hStartPitch = camera.pitch
                    val hStartZoom = camera.zoom
                    val hDeltaYaw = shortestDeltaYaw(hStartYaw, -35f)
                    val homeAnim = Animatable(0f)
                    homeAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing)
                    ) {
                        val t = value
                        camera.yaw = (hStartYaw + hDeltaYaw * t) % 360f
                        camera.pitch = hStartPitch + (32f - hStartPitch) * t
                        camera.zoom = hStartZoom + (2.0f - hStartZoom) * t
                        camera.panX = 0f
                        camera.panY = 0f
                    }
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

            // Kamera & Hızlı Bakış Çubuğu (Yenilenmiş Yüksek Ergonomi & UX)
            FloatingCameraBar(
                is3dActive = is3dActive,
                onToggle3dActive = onToggle3dActive,
                onReset = { animateCameraTo(-35f, 32f, 2.0f, 0f, 0f) },
                onTopView = { animateCameraTo(45f, 80f, 2.0f, 0f, 0f) },
                onBottomView = { animateCameraTo(180f, -70f, 2.1f, 0f, 0f) },
                onFrontView = { animateCameraTo(0f, 16f, 2.8f, 0f, 14f) },
                onSideView = { animateCameraTo(90f, 24f, 2.3f, 0f, 0f) },
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
                        "🎬 Sinematik Sonsuz Tur Aktif • Robot her açıdan sergileniyor (Durdurmak için ekrana dokunun)"
                    } else if (is3dActive) {
                        "🎮 3D Aktif: 👆 Tek Parmak: 360° Döndür • ✌️ İki Parmak: Gezin & Zoom • Çift Dokun: Sıfırla"
                    } else {
                        "👆 Sayfa Kaydırma Modu: Ekranlar arası geçiş serbest (3D kontrol için '3D Aktif'e dokunun)"
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
    Robot3DHeaderCard(
        telemetry = telemetry,
        colors = colors,
        onNavigateToTelemetry = onNavigateToTelemetry
    )
}

@Composable
private fun FloatingCameraBar(
    is3dActive: Boolean,
    onToggle3dActive: () -> Unit,
    onReset: () -> Unit,
    onTopView: () -> Unit,
    onBottomView: () -> Unit,
    onFrontView: () -> Unit,
    onSideView: () -> Unit,
    isTourPlaying: Boolean,
    onPlayTour: () -> Unit,
    onStopTour: () -> Unit,
    themeMode: ThemeMode,
    languageMode: LanguageMode,
    colors: AppColors,
    onThemeSelected: (ThemeMode) -> Unit,
    onLanguageSelected: (LanguageMode) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bgSecondary.copy(alpha = 0.94f))
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 7.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // ==========================================
            // 1. BİRİNCİL EYLEM ÇUBUĞU (3D Mod & Sonsuz Tur & Ayarlar)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3D ETKİLEŞİM / SAYFA KAYDIRMA BUTONU
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (is3dActive) Color(0xFF10B981).copy(alpha = 0.22f)
                            else colors.bgPrimary
                        )
                        .border(
                            1.dp,
                            if (is3dActive) Color(0xFF10B981) else colors.border,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = onToggle3dActive)
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (is3dActive) Color(0xFF10B981) else colors.textMuted)
                        )
                        Text(
                            text = if (is3dActive) "🔓 ${Strings.mode3dActive}" else "🔒 ${Strings.mode3dPassive}",
                            color = if (is3dActive) Color(0xFF10B981) else colors.textPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // SONSUZ SİNEMATİK TUR (OYNAT / DURDUR)
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isTourPlaying) Color(0xFFF59E0B).copy(alpha = 0.25f)
                            else Color(0xFF3B82F6).copy(alpha = 0.16f)
                        )
                        .border(
                            1.dp,
                            if (isTourPlaying) Color(0xFFF59E0B) else Color(0xFF3B82F6).copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            if (isTourPlaying) onStopTour() else onPlayTour()
                        }
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = if (isTourPlaying) "⏸️" else "🎬",
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (isTourPlaying) Strings.tourStop else Strings.tourPlayInfinite,
                            color = if (isTourPlaying) Color(0xFFF59E0B) else Color(0xFF60A5FA),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // TEMA VE DİL MİNİ BUTONLARI
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.bgPrimary)
                            .border(0.8.dp, colors.border, RoundedCornerShape(8.dp))
                            .clickable {
                                val next = when (themeMode) {
                                    ThemeMode.SYSTEM -> ThemeMode.DARK
                                    ThemeMode.DARK -> ThemeMode.LIGHT
                                    ThemeMode.LIGHT -> ThemeMode.SYSTEM
                                }
                                onThemeSelected(next)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (themeMode == ThemeMode.DARK) "🌙" else if (themeMode == ThemeMode.LIGHT) "☀️" else "💻",
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.bgPrimary)
                            .border(0.8.dp, colors.border, RoundedCornerShape(8.dp))
                            .clickable {
                                val next = when (languageMode) {
                                    LanguageMode.SYSTEM -> LanguageMode.TR
                                    LanguageMode.TR -> LanguageMode.EN
                                    LanguageMode.EN -> LanguageMode.SYSTEM
                                }
                                onLanguageSelected(next)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (languageMode == LanguageMode.TR) "🇹🇷" else if (languageMode == LanguageMode.EN) "🇬🇧" else "🌐",
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // ==========================================
            // 2. KAMERA PERSPEKTİFLERİ (HIZLI AÇI DOCK)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CameraPresetChip("🎯", Strings.viewStandard, colors, onClick = onReset)
                CameraPresetChip("🔝", Strings.viewTop, colors, onClick = onTopView)
                CameraPresetChip("🔽", Strings.viewBottom, colors, onClick = onBottomView)
                CameraPresetChip("🚘", Strings.viewFront, colors, onClick = onFrontView)
                CameraPresetChip("🏎️", Strings.viewSide, colors, onClick = onSideView)
            }
        }
    }
}

@Composable
private fun CameraPresetChip(
    icon: String,
    label: String,
    colors: AppColors,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(30.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(colors.bgPrimary)
            .border(0.8.dp, colors.border, RoundedCornerShape(7.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 11.sp)
            Text(
                text = label,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
        }
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

// ==========================================
// COMPOSE PREVIEW (BNV ÜSTÜ GÜVENLİ ALAN)
// ==========================================
@Preview
@Composable
private fun Robot3DScreenPreview() {
    Robot3DScreen(
        repository = remember { MockTelemetryRepository() },
        is3dActive = true,
        themeMode = ThemeMode.DARK,
        languageMode = LanguageMode.TR,
        onThemeSelected = {},
        onLanguageSelected = {}
    )
}

