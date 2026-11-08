package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.vahitkeskin.engeldenkacanrobotmobile.SetSystemBarsTheme
import com.vahitkeskin.engeldenkacanrobotmobile.data.MockTelemetryRepository
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotState
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.domain.repository.ITelemetryRepository
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.LanguageMode
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors
import com.vahitkeskin.engeldenkacanrobotmobile.theme.ThemeMode
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DashboardScreen(
    repository: ITelemetryRepository = remember { MockTelemetryRepository() },
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    languageMode: LanguageMode = LanguageMode.SYSTEM
) {
    // Sistem teması ve seçilen modun çözümlenmesi
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    val colors = if (isDark) AppColors.Dark else AppColors.Light

    // Telefonun sistem status barı ve navigation bar ikonlarını anlık değiştir
    SetSystemBarsTheme(isDark = isDark)
    Strings.currentLanguage = languageMode

    val telemetry by repository.telemetry.collectAsState()
    val logs by repository.logs.collectAsState()
    val isConnected by repository.isConnected.collectAsState()

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    var containerScreenY by remember { mutableStateOf(0f) }
    var cardScreenY by remember { mutableStateOf(0f) }

    fun scrollToI2CCardTop() {
        coroutineScope.launch {
            val desiredGapPx = with(density) { 10.dp.toPx() }
            // 1. Frame senkronizasyonu için kısa bir gecikme ver (AnimatedVisibility genişleme başlangıcı)
            delay(50)
            val delta1 = cardScreenY - (containerScreenY + desiredGapPx)
            val target1 = (scrollState.value + delta1).toInt().coerceAtLeast(0)
            scrollState.animateScrollTo(
                value = target1,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
            )
            // 2. Genişleme animasyonu tamamlandığında pixel-perfect hassas hizalama
            delay(120)
            val delta2 = cardScreenY - (containerScreenY + desiredGapPx)
            if (kotlin.math.abs(delta2) > 2f) {
                val target2 = (scrollState.value + delta2).toInt().coerceAtLeast(0)
                scrollState.animateScrollTo(
                    value = target2,
                    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    // 1. TÜM EKRAN (Edge-to-Edge Arka Plan)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPrimary)
    ) {
        // 2. İÇERİK ALANI (Dikey Scroll Edilebilir, Status Bar Altında ve BNV Üstünde)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 80.dp)  // BNV için güvenli alan
                .imePadding()
                .onGloballyPositioned { coordinates ->
                    containerScreenY = coordinates.positionInWindow().y
                }
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // BAŞLIK & DURUM ROZETİ KARTI (BATARYA ROZETİ ENTEGRELİ)
            HeaderCard(
                telemetry = telemetry,
                colors = colors
            )

            // DONANIMA BAĞLAN / BAĞLANTIYI KES BUTONU
            HardwareConnectionButton(
                isConnected = isConnected,
                onConnectToggle = {
                    if (isConnected) repository.disconnect() else repository.connect("/dev/cu.usbserial-1240")
                }
            )

            // BATARYA & GÜÇ YÖNETİM KARTI (2S Li-Ion: 7.4V / 8.4V)
            BatteryStatusCard(
                telemetry = telemetry,
                colors = colors,
                onToggleCharging = { repository.toggleChargingSimulation() }
            )

            // GÖRSEL TELEMETRİ PANELLERİ (Radar ve Servo Göstergesi - Yan Yana)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RadarView(
                    telemetry = telemetry,
                    colors = colors,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                ServoGaugeView(
                    telemetry = telemetry,
                    colors = colors,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }

            // MESAFE METRİK KARTI
            MetricCards(
                telemetry = telemetry,
                colors = colors
            )

            // I2C VERİYOLU & AĞ BİLGİLERİ (Açılır / Kapanır UI & Otomatik En Üste Animasyonlu Kaydırma)
            I2CNetworkCard(
                telemetry = telemetry,
                colors = colors,
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    cardScreenY = coordinates.positionInWindow().y
                },
                onScrollToTop = {
                    scrollToI2CCardTop()
                }
            )

            // TELEMETRİ GÜNLÜĞÜ (Kompakt ve Scroll Edilebilir Terminal)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.logTitle,
                    color = colors.textMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (telemetry.hasSignal) "● CANLI AKIŞ" else "○ BEKLEMEDE",
                    color = if (telemetry.hasSignal) Color(0xFF10B981) else colors.textMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.logBg)
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true
                ) {
                    if (logs.isEmpty()) {
                        item {
                            Text(
                                text = if (telemetry.hasSignal) "> Telemetri akışı bekleniyor..." else "> ${Strings.noSignal}",
                                color = colors.textMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else {
                        items(logs.reversed()) { logLine ->
                            Text(
                                text = logLine,
                                color = colors.logText,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // Alt kenar boşluğu
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HeaderCard(
    telemetry: RobotTelemetry,
    colors: AppColors
) {
    DashboardHeaderCard(
        telemetry = telemetry,
        colors = colors
    )
}

@Composable
private fun HardwareConnectionButton(
    isConnected: Boolean,
    onConnectToggle: () -> Unit
) {
    Button(
        onClick = onConnectToggle,
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isConnected) Color(0xFFDC2626) else Color(0xFF2563EB)
        )
    ) {
        Text(
            text = if (isConnected) "⚡ " + Strings.disconnect else "🔌 " + Strings.connect,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// ==========================================
// COMPOSE PREVIEW (BNV ÜSTÜ GÜVENLİ ALAN)
// ==========================================
@Preview
@Composable
private fun DashboardScreenPreview() {
    DashboardScreen(
        repository = remember { MockTelemetryRepository() },
        themeMode = ThemeMode.DARK,
        languageMode = LanguageMode.TR
    )
}

