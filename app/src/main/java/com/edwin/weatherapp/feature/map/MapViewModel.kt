package com.edwin.weatherapp.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.edwin.domain.usecase.GetFusedLocationUseCase

class MapViewModel(getFusedLocationUseCase: GetFusedLocationUseCase) : ViewModel() {

    val fusedLocation = getFusedLocationUseCase.getFusedLocation().asLiveData()
}