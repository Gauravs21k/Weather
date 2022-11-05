package com.kaimiya.gaurav.weather

import android.app.Application
import com.kaimiya.gaurav.weather.di.ApplicationComponent
import com.kaimiya.gaurav.weather.di.DaggerApplicationComponent

class WeatherApplication: Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.create()
    }

}