package com.edwin.data.di

import com.edwin.data.network.WeatherApi
import com.edwin.data.repository.WeatherRepository
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(create(GsonBuilder().create()))
            .build().create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(weatherApi: WeatherApi): WeatherRepository {
        return WeatherRepository(weatherApi)
    }
}