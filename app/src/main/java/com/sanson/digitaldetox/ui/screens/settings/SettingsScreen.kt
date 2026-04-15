package com.sanson.digitaldetox.ui.screens.settings

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.QueryStats
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanson.digitaldetox.ui.components.PermissionCard
import com.sanson.digitaldetox.ui.theme.Primary
import com.sanson.digitaldetox.ui.theme.PrimaryDark
import com.sanson.digitaldetox.ui.theme.RetroBackgroundBrush
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
            .background(
                RetroBackgroundBrush
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Tune the amount of friction and support you want.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.94f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.38f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                if (isServiceEnabled) PrimaryDark.copy(alpha = 0.30f)
                                else Color.White.copy(alpha = 0.03f),
                                if (isServiceEnabled) Primary.copy(alpha = 0.14f)
                                else Color.Transparent
                            )
                        )
                    )
                    .padding(22.dp)
            ) {
                Text(
                    text = if (isServiceEnabled) "Protection is active" else "Protection is paused",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isServiceEnabled) {
                        "Mindful pauses are active when you open monitored apps."
                    } else {
                        "Turn protection on to start blocking distracting opens."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isServiceEnabled) "Enabled" else "Disabled",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isServiceEnabled) Secondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = isServiceEnabled,
                        onCheckedChange = { viewModel.setServiceEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Secondary,
                            checkedTrackColor = Secondary.copy(alpha = 0.34f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "Timing", subtitle = "How long the pause should last") {
            SettingRow(
                title = "Cooldown duration",
                subtitle = "$cooldown seconds before Continue becomes available"
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
                title = "Smart escalation",
                subtitle = "Longer waits as you reopen distracting apps more often",
                checked = escalation,
                onCheckedChange = { viewModel.setEscalationEnabled(it) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        SettingsSection(title = "Night mode", subtitle = "Add stronger friction in late hours") {
            ToggleRow(
                title = "Late night mode",
                subtitle = "Double cooldown from ${lateNightStart}:00 to ${lateNightEnd}:00",
                checked = lateNight,
                onCheckedChange = { viewModel.setLateNightMode(it) }
            )
            if (lateNight) {
                SettingRow(
                    title = "Start hour",
                    subtitle = "Night mode begins at ${lateNightStart}:00"
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
                    title = "End hour",
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
        }

        Spacer(modifier = Modifier.height(18.dp))

        SettingsSection(title = "Session monitoring", subtitle = "Check back in when a session stretches on") {
            ToggleRow(
                title = "Session nudge",
                subtitle = "Show a reminder after $sessionNudgeMinutes minutes of continuous use",
                checked = sessionNudge,
                onCheckedChange = { viewModel.setSessionNudgeEnabled(it) }
            )
            if (sessionNudge) {
                SettingRow(
                    title = "Nudge after",
                    subtitle = "$sessionNudgeMinutes minutes"
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
        }

        Spacer(modifier = Modifier.height(18.dp))

        SettingsSection(title = "Permissions", subtitle = "These keep the protection working reliably") {
            PermissionCard(
                title = "Accessibility service",
                description = "Detects when you open monitored apps",
                icon = Icons.Filled.Accessibility,
                isGranted = hasAccessibility,
                onGrant = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            PermissionCard(
                title = "Display over other apps",
                description = "Shows the mindful pause screen",
                icon = Icons.Filled.Layers,
                isGranted = hasOverlay,
                onGrant = {
                    context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            PermissionCard(
                title = "Usage access",
                description = "Tracks time spent inside monitored apps",
                icon = Icons.Filled.QueryStats,
                isGranted = hasUsageStats,
                onGrant = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Digital Detox v1.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(110.dp))
    }
}

@Composable
private fun SettingsSection(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
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
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
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
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
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
    @Suppress("DEPRECATION")
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
    } else {
        @Suppress("DEPRECATION")
        appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
    }
    return mode == AppOpsManager.MODE_ALLOWED
}
