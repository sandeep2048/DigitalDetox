package com.sanson.digitaldetox.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Game Boy DMG LCD background — olive-yellow green, no gradient
val RetroBackgroundBrush: Brush
    get() = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF9BBC0F),
            Color(0xFF9BBC0F)
        )
    )

