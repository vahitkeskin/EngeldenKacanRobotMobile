package com.vahitkeskin.engeldenkacanrobotmobile.domain.repository

import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import kotlinx.coroutines.flow.StateFlow

interface ITelemetryRepository {
    val telemetry: StateFlow<RobotTelemetry>
    val logs: StateFlow<List<String>>
    val isConnected: StateFlow<Boolean>

    fun connect(target: String)
    fun disconnect()

    // 🎮 Robota Doğrudan Müdahale Metotları (Direct Hardware Override)
    fun sendDriveCommand(command: String, pwmSpeed: Int = 180)
    fun setServoAngle(angle: Int)
    fun emergencyStop()
    fun startAutonomous()
    fun setManualMode(enabled: Boolean)
    fun triggerHorn()

    // ⚡ Batarya & Şarj Simülasyonu / Durum Yönetimi
    fun toggleChargingSimulation()
}
