package com.edwin.weatherapp.extensions

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


inline fun GoogleMap.addMarker(
    optionsActions: MarkerOptions.() -> Unit
): Marker? = this.addMarker(markerOptions(optionsActions))

inline fun markerOptions(
    optionsActions: MarkerOptions.() -> Unit
): MarkerOptions = MarkerOptions().apply(optionsActions)