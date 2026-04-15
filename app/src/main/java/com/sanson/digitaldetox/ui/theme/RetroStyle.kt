package com.sanson.digitaldetox.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val RetroBackgroundBrush: Brush
    get() = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF060C09),
            Color(0xFF0E1E14),
            Color(0xFF060C09)
        )
    )

