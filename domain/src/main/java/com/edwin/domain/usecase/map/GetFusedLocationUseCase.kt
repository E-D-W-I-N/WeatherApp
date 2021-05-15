package com.edwin.domain.usecase.map

import android.location.Location
import com.edwin.domain.WeatherRepository
import com.edwin.domain.usecase.UseCase

class GetFusedLocationUseCase(private val weatherRepository: WeatherRepository) :
    UseCase<Location, Unit>() {

    override suspend fun run(params: Unit): Location = weatherRepository.getFusedLocation()
}