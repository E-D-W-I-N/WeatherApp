package com.edwin.weatherapp

import android.app.Application
import com.edwin.weatherapp.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WeatherApplication)
            modules(
                AppModule.dataModule,
                AppModule.useCaseModule,
                AppModule.viewModelModule
            )
        }
    }
}