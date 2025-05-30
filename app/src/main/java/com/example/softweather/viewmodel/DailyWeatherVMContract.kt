package com.example.softweather.viewmodel

import kotlinx.coroutines.flow.StateFlow

interface DailyWeatherVMContract {
    val temperature_max: StateFlow<List<Double?>>
    val temperature_min: StateFlow<List<Double?>>
    val precipitation_sum: StateFlow<List<Double?>>
    val sunrise: StateFlow<List<String?>>
    val sunset: StateFlow<List<String?>>
    val windSpeed_max: StateFlow<List<Double?>>
    val weatherCode: StateFlow<List<Int?>>

    fun fetchDailyWeather(lat: Double, lon: Double, startDate: String, endDate: String)
}
