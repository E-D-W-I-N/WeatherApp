package com.edwin.weatherapp.presentation.map.model

import android.location.Location
import com.google.android.gms.maps.model.LatLng

data class MapViewState(
    val isLoading: Boolean = false,
    val fusedLocation: Location? = null,
    val cityName: String? = null,
    val clickPosition: LatLng? = null
)