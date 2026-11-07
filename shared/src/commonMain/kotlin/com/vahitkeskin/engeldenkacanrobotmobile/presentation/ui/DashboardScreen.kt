package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.SetSystemBarsTheme
import com.vahitkeskin.engeldenkacanrobotmobile.data.MockTelemetryRepository
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotState
import com.vahitkeskin.engeldenkacanrobotmobile.domain.repository.ITelemetryRepository
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.LanguageMode
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors
import com.vahitkeskin.engeldenkacanrobotmobile.theme.ThemeMode

@Composable
fun DashboardScreen(
    repository: ITelemetryRepository = remember { MockTelemetryRepository() },
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    languageMode: LanguageMode = LanguageMode.SYSTEM,
    onThemeSelected: (ThemeMode) -> Unit = {},
    onLanguageSelected: (LanguageMode) -> Unit = {}
) {
    // Sistem teması ve seçilen modun çözümlenmesi
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    val colors = if (isDark) AppColors.Dark else AppColors.Light

    // Telefonun sistem status barı (saat, wifi, şarj ikonu) ve navigation bar ikonlarını anlık değiştir
    SetSystemBarsTheme(isDark = isDark)

    // Dil modunun anlık atanması
    Strings.currentLanguage = languageMode

    val telemetry by repository.telemetry.collectAsState()
    val logs by repository.logs.collectAsState()
    val isConnected by repository.isConnected.collectAsState()

    val scrollState = rememberScrollState()

    // 1. TÜM EKRAN (Edge-to-Edge Arka Plan)
    // Saydam status bar ve navigation bar altını temanın rengiyle doldurur.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPrimary)
    ) {
        // 2. İÇERİK ALANI (Dikey Scroll Edilebilir, Status Bar Altında ve BNV Üstünde)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()      // Ekranın en üstü kesinlikle status barın altındadır
                .padding(bottom = 80.dp)  // Bottom Navigation View için güvenli alan
                .imePadding()
                .verticalScroll(scrollState) // EKRAN KESİNTİSİZ SCROLL EDİLEBİLİR
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // BAŞLIK & DURUM ROZETİ KARTI
            HeaderCard(
                telemetryHasSignal = telemetry.hasSignal,
                robotState = telemetry.state,
                colors = colors
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TEMA, DİL VE BAĞLANTI KONTROL BARI
            ControlBar(
                isConnected = isConnected,
                themeMode = themeMode,
                languageMode = languageMode,
                colors = colors,
                onThemeSelected = onThemeSelected,
                onLanguageSelected = onLanguageSelected,
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

            Spacer(modifier = Modifier.height(10.dp))

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
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bgSecondary)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. TEMA SEÇİCİ (Segmented Row)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = Strings.themeLabel,
                color = colors.textMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(42.dp)
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(colors.bgPrimary)
                    .border(0.8.dp, colors.border, RoundedCornerShape(7.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                SegmentTab(
                    icon = "💻",
                    label = Strings.optSystem,
                    isSelected = themeMode == ThemeMode.SYSTEM,
                    colors = colors,
                    modifier = Modifier.weight(1f),
                    onClick = { onThemeSelected(ThemeMode.SYSTEM) }
                )
                SegmentTab(
                    icon = "🌙",
                    label = Strings.optDark,
                    isSelected = themeMode == ThemeMode.DARK,
                    colors = colors,
                    modifier = Modifier.weight(1f),
                    onClick = { onThemeSelected(ThemeMode.DARK) }
                )
                SegmentTab(
                    icon = "☀️",
                    label = Strings.optLight,
                    isSelected = themeMode == ThemeMode.LIGHT,
                    colors = colors,
                    modifier = Modifier.weight(1f),
                    onClick = { onThemeSelected(ThemeMode.LIGHT) }
                )
            }
        }

        // 2. DİL SEÇİCİ (Segmented Row)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = Strings.langLabel,
                color = colors.textMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(42.dp)
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(colors.bgPrimary)
                    .border(0.8.dp, colors.border, RoundedCornerShape(7.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                SegmentTab(
                    icon = "🌐",
                    label = Strings.optLangAuto,
                    isSelected = languageMode == LanguageMode.SYSTEM,
                    colors = colors,
                    modifier = Modifier.weight(1f),
                    onClick = { onLanguageSelected(LanguageMode.SYSTEM) }
                )
                SegmentTab(
                    icon = "🇹🇷",
                    label = Strings.optLangTr,
                    isSelected = languageMode == LanguageMode.TR,
                    colors = colors,
                    modifier = Modifier.weight(1f),
                    onClick = { onLanguageSelected(LanguageMode.TR) }
                )
                SegmentTab(
                    icon = "🇬🇧",
                    label = Strings.optLangEn,
                    isSelected = languageMode == LanguageMode.EN,
                    colors = colors,
                    modifier = Modifier.weight(1f),
                    onClick = { onLanguageSelected(LanguageMode.EN) }
                )
            }
        }

        // 3. BAĞLANTI BUTONU
        Button(
            onClick = onConnectToggle,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isConnected) Color(0xFFDC2626) else Color(0xFF2563EB)
            )
        ) {
            Text(
                text = if (isConnected) "⚡ " + Strings.disconnect else "🔌 " + Strings.connect,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SegmentTab(
    icon: String,
    label: String,
    isSelected: Boolean,
    colors: AppColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(5.dp))
            .background(if (isSelected) Color(0xFF2563EB) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 10.sp,
                modifier = Modifier.padding(end = 3.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else colors.textMuted,
                maxLines = 1
            )
        }
    }
}
