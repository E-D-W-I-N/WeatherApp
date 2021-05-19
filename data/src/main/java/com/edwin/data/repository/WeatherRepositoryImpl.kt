package com.edwin.data.repository

import android.location.Location
import com.edwin.data.BuildConfig
import com.edwin.data.device.GetLocationDataSource
import com.edwin.data.entity.util.toDomain
import com.edwin.data.network.GeocoderDataSource
import com.edwin.data.network.WeatherApiDataSource
import com.edwin.domain.WeatherRepository
import com.edwin.domain.exception.MapException
import com.edwin.domain.exception.WeatherException
import com.edwin.domain.model.WeatherDetails
import java.io.IOException
import java.net.UnknownHostException

class WeatherRepositoryImpl(
    private val weatherApiDataSource: WeatherApiDataSource,
    private val getLocationDataSource: GetLocationDataSource,
    private val geocoderDataSource: GeocoderDataSource
) : WeatherRepository {

    private val apiKey = BuildConfig.OPEN_WEATHER_MAP_API_KEY
    private val units = BuildConfig.UNITS

    override fun getWeatherResponse(
        latitude: Float,
        longitude: Float
    ): WeatherDetails? {
        return try {
            val weatherDetailsResponse = weatherApiDataSource.getWeatherResponse(
                latitude,
                longitude,
                apiKey,
                units
            ).execute()
            val weatherBody = weatherDetailsResponse.body() ?: throw WeatherException.NoWeatherData
            weatherBody.toDomain()
        } catch (e: UnknownHostException) {
            throw WeatherException.HostFailure
        }
    }

    override fun getFusedLocation(): Location =
        getLocationDataSource.getFusedLocation() ?: throw MapException.NoLastLocation

    override fun getCityName(
        latitude: Double,
        longitude: Double
    ): String {
        return try {
            geocoderDataSource.getCityNameFromGeocoder(latitude, longitude)
        } catch (e: Exception) {
            when (e) {
                is IOException -> throw MapException.GeocoderFailed
                else -> throw MapException.CityNotFound
            }
        }
    }
}