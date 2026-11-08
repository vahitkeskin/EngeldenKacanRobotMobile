package com.vahitkeskin.engeldenkacanrobotmobile.theme

import androidx.compose.ui.graphics.Color

/**
 * 🎨 EKRobotColor
 * Uygulama genelinde kullanılan tüm özel (custom) renk kodlarının
 * anlamsal ve fonksiyonel gruplar halinde toplandığı merkezi renk kütüphanesi.
 *
 * Standart Android / Compose renkleri (Color.White, Color.Black, Color.Transparent)
 * bu kütüphanede tutulmaz; doğrudan Compose'un yerleşik renkleri kullanılır.
 */
object EKRobotColor {

    // ==========================================
    // 1. MARKA & BİRİNCİL VURGU RENKLERİ (BRAND)
    // ==========================================
    object Brand {
        val BluePrimary = Color(0xFF2563EB)     // Ana Vurgu Mavisi
        val BlueDark = Color(0xFF1D4ED8)        // Koyu Mavi
        val BlueDeep = Color(0xFF1E40AF)        // Derin Mavi
        val BlueLight = Color(0xFF3B82F6)       // Açık Mavi
        val BlueMuted = Color(0xFF60A5FA)       // Pastel Mavi
        val SkyAccent = Color(0xFF0EA5E9)       // Gökyüzü Mavisi
        val SkyLight = Color(0xFF38BDF8)        // Açık Gökyüzü Mavisi
        val SkyCyan = Color(0xFF0284C7)         // Koyu Camgöbeği
        val CyanAccent = Color(0xFF00E5FF)      // Canlı Camgöbeği
        val Indigo = Color(0xFF1565C0)          // Koyu İndigo
        val PurpleAccent = Color(0xFF8B5CF6)    // Mor Vurgu
        val Violet = Color(0xFFA855F7)          // Eflatun
    }

    // ==========================================
    // 2. DURUM & TELEMETRİ RENKLERİ (STATUS)
    // ==========================================
    object Status {
        val Success = Color(0xFF10B981)         // Yeşil (Aktif / Başarılı)
        val SuccessLight = Color(0xFF34D399)    // Açık Nane Yeşili
        val SuccessVibrant = Color(0xFF22C55E)  // Canlı Yeşil
        val SuccessDark = Color(0xFF059669)     // Koyu Yeşil
        val Danger = Color(0xFFEF4444)          // Kırmızı (Engel / Kritik / Hata)
        val DangerDark = Color(0xFFDC2626)      // Koyu Kırmızı
        val DangerLight = Color(0xFFFECACA)     // Açık Kırmızı / Pembe
        val DangerBgLight = Color(0xFFFEE2E2)   // Açık Mod Kırmızı Rozet Zemin
        val DangerTextLight = Color(0xFFB91C1C) // Açık Mod Kırmızı Rozet Metin
        val Warning = Color(0xFFF59E0B)         // Sarı / Kehribar (Uyarı)
        val WarningDark = Color(0xFFD97706)     // Koyu Kehribar
        val WarningYellow = Color(0xFFEAB308)   // Sarı
        val WarningBright = Color(0xFFFACC15)   // Parlak Sarı
        val WarningGold = Color(0xFFFDE047)     // Altın Sarısı
        val WarningBgLight = Color(0xFFFEF3C7)  // Açık Mod Kehribar Rozet Zemin
        val WarningTextLight = Color(0xFFB45309)// Açık Mod Kehribar Rozet Metin
        val SuccessBgLight = Color(0xFFECFDF5)  // Açık Mod Yeşil Rozet Zemin
        val SuccessTextLight = Color(0xFF047857)// Açık Mod Yeşil Rozet Metin
        val Orange = Color(0xFFF97316)          // Turuncu
        val Muted = Color(0xFF6B7280)           // Gri (Pasif / Beklemede)
        val MutedDark = Color(0xFF475569)       // Koyu Gri
    }

    // ==========================================
    // 3. TEMA RENKLERİ (DARK / LIGHT THEME)
    // ==========================================
    object Theme {
        object Dark {
            val BgPrimary = Color(0xFF0F172A)    // Koyu Ana Zemin (Slate 900)
            val BgSecondary = Color(0xFF1E293B)  // Koyu İkincil Zemin (Slate 800)
            val BgCard = Color(0xFF1E293B)       // Koyu Kart Zemin
            val Border = Color(0xFF334155)       // Koyu Çerçeve Çizgisi (Slate 700)
            val TextPrimary = Color(0xFFF8FAFC)  // Koyu Metin (Slate 50)
            val TextSecondary = Color(0xFFCBD5E1)// Koyu İkincil Metin (Slate 300)
            val TextMuted = Color(0xFF94A3B8)    // Koyu Kırık Metin (Slate 400)
            val LogBg = Color(0xFF030712)        // Terminal Siyahı
            val LogText = Color(0xFF6EE7B7)      // Canlı Terminal Nane Yeşili
            val SurfaceDark = Color(0xFF111827)  // Siyah Yüzey
            val GrayDark = Color(0xFF1F2937)     // Koyu Gri Kart
            val SlateDark = Color(0xFF374151)    // Orta Gri
        }

        object Light {
            val BgPrimary = Color(0xFFF1F5F9)    // Açık Ana Zemin (Slate 100)
            val BgSecondary = Color(0xFFFFFFFF)  // Açık İkincil Zemin (Beyaz)
            val BgCard = Color(0xFFFFFFFF)       // Açık Kart Zemin
            val Border = Color(0xFFCBD5E1)       // Açık Çerçeve Çizgisi (Slate 300)
            val TextPrimary = Color(0xFF0F172A)  // Açık Koyu Metin (Slate 900)
            val TextSecondary = Color(0xFF334155)// Açık İkincil Metin (Slate 700)
            val TextMuted = Color(0xFF64748B)    // Açık Kırık Metin (Slate 500)
            val RadarBg = Color(0xFFE2E8F0)      // Açık Radar Zemin
            val RadarGrid = Color(0xFF94A3B8)    // Açık Radar Izgarası
            val LogBg = Color(0xFFF8FAFC)        // Açık Log Zemin
            val LogText = Color(0xFF059669)      // Açık Log Yeşil Metin
        }
    }

    // ==========================================
    // 4. A3 YEŞİL KESME MATI RENKLERİ (CUTTING MAT)
    // ==========================================
    object CuttingMat {
        val MatGreenDark = Color(0xFF0F5132)    // Koyu Mod Mat Yeşili
        val MatGreenLight = Color(0xFF15803D)   // Açık Mod Mat Yeşili
        val MatRimDark = Color(0xFF0A3622)      // Koyu Mod Dış Kenar Yeşili
        val MatRimLight = Color(0xFF166534)     // Açık Mod Dış Kenar Yeşili
        val GridDarkMinor = Color(0xFF6EE7B7)   // Koyu Mod 1cm Kılavuz (Nane Yeşili)
        val GridDarkMajor = Color(0xFFD1FAE5)   // Koyu Mod 5cm Ana Izgara
        val GridLightMinor = Color(0xFF86EFAC)  // Açık Mod 1cm Kılavuz
        val GridLightMajor = Color(0xFFECFDF5)  // Açık Mod 5cm Ana Izgara
        val RulerWhite = Color(0xFFF0FDF4)      // Milimetrik Cetvel Beyazı
        val BorderPale = Color(0xFFE2E8F0)      // Mat Açık Çerçeve
        val ForestDeep = Color(0xFF14532D)      // Derin Orman Yeşili
    }

    // ==========================================
    // 5. ROBOT 3D MODELİ & MATERYAL RENKLERİ (ROBOT 3D)
    // ==========================================
    object Robot3D {
        // MDF Ahşap Şasi
        val MdfTop = Color(0xFFD4A373)          // MDF Üst Yüzey (Doğal Ahşap)
        val MdfBottom = Color(0xFFB08958)       // MDF Alt Yüzey (Koyu Ahşap)
        val MdfEdge = Color(0xFF8B5A2B)         // MDF Lazer Kesim Kenar Yanığı
        val MdfWoodDark = Color(0xFF6E4723)     // Koyu Ağaç Kenarı
        val MdfWoodDeep = Color(0xFF3E2723)     // En Koyu Ahşap
        val MdfTan = Color(0xFFC49A6C)          // Açık Ahşap
        val ScrewSilver = Color(0xFF475569)     // Montaj Vida Başı Çeliği

        // Arduino Uno R3
        val ArduinoPcb = Color(0xFF00878F)      // Arduino Teal Mavisi PCB
        val UsbSilver = Color(0xFFCBD5E1)       // USB & Kristal Metalik Gümüş
        val HeaderBlack = Color(0xFF0F172A)     // Dişi Başlık Soketi Plastik Siyah
        val ChipBlack = Color(0xFF1E293B)       // Atmega328P DIP Çip Siyahı
        val PinGold = Color(0xFFF59E0B)         // Altın / Pirinç Başlık Pinleri

        // TT Dişli Motorlar & Tekerlekler
        val MotorYellow = Color(0xFFFACC15)     // Sarı TT Motor Gövde Plastik
        val MotorSteel = Color(0xFFCBD5E1)      // DC Motor Çelik Gövde
        val TireRubber = Color(0xFF1E293B)      // Lastik Kauçuğu
        val WheelRim = Color(0xFFF59E0B)        // Sarı / Turuncu TT Jant

        // L298N Sürücü & SG90 Servo & HC-SR04
        val L298NPcb = Color(0xFFDC2626)        // L298N Kırmızı PCB
        val HeatSink = Color(0xFF0F172A)        // Alüminyum Soğutucu Blok
        val ServoBlue = Color(0xFF1D4ED8)       // SG90 Yarı Saydam Mavi Plastik
        val Hcsr04Pcb = Color(0xFF1E40AF)       // HC-SR04 Mavi PCB
        val SensorSilver = Color(0xFFCBD5E1)    // Ultrasonik Alıcı/Verici Çeliği

        // Batarya Paketi (2S 18650 Li-Ion)
        val BatteryGreen = Color(0xFF22C55E)    // 18650 Li-Ion Yeşil Kılıf
        val HolderBlack = Color(0xFF0F172A)     // ABS Plastik Yuva Siyahı

        // Kablo Demeti (Wire Harness)
        val WireRed = Color(0xFFEF4444)         // +VCC Kırmızı Hat
        val WireBlack = Color(0xFF0F172A)       // GND Siyah Hat
        val WireBlue = Color(0xFF3B82F6)        // Sinyal Mavi Hat
        val WireYellow = Color(0xFFF59E0B)      // Sinyal Sarı Hat
    }

    // ==========================================
    // 6. SAYDAMLIK VURGULARI (ALPHA TINTS)
    // ==========================================
    object Alpha {
        val GreenGlass = Color(0x3310B981)      // Yarı Saydam Yeşil Cam
        val GreenGlow = Color(0x6610B981)       // Yeşil Parıltı
        val BlueGlass = Color(0xEE1E40AF)       // Mavi Cam
        val AmberDarkBg = Color(0xCC78350F)     // Kehribar Zemin
        val AmberDeep = Color(0xFF78350F)       // Derin Kehribar
        val SlateGlassDark = Color(0xCC0F172A)  // Koyu Cam Panel
        val SlateGlassMedium = Color(0xCC1E293B)// Orta Cam Panel
        val SlateGlassSoft = Color(0xB31E293B)  // Hafif Cam Panel
        val SlateGlassLight = Color(0x990F172A) // Çok Hafif Cam Panel
        val SlateGlassSolid = Color(0xDD0F172A) // Neredeyse Katı Cam Panel
        val WhiteLine = Color(0x33FFFFFF)       // Beyaz Çizgi Vurgusu
        val NightDark = Color(0xFF060914)       // Gece Zemin
        val NightDeeper = Color(0xFF090D1C)     // Derin Gece
        val NightDeepest = Color(0xFF0B1120)    // En Derin Gece
    }
}

