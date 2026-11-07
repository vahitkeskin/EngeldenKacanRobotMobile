package com.vahitkeskin.engeldenkacanrobotmobile.i18n

enum class LanguageMode {
    SYSTEM, TR, EN
}

object Strings {
    var currentLanguage: LanguageMode = LanguageMode.SYSTEM

    fun isTurkish(): Boolean {
        return when (currentLanguage) {
            LanguageMode.SYSTEM -> true
            LanguageMode.TR -> true
            LanguageMode.EN -> false
        }
    }

    val appTitle: String get() = if (isTurkish()) "Engelden Kaçan Robot" else "Obstacle Avoiding Robot"
    val i2cBus: String get() = if (isTurkish()) "I2C Veri Yolu (0x08)" else "I2C Bus (0x08)"
    val connect: String get() = if (isTurkish()) "Donanıma Bağlan" else "Connect Hardware"
    val disconnect: String get() = if (isTurkish()) "Bağlantıyı Kes" else "Disconnect"
    val radarTitle: String get() = if (isTurkish()) "ULTRASONİK RADAR TARAMASI" else "ULTRASONIC RADAR SWEEP"
    val servoTitle: String get() = if (isTurkish()) "SERVO DÖNME AÇISI" else "SERVO ROTATION ANGLE"
    val sensorTitle: String get() = if (isTurkish()) "HC-SR04 ULTRASONİK MESAFE SENSÖRÜ" else "HC-SR04 ULTRASONIC DISTANCE SENSOR"
    val center: String get() = if (isTurkish()) "MERKEZ" else "CENTER"
    val rightScan: String get() = if (isTurkish()) "SAĞ TARAMA" else "RIGHT SCAN"
    val leftScan: String get() = if (isTurkish()) "SOL TARAMA" else "LEFT SCAN"
    val noSignal: String get() = if (isTurkish()) "Sinyal Yok" else "No Signal"
    val safeCorridor: String get() = if (isTurkish()) "🟢 GÜVENLİ" else "🟢 SAFE"
    val warningCorridor: String get() = if (isTurkish()) "🟡 DİKKAT" else "🟡 WARNING"
    val dangerCorridor: String get() = if (isTurkish()) "🔴 TEHLİKE" else "🔴 DANGER"
    val leftCorridor: (Int) -> String get() = { d -> if (isTurkish()) "Sol Koridor: ${if (d > 0) "$d cm" else "--"}" else "Left Corridor: ${if (d > 0) "$d cm" else "--"}" }
    val rightCorridor: (Int) -> String get() = { d -> if (isTurkish()) "Sağ Koridor: ${if (d > 0) "$d cm" else "--"}" else "Right Corridor: ${if (d > 0) "$d cm" else "--"}" }
    val logTitle: String get() = if (isTurkish()) "I2C & TELEMETRİ GÜNLÜĞÜ" else "I2C & TELEMETRY LOG"
    val themeLabel: String get() = if (isTurkish()) "Tema" else "Theme"
    val langLabel: String get() = if (isTurkish()) "Dil" else "Language"
    val optSystem: String get() = if (isTurkish()) "Sistem" else "System"
    val optDark: String get() = if (isTurkish()) "Koyu" else "Dark"
    val optLight: String get() = if (isTurkish()) "Açık" else "Light"
    val optLangAuto: String get() = if (isTurkish()) "Sistem" else "System"
    val optLangTr: String get() = "Türkçe"
    val optLangEn: String get() = "English"

    // Bottom Navigation Bar
    val navTelemetry: String get() = if (isTurkish()) "Telemetri" else "Telemetry"
    val navController: String get() = if (isTurkish()) "Kumanda" else "Remote"
    val navRobotTwin: String get() = if (isTurkish()) "Robot 3D" else "Robot 3D"

    // Robot İkiz Ekranı
    val robotTwinTitle: String get() = if (isTurkish()) "ROBOT 3D DİJİTAL İKİZ" else "ROBOT 3D DIGITAL TWIN"
    val leftWheel: String get() = if (isTurkish()) "Sol Teker" else "Left Wheel"
    val rightWheel: String get() = if (isTurkish()) "Sağ Teker" else "Right Wheel"
    val servoMotor: String get() = if (isTurkish()) "SG90 Servo Motor" else "SG90 Servo Motor"
    val ultrasonicSensor: String get() = if (isTurkish()) "HC-SR04 Sensör" else "HC-SR04 Sensor"
    val wheelFwd: String get() = if (isTurkish()) "▲ İLERİ" else "▲ FORWARD"
    val wheelRev: String get() = if (isTurkish()) "▼ GERİ" else "▼ BACKWARD"
    val wheelStop: String get() = if (isTurkish()) "■ DURUYOR" else "■ STOPPED"
    val batteryPack: String get() = if (isTurkish()) "2x 18650 Batarya" else "2x 18650 Battery"
    val casterWheel: String get() = if (isTurkish()) "Sarhoş Tekerlek" else "Caster Wheel"
    val motorDriverL298N: String get() = if (isTurkish()) "L298N Sürücü" else "L298N Driver"
    val arduinoShield: String get() = if (isTurkish()) "Arduino Uno" else "Arduino Uno"
    val liveBeam: String get() = if (isTurkish()) "Aktif Radar Işını" else "Active Sonar Beam"

    // Robota Doğrudan Müdahale & Batarya
    val directInterventionTitle: String get() = if (isTurkish()) "ROBOTA DOĞRUDAN MÜDAHALE" else "DIRECT ROBOT OVERRIDE"
    val batteryTitle: String get() = if (isTurkish()) "BATARYA & GÜÇ YÖNETİMİ" else "BATTERY & POWER MANAGEMENT"
    val batteryCharging: String get() = if (isTurkish()) "⚡ Şarj Oluyor" else "⚡ Charging"
    val batteryDischarging: String get() = if (isTurkish()) "🔋 Kullanımda (Deşarj)" else "🔋 In Use (Discharge)"
    val batteryCritical: String get() = if (isTurkish()) "🪫 Kritik Pil!" else "🪫 Low Battery!"
    val batterySimToggle: String get() = if (isTurkish()) "Şarjı Simüle Et" else "Simulate Charge"
    val modeAutonomous: String get() = if (isTurkish()) "🤖 OTONOM" else "🤖 AUTO"
    val modeManual: String get() = if (isTurkish()) "🎮 MANUEL" else "🎮 MANUAL"
    val btnEmergencyStop: String get() = if (isTurkish()) "🛑 ACİL FREN" else "🛑 E-BRAKE"
    val btnStart: String get() = if (isTurkish()) "▶ BAŞLAT" else "▶ START"
    val btnStop: String get() = if (isTurkish()) "⏹ DURDUR" else "⏹ STOP"
    val btnHorn: String get() = if (isTurkish()) "📢 KORNA" else "📢 HORN"
    val btnForward: String get() = if (isTurkish()) "İLERİ" else "FORWARD"
    val btnBackward: String get() = if (isTurkish()) "GERİ" else "BACKWARD"
    val btnLeft: String get() = if (isTurkish()) "SOL" else "LEFT"
    val btnRight: String get() = if (isTurkish()) "SAĞ" else "RIGHT"
    val speedLabel: String get() = if (isTurkish()) "Motor Hızı (PWM)" else "Motor Speed (PWM)"
    val servoManualAim: String get() = if (isTurkish()) "Servo Manuel Açı" else "Manual Servo Angle"
    val cockpitTitle: String get() = if (isTurkish()) "SANAL KOKPİT & KUMANDA" else "VIRTUAL COCKPIT & CONTROLLER"
    val cockpitSubtitle: String get() = if (isTurkish()) "Gerçek Zamanlı Donanım Müdahalesi" else "Real-Time Hardware Override"
}
