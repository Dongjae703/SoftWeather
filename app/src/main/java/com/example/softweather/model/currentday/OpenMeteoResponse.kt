package com.example.softweather.model.currentday

import com.squareup.moshi.Json

data class OpenMeteoResponse(
    @Json(name = "current_weather")
    val currentWeather: CurrentWeather
)

data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Double,
    val weathercode: Int,
    val is_day: Int,
    val time: String
)
