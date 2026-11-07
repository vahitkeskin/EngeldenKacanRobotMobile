package com.vahitkeskin.engeldenkacanrobotmobile.domain.model

data class RobotTelemetry(
    val frontDistance: Int = 400,
    val servoAngle: Int = 90,
    val state: RobotState = RobotState.DISCONNECTED,
    val leftDistance: Int = 400,
    val rightDistance: Int = 400,
    val timestamp: Long = 0L
) {
    val isDanger: Boolean get() = frontDistance <= 15
    val isWarning: Boolean get() = frontDistance in 16..30
    val isSafe: Boolean get() = frontDistance > 30
}
