package com.edwin.weatherapp.presentation.weatherDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.domain.DataResult
import com.edwin.domain.model.WeatherDetails
import com.edwin.domain.usecase.GetWeatherDetailsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(
    private val getWeatherDetailsUseCase: GetWeatherDetailsUseCase
) : ViewModel() {

    private val _weatherDetails = MutableStateFlow<DataResult<WeatherDetails?>>(DataResult.Empty)
    val weatherDetails: StateFlow<DataResult<WeatherDetails?>> = _weatherDetails.asStateFlow()

    fun getWeatherDetails(latitude: Float, longitude: Float) =
        viewModelScope.launch(Dispatchers.IO) {
            _weatherDetails.value = DataResult.Loading
            _weatherDetails.value = getWeatherDetailsUseCase.getWeatherDetails(latitude, longitude)
        }
}