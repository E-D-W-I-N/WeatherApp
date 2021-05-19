package com.edwin.weatherapp.presentation.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.domain.usecase.UseCase
import com.edwin.domain.usecase.map.GetCityNameFromGeocoderUseCase.Params
import com.edwin.weatherapp.presentation.map.model.MapAction
import com.edwin.weatherapp.presentation.map.model.MapEvent
import com.edwin.weatherapp.presentation.map.model.MapViewState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MapViewModel(
    private val getFusedLocationUseCase: UseCase<Location, Unit>,
    private val getCityNameFromGeocoderUseCase: UseCase<String, Params>
) : ViewModel() {

    private val _viewStates = MutableStateFlow(MapViewState())
    val viewStates: StateFlow<MapViewState> = _viewStates.asStateFlow()

    private val _viewActions = Channel<MapAction>()
    val viewActions = _viewActions.receiveAsFlow()

    fun obtainEvent(viewEvent: MapEvent) {
        when (viewEvent) {
            is MapEvent.GetFusedLocation -> getFusedLocation()
            is MapEvent.GetCityName -> getCityName(viewEvent.latitude, viewEvent.longitude)
            is MapEvent.SetClickPosition -> setClickPosition(viewEvent.latLng)
        }
    }

    private fun getFusedLocation() = viewModelScope.launch {
        _viewStates.value = _viewStates.value.copy(isLoading = true)
        getFusedLocationUseCase(Unit).single()
            .onSuccess {
                _viewStates.value = _viewStates.value.copy(isLoading = false, fusedLocation = it)
            }
            .onFailure {
                _viewStates.value = _viewStates.value.copy(isLoading = false)
                _viewActions.send(MapAction.ShowError(it))
            }
    }

    private fun getCityName(latitude: Double, longitude: Double) = viewModelScope.launch {
        _viewStates.value = _viewStates.value.copy(isLoading = true)
        val latLng = LatLng(latitude, longitude)
        getCityNameFromGeocoderUseCase(Params(latitude, longitude)).single()
            .onSuccess {
                _viewStates.value = _viewStates.value.copy(
                    isLoading = false,
                    fusedLocation = null,
                    cityName = it,
                    clickPosition = latLng
                )
            }
            .onFailure {
                _viewStates.value = _viewStates.value.copy(isLoading = false)
                _viewActions.send(MapAction.ShowError(it))
            }
    }

    private fun setClickPosition(latLng: LatLng) {
        _viewStates.value = _viewStates.value.copy(cityName = null, clickPosition = latLng)
    }

}