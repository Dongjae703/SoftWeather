package com.example.softweather.model.hourly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.softweather.viewmodel.HourlyWeatherViewModel

class HourlyWeatherViewModelFactory(private val api: HourlyWeatherApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HourlyWeatherViewModel(api) as T
    }
}