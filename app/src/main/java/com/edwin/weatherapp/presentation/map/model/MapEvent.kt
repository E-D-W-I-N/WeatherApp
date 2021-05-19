package com.edwin.weatherapp.presentation.map.model

import com.google.android.gms.maps.model.LatLng

sealed class MapEvent {
    object GetFusedLocation : MapEvent()
    data class GetCityName(val latitude: Double, val longitude: Double) : MapEvent()
    data class SetClickPosition(val latLng: LatLng) : MapEvent()
}