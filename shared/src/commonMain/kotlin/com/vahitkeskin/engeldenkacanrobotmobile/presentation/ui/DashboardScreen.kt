package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPrimary)
            .padding(14.dp)
    ) {
        // ÜST ARAÇ ÇUBUĞU
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(colors.bgSecondary)
                .border(1.dp, colors.border, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            val badgeText = if (!telemetry.hasSignal) {
                Strings.noSignal.uppercase()
            } else {
                when (telemetry.state) {
                    RobotState.CRUISING -> "SEYİR (CRUISE)"
                    RobotState.AVOIDING -> "KAÇIŞ (AVOID)"
                    RobotState.STOPPED -> "DURDU (STOP)"
                    else -> Strings.noSignal.uppercase()
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(if (telemetry.hasSignal) telemetry.state.badgeColor else Color(0xFF6B7280))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // TEMA VE DİL SEÇİCİ BARI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tema Seçici
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = Strings.themeLabel, color = colors.textMuted, fontSize = 10.sp, modifier = Modifier.padding(end = 4.dp))
                ThemeOptionButton("Sys", themeMode == ThemeMode.SYSTEM, colors) { themeMode = ThemeMode.SYSTEM }
                ThemeOptionButton("Dark", themeMode == ThemeMode.DARK, colors) { themeMode = ThemeMode.DARK }
                ThemeOptionButton("Light", themeMode == ThemeMode.LIGHT, colors) { themeMode = ThemeMode.LIGHT }
            }

            // Dil Seçici
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = Strings.langLabel, color = colors.textMuted, fontSize = 10.sp, modifier = Modifier.padding(end = 4.dp))
                ThemeOptionButton("Sys", languageMode == LanguageMode.SYSTEM, colors) { languageMode = LanguageMode.SYSTEM }
                ThemeOptionButton("TR", languageMode == LanguageMode.TR, colors) { languageMode = LanguageMode.TR }
                ThemeOptionButton("EN", languageMode == LanguageMode.EN, colors) { languageMode = LanguageMode.EN }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // BAĞLANTI BUTONU
        Button(
            onClick = {
                if (isConnected) repository.disconnect() else repository.connect("/dev/cu.usbserial-1240")
            },
            modifier = Modifier.fillMaxWidth().height(36.dp),
            shape = RoundedCornerShape(8.dp),
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

        Spacer(modifier = Modifier.height(10.dp))

        // GÖRSEL PANELLER (Radar ve Servo Göstergesi)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RadarView(
                telemetry = telemetry,
                colors = colors,
                modifier = Modifier.weight(1f)
            )
            ServoGaugeView(
                telemetry = telemetry,
                colors = colors,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // MESAFE METRİK KARTI
        MetricCards(
            telemetry = telemetry,
            colors = colors
        )

        Spacer(modifier = Modifier.height(8.dp))

        // CANLI TELEMETRİ LOGLARI
        Text(
            text = Strings.logTitle,
            color = colors.textMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.logBg)
                .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                .padding(8.dp),
            reverseLayout = true
        ) {
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

@Composable
private fun ThemeOptionButton(
    label: String,
    isSelected: Boolean,
    colors: AppColors,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
        modifier = Modifier.height(26.dp).padding(horizontal = 2.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF2563EB) else colors.bgSecondary,
            contentColor = if (isSelected) Color.White else colors.textMuted
        )
    ) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}
