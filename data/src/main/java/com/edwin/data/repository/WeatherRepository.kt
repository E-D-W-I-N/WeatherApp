package com.edwin.data.repository

import com.edwin.data.BuildConfig
import com.edwin.data.network.WeatherApi
import com.edwin.data.network.response.WeatherDTO
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {

    private val apiKey = BuildConfig.OPEN_WEATHER_MAP_API_KEY
    private val units = "metric"

    fun getWeatherResponse(query: String): WeatherDTO? {
        return weatherApi.getWeatherResponse(query, apiKey, units).execute().body()
    }
}