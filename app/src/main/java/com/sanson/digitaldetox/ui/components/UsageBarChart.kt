package com.sanson.digitaldetox.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sanson.digitaldetox.data.model.DailyStats
import com.sanson.digitaldetox.util.TimeUtils

@Composable
fun UsageBarChart(
    dailyStats: List<DailyStats>,
    modifier: Modifier = Modifier
) {
    val maxDuration = dailyStats.maxOfOrNull { it.totalDurationMs } ?: 1L
    val maxBarHeight = 140.dp
    var animate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { animate = true }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        dailyStats.forEach { stat ->
            val fraction = if (maxDuration > 0) stat.totalDurationMs.toFloat() / maxDuration else 0f
            val animatedFraction by animateFloatAsState(
                targetValue = if (animate) fraction else 0f,
                animationSpec = tween(durationMillis = 850),
                label = "barAnimation"
            )
            val isToday = TimeUtils.getDayLabel(stat.dayTimestamp) == "Today"

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = TimeUtils.formatDurationShort(stat.totalDurationMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isToday) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(maxBarHeight)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.06f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(maxBarHeight * animatedFraction.coerceAtLeast(0.03f))
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isToday) {
                                    Brush.verticalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                                        )
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                                        )
                                    )
                                }
                            )
                    )
                }
                Text(
                    text = TimeUtils.getDayShortLabel(stat.dayTimestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isToday) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
