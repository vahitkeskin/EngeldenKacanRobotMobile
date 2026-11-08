package com.vahitkeskin.engeldenkacanrobotmobile.domain.model

import androidx.compose.ui.graphics.Color
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.theme.EKRobotColor

enum class RobotState(val displayNameKey: String, val badgeColor: Color, val code: Int) {
    CRUISING("CRUISING", EKRobotColor.Status.Success, 0),
    AVOIDING("AVOIDING", EKRobotColor.Status.Danger, 1),
    STOPPED("STOPPED", EKRobotColor.Status.Warning, 2),
    MANUAL_OVERRIDE("MANUAL_OVERRIDE", EKRobotColor.Brand.BlueLight, 3),
    DISCONNECTED("DISCONNECTED", EKRobotColor.Status.Muted, -1);

    val displayName: String
        get() = when (this) {
            CRUISING -> Strings.stateCruising
            AVOIDING -> Strings.stateAvoiding
            STOPPED -> Strings.stateStopped
            MANUAL_OVERRIDE -> Strings.stateManualOverride
            DISCONNECTED -> Strings.stateDisconnected
        }

    companion object {
        fun fromCode(code: Int): RobotState {
            return entries.firstOrNull { it.code == code } ?: CRUISING
        }
    }
}
