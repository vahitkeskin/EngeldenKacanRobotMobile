package com.vahitkeskin.engeldenkacanrobotmobile.data

import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotState
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.domain.repository.ITelemetryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockTelemetryRepository : ITelemetryRepository {

    private val _telemetry = MutableStateFlow(RobotTelemetry.noSignal())
    override val telemetry: StateFlow<RobotTelemetry> = _telemetry.asStateFlow()

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    override val logs: StateFlow<List<String>> = _logs.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    override fun connect(target: String) {
        _isConnected.value = true
        addLog("[Donanım Bağlantısı] $target portuna bağlandı. Gerçek telemetri bekleniyor.")
    }

    fun onRealPacketReceived(front: Int, angle: Int, stateCode: Int, left: Int, right: Int) {
        if (!_isConnected.value) return
        _telemetry.value = RobotTelemetry(
            frontDistance = front,
            servoAngle = angle,
            state = RobotState.fromCode(stateCode),
            leftDistance = left,
            rightDistance = right,
            hasSignal = true
        )
    }

    private fun addLog(msg: String) {
        val current = _logs.value.toMutableList()
        if (current.size > 40) current.removeAt(0)
        current.add(msg)
        _logs.value = current
    }

    override fun disconnect() {
        _isConnected.value = false
        addLog("[Donanım Bağlantısı] Bağlantı kesildi.")
        _telemetry.value = RobotTelemetry.noSignal()
    }
}
