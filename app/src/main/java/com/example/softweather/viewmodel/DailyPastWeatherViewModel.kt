package com.example.softweather.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softweather.model.daily.DailyPastWeatherApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DailyPastWeatherViewModel(
    private val api: DailyPastWeatherApi
) : ViewModel(), DailyWeatherVMContract {

    private val _temperature_max = MutableStateFlow<List<Double?>>(emptyList())
    private val _temperature_min = MutableStateFlow<List<Double?>>(emptyList())
    private val _precipitation_sum = MutableStateFlow<List<Double?>>(emptyList())
    private val _sunrise = MutableStateFlow<List<String?>>(emptyList())
    private val _sunset = MutableStateFlow<List<String?>>(emptyList())
    private val _windspeed_max = MutableStateFlow<List<Double?>>(emptyList())
    private val _weatherCode = MutableStateFlow<List<Int?>>(emptyList())

    override val temperature_max: StateFlow<List<Double?>> = _temperature_max
    override val temperature_min: StateFlow<List<Double?>> = _temperature_min
    override val precipitation_sum: StateFlow<List<Double?>> = _precipitation_sum
    override val sunrise: StateFlow<List<String?>> = _sunrise
    override val sunset: StateFlow<List<String?>> = _sunset
    override val windSpeed_max: StateFlow<List<Double?>> = _windspeed_max
    override val weatherCode: StateFlow<List<Int?>> = _weatherCode

    override fun fetchDailyWeather(lat: Double, lon: Double, startDate: String, endDate: String) {
        viewModelScope.launch {
            Log.e("DailyPastWeatherVM", "📤 요청 시작 → lat=$lat, lon=$lon")
            Log.e("DailyPastWeatherVM", "📅 날짜 범위: $startDate ~ $endDate")

            try {
                val response = api.getDailyArchive(
                    lat = lat,
                    lon = lon,
                    startDate = startDate,
                    endDate = endDate,
                    daily = "temperature_2m_max,temperature_2m_min,precipitation_sum,sunrise,sunset,weathercode,windspeed_10m_max",
                    timezone = "Asia/Seoul"
                )

                Log.e("DailyPastWeatherVM", "✅ 응답 성공, daily=${response.daily}")

                val dailyWeather = response.daily
                if (dailyWeather != null) {
                    _temperature_max.value = dailyWeather.temperature_2m_max
                    _temperature_min.value = dailyWeather.temperature_2m_min
                    _precipitation_sum.value = dailyWeather.precipitation_sum
                    _sunrise.value = dailyWeather.sunrise
                    _sunset.value = dailyWeather.sunset
                    _windspeed_max.value = dailyWeather.windspeed_10m_max
                    _weatherCode.value = dailyWeather.weathercode
                } else {
                    Log.e("DailyPastWeatherVM", "⚠️ dailyWeather == null")
                }

            } catch (e: retrofit2.HttpException) {
                Log.e("DailyPastWeatherVM", "❌ HTTP 오류: ${e.code()} ${e.message()}")
                Log.e("DailyPastWeatherVM", "❌ ErrorBody: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("DailyPastWeatherVM", "❌ 예외 발생: ${e.localizedMessage}")
            }
        }
    }
}
