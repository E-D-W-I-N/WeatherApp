package com.edwin.domain

import com.edwin.domain.model.WeatherDetails

interface WeatherRepository {

    fun getWeatherResponse(query: String): WeatherDetails?
}