package com.kaimiya.gaurav.weather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kaimiya.gaurav.weather.R
import com.kaimiya.gaurav.weather.databinding.ItemHourlyForecastBinding
import com.kaimiya.gaurav.weather.model.ThreeHourWeather

class HourlyWeatherAdapter(var weatherHours: List<ThreeHourWeather>): RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder>() {


    class HourlyViewHolder(val binding: ItemHourlyForecastBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        return HourlyViewHolder(
            ItemHourlyForecastBinding
                .inflate(LayoutInflater
                    .from(parent.context),
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.binding.apply {
            threeHourWeather = weatherHours[position]
            itemIcon.setImageResource(when(weatherHours[position].weather[0].icon) {
                "01d" -> R.drawable.sun
                "02d" -> R.drawable.sun_cloud
                "03d" -> R.drawable.cloud
                "04d" -> R.drawable.black_cloud
                "09d" -> R.drawable.cloud_rain
                "10d" -> R.drawable.sun_rain
                "11d" -> R.drawable.thunder
                else -> R.drawable.sun
            })

            time.text = weatherHours[position].dt_txt.subSequence(11,16)
            itemTemp.text = String.format("%.1f", weatherHours[position].main.temp - 273) + "\u2103"
        }
    }

    override fun getItemCount(): Int {
        return weatherHours.size
    }

}