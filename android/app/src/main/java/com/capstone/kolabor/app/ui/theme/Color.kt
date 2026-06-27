package com.capstone.serviceplatform.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme


//  Couleurs de marque (Navy + Green)
val NavyPrimary = Color(0xFF19355F)
val NavyDark = Color(0xFF122847)
val NavyDarker = Color(0xFF0D1D34)
val NavyDarkest = Color(0xFF060F1A)
val NavyLight = Color(0xFF4A6387)
val NavyLighter = Color(0xFFA3B1C7)
val NavyLightest = Color(0xFFDCE3EC)

val GreenPrimary = Color(0xFF139356)
val GreenDark = Color(0xFF0F7A48)
val GreenDarker = Color(0xFF0B5C36)
val GreenDarkest = Color(0xFF063A22)
val GreenLight = Color(0xFF4CB57F)
val GreenLighter = Color(0xFF8DD3AF)
val GreenLightest = Color(0xFFD8F3E4)

//  Sémantique
val Success = Color(0xFF22C55E)
val ErrorColor = Color(0xFFDC2626)
val Warning = Color(0xFFF59E0B)
val Info = Color(0xFF2563EB)

//  Neutres / Gris
val Gray900 = Color(0xFF111827)
val Gray800 = Color(0xFF1F2937)
val Gray700 = Color(0xFF374151)
val Gray600 = Color(0xFF4B5563)
val Gray500 = Color(0xFF6B7280)
val Gray400 = Color(0xFF9CA3AF)
val Gray300 = Color(0xFFD1D5DB)
val Gray200 = Color(0xFFE5E7EB)
val Gray100 = Color(0xFFF3F4F6)
val Gray50 = Color(0xFFF9FAFB)

//  Matériau 3 – Schéma de couleurs
val KolaborLightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = NavyLightest,
    onPrimaryContainer = NavyDark,
    secondary = GreenPrimary,
    onSecondary = Color.White,
    secondaryContainer = GreenLightest,
    onSecondaryContainer = GreenDark,
    background = Gray50,
    onBackground = Gray900,
    surface = Color.White,
    onSurface = Gray900,
    error = ErrorColor,
    onError = Color.White,
    errorContainer = Color(0xFFFFD6D6),
    onErrorContainer = ErrorColor
)