package com.vahitkeskin.engeldenkacanrobotmobile.data

import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotState
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.domain.repository.ITelemetryRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sin
import kotlin.random.Random

class MockTelemetryRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) : ITelemetryRepository {

    private val _telemetry = MutableStateFlow(RobotTelemetry(frontDistance = 65, servoAngle = 90, state = RobotState.CRUISING))
    override val telemetry: StateFlow<RobotTelemetry> = _telemetry.asStateFlow()

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    override val logs: StateFlow<List<String>> = _logs.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private var job: Job? = null
    private var servoAngle = 90
    private var servoDirection = 2
    private var distance = 65
    private var state = RobotState.CRUISING
    private var timerTicks: Long = 0L

    override fun connect(target: String) {
        _isConnected.value = true
        addLog("[I2C Köprüsü] $target adresine I2C/Bluetooth telemetri bağlantısı kuruldu.")

        job?.cancel()
        job = scope.launch {
            while (isActive && _isConnected.value) {
                delay(50)
                timerTicks += 50
                tick()
            }
        }
    }

    private fun tick() {
        if (state == RobotState.AVOIDING) {
            servoAngle += servoDirection * 4
            if (servoAngle >= 160) {
                servoAngle = 160
                servoDirection = -1
            } else if (servoAngle <= 20) {
                servoAngle = 20
                servoDirection = 1
            }
        } else {
            servoAngle = 90 + (sin(timerTicks / 300.0) * 5).toInt()
        }

        if (state == RobotState.CRUISING) {
            distance -= 1
            if (distance <= 25) {
                state = RobotState.AVOIDING
                addLog("[I2C 0x08] REG_FRONT_DISTANCE=$distance cm -> ENGEL ALGILANDI!")
            }
        } else if (state == RobotState.AVOIDING) {
            distance += 2
            if (distance >= 55) {
                state = RobotState.CRUISING
                addLog("[I2C 0x08] Yol açıldı, Seyir moduna (CRUISING) dönüldü.")
            }
        }

        val leftDist = 40 + Random.nextInt(15)
        val rightDist = 20 + Random.nextInt(10)

        _telemetry.value = RobotTelemetry(
            frontDistance = distance,
            servoAngle = servoAngle,
            state = state,
            leftDistance = leftDist,
            rightDistance = rightDist,
            timestamp = timerTicks
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
        job?.cancel()
        addLog("[I2C Köprüsü] Bağlantı kapatıldı.")
        _telemetry.value = RobotTelemetry(frontDistance = 400, servoAngle = 90, state = RobotState.DISCONNECTED)
    }
}
