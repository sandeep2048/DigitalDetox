package com.sanson.digitaldetox.ui.overlay

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanson.digitaldetox.ui.theme.BrightWhite
import com.sanson.digitaldetox.ui.theme.CoolLav
import com.sanson.digitaldetox.ui.theme.DarkIndigo
import com.sanson.digitaldetox.ui.theme.DeepVoid
import com.sanson.digitaldetox.ui.theme.HotPink
import com.sanson.digitaldetox.ui.theme.MidIndigo
import com.sanson.digitaldetox.ui.theme.NeonCyan
import com.sanson.digitaldetox.ui.theme.NeonYellow
import com.sanson.digitaldetox.ui.theme.PixelFont
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun CountdownTimerOverlay(
    totalSeconds: Int,
    onTimerFinished: () -> Unit
) {
    var secondsRemaining by remember(totalSeconds) { mutableIntStateOf(totalSeconds) }
    var callbackFired by remember(totalSeconds) { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (totalSeconds > 0) secondsRemaining.toFloat() / totalSeconds else 0f,
        animationSpec = tween(durationMillis = 900, easing = LinearEasing),
        label = "progress"
    )

    LaunchedEffect(totalSeconds) {
        if (totalSeconds <= 0) {
            if (!callbackFired) { callbackFired = true; onTimerFinished() }
            return@LaunchedEffect
        }
        val startMs = System.currentTimeMillis()
        while (secondsRemaining > 0) {
            delay(200L)
            val elapsedSec = ((System.currentTimeMillis() - startMs) / 1000L).toInt()
            secondsRemaining = maxOf(0, totalSeconds - elapsedSec)
        }
        if (!callbackFired) { callbackFired = true; onTimerFinished() }
    }

    val timeText = String.format(Locale.US, "%d:%02d", secondsRemaining / 60, secondsRemaining % 60)
    val timerColor = when {
        secondsRemaining > totalSeconds * 0.3f -> NeonCyan
        secondsRemaining > totalSeconds * 0.1f -> NeonYellow
        else -> HotPink
    }

    // Game Boy style floating chip
    Box(
        modifier = Modifier
            .border(2.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .background(DeepVoid.copy(alpha = 0.96f), RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Timer display box
            Box(
                modifier = Modifier
                    .border(2.dp, timerColor, RoundedCornerShape(2.dp))
                    .background(DarkIndigo, RoundedCornerShape(2.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = timeText,
                    fontSize = 10.sp,
                    fontFamily = PixelFont,
                    fontWeight = FontWeight.Bold,
                    color = timerColor
                )
            }

            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = "SESSION",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrightWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.size(1.dp))
                // Mini progress bar
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(4.dp)
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(1.dp))
                        .background(MidIndigo, RoundedCornerShape(1.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(4.dp)
                            .background(timerColor, RoundedCornerShape(1.dp))
                    )
                }
            }
        }
    }
}
