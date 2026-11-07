package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ServoGaugeView(
    servoAngle: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SERVO DÖNME AÇISI",
            color = Color(0xFFF1F5F9),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
        )

        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            val cx = size.width / 2
            val cy = size.height - 10f
            val radius = min(cx - 20f, cy - 10f)

            // Arka Plan Yayı (180 derece)
            drawArc(
                color = Color(0xFF1E293B),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 18f, cap = StrokeCap.Round)
            )

            // Aktif İlerleme Yayı (0'dan servo açısına)
            val sweep = (servoAngle.toFloat() / 180f) * 180f
            drawArc(
                color = Color(0xFF3B82F6),
                startAngle = 180f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 18f, cap = StrokeCap.Round)
            )

            // İbre Çizgisi
            val rad = servoAngle * (PI / 180.0)
            val nx = cx + ((radius - 10f) * cos(rad)).toFloat()
            val ny = cy - ((radius - 10f) * sin(rad)).toFloat()

            drawLine(
                color = Color(0xFFEF4444),
                start = Offset(cx, cy),
                end = Offset(nx, ny),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )

            // Göbek Noktası
            drawCircle(color = Color(0xFF1E293B), radius = 8f, center = Offset(cx, cy))
            drawCircle(color = Color(0xFFEF4444), radius = 4f, center = Offset(cx, cy))
        }

        // Açı Metni
        Text(
            text = "$servoAngle°",
            color = Color(0xFFF1F5F9),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        val posLabel = when {
            servoAngle < 70 -> "SAĞ TARAMA ($servoAngle°)"
            servoAngle > 110 -> "SOL TARAMA ($servoAngle°)"
            else -> "MERKEZ (90°)"
        }
        val posColor = when {
            servoAngle < 70 -> Color(0xFF60A5FA)
            servoAngle > 110 -> Color(0xFFF472B6)
            else -> Color(0xFF34D399)
        }

        Text(
            text = posLabel,
            color = posColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

