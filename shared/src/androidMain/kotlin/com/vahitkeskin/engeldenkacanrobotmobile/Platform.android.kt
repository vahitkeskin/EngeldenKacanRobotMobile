package com.vahitkeskin.engeldenkacanrobotmobile

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

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
                    // Koyu mod: Saat, wifi ve şarj ikonları BEYAZ
                    controller.setSystemBarsAppearance(0, mask)
                } else {
                    // Açık mod: Saat, wifi ve şarj ikonları SİYAH
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
