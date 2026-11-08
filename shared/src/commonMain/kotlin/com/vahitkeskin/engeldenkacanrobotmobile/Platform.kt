package com.vahitkeskin.engeldenkacanrobotmobile

import androidx.compose.runtime.Composable

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

@Composable
expect fun SetSystemBarsTheme(isDark: Boolean)

data class RealNetworkInfo(
    val isConnected: Boolean = false,
    val ipAddress: String = "--",
    val networkName: String = "--",
    val interfaceName: String = "--",
    val subnetMask: String = "--",
    val macAddress: String = "--",
    val signalLevel: String = "--"
)

expect fun getRealNetworkInfo(): RealNetworkInfo
