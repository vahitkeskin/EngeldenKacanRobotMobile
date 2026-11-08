package com.vahitkeskin.engeldenkacanrobotmobile.i18n

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class LanguageMode {
    SYSTEM, TR, EN
}

object Strings {
    var currentLanguage: LanguageMode by mutableStateOf(LanguageMode.SYSTEM)

    fun isTurkish(): Boolean {
        return when (currentLanguage) {
            LanguageMode.SYSTEM -> true
            LanguageMode.TR -> true
            LanguageMode.EN -> false
        }
    }

    val appTitle: String get() = "EK Robot"
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
    val navSettings: String get() = if (isTurkish()) "Ayarlar" else "Settings"

    // Ayarlar Ekranı (Settings Screen)
    val settingsTitle: String get() = if (isTurkish()) "AYARLAR" else "SETTINGS"
    val settingsSubtitle: String get() = if (isTurkish()) "Tema, Dil & Sistem Tercihleri" else "Theme, Language & System Preferences"
    val sectionAppearance: String get() = if (isTurkish()) "GÖRÜNÜM & TEMA" else "APPEARANCE & THEME"
    val appearanceDesc: String get() = if (isTurkish()) "Arayüz rengini cihazınıza veya kişisel zevkinize göre ayarlayın" else "Customize interface colors to match your device or preference"
    val sectionLanguage: String get() = if (isTurkish()) "DİL SEÇİMİ" else "LANGUAGE PREFERENCE"
    val languageDesc: String get() = if (isTurkish()) "Tüm etiket ve telemetri metinleri anında seçilen dile geçer" else "All labels and telemetry texts instantly update to chosen language"
    val sectionHardwareInfo: String get() = if (isTurkish()) "DONANIM & VERİYOLU" else "HARDWARE & BUS"
    val sectionAppInfo: String get() = if (isTurkish()) "UYGULAMA HAKKINDA" else "ABOUT APPLICATION"
    val optSystemDesc: String get() = if (isTurkish()) "Cihazınızın sistem ayarlarıyla otomatik senkronize olur" else "Automatically synchronizes with your device settings"
    val optDarkDesc: String get() = if (isTurkish()) "Göz yormayan, OLED dostu derin lacivert/siyah renkler" else "Eye-friendly, OLED-optimized deep navy/black tones"
    val optLightDesc: String get() = if (isTurkish()) "Güneş altında yüksek okunabilirlik sunan aydınlık arayüz" else "Bright high-contrast interface for daylight reading"
    val optTrDesc: String get() = "Türkçe (Varsayılan)"
    val optEnDesc: String get() = "English (International)"
    val optLangAutoDesc: String get() = if (isTurkish()) "Cihazın sistem diline göre belirlenir" else "Determined by device system language"

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
    val statusLiveStream: String get() = if (isTurkish()) "CANLI AKIŞ" else "LIVE STREAM"
    val statusLive: String get() = if (isTurkish()) "CANLI" else "LIVE"
    val statusStandby: String get() = if (isTurkish()) "BEKLEMEDE" else "STANDBY"
    val simChargePlug: String get() = if (isTurkish()) "⚡ Şarj Cihazını Tak" else "⚡ Plug Charger"
    val simChargeUnplug: String get() = if (isTurkish()) "🔌 Şarjdan Çıkar" else "🔌 Unplug Charger"
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
    val gearEco: String get() = if (isTurkish()) "🌱 Eko (120)" else "🌱 Eco (120)"
    val gearStandard: String get() = if (isTurkish()) "🚗 Standart (180)" else "🚗 Standard (180)"
    val gearTurbo: String get() = if (isTurkish()) "🚀 Turbo (255)" else "🚀 Turbo (255)"
    val servoManualAim: String get() = if (isTurkish()) "Servo Manuel Açı" else "Manual Servo Angle"
    val cockpitTitle: String get() = if (isTurkish()) "SANAL KOKPİT & KUMANDA" else "VIRTUAL COCKPIT & CONTROLLER"
    val cockpitSubtitle: String get() = if (isTurkish()) "Gerçek Zamanlı Donanım Müdahalesi" else "Real-Time Hardware Override"
    val viewRobotTwinBtn: String get() = if (isTurkish()) "🤖 Robot 3D Görüntüle" else "🤖 View Robot 3D"
    val waitingTelemetryStream: String get() = if (isTurkish()) "> Telemetri akışı bekleniyor..." else "> Waiting for telemetry stream..."

    // I2C & Wi-Fi Köprü Bilgileri
    val i2cCardTitle: String get() = if (isTurkish()) "I2C VERİYOLU & AĞ BİLGİLERİ" else "I2C BUS & NETWORK INFO"
    val i2cBusSubtitle: String get() = if (isTurkish()) "Bağlı I2C Slave & Wi-Fi Donanım Detayları" else "Connected I2C Slave & Wi-Fi Hardware Details"
    val i2cSectionHardware: String get() = if (isTurkish()) "I2C VERİYOLU PARAMETRELERİ" else "I2C BUS PARAMETERS"
    val i2cSectionNetwork: String get() = if (isTurkish()) "WIFI & AĞ KÖPRÜSÜ BİLGİLERİ" else "WIFI & NETWORK BRIDGE INFO"
    val i2cSectionRegisters: String get() = if (isTurkish()) "I2C KAYIT (REGISTER) HARİTASI" else "I2C REGISTER MAP"
    val i2cSectionStats: String get() = if (isTurkish()) "HABERLEŞME & SAĞLIK İSTATİSTİKLERİ" else "COMMUNICATION & HEALTH STATS"
    val i2cSlaveAddr: String get() = if (isTurkish()) "I2C Adresi" else "I2C Address"
    val i2cBusSpeed: String get() = if (isTurkish()) "Veriyolu Hızı" else "Bus Speed"
    val i2cPins: String get() = if (isTurkish()) "SDA / SCL Pinleri" else "SDA / SCL Pins"
    val i2cStatus: String get() = if (isTurkish()) "Hat Durumu" else "Line Status"
    val i2cHealthy: String get() = if (isTurkish()) "Aktif / Sağlıklı (ACK OK)" else "Active / Healthy (ACK OK)"
    val i2cOscillator: String get() = if (isTurkish()) "16 MHz Osilatör" else "16 MHz Oscillator"
    val i2cActiveLine: String get() = if (isTurkish()) "● Aktif Veri Hattı" else "● Active Data Line"
    val i2cWaitingLine: String get() = if (isTurkish()) "○ Bağlantı Bekleniyor" else "○ Waiting for Connection"
    val i2cNetworkInterface: String get() = if (isTurkish()) "Ağ Arabirimi (Interface)" else "Network Interface"
    val i2cHardwareIntegration: String get() = if (isTurkish()) "DONANIM ENTEGRASYON BİLGİSİ" else "HARDWARE INTEGRATION INFO"
    val i2cBatteryChemistry: String get() = if (isTurkish()) "Batarya Kimyası" else "Battery Chemistry"
    val i2cBaudRate: String get() = if (isTurkish()) "Seri / I2C Baud Hızı" else "Serial / I2C Baud Rate"
    val i2cUartBridge: String get() = if (isTurkish()) "8-N-1 UART Köprüsü" else "8-N-1 UART Bridge"
    val i2cScrollToTop: String get() = if (isTurkish()) "▲ Ekranın Başına Hizala" else "▲ Scroll to Top"
    val wifiSsidLabel: String get() = if (isTurkish()) "Wi-Fi Ağı (SSID)" else "Wi-Fi SSID"
    val ipAddressLabel: String get() = if (isTurkish()) "IP Adresi" else "IP Address"
    val portLabel: String get() = if (isTurkish()) "Port" else "Port"
    val gatewayLabel: String get() = if (isTurkish()) "Ağ Geçidi (Gateway)" else "Gateway"
    val subnetLabel: String get() = if (isTurkish()) "Alt Ağ Maskesi" else "Subnet Mask"
    val macLabel: String get() = if (isTurkish()) "MAC Adresi" else "MAC Address"
    val rssiLabel: String get() = if (isTurkish()) "Sinyal (RSSI)" else "Signal (RSSI)"
    val packetRateLabel: String get() = if (isTurkish()) "Paket Frekansı" else "Packet Rate"
    val packetLossLabel: String get() = if (isTurkish()) "Paket Kaybı" else "Packet Loss"
    val latencyLabel: String get() = if (isTurkish()) "Gecikme (Ping)" else "Latency (Ping)"
    val crcIntegrityLabel: String get() = if (isTurkish()) "CRC-8 Doğrulama" else "CRC-8 Integrity"
    val crcValid: String get() = if (isTurkish()) "Geçerli (Eşleşti)" else "Valid (Matched)"
    val tapToExpand: String get() = if (isTurkish()) "Detayları Göster" else "Show Details"
    val tapToCollapse: String get() = if (isTurkish()) "Detayları Gizle" else "Hide Details"

    // I2C Register Açıklamaları
    val regFrontDistDesc: String get() = if (isTurkish()) "Ön ultrasonik engel mesafesi" else "Front ultrasonic obstacle distance"
    val regServoAngleDesc: String get() = if (isTurkish()) "SG90 radar tarama açısı (0-180°)" else "SG90 radar sweep angle (0-180°)"
    val regRightDistDesc: String get() = if (isTurkish()) "Sağ koridor tarama mesafesi" else "Right corridor scan distance"
    val regLeftDistDesc: String get() = if (isTurkish()) "Sol koridor tarama mesafesi" else "Left corridor scan distance"
    val regBatterySocDesc: String get() = if (isTurkish()) "Hesaplanan Şarj Durumu (% SOC)" else "State of Charge (% SOC)"
    val regChargingCharging: String get() = if (isTurkish()) "0x01 (Şarjda)" else "0x01 (Charging)"
    val regChargingDischarging: String get() = if (isTurkish()) "0x00 (Kullanımda)" else "0x00 (In Use)"
    val regChargingDesc: String get() = if (isTurkish()) "Şarj cihazı algılama bayrağı" else "Charger detection flag"
    val regCommandDesc: String get() = if (isTurkish()) "Aktif sürüş yön komutu" else "Active driving direction command"
    val regFullDesc: String get() = if (isTurkish()) "Tüm telemetri çerçeve dökümü" else "Full telemetry frame dump"

    // Ayarlar Ekranı Donanım Bilgileri
    val hwMcu: String get() = if (isTurkish()) "Mikrodenetleyici" else "Microcontroller"
    val hwMcuDesc: String get() = "Arduino Uno R3 (ATmega328P @ 16 MHz)"
    val hwI2cBus: String get() = if (isTurkish()) "I2C Veriyolu" else "I2C Bus"
    val hwI2cDesc: String get() = if (isTurkish()) "Slave Adresi 0x08 • Standart Hız 100 kHz" else "Slave Address 0x08 • Standard Speed 100 kHz"
    val hwMotorDriver: String get() = if (isTurkish()) "Motor Sürücü" else "Motor Driver"
    val hwMotorDesc: String get() = if (isTurkish()) "L298N Çift H-Köprüsü (2x TT DC Motor)" else "L298N Dual H-Bridge (2x TT DC Motors)"
    val hwSonar: String get() = if (isTurkish()) "Ultrasonik Sonar" else "Ultrasonic Sonar"
    val hwSonarDesc: String get() = if (isTurkish()) "HC-SR04 (2-400 cm Hassasiyet)" else "HC-SR04 (2-400 cm Precision)"
    val hwServo: String get() = if (isTurkish()) "Servo Motor" else "Servo Motor"
    val hwServoDesc: String get() = if (isTurkish()) "TowerPro SG90 9g Mikro Servo (0° - 180°)" else "TowerPro SG90 9g Micro Servo (0° - 180°)"
    val hwPowerSupply: String get() = if (isTurkish()) "Güç Kaynağı" else "Power Supply"
    val hwPowerDesc: String get() = "2x 18650 Li-Ion (7.4V Nominal / 8.4V Peak)"
    val hwAppName: String get() = if (isTurkish()) "Uygulama Adı" else "Application Name"
    val hwAppVersion: String get() = if (isTurkish()) "Sürüm" else "Version"
    val hwArch: String get() = if (isTurkish()) "Mimari" else "Architecture"
    val hwArchDesc: String get() = "Kotlin Multiplatform & Jetpack Compose"
    val hwPlatforms: String get() = if (isTurkish()) "Platformlar" else "Platforms"
    val hwPlatformsDesc: String get() = "Android • iOS • Desktop (Mac/Win/Linux)"
    val hwDeveloper: String get() = if (isTurkish()) "Geliştirici" else "Developer"

    // 3D Ekranı Kontrolleri & Perspektifler & Jest İpuçları
    val hintTourActive: String get() = if (isTurkish()) "🎬 Sinematik Sonsuz Tur Aktif • Robot her açıdan sergileniyor (Durdurmak için ekrana dokunun)" else "🎬 Infinite 3D Tour Active • Robot showcased from all angles (Tap to stop)"
    val hint3dActive: String get() = if (isTurkish()) "🎮 3D Aktif: 👆 Tek Parmak: 360° Döndür • ✌️ İki Parmak: Gezin & Zoom • Çift Dokun: Sıfırla" else "🎮 3D Active: 👆 One Finger: 360° Orbit • ✌️ Two Fingers: Pan & Zoom • Double Tap: Reset"
    val hint3dPassive: String get() = if (isTurkish()) "👆 Sayfa Kaydırma Modu: Ekranlar arası geçiş serbest (3D kontrol için '3D Aktif'e dokunun)" else "👆 Page Swipe Mode: Free screen navigation (Tap '3D Locked' to enable 3D)"

    // Robot FSM Durumları
    val stateCruising: String get() = if (isTurkish()) "OTONOM İLERLEME" else "CRUISING"
    val stateAvoiding: String get() = if (isTurkish()) "ENGELDEN KAÇMA" else "AVOIDING OBSTACLE"
    val stateStopped: String get() = if (isTurkish()) "DURDURULDU" else "STOPPED"
    val stateManualOverride: String get() = if (isTurkish()) "MANUEL KONTROL" else "MANUAL OVERRIDE"
    val stateDisconnected: String get() = if (isTurkish()) "BAĞLANTI YOK" else "DISCONNECTED"

    // 3D Ekranı Kontrolleri & Perspektifler
    val mode3dActive: String get() = if (isTurkish()) "3D Aktif" else "3D Active"
    val mode3dPassive: String get() = if (isTurkish()) "3D Pasif" else "3D Locked"
    val mode3dActiveDesc: String get() = if (isTurkish()) "Döndür / Yaklaş" else "Rotate / Zoom"
    val mode3dPassiveDesc: String get() = if (isTurkish()) "Sayfa Kaydır" else "Swipe Pages"
    val tourPlayInfinite: String get() = if (isTurkish()) "Sonsuz 3D Tur" else "Infinite 3D Tour"
    val tourStop: String get() = if (isTurkish()) "Turu Durdur" else "Stop Tour"
    val viewStandard: String get() = if (isTurkish()) "Standart" else "Standard"
    val viewTop: String get() = if (isTurkish()) "Kuşbakışı" else "Top-Down"
    val viewBottom: String get() = if (isTurkish()) "Alttan" else "Bottom"
    val viewFront: String get() = if (isTurkish()) "Ön" else "Front"
    val viewRear: String get() = if (isTurkish()) "Arka" else "Rear"
    val viewRight: String get() = if (isTurkish()) "Sağ" else "Right"
    val viewLeft: String get() = if (isTurkish()) "Sol" else "Left"

    // Splash Screen
    val splashTitle: String get() = "EK ROBOT"
    val splashSubtitle: String get() = if (isTurkish()) "OTONOM ENGEL TESPİT & TELEMETRİ SİSTEMİ" else "AUTONOMOUS OBSTACLE DETECTION & TELEMETRY"
    val splashStep1: String get() = if (isTurkish()) "I2C Veri Yolu (0x08) Başlatılıyor..." else "Initializing I2C Bus (0x08)..."
    val splashStep2: String get() = if (isTurkish()) "HC-SR04 Ultrasonik Sonar Testi..." else "Testing HC-SR04 Ultrasonic Sonar..."
    val splashStep3: String get() = if (isTurkish()) "SG90 Mikro Servo Sıfırlanıyor (90°)..." else "Centering SG90 Micro Servo (90°)..."
    val splashStep4: String get() = if (isTurkish()) "L298N Çift Motor Sürücü Hazır..." else "L298N Dual Motor Driver Ready..."
    val splashStep5: String get() = if (isTurkish()) "Telemetri & Dijital İkiz Hazır!" else "Telemetry & Digital Twin Ready!"
    val splashHardwareBadge: String get() = "ARDUINO UNO R3 • ATMEGA328P • I2C: 0x08"
    val splashVersion: String get() = "v1.0.0 Production"
    val splashSkipHint: String get() = if (isTurkish()) "Devam etmek için ekrana dokunun" else "Tap screen to continue"
    val splashDevelopedBy: String get() = if (isTurkish()) "vahitkeskin tarafından geliştirildi" else "Developed by vahitkeskin"
}
