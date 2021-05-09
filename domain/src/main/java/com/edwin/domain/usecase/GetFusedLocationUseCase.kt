package com.edwin.domain.usecase

import android.location.Location
import com.edwin.domain.WeatherRepository
import com.edwin.domain.util.flowWithResult
import kotlinx.coroutines.flow.Flow

class GetFusedLocationUseCase(private val weatherRepository: WeatherRepository) {

    fun invoke(): Flow<Result<Location>> = flowWithResult { weatherRepository.getFusedLocation() }
}