package com.edwin.weatherapp.presentation.weatherDetails.model

sealed class WeatherAction {
    object ShowNoImageSnackbar : WeatherAction()
}