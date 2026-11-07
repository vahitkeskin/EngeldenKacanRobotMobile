package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.WheelDirection
import kotlinx.coroutines.isActive
import kotlin.math.sqrt

/**
 * 🎮 Robot3DViewport
 * 3D Robot Dijital İkizini tam ekranda (full-screen) 60-120 FPS sıfır takılmayla çizen,
 * hem tek parmakla 360° serbest eksen dönüşünü (Orbit), hem iki parmakla gezinme (Pan) ve
 * yakınlaştırmayı (Pinch Zoom) aynı anda kesintisiz destekleyen, MacBook trackpad ve
 * çift dokunmayla sıfırlama özelliklerine sahip Compose Canvas bileşeni.
 *
 * Jest Yönetimi:
 * - 👆 Tek Parmak: Tam eksende serbest 360° Yaw ve Pitch dönüşü.
 * - ✌️ İki Parmak: Ekranda serbest gezinme (Pan X/Y) ve iki parmağı açıp kapatarak yakınlaştırma (Pinch Zoom).
 * - 💻 MacBook Trackpad / Fare: İki parmakla veya tekerlekle zoom ve yatay kaydırma.
 * - 🎯 Çift Dokunma: Kamerayı pürüzsüz animasyonla ilk başlangıç açısına sıfırlar.
 */
@Composable
fun Robot3DViewport(
    telemetry: RobotTelemetry,
    camera: Camera3D,
    is3dActive: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onDoubleTapReset: () -> Unit = { camera.reset() },
    onUserInteraction: () -> Unit = {}
) {
    // Bağımsız Tekerlek ve Sonar Fazları (Gerçek Arduino PWM Hız & Yön Senkronizasyonu)
    var leftWheelAngle by remember { mutableFloatStateOf(0f) }
    var rightWheelAngle by remember { mutableFloatStateOf(0f) }
    var sonarWavePhase by remember { mutableFloatStateOf(0f) }

    val leftMoving = telemetry.leftWheelState.direction != WheelDirection.STOPPED
    val rightMoving = telemetry.rightWheelState.direction != WheelDirection.STOPPED
    val sonarActive = telemetry.hasSignal && telemetry.frontDistance > 0
    val isAnimating = leftMoving || rightMoving || sonarActive

    val gestureModifier = if (is3dActive) {
        Modifier.pointerInput(is3dActive) {
            awaitPointerEventScope {
                var prevCentroid = Offset.Zero
                var prevSpan = 0f
                var prevCount = 0
                var lastTapTime = 0L
                var lastTapPos = Offset.Zero

                while (true) {
                    val event = awaitPointerEvent()

                    // 1. MacBook Trackpad ve Fare Tekerleği ile Zoom ve Yatay Kaydırma
                    if (event.type == PointerEventType.Scroll) {
                        val scrollChange = event.changes.firstOrNull()
                        if (scrollChange != null) {
                            val scrollY = scrollChange.scrollDelta.y
                            val scrollX = scrollChange.scrollDelta.x
                            if (scrollY != 0f) {
                                camera.zoom = (camera.zoom - scrollY * 0.08f).coerceIn(0.45f, 5.0f)
                            }
                            if (scrollX != 0f) {
                                camera.panX -= scrollX * 10f
                            }
                        }
                        continue
                    }

                    // 2. Dokunmatik Parmaklar (Multi-touch: 1 parmak = 360° Orbit, 2 parmak = Pan & Pinch Zoom)
                    val pressed = event.changes.filter { it.pressed }
                    val count = pressed.size

                    if (count == 0) {
                        prevCount = 0
                        prevCentroid = Offset.Zero
                        prevSpan = 0f
                        continue
                    }

                    onUserInteraction()

                    // Çift Dokunma Tespiti (Double Tap -> Kamerayı Sıfırla)
                    if (count == 1 && event.changes.any { !it.previousPressed && it.pressed }) {
                        val downChange = event.changes.first { !it.previousPressed && it.pressed }
                        val now = downChange.uptimeMillis
                        val pos = downChange.position
                        val dist = (pos - lastTapPos).getDistance()
                        if (now - lastTapTime < 350L && dist < 60f) {
                            onDoubleTapReset()
                            lastTapTime = 0L
                        } else {
                            lastTapTime = now
                            lastTapPos = pos
                        }
                    }

                    if (count == 1) {
                        // 👆 TEK PARMAK MODU: Tam 360° Eksen Etrafında Serbest Döndürme (Orbit Yaw & Pitch)
                        val change = pressed[0]
                        if (prevCount == 1) {
                            val delta = change.position - change.previousPosition
                            camera.yaw = (camera.yaw + delta.x * 0.45f)
                            camera.pitch = (camera.pitch - delta.y * 0.45f).coerceIn(-88f, 88f)
                        }
                        change.consume()
                        prevCount = 1
                    } else if (count >= 2) {
                        // ✌️ İKİ PARMAK MODU: Gezinme (Pan X/Y) ve Yakınlaştırma (Pinch Zoom)
                        var sumX = 0f
                        var sumY = 0f
                        for (p in pressed) {
                            sumX += p.position.x
                            sumY += p.position.y
                        }
                        val centroid = Offset(sumX / count, sumY / count)

                        var totalDist = 0f
                        for (p in pressed) {
                            val dx = p.position.x - centroid.x
                            val dy = p.position.y - centroid.y
                            totalDist += sqrt(dx * dx + dy * dy)
                        }
                        val span = totalDist / count

                        if (prevCount >= 2) {
                            // A. İki parmakla ekranda gezinme / taşıma (Pan)
                            val panDelta = centroid - prevCentroid
                            camera.panX += panDelta.x * 0.85f
                            camera.panY += panDelta.y * 0.85f

                            // B. İki parmakla yakınlaştırma / uzaklaştırma (Pinch Zoom)
                            if (prevSpan > 10f && span > 10f) {
                                val zoomFactor = span / prevSpan
                                camera.zoom = (camera.zoom * zoomFactor).coerceIn(0.45f, 5.0f)
                            }
                        }

                        prevCentroid = centroid
                        prevSpan = span
                        prevCount = count

                        for (p in pressed) {
                            p.consume()
                        }
                    }
                }
            }
        }
    } else {
        Modifier
    }

    // 1. CANLI MOTOR & SONAR ANİMASYON DÖNGÜSÜ (Hareket varken VSYNC ile senkron 60-120 FPS)
    if (isAnimating) {
        LaunchedEffect(isAnimating, telemetry) {
            var lastTime = 0L
            while (isActive) {
                withFrameMillis { frameTime ->
                    if (lastTime != 0L) {
                        val dt = ((frameTime - lastTime) / 1000f).coerceIn(0.001f, 0.05f)

                        if (leftMoving) {
                            val dir = if (telemetry.leftWheelState.direction == WheelDirection.FORWARD) 1f else -1f
                            val speedRatio = (telemetry.leftWheelState.speedPwm / 255f).coerceIn(0.25f, 1.0f)
                            leftWheelAngle = (leftWheelAngle + dir * speedRatio * 320f * dt) % 360f
                        }

                        if (rightMoving) {
                            val dir = if (telemetry.rightWheelState.direction == WheelDirection.FORWARD) 1f else -1f
                            val speedRatio = (telemetry.rightWheelState.speedPwm / 255f).coerceIn(0.25f, 1.0f)
                            rightWheelAngle = (rightWheelAngle + dir * speedRatio * 320f * dt) % 360f
                        }

                        if (sonarActive) {
                            sonarWavePhase = (sonarWavePhase + dt * 0.9f) % 1f
                        }
                    }
                    lastTime = frameTime
                }
            }
        }
    }

    // 🎨 2D/3D ÇİZİM MOTORU (Senkron, sıfır takılmalı ve katmanlı derinlik sıralamalı)
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .then(gestureModifier)
    ) {
        val width = size.width
        val height = size.height
        if (width <= 10f || height <= 10f) return@Canvas

        // 1. 3D Model Geometrisini İnşa Et
        val polygons = Robot3DModelBuilder.buildRobotModel(
            telemetry = telemetry,
            leftWheelPhase = leftWheelAngle,
            rightWheelPhase = rightWheelAngle,
            sonarWavePhase = sonarWavePhase,
            isDark = isDark,
            cameraPitch = camera.pitch
        )

        // 2. Katmanlı Derinlik Sıralaması (Layered Depth Sorting) ile 2D Ekrana İzdüşür
        val projectedPolygons = camera.project(
            polygons = polygons,
            screenWidth = width,
            screenHeight = height
        )

        // 3. Poligonları Çiz
        val path = Path()
        for (poly in projectedPolygons) {
            val pts = poly.points
            if (pts.size < 3) continue

            path.reset()
            path.moveTo(pts[0].x, pts[0].y)
            for (i in 1 until pts.size) {
                path.lineTo(pts[i].x, pts[i].y)
            }
            path.close()

            // Poligon Dolgusu (Saydamlık / Alpha destekler)
            drawPath(path = path, color = poly.color)

            // Varsa Kenar Çizgisi
            if (poly.strokeColor != null && poly.strokeWidth > 0f) {
                drawPath(
                    path = path,
                    color = poly.strokeColor,
                    style = Stroke(width = poly.strokeWidth)
                )
            }
        }
    }
}
