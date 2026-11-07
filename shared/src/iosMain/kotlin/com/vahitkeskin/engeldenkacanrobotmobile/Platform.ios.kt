package com.vahitkeskin.engeldenkacanrobotmobile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@Composable
actual fun SetSystemBarsTheme(isDark: Boolean) {
    SideEffect {
        val style = if (isDark) UIStatusBarStyleLightContent else UIStatusBarStyleDarkContent
        UIApplication.sharedApplication.setStatusBarStyle(style, animated = true)
    }
}
