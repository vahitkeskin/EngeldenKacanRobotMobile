package com.vahitkeskin.engeldenkacanrobotmobile.domain.model

import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings

enum class WheelDirection(val symbol: String) {
    FORWARD("▲"),
    BACKWARD("▼"),
    STOPPED("■")
}

data class WheelState(
    val speedPwm: Int,
    val direction: WheelDirection
)


data class I2CRegisterEntry(
    val regHex: String,
    val name: String,
    val currentValue: String,
    val description: String
)

data class I2CBridgeInfo(
    val slaveAddress: String = "0x08",
    val busSpeedKhz: Int = 100,
    val sdaPin: String = "A4 (Pin 18)",
    val sclPin: String = "A5 (Pin 19)",
    val busProtocol: String = "Wire (I2C Slave)",
    val isBusHealthy: Boolean = true,
    // Wi-Fi & Ağ Bilgileri
    val isWifiConnected: Boolean = true,
    val wifiSsid: String = "RobotNet_2.4G",
    val ipAddress: String = "192.168.1.101",
    val port: Int = 8080,
    val gatewayIp: String = "192.168.1.1",
    val subnetMask: String = "255.255.255.0",
    val macAddress: String = "24:6F:28:B4:7E:12",
    val rssiDbm: Int = -58,
    // Protokol & Sağlık
    val packetRateHz: Int = 20,
    val packetLossPercent: Float = 0.0f,
    val latencyMs: Int = 12,
    val crcChecksumValid: Boolean = true
)

data class RobotTelemetry(
    val frontDistance: Int = -1,
    val servoAngle: Int = 90,
    val state: RobotState = RobotState.DISCONNECTED,
    val leftDistance: Int = -1,
    val rightDistance: Int = -1,
    val hasSignal: Boolean = false,
    val timestamp: Long = 0L,
    // Batarya & Şarj Telemetrisi (2S 18650 Li-Ion: 6.4V - 8.4V)
    val batteryVoltage: Float = 7.84f,
    val batteryPercentage: Int = 85,
    val isCharging: Boolean = false,
    // Manuel Müdahale & Kumanda Durumu
    val isManualMode: Boolean = false,
    val manualDriveCommand: String = "STOP", // FORWARD, BACKWARD, LEFT, RIGHT, STOP
    val targetPwmSpeed: Int = 180,
    // I2C & Ağ Bilgileri
    val i2cInfo: I2CBridgeInfo = I2CBridgeInfo()
) {
    val isDanger: Boolean get() = hasSignal && frontDistance in 1..15
    val isWarning: Boolean get() = hasSignal && frontDistance in 16..30
    val isSafe: Boolean get() = hasSignal && frontDistance > 30

    val isBatteryLow: Boolean get() = batteryPercentage <= 20
    val isBatteryCritical: Boolean get() = batteryPercentage <= 10

    val leftWheelState: WheelState get() {
        if (!hasSignal) return WheelState(0, WheelDirection.STOPPED)
        
        // Manuel mod aktifse doğrudan kullanıcının kumanda komutunu uygula
        if (state == RobotState.MANUAL_OVERRIDE || isManualMode) {
            return when (manualDriveCommand.uppercase()) {
                "FORWARD", "FWD" -> WheelState(targetPwmSpeed, WheelDirection.FORWARD)
                "BACKWARD", "REV" -> WheelState(targetPwmSpeed, WheelDirection.BACKWARD)
                "LEFT" -> WheelState(targetPwmSpeed, WheelDirection.BACKWARD)
                "RIGHT" -> WheelState(targetPwmSpeed, WheelDirection.FORWARD)
                else -> WheelState(0, WheelDirection.STOPPED)
            }
        }

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
            RobotState.MANUAL_OVERRIDE -> WheelState(0, WheelDirection.STOPPED)
            RobotState.STOPPED, RobotState.DISCONNECTED -> WheelState(0, WheelDirection.STOPPED)
        }
    }

    val rightWheelState: WheelState get() {
        if (!hasSignal) return WheelState(0, WheelDirection.STOPPED)

        // Manuel mod aktifse doğrudan kullanıcının kumanda komutunu uygula
        if (state == RobotState.MANUAL_OVERRIDE || isManualMode) {
            return when (manualDriveCommand.uppercase()) {
                "FORWARD", "FWD" -> WheelState(targetPwmSpeed, WheelDirection.FORWARD)
                "BACKWARD", "REV" -> WheelState(targetPwmSpeed, WheelDirection.BACKWARD)
                "LEFT" -> WheelState(targetPwmSpeed, WheelDirection.FORWARD)
                "RIGHT" -> WheelState(targetPwmSpeed, WheelDirection.BACKWARD)
                else -> WheelState(0, WheelDirection.STOPPED)
            }
        }

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
            RobotState.MANUAL_OVERRIDE -> WheelState(0, WheelDirection.STOPPED)
            RobotState.STOPPED, RobotState.DISCONNECTED -> WheelState(0, WheelDirection.STOPPED)
        }
    }


    val i2cRegisters: List<I2CRegisterEntry> get() = listOf(
        I2CRegisterEntry("0x01", "REG_FRONT_DIST", if (frontDistance > 0) "$frontDistance cm" else "--", Strings.regFrontDistDesc),
        I2CRegisterEntry("0x02", "REG_SERVO_ANGLE", "$servoAngle°", Strings.regServoAngleDesc),
        I2CRegisterEntry("0x03", "REG_ROBOT_STATE", "0x0${state.code} (${state.name})", if (Strings.isTurkish()) "Otonom / Manuel durum kodu" else "Autonomous / Manual state code"),
        I2CRegisterEntry("0x04", "REG_LEFT_DIST", if (leftDistance > 0) "$leftDistance cm" else "--", Strings.regLeftDistDesc),
        I2CRegisterEntry("0x05", "REG_RIGHT_DIST", if (rightDistance > 0) "$rightDistance cm" else "--", Strings.regRightDistDesc),
        I2CRegisterEntry("0x06", "REG_BATTERY_VOLT", "${(batteryVoltage * 1000).toInt()} mV (${batteryVoltage}V)", if (Strings.isTurkish()) "2S Li-Ion analog okunan voltaj" else "2S Li-Ion analog read voltage"),
        I2CRegisterEntry("0x07", "REG_BATTERY_PCT", "%$batteryPercentage", Strings.regBatterySocDesc),
        I2CRegisterEntry("0x08", "REG_CHARGING_STATE", if (isCharging) Strings.regChargingCharging else Strings.regChargingDischarging, Strings.regChargingDesc),
        I2CRegisterEntry("0x10", "REG_COMMAND", manualDriveCommand, Strings.regCommandDesc),
        I2CRegisterEntry("0xFF", "REG_FULL_TELEMETRY", "10B (CRC-8 OK)", Strings.regFullDesc)
    )

    companion object {
        fun noSignal(): RobotTelemetry = RobotTelemetry(hasSignal = false)
    }
}
