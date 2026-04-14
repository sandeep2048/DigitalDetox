package com.sanson.digitaldetox.ui.overlay

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanson.digitaldetox.ui.theme.ErrorColor
import com.sanson.digitaldetox.ui.theme.Primary
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun CountdownTimerOverlay(
    totalSeconds: Int,
    onTimerFinished: () -> Unit
) {
    // Key on totalSeconds so the state resets if the value changes on recomposition
    var secondsRemaining by remember(totalSeconds) { mutableIntStateOf(totalSeconds) }
    var callbackFired by remember(totalSeconds) { mutableStateOf(false) }
    val retroFont = FontFamily.Monospace

    val progress by animateFloatAsState(
        targetValue = if (totalSeconds > 0) secondsRemaining.toFloat() / totalSeconds else 0f,
        animationSpec = tween(durationMillis = 900, easing = LinearEasing),
        label = "progress"
    )

    // Wall-clock countdown — not susceptible to delay(1000) drift
    LaunchedEffect(totalSeconds) {
        if (totalSeconds <= 0) {
            if (!callbackFired) {
                callbackFired = true
                onTimerFinished()
            }
            return@LaunchedEffect
        }
        val startMs = System.currentTimeMillis()
        while (secondsRemaining > 0) {
            delay(200L) // poll every 200 ms for a smooth display
            val elapsedSec = ((System.currentTimeMillis() - startMs) / 1000L).toInt()
            secondsRemaining = maxOf(0, totalSeconds - elapsedSec)
        }
        if (!callbackFired) {
            callbackFired = true
            onTimerFinished()
        }
    }

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeText = String.format(Locale.US, "%d:%02d", minutes, seconds)

    // Color transitions: green → yellow → red as time runs out
    val timerColor = when {
        secondsRemaining > totalSeconds * 0.3f -> Primary
        secondsRemaining > totalSeconds * 0.1f -> Color(0xFFFFB347)
        else -> ErrorColor
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xE60D1611),
        border = BorderStroke(1.dp, Color(0xFF2CEB7E).copy(alpha = 0.75f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp)) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(44.dp),
                    color = timerColor,
                    trackColor = Color(0xFF1E3A2A),
                    strokeWidth = 3.dp
                )
                Text(
                    text = timeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE5FFEF),
                    fontFamily = retroFont
                )
            }

            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = "SESSION ACTIVE",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFE5FFEF),
                    fontWeight = FontWeight.Bold,
                    fontFamily = retroFont
                )
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    text = "NEXT CHECK-IN SOON",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9BD5B4),
                    fontFamily = retroFont
                )
            }
        }
    }
}
