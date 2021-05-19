package com.edwin.weatherapp.presentation.map.model

sealed class MapAction {
    data class ShowError(val throwable: Throwable) : MapAction()
}