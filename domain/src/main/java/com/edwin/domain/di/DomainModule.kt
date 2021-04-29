package com.edwin.domain.di

import com.edwin.data.repository.WeatherRepository
import com.edwin.domain.usecase.GetWeatherDetailsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Singleton
    @Provides
    fun provideGetWeatherDetailsUseCase(
        weatherRepository: WeatherRepository
    ): GetWeatherDetailsUseCase {
        return GetWeatherDetailsUseCase(weatherRepository)
    }
}