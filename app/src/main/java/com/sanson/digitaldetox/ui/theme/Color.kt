package com.sanson.digitaldetox.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════
// GAME BOY DMG — authentic 4-shade LCD palette
// ══════════════════════════════════════════════════
val GBLightest = Color(0xFF9BBC0F)   // LCD background
val GBLight    = Color(0xFF8BAC0F)   // lighter elements
val GBDark     = Color(0xFF306230)   // dark UI elements
val GBDarkest  = Color(0xFF0F380F)   // text / pixel ink

// Mapped to Material token names for theme wiring
val Primary          = GBDarkest
val PrimaryLight     = GBDark
val PrimaryDark      = GBDarkest
val PrimaryContainer = GBDark
val OnPrimary        = GBLightest
val OnPrimaryContainer = GBLightest

val Secondary          = GBDark
val SecondaryLight     = GBLight
val SecondaryDark      = GBDarkest
val SecondaryContainer = GBDark
val OnSecondary        = GBLightest
val OnSecondaryContainer = GBLightest

val Tertiary            = GBDarkest
val TertiaryContainer   = GBDark
val OnTertiary          = GBLightest
val OnTertiaryContainer = GBLightest

val ErrorColor      = Color(0xFF5A1010)  // muted dark red on LCD
val ErrorContainer  = GBLight
val OnError         = GBLightest
val OnErrorContainer = GBDarkest

// Surfaces — LCD green shades
val Background       = GBLightest
val Surface          = GBLight
val SurfaceVariant   = GBLight
val SurfaceHigh      = GBDark
val SurfaceBright    = GBLightest
val OnBackground     = GBDarkest
val OnSurface        = GBDarkest
val OnSurfaceVariant = GBDark
val Outline          = GBDark
val OutlineVariant   = GBDark.copy(alpha = 0.5f)

// Light scheme (same for this monochrome theme)
val LightBackground       = GBLightest
val LightSurface          = GBLight
val LightSurfaceVariant   = GBLight
val LightSurfaceHigh      = GBDark
val LightOnBackground     = GBDarkest
val LightOnSurface        = GBDarkest
val LightOnSurfaceVariant = GBDark
val LightOutline          = GBDark

val LightPrimaryContainer   = GBLight
val LightSecondaryContainer = GBLight
val LightTertiaryContainer  = GBLight
val LightErrorContainer     = GBLight

// Overlay
val OverlayBackground = GBLightest
val OverlayCard       = GBLight
