package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.SetSystemBarsTheme
import com.vahitkeskin.engeldenkacanrobotmobile.data.MockTelemetryRepository
import com.vahitkeskin.engeldenkacanrobotmobile.domain.repository.ITelemetryRepository
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.LanguageMode
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors
import com.vahitkeskin.engeldenkacanrobotmobile.theme.ThemeMode

/**
 * ⚙️ SettingsScreen
 * Kullanıcının uygulama temasını (Sistem / Koyu / Açık),
 * dil tercihlerini (Sistem / Türkçe / English), donanım haberleşme
 * mimarisini ve sistem bilgilerini yapılandırdığı merkezi ayarlar ekranı.
 */
@Composable
fun SettingsScreen(
    repository: ITelemetryRepository,
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
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 80.dp) // BNV için güvenli taban mesafesi
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ==========================================
            // 1. ÜST BAŞLIK KARTI (Header)
            // ==========================================
            DashboardHeaderCard(
                telemetry = telemetry,
                colors = colors
            )

            // AYARLAR BAŞLIK ROZETİ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "⚙️ " + Strings.settingsTitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = Strings.settingsSubtitle,
                        fontSize = 11.sp,
                        color = colors.textMuted
                    )
                }
            }

            // ==========================================
            // 2. GÖRÜNÜM & TEMA YAPILANDIRMASI
            // ==========================================
            SettingsSectionCard(
                icon = "🎨",
                title = Strings.sectionAppearance,
                subtitle = Strings.appearanceDesc,
                colors = colors
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ThemeOptionRow(
                        icon = "💻",
                        title = Strings.optSystem,
                        desc = Strings.optSystemDesc,
                        isSelected = themeMode == ThemeMode.SYSTEM,
                        colors = colors,
                        onClick = { onThemeSelected(ThemeMode.SYSTEM) }
                    )
                    ThemeOptionRow(
                        icon = "🌙",
                        title = Strings.optDark,
                        desc = Strings.optDarkDesc,
                        isSelected = themeMode == ThemeMode.DARK,
                        colors = colors,
                        onClick = { onThemeSelected(ThemeMode.DARK) }
                    )
                    ThemeOptionRow(
                        icon = "☀️",
                        title = Strings.optLight,
                        desc = Strings.optLightDesc,
                        isSelected = themeMode == ThemeMode.LIGHT,
                        colors = colors,
                        onClick = { onThemeSelected(ThemeMode.LIGHT) }
                    )
                }
            }

            // ==========================================
            // 3. DİL SEÇİMİ YAPILANDIRMASI
            // ==========================================
            SettingsSectionCard(
                icon = "🌐",
                title = Strings.sectionLanguage,
                subtitle = Strings.languageDesc,
                colors = colors
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LanguageOptionRow(
                        flag = "🌐",
                        title = Strings.optLangAuto,
                        desc = Strings.optLangAutoDesc,
                        isSelected = languageMode == LanguageMode.SYSTEM,
                        colors = colors,
                        onClick = { onLanguageSelected(LanguageMode.SYSTEM) }
                    )
                    LanguageOptionRow(
                        flag = "🇹🇷",
                        title = Strings.optLangTr,
                        desc = Strings.optTrDesc,
                        isSelected = languageMode == LanguageMode.TR,
                        colors = colors,
                        onClick = { onLanguageSelected(LanguageMode.TR) }
                    )
                    LanguageOptionRow(
                        flag = "🇬🇧",
                        title = Strings.optLangEn,
                        desc = Strings.optEnDesc,
                        isSelected = languageMode == LanguageMode.EN,
                        colors = colors,
                        onClick = { onLanguageSelected(LanguageMode.EN) }
                    )
                }
            }

            // ==========================================
            // 4. DONANIM & VERİYOLU BİLGİLERİ
            // ==========================================
            SettingsSectionCard(
                icon = "🔌",
                title = Strings.sectionHardwareInfo,
                subtitle = "Arduino Uno R3 • I2C: 0x08 • L298N • HC-SR04 • SG90",
                colors = colors
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    HardwareInfoRow("Mikrodenetleyici", "Arduino Uno R3 (ATmega328P @ 16 MHz)", colors)
                    HardwareInfoRow("I2C Veriyolu", "Slave Adresi 0x08 • Standart Hız 100 kHz", colors)
                    HardwareInfoRow("Motor Sürücü", "L298N Çift H-Köprüsü (2x TT DC Motor)", colors)
                    HardwareInfoRow("Ultrasonik Sonar", "HC-SR04 (2-400 cm Hassasiyet)", colors)
                    HardwareInfoRow("Servo Motor", "TowerPro SG90 9g Mikro Servo (0° - 180°)", colors)
                    HardwareInfoRow("Güç Kaynağı", "2x 18650 Li-Ion (7.4V Nominal / 8.4V Peak)", colors)
                }
            }

            // ==========================================
            // 5. UYGULAMA HAKKINDA & GELİŞTİRİCİ
            // ==========================================
            SettingsSectionCard(
                icon = "🤖",
                title = Strings.sectionAppInfo,
                subtitle = "EK Robot • Kotlin Multiplatform",
                colors = colors
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    HardwareInfoRow("Uygulama Adı", "EK Robot", colors)
                    HardwareInfoRow("Sürüm", Strings.splashVersion, colors)
                    HardwareInfoRow("Mimari", "Kotlin Multiplatform & Jetpack Compose", colors)
                    HardwareInfoRow("Platformlar", "Android • iOS • Desktop (Mac/Win/Linux)", colors)
                    HardwareInfoRow("Geliştirici", Strings.splashDevelopedBy, colors)
                }
            }

            // BNV için ek nefes alma boşluğu
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

// -------------------------------------------------------------
// YARDIMCI BİLEŞENLER
// -------------------------------------------------------------

@Composable
private fun SettingsSectionCard(
    icon: String,
    title: String,
    subtitle: String,
    colors: AppColors,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bgSecondary)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = icon, fontSize = 14.sp)
            Column {
                Text(
                    text = title,
                    color = colors.textPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = subtitle,
                    color = colors.textMuted,
                    fontSize = 10.sp
                )
            }
        }

        content()
    }
}

@Composable
private fun ThemeOptionRow(
    icon: String,
    title: String,
    desc: String,
    isSelected: Boolean,
    colors: AppColors,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF2563EB).copy(alpha = 0.12f) else colors.bgPrimary)
            .border(
                width = if (isSelected) 1.5.dp else 0.8.dp,
                color = if (isSelected) Color(0xFF2563EB) else colors.border,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = icon, fontSize = 16.sp)
                Column {
                    Text(
                        text = title,
                        color = if (isSelected) colors.textPrimary else colors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                    )
                    Text(
                        text = desc,
                        color = colors.textMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFF2563EB) else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (isSelected) Color(0xFF2563EB) else colors.border,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageOptionRow(
    flag: String,
    title: String,
    desc: String,
    isSelected: Boolean,
    colors: AppColors,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF2563EB).copy(alpha = 0.12f) else colors.bgPrimary)
            .border(
                width = if (isSelected) 1.5.dp else 0.8.dp,
                color = if (isSelected) Color(0xFF2563EB) else colors.border,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = flag, fontSize = 16.sp)
                Column {
                    Text(
                        text = title,
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                    )
                    Text(
                        text = desc,
                        color = colors.textMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFF2563EB) else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (isSelected) Color(0xFF2563EB) else colors.border,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

@Composable
private fun HardwareInfoRow(
    label: String,
    value: String,
    colors: AppColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(colors.bgPrimary.copy(alpha = 0.6f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 10.5.sp,
            color = colors.textMuted,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 10.5.sp,
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Preview
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        repository = MockTelemetryRepository(),
        themeMode = ThemeMode.DARK,
        languageMode = LanguageMode.TR,
        onThemeSelected = {},
        onLanguageSelected = {}
    )
}
