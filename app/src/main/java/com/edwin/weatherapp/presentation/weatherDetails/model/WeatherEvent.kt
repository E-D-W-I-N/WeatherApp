package com.edwin.weatherapp.presentation.weatherDetails.model

sealed class WeatherEvent {
    data class FetchWeather(val latitude: Float, val longitude: Float) : WeatherEvent()
}