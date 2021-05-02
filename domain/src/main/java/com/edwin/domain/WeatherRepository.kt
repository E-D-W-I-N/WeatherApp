package com.edwin.domain

import android.location.Address
import android.location.Location
import com.edwin.domain.model.WeatherDetails

interface WeatherRepository {

    fun getWeatherResponse(latitude: Float, longitude: Float): DataResult<WeatherDetails?>

    fun getFusedLocation(): DataResult<Location>

    fun getAddress(latitude: Double, longitude: Double): DataResult<Address>
}