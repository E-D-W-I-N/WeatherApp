package com.edwin.domain.model

data class WeatherDetails(
    val temperature: Double,
    val icon: String,
    val generalInfo: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double
)