package com.sanson.digitaldetox.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════
// SYNTHWAVE RETRO — Gen-Z arcade palette
// Dark mode + neon accents = retro font feels premium
// Psychology: deep dark = focus/calm, neon = dopamine hit
// ══════════════════════════════════════════════════════════

// Core darks — OLED-friendly deep indigo-black
val DeepVoid    = Color(0xFF0F0E17)   // background — near-black purple
val DarkIndigo  = Color(0xFF1A1A2E)   // surface — card bg
val MidIndigo   = Color(0xFF232946)   // surface variant — elevated cards

// Neon accents — the dopamine colors
val NeonCyan    = Color(0xFF00F5D4)   // primary — borders, highlights, pixel glow
val HotPink     = Color(0xFFF72585)   // secondary — CTA, important actions
val NeonYellow  = Color(0xFFFFD166)   // tertiary — warnings, counters, attention
val ElecPurple  = Color(0xFF7B61FF)   // extra — selected states, active indicators

// Text — high contrast for readability
val BrightWhite = Color(0xFFFFFFFE)   // primary text on dark
val CoolLav     = Color(0xFFA7A9BE)   // secondary/muted text
val SoftCyan    = Color(0xFF94F3E4)   // hint text (softer cyan)

// ── Material token mappings ──

val Primary          = NeonCyan
val PrimaryLight     = SoftCyan
val PrimaryDark      = Color(0xFF00C4A8)
val PrimaryContainer = Color(0xFF0A2E28)
val OnPrimary        = DeepVoid
val OnPrimaryContainer = NeonCyan

val Secondary          = HotPink
val SecondaryLight     = Color(0xFFFF6EB4)
val SecondaryDark      = Color(0xFFC41E6A)
val SecondaryContainer = Color(0xFF2E0A1E)
val OnSecondary        = BrightWhite
val OnSecondaryContainer = HotPink

val Tertiary            = NeonYellow
val TertiaryContainer   = Color(0xFF2E2508)
val OnTertiary          = DeepVoid
val OnTertiaryContainer = NeonYellow

val ErrorColor      = Color(0xFFFF6B6B)
val ErrorContainer  = Color(0xFF2E1212)
val OnError         = BrightWhite
val OnErrorContainer = Color(0xFFFFCDD2)

// Surfaces
val Background       = DeepVoid
val Surface          = DarkIndigo
val SurfaceVariant   = MidIndigo
val SurfaceHigh      = Color(0xFF2D2B55)
val SurfaceBright    = Color(0xFF3A3768)
val OnBackground     = BrightWhite
val OnSurface        = BrightWhite
val OnSurfaceVariant = CoolLav
val Outline          = NeonCyan.copy(alpha = 0.4f)
val OutlineVariant   = MidIndigo

// Light scheme (unused — always dark for this aesthetic)
val LightBackground       = DeepVoid
val LightSurface          = DarkIndigo
val LightSurfaceVariant   = MidIndigo
val LightSurfaceHigh      = SurfaceHigh
val LightOnBackground     = BrightWhite
val LightOnSurface        = BrightWhite
val LightOnSurfaceVariant = CoolLav
val LightOutline          = NeonCyan.copy(alpha = 0.4f)

val LightPrimaryContainer   = PrimaryContainer
val LightSecondaryContainer = SecondaryContainer
val LightTertiaryContainer  = TertiaryContainer
val LightErrorContainer     = ErrorContainer

// Overlay
val OverlayBackground = DeepVoid
val OverlayCard       = DarkIndigo
