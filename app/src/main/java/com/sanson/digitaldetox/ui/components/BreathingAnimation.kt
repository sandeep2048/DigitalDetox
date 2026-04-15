package com.sanson.digitaldetox.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Game Boy DMG palette
private val GBLightest = Color(0xFF9BBC0F)
private val GBDarkest  = Color(0xFF0F380F)
private val GBDark     = Color(0xFF306230)

@Composable
fun BreathingAnimation(
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") primaryColor: Color = GBDarkest,
    @Suppress("UNUSED_PARAMETER") secondaryColor: Color = GBDark
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Pixel-art pulsing grid — feels like a Game Boy sprite
    Canvas(modifier = modifier.size(100.dp)) {
        val pixelSize = size.minDimension / 10f
        val cx = size.width / 2f
        val cy = size.height / 2f
        val gridCount = (10 * scale).toInt().coerceIn(2, 8)
        val half = gridCount / 2f

        // Draw outer border square
        val outerSize = pixelSize * 10
        val outerOff = (size.width - outerSize) / 2f
        drawRect(
            color = GBDark.copy(alpha = 0.3f),
            topLeft = Offset(outerOff, outerOff),
            size = Size(outerSize, outerSize)
        )

        // Draw pulsing pixel grid
        for (row in 0 until gridCount) {
            for (col in 0 until gridCount) {
                val x = cx - half * pixelSize + col * pixelSize
                val y = cy - half * pixelSize + row * pixelSize
                // Diamond mask for a classic sprite shape
                val distFromCenter = kotlin.math.abs(row - half + 0.5f) + kotlin.math.abs(col - half + 0.5f)
                if (distFromCenter <= half) {
                    drawRect(
                        color = GBDarkest,
                        topLeft = Offset(x, y),
                        size = Size(pixelSize - 1f, pixelSize - 1f)
                    )
                }
            }
        }
    }
}
