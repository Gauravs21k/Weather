package com.kaimiya.gaurav.weather.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HourlyWeather(
    @SerializedName("list")
    @Expose
    val threeHourWeather: List<ThreeHourWeather>,
    val city: City,
    val cnt: Int,
    val cod: String,
    val message: Int
)