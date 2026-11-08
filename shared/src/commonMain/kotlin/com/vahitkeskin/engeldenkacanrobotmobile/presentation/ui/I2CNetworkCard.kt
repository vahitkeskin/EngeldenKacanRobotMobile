package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui
import com.vahitkeskin.engeldenkacanrobotmobile.theme.EKRobotColor

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.getRealNetworkInfo
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun I2CNetworkCard(
    telemetry: RobotTelemetry,
    colors: AppColors,
    modifier: Modifier = Modifier,
    onScrollToTop: (() -> Unit)? = null
) {
    var isExpanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    // Cihazın işletim sisteminden %100 GERÇEK anlık ağ bilgilerini al
    val realNet = remember { getRealNetworkInfo() }

    // Arduino donanım I2C slave parametreleri (Atmega328P Wire.h)
    val i2cAddress = "0x08"
    val i2cSpeed = "100 kHz (Standart I2C)"
    val sdaPin = "A4 (Pin 18/20)"
    val sclPin = "A5 (Pin 19/21)"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.bgCard)
            .border(0.8.dp, colors.border, RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ==========================================
            // 1. TIKLANABİLİR BAŞLIK (AÇILIR / KAPANIR & EN ÜSTE ANİMASYONLU HİZALAMA)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isExpanded = !isExpanded
                        if (isExpanded) {
                            onScrollToTop?.invoke()
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // İkon Rozeti
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(EKRobotColor.Brand.BluePrimary.copy(alpha = 0.15f))
                            .border(0.8.dp, EKRobotColor.Brand.BluePrimary.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌐", fontSize = 16.sp)
                    }

                    // Başlık ve Gerçek Özet Bilgiler
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = Strings.i2cCardTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                letterSpacing = 0.4.sp
                            )
                            // Gerçek Hat Durumu Rozeti
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (telemetry.hasSignal) EKRobotColor.Status.Success.copy(alpha = 0.15f)
                                        else EKRobotColor.Theme.Light.TextMuted.copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (telemetry.hasSignal) "● ${Strings.statusLive}" else "○ ${Strings.statusStandby}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (telemetry.hasSignal) EKRobotColor.Status.Success else colors.textMuted
                                )
                            }
                        }

                        // Özet Satırı (Gerçek I2C Adresi & Gerçek Cihaz IP'si)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Slave: $i2cAddress",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                color = EKRobotColor.Brand.SkyAccent
                            )
                            Text(
                                text = "•",
                                fontSize = 10.sp,
                                color = colors.textMuted
                            )
                            Text(
                                text = "IP: ${realNet.ipAddress}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textMuted
                            )
                        }
                    }
                }

                // Aç / Kapa Gösterge Oku
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(colors.bgPrimary)
                        .border(0.6.dp, colors.border, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▼",
                        fontSize = 10.sp,
                        color = colors.textPrimary,
                        modifier = Modifier.rotate(arrowRotation)
                    )
                }
            }

            // ==========================================
            // 2. AÇILIR GÖVDE (YALNIZCA %100 GERÇEK BİLGİLER)
            // ==========================================
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(250)) + expandVertically(animationSpec = tween(320, easing = FastOutSlowInEasing)),
                exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(280, easing = FastOutSlowInEasing))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Ayırıcı Çizgi
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.6.dp)
                            .background(colors.border)
                    )

                    // BÖLÜM 1: I2C DONANIM VE VERİYOLU SPESİFİKASYONU (ARDUINO ATMEGA328P)
                    SectionHeader(
                        icon = "⚡",
                        title = Strings.i2cSectionHardware,
                        colors = colors
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoBox(
                            title = Strings.i2cSlaveAddr,
                            value = "$i2cAddress (7-bit)",
                            subtext = "Atmega328P Hardware Slave",
                            highlightColor = EKRobotColor.Brand.SkyAccent,
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                        InfoBox(
                            title = Strings.i2cBusSpeed,
                            value = i2cSpeed,
                            subtext = Strings.i2cOscillator,
                            highlightColor = EKRobotColor.Status.Success,
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoBox(
                            title = Strings.i2cPins,
                            value = "$sdaPin | $sclPin",
                            subtext = "Dahili 20-50 kΩ Pull-up",
                            highlightColor = EKRobotColor.Status.Warning,
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                        InfoBox(
                            title = Strings.i2cStatus,
                            value = if (telemetry.hasSignal) Strings.i2cActiveLine else Strings.i2cWaitingLine,
                            subtext = "Wire.h Slave Callback",
                            highlightColor = if (telemetry.hasSignal) EKRobotColor.Status.Success else colors.textMuted,
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // BÖLÜM 2: %100 GERÇEK CİHAZ AĞ VE WI-FI BİLGİLERİ
                    SectionHeader(
                        icon = "📶",
                        title = Strings.i2cSectionNetwork,
                        colors = colors
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.bgPrimary)
                            .border(0.6.dp, colors.border, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        NetworkRow(
                            label = Strings.wifiSsidLabel,
                            value = realNet.networkName,
                            accentColor = EKRobotColor.Brand.BlueLight,
                            colors = colors
                        )
                        NetworkRow(
                            label = Strings.ipAddressLabel,
                            value = realNet.ipAddress,
                            accentColor = EKRobotColor.Status.Success,
                            colors = colors
                        )
                        NetworkRow(
                            label = Strings.i2cNetworkInterface,
                            value = realNet.interfaceName,
                            accentColor = colors.textPrimary,
                            colors = colors
                        )
                        NetworkRow(
                            label = Strings.subnetLabel,
                            value = realNet.subnetMask,
                            accentColor = colors.textMuted,
                            colors = colors
                        )
                        NetworkRow(
                            label = Strings.macLabel,
                            value = realNet.macAddress,
                            accentColor = colors.textPrimary,
                            colors = colors
                        )
                        if (realNet.signalLevel != "--") {
                            NetworkRow(
                                label = Strings.rssiLabel,
                                value = realNet.signalLevel,
                                accentColor = EKRobotColor.Status.Success,
                                colors = colors
                            )
                        }
                    }

                    // BÖLÜM 3: I2C KAYIT (REGISTER) HARİTASI (CANLI GERÇEK TELEMETRİ)
                    SectionHeader(
                        icon = "📋",
                        title = Strings.i2cSectionRegisters,
                        colors = colors
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.bgPrimary)
                            .border(0.6.dp, colors.border, RoundedCornerShape(8.dp))
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        telemetry.i2cRegisters.forEach { reg ->
                            RegisterRow(
                                entry = reg,
                                colors = colors
                            )
                        }
                    }

                    // BÖLÜM 4: DONANIM VE BATARYA ENTEGRASYONU
                    SectionHeader(
                        icon = "🔋",
                        title = Strings.i2cHardwareIntegration,
                        colors = colors
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoBox(
                            title = Strings.i2cBatteryChemistry,
                            value = "2S 18650 Li-Ion",
                            subtext = "7.4V Nom / 8.4V Tepe (ADC A0)",
                            highlightColor = EKRobotColor.Status.Success,
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                        InfoBox(
                            title = Strings.i2cBaudRate,
                            value = "115200 Baud",
                            subtext = Strings.i2cUartBridge,
                            highlightColor = EKRobotColor.Brand.BlueLight,
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (onScrollToTop != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.bgPrimary)
                                .border(0.6.dp, colors.border, RoundedCornerShape(6.dp))
                                .clickable { onScrollToTop.invoke() }
                                .padding(vertical = 7.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Strings.i2cScrollToTop,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EKRobotColor.Brand.SkyAccent,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: String,
    title: String,
    colors: AppColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(top = 2.dp)
    ) {
        Text(text = icon, fontSize = 11.sp)
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun InfoBox(
    title: String,
    value: String,
    subtext: String,
    highlightColor: Color,
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.bgPrimary)
            .border(0.6.dp, colors.border, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textMuted
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = highlightColor
            )
            Text(
                text = subtext,
                fontSize = 8.sp,
                color = colors.textMuted
            )
        }
    }
}

@Composable
private fun NetworkRow(
    label: String,
    value: String,
    accentColor: Color,
    colors: AppColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = colors.textMuted
        )
        Text(
            text = value,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            color = accentColor
        )
    }
}

@Composable
private fun RegisterRow(
    entry: com.vahitkeskin.engeldenkacanrobotmobile.domain.model.I2CRegisterEntry,
    colors: AppColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(colors.bgCard.copy(alpha = 0.6f))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(EKRobotColor.Brand.BluePrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = entry.regHex,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = EKRobotColor.Brand.BlueLight
                )
            }
            Text(
                text = entry.name,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = colors.textPrimary
            )
        }
        Text(
            text = entry.currentValue,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = EKRobotColor.Status.Success
        )
    }
}

// ==========================================
// COMPOSE PREVIEW (BNV ÜSTÜ GÜVENLİ ALAN)
// ==========================================
@Preview
@Composable
private fun I2CNetworkCardPreview() {
    val sampleTelemetry = RobotTelemetry(
        frontDistance = 42,
        servoAngle = 90,
        batteryVoltage = 7.84f,
        batteryPercentage = 85,
        isCharging = false,
        hasSignal = true
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Dark.bgPrimary)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .padding(bottom = 80.dp) // BNV üstünde kalacak şekilde güvenli boşluk
    ) {
        I2CNetworkCard(
            telemetry = sampleTelemetry,
            colors = AppColors.Dark
        )
    }
}
