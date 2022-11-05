package com.kaimiya.gaurav.weather.di

import com.kaimiya.gaurav.weather.MainActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface MainSubComponent {
    fun inject(activity: MainActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainSubComponent
    }
}