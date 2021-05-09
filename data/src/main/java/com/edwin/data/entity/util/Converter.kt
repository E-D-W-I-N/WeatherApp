package com.edwin.data.entity.util

import com.edwin.data.entity.WeatherDTO
import com.edwin.domain.model.BriefWeatherInfo
import com.edwin.domain.model.WeatherDetails
import com.edwin.domain.model.WindDirection
import kotlin.math.roundToInt

fun WeatherDTO.toDomain(): WeatherDetails {
    return WeatherDetails(
        main["temp"]?.roundToInt() ?: 0,
        weather.first().icon,
        toBriefWeatherInfo(weather.first().description),
        weather.first().description,
        main["humidity"]?.toInt() ?: 0,
        wind["speed"] ?: 0.0,
        wind["deg"]?.toInt()?.let { WindDirection.degreeToDirection(it) } ?: WindDirection.NORTH,
        main["pressure"] ?: 0.0
    )
}

private fun toBriefWeatherInfo(description: String) = when (description) {
    "scattered clouds" -> BriefWeatherInfo.SCATTERED_CLOUDS
    "clear sky" -> BriefWeatherInfo.CLEAR_SKY
    "rain" -> BriefWeatherInfo.RAIN
    "thunderstorm" -> BriefWeatherInfo.THUNDERSTORM
    "few clouds" -> BriefWeatherInfo.FEW_CLOUDS
    "broken clouds" -> BriefWeatherInfo.BROKEN_CLOUDS
    "shower rain" -> BriefWeatherInfo.SHOWER_RAIN
    "snow" -> BriefWeatherInfo.SNOW
    "mist" -> BriefWeatherInfo.MIST
    else -> BriefWeatherInfo.DEFAULT
}