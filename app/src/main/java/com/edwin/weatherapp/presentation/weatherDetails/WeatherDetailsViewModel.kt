package com.edwin.weatherapp.presentation.weatherDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.domain.model.WeatherDetails
import com.edwin.domain.usecase.UseCase
import com.edwin.domain.usecase.weather.GetWeatherDetailsUseCase.Params
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(
    private val getWeatherDetailsUseCase: UseCase<WeatherDetails?, Params>
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Default)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun getWeatherDetails(latitude: Float, longitude: Float) = viewModelScope.launch {
        _uiState.value = WeatherUiState.Loading
        getWeatherDetailsUseCase(Params(latitude, longitude)).single()
            .onSuccess { _uiState.value = WeatherUiState.WeatherDetailsLoaded(it) }
            .onFailure { _uiState.value = WeatherUiState.Error(it) }
    }

    sealed class WeatherUiState {
        object Loading : WeatherUiState()
        object Default : WeatherUiState()
        data class Error(val throwable: Throwable) : WeatherUiState()
        data class WeatherDetailsLoaded(val weatherDetails: WeatherDetails?) : WeatherUiState()
    }
}