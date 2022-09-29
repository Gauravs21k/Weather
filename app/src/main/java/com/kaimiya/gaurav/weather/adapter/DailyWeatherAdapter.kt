package com.kaimiya.gaurav.weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kaimiya.gaurav.weather.R
import com.kaimiya.gaurav.weather.databinding.ItemDailyWeatherBinding
import com.kaimiya.gaurav.weather.model.DailyWeather
import kotlin.math.min

class DailyWeatherAdapter(var days: List<DailyWeather>): RecyclerView.Adapter<DailyWeatherAdapter.DailyViewHolder>() {
    class DailyViewHolder(val binding: ItemDailyWeatherBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        return DailyViewHolder(
            ItemDailyWeatherBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.binding.apply {
            day.text = days[position].day
            date.text = days[position].date
            minTemp.text = days[position].minTemp.toString() + "\u2103"
            maxTemp.text = days[position].maxTemp.toString() + "\u2103"
            itemIcon.setImageResource(when(days[position].icon) {
                "01d" -> R.drawable.sun
                "01n" -> R.drawable.moon
                "02d" -> R.drawable.sun_cloud
                "02n" -> R.drawable.cloud_moon
                "03d" -> R.drawable.cloud
                "03n" -> R.drawable.cloud
                "04d" -> R.drawable.black_cloud
                "04n" -> R.drawable.black_cloud
                "09d" -> R.drawable.cloud_rain
                "09n" -> R.drawable.cloud_rain
                "10d" -> R.drawable.sun_rain
                "10n" -> R.drawable.moon_rain
                "11d" -> R.drawable.thunder
                "11n" -> R.drawable.thunder
                else -> R.drawable.sun
            })
        }

    }

    override fun getItemCount(): Int {
        return days.size
    }


}