package com.example.softweather.model.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.softweather.viewmodel.DailyPastWeatherViewModel

class DailyPastWeatherViewModelFactory(private val api: DailyPastWeatherApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DailyPastWeatherViewModel(api) as T
    }
}