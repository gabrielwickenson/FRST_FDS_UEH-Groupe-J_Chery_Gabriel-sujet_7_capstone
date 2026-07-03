package com.capstone.kolabor.app.data.model

data class FilterOptions(
    val noteMin: Float = 0f,
    val priceMin: Double = 0.0,
    val priceMax: Double = 3000.0,
    val availability: String = "Tous" // "Tous", "Aujourd'hui", "Cette semaine"
)