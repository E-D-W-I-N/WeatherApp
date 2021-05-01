package com.edwin.weatherapp.feature.weatherDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.edwin.domain.usecase.GetWeatherDetailsUseCase

class WeatherDetailsViewModel(
    private val getWeatherDetailsUseCase: GetWeatherDetailsUseCase
) : ViewModel() {

    fun getWeatherDetails(
        cityName: String
    ) = getWeatherDetailsUseCase.getWeatherDetails(cityName).asLiveData()
}