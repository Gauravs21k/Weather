package com.kaimiya.gaurav.weather

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kaimiya.gaurav.weather.adapter.DailyWeatherAdapter
import com.kaimiya.gaurav.weather.adapter.HourlyWeatherAdapter
import com.kaimiya.gaurav.weather.adapter.TemperatureTrendGraphLinearItemDecorator
import com.kaimiya.gaurav.weather.databinding.ActivityMainBinding
import com.kaimiya.gaurav.weather.network.RetrofitInstance
import com.kaimiya.gaurav.weather.repository.MainRepository
import com.kaimiya.gaurav.weather.viewmodel.MainActivityViewModel
import com.kaimiya.gaurav.weather.viewmodel.MainViewModelFactory
import java.time.LocalDateTime


private const val SHARED_PREFERENCES_KEY = "my_shared_preferences"
class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: MainActivityViewModel
    lateinit var binding: ActivityMainBinding
    lateinit var hourlyWeatherAdapter: HourlyWeatherAdapter
    lateinit var dailyWeatherAdapter: DailyWeatherAdapter
    private var graphDecorator: RecyclerView.ItemDecoration? = null
    val location = Location("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val api = RetrofitInstance.api
        val mainRepository = MainRepository(api)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(mainRepository)
        )[MainActivityViewModel::class.java]

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

        hourlyWeatherAdapter = HourlyWeatherAdapter(emptyList())
        dailyWeatherAdapter = DailyWeatherAdapter(emptyList())

        binding.hourlyForecast.adapter = hourlyWeatherAdapter
        binding.hourlyForecast.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

        binding.dailyForecast.adapter = dailyWeatherAdapter
        binding.dailyForecast.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

        binding.swipeDownRefresh.setOnRefreshListener {
            updateLocation()
        }

        if (sharedPreferences.getString("lat", "0.0")?.toDouble() != 0.0 && sharedPreferences.getString("lat", "0.0")?.toDouble() != 0.0) {
            location.latitude = sharedPreferences.getString("lat", "")!!.toDouble()
            location.longitude = sharedPreferences.getString("long", "")!!.toDouble()
            viewModel.location.value = location
            Log.d("test", "${sharedPreferences.getString("lat", "")!!.toDouble()} ${sharedPreferences.getString("long", "")!!.toDouble()}")
            Log.d("test_s","${sharedPreferences.getString("time","")}")
        } else {
            updateLocation()
        }

        viewModel.location.observe(this) {
            viewModel.getCurrentWeather(it.latitude.toString(), it.longitude.toString(), "30287abb574aa5fccb986c8a47fd6f5f")
            viewModel.getHourlyWeather(it.latitude.toString(), it.longitude.toString(), "30287abb574aa5fccb986c8a47fd6f5f")
            viewModel.getDailyWeather()
            sharedPreferences.edit().putString("lat", it.latitude.toString()).apply()
            sharedPreferences.edit().putString("long", it.longitude.toString()).apply()
        }

        viewModel.currentWeather.observe(this) {
            Log.d("test", "$it")
            binding.currentWeather = it
            binding.apply {
                currentTemp.text = String.format("%.1f", it.main.temp - 273) + "\u2103"
                humidity.text = it.main.humidity.toString()
                wind.text = it.wind.speed.toString()
                pressure.text = it.main.pressure.toString()
                visibility.text = it.visibility.toString()
                iconWeather.setImageResource(when(it.weather[0].icon) {
                    "01d" -> R.drawable.sun
                    "02d" -> R.drawable.sun_cloud
                    "03d" -> R.drawable.cloud
                    "04d" -> R.drawable.black_cloud
                    "09d" -> R.drawable.cloud_rain
                    "10d" -> R.drawable.sun_rain
                    "11d" -> R.drawable.thunder
                    else -> R.drawable.sun
                })
            }
        }

        viewModel.hourlyWeather.observe(this) {
            Log.d("test", "$it")
            hourlyWeatherAdapter.apply {
                weatherHours = it.threeHourWeather
                notifyDataSetChanged()
            }
            graphDecorator?.let { itemDecoration ->
                binding.hourlyForecast.removeItemDecoration(
                    itemDecoration
                )
            }
            graphDecorator = TemperatureTrendGraphLinearItemDecorator(it.threeHourWeather, this)
            graphDecorator?.let { itemDecorator ->
                binding.hourlyForecast.addItemDecoration(
                    itemDecorator
                )
            }
        }

        viewModel.dailyweather.observe(this) {
            dailyWeatherAdapter.days = it
            Log.d("test_", "$it")
            dailyWeatherAdapter.notifyDataSetChanged()
        }
    }

    private fun updateLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) {
                    if (it.result == null) {
                        Toast.makeText(applicationContext, "System not working", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
                        viewModel.location.value = Location(it.result)
                        Log.d("test_i", "${it.result.latitude} ${it.result.latitude}")
                        binding.swipeDownRefresh.isRefreshing = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            sharedPreferences.edit().putString("time", LocalDateTime.now().toString()).apply()
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                updateLocation()
            } else {
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}