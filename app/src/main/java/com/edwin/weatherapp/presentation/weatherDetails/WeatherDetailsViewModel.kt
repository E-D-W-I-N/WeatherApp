package com.edwin.weatherapp.presentation.weatherDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.domain.model.WeatherDetails
import com.edwin.domain.usecase.GetWeatherDetailsUseCase
import com.edwin.weatherapp.util.asLiveData
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(
    private val getWeatherDetailsUseCase: GetWeatherDetailsUseCase
) : ViewModel() {

    private val _weatherDetails = MutableLiveData<Result<WeatherDetails?>>()
    val weatherDetails: LiveData<Result<WeatherDetails?>> = _weatherDetails.asLiveData()

    fun getWeatherDetails(latitude: Float, longitude: Float) = viewModelScope.launch {
        _weatherDetails.value = getWeatherDetailsUseCase.invoke(latitude, longitude).single()
    }
}