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
            bgPrimary = Color(0xFF0F172A),
            bgSecondary = Color(0xFF1E293B),
            bgCard = Color(0xFF1E293B),
            border = Color(0xFF334155),
            textPrimary = Color(0xFFF8FAFC),
            textSecondary = Color(0xFFCBD5E1),
            textMuted = Color(0xFF94A3B8),
            radarBg = Color(0xFF0F172A),
            radarGrid = Color(0xFF334155),
            logBg = Color(0xFF030712),
            logText = Color(0xFF6EE7B7)
        )

        val Light = AppColors(
            bgPrimary = Color(0xFFF1F5F9),
            bgSecondary = Color(0xFFFFFFFF),
            bgCard = Color(0xFFFFFFFF),
            border = Color(0xFFCBD5E1),
            textPrimary = Color(0xFF0F172A),
            textSecondary = Color(0xFF334155),
            textMuted = Color(0xFF64748B),
            radarBg = Color(0xFFE2E8F0),
            radarGrid = Color(0xFF94A3B8),
            logBg = Color(0xFFF8FAFC),
            logText = Color(0xFF059669)
        )
    }
}
