package com.example.softweather.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softweather.model.RetrofitInstance
import com.example.softweather.model.WeatherUIState
import com.example.softweather.model.daily.DailyData
import com.example.softweather.model.daily.DailyWeatherResponse
import com.example.softweather.model.hourly.HourlyData
import com.example.softweather.model.hourly.HourlyWeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
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

    fun setDummyWeather(key: String, start: LocalDate, end: LocalDate) {
        val dateRange = generateSequence(start) { it.plusDays(1) }
            .takeWhile { !it.isAfter(end) }
            .toList()

        val size = dateRange.size

        val dummyState = WeatherUIState.Success(
            current = null,
            daily = DailyWeatherResponse(
                daily = DailyData(
                    time = dateRange.map { it.toString() },
                    temperature_2m_max = List(size) { 999.0},
                    temperature_2m_min = List(size) { 999.0},
                    weathercode = List(size) {120},
                    precipitation_sum = List(size) { 9999.0},
                    sunrise = List(size) {""},
                    sunset = List(size) {""},
                    windspeed_10m_max = List(size) {9999.0}
                )
            ),
            hourly = HourlyWeatherResponse(
                hourly = HourlyData(
                    time = dateRange.map { it.toString() },
                    temperature_2m = List(size) {999.0},
                    relative_humidity_2m = List(size) {999},
                    weather_code = List(size){120}
                )
            )
        )

        weatherMap[key] = dummyState
        _weatherStates.value = weatherMap
    }



    fun loadWeatherData(lat: Double, lon: Double, key: String, startDate: String, endDate: String) {
        if (weatherMap.containsKey(key)) return
        weatherMap[key] = WeatherUIState.Loading
        _weatherStates.update { it + (key to WeatherUIState.Loading) }
        val allowDate = LocalDate.now(ZoneId.of("Asia/Seoul")).plusDays(14L)
        viewModelScope.launch {
            try {
                val current = RetrofitInstance.openMeteoApi.getCurrentWeather(lat, lon)
                val daily = RetrofitInstance.dailyWeatherApi.getDailyForecast(lat, lon, startDate = startDate, endDate = endDate)
                val hourly = RetrofitInstance.hourlyWeatherApi.getHourlyWeather(lat, lon, start = startDate,end = minOf(allowDate,LocalDate.parse(startDate,
                    DateTimeFormatter.ISO_LOCAL_DATE).plusDays(2L)).toString())

                val successState = WeatherUIState.Success(current, daily, hourly)
                weatherMap[key] = successState
                _weatherStates.update { it + (key to successState) }
            } catch (e: Exception) {
                val errorState = WeatherUIState.Error(e.message ?: "알 수 없는 오류")
                weatherMap[key] = errorState
                _weatherStates.update { it + (key to errorState) }
            }
        }
    }
}