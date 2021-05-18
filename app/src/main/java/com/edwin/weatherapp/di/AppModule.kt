package com.edwin.weatherapp.di

import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.edwin.data.device.GetLocationDataSource
import com.edwin.data.network.GeocoderDataSource
import com.edwin.data.network.RetrofitClient
import com.edwin.data.repository.WeatherRepositoryImpl
import com.edwin.domain.WeatherRepository
import com.edwin.domain.model.WeatherDetails
import com.edwin.domain.usecase.UseCase
import com.edwin.domain.usecase.map.GetAddressFromGeocoderUseCase
import com.edwin.domain.usecase.map.GetFusedLocationUseCase
import com.edwin.domain.usecase.weather.GetWeatherDetailsUseCase
import com.edwin.weatherapp.di.util.Constants
import com.edwin.weatherapp.presentation.map.MapViewModel
import com.edwin.weatherapp.presentation.weatherDetails.WeatherDetailsViewModel
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

object AppModule {
    val dataModule = module {

        // FusedLocationProvider
        single { LocationServices.getFusedLocationProviderClient(androidContext()) }

        // Location
        single { GetLocationDataSource(get()) }

        // Geocoder
        single { Geocoder(androidContext()) }

        // Get address from Geocoder
        single { GeocoderDataSource(get()) }

        // WeatherRepository
        single<WeatherRepository> {
            WeatherRepositoryImpl(
                RetrofitClient.weatherDataSource,
                get(),
                get()
            )
        }
    }

    val useCaseModule = module {
        single<UseCase<Location, Unit>>(
            named(Constants.fusedLocation)
        ) { GetFusedLocationUseCase(get()) }

        single<UseCase<Address, GetAddressFromGeocoderUseCase.Params>>(
            named(Constants.addressFromGeocoder)
        ) { GetAddressFromGeocoderUseCase(get()) }

        single<UseCase<WeatherDetails?, GetWeatherDetailsUseCase.Params>>(
            named(Constants.weatherDetails)
        ) { GetWeatherDetailsUseCase(get()) }
    }

    val viewModelModule = module {
        viewModel {
            MapViewModel(
                get(named(Constants.fusedLocation)),
                get(named(Constants.addressFromGeocoder))
            )
        }
        viewModel { WeatherDetailsViewModel(get(named(Constants.weatherDetails))) }
    }
}