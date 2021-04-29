package com.edwin.weatherapp.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.edwin.domain.usecase.GetWeatherDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    getWeatherDetailsUseCase: GetWeatherDetailsUseCase
) : ViewModel() {
    val weatherDetails = getWeatherDetailsUseCase.getWeatherDetails("Tomsk").asLiveData()
}