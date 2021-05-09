package com.edwin.domain.exception

sealed class MapException : Exception() {

    object NoLastLocation : MapException()
    object CityNotFound : MapException()
    object GeocoderFailed : MapException()
}