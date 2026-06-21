package com.magics.puffer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using system default sans-serif (Roboto on Android) for maximum compatibility.
// For production, add Inter or Nunito font files to res/font/ and reference here.
val PufferFontFamily = FontFamily.Default

val PufferTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        color = PufferWhite
    ),
    displayMedium = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        color = PufferWhite
    ),
    headlineLarge = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        color = PufferWhite
    ),
    headlineMedium = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        color = PufferWhite
    ),
    headlineSmall = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        color = PufferWhite
    ),
    titleLarge = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = PufferWhite
    ),
    titleMedium = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        color = PufferWhite
    ),
    titleSmall = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = PufferWhite
    ),
    bodyLarge = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = PufferWhite
    ),
    bodyMedium = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = PufferGray
    ),
    bodySmall = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        color = PufferGray
    ),
    labelLarge = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = PufferWhite
    ),
    labelMedium = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = PufferGray
    ),
    labelSmall = TextStyle(
        fontFamily = PufferFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = PufferGray
    )
)