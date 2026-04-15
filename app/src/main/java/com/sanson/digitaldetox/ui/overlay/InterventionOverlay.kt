package com.sanson.digitaldetox.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.sp
import com.sanson.digitaldetox.R
import com.sanson.digitaldetox.data.db.entity.UsageLogEntity
import com.sanson.digitaldetox.data.model.OverlayData
import com.sanson.digitaldetox.ui.theme.BrightWhite
import com.sanson.digitaldetox.ui.theme.CoolLav
import com.sanson.digitaldetox.ui.theme.DarkIndigo
import com.sanson.digitaldetox.ui.theme.DeepVoid
import com.sanson.digitaldetox.ui.theme.ElecPurple
import com.sanson.digitaldetox.ui.theme.HotPink
import com.sanson.digitaldetox.ui.theme.MidIndigo
import com.sanson.digitaldetox.ui.theme.NeonCyan
import com.sanson.digitaldetox.ui.theme.NeonYellow
import com.sanson.digitaldetox.ui.theme.PixelFont
import com.sanson.digitaldetox.ui.theme.SoftCyan
import com.sanson.digitaldetox.util.TimeUtils
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════
//  Game Boy DMG LCD palette (4 shades)
//  Lightest 0xFF9BBC0F  — LCD background
//  Light    0xFF8BAC0F  — card / subtle bg
//  Dark     0xFF306230  — borders / secondary
//  Darkest  0xFF0F380F  — text / pixel ink
// ═══════════════════════════════════════════════════

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

    val intents = listOf(
        IntentOption(UsageLogEntity.INTENT_SUBCONSCIOUS, stringResource(R.string.intent_subconscious)),
        IntentOption(UsageLogEntity.INTENT_WORK, stringResource(R.string.intent_work)),
        IntentOption(UsageLogEntity.INTENT_BORED, stringResource(R.string.intent_bored)),
        IntentOption(UsageLogEntity.INTENT_URGENT, stringResource(R.string.intent_urgent))
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

    // ── Full-screen LCD background ──
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepVoid, Color(0xFF13112A), DeepVoid))),
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
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── App name badge ──
                Box(
                    modifier = Modifier
                        .border(2.dp, NeonCyan, RoundedCornerShape(4.dp))
                        .background(DarkIndigo, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "[ ${data.appName.uppercase()} ]",
                        style = MaterialTheme.typography.labelLarge,
                        color = BrightWhite
                    )
                }

                Spacer(Modifier.height(14.dp))

                // ── Title ──
                Text(
                    text = stringResource(R.string.overlay_pause_title).uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = BrightWhite,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                // ── Custom message ──
                Text(
                    text = data.customMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = CoolLav,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(Modifier.height(16.dp))

                // ══ Main card — timer or intent check ══
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, NeonCyan, RoundedCornerShape(4.dp))
                        .background(DarkIndigo, RoundedCornerShape(4.dp))
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!timerFinished) {
                            // ── Countdown ──
                            Text(
                                text = "BREATHE...",
                                style = MaterialTheme.typography.titleMedium,
                                color = BrightWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(10.dp))

                            // Pixel-style countdown box
                            Box(
                                modifier = Modifier
                                    .border(2.dp, NeonCyan, RoundedCornerShape(4.dp))
                                    .background(DeepVoid, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 24.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$timeRemaining",
                                    fontSize = 32.sp,
                                    fontFamily = PixelFont,
                                    fontWeight = FontWeight.Bold,
                                    color = BrightWhite
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            // Simple progress bar
                            val progress = if (data.cooldownSeconds > 0)
                                timeRemaining.toFloat() / data.cooldownSeconds else 0f
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .border(1.dp, NeonCyan, RoundedCornerShape(2.dp))
                                    .background(DeepVoid, RoundedCornerShape(2.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .height(10.dp)
                                        .background(BrightWhite, RoundedCornerShape(2.dp))
                                )
                            }

                        } else {
                            // ── Intent check ──
                            Text(
                                text = stringResource(R.string.intent_check_title).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                color = BrightWhite,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.intent_check_hint, data.appName),
                                style = MaterialTheme.typography.bodySmall,
                                color = CoolLav,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(12.dp))

                            intents.chunked(2).forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    row.forEach { opt ->
                                        GBIntentChip(
                                            label = opt.label,
                                            count = data.intentCountsToday[opt.key] ?: 0,
                                            selected = selectedIntent == opt.key,
                                            onClick = { selectedIntent = opt.key },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (row.size == 1) Spacer(Modifier.weight(1f))
                                }
                                Spacer(Modifier.height(6.dp))
                            }

                            if (selectedIntent == null) {
                                Text(
                                    text = "> ${stringResource(R.string.intent_check_required)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = HotPink
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ══ Stats row ══
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, NeonCyan, RoundedCornerShape(4.dp))
                        .background(DarkIndigo, RoundedCornerShape(4.dp))
                        .padding(vertical = 10.dp, horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GBStat(Icons.Filled.Repeat, "${data.opensToday}", stringResource(R.string.overlay_opens_today_label))
                        GBStat(Icons.Filled.AccessTime, TimeUtils.formatDuration(data.timeSpentTodayMs), stringResource(R.string.overlay_today_label))
                        GBStat(Icons.Filled.Timeline, TimeUtils.formatDuration(data.timeSpentWeekMs), stringResource(R.string.overlay_this_week_label))
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── GO BACK button (primary action) ──
                Button(
                    onClick = onGoBack,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HotPink,
                        contentColor = BrightWhite
                    ),
                    border = BorderStroke(2.dp, HotPink)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.go_back).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(8.dp))

                // ── CONTINUE button (secondary) ──
                OutlinedButton(
                    onClick = { selectedIntent?.let(onContinue) },
                    enabled = timerFinished && selectedIntent != null,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NeonCyan,
                        disabledContentColor = CoolLav.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(2.dp, if (timerFinished && selectedIntent != null) NeonCyan else CoolLav.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = stringResource(R.string.continue_nudge_format, data.nudgeAfterMinutes).uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ── Stat column ──
@Composable
private fun GBStat(icon: ImageVector, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(icon, null, tint = NeonCyan, modifier = Modifier.size(14.dp))
        Text(value, style = MaterialTheme.typography.titleSmall, color = BrightWhite, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = CoolLav)
    }
}

// ── Intent chip — Game Boy button style ──
@Composable
private fun GBIntentChip(
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) ElecPurple else MidIndigo
    val fg = if (selected) BrightWhite else BrightWhite
    val border = if (selected) ElecPurple else NeonCyan.copy(alpha = 0.35f)

    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .border(2.dp, border, RoundedCornerShape(4.dp))
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = fg, fontWeight = FontWeight.Bold)
            Text(
                stringResource(R.string.intent_count_for_app, count),
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) SoftCyan else NeonYellow
            )
        }
    }
}

private data class IntentOption(val key: String, val label: String)

