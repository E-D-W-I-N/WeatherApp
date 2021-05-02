package com.edwin.domain.usecase

import android.location.Location
import com.edwin.domain.DataResult
import com.edwin.domain.WeatherRepository

class GetFusedLocationUseCase(private val weatherRepository: WeatherRepository) {

    fun getFusedLocation(): DataResult<Location> = weatherRepository.getFusedLocation()
}