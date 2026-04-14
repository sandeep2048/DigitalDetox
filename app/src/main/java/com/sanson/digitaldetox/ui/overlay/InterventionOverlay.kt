package com.sanson.digitaldetox.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sanson.digitaldetox.R
import com.sanson.digitaldetox.data.db.entity.UsageLogEntity
import com.sanson.digitaldetox.data.model.OverlayData
import com.sanson.digitaldetox.ui.components.BreathingAnimation
import com.sanson.digitaldetox.ui.theme.Primary
import com.sanson.digitaldetox.ui.theme.Secondary
import com.sanson.digitaldetox.util.TimeUtils
import kotlinx.coroutines.delay

@Composable
fun InterventionOverlay(
    data: OverlayData,
    onContinue: (String) -> Unit,
    onGoBack: () -> Unit
) {
    var timeRemaining by remember { mutableIntStateOf(data.cooldownSeconds) }
    var contentVisible by remember { mutableStateOf(false) }
    var selectedIntent by remember { mutableStateOf<String?>(null) }
    val timerFinished = timeRemaining <= 0

    val retroFont = FontFamily.Monospace
    val intents = listOf(
        IntentOption(
            key = UsageLogEntity.INTENT_SUBCONSCIOUS,
            label = stringResource(id = R.string.intent_subconscious)
        ),
        IntentOption(
            key = UsageLogEntity.INTENT_WORK,
            label = stringResource(id = R.string.intent_work)
        ),
        IntentOption(
            key = UsageLogEntity.INTENT_BORED,
            label = stringResource(id = R.string.intent_bored)
        ),
        IntentOption(
            key = UsageLogEntity.INTENT_URGENT,
            label = stringResource(id = R.string.intent_urgent)
        )
    )

    LaunchedEffect(Unit) {
        contentVisible = true
        if (data.cooldownSeconds <= 0) return@LaunchedEffect
        val startMs = System.currentTimeMillis()
        while (timeRemaining > 0) {
            delay(200L)
            val elapsedSec = ((System.currentTimeMillis() - startMs) / 1000L).toInt()
            timeRemaining = maxOf(0, data.cooldownSeconds - elapsedSec)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF050609), Color(0xFF0B1A12), Color(0xFF050609))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 8 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF10251A),
                    border = BorderStroke(1.dp, Color(0xFF2CEB7E))
                ) {
                    Text(
                        text = data.appName.uppercase(),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF95FFBE),
                        fontFamily = retroFont,
                        letterSpacing = MaterialTheme.typography.labelLarge.letterSpacing
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.overlay_pause_title).uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFE5FFEF),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontFamily = retroFont
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = data.customMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFC6F3D7),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontFamily = retroFont
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0D1611),
                    border = BorderStroke(1.dp, Color(0xFF2CEB7E).copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BreathingAnimation()
                        Spacer(modifier = Modifier.height(14.dp))

                        if (!timerFinished) {
                            CircularProgressIndicator(
                                progress = {
                                    if (data.cooldownSeconds > 0) {
                                        timeRemaining.toFloat() / data.cooldownSeconds
                                    } else 0f
                                },
                                modifier = Modifier.size(70.dp),
                                color = Primary,
                                trackColor = Color(0xFF1E3A2A),
                                strokeWidth = 5.dp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "$timeRemaining",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color(0xFFE5FFEF),
                                fontWeight = FontWeight.Bold,
                                fontFamily = retroFont
                            )
                            Text(
                                text = stringResource(id = R.string.breathe),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF95FFBE),
                                fontFamily = retroFont
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.intent_check_title).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFE5FFEF),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontFamily = retroFont
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(id = R.string.intent_check_hint, data.appName),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB1EBC8),
                                textAlign = TextAlign.Center,
                                fontFamily = retroFont
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            intents.chunked(2).forEach { rowIntents ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowIntents.forEach { intentOption ->
                                        IntentOptionCard(
                                            label = intentOption.label,
                                            countToday = data.intentCountsToday[intentOption.key] ?: 0,
                                            isSelected = selectedIntent == intentOption.key,
                                            onClick = { selectedIntent = intentOption.key },
                                            fontFamily = retroFont,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (rowIntents.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (selectedIntent == null) {
                                Text(
                                    text = stringResource(id = R.string.intent_check_required),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFFFB4AB),
                                    fontFamily = retroFont
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0D1611),
                    border = BorderStroke(1.dp, Color(0xFF2CEB7E).copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OverlayStat(
                            icon = Icons.Filled.Repeat,
                            value = "${data.opensToday}",
                            label = stringResource(id = R.string.overlay_opens_today_label),
                            fontFamily = retroFont
                        )
                        OverlayStat(
                            icon = Icons.Filled.AccessTime,
                            value = TimeUtils.formatDuration(data.timeSpentTodayMs),
                            label = stringResource(id = R.string.overlay_today_label),
                            fontFamily = retroFont
                        )
                        OverlayStat(
                            icon = Icons.Filled.Timeline,
                            value = TimeUtils.formatDuration(data.timeSpentWeekMs),
                            label = stringResource(id = R.string.overlay_this_week_label),
                            fontFamily = retroFont
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onGoBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary, contentColor = Color(0xFF07140C))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.go_back).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = retroFont
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { selectedIntent?.let(onContinue) },
                    enabled = timerFinished && selectedIntent != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE5FFEF)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF2CEB7E))
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.continue_nudge_format,
                            data.nudgeAfterMinutes
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = retroFont,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun OverlayStat(
    icon: ImageVector,
    value: String,
    label: String,
    fontFamily: FontFamily
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF95FFBE),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFFE5FFEF),
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF9BD5B4),
            fontFamily = fontFamily
        )
    }
}

private data class IntentOption(
    val key: String,
    val label: String
)

@Composable
private fun IntentOptionCard(
    label: String,
    countToday: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(0xFF2CEB7E) else Color(0xFF2A4A37)
    val bgColor = if (isSelected) Color(0xFF143325) else Color(0xFF0B1510)

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFE5FFEF),
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.intent_count_for_app, countToday),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9BD5B4),
                fontFamily = fontFamily
            )
        }
    }
}
