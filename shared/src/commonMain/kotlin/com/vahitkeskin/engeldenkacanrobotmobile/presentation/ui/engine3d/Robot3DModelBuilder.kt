package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d

import com.vahitkeskin.engeldenkacanrobotmobile.domain.model.RobotTelemetry
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.component.*

/**
 * 🛠️ Robot3DModelBuilder
 * Kullanıcının fiziksel robotunun tüm parçalarını
 * (MDF ahşap şasi, tekerlekler, SG90 servo motor, HC-SR04 ultrasonik sensör,
 * L298N, Arduino Uno R3, 18650 piller, TT motorlar) component paketindeki
 * bağımsız 3D modüllerinden birleştirerek kusursuz 3D dijital ikizini inşa eder.
 */
object Robot3DModelBuilder {

    typealias CylinderAxis = com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.CylinderAxis

    fun buildRobotModel(
        telemetry: RobotTelemetry,
        leftWheelPhase: Float,
        rightWheelPhase: Float,
        sonarWavePhase: Float,
        isDark: Boolean = true,
        cameraPitch: Float = 32f
    ): List<Polygon3D> {
        val polygons = ArrayList<Polygon3D>(950)

        // 1. Zemin Kesim Matı (Layer 0 - Arka plan, robot parçalarını asla örtmez)
        CuttingMat3D.build(
            polygons = polygons,
            isDark = isDark,
            cameraPitch = cameraPitch
        )

        // 2. MDF Ahşap Şasi (Layer 1 - Katı Robot Modeli)
        MdfChassis3D.build(polygons = polygons)

        // 3. Alt Gövde: Sarı TT Motor Gövdeleri, Çelik DC Motorlar, Terminal Tabları, Kablolar (Layer 1)
        UndersideMotors3D.build(polygons = polygons)

        // 4. Arka Sarhoş Bilye Tekerlek (Layer 1)
        CasterWheel3D.build(polygons = polygons)

        // 5. Sol ve Sağ 32-Segment Yuvarlak Lastikler ve Sarı TT Jantlar (Layer 1)
        LeftWheel3D.build(
            polygons = polygons,
            direction = telemetry.leftWheelState.direction,
            phase = leftWheelPhase
        )
        RightWheel3D.build(
            polygons = polygons,
            direction = telemetry.rightWheelState.direction,
            phase = rightWheelPhase
        )

        // 6. Gövde Elektroniği: L298N Sürücü, Arduino Uno R3, 18650 Piller (Layer 1)
        L298NDriver3D.build(polygons = polygons)
        ArduinoUno3D.build(polygons = polygons, telemetry = telemetry)
        BatteryPack3D.build(polygons = polygons)

        // 7. Elektronik Jumper Kablo Demeti & Siyah Kelepçe (Layer 1)
        WireHarness3D.build(polygons = polygons)

        // 8. SG90 Mikro Servo & Dönen HC-SR04 Ultrasonik Başlık (Layer 1) ve Canlı Sonar Işını (Layer 2)
        Sg90Servo3D.build(
            polygons = polygons,
            servoAngle = telemetry.servoAngle
        )
        Hcsr04Sensor3D.build(
            polygons = polygons,
            telemetry = telemetry,
            sonarWavePhase = sonarWavePhase
        )

        return polygons
    }
}
