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

// Synthwave neon palette
private val NeonCyan = Color(0xFF00F5D4)
private val ElecPurple = Color(0xFF7B61FF)
private val HotPink = Color(0xFFF72585)
private val DeepVoid = Color(0xFF0F0E17)

@Composable
fun BreathingAnimation(
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") primaryColor: Color = NeonCyan,
    @Suppress("UNUSED_PARAMETER") secondaryColor: Color = ElecPurple
) {
    val transition = rememberInfiniteTransition(label = "breathing")
    val scale by transition.animateFloat(
        initialValue = 0.4f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 3000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "scale"
    )
    val colorPhase by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 4000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "colorPhase"
    )

    Canvas(modifier = modifier.size(100.dp)) {
        val pixelSize = size.minDimension / 10f
        val cx = size.width / 2f
        val cy = size.height / 2f
        val gridCount = (10 * scale).toInt().coerceIn(2, 8)
        val half = gridCount / 2f
        val pixelColor = lerp(NeonCyan, ElecPurple, colorPhase)
        val glowColor = lerp(ElecPurple, HotPink, colorPhase)

        // Outer glow border
        val outerSize = pixelSize * 10
        val outerOff = (size.width - outerSize) / 2f
        drawRect(color = glowColor.copy(alpha = 0.15f), topLeft = Offset(outerOff, outerOff), size = Size(outerSize, outerSize))

        // Pulsing pixel diamond
        for (row in 0 until gridCount) {
            for (col in 0 until gridCount) {
                val x = cx - half * pixelSize + col * pixelSize
                val y = cy - half * pixelSize + row * pixelSize
                val dist = kotlin.math.abs(row - half + 0.5f) + kotlin.math.abs(col - half + 0.5f)
                if (dist <= half) {
                    drawRect(color = pixelColor, topLeft = Offset(x, y), size = Size(pixelSize - 1f, pixelSize - 1f))
                }
            }
        }
    }
}

private fun lerp(a: Color, b: Color, t: Float): Color {
    return Color(
        red = a.red + (b.red - a.red) * t,
        green = a.green + (b.green - a.green) * t,
        blue = a.blue + (b.blue - a.blue) * t,
        alpha = a.alpha + (b.alpha - a.alpha) * t
    )
}
