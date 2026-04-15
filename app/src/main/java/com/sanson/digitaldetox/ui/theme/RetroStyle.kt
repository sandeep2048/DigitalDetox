package com.sanson.digitaldetox.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Synthwave dark gradient — subtle purple shift
val RetroBackgroundBrush: Brush
    get() = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F0E17),
            Color(0xFF13112A),
            Color(0xFF0F0E17)
        )
    )

