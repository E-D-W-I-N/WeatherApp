package com.edwin.domain.usecase

import com.edwin.domain.DataResult
import com.edwin.domain.WeatherRepository
import com.edwin.domain.model.WeatherDetails

class GetWeatherDetailsUseCase(private val weatherRepository: WeatherRepository) {

    fun getWeatherDetails(
        latitude: Float,
        longitude: Float
    ): DataResult<WeatherDetails?> = weatherRepository.getWeatherResponse(latitude, longitude)
}