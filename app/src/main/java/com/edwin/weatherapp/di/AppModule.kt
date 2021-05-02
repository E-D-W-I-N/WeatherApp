package com.edwin.weatherapp.di

import android.location.Geocoder
import com.edwin.data.device.GetLocationDataSource
import com.edwin.data.network.GeocoderDataSource
import com.edwin.data.network.WeatherApiDataSource
import com.edwin.data.repository.WeatherRepositoryImpl
import com.edwin.domain.WeatherRepository
import com.edwin.domain.usecase.GetAddressFromGeocoderUseCase
import com.edwin.domain.usecase.GetFusedLocationUseCase
import com.edwin.domain.usecase.GetWeatherDetailsUseCase
import com.edwin.weatherapp.presentation.map.MapViewModel
import com.edwin.weatherapp.presentation.weatherDetails.WeatherDetailsViewModel
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
            .build().create(WeatherApiDataSource::class.java)
    }

    // FusedLocationProvider
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }

    // Location 
    single { GetLocationDataSource(get()) }

    // Geocoder
    single { Geocoder(androidContext()) }

    // Get address from Geocoder
    single { GeocoderDataSource(get()) }

    // WeatherRepository
    single<WeatherRepository> { WeatherRepositoryImpl(get(), get(), get()) }
}

val useCaseModule = module {
    single { GetWeatherDetailsUseCase(get()) }
    single { GetFusedLocationUseCase(get()) }
    single { GetAddressFromGeocoderUseCase(get()) }
}

val viewModelModule = module {
    viewModel { MapViewModel(get(), get()) }
    viewModel { WeatherDetailsViewModel(get()) }
}