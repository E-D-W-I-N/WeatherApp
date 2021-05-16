package com.edwin.data.entity.util

enum class WeatherParameters {
    Temp,
    Humidity,
    Speed,
    Deg,
    Pressure, ;

    fun toLowerCase(): String {
        return name.lowercase()
    }
}