package com.example.softweather.viewmodel

import kotlinx.coroutines.flow.StateFlow

interface HourlyWeatherVMContract {
    val timeList: StateFlow<List<String>>
    val humidity: StateFlow<List<Int?>>
    val temperature: StateFlow<List<Double?>>
    val weatherCode: StateFlow<List<Int?>>

    fun fetchHourlyWeather(lat: Double, lon: Double, start: String, end: String)
}