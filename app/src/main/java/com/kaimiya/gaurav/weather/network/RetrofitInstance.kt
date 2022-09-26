package com.kaimiya.gaurav.weather.network

import com.kaimiya.gaurav.weather.logginginterceptor.AddLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(AddLoggingInterceptor.setLogging())
            .build()
            .create(WeatherApi::class.java)
    }
}