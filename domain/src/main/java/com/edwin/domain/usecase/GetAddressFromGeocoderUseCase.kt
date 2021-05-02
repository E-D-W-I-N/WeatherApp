package com.edwin.domain.usecase

import android.location.Address
import com.edwin.domain.DataResult
import com.edwin.domain.WeatherRepository

class GetAddressFromGeocoderUseCase(private val weatherRepository: WeatherRepository) {

    fun getAddress(
        latitude: Double,
        longitude: Double
    ): DataResult<Address> = weatherRepository.getAddress(latitude, longitude)
}