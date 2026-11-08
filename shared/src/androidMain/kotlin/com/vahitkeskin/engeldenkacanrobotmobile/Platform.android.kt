package com.vahitkeskin.engeldenkacanrobotmobile

import android.app.Activity
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import java.net.Inet4Address
import java.net.NetworkInterface

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

object AndroidContextHolder {
    var context: Context? = null
}

@Composable
actual fun SetSystemBarsTheme(isDark: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window ?: return@SideEffect
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = window.insetsController ?: return@SideEffect
                val mask = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                if (isDark) {
                    controller.setSystemBarsAppearance(0, mask)
                } else {
                    controller.setSystemBarsAppearance(mask, mask)
                }
            } else {
                @Suppress("DEPRECATION")
                val decor = window.decorView
                @Suppress("DEPRECATION")
                decor.systemUiVisibility = if (isDark) {
                    decor.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                } else {
                    decor.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }
    }
}

actual fun getRealNetworkInfo(): RealNetworkInfo {
    val ctx = AndroidContextHolder.context
    var ip = "--"
    var mac = "--"
    var ifaceName = "--"
    var subnet = "--"
    var isConnected = false
    var ssid = "--"
    var signal = "--"

    try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        if (interfaces != null) {
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                if (iface.isLoopback || !iface.isUp) continue
                val addrs = iface.interfaceAddresses
                for (addr in addrs) {
                    val inetAddr = addr.address
                    if (!inetAddr.isLoopbackAddress && inetAddr is Inet4Address) {
                        ip = inetAddr.hostAddress ?: "--"
                        ifaceName = iface.name
                        isConnected = true
                        val prefix = addr.networkPrefixLength.toInt()
                        subnet = prefixToSubnet(prefix)
                        val hw = iface.hardwareAddress
                        if (hw != null && hw.isNotEmpty()) {
                            mac = hw.joinToString(":") { "%02x".format(it) }
                        }
                        if (iface.name.contains("wlan") || iface.name.contains("eth")) {
                            break
                        }
                    }
                }
                if (isConnected && ifaceName.contains("wlan")) break
            }
        }
    } catch (ignored: Exception) {}

    // WifiManager ile gerçek SSID ve RSSI (dBm) al
    if (ctx != null) {
        try {
            val wifiManager = ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            if (wifiManager != null) {
                val info = wifiManager.connectionInfo
                if (info != null && info.networkId != -1) {
                    val rawSsid = info.ssid
                    if (!rawSsid.isNullOrBlank() && rawSsid != "<unknown ssid>") {
                        ssid = rawSsid.removePrefix("\"").removeSuffix("\"")
                    }
                    val rssi = info.rssi
                    if (rssi != -127 && rssi < 0) {
                        signal = "$rssi dBm"
                    }
                    if (mac == "--") {
                        val hwMac = info.macAddress
                        if (!hwMac.isNullOrBlank() && hwMac != "02:00:00:00:00:00") {
                            mac = hwMac
                        }
                    }
                }
            }
        } catch (ignored: Exception) {}
    }

    if (ssid == "--" && isConnected) {
        ssid = "Wi-Fi ($ifaceName)"
    }

    return RealNetworkInfo(
        isConnected = isConnected,
        ipAddress = ip,
        networkName = ssid,
        interfaceName = ifaceName,
        subnetMask = subnet,
        macAddress = mac,
        signalLevel = signal
    )
}

private fun prefixToSubnet(prefix: Int): String {
    val netmask = if (prefix == 0) 0 else (-1 shl (32 - prefix))
    return "${(netmask shr 24) and 0xff}.${(netmask shr 16) and 0xff}.${(netmask shr 8) and 0xff}.${netmask and 0xff}"
}
