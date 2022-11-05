package com.kaimiya.gaurav.weather.di

import com.kaimiya.gaurav.weather.logginginterceptor.AddLoggingInterceptor
import com.kaimiya.gaurav.weather.network.WeatherApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofitService(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(AddLoggingInterceptor.setLogging())
            .build()
            .create(WeatherApi::class.java)
    }
}