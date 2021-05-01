package com.edwin.data.entity.util

import com.edwin.data.entity.WeatherDTO
import com.edwin.domain.model.WeatherDetails

fun WeatherDTO.toDomain(): WeatherDetails {
    return WeatherDetails(
        temperature = main["temp"] ?: 0.0,
        icon = weather.first().icon,
        generalInfo = weather.first().description,
        humidity = main["humidity"]?.toInt() ?: 0,
        windSpeed = wind["speed"] ?: 0.0,
        airPressure = main["pressure"] ?: 0.0
    )
}