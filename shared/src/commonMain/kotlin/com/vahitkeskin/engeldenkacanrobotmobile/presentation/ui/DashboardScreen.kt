package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.data.MockTelemetryRepository
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotState
import com.vahitkeskin.engeldenkacanrobotmobile.domain.repository.ITelemetryRepository
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.LanguageMode
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors
import com.vahitkeskin.engeldenkacanrobotmobile.theme.ThemeMode

@Composable
fun DashboardScreen(
    repository: ITelemetryRepository = remember { MockTelemetryRepository() }
) {
    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
    var languageMode by remember { mutableStateOf(LanguageMode.SYSTEM) }

    // Sistem teması ve seçilen modun çözümlenmesi
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    val colors = if (isDark) AppColors.Dark else AppColors.Light

    // Dil modunun anlık atanması
    Strings.currentLanguage = languageMode

    val telemetry by repository.telemetry.collectAsState()
    val logs by repository.logs.collectAsState()
    val isConnected by repository.isConnected.collectAsState()

    // 1. TÜM EKRAN (Edge-to-Edge Arka Plan)
    // Statusbar ve navigationbar saydam olsa bile arka plan rengi tüm ekrana taşar, siyah boşluk kalmaz.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPrimary)
    ) {
        // 2. İÇERİK ALANI (Tam Status Bar Altında ve Tam Navigation Bar Üstünde)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()      // Ekranın en üstü kesinlikle status barın altındadır
                .navigationBarsPadding()  // Ekranın en altı kesinlikle navigation barın üstündedir
                .imePadding()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // BAŞLIK & DURUM ROZETİ KARTI
            HeaderCard(
                telemetryHasSignal = telemetry.hasSignal,
                robotState = telemetry.state,
                colors = colors
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TEMA, DİL VE BAĞLANTI KONTROL BARI (Derli toplu tek panel)
            ControlBar(
                isConnected = isConnected,
                themeMode = themeMode,
                languageMode = languageMode,
                colors = colors,
                onThemeSelected = { themeMode = it },
                onLanguageSelected = { languageMode = it },
                onConnectToggle = {
                    if (isConnected) repository.disconnect() else repository.connect("/dev/cu.usbserial-1240")
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

            // MESAFE METRİK KARTI
            MetricCards(
                telemetry = telemetry,
                colors = colors
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TELEMETRİ GÜNLÜĞÜ (Kompakt ve Şık Terminal)
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.logBg)
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
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
    }
}

@Composable
private fun HeaderCard(
    telemetryHasSignal: Boolean,
    robotState: RobotState,
    colors: AppColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bgSecondary)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (telemetryHasSignal) Color(0xFF10B981) else Color(0xFFEF4444))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = Strings.appTitle,
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = Strings.i2cBus,
                    color = colors.textMuted,
                    fontSize = 10.sp
                )
            }
        }

        val badgeText = if (!telemetryHasSignal) {
            Strings.noSignal.uppercase()
        } else {
            when (robotState) {
                RobotState.CRUISING -> "SEYİR (CRUISE)"
                RobotState.AVOIDING -> "KAÇIŞ (AVOID)"
                RobotState.STOPPED -> "DURDURULDU"
                else -> Strings.noSignal.uppercase()
            }
        }
        val badgeColor = if (telemetryHasSignal) robotState.badgeColor else Color(0xFF6B7280)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(badgeColor)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = badgeText,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ControlBar(
    isConnected: Boolean,
    themeMode: ThemeMode,
    languageMode: LanguageMode,
    colors: AppColors,
    onThemeSelected: (ThemeMode) -> Unit,
    onLanguageSelected: (LanguageMode) -> Unit,
    onConnectToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.bgSecondary)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Üst Satır: Tema ve Dil Seçicileri
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tema Segmenti
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = Strings.themeLabel, color = colors.textMuted, fontSize = 10.sp, modifier = Modifier.padding(end = 4.dp))
                SegmentedPill("Sys", themeMode == ThemeMode.SYSTEM, colors) { onThemeSelected(ThemeMode.SYSTEM) }
                SegmentedPill("Dark", themeMode == ThemeMode.DARK, colors) { onThemeSelected(ThemeMode.DARK) }
                SegmentedPill("Light", themeMode == ThemeMode.LIGHT, colors) { onThemeSelected(ThemeMode.LIGHT) }
            }

            // Dil Segmenti
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = Strings.langLabel, color = colors.textMuted, fontSize = 10.sp, modifier = Modifier.padding(end = 4.dp))
                SegmentedPill("Sys", languageMode == LanguageMode.SYSTEM, colors) { onLanguageSelected(LanguageMode.SYSTEM) }
                SegmentedPill("TR", languageMode == LanguageMode.TR, colors) { onLanguageSelected(LanguageMode.TR) }
                SegmentedPill("EN", languageMode == LanguageMode.EN, colors) { onLanguageSelected(LanguageMode.EN) }
            }
        }

        // Alt Satır: Bağlantı Butonu
        Button(
            onClick = onConnectToggle,
            modifier = Modifier.fillMaxWidth().height(32.dp),
            contentPadding = PaddingValues(vertical = 0.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isConnected) Color(0xFFDC2626) else Color(0xFF2563EB)
            )
        ) {
            Text(
                text = if (isConnected) Strings.disconnect else Strings.connect,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SegmentedPill(
    label: String,
    isSelected: Boolean,
    colors: AppColors,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
        modifier = Modifier.height(24.dp).padding(horizontal = 1.5.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF2563EB) else colors.bgPrimary,
            contentColor = if (isSelected) Color.White else colors.textMuted
        )
    ) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}
