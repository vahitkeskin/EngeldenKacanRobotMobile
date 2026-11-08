package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Camera3D
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Polygon3D
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.engine3d.Vector3D

/**
 * 🔍 Part3DPreviewBox
 * Her bir robot parçasının tekil olarak (izole 3D perspektifte)
 * odaklanmış, ortalanmış ve pürüzsüz biçimde Compose Preview'da
 * ve canlı ekranda incelenebilmesini sağlayan önizleme konteyneri.
 */
@Composable
fun Part3DPreviewBox(
    title: String,
    subtitle: String,
    polygons: List<Polygon3D>,
    modifier: Modifier = Modifier.size(340.dp),
    backgroundColor: Color = Color(0xFF0F172A),
    yaw: Float = -35f,
    pitch: Float = 26f,
    zoom: Float = 2.4f,
    targetCenter: Vector3D? = null
) {
    // Parçayı geometrik ağırlık merkezine göre (0,0,0) noktasına otomatik hizala
    val centeredPolygons = remember(polygons, targetCenter) {
        if (polygons.isEmpty()) return@remember emptyList()
        val center = targetCenter ?: run {
            var minX = Float.MAX_VALUE
            var maxX = -Float.MAX_VALUE
            var minY = Float.MAX_VALUE
            var maxY = -Float.MAX_VALUE
            var minZ = Float.MAX_VALUE
            var maxZ = -Float.MAX_VALUE
            for (poly in polygons) {
                for (v in poly.vertices) {
                    if (v.x < minX) minX = v.x
                    if (v.x > maxX) maxX = v.x
                    if (v.y < minY) minY = v.y
                    if (v.y > maxY) maxY = v.y
                    if (v.z < minZ) minZ = v.z
                    if (v.z > maxZ) maxZ = v.z
                }
            }
            Vector3D((minX + maxX) / 2f, (minY + maxY) / 2f, (minZ + maxZ) / 2f)
        }
        polygons.map { poly ->
            poly.copy(
                vertices = poly.vertices.map { v -> v - center }
            )
        }
    }

    val camera = remember(yaw, pitch, zoom) {
        Camera3D(initialYaw = yaw, initialPitch = pitch, initialZoom = zoom)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
    ) {
        // 3D Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (size.width <= 10f || size.height <= 10f) return@Canvas
            val projected = camera.project(centeredPolygons, size.width, size.height)
            val path = Path()
            for (poly in projected) {
                val pts = poly.points
                if (pts.size < 3) continue
                path.reset()
                path.moveTo(pts[0].x, pts[0].y)
                for (i in 1 until pts.size) {
                    path.lineTo(pts[i].x, pts[i].y)
                }
                path.close()
                drawPath(path = path, color = poly.color)
                if (poly.strokeColor != null && poly.strokeWidth > 0f) {
                    drawPath(
                        path = path,
                        color = poly.strokeColor,
                        style = Stroke(width = poly.strokeWidth)
                    )
                }
            }
        }

        // Başlık Rozeti (Badge)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC1E293B))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
            )
        }

        // Poligon Sayacı Rozeti
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0x990F172A))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${polygons.size} Poly",
                color = Color(0xFF38BDF8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
