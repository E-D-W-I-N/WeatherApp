package com.edwin.data.network

import android.location.Geocoder
import com.edwin.domain.exception.MapException

class GeocoderDataSource(private val geocoder: Geocoder) {

    fun getCityNameFromGeocoder(
        latitude: Double,
        longitude: Double
    ): String {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses.isEmpty() || addresses.first().locality.isNullOrEmpty()) {
            throw MapException.CityNotFound
        }
        return addresses.first().locality
    }
}