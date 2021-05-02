package com.edwin.data.network

import android.location.Address
import android.location.Geocoder
import java.io.IOException

class GeocoderDataSource(private val geocoder: Geocoder) {

    fun getAddressFromGeocoder(
        latitude: Double,
        longitude: Double
    ): Address {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses.isEmpty() || addresses.first().locality.isNullOrEmpty()) {
            throw IOException()
        }
        return addresses.first()
    }
}