package com.edwin.domain.usecase

import com.edwin.data.repository.WeatherRepository
import com.edwin.domain.model.WeatherDetails
import com.edwin.domain.util.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetWeatherDetailsUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    fun getWeatherDetails(query: String): Flow<Result<WeatherDetails?>> {
        return flow {
            val weatherDetails = try {
                Result.success(weatherRepository.getWeatherResponse(query)?.toDomain())
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(weatherDetails)
        }.flowOn(Dispatchers.IO)
    }
}