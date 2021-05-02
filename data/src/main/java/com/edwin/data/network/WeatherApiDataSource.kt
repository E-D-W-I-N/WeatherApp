package com.edwin.data.network

import com.edwin.data.entity.WeatherDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiDataSource {

    @GET("weather")
    fun getWeatherResponse(
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<WeatherDTO>
}