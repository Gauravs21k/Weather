package com.kaimiya.gaurav.weather.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kaimiya.gaurav.weather.di.ActivityScope
import com.kaimiya.gaurav.weather.model.CurrentWeather
import com.kaimiya.gaurav.weather.model.DailyWeather
import com.kaimiya.gaurav.weather.model.HourlyWeather
import com.kaimiya.gaurav.weather.repository.MainRepository
import kotlinx.coroutines.*
import javax.inject.Inject

@ActivityScope
class MainViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel() {
    var job: Job? = null
    var job1: Job? = null
    val errorMessage = MutableLiveData<String>()
    val currentWeather = MutableLiveData<CurrentWeather>()
    val hourlyWeather = MutableLiveData<HourlyWeather>()
    val dailyweather = MutableLiveData<List<DailyWeather>>()
    var location = MutableLiveData<Location>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    val currentloading = MutableLiveData<Boolean>()
    val hourlyloading = MutableLiveData<Boolean>()

    fun getCurrentWeather(lat: String, lon: String, appid: String) {
        currentloading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.getCurrentWeather(lat, lon, appid)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    currentWeather.value = response.body()
                    currentloading.value = false
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }

    fun getHourlyWeather(lat: String, lon: String, appid: String) {
        hourlyloading.value = true
        job1 = CoroutineScope(Dispatchers.IO).launch {
            val response = mainRepository.getHourlyWeather(lat, lon, appid)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    hourlyWeather.postValue(response.body())
                    hourlyloading.value = false
                } else {
                    //errorMessage.value = response.message()
                    hourlyloading.value = false
                }
            }
        }
    }

    fun getDailyWeather() {
        val days: MutableList<DailyWeather> = mutableListOf()
        days.add(DailyWeather(22,33, "01d", "Monday", "Sep 19"))
        days.add(DailyWeather(22,33, "09d", "Tuesday", "Sep 20"))
        days.add(DailyWeather(17,28, "10d", "Wednesday", "Sep 21"))
        days.add(DailyWeather(17,30, "03d", "Thursday", "Sep 22"))
        days.add(DailyWeather(20,30, "02d", "Friday", "Sep 23"))
        days.add(DailyWeather(18,25, "02d", "Saturday", "Sep 24"))
        days.add(DailyWeather(23,26, "04d", "Sunday", "Sep 25"))
        days.add(DailyWeather(23,26, "11d", "Monday", "Sep 26"))
        days.add(DailyWeather(18,25, "11d", "Tuesday", "Sep 27"))
        days.add(DailyWeather(18,25, "01d", "Wednesday", "Sep 28"))
        days.add(DailyWeather(21,29, "01d", "Thursday", "Sep 29"))
        days.add(DailyWeather(18,25, "04d", "Friday", "Sep 30"))
        days.add(DailyWeather(18,24, "02d", "Saturday", "Oct 1"))
        days.add(DailyWeather(18,27, "02d", "Sunday", "Oct 2"))
        days.add(DailyWeather(18,25, "03d", "Monday", "Oct 3"))

        dailyweather.value = days
    }


    private fun onError(message: String) {
        errorMessage.value = message
        currentloading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        job1?.cancel()
    }

}