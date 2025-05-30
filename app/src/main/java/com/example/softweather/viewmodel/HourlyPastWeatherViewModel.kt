package com.example.softweather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softweather.model.hourly.HourlyPastWeatherApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HourlyPastWeatherViewModel(private val api: HourlyPastWeatherApi) : ViewModel(), HourlyWeatherVMContract {
    private val _timeList = MutableStateFlow<List<String>>(emptyList())
    private val _humidity = MutableStateFlow<List<Int?>>(emptyList())
    private val _temperature = MutableStateFlow<List<Double?>>(emptyList())
    private val _weatherCode = MutableStateFlow<List<Int?>>(emptyList())

    override val timeList: StateFlow<List<String>> = _timeList
    override val humidity: StateFlow<List<Int?>> = _humidity
    override val temperature: StateFlow<List<Double?>> = _temperature
    override val weatherCode: StateFlow<List<Int?>> = _weatherCode

    override fun fetchHourlyWeather(lat: Double, lon: Double, start: String, end: String) {
        viewModelScope.launch {
            try {
                val response = api.getHourlyWeather(lat, lon, start = start, end = end)
                _timeList.value = response.hourly.time
                _humidity.value = response.hourly.relative_humidity_2m
                _temperature.value = response.hourly.temperature_2m
                _weatherCode.value = response.hourly.weather_code
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
