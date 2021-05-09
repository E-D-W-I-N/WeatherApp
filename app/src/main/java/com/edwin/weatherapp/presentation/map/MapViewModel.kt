package com.edwin.weatherapp.presentation.map

import android.location.Address
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.domain.usecase.GetAddressFromGeocoderUseCase
import com.edwin.domain.usecase.GetFusedLocationUseCase
import com.edwin.weatherapp.util.asLiveData
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class MapViewModel(
    private val getFusedLocationUseCase: GetFusedLocationUseCase,
    private val getAddressFromGeocoderUseCase: GetAddressFromGeocoderUseCase
) : ViewModel() {

    private val _fusedLocation = MutableLiveData<Result<Location>>()
    val fusedLocation: LiveData<Result<Location>> = _fusedLocation.asLiveData()

    private val _address = MutableLiveData<Result<Address>>()
    val address: LiveData<Result<Address>> = _address.asLiveData()

    fun getFusedLocation() = viewModelScope.launch {
        _fusedLocation.value = getFusedLocationUseCase.invoke().single()
    }

    fun getAddress(latitude: Double, longitude: Double) = viewModelScope.launch {
        _address.value = getAddressFromGeocoderUseCase.invoke(latitude, longitude).single()
    }
}