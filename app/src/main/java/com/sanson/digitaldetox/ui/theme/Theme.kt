package com.sanson.digitaldetox.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Game Boy has ONE look — always the same 4-shade LCD palette
private val GameBoyScheme = lightColorScheme(
    primary = GBDarkest,
    onPrimary = GBLightest,
    primaryContainer = GBDark,
    onPrimaryContainer = GBLightest,
    secondary = GBDark,
    onSecondary = GBLightest,
    secondaryContainer = GBLight,
    onSecondaryContainer = GBDarkest,
    tertiary = GBDarkest,
    onTertiary = GBLightest,
    tertiaryContainer = GBDark,
    onTertiaryContainer = GBLightest,
    error = ErrorColor,
    onError = GBLightest,
    errorContainer = GBLight,
    onErrorContainer = GBDarkest,
    background = GBLightest,
    onBackground = GBDarkest,
    surface = GBLight,
    onSurface = GBDarkest,
    surfaceVariant = GBLight,
    onSurfaceVariant = GBDark,
    outline = GBDark,
    outlineVariant = GBDark.copy(alpha = 0.5f)
)

@Composable
fun DigitalDetoxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Always Game Boy — ignore system dark/light
    MaterialTheme(
        colorScheme = GameBoyScheme,
        typography = Typography,
        content = content
    )
}
