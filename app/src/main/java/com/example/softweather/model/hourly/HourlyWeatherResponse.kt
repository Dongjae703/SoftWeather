package com.example.softweather.model.hourly

data class HourlyWeatherResponse(
    val hourly: HourlyData
)

data class HourlyData(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val relative_humidity_2m: List<Int>,
    val weather_code : List<Int>
)