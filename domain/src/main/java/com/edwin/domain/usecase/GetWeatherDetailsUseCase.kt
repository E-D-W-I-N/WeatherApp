package com.edwin.domain.usecase

import com.edwin.domain.WeatherRepository
import com.edwin.domain.model.WeatherDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetWeatherDetailsUseCase(private val weatherRepository: WeatherRepository) {

    fun getWeatherDetails(query: String): Flow<Result<WeatherDetails?>> {
        return flow {
            val weatherDetails = try {
                Result.success(weatherRepository.getWeatherResponse(query))
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(weatherDetails)
        }.flowOn(Dispatchers.IO)
    }
}