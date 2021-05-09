package com.edwin.domain.usecase

import com.edwin.domain.WeatherRepository
import com.edwin.domain.model.WeatherDetails
import com.edwin.domain.util.flowWithResult
import kotlinx.coroutines.flow.Flow

class GetWeatherDetailsUseCase(private val weatherRepository: WeatherRepository) {

    fun invoke(
        latitude: Float,
        longitude: Float
    ): Flow<Result<WeatherDetails?>> = flowWithResult {
        weatherRepository.getWeatherResponse(latitude, longitude)
    }
}