package com.example.softweather.model.daily

data class DailyWeatherResponse(
    val daily: DailyData
)

data class DailyData(
    var time: List<String>,
    var temperature_2m_max: List<Double>,
    var temperature_2m_min: List<Double>,
    var weathercode: List<Int>,
    var precipitation_sum: List<Double>,
    var sunrise: List<String>,
    var sunset: List<String>,
    var windspeed_10m_max: List<Double>,
)