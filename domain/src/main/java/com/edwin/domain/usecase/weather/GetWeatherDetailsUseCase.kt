package com.edwin.domain.usecase.weather

import com.edwin.domain.WeatherRepository
import com.edwin.domain.model.WeatherDetails
import com.edwin.domain.usecase.UseCase

class GetWeatherDetailsUseCase(private val weatherRepository: WeatherRepository) :
    UseCase<WeatherDetails?, GetWeatherDetailsUseCase.Params>() {

    override suspend fun run(params: Params): WeatherDetails? =
        weatherRepository.getWeatherResponse(
            params.latitude, params.longitude
        )

    data class Params(
        val latitude: Float,
        val longitude: Float
    )

}