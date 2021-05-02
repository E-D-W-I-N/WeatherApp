package com.edwin.data.repository

import android.location.Address
import android.location.Location
import com.edwin.data.BuildConfig
import com.edwin.data.device.GetLocationDataSource
import com.edwin.data.entity.util.toDomain
import com.edwin.data.network.GeocoderDataSource
import com.edwin.data.network.WeatherApiDataSource
import com.edwin.domain.DataResult
import com.edwin.domain.WeatherRepository
import com.edwin.domain.model.WeatherDetails

class WeatherRepositoryImpl(
    private val weatherApiDataSource: WeatherApiDataSource,
    private val getLocationDataSource: GetLocationDataSource,
    private val geocoderDataSource: GeocoderDataSource
) : WeatherRepository {

    private val apiKey = BuildConfig.OPEN_WEATHER_MAP_API_KEY
    private val units = "metric"

    override fun getWeatherResponse(
        latitude: Float,
        longitude: Float
    ): DataResult<WeatherDetails?> {
        return try {
            val weatherDetailsCall =
                weatherApiDataSource.getWeatherResponse(latitude, longitude, apiKey, units)
            DataResult.Success(
                weatherDetailsCall.execute().body()?.toDomain()
            )
        } catch (e: Exception) {
            DataResult.Error("Can't reach server. Check your Internet connection")
        }
    }

    override fun getFusedLocation(): DataResult<Location> {
        return try {
            DataResult.Success(
                getLocationDataSource.getFusedLocation()
            )
        } catch (e: Exception) {
            DataResult.Error("Can't get your current location")
        }
    }

    override fun getAddress(
        latitude: Double,
        longitude: Double
    ): DataResult<Address> {
        return try {
            DataResult.Success(
                geocoderDataSource.getAddressFromGeocoder(latitude, longitude)
            )
        } catch (e: Exception) {
            DataResult.Error("No city here")
        }
    }
}