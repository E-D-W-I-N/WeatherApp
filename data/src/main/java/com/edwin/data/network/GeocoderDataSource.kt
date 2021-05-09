package com.edwin.data.network

import android.location.Address
import android.location.Geocoder
import com.edwin.domain.exception.MapException

class GeocoderDataSource(private val geocoder: Geocoder) {

    fun getAddressFromGeocoder(
        latitude: Double,
        longitude: Double
    ): Address {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses.isEmpty() || addresses.first().locality.isNullOrEmpty()) {
            throw MapException.CityNotFound
        }
        return addresses.first()
    }
}