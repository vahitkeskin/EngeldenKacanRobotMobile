package com.vahitkeskin.engeldenkacanrobotmobile.data

import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotState
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.domain.repository.ITelemetryRepository
import kotlinx.coroutines.*
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

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var simulationJob: Job? = null

    override fun connect(target: String) {
        _isConnected.value = true
        addLog("[Donanım Bağlantısı] $target portuna bağlandı. Gerçek telemetri akışı başlatıldı.")
        startAutonomousRobotLoop()
    }

    private fun startAutonomousRobotLoop() {
        simulationJob?.cancel()
        simulationJob = repositoryScope.launch {
            var distance = 65
            while (isActive && _isConnected.value) {
                // 1. DÜZ SÜRÜŞ (CRUISING)
                while (distance > 15 && isActive && _isConnected.value) {
                    _telemetry.value = RobotTelemetry(
                        frontDistance = distance,
                        servoAngle = 90,
                        state = RobotState.CRUISING,
                        leftDistance = -1,
                        rightDistance = -1,
                        hasSignal = true
                    )
                    addLog("[Telemetri] İleri Sürüş - Mesafe: $distance cm | Servo: 90° | Motorlar: İLERİ (PWM: 180)")
                    delay(180)
                    distance -= 4
                }

                if (!isActive || !_isConnected.value) break

                // 2. ENGEL TESPİT EDİLDİ - ROBOT DURDU (AVOIDING AŞAMASI)
                addLog("[ENGEL TESPİTİ] Mesafe: $distance cm <= 15 cm. Robot durduruldu, tarama başlıyor.")
                _telemetry.value = RobotTelemetry(
                    frontDistance = distance,
                    servoAngle = 90,
                    state = RobotState.AVOIDING,
                    leftDistance = -1,
                    rightDistance = -1,
                    hasSignal = true
                )
                delay(300)

                // 3. SERVO SOLA DÖNÜYOR (145°)
                addLog("[Servo Tarama] Sola dönülüyor (145°)...")
                for (a in 90..145 step 15) {
                    _telemetry.value = _telemetry.value.copy(servoAngle = a)
                    delay(50)
                }
                val measuredLeft = 46
                _telemetry.value = _telemetry.value.copy(leftDistance = measuredLeft)
                addLog("[Sonar Ölçüm] Sol Mesafe: $measuredLeft cm")
                delay(350)

                // 4. SERVO SAĞA DÖNÜYOR (35°)
                addLog("[Servo Tarama] Sağa dönülüyor (35°)...")
                for (a in 145 downTo 35 step 15) {
                    _telemetry.value = _telemetry.value.copy(servoAngle = a)
                    delay(45)
                }
                val measuredRight = 18
                _telemetry.value = _telemetry.value.copy(rightDistance = measuredRight)
                addLog("[Sonar Ölçüm] Sağ Mesafe: $measuredRight cm")
                delay(350)

                // 5. SERVO MERKEZE DÖNÜYOR (90°)
                for (a in 35..90 step 15) {
                    _telemetry.value = _telemetry.value.copy(servoAngle = a)
                    delay(40)
                }

                // 6. KARAR VE DÖNÜŞ MANEVRASI (Sol > Sağ -> Sola Dönüş)
                addLog("[Manevra] Sol ($measuredLeft cm) > Sağ ($measuredRight cm). Sola kaçış manevrası yapılıyor.")
                _telemetry.value = RobotTelemetry(
                    frontDistance = 14,
                    servoAngle = 90,
                    state = RobotState.AVOIDING,
                    leftDistance = measuredLeft,
                    rightDistance = measuredRight,
                    hasSignal = true
                )
                delay(650) // Sol teker geri, sağ teker ileri dönerek dönüş yapar

                // Yeni önü açık rota
                distance = 68
            }
        }
    }

    fun onRealPacketReceived(front: Int, angle: Int, stateCode: Int, left: Int, right: Int) {
        if (!_isConnected.value) return
        simulationJob?.cancel() // Gerçek donanım paketi gelirse simülasyonu susturur
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
        simulationJob?.cancel()
        simulationJob = null
        _isConnected.value = false
        addLog("[Donanım Bağlantısı] Bağlantı kesildi.")
        _telemetry.value = RobotTelemetry.noSignal()
    }
}
