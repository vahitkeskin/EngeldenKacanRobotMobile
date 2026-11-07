package com.vahitkeskin.engeldenkacanrobotmobile

import androidx.compose.runtime.Composable

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

@Composable
actual fun SetSystemBarsTheme(isDark: Boolean) {
    // Masaüstü işletim sistemlerinde telefon statusbar'ı bulunmaz
}
