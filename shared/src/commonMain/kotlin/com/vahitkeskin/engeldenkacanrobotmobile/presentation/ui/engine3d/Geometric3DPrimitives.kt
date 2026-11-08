package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🧊 Geometric3DPrimitives
 * Robot parçalarının 3D kutu, silindir, döndürülmüş gövde ve şasi plakası
 * gibi temel geometrik poligonlarını oluşturan yardımcı motor.
 */
enum class CylinderAxis { X, Y, Z }

object Geometric3DPrimitives {

    fun add3DBox(
        polygons: ArrayList<Polygon3D>,
        cx: Float, cy: Float, cz: Float,
        width: Float, height: Float, depth: Float,
        color: Color,
        renderLayer: Int = 1
    ) {
        val hw = width / 2f
        val hh = height / 2f
        val hd = depth / 2f

        val v0 = Vector3D(cx - hw, cy - hh, cz - hd)
        val v1 = Vector3D(cx + hw, cy - hh, cz - hd)
        val v2 = Vector3D(cx + hw, cy + hh, cz - hd)
        val v3 = Vector3D(cx - hw, cy + hh, cz - hd)
        val v4 = Vector3D(cx - hw, cy - hh, cz + hd)
        val v5 = Vector3D(cx + hw, cy - hh, cz + hd)
        val v6 = Vector3D(cx + hw, cy + hh, cz + hd)
        val v7 = Vector3D(cx - hw, cy + hh, cz + hd)

        polygons.add(Polygon3D(listOf(v0, v1, v2, v3), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v5, v4, v7, v6), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v4, v0, v3, v7), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v1, v5, v6, v2), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v4, v5, v1, v0), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v3, v2, v6, v7), color, isDoubleSided = true, renderLayer = renderLayer))
    }

    fun addRotatedBox(
        polygons: ArrayList<Polygon3D>,
        pivot: Vector3D,
        center: Vector3D,
        width: Float, height: Float, depth: Float,
        color: Color,
        angleRad: Float,
        renderLayer: Int = 1
    ) {
        val hw = width / 2f
        val hh = height / 2f
        val hd = depth / 2f

        val raw = listOf(
            Vector3D(center.x - hw, center.y - hh, center.z - hd),
            Vector3D(center.x + hw, center.y - hh, center.z - hd),
            Vector3D(center.x + hw, center.y + hh, center.z - hd),
            Vector3D(center.x - hw, center.y + hh, center.z - hd),
            Vector3D(center.x - hw, center.y - hh, center.z + hd),
            Vector3D(center.x + hw, center.y - hh, center.z + hd),
            Vector3D(center.x + hw, center.y + hh, center.z + hd),
            Vector3D(center.x - hw, center.y + hh, center.z + hd)
        )

        val v = raw.map { pivot + (it - pivot).rotateY(angleRad) }

        polygons.add(Polygon3D(listOf(v[0], v[1], v[2], v[3]), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v[5], v[4], v[7], v[6]), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v[4], v[0], v[3], v[7]), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v[1], v[5], v[6], v[2]), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v[4], v[5], v[1], v[0]), color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(listOf(v[3], v[2], v[6], v[7]), color, isDoubleSided = true, renderLayer = renderLayer))
    }

    fun add3DCylinder(
        polygons: ArrayList<Polygon3D>,
        cx: Float, cy: Float, cz: Float,
        radius: Float, height: Float, segments: Int,
        color: Color,
        axis: CylinderAxis,
        renderLayer: Int = 1
    ) {
        val halfH = height / 2f
        val cap1 = ArrayList<Vector3D>(segments)
        val cap2 = ArrayList<Vector3D>(segments)

        for (i in 0 until segments) {
            val a = i * (2f * PI.toFloat() / segments)
            val d1 = radius * cos(a)
            val d2 = radius * sin(a)

            when (axis) {
                CylinderAxis.X -> {
                    cap1.add(Vector3D(cx - halfH, cy + d1, cz + d2))
                    cap2.add(Vector3D(cx + halfH, cy + d1, cz + d2))
                }
                CylinderAxis.Y -> {
                    cap1.add(Vector3D(cx + d1, cy - halfH, cz + d2))
                    cap2.add(Vector3D(cx + d1, cy + halfH, cz + d2))
                }
                CylinderAxis.Z -> {
                    cap1.add(Vector3D(cx + d1, cy + d2, cz - halfH))
                    cap2.add(Vector3D(cx + d1, cy + d2, cz + halfH))
                }
            }
        }

        polygons.add(Polygon3D(cap1, color, isDoubleSided = true, renderLayer = renderLayer))
        polygons.add(Polygon3D(cap2, color, isDoubleSided = true, renderLayer = renderLayer))

        for (i in 0 until segments) {
            val next = (i + 1) % segments
            polygons.add(
                Polygon3D(
                    vertices = listOf(cap1[i], cap1[next], cap2[next], cap2[i]),
                    color = color,
                    isDoubleSided = true,
                    renderLayer = renderLayer
                )
            )
        }
    }

    fun addChassisPlate(
        polygons: ArrayList<Polygon3D>,
        vTop: List<Vector3D>,
        vBottom: List<Vector3D>,
        edges: List<Pair<Int, Int>>,
        topColor: Color,
        bottomColor: Color,
        edgeColor: Color
    ) {
        polygons.add(Polygon3D(vTop, topColor, strokeColor = edgeColor, strokeWidth = 0.8f, isDoubleSided = true, renderLayer = 1))
        polygons.add(Polygon3D(vBottom, bottomColor, strokeColor = edgeColor, strokeWidth = 0.8f, isDoubleSided = true, renderLayer = 1))
        for (edge in edges) {
            val i1 = edge.first
            val i2 = edge.second
            polygons.add(
                Polygon3D(
                    vertices = listOf(vTop[i1], vTop[i2], vBottom[i2], vBottom[i1]),
                    color = edgeColor,
                    isDoubleSided = true,
                    renderLayer = 1
                )
            )
        }
    }
}

