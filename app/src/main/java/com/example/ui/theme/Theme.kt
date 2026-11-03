package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = DarkNavy,
    primaryContainer = AccentCyanDark,
    onPrimaryContainer = TextPrimary,
    secondary = AccentBlue,
    onSecondary = TextPrimary,
    secondaryContainer = DarkSlate,
    onSecondaryContainer = TextPrimary,
    tertiary = WarningAmber,
    background = DarkNavy,
    onBackground = TextPrimary,
    surface = CardCharcoal,
    onSurface = TextPrimary,
    surfaceVariant = DarkSlate,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = DangerRose,
    onError = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    secondary = AccentCyan,
    onSecondary = DarkNavy,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun TimeLoomTheme(
    darkTheme: Boolean = true, // Default to Dark Theme as per Data Intelligence specifications
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
