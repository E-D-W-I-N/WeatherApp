package com.edwin.domain.usecase.map

import com.edwin.domain.WeatherRepository
import com.edwin.domain.usecase.UseCase

class GetCityNameFromGeocoderUseCase(private val weatherRepository: WeatherRepository) :
    UseCase<String, GetCityNameFromGeocoderUseCase.Params> {

    override suspend fun run(params: Params): String = weatherRepository.getCityName(
        params.latitude, params.longitude
    )

    data class Params(
        val latitude: Double,
        val longitude: Double
    )

}