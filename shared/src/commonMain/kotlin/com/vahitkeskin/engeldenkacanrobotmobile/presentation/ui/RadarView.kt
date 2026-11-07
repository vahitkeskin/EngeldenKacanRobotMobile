package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun RadarView(
    telemetry: RobotTelemetry,
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.radarBg)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = Strings.radarTitle,
            color = colors.textPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
            val cx = size.width / 2
            val cy = size.height - 15f
            val maxRadius = min(cx - 20f, cy - 15f)

            // Konsantrik Mesafe Yayları (10, 20, 30, 40, 50 cm)
            val rings = 5
            for (i in 1..rings) {
                val r = (maxRadius * i) / rings
                drawArc(
                    color = colors.radarGrid,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(cx - r, cy - r),
                    size = Size(r * 2, r * 2),
                    style = Stroke(width = 1.2f)
                )
            }

            // Açı Çizgileri
            for (a in 0..180 step 30) {
                val rad = a * (PI / 180.0)
                val ex = cx + (maxRadius * cos(rad)).toFloat()
                val ey = cy - (maxRadius * sin(rad)).toFloat()
                drawLine(
                    color = colors.radarGrid,
                    start = Offset(cx, cy),
                    end = Offset(ex, ey),
                    strokeWidth = 1f
                )
            }

            if (telemetry.hasSignal) {
                // Gerçek Servo Işını
                val beamRad = telemetry.servoAngle * (PI / 180.0)
                val bx = cx + (maxRadius * cos(beamRad)).toFloat()
                val by = cy - (maxRadius * sin(beamRad)).toFloat()

                drawLine(
                    color = Color(0x3310B981),
                    start = Offset(cx, cy),
                    end = Offset(bx, by),
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color(0xFF34D399),
                    start = Offset(cx, cy),
                    end = Offset(bx, by),
                    strokeWidth = 2.5f,
                    cap = StrokeCap.Round
                )

                // Gerçek Engel Noktası
                if (telemetry.frontDistance in 3..50) {
                    val rNorm = (telemetry.frontDistance / 50.0f) * maxRadius
                    val obsX = cx + (rNorm * cos(beamRad)).toFloat()
                    val obsY = cy - (rNorm * sin(beamRad)).toFloat()

                    drawCircle(color = Color(0xFFEF4444), radius = 5f, center = Offset(obsX, obsY))
                    drawCircle(color = Color(0xFFFECACA), radius = 2.5f, center = Offset(obsX, obsY))
                }
            }

            // Merkez Noktası
            drawCircle(
                color = if (telemetry.hasSignal) Color(0xFF10B981) else colors.textMuted,
                radius = 6f,
                center = Offset(cx, cy)
            )
            drawCircle(color = Color.White, radius = 2.5f, center = Offset(cx, cy))
        }

        if (!telemetry.hasSignal) {
            Text(
                text = "[${Strings.noSignal.uppercase()}]",
                color = Color(0xFFEF4444),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
