package com.edwin.data.device

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Tasks

class GetLocationDataSource(private val fusedLocationProviderClient: FusedLocationProviderClient) {

    @SuppressLint("MissingPermission")
    fun getFusedLocation(): Location? =
        Tasks.await(fusedLocationProviderClient.lastLocation) ?: null
}