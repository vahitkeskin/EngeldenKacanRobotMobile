package com.vahitkeskin.engeldenkacanrobotmobile.theme

import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    SYSTEM, DARK, LIGHT
}

data class AppColors(
    val bgPrimary: Color,
    val bgSecondary: Color,
    val bgCard: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val radarBg: Color,
    val radarGrid: Color,
    val logBg: Color,
    val logText: Color
) {
    companion object {
        val Dark = AppColors(
            bgPrimary = EKRobotColor.Theme.Dark.BgPrimary,
            bgSecondary = EKRobotColor.Theme.Dark.BgSecondary,
            bgCard = EKRobotColor.Theme.Dark.BgCard,
            border = EKRobotColor.Theme.Dark.Border,
            textPrimary = EKRobotColor.Theme.Dark.TextPrimary,
            textSecondary = EKRobotColor.Theme.Dark.TextSecondary,
            textMuted = EKRobotColor.Theme.Dark.TextMuted,
            radarBg = EKRobotColor.Theme.Dark.BgPrimary,
            radarGrid = EKRobotColor.Theme.Dark.Border,
            logBg = EKRobotColor.Theme.Dark.LogBg,
            logText = EKRobotColor.Theme.Dark.LogText
        )

        val Light = AppColors(
            bgPrimary = EKRobotColor.Theme.Light.BgPrimary,
            bgSecondary = EKRobotColor.Theme.Light.BgSecondary,
            bgCard = EKRobotColor.Theme.Light.BgCard,
            border = EKRobotColor.Theme.Light.Border,
            textPrimary = EKRobotColor.Theme.Light.TextPrimary,
            textSecondary = EKRobotColor.Theme.Light.TextSecondary,
            textMuted = EKRobotColor.Theme.Light.TextMuted,
            radarBg = EKRobotColor.Theme.Light.RadarBg,
            radarGrid = EKRobotColor.Theme.Light.RadarGrid,
            logBg = EKRobotColor.Theme.Light.LogBg,
            logText = EKRobotColor.Theme.Light.LogText
        )
    }
}
