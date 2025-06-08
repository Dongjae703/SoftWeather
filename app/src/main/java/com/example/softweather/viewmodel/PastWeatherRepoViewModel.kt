package com.example.softweather.viewmodel

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

class PastWeatherRepoViewModel : ViewModel() {
//    private val weatherMap = mutableStateMapOf<String, WeatherUIState>()
    private val _weatherStates = MutableStateFlow<Map<String, WeatherUIState>>(emptyMap())
//    val weatherStates: StateFlow<Map<String, WeatherUIState>> = _weatherStates

    val hourlyPastVM = HourlyPastWeatherViewModel(RetrofitInstance.hourlyPastWeatherApi)
    val dailyPastVM = DailyPastWeatherViewModel(RetrofitInstance.dailyPastWeatherApi)

    fun getWeatherState(locationKey: String): StateFlow<WeatherUIState?> {
        return _weatherStates
            .map { it[locationKey] ?: WeatherUIState.Loading }
            .stateIn(viewModelScope, SharingStarted.Eagerly, WeatherUIState.Loading)
    }

    fun loadPastWeather(lat: Double, lon: Double, key: String, startDate: String, endDate: String) {
        _weatherStates.update { it + (key to WeatherUIState.Loading) }

        viewModelScope.launch {
            try {
                val dailyPast = RetrofitInstance.dailyPastWeatherApi.getDailyArchive(lat, lon, startDate = startDate, endDate = endDate)
                val hourlyPast = RetrofitInstance.hourlyPastWeatherApi.getHourlyWeather(lat, lon, start = startDate, end = LocalDate.parse(startDate,
                    DateTimeFormatter.ISO_LOCAL_DATE).plusDays(2L).toString())

                dailyPastVM.setData(dailyPast)
                hourlyPastVM.setData(hourlyPast)

                val successState = WeatherUIState.Success(
                    current = null,  // 과거는 current 없음
                    daily = dailyPast,
                    hourly = hourlyPast
                )

                _weatherStates.update { it + (key to successState) }

            } catch (e: Exception) {
                _weatherStates.update { it + (key to WeatherUIState.Error(e.message ?: "알 수 없는 오류")) }
            }
        }
    }
}