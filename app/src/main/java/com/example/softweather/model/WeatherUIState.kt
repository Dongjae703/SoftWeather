package com.example.softweather.model

import com.example.softweather.model.currentday.OpenMeteoResponse
import com.example.softweather.model.daily.DailyWeatherResponse
import com.example.softweather.model.hourly.HourlyWeatherResponse

sealed class WeatherUIState {
    object Loading : WeatherUIState()
    data class Success(
        val current: OpenMeteoResponse?,
        val daily: DailyWeatherResponse,
        val hourly: HourlyWeatherResponse
    ) : WeatherUIState()
    data class Error(val message: String) : WeatherUIState()
}