package com.sanson.digitaldetox.ui.screens.settings

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanson.digitaldetox.ui.components.PermissionCard
import com.sanson.digitaldetox.ui.theme.Secondary
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val isServiceEnabled by viewModel.isServiceEnabled.collectAsState()
    val cooldown by viewModel.cooldownSeconds.collectAsState()
    val escalation by viewModel.isEscalationEnabled.collectAsState()
    val lateNight by viewModel.isLateNightModeEnabled.collectAsState()
    val lateNightStart by viewModel.lateNightStart.collectAsState()
    val lateNightEnd by viewModel.lateNightEnd.collectAsState()
    val sessionNudge by viewModel.isSessionNudgeEnabled.collectAsState()
    val sessionNudgeMinutes by viewModel.sessionNudgeMinutes.collectAsState()

    var hasAccessibility by remember { mutableStateOf(isAccessibilityEnabled(context)) }
    var hasOverlay by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var hasUsageStats by remember { mutableStateOf(hasUsageStatsPermission(context)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            hasAccessibility = isAccessibilityEnabled(context)
            hasOverlay = Settings.canDrawOverlays(context)
            hasUsageStats = hasUsageStatsPermission(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Master toggle
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = if (isServiceEnabled) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isServiceEnabled) "Protection Active" else "Protection Off",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isServiceEnabled) "Monitoring your app usage"
                        else "Turn on to start monitoring",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isServiceEnabled,
                    onCheckedChange = { viewModel.setServiceEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Secondary,
                        checkedTrackColor = Secondary.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Timing section
        SectionHeader("Timing")

        SettingRow(
            title = "Cooldown Duration",
            subtitle = "${cooldown}s pause before you can continue"
        ) {
            Slider(
                value = cooldown.toFloat(),
                onValueChange = { viewModel.setCooldownSeconds(it.roundToInt()) },
                valueRange = 3f..30f,
                steps = 26,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        ToggleRow(
            title = "Smart Escalation",
            subtitle = "Longer waits as you open apps more often",
            checked = escalation,
            onCheckedChange = { viewModel.setEscalationEnabled(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Night mode section
        SectionHeader("Night Mode")

        ToggleRow(
            title = "Late Night Mode",
            subtitle = "Double cooldown ${lateNightStart}:00 - ${lateNightEnd}:00",
            checked = lateNight,
            onCheckedChange = { viewModel.setLateNightMode(it) }
        )

        if (lateNight) {
            SettingRow(
                title = "Start Hour",
                subtitle = "Night mode starts at ${lateNightStart}:00"
            ) {
                Slider(
                    value = lateNightStart.toFloat(),
                    onValueChange = { viewModel.setLateNightStart(it.roundToInt()) },
                    valueRange = 18f..23f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            SettingRow(
                title = "End Hour",
                subtitle = "Night mode ends at ${lateNightEnd}:00"
            ) {
                Slider(
                    value = lateNightEnd.toFloat(),
                    onValueChange = { viewModel.setLateNightEnd(it.roundToInt()) },
                    valueRange = 4f..10f,
                    steps = 5,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Session monitoring
        SectionHeader("Session Monitoring")

        ToggleRow(
            title = "Session Nudge",
            subtitle = "Remind after ${sessionNudgeMinutes} min of continuous use",
            checked = sessionNudge,
            onCheckedChange = { viewModel.setSessionNudgeEnabled(it) }
        )

        if (sessionNudge) {
            SettingRow(
                title = "Nudge After",
                subtitle = "${sessionNudgeMinutes} minutes"
            ) {
                Slider(
                    value = sessionNudgeMinutes.toFloat(),
                    onValueChange = { viewModel.setSessionNudgeMinutes(it.roundToInt()) },
                    valueRange = 1f..60f,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Permissions section
        SectionHeader("Permissions")

        Spacer(modifier = Modifier.height(8.dp))

        PermissionCard(
            title = "Accessibility Service",
            description = "Detects when you open monitored apps",
            icon = Icons.Filled.Accessibility,
            isGranted = hasAccessibility,
            onGrant = {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        PermissionCard(
            title = "Display Over Other Apps",
            description = "Shows the mindful pause screen",
            icon = Icons.Filled.Layers,
            isGranted = hasOverlay,
            onGrant = {
                context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        PermissionCard(
            title = "Usage Access",
            description = "Tracks how much time you spend in apps",
            icon = Icons.Filled.QueryStats,
            isGranted = hasUsageStats,
            onGrant = {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // About
        Text(
            text = "Digital Detox v1.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

private fun isAccessibilityEnabled(context: Context): Boolean {
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    return enabledServices.contains(context.packageName)
}

private fun hasUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}
