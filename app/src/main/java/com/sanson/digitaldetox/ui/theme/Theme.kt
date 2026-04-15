package com.sanson.digitaldetox.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Synthwave — always dark, neon accents on OLED black
private val SynthwaveScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = DeepVoid,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = NeonCyan,
    secondary = HotPink,
    onSecondary = BrightWhite,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = HotPink,
    tertiary = NeonYellow,
    onTertiary = DeepVoid,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = NeonYellow,
    error = ErrorColor,
    onError = BrightWhite,
    errorContainer = ErrorContainer,
    onErrorContainer = Color(0xFFFFCDD2),
    background = DeepVoid,
    onBackground = BrightWhite,
    surface = DarkIndigo,
    onSurface = BrightWhite,
    surfaceVariant = MidIndigo,
    onSurfaceVariant = CoolLav,
    outline = NeonCyan.copy(alpha = 0.4f),
    outlineVariant = MidIndigo
)

@Composable
fun DigitalDetoxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Always synthwave dark — this IS the brand
    MaterialTheme(
        colorScheme = SynthwaveScheme,
        typography = Typography,
        content = content
    )
}
