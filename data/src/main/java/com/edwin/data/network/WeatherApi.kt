package com.edwin.data.network

import com.edwin.data.network.response.WeatherDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather")
    fun getWeatherResponse(
        @Query("q") q: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<WeatherDTO>
}