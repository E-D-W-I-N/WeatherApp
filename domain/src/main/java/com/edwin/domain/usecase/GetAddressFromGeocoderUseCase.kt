package com.edwin.domain.usecase

import android.location.Address
import com.edwin.domain.WeatherRepository
import com.edwin.domain.util.flowWithResult
import kotlinx.coroutines.flow.Flow

class GetAddressFromGeocoderUseCase(private val weatherRepository: WeatherRepository) {

    fun invoke(
        latitude: Double,
        longitude: Double
    ): Flow<Result<Address>> = flowWithResult { weatherRepository.getAddress(latitude, longitude) }
}