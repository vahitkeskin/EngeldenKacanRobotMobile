package com.vahitkeskin.engeldenkacanrobotmobile

import androidx.compose.runtime.Composable

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

@Composable
expect fun SetSystemBarsTheme(isDark: Boolean)
