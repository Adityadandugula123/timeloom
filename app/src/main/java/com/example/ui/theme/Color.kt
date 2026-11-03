package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Time Loom - Strict Data Intelligence Color Palette
val DarkNavy = Color(0xFF101827)       // Primary background (strictly #101827)
val DarkSlate = Color(0xFF182235)      // Secondary background (strictly #182235)
val CardCharcoal = Color(0xFF1E293B)   // Card background (slightly lighter charcoal #1E293B)
val CardCharcoalElevated = Color(0xFF243248) // Elevated card surface
val CardBorder = Color(0xFF2B3D58)     // Subtle border
val CardBorderGlow = Color(0xFF3B5274) // Focused border

val AccentBlue = Color(0xFF4285F4)     // Primary accent (strictly #4285F4)
val AccentCyan = Color(0xFF22C7D8)     // Secondary accent (strictly #22C7D8)
val AccentCyanLight = Color(0xFF67E8F9) // Cyan highlight
val AccentCyanDark = Color(0xFF0F4C5C) // Cyan muted

val SuccessGreen = Color(0xFF10B981)   // Success (#10B981)
val SuccessGreenGlow = Color(0xFF34D399) // Success bright
val WarningAmber = Color(0xFFF59E0B)   // Warning (#F59E0B)
val DangerRose = Color(0xFFEF4444)     // Danger
val InfoPurple = Color(0xFF8B5CF6)     // Priority / Category info
val CategoryPink = Color(0xFFEC4899)   // Category highlight

val TextPrimary = Color(0xFFFFFFFF)    // Primary text: White
val TextSecondary = Color(0xFF94A3B8)  // Secondary text: Muted grey
val TextMuted = Color(0xFF64748B)      // Sub-label text

// Heatmap Intensity Colors (Levels 0 to 4)
val HeatmapLevel0 = Color(0xFF182235)  // No activity (Dark Slate #182235)
val HeatmapLevel1 = Color(0xFF0F4C5C)  // 1-30 mins
val HeatmapLevel2 = Color(0xFF0284C7)  // 31-60 mins
val HeatmapLevel3 = Color(0xFF4285F4)  // 61-120 mins (Accent Blue #4285F4)
val HeatmapLevel4 = Color(0xFF22C7D8)  // >120 mins (Accent Cyan #22C7D8)

// Gradient Brushes strictly using #22C7D8 and #4285F4
val CyanBlueGlowBrush = Brush.linearGradient(
    colors = listOf(AccentCyan, AccentBlue)
)

val CardGlowBorderBrush = Brush.linearGradient(
    colors = listOf(AccentCyan.copy(alpha = 0.5f), Color.Transparent, AccentBlue.copy(alpha = 0.4f))
)

val HeroCardGradient = Brush.radialGradient(
    colors = listOf(Color(0xFF1C2C47), CardCharcoal),
    radius = 600f
)
