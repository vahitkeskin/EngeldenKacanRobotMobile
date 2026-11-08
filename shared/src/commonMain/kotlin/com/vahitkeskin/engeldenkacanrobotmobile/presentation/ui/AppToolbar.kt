package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui
import com.vahitkeskin.engeldenkacanrobotmobile.theme.EKRobotColor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotState
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors

/**
 * 🔋 HeaderBatteryPill
 * Yüksek çözünürlüklü donanım/IoT batarya göstergesi.
 * Mikro pil hücresi vektörü (kasa, anot çıkıntısı, doluluk barı) ve net yüzdelik metni içerir.
 * Asla satır atlamaz, ezilmez ve her ekranda kusursuz görünür.
 */
@Composable
fun HeaderBatteryPill(
    batteryPercentage: Int,
    isCharging: Boolean,
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    val pct = batteryPercentage.coerceIn(0, 100)
    val batColor = when {
        isCharging -> EKRobotColor.Status.Success // Yeşil (Şarj Oluyor)
        pct <= 15 -> EKRobotColor.Status.Danger  // Kritik Kırmızı
        pct <= 35 -> EKRobotColor.Status.Warning  // Uyarı Sarı
        else -> EKRobotColor.Status.Success       // Güvenli Yeşil
    }

    Box(
        modifier = modifier
            .height(26.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(batColor.copy(alpha = 0.12f))
            .border(0.8.dp, batColor.copy(alpha = 0.45f), RoundedCornerShape(7.dp))
            .padding(horizontal = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (isCharging) {
                Text(
                    text = "⚡",
                    fontSize = 10.sp,
                    color = batColor
                )
            } else {
                // Mikro Pil Vektörü (Kasa, Anot Ucu ve Seviye Barı)
                Canvas(modifier = Modifier.size(width = 17.dp, height = 10.dp)) {
                    val strokeW = 1.dp.toPx()
                    val bodyWidth = 14.5.dp.toPx()
                    val bodyHeight = size.height
                    val cornerR = 2.dp.toPx()
                    val nubWidth = 2.dp.toPx()
                    val nubHeight = 4.dp.toPx()

                    // Pil Dış Çerçevesi
                    drawRoundRect(
                        color = batColor,
                        topLeft = Offset(0f, 0f),
                        size = Size(bodyWidth, bodyHeight),
                        cornerRadius = CornerRadius(cornerR, cornerR),
                        style = Stroke(width = strokeW)
                    )

                    // Pozitif (+) Terminal Ucu
                    drawRoundRect(
                        color = batColor,
                        topLeft = Offset(bodyWidth + 0.5.dp.toPx(), (bodyHeight - nubHeight) / 2f),
                        size = Size(nubWidth, nubHeight),
                        cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                    )

                    // İç Doluluk Barı
                    val fillPad = 2.dp.toPx()
                    val maxFillW = bodyWidth - fillPad * 2f
                    val actualFillW = (maxFillW * (pct / 100f)).coerceIn(1.5f, maxFillW)
                    drawRoundRect(
                        color = batColor,
                        topLeft = Offset(fillPad, fillPad),
                        size = Size(actualFillW, bodyHeight - fillPad * 2f),
                        cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                    )
                }
            }

            // Yüzde Metni (%85)
            Text(
                text = "%$pct",
                color = batColor,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

/**
 * 📡 HeaderStatusBadge
 * Robotun bağlantı ve hareket durumunu gösteren, asla satır atlamayan kompakt rozet.
 */
@Composable
fun HeaderStatusBadge(
    telemetryHasSignal: Boolean,
    robotState: RobotState,
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    val badgeText = if (!telemetryHasSignal) {
        Strings.noSignal
    } else {
        when (robotState) {
            RobotState.CRUISING -> if (Strings.isTurkish()) "Seyir" else "Cruise"
            RobotState.AVOIDING -> if (Strings.isTurkish()) "Kaçış" else "Avoid"
            RobotState.MANUAL_OVERRIDE -> if (Strings.isTurkish()) "Manuel" else "Manual"
            RobotState.STOPPED -> if (Strings.isTurkish()) "Durduruldu" else "Stopped"
            else -> Strings.noSignal
        }
    }

    val badgeColor = if (telemetryHasSignal) robotState.badgeColor else EKRobotColor.Status.Danger
    val dotColor = if (telemetryHasSignal) EKRobotColor.Status.Success else EKRobotColor.Status.Danger

    Box(
        modifier = modifier
            .height(26.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(badgeColor.copy(alpha = 0.16f))
            .border(0.8.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(7.dp))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(
                text = badgeText,
                color = if (telemetryHasSignal) colors.textPrimary else badgeColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

/**
 * 🧭 DashboardHeaderCard (Telemetri Ekranı Üst Toolbar)
 */
@Composable
fun DashboardHeaderCard(
    telemetry: RobotTelemetry,
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bgSecondary)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(if (telemetry.hasSignal) EKRobotColor.Status.Success else EKRobotColor.Status.Danger)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = Strings.appTitle,
                    color = colors.textPrimary,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = Strings.i2cBus,
                    color = colors.textMuted,
                    fontSize = 9.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            HeaderBatteryPill(
                batteryPercentage = telemetry.batteryPercentage,
                isCharging = telemetry.isCharging,
                colors = colors
            )
            HeaderStatusBadge(
                telemetryHasSignal = telemetry.hasSignal,
                robotState = telemetry.state,
                colors = colors
            )
        }
    }
}

/**
 * 🤖 Robot3DHeaderCard (3D Dijital İkiz Ekranı Üst Toolbar)
 */
@Composable
fun Robot3DHeaderCard(
    telemetry: RobotTelemetry,
    colors: AppColors,
    onNavigateToTelemetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.bgSecondary.copy(alpha = 0.95f))
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 9.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Geri Butonu (Kompakt ve şık)
            Box(
                modifier = Modifier
                    .height(26.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.bgPrimary)
                    .border(0.6.dp, colors.border, RoundedCornerShape(6.dp))
                    .clickable(onClick = onNavigateToTelemetry)
                    .padding(horizontal = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⬅️ " + Strings.navTelemetry,
                    color = colors.textPrimary,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.width(7.dp))

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (telemetry.hasSignal) EKRobotColor.Status.Success else EKRobotColor.Status.Danger)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "🤖 " + (if (Strings.isTurkish()) "3D Dijital İkiz" else "3D Digital Twin"),
                    color = colors.textPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "I2C Motor • 0x08",
                    color = colors.textMuted,
                    fontSize = 8.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            HeaderBatteryPill(
                batteryPercentage = telemetry.batteryPercentage,
                isCharging = telemetry.isCharging,
                colors = colors
            )
            HeaderStatusBadge(
                telemetryHasSignal = telemetry.hasSignal,
                robotState = telemetry.state,
                colors = colors
            )
        }
    }
}

/**
 * 🎮 ControllerHeaderCard (Sanal Kokpit & Kumanda Ekranı Üst Toolbar)
 */
@Composable
fun ControllerHeaderCard(
    telemetry: RobotTelemetry,
    isConnected: Boolean,
    colors: AppColors,
    onEmergencyBrake: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bgSecondary)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(if (isConnected) EKRobotColor.Status.Success else EKRobotColor.Status.Danger)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = Strings.cockpitTitle,
                    color = colors.textPrimary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = Strings.cockpitSubtitle,
                    color = colors.textMuted,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            HeaderBatteryPill(
                batteryPercentage = telemetry.batteryPercentage,
                isCharging = telemetry.isCharging,
                colors = colors
            )

            // ACİL FREN BUTONU
            Button(
                onClick = onEmergencyBrake,
                shape = RoundedCornerShape(7.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EKRobotColor.Status.DangerDark),
                contentPadding = PaddingValues(horizontal = 9.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    text = Strings.btnEmergencyStop,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

// ==========================================
// COMPOSE PREVIEW (BNV ÜSTÜ GÜVENLİ ALAN)
// ==========================================
@Preview
@Composable
private fun AppToolbarPreview() {
    val sample = RobotTelemetry(
        frontDistance = 35,
        batteryPercentage = 85,
        batteryVoltage = 7.84f,
        hasSignal = false
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(EKRobotColor.Alpha.NightDeepest)
            .padding(16.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DashboardHeaderCard(telemetry = sample, colors = AppColors.Dark)
        Robot3DHeaderCard(telemetry = sample, colors = AppColors.Dark, onNavigateToTelemetry = {})
        ControllerHeaderCard(telemetry = sample, isConnected = false, colors = AppColors.Dark, onEmergencyBrake = {})
    }
}

