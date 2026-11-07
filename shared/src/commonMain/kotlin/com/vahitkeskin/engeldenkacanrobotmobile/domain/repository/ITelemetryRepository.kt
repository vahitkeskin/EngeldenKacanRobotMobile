package com.vahitkeskin.engeldenkacanrobotmobile.domain.repository

import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import kotlinx.coroutines.flow.StateFlow

interface ITelemetryRepository {
    val telemetry: StateFlow<RobotTelemetry>
    val logs: StateFlow<List<String>>
    val isConnected: StateFlow<Boolean>

    fun connect(target: String)
    fun disconnect()
}

