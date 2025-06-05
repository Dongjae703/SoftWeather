package com.example.softweather.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softweather.model.RetrofitInstance
import com.example.softweather.model.WeatherUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WeatherRepoViewModel : ViewModel() {
    private val weatherMap = mutableStateMapOf<String, WeatherUIState>()
    private val _weatherStates = MutableStateFlow<Map<String, WeatherUIState>>(emptyMap())
//    val weatherStates: StateFlow<Map<String, WeatherUIState>> = _weatherStates

    fun getWeatherState(locationKey: String): StateFlow<WeatherUIState?> {
        return _weatherStates
            .map { it[locationKey] ?: WeatherUIState.Loading }
            .stateIn(viewModelScope, SharingStarted.Eagerly, WeatherUIState.Loading)
    }

    fun loadWeatherData(lat: Double, lon: Double, key: String, startDate: String, endDate: String) {
        if (weatherMap.containsKey(key)) return
        weatherMap[key] = WeatherUIState.Loading
        _weatherStates.update { it + (key to WeatherUIState.Loading) } // ✅ UI에 반영

        viewModelScope.launch {
            try {
                val current = RetrofitInstance.openMeteoApi.getCurrentWeather(lat, lon)
                val daily = RetrofitInstance.dailyWeatherApi.getDailyForecast(lat, lon, startDate = startDate, endDate = endDate)
                val hourly = RetrofitInstance.hourlyWeatherApi.getHourlyWeather(lat, lon, start = startDate,end = LocalDate.parse(startDate,
                    DateTimeFormatter.ISO_LOCAL_DATE).plusDays(2L).toString())

                val successState = WeatherUIState.Success(current, daily, hourly)
                weatherMap[key] = successState
                _weatherStates.update { it + (key to successState) } // ✅ UI에 반영
            } catch (e: Exception) {
                val errorState = WeatherUIState.Error(e.message ?: "알 수 없는 오류")
                weatherMap[key] = errorState
                _weatherStates.update { it + (key to errorState) } // ✅ UI에 반영
            }
        }
    }
}