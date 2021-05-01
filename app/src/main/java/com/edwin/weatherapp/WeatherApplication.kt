package com.edwin.weatherapp

import android.app.Application
import com.edwin.weatherapp.di.dataModule
import com.edwin.weatherapp.di.useCaseModule
import com.edwin.weatherapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WeatherApplication)
            modules(listOf(dataModule, useCaseModule, viewModelModule))
        }
    }
}