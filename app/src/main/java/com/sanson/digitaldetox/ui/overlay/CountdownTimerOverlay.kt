package com.sanson.digitaldetox.ui.overlay

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    var secondsRemaining by remember { mutableIntStateOf(totalSeconds) }
    val progress by animateFloatAsState(
        targetValue = secondsRemaining.toFloat() / totalSeconds,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
        onTimerFinished()
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

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xE6101020))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(48.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(48.dp),
                    color = timerColor,
                    trackColor = Color.White.copy(alpha = 0.1f),
                    strokeWidth = 3.dp
                )
                Text(
                    text = timeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                text = "nudge",
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
