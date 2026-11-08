package com.vahitkeskin.engeldenkacanrobotmobile

import androidx.compose.runtime.Composable
import java.net.Inet4Address
import java.net.NetworkInterface

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

@Composable
actual fun SetSystemBarsTheme(isDark: Boolean) {
    // Masaüstü işletim sistemlerinde telefon statusbar'ı bulunmaz
}

actual fun getRealNetworkInfo(): RealNetworkInfo {
    var ip = "--"
    var mac = "--"
    var ifaceName = "--"
    var subnet = "--"
    var isConnected = false

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
                        val netmask = if (prefix == 0) 0 else (-1 shl (32 - prefix))
                        subnet = "${(netmask shr 24) and 0xff}.${(netmask shr 16) and 0xff}.${(netmask shr 8) and 0xff}.${netmask and 0xff}"
                        val hw = iface.hardwareAddress
                        if (hw != null && hw.isNotEmpty()) {
                            mac = hw.joinToString(":") { "%02x".format(it) }
                        }
                        if (iface.name.contains("en") || iface.name.contains("eth") || iface.name.contains("wlan")) {
                            break
                        }
                    }
                }
                if (isConnected) break
            }
        }
    } catch (ignored: Exception) {}

    return RealNetworkInfo(
        isConnected = isConnected,
        ipAddress = ip,
        networkName = if (isConnected) "Yerel Ağ ($ifaceName)" else "--",
        interfaceName = ifaceName,
        subnetMask = subnet,
        macAddress = mac,
        signalLevel = if (isConnected) "Ethernet / Wi-Fi" else "--"
    )
}
