package com.edwin.domain.usecase

import android.location.Location
import com.edwin.domain.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetFusedLocationUseCase(private val weatherRepository: WeatherRepository) {

    fun getFusedLocation(): Flow<Result<Location>> {
        return flow {
            val fusedLocation = try {
                Result.success(weatherRepository.getFusedLocation())
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(fusedLocation)
        }.flowOn(Dispatchers.IO)
    }
}