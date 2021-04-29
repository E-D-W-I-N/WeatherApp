package com.edwin.weatherapp.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.edwin.domain.usecase.GetWeatherDetailsUseCase

class MapViewModel(getWeatherDetailsUseCase: GetWeatherDetailsUseCase) : ViewModel() {

    val weatherDetails = getWeatherDetailsUseCase.getWeatherDetails("Tomsk").asLiveData()
}