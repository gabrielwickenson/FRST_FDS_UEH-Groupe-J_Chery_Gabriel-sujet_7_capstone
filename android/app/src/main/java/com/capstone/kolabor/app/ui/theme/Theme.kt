package com.kolabor.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.capstone.serviceplatform.app.ui.theme.KolaborLightColorScheme

@Composable
fun KolaborTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KolaborLightColorScheme,
        typography = KolaborTypography,
        shapes = KolaborShapes,
        content = content
    )
}