package com.vahitkeskin.engeldenkacanrobotmobile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform