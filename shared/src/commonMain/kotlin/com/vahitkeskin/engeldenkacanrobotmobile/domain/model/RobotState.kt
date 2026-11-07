package com.vahitkeskin.engeldenkacanrobotmobile.domain.model

import androidx.compose.ui.graphics.Color

enum class RobotState(val displayName: String, val badgeColor: Color, val code: Int) {
    CRUISING("SEYİR MODU (CRUISING)", Color(0xFF10B981), 0),
    AVOIDING("ENGEL KAÇIŞI (AVOIDING)", Color(0xFFEF4444), 1),
    STOPPED("DURDURULDU (STOPPED)", Color(0xFFF59E0B), 2),
    DISCONNECTED("BAĞLANTI YOK", Color(0xFF6B7280), -1);

    companion object {
        fun fromCode(code: Int): RobotState {
            return entries.firstOrNull { it.code == code } ?: CRUISING
        }
    }
}

