package com.edwin.domain.model

data class WeatherDetails(
    val temperature: Int,
    val icon: String,
    val briefWeatherInfo: BriefWeatherInfo,
    val generalInfo: String,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: WindDirection,
    val pressure: Double
)