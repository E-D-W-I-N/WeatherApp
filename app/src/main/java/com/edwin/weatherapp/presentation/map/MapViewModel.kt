package com.edwin.weatherapp.presentation.map

import android.location.Address
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.domain.DataResult
import com.edwin.domain.usecase.GetAddressFromGeocoderUseCase
import com.edwin.domain.usecase.GetFusedLocationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val getFusedLocationUseCase: GetFusedLocationUseCase,
    private val getAddressFromGeocoderUseCase: GetAddressFromGeocoderUseCase
) : ViewModel() {

    private val _fusedLocation = MutableStateFlow<DataResult<Location>>(DataResult.Empty)
    val fusedLocation: StateFlow<DataResult<Location>> = _fusedLocation.asStateFlow()

    private val _cityName = MutableStateFlow<DataResult<Address>>(DataResult.Empty)
    val cityName: StateFlow<DataResult<Address>> = _cityName.asStateFlow()

    fun getFusedLocation() = viewModelScope.launch(Dispatchers.IO) {
        _fusedLocation.value = DataResult.Loading
        _fusedLocation.value = getFusedLocationUseCase.getFusedLocation()
    }

    fun getCityName(latitude: Double, longitude: Double) = viewModelScope.launch(Dispatchers.IO) {
        _cityName.value = DataResult.Loading
        _cityName.value = getAddressFromGeocoderUseCase.getAddress(latitude, longitude)
    }
}