package com.example.softweather.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softweather.model.currentday.OpenMeteoApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val api: OpenMeteoApi
) : ViewModel() {
    private val _temperature = MutableStateFlow<Double?>(null)
    private val _windSpeed = MutableStateFlow<Double?>(null)
    private val _windDirection = MutableStateFlow<Double?>(null)
    private val _weatherCode = MutableStateFlow<Int?>(null)
    val temperature: StateFlow<Double?> = _temperature
    val windSpeed : StateFlow<Double?> = _windSpeed
    val windDirection : StateFlow<Double?> = _windDirection
    val weatherCode : StateFlow<Int?> = _weatherCode
    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val result = api.getCurrentWeather(lat, lon)
                val weather = result.currentWeather
                if (weather != null) {
                    _temperature.value = weather.temperature
                    _windSpeed.value = weather.windspeed
                    _windDirection.value = weather.winddirection
                    _weatherCode.value = weather.weathercode
                } else {
                    Log.e("WeatherVM", "currentWeather == null")
                }
            } catch (e: Exception) {
                Log.e("WeatherVM", "API 오류: ${e.message}")
            }
        }
    }
}