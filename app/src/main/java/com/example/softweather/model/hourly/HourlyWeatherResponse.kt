package com.example.softweather.model.hourly

data class HourlyWeatherResponse(
    val hourly: HourlyData
)

data class HourlyData(
    var time: List<String>,
    var temperature_2m: List<Double>,
    var relative_humidity_2m: List<Int>,
    var weather_code : List<Int>
)