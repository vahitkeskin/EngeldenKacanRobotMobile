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
        cameraPitch: Float = 32f,
        cameraYaw: Float = -35f
    ): List<Polygon3D> {
        val polygons = ArrayList<Polygon3D>(950)

        // 1. Zemin Kesim Matı (Layer -1 ve 0 - Arka plan, robot parçalarını asla örtmez)
        CuttingMat3D.build(
            polygons = polygons,
            isDark = isDark,
            cameraPitch = cameraPitch
        )

        // 2. Dinamik Katman Belirleme (Fiziksel Örtme - 3D Occlusion):
        // Dikey Eksen (Pitch):
        // cameraPitch >= 0f iken (üstten bakış): Alt parçalar (Layer 1) -> Şasi (Layer 2) -> Üst parçalar (Layer 3)
        // Böylece opak ahşap şasi alt motorları ve sarhoş tekeri tamamen örter.
        // cameraPitch < 0f iken (alttan bakış): Üst parçalar (Layer 1) -> Şasi (Layer 2) -> Alt parçalar (Layer 3)
        // Böylece opak ahşap şasi Arduino, servo, pil ve kabloları tamamen örter.
        val isLookingFromAbove = cameraPitch >= 0f
        val undersideLayer = if (isLookingFromAbove) 1 else 3
        val chassisLayer = 2
        val topLayer = if (isLookingFromAbove) 3 else 1

        // Yatay Eksen (Yaw) ve Yan Profil Fiziksel Bariyer:
        // Robot X ekseninde -72 (Sol Tekerlek) ve +72 (Sağ Tekerlek) genişliğindedir.
        // Ahşap şasi ise merkezde -65 ile +65 arasında katı bir fiziksel bariyer oluşturur.
        // Kullanıcı İsteği: "fizik kurallarına göre yandan baktığımda tekerlerinde görünmemesi gerekiyor çünkü yanda tahta varken bakıyorum, kısaca tahtanın olduğu her açıda tahtanın arkasında kalan kısım görünmesin"
        val yawRad = cameraYaw * (kotlin.math.PI.toFloat() / 180f)
        val sinYaw = kotlin.math.sin(yawRad)

        // Yan profil bakış açısı kontrolü (pitch < 45° ve |sinYaw| > 0.72 - yaklaşık 46° ile 134° arası tam yan cephe):
        // Kamera sağdayken (Sağ görünüm): Karşıdaki sol tekerlek ahşap gövdenin arkasında kalır -> Tamamen gizlenir!
        // Kamera soldayken (Sol görünüm): Karşıdaki sağ tekerlek ahşap gövdenin arkasında kalır -> Tamamen gizlenir!
        // Kuşbakışı (pitch >= 45°) veya ön/arka bakışta her iki tekerlek de iki yanda temizce görünür.
        val isSideView = cameraPitch < 45f && cameraPitch > -45f
        val isRightSide = isSideView && sinYaw > 0.72f
        val isLeftSide = isSideView && sinYaw < -0.72f

        val leftWheelLayer = if (sinYaw > 0.05f) 1 else 3
        val rightWheelLayer = if (sinYaw < -0.05f) 1 else 3

        // 3. MDF Ahşap Şasi (Katı Ahşap Bariyer)
        val chassisPolys = ArrayList<Polygon3D>(40)
        MdfChassis3D.build(polygons = chassisPolys)
        for (p in chassisPolys) {
            polygons.add(p.copy(renderLayer = chassisLayer))
        }

        // 4. Alt Gövde Parçaları: Sarı TT Motor Gövdeleri, Çelik DC Motorlar, Sarhoş Bilye Tekerlek
        val undersidePolys = ArrayList<Polygon3D>(120)
        UndersideMotors3D.build(polygons = undersidePolys)
        CasterWheel3D.build(polygons = undersidePolys)
        for (p in undersidePolys) {
            polygons.add(p.copy(renderLayer = undersideLayer))
        }

        // 5. Üst Gövde Elektroniği: L298N Sürücü, Arduino Uno R3, 18650 Piller, Kablo Demeti, SG90 Servo, HC-SR04
        val topPolys = ArrayList<Polygon3D>(400)
        L298NDriver3D.build(polygons = topPolys)
        ArduinoUno3D.build(polygons = topPolys, telemetry = telemetry)
        BatteryPack3D.build(polygons = topPolys)
        WireHarness3D.build(polygons = topPolys)
        Sg90Servo3D.build(
            polygons = topPolys,
            servoAngle = telemetry.servoAngle
        )
        Hcsr04Sensor3D.build(
            polygons = topPolys,
            telemetry = telemetry,
            sonarWavePhase = sonarWavePhase
        )
        for (p in topPolys) {
            if (p.renderLayer >= 2) {
                // Sonar dalgası gibi özel efekt katmanları en üstte çizilir
                polygons.add(p.copy(renderLayer = 4))
            } else {
                polygons.add(p.copy(renderLayer = topLayer))
            }
        }

        // 6. Sol ve Sağ 32-Segment Yuvarlak Lastikler ve Sarı TT Jantlar
        if (!isRightSide) {
            val leftPolys = ArrayList<Polygon3D>(80)
            LeftWheel3D.build(
                polygons = leftPolys,
                direction = telemetry.leftWheelState.direction,
                phase = leftWheelPhase
            )
            for (p in leftPolys) {
                polygons.add(p.copy(renderLayer = leftWheelLayer))
            }
        }

        if (!isLeftSide) {
            val rightPolys = ArrayList<Polygon3D>(80)
            RightWheel3D.build(
                polygons = rightPolys,
                direction = telemetry.rightWheelState.direction,
                phase = rightWheelPhase
            )
            for (p in rightPolys) {
                polygons.add(p.copy(renderLayer = rightWheelLayer))
            }
        }

        return polygons
    }
}
