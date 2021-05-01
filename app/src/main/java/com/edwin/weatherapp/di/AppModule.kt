package com.edwin.weatherapp.di

import com.edwin.data.device.GetLocation
import com.edwin.data.network.WeatherApi
import com.edwin.data.repository.WeatherRepositoryImpl
import com.edwin.domain.WeatherRepository
import com.edwin.domain.usecase.GetFusedLocationUseCase
import com.edwin.domain.usecase.GetWeatherDetailsUseCase
import com.edwin.weatherapp.feature.map.MapViewModel
import com.edwin.weatherapp.feature.weatherDetails.WeatherDetailsViewModel
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    // Retrofit API
    single {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(WeatherApi::class.java)
    }

    // FusedLocationProvider
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }

    // Location 
    single { GetLocation(get()) }

    // WeatherRepository
    single<WeatherRepository> { WeatherRepositoryImpl(get(), get()) }
}

val useCaseModule = module {
    single { GetWeatherDetailsUseCase(get()) }
    single { GetFusedLocationUseCase(get()) }
}

val viewModelModule = module {
    viewModel { MapViewModel(get()) }
    viewModel { WeatherDetailsViewModel(get()) }
}