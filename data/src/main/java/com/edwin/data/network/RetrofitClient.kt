package com.edwin.data.network

import com.edwin.data.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = BuildConfig.API_URL

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    val weatherDataSource: WeatherApiDataSource by lazy {
        retrofit.create(WeatherApiDataSource::class.java)
    }
}