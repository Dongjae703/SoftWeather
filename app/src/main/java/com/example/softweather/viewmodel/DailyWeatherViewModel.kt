package com.example.softweather.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softweather.model.daily.DailyWeatherApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DailyWeatherViewModel(
    private val api: DailyWeatherApi
) : ViewModel(), DailyWeatherVMContract {
    private val _temperature_max = MutableStateFlow<List<Double?>>(emptyList())
    private val _temperature_min = MutableStateFlow<List<Double?>>(emptyList())
    private val _precipitation_sum = MutableStateFlow<List<Double?>>(emptyList())
    private val _sunrise = MutableStateFlow<List<String?>>(emptyList())
    private val _sunset = MutableStateFlow<List<String?>>(emptyList())
    private val _windspeed_max = MutableStateFlow<List<Double?>>(emptyList())
    private val _weatherCode = MutableStateFlow<List<Int?>>(emptyList())

    override val temperature_max: StateFlow<List<Double?>> = _temperature_max
    override val temperature_min : StateFlow<List<Double?>> = _temperature_min
    override val precipitation_sum : StateFlow<List<Double?>> = _precipitation_sum
    override val sunrise : StateFlow<List<String?>> = _sunrise
    override val sunset : StateFlow<List<String?>> = _sunset
    override val windSpeed_max : StateFlow<List<Double?>> = _windspeed_max
    override val weatherCode : StateFlow<List<Int?>> = _weatherCode

    override fun fetchDailyWeather(lat: Double, lon: Double, startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                Log.e("DailyWeatherVM", "📤 요청 시작 → lat=$lat, lon=$lon")
                Log.e("DailyWeatherVM", "📅 날짜 범위: $startDate ~ $endDate")
                val response = api.getDailyForecast(lat, lon, startDate = startDate, endDate = endDate)
                val dailyWeather = response.daily
                Log.e("DailyWeatherVM", "✅ 응답 성공, daily=${response.daily}")
                if(dailyWeather!=null){
                    _temperature_max.value = dailyWeather.temperature_2m_max
                    _temperature_min.value = dailyWeather.temperature_2m_min
                    _precipitation_sum.value = dailyWeather.precipitation_sum
                    _sunrise.value = dailyWeather.sunrise
                    _sunset.value = dailyWeather.sunset
                    _windspeed_max.value =dailyWeather.windspeed_10m_max
                    _weatherCode.value = dailyWeather.weathercode
                }else {
                    Log.e("WeatherVM", "currentWeather == null")
                }

            } catch (e: Exception) {
                Log.e("HumidityVM", "에러: ${e.message}")
            }
        }
    }
}