package com.kaimiya.gaurav.weather.network

import com.kaimiya.gaurav.weather.model.CurrentWeather
import com.kaimiya.gaurav.weather.model.HourlyWeather
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String?,
        @Query("appid") appid: String?
    ): Response<CurrentWeather>

    @GET("forecast")
    suspend fun getHourlyWeather(
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("appid") appid: String?
    ): Response<HourlyWeather>
}