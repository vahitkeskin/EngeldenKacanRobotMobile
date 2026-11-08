package com.vahitkeskin.engeldenkacanrobotmobile

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "EK Robot",
        icon = painterResource("icons/icon.png"),
    ) {
        App()
    }
}
