package com.edwin.domain.exception

sealed class WeatherException : Exception() {

    object NoWeatherData : WeatherException()
    object HostFailure : WeatherException()
}