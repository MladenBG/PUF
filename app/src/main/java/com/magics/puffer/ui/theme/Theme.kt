package com.magics.puffer.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Material 3 color scheme for Puffer — dark theme only.
 * Built around deep navy background and electric violet primary.
 */
private val PufferDarkColorScheme = darkColorScheme(
    primary          = PufferViolet,
    onPrimary        = Color.White,
    primaryContainer = PufferVioletGlow,
    onPrimaryContainer = PufferWhite,

    secondary        = PufferTeal,
    onSecondary      = Color.Black,
    secondaryContainer = PufferTealGlow,
    onSecondaryContainer = PufferWhite,

    tertiary         = PufferGold,
    onTertiary       = Color.Black,
    tertiaryContainer = PufferGoldGlow,
    onTertiaryContainer = PufferWhite,

    background       = PufferDeepNavy,
    onBackground     = PufferWhite,

    surface          = PufferDarkCard,
    onSurface        = PufferWhite,
    surfaceVariant   = PufferDarkerCard,
    onSurfaceVariant = PufferGray,

    outline          = PufferBorderGray,

    error            = PufferRed,
    onError          = Color.White
)

@Composable
fun PufferTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PufferDarkColorScheme,
        typography  = PufferTypography,
        content     = content
    )
}