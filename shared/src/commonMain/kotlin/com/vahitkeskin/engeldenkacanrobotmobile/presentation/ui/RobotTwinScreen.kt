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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors
import com.vahitkeskin.engeldenkacanrobotmobile.theme.ThemeMode

/**
 * 🤖 RobotTwinScreen
 * 2D Robot Donanım Şematiği Ekranı (Tab 2)
 * Robotun fiziksel gövdesini, dönen SG90 servoyu, HC-SR04 ultrasonik ışınını
 * ve sağ/sol tekerlek durumlarını %100 gerçek donanım değerleriyle gösterir.
 */
@Composable
fun RobotTwinScreen(
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

    // Durum çubuğu renklerini anlık senkronize et
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
                .padding(bottom = 80.dp) // Bottom Navigation Bar için güvenli alan
                .verticalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. ÜST BİLGİ KARTI
            TwinHeaderCard(telemetry, colors)

            // 2. KONTROL VE SEÇİCİ BARI
            TwinControlBar(
                isConnected = isConnected,
                themeMode = themeMode,
                languageMode = languageMode,
                colors = colors,
                onThemeSelected = onThemeSelected,
                onLanguageSelected = onLanguageSelected,
                onConnectToggle = {
                    if (isConnected) repository.disconnect()
                    else repository.connect("/dev/cu.usbserial-1240")
                }
            )

            // 3. CANLI 2D ROBOT DİJİTAL İKİZ TUVALİ
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(390.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.bgSecondary)
                    .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                RobotDigitalTwinView(
                    telemetry = telemetry,
                    colors = colors,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 4. DONANIM AYRINTILI METRİK KARTLARI
            HardwareComponentCards(telemetry, colors)

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TwinHeaderCard(
    telemetry: RobotTelemetry,
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
                    .background(if (telemetry.hasSignal) Color(0xFF10B981) else Color(0xFFEF4444))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = Strings.robotTwinTitle,
                    color = colors.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = Strings.i2cBus,
                    color = colors.textMuted,
                    fontSize = 10.sp
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
private fun TwinControlBar(
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
        // TEMA SEÇİCİ
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
                TwinSegmentTab("💻", Strings.optSystem, themeMode == ThemeMode.SYSTEM, colors, Modifier.weight(1f)) { onThemeSelected(ThemeMode.SYSTEM) }
                TwinSegmentTab("🌙", Strings.optDark, themeMode == ThemeMode.DARK, colors, Modifier.weight(1f)) { onThemeSelected(ThemeMode.DARK) }
                TwinSegmentTab("☀️", Strings.optLight, themeMode == ThemeMode.LIGHT, colors, Modifier.weight(1f)) { onThemeSelected(ThemeMode.LIGHT) }
            }
        }

        // DİL SEÇİCİ
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
                TwinSegmentTab("🌐", Strings.optLangAuto, languageMode == LanguageMode.SYSTEM, colors, Modifier.weight(1f)) { onLanguageSelected(LanguageMode.SYSTEM) }
                TwinSegmentTab("🇹🇷", Strings.optLangTr, languageMode == LanguageMode.TR, colors, Modifier.weight(1f)) { onLanguageSelected(LanguageMode.TR) }
                TwinSegmentTab("🇬🇧", Strings.optLangEn, languageMode == LanguageMode.EN, colors, Modifier.weight(1f)) { onLanguageSelected(LanguageMode.EN) }
            }
        }

        // BAĞLANTI BUTONU
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
private fun TwinSegmentTab(
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
            Text(text = icon, fontSize = 10.sp, modifier = Modifier.padding(end = 3.dp))
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

@Composable
private fun HardwareComponentCards(
    telemetry: RobotTelemetry,
    colors: AppColors
) {
    // 1. SOL VE SAĞ TEKER DURUMLARI (Yan Yana)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val leftState = telemetry.leftWheelState
        val rightState = telemetry.rightWheelState

        ComponentDetailCard(
            title = "🏎️ " + Strings.leftWheel,
            value = if (telemetry.hasSignal) "${leftState.speedPwm} PWM" else "--",
            subtitle = if (telemetry.hasSignal) {
                when (leftState.direction) {
                    WheelDirection.FORWARD -> Strings.wheelFwd
                    WheelDirection.BACKWARD -> Strings.wheelRev
                    WheelDirection.STOPPED -> Strings.wheelStop
                }
            } else Strings.noSignal,
            valueColor = if (leftState.direction == WheelDirection.FORWARD) Color(0xFF10B981) else if (leftState.direction == WheelDirection.BACKWARD) Color(0xFFEF4444) else colors.textMuted,
            colors = colors,
            modifier = Modifier.weight(1f)
        )

        ComponentDetailCard(
            title = "🏎️ " + Strings.rightWheel,
            value = if (telemetry.hasSignal) "${rightState.speedPwm} PWM" else "--",
            subtitle = if (telemetry.hasSignal) {
                when (rightState.direction) {
                    WheelDirection.FORWARD -> Strings.wheelFwd
                    WheelDirection.BACKWARD -> Strings.wheelRev
                    WheelDirection.STOPPED -> Strings.wheelStop
                }
            } else Strings.noSignal,
            valueColor = if (rightState.direction == WheelDirection.FORWARD) Color(0xFF10B981) else if (rightState.direction == WheelDirection.BACKWARD) Color(0xFFEF4444) else colors.textMuted,
            colors = colors,
            modifier = Modifier.weight(1f)
        )
    }

    // 2. SG90 SERVO VE HC-SR04 SENSÖR DURUMLARI (Yan Yana)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ComponentDetailCard(
            title = "🔄 " + Strings.servoMotor,
            value = if (telemetry.hasSignal) "${telemetry.servoAngle}°" else "--°",
            subtitle = if (telemetry.hasSignal) {
                when {
                    telemetry.servoAngle > 105 -> Strings.leftScan
                    telemetry.servoAngle < 75 -> Strings.rightScan
                    else -> Strings.center
                }
            } else Strings.noSignal,
            valueColor = Color(0xFF3B82F6),
            colors = colors,
            modifier = Modifier.weight(1f)
        )

        val distColor = when {
            !telemetry.hasSignal -> colors.textMuted
            telemetry.isDanger -> Color(0xFFEF4444)
            telemetry.isWarning -> Color(0xFFF59E0B)
            else -> Color(0xFF10B981)
        }

        ComponentDetailCard(
            title = "🎯 " + Strings.ultrasonicSensor,
            value = if (telemetry.hasSignal && telemetry.frontDistance > 0) "${telemetry.frontDistance} cm" else "-- cm",
            subtitle = if (telemetry.hasSignal) {
                when {
                    telemetry.isDanger -> Strings.dangerCorridor
                    telemetry.isWarning -> Strings.warningCorridor
                    else -> Strings.safeCorridor
                }
            } else Strings.noSignal,
            valueColor = distColor,
            colors = colors,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ComponentDetailCard(
    title: String,
    value: String,
    subtitle: String,
    valueColor: Color,
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.bgSecondary)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Text(
            text = title,
            color = colors.textMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            color = colors.textMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
