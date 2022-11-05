package com.kaimiya.gaurav.weather.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, MainSubModule::class])
interface ApplicationComponent {

    fun mainComponentFactory(): MainSubComponent.Factory
}