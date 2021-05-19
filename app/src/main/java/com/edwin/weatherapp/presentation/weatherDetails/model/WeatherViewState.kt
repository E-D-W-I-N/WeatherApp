package com.edwin.weatherapp.presentation.weatherDetails.model

import com.edwin.domain.model.WeatherDetails

data class WeatherViewState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val weatherDetails: WeatherDetails? = null
)