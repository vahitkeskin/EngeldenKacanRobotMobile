package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry

@Composable
fun MetricCards(
    telemetry: RobotTelemetry,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B))
            .padding(14.dp)
    ) {
        Text(
            text = "HC-SR04 ULTRASONİK MESAFE SENSÖRÜ",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val distColor = when {
                telemetry.isDanger -> Color(0xFFEF4444)
                telemetry.isWarning -> Color(0xFFF59E0B)
                else -> Color(0xFF34D399)
            }

            Text(
                text = "${telemetry.frontDistance} cm",
                color = distColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            val statusText = when {
                telemetry.isDanger -> "🔴 TEHLİKE"
                telemetry.isWarning -> "🟡 DİKKAT"
                else -> "🟢 GÜVENLİ"
            }
            Text(
                text = statusText,
                color = distColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val progress = (telemetry.frontDistance / 100f).coerceIn(0f, 1f)
        val progressColor = when {
            telemetry.isDanger -> Color(0xFFEF4444)
            telemetry.isWarning -> Color(0xFFF59E0B)
            else -> Color(0xFF10B981)
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = progressColor,
            trackColor = Color(0xFF0F172A),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Sol Koridor: ${telemetry.leftDistance} cm",
                color = Color(0xFFCBD5E1),
                fontSize = 11.sp
            )
            Text(
                text = "Sağ Koridor: ${telemetry.rightDistance} cm",
                color = Color(0xFFCBD5E1),
                fontSize = 11.sp
            )
        }
    }
}

