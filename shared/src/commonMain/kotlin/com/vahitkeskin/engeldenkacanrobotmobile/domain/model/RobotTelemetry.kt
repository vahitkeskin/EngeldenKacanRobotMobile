package com.vahitkeskin.engeldenkacanrobotmobile.domain.model

enum class WheelDirection(val symbol: String) {
    FORWARD("▲"),
    BACKWARD("▼"),
    STOPPED("■")
}

data class WheelState(
    val speedPwm: Int,
    val direction: WheelDirection
)

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

    val leftWheelState: WheelState get() {
        if (!hasSignal) return WheelState(0, WheelDirection.STOPPED)
        return when (state) {
            RobotState.CRUISING -> WheelState(180, WheelDirection.FORWARD)
            RobotState.AVOIDING -> {
                if (servoAngle > 90) {
                    if (leftDistance >= rightDistance && rightDistance > 0) WheelState(160, WheelDirection.BACKWARD)
                    else WheelState(0, WheelDirection.STOPPED)
                } else if (servoAngle < 90) {
                    WheelState(180, WheelDirection.FORWARD)
                } else {
                    WheelState(0, WheelDirection.STOPPED)
                }
            }
            RobotState.STOPPED, RobotState.DISCONNECTED -> WheelState(0, WheelDirection.STOPPED)
        }
    }

    val rightWheelState: WheelState get() {
        if (!hasSignal) return WheelState(0, WheelDirection.STOPPED)
        return when (state) {
            RobotState.CRUISING -> WheelState(180, WheelDirection.FORWARD)
            RobotState.AVOIDING -> {
                if (servoAngle < 90) {
                    if (rightDistance > leftDistance && leftDistance > 0) WheelState(160, WheelDirection.BACKWARD)
                    else WheelState(0, WheelDirection.STOPPED)
                } else if (servoAngle > 90) {
                    WheelState(180, WheelDirection.FORWARD)
                } else {
                    WheelState(0, WheelDirection.STOPPED)
                }
            }
            RobotState.STOPPED, RobotState.DISCONNECTED -> WheelState(0, WheelDirection.STOPPED)
        }
    }

    companion object {
        fun noSignal(): RobotTelemetry = RobotTelemetry()
    }
}
