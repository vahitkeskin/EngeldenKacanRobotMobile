package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.ui.tooling.preview.Preview

/**
 * 🎮 RobotControllerScreen - Sanal Kokpit & Robota Doğrudan Müdahale Kumandası
 * KMP (Android, iOS, Desktop) üzerinde çalışan, robotun tüm motorlarına,
 * servosuna, acil frenine ve bataryasına doğrudan hükmeden uzaktan kumanda paneli.
 */
@Composable
fun RobotControllerScreen(
    repository: ITelemetryRepository,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    languageMode: LanguageMode = LanguageMode.SYSTEM,
    onNavigateTo3D: () -> Unit = {}
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    val colors = if (isDark) AppColors.Dark else AppColors.Light
    SetSystemBarsTheme(isDark = isDark)
    Strings.currentLanguage = languageMode

    val telemetry by repository.telemetry.collectAsState()
    val isConnected by repository.isConnected.collectAsState()

    var activeDriveDirection by remember { mutableStateOf("STOP") }
    var currentSpeedPwm by remember { mutableStateOf(180f) }
    var manualServoAngle by remember { mutableStateOf(90f) }

    // Telemetrideki servo açısını manuel açıyla senkronize et (otonom moddayken)
    LaunchedEffect(telemetry.servoAngle) {
        if (!telemetry.isManualMode) {
            manualServoAngle = telemetry.servoAngle.toFloat()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 76.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. ÜST BAŞLIK VE BATARYA DURUM ÇUBUĞU
            CockpitHeader(
                telemetry = telemetry,
                isConnected = isConnected,
                colors = colors,
                onEmergencyBrake = {
                    activeDriveDirection = "STOP"
                    repository.emergencyStop()
                }
            )

            // 2. BATARYA & ŞARJ YÖNETİM ROZETİ (Li-Ion 2S)
            BatteryStatusCard(
                telemetry = telemetry,
                colors = colors,
                onToggleCharging = { repository.toggleChargingSimulation() }
            )

            // 3. SÜRÜŞ MODU SEÇİCİ (OTONOM vs MANUEL)
            DrivingModeSelector(
                isManual = telemetry.isManualMode,
                colors = colors,
                onSelectMode = { isManual ->
                    repository.setManualMode(isManual)
                }
            )

            // 4. D-PAD DİREKSİYON & SERVO KONTROL KONSOLU
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // SOL: Taktil D-Pad (Yön Butonları)
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.bgSecondary)
                        .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🕹️ ${Strings.btnForward} / ${Strings.btnBackward}",
                            color = colors.textMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // İLERİ BUTONU
                        DPadButton(
                            label = "▲ ${Strings.btnForward}",
                            isActive = activeDriveDirection == "FORWARD",
                            colors = colors,
                            accentColor = Color(0xFF10B981),
                            modifier = Modifier.width(96.dp).height(46.dp),
                            onPress = {
                                activeDriveDirection = "FORWARD"
                                repository.sendDriveCommand("FORWARD", currentSpeedPwm.toInt())
                            },
                            onRelease = {
                                activeDriveDirection = "STOP"
                                repository.sendDriveCommand("STOP", currentSpeedPwm.toInt())
                            }
                        )

                        // ORTA SATIR: SOL - DUR - SAĞ
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DPadButton(
                                label = "◀ ${Strings.btnLeft}",
                                isActive = activeDriveDirection == "LEFT",
                                colors = colors,
                                accentColor = Color(0xFF3B82F6),
                                modifier = Modifier.size(width = 62.dp, height = 44.dp),
                                onPress = {
                                    activeDriveDirection = "LEFT"
                                    repository.sendDriveCommand("LEFT", currentSpeedPwm.toInt())
                                },
                                onRelease = {
                                    activeDriveDirection = "STOP"
                                    repository.sendDriveCommand("STOP", currentSpeedPwm.toInt())
                                }
                            )

                            // DURDUR BUTONU
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (activeDriveDirection == "STOP") Color(0xFFEF4444) else colors.bgPrimary)
                                    .border(1.5.dp, Color(0xFFEF4444), CircleShape)
                                    .clickable {
                                        activeDriveDirection = "STOP"
                                        repository.sendDriveCommand("STOP", 0)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "■",
                                    color = if (activeDriveDirection == "STOP") Color.White else Color(0xFFEF4444),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            DPadButton(
                                label = "${Strings.btnRight} ▶",
                                isActive = activeDriveDirection == "RIGHT",
                                colors = colors,
                                accentColor = Color(0xFF3B82F6),
                                modifier = Modifier.size(width = 62.dp, height = 44.dp),
                                onPress = {
                                    activeDriveDirection = "RIGHT"
                                    repository.sendDriveCommand("RIGHT", currentSpeedPwm.toInt())
                                },
                                onRelease = {
                                    activeDriveDirection = "STOP"
                                    repository.sendDriveCommand("STOP", currentSpeedPwm.toInt())
                                }
                            )
                        }

                        // GERİ BUTONU
                        DPadButton(
                            label = "▼ ${Strings.btnBackward}",
                            isActive = activeDriveDirection == "BACKWARD",
                            colors = colors,
                            accentColor = Color(0xFFF59E0B),
                            modifier = Modifier.width(96.dp).height(46.dp),
                            onPress = {
                                activeDriveDirection = "BACKWARD"
                                repository.sendDriveCommand("BACKWARD", currentSpeedPwm.toInt())
                            },
                            onRelease = {
                                activeDriveDirection = "STOP"
                                repository.sendDriveCommand("STOP", currentSpeedPwm.toInt())
                            }
                        )
                    }
                }

                // SAĞ: Servo Açı Ayarı & Radar Yönü
                Column(
                    modifier = Modifier
                        .weight(0.9f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.bgSecondary)
                        .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📡 ${Strings.servoTitle}",
                        color = colors.textMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Anlık Servo Açısı
                    Text(
                        text = "${manualServoAngle.toInt()}°",
                        color = Color(0xFF2563EB),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = when {
                            manualServoAngle.toInt() in 85..95 -> "🎯 ${Strings.center}"
                            manualServoAngle.toInt() > 95 -> "◀ ${Strings.leftScan}"
                            else -> "▶ ${Strings.rightScan}"
                        },
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Servo Slider
                    Slider(
                        value = manualServoAngle,
                        onValueChange = {
                            manualServoAngle = it
                            repository.setServoAngle(it.toInt())
                        },
                        valueRange = 0f..180f,
                        steps = 11,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Hızlı Açı Butonları
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        QuickAngleBtn("160°", colors, modifier = Modifier.weight(1f)) {
                            manualServoAngle = 160f
                            repository.setServoAngle(160)
                        }
                        QuickAngleBtn("90°", colors, modifier = Modifier.weight(1f)) {
                            manualServoAngle = 90f
                            repository.setServoAngle(90)
                        }
                        QuickAngleBtn("20°", colors, modifier = Modifier.weight(1f)) {
                            manualServoAngle = 20f
                            repository.setServoAngle(20)
                        }
                    }
                }
            }

            // 5. MOTOR HIZ AYARI (PWM SLIDER & VİTES PRESETLERİ)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.bgSecondary),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ ${Strings.speedLabel}",
                            color = colors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${currentSpeedPwm.toInt()} PWM (%${(currentSpeedPwm / 255f * 100).toInt()})",
                            color = Color(0xFF2563EB),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Slider(
                        value = currentSpeedPwm,
                        onValueChange = {
                            currentSpeedPwm = it
                            if (activeDriveDirection != "STOP") {
                                repository.sendDriveCommand(activeDriveDirection, it.toInt())
                            }
                        },
                        valueRange = 80f..255f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Vites Seçenekleri (Eko / Normal / Turbo)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GearButton("🌱 Eko (120)", currentSpeedPwm == 120f, colors, Modifier.weight(1f)) {
                            currentSpeedPwm = 120f
                        }
                        GearButton("🚗 Standart (180)", currentSpeedPwm == 180f, colors, Modifier.weight(1f)) {
                            currentSpeedPwm = 180f
                        }
                        GearButton("🚀 Turbo (255)", currentSpeedPwm == 255f, colors, Modifier.weight(1f)) {
                            currentSpeedPwm = 255f
                        }
                    }
                }
            }

            // 6. EKSTRA DONANIM MÜDAHALE AKSİYONLARI (KORNA, ACİL DURMA, 3D GEÇİŞ)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { repository.triggerHorn() },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                ) {
                    Text(
                        text = Strings.btnHorn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onNavigateTo3D,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text(
                        text = "🤖 ${Strings.navRobotTwin} Görüntüle",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CockpitHeader(
    telemetry: com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry,
    isConnected: Boolean,
    colors: AppColors,
    onEmergencyBrake: () -> Unit
) {
    ControllerHeaderCard(
        telemetry = telemetry,
        isConnected = isConnected,
        colors = colors,
        onEmergencyBrake = onEmergencyBrake
    )
}

@Composable
fun BatteryStatusCard(
    telemetry: com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry,
    colors: AppColors,
    onToggleCharging: () -> Unit
) {
    val pct = telemetry.batteryPercentage
    val volt = telemetry.batteryVoltage
    val isChg = telemetry.isCharging

    val barColor = when {
        isChg -> Color(0xFF10B981)
        pct <= 15 -> Color(0xFFEF4444)
        pct <= 35 -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bgSecondary)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isChg) "⚡" else "🔋",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.batteryTitle,
                        color = colors.textPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Durum Rozeti (Şarj Oluyor / Kullanımda)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(barColor.copy(alpha = 0.15f))
                        .border(1.dp, barColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isChg) Strings.batteryCharging else if (pct <= 15) Strings.batteryCritical else Strings.batteryDischarging,
                        color = barColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Batarya Doluluk Barı & Metrik
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // İlerleme Çubuğu
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(colors.bgPrimary)
                        .border(0.8.dp, colors.border, RoundedCornerShape(7.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(pct / 100f)
                            .clip(RoundedCornerShape(7.dp))
                            .background(barColor)
                    )
                }

                // Yüzde ve Voltaj Göstergesi
                Text(
                    text = "%$pct ($volt V / 8.4V)",
                    color = colors.textPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Simülasyon Butonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (isChg) "⚡ Şarj Cihazını Çıkar" else "🔌 Şarja Tak (Simüle Et)",
                    color = Color(0xFF2563EB),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable(onClick = onToggleCharging)
                        .padding(vertical = 2.dp, horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DrivingModeSelector(
    isManual: Boolean,
    colors: AppColors,
    onSelectMode: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.bgSecondary)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // OTONOM MOD TAB
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(if (!isManual) Color(0xFF10B981) else Color.Transparent)
                .clickable { onSelectMode(false) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = Strings.modeAutonomous,
                color = if (!isManual) Color.White else colors.textMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // MANUEL MOD TAB
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isManual) Color(0xFF2563EB) else Color.Transparent)
                .clickable { onSelectMode(true) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = Strings.modeManual,
                color = if (isManual) Color.White else colors.textMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DPadButton(
    label: String,
    isActive: Boolean,
    colors: AppColors,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isActive) accentColor else colors.bgPrimary)
            .border(1.2.dp, if (isActive) accentColor else colors.border, RoundedCornerShape(10.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPress()
                        tryAwaitRelease()
                        onRelease()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isActive) Color.White else colors.textPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuickAngleBtn(
    label: String,
    colors: AppColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(colors.bgPrimary)
            .border(0.8.dp, colors.border, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = colors.textSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun GearButton(
    label: String,
    isSelected: Boolean,
    colors: AppColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(if (isSelected) Color(0xFF2563EB) else colors.bgPrimary)
            .border(1.dp, if (isSelected) Color(0xFF2563EB) else colors.border, RoundedCornerShape(7.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else colors.textMuted,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// ==========================================
// COMPOSE PREVIEW (BNV ÜSTÜ GÜVENLİ ALAN)
// ==========================================
@Preview
@Composable
private fun RobotControllerScreenPreview() {
    RobotControllerScreen(
        repository = remember { MockTelemetryRepository() },
        themeMode = ThemeMode.DARK,
        languageMode = LanguageMode.TR
    )
}

