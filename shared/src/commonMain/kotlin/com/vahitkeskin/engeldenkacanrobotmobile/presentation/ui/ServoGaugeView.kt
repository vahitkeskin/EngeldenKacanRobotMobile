package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * 📡 ServoGaugeView
 * Robotun önündeki SG90 servo motorun anlık dönme açısını (0° Sağ - 90° Merkez - 180° Sol)
 * modern ve yüksek okunabilirlikli bir yarı dairesel gösterge kadranı (instrument dial) olarak sunar.
 */
@Composable
fun ServoGaugeView(
    telemetry: RobotTelemetry,
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.radarBg)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // BAŞLIK (RadarView ile dikey eksende birebir eşit 2 satır)
        Text(
            text = Strings.servoTitle,
            color = colors.textPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            minLines = 2,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)
        )

        val angleColor = when {
            !telemetry.hasSignal -> colors.textMuted
            telemetry.servoAngle in 80..100 -> Color(0xFF10B981) // Merkez (Zümrüt Yeşil)
            telemetry.servoAngle < 80 -> Color(0xFF38BDF8)       // Sağ (Açık Mavi)
            else -> Color(0xFFA855F7)                            // Sol (Mor)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // KADRAN VE İBRE ÇİZİMİ (Canvas)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2
                // cy: yayın taban hizası (alttaki 180° ve 0° etiketlerinin hemen üstü)
                val cy = size.height - 24.dp.toPx()
                val radius = min(cx - 20.dp.toPx(), cy - 8.dp.toPx())

                val trackWidth = 7.dp.toPx()
                val arcTopLeft = Offset(cx - radius, cy - radius)
                val arcSize = Size(radius * 2, radius * 2)

                // 1. Kadran Arka Plan Yayı (180° Sol'dan 0° Sağ'a yarım daire)
                drawArc(
                    color = colors.border,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = trackWidth, cap = StrokeCap.Round)
                )

                // 2. Kadran Açı Çentikleri (0°, 30°, 60°, 90°, 120°, 150°, 180°)
                for (a in 0..180 step 30) {
                    val rad = a * (PI / 180.0)
                    val isMajor = (a % 90 == 0)
                    val tickLen = if (isMajor) 7.dp.toPx() else 4.dp.toPx()
                    val r1 = radius - tickLen
                    val r2 = radius + 2.dp.toPx()

                    val x1 = cx + (r1 * cos(rad)).toFloat()
                    val y1 = cy - (r1 * sin(rad)).toFloat()
                    val x2 = cx + (r2 * cos(rad)).toFloat()
                    val y2 = cy - (r2 * sin(rad)).toFloat()

                    drawLine(
                        color = if (isMajor) colors.textMuted.copy(alpha = 0.7f) else colors.radarGrid,
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = if (isMajor) 1.8f else 1.0f,
                        cap = StrokeCap.Round
                    )
                }

                if (telemetry.hasSignal) {
                    // 3. Aktif İlerleme Yayı: 90° (Merkez) noktasından anlık açıya doğru
                    // Compose Canvas açılarında: 90° üst = 270f, 180° sol = 180f, 0° sağ = 360f/0f
                    if (telemetry.servoAngle > 90) {
                        // Sol tarama: 270f'den geriye (saat yönünün tersine)
                        val sweep = -(telemetry.servoAngle - 90f)
                        drawArc(
                            color = angleColor,
                            startAngle = 270f,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = trackWidth, cap = StrokeCap.Round)
                        )
                    } else if (telemetry.servoAngle < 90) {
                        // Sağ tarama: 270f'den ileriye (saat yönünde)
                        val sweep = (90f - telemetry.servoAngle)
                        drawArc(
                            color = angleColor,
                            startAngle = 270f,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = trackWidth, cap = StrokeCap.Round)
                        )
                    }

                    // 4. Kadran Üstündeki Parlayan Gösterge Düğmesi (Indicator Thumb)
                    val rad = telemetry.servoAngle * (PI / 180.0)
                    val tx = cx + radius * cos(rad).toFloat()
                    val ty = cy - radius * sin(rad).toFloat()

                    // Dış parlama halkası
                    drawCircle(
                        color = angleColor.copy(alpha = 0.35f),
                        radius = 8.dp.toPx(),
                        center = Offset(tx, ty)
                    )
                    // Halka gövdesi
                    drawCircle(
                        color = colors.radarBg,
                        radius = 6.dp.toPx(),
                        center = Offset(tx, ty)
                    )
                    drawCircle(
                        color = angleColor,
                        radius = 6.dp.toPx(),
                        center = Offset(tx, ty),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    // Merkez beyaz nokta
                    drawCircle(
                        color = Color.White,
                        radius = 2.5.dp.toPx(),
                        center = Offset(tx, ty)
                    )
                }
            }

            // KADRANIN TAM İÇİNDEKİ DEĞER VE DURUM METRİĞİ (İbre ile asla çakışmaz)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 16.dp)
            ) {
                if (telemetry.hasSignal) {
                    Text(
                        text = "${telemetry.servoAngle}°",
                        color = angleColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )

                    val posLabel = when {
                        telemetry.servoAngle < 80 -> "▶ ${Strings.rightScan} (${telemetry.servoAngle}°)"
                        telemetry.servoAngle > 100 -> "◀ ${Strings.leftScan} (${telemetry.servoAngle}°)"
                        else -> "🎯 ${Strings.center}"
                    }

                    Text(
                        text = posLabel,
                        color = colors.textSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = "--°",
                        color = colors.textMuted,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )
                    Text(
                        text = "[${Strings.noSignal.uppercase()}]",
                        color = Color(0xFFEF4444),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            // KADRAN TABANINDAKİ 180° VE 0° REFERANS ETİKETLERİ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, bottom = 2.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "180°",
                    color = colors.textMuted.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "0°",
                    color = colors.textMuted.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
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
private fun ServoGaugeViewPreview() {
    val sample = RobotTelemetry(
        servoAngle = 120,
        hasSignal = true
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Dark.bgPrimary)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .padding(bottom = 80.dp) // BNV üstünde kalacak şekilde güvenli alan
    ) {
        ServoGaugeView(
            telemetry = sample,
            colors = AppColors.Dark,
            modifier = Modifier.fillMaxWidth().height(185.dp)
        )
    }
}
