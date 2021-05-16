package com.edwin.data.entity.util

import com.edwin.data.entity.WeatherDTO
import com.edwin.domain.model.BriefWeatherInfo
import com.edwin.domain.model.WeatherDetails
import com.edwin.domain.model.WindDirection
import kotlin.math.roundToInt

fun WeatherDTO.toDomain(): WeatherDetails {
    return WeatherDetails(
        main[WeatherParameters.Temp.toLowerCase()]?.roundToInt() ?: 0,
        weather.first().icon,
        toBriefWeatherInfo(weather.first().description),
        weather.first().description,
        main[WeatherParameters.Humidity.toLowerCase()]?.toInt() ?: 0,
        wind[WeatherParameters.Speed.toLowerCase()] ?: 0.0,
        wind[WeatherParameters.Deg.toLowerCase()]?.toInt()
            ?.let { WindDirection.degreeToDirection(it) } ?: WindDirection.NORTH,
        main[WeatherParameters.Pressure.toLowerCase()] ?: 0.0
    )
}

private val String.asEnumValueName: String
    get() {
        return this.replace(' ', '_').uppercase()
    }

inline fun <reified T : Enum<T>> valueOf(type: String, default: T): T {
    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: IllegalArgumentException) {
        default
    }
}

private fun toBriefWeatherInfo(description: String) = valueOf(
    description.asEnumValueName,
    BriefWeatherInfo.DEFAULT
)