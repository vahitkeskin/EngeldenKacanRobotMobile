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
    private var batteryJob: Job? = null

    override fun connect(target: String) {
        _isConnected.value = true
        addLog("[Donanım Bağlantısı] $target portuna bağlandı. Gerçek telemetri ve kumanda hattı açıldı.")
        startBatteryMonitoringLoop()
        startAutonomousRobotLoop()
    }

    private fun startBatteryMonitoringLoop() {
        batteryJob?.cancel()
        batteryJob = repositoryScope.launch {
            while (isActive && _isConnected.value) {
                delay(3000)
                val current = _telemetry.value
                if (current.isCharging) {
                    val nextPct = (current.batteryPercentage + 1).coerceAtMost(100)
                    val nextVolt = 6.40f + (nextPct / 100.0f) * 2.00f
                    _telemetry.value = current.copy(
                        batteryPercentage = nextPct,
                        batteryVoltage = (nextVolt * 100).toInt() / 100.0f
                    )
                    if (nextPct % 5 == 0) {
                        addLog("[Batarya Şarjı] %$nextPct doldu (${_telemetry.value.batteryVoltage} V / 8.40 V Li-Ion)")
                    }
                } else if (current.state != RobotState.STOPPED && current.state != RobotState.DISCONNECTED) {
                    // Motorlar çalışırken çok hafif tüketim simülasyonu
                    if ((1..10).random() == 1) {
                        val nextPct = (current.batteryPercentage - 1).coerceAtLeast(5)
                        val nextVolt = 6.40f + (nextPct / 100.0f) * 2.00f
                        _telemetry.value = current.copy(
                            batteryPercentage = nextPct,
                            batteryVoltage = (nextVolt * 100).toInt() / 100.0f
                        )
                    }
                }
            }
        }
    }

    private fun startAutonomousRobotLoop() {
        simulationJob?.cancel()
        simulationJob = repositoryScope.launch {
            var distance = 65
            while (isActive && _isConnected.value) {
                // Manuel mod aktifse otonom sürüş döngüsü kullanıcı komutunu bekler
                if (_telemetry.value.isManualMode) {
                    delay(150)
                    continue
                }

                // 1. DÜZ SÜRÜŞ (CRUISING)
                while (distance > 15 && isActive && _isConnected.value && !_telemetry.value.isManualMode) {
                    _telemetry.value = _telemetry.value.copy(
                        frontDistance = distance,
                        servoAngle = 90,
                        state = RobotState.CRUISING,
                        leftDistance = -1,
                        rightDistance = -1,
                        hasSignal = true,
                        manualDriveCommand = "FORWARD"
                    )
                    addLog("[Telemetri] İleri Sürüş - Mesafe: $distance cm | Servo: 90° | Motorlar: İLERİ (PWM: 180)")
                    delay(180)
                    distance -= 4
                }

                if (!isActive || !_isConnected.value || _telemetry.value.isManualMode) continue

                // 2. ENGEL TESPİT EDİLDİ - ROBOT DURDU (AVOIDING AŞAMASI)
                addLog("[ENGEL TESPİTİ] Mesafe: $distance cm <= 15 cm. Robot durduruldu, tarama başlıyor.")
                _telemetry.value = _telemetry.value.copy(
                    frontDistance = distance,
                    servoAngle = 90,
                    state = RobotState.AVOIDING,
                    leftDistance = -1,
                    rightDistance = -1,
                    hasSignal = true,
                    manualDriveCommand = "STOP"
                )
                delay(300)

                if (_telemetry.value.isManualMode) continue

                // 3. SERVO SOLA DÖNÜYOR (145°)
                addLog("[Servo Tarama] Sola dönülüyor (145°)...")
                for (a in 90..145 step 15) {
                    if (_telemetry.value.isManualMode) break
                    _telemetry.value = _telemetry.value.copy(servoAngle = a)
                    delay(50)
                }
                val measuredLeft = 46
                _telemetry.value = _telemetry.value.copy(leftDistance = measuredLeft)
                addLog("[Sonar Ölçüm] Sol Mesafe: $measuredLeft cm")
                delay(350)

                if (_telemetry.value.isManualMode) continue

                // 4. SERVO SAĞA DÖNÜYOR (35°)
                addLog("[Servo Tarama] Sağa dönülüyor (35°)...")
                for (a in 145 downTo 35 step 15) {
                    if (_telemetry.value.isManualMode) break
                    _telemetry.value = _telemetry.value.copy(servoAngle = a)
                    delay(45)
                }
                val measuredRight = 18
                _telemetry.value = _telemetry.value.copy(rightDistance = measuredRight)
                addLog("[Sonar Ölçüm] Sağ Mesafe: $measuredRight cm")
                delay(350)

                if (_telemetry.value.isManualMode) continue

                // 5. SERVO MERKEZE DÖNÜYOR (90°)
                for (a in 35..90 step 15) {
                    if (_telemetry.value.isManualMode) break
                    _telemetry.value = _telemetry.value.copy(servoAngle = a)
                    delay(40)
                }

                if (_telemetry.value.isManualMode) continue

                // 6. KARAR VE DÖNÜŞ MANEVRASI (Sol > Sağ -> Sola Dönüş)
                addLog("[Manevra] Sol ($measuredLeft cm) > Sağ ($measuredRight cm). Sola kaçış manevrası yapılıyor.")
                _telemetry.value = _telemetry.value.copy(
                    frontDistance = 14,
                    servoAngle = 90,
                    state = RobotState.AVOIDING,
                    leftDistance = measuredLeft,
                    rightDistance = measuredRight,
                    hasSignal = true,
                    manualDriveCommand = "LEFT"
                )
                delay(650) // Sol teker geri, sağ teker ileri dönerek dönüş yapar

                // Yeni önü açık rota
                distance = 68
            }
        }
    }

    // ========================================================
    // 🎮 ROBOTA DOĞRUDAN MÜDAHALE METOTLARI (DIRECT OVERRIDE)
    // ========================================================

    override fun sendDriveCommand(command: String, pwmSpeed: Int) {
        val current = _telemetry.value
        val normalizedCmd = command.uppercase().trim()
        val newState = if (normalizedCmd == "STOP") RobotState.STOPPED else RobotState.MANUAL_OVERRIDE
        
        _telemetry.value = current.copy(
            isManualMode = true,
            state = newState,
            manualDriveCommand = normalizedCmd,
            targetPwmSpeed = pwmSpeed,
            hasSignal = true
        )
        addLog("[Manuel Kumanda] Komut: $normalizedCmd | Hız: $pwmSpeed PWM (Motor Sürücüsü Tetiklendi)")
    }

    override fun setServoAngle(angle: Int) {
        val clamped = angle.coerceIn(0, 180)
        _telemetry.value = _telemetry.value.copy(
            servoAngle = clamped,
            hasSignal = true
        )
        addLog("[Manuel Servo] Açı ayarlandı: $clamped°")
    }

    override fun emergencyStop() {
        val current = _telemetry.value
        _telemetry.value = current.copy(
            isManualMode = true,
            state = RobotState.STOPPED,
            manualDriveCommand = "STOP",
            hasSignal = true
        )
        addLog("[🛑 ACİL FREN] ROBOT KESİN OLARAK DURDURULDU! Tüm PWM çıkışları 0'a çekildi.")
    }

    override fun startAutonomous() {
        _telemetry.value = _telemetry.value.copy(
            isManualMode = false,
            state = RobotState.CRUISING,
            manualDriveCommand = "FORWARD",
            hasSignal = true
        )
        addLog("[Mod Değişimi] 🤖 Tam Otonom Engelden Kaçış Modu Başlatıldı.")
        startAutonomousRobotLoop()
    }

    override fun setManualMode(enabled: Boolean) {
        if (enabled) {
            _telemetry.value = _telemetry.value.copy(
                isManualMode = true,
                state = RobotState.MANUAL_OVERRIDE,
                manualDriveCommand = "STOP"
            )
            addLog("[Mod Değişimi] 🎮 Manuel Müdahale Modu Aktif. Kontrol Kullanıcıda.")
        } else {
            startAutonomous()
        }
    }

    override fun triggerHorn() {
        addLog("[Donanım] 📢 KORNA ÇALINDI! (Buzzer 800Hz / 200ms tetiklendi)")
    }

    override fun toggleChargingSimulation() {
        val current = _telemetry.value
        val newChargingState = !current.isCharging
        _telemetry.value = current.copy(isCharging = newChargingState)
        if (newChargingState) {
            addLog("[Güç Yönetimi] ⚡ Şarj kablosu takıldı. Batarya doluyor (+%1/3s)...")
        } else {
            addLog("[Güç Yönetimi] 🔋 Şarj kablosu çıkarıldı. Batarya kullanımda (deşarj)...")
        }
    }

    fun onRealPacketReceived(front: Int, angle: Int, stateCode: Int, left: Int, right: Int, vbat: Float = 7.84f, batPct: Int = 85, isChg: Boolean = false) {
        if (!_isConnected.value) return
        simulationJob?.cancel() // Gerçek donanım paketi gelirse simülasyonu susturur
        _telemetry.value = _telemetry.value.copy(
            frontDistance = front,
            servoAngle = angle,
            state = RobotState.fromCode(stateCode),
            leftDistance = left,
            rightDistance = right,
            batteryVoltage = vbat,
            batteryPercentage = batPct,
            isCharging = isChg,
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
        batteryJob?.cancel()
        simulationJob = null
        batteryJob = null
        _isConnected.value = false
        addLog("[Donanım Bağlantısı] Bağlantı kesildi.")
        _telemetry.value = RobotTelemetry.noSignal()
    }
}
