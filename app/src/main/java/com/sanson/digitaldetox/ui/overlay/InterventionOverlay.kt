package com.sanson.digitaldetox.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sanson.digitaldetox.data.model.OverlayData
import com.sanson.digitaldetox.ui.components.BreathingAnimation
import com.sanson.digitaldetox.ui.theme.OverlayBackground
import com.sanson.digitaldetox.ui.theme.OverlayCard
import com.sanson.digitaldetox.ui.theme.Secondary
import com.sanson.digitaldetox.util.TimeUtils
import kotlinx.coroutines.delay

@Composable
fun InterventionOverlay(
    data: OverlayData,
    onContinue: () -> Unit,
    onGoBack: () -> Unit
) {
    var timeRemaining by remember { mutableIntStateOf(data.cooldownSeconds) }
    var contentVisible by remember { mutableStateOf(false) }
    val timerFinished = timeRemaining <= 0

    LaunchedEffect(Unit) {
        contentVisible = true
        while (timeRemaining > 0) {
            delay(1000)
            timeRemaining--
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayBackground),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App name chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = data.appName,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Breathing animation
                BreathingAnimation()

                Spacer(modifier = Modifier.height(32.dp))

                // Custom message
                Text(
                    text = data.customMessage,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Stats row
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = OverlayCard
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OverlayStat(
                            icon = Icons.Filled.Repeat,
                            value = "${data.opensToday}",
                            label = "opens today"
                        )
                        OverlayStat(
                            icon = Icons.Filled.AccessTime,
                            value = TimeUtils.formatDuration(data.timeSpentTodayMs),
                            label = "today"
                        )
                        OverlayStat(
                            icon = Icons.Filled.Timeline,
                            value = TimeUtils.formatDuration(data.timeSpentWeekMs),
                            label = "this week"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Timer or ready state
                if (!timerFinished) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { timeRemaining.toFloat() / data.cooldownSeconds },
                            modifier = Modifier.size(64.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = "$timeRemaining",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Light
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Breathe...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                } else {
                    // Action buttons
                    Button(
                        onClick = onGoBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Secondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Go Back",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = "Continue anyway (nudge in ${data.nudgeAfterMinutes}m)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverlayStat(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}
