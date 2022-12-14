package com.kaimiya.gaurav.weather.repository

import com.kaimiya.gaurav.weather.network.WeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(private val api: WeatherApi) {

    suspend fun getCurrentWeather(lat: String, lon: String, appid: String) = api.getCurrentWeather(lat, lon, appid)

    suspend fun getHourlyWeather(lat: String, lon: String, appid: String) = api.getHourlyWeather(lat, lon, appid)
}