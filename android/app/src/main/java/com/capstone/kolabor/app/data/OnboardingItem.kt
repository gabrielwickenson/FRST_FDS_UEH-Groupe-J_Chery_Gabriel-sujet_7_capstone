package com.capstone.kolabor.app.data

import androidx.compose.ui.graphics.vector.ImageVector

data class OnboardingItem(
    val title: String,
    val description: String,
    val icon: ImageVector // Ou Painter pour des SVG
)
