package com.edwin.data.network.response

data class WeatherDTO(
    val coord: Map<String, Double>,
    val weather: List<Weather>,
    val base: String,
    val main: Map<String, Double>,
    val visibility: Int,
    val wind: Map<String, Double>,
    val clouds: Pair<String, Int>,
    val rain: Map<String, Double>,
    val snow: Map<String, Double>,
    val dt: Long,
    val sys: Sys,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Int
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Sys(
    val type: Int,
    val id: Int,
    val message: Double,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
)