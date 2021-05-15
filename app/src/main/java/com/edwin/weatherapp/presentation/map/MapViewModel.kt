package com.edwin.weatherapp.presentation.map

import android.location.Address
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.domain.usecase.GetAddressFromGeocoderUseCase
import com.edwin.domain.usecase.GetFusedLocationUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MapViewModel(
    private val getFusedLocationUseCase: GetFusedLocationUseCase,
    private val getAddressFromGeocoderUseCase: GetAddressFromGeocoderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Default)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<ActionState>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun getFusedLocation() = viewModelScope.launch {
        _uiState.value = MapUiState.Loading
        val location = getFusedLocationUseCase.invoke().single()
        location.fold(
            onSuccess = { _uiState.value = MapUiState.CurrentLocationLoaded(it) },
            onFailure = {
                _uiState.value = MapUiState.Error
                eventChannel.send(ActionState.ShowError(it))
            }
        )
    }

    fun getAddress(latitude: Double, longitude: Double) = viewModelScope.launch {
        _uiState.value = MapUiState.Loading
        val address = getAddressFromGeocoderUseCase.invoke(latitude, longitude).single()
        address.fold(
            onSuccess = { _uiState.value = MapUiState.AddressLoaded(it) },
            onFailure = {
                _uiState.value = MapUiState.Error
                eventChannel.send(ActionState.ShowError(it))
            }
        )
    }

    sealed class MapUiState {
        object Loading : MapUiState()
        object Error : MapUiState()
        object Default : MapUiState()
        data class CurrentLocationLoaded(val location: Location) : MapUiState()
        data class AddressLoaded(val address: Address) : MapUiState()
    }

    sealed class ActionState {
        data class ShowError(val throwable: Throwable) : ActionState()
    }
}