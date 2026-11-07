package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors

@Composable
fun MetricCards(
    telemetry: RobotTelemetry,
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bgCard)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text(
            text = Strings.sensorTitle,
            color = colors.textMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (telemetry.hasSignal) {
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
                    telemetry.isDanger -> Strings.dangerCorridor
                    telemetry.isWarning -> Strings.warningCorridor
                    else -> Strings.safeCorridor
                }
                Text(
                    text = statusText,
                    color = distColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "-- cm",
                    color = colors.textMuted,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[${Strings.noSignal.uppercase()}]",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val progress = if (telemetry.hasSignal) (telemetry.frontDistance / 100f).coerceIn(0f, 1f) else 0f
        val progressColor = when {
            telemetry.isDanger -> Color(0xFFEF4444)
            telemetry.isWarning -> Color(0xFFF59E0B)
            else -> Color(0xFF10B981)
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = progressColor,
            trackColor = colors.bgPrimary,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = Strings.leftCorridor(telemetry.leftDistance),
                color = colors.textSecondary,
                fontSize = 11.sp
            )
            Text(
                text = Strings.rightCorridor(telemetry.rightDistance),
                color = colors.textSecondary,
                fontSize = 11.sp
            )
        }
    }
}
