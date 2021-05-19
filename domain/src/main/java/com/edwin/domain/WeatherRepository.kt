package com.edwin.domain

import android.location.Location
import com.edwin.domain.model.WeatherDetails

interface WeatherRepository {

    fun getWeatherResponse(latitude: Float, longitude: Float): WeatherDetails?

    fun getFusedLocation(): Location

    fun getCityName(latitude: Double, longitude: Double): String
}