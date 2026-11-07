package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun RadarView(
    servoAngle: Int,
    frontDistance: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .padding(12.dp)
    ) {
        Text(
            text = "ULTRASONİK RADAR TARAMASI",
            color = Color(0xFFF1F5F9),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            val cx = size.width / 2
            val cy = size.height - 15f
            val maxRadius = min(cx - 20f, cy - 15f)

            // Konsantrik Mesafe Yayları (10, 20, 30, 40, 50 cm)
            val rings = 5
            for (i in 1..rings) {
                val r = (maxRadius * i) / rings
                drawArc(
                    color = Color(0xFF334155),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(cx - r, cy - r),
                    size = Size(r * 2, r * 2),
                    style = Stroke(width = 1.5f)
                )
            }

            // Açı Çizgileri (0°, 30°, 60°, 90°, 120°, 150°, 180°)
            for (a in 0..180 step 30) {
                val rad = a * (PI / 180.0)
                val ex = cx + (maxRadius * cos(rad)).toFloat()
                val ey = cy - (maxRadius * sin(rad)).toFloat()
                drawLine(
                    color = Color(0xFF334155),
                    start = Offset(cx, cy),
                    end = Offset(ex, ey),
                    strokeWidth = 1.2f
                )
            }

            // Servo Açısı Işını (Beam)
            val beamRad = servoAngle * (PI / 180.0)
            val bx = cx + (maxRadius * cos(beamRad)).toFloat()
            val by = cy - (maxRadius * sin(beamRad)).toFloat()

            // Parlama efekti
            drawLine(
                color = Color(0x3310B981),
                start = Offset(cx, cy),
                end = Offset(bx, by),
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )
            // Ana ışın
            drawLine(
                color = Color(0xFF34D399),
                start = Offset(cx, cy),
                end = Offset(bx, by),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )

            // Engel Blip Noktası
            if (frontDistance in 3..50) {
                val rNorm = (frontDistance / 50.0f) * maxRadius
                val obsX = cx + (rNorm * cos(beamRad)).toFloat()
                val obsY = cy - (rNorm * sin(beamRad)).toFloat()

                drawCircle(
                    color = Color(0xFFEF4444),
                    radius = 5f,
                    center = Offset(obsX, obsY)
                )
                drawCircle(
                    color = Color(0xFFFECACA),
                    radius = 2.5f,
                    center = Offset(obsX, obsY)
                )
            }

            // Merkez Pivotu
            drawCircle(color = Color(0xFF10B981), radius = 6f, center = Offset(cx, cy))
            drawCircle(color = Color.White, radius = 2.5f, center = Offset(cx, cy))
        }
    }
}

