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
    val themeLabel: String get() = if (isTurkish()) "Tema:" else "Theme:"
    val langLabel: String get() = if (isTurkish()) "Dil:" else "Lang:"
    val optSystem: String get() = if (isTurkish()) "Sistem" else "System"
    val optDark: String get() = if (isTurkish()) "Koyu" else "Dark"
    val optLight: String get() = if (isTurkish()) "Açık" else "Light"
}
