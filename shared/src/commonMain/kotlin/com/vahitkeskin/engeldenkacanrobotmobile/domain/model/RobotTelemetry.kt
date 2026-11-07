package com.vahitkeskin.engeldenkacanrobotmobile.domain.model

data class RobotTelemetry(
    val frontDistance: Int = -1,
    val servoAngle: Int = 90,
    val state: RobotState = RobotState.DISCONNECTED,
    val leftDistance: Int = -1,
    val rightDistance: Int = -1,
    val hasSignal: Boolean = false,
    val timestamp: Long = 0L
) {
    val isDanger: Boolean get() = hasSignal && frontDistance in 1..15
    val isWarning: Boolean get() = hasSignal && frontDistance in 16..30
    val isSafe: Boolean get() = hasSignal && frontDistance > 30

    companion object {
        fun noSignal(): RobotTelemetry = RobotTelemetry()
    }
}
