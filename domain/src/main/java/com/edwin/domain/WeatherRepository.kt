package com.edwin.domain

import android.location.Location
import com.edwin.domain.model.WeatherDetails

interface WeatherRepository {

    fun getWeatherResponse(query: String): WeatherDetails?

    fun getFusedLocation(): Location
}