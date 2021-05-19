package com.edwin.weatherapp.presentation.weatherDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.domain.model.WeatherDetails
import com.edwin.domain.usecase.UseCase
import com.edwin.domain.usecase.weather.GetWeatherDetailsUseCase.Params
import com.edwin.weatherapp.presentation.weatherDetails.model.WeatherAction
import com.edwin.weatherapp.presentation.weatherDetails.model.WeatherEvent
import com.edwin.weatherapp.presentation.weatherDetails.model.WeatherViewState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(
    private val getWeatherDetailsUseCase: UseCase<WeatherDetails?, Params>
) : ViewModel() {

    private val _viewStates = MutableStateFlow(WeatherViewState())
    val viewStates: StateFlow<WeatherViewState> = _viewStates.asStateFlow()

    private val _viewActions = Channel<WeatherAction>()
    val viewActions = _viewActions.receiveAsFlow()

    fun obtainEvent(viewEvent: WeatherEvent) {
        when (viewEvent) {
            is WeatherEvent.FetchWeather -> getWeatherDetails(
                viewEvent.latitude, viewEvent.longitude
            )
        }
    }

    private fun getWeatherDetails(latitude: Float, longitude: Float) = viewModelScope.launch {
        _viewStates.value = _viewStates.value.copy(isLoading = true)
        getWeatherDetailsUseCase(Params(latitude, longitude)).single()
            .onSuccess {
                _viewStates.value = _viewStates.value.copy(isLoading = false, weatherDetails = it)
                if (it?.briefWeatherInfo == null) {
                    _viewActions.send(WeatherAction.ShowNoImageSnackbar)
                }
            }
            .onFailure {
                _viewStates.value = _viewStates.value.copy(isLoading = false, error = it)
            }
    }
}