package com.example.softweather.model.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.softweather.viewmodel.DailyWeatherViewModel

class DailyWeatherViewModelFactory(private val api: DailyWeatherApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DailyWeatherViewModel(api) as T
    }
}