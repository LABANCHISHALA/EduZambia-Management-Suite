package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color.Black,
    primaryContainer = ZambiaGreenDark,
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = ZambiaOrange,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF6B3600),
    onSecondaryContainer = ZambiaOrangeLight,
    tertiary = Color(0xFFFF5252),
    background = Color(0xFF121413),
    surface = Color(0xFF1E211F),
    onBackground = Color(0xFFE2E3E0),
    onSurface = Color(0xFFE2E3E0),
)

private val LightColorScheme = lightColorScheme(
    primary = ZambiaGreen,
    onPrimary = Color.White,
    primaryContainer = ZambiaGreenContainer,
    onPrimaryContainer = ZambiaGreenDark,
    secondary = ZambiaOrange,
    onSecondary = Color.White,
    secondaryContainer = ZambiaOrangeContainer,
    onSecondaryContainer = Color(0xFF4E2600),
    tertiary = ZambiaRed,
    onTertiary = Color.White,
    tertiaryContainer = ZambiaRedLight,
    onTertiaryContainer = Color(0xFF5A000A),
    background = SurfaceLight,
    surface = Color.White,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = CardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep Zambian Flag palette default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
