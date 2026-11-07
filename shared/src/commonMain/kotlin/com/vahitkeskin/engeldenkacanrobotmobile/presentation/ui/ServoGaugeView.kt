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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

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
        Text(
            text = Strings.servoTitle,
            color = colors.textPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
        )

        Canvas(modifier = Modifier.fillMaxWidth().height(110.dp)) {
            val cx = size.width / 2
            val cy = size.height - 10f
            val radius = min(cx - 20f, cy - 10f)

            // Arka Plan Yayı
            drawArc(
                color = colors.border,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 16f, cap = StrokeCap.Round)
            )

            if (telemetry.hasSignal) {
                // Aktif İlerleme Yayı
                val sweep = (telemetry.servoAngle.toFloat() / 180f) * 180f
                drawArc(
                    color = Color(0xFF3B82F6),
                    startAngle = 180f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(cx - radius, cy - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )

                // Gerçek İbre Çizgisi
                val rad = telemetry.servoAngle * (PI / 180.0)
                val nx = cx + ((radius - 8f) * cos(rad)).toFloat()
                val ny = cy - ((radius - 8f) * sin(rad)).toFloat()

                drawLine(
                    color = Color(0xFFEF4444),
                    start = Offset(cx, cy),
                    end = Offset(nx, ny),
                    strokeWidth = 3.5f,
                    cap = StrokeCap.Round
                )

                drawCircle(color = colors.bgCard, radius = 7f, center = Offset(cx, cy))
                drawCircle(color = Color(0xFFEF4444), radius = 3.5f, center = Offset(cx, cy))
            }
        }

        if (telemetry.hasSignal) {
            Text(
                text = "${telemetry.servoAngle}°",
                color = colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            val posLabel = when {
                telemetry.servoAngle < 70 -> "${Strings.rightScan} (${telemetry.servoAngle}°)"
                telemetry.servoAngle > 110 -> "${Strings.leftScan} (${telemetry.servoAngle}°)"
                else -> "${Strings.center} (90°)"
            }
            val posColor = when {
                telemetry.servoAngle < 70 -> Color(0xFF60A5FA)
                telemetry.servoAngle > 110 -> Color(0xFFF472B6)
                else -> Color(0xFF34D399)
            }

            Text(
                text = posLabel,
                color = posColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            Text(
                text = "--°",
                color = colors.textMuted,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "[${Strings.noSignal.uppercase()}]",
                color = Color(0xFFEF4444),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
