package com.example.softweather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.softweather.model.currentday.OpenMeteoApi

class WeatherViewModelFactory(private val api: OpenMeteoApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(api) as T
    }
}