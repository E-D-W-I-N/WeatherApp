package com.edwin.domain.usecase.map

import android.location.Address
import com.edwin.domain.WeatherRepository
import com.edwin.domain.usecase.UseCase

class GetAddressFromGeocoderUseCase(private val weatherRepository: WeatherRepository) :
    UseCase<Address, GetAddressFromGeocoderUseCase.Params>() {

    override suspend fun run(params: Params): Address = weatherRepository.getAddress(
        params.latitude, params.longitude
    )

    data class Params(
        val latitude: Double,
        val longitude: Double
    )

}