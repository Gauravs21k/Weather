package com.kaimiya.gaurav.weather.model

data class DailyWeather(
    val minTemp: Int,
    val maxTemp: Int,
    val icon: String,
    val day: String,
    val date: String
)
