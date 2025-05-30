package com.example.softweather.model.hourly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.softweather.viewmodel.HourlyPastWeatherViewModel

class HourlyPastWeatherViewModelFactory(private val api: HourlyPastWeatherApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HourlyPastWeatherViewModel(api) as T
    }
}