package com.vahitkeskin.engeldenkacanrobotmobile

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return sayHello(platform.name)
    }
}