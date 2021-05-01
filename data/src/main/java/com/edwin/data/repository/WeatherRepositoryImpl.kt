package com.edwin.data.repository

import android.location.Location
import com.edwin.data.BuildConfig
import com.edwin.data.device.GetLocation
import com.edwin.data.entity.util.toDomain
import com.edwin.data.network.WeatherApi
import com.edwin.domain.WeatherRepository
import com.edwin.domain.model.WeatherDetails

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApi,
    private val getLocation: GetLocation
) : WeatherRepository {

    private val apiKey = BuildConfig.OPEN_WEATHER_MAP_API_KEY
    private val units = "metric"

    override fun getWeatherResponse(query: String): WeatherDetails? {
        val weatherDTO = weatherApi.getWeatherResponse(query, apiKey, units).execute().body()
        return weatherDTO?.toDomain()
    }

    override fun getFusedLocation(): Location = getLocation.getFusedLocation()
}