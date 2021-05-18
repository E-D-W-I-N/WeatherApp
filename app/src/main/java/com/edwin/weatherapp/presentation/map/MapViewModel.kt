package com.edwin.weatherapp.presentation.map

import android.location.Address
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.domain.usecase.UseCase
import com.edwin.domain.usecase.map.GetAddressFromGeocoderUseCase.Params
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MapViewModel(
    private val getFusedLocationUseCase: UseCase<Location, Unit>,
    private val getAddressFromGeocoderUseCase: UseCase<Address, Params>
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Default)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<ActionState>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun getFusedLocation() = viewModelScope.launch {
        _uiState.value = MapUiState.Loading
        getFusedLocationUseCase(Unit).single()
            .onSuccess { _uiState.value = MapUiState.CurrentLocationLoaded(it) }
            .onFailure {
                _uiState.value = MapUiState.Error
                eventChannel.send(ActionState.ShowError(it))
            }
    }

    fun getAddress(latitude: Double, longitude: Double) = viewModelScope.launch {
        _uiState.value = MapUiState.Loading
        getAddressFromGeocoderUseCase(Params(latitude, longitude)).single()
            .onSuccess { _uiState.value = MapUiState.AddressLoaded(it) }
            .onFailure {
                _uiState.value = MapUiState.Error
                eventChannel.send(ActionState.ShowError(it))
            }
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