package com.example.softweather.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.softweather.model.RetrofitInstance
import com.example.softweather.model.daily.DailyPastWeatherViewModelFactory
import com.example.softweather.model.daily.DailyWeatherViewModelFactory
import com.example.softweather.ui.implement.tool.WeatherCodeConverter
import com.example.softweather.viewmodel.DailyPastWeatherViewModel
import com.example.softweather.viewmodel.DailyWeatherViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DailyWeatherScreen(lat: Double, lon: Double) {
    val foreViewModel: DailyWeatherViewModel = viewModel(
        factory = DailyWeatherViewModelFactory(RetrofitInstance.dailyWeatherApi)
    )
    val pastViewModel: DailyPastWeatherViewModel = viewModel(
        factory = DailyPastWeatherViewModelFactory(RetrofitInstance.dailyPastWeatherApi)
    )
    var targetDate by remember { mutableStateOf("") }
    var viewModelSelector by remember { mutableStateOf(-1) }
    var temperature_max: List<Double?>
    var temperature_min: List<Double?>
    var precipitation_sum: List<Double?>
    var sunrise: List<String?>
    var sunset: List<String?>
    var windSpeed_max: List<Double?>
    var weatherCode: List<Int?>
    val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val targetTimeStr = now.format(formatter)
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = targetDate,
            onValueChange = {
                targetDate = it
                try {

                    val inputDate = LocalDate.parse(it)
                    val today = LocalDate.parse(targetTimeStr)
                    if (inputDate.isBefore(today))
                        viewModelSelector = 0
                    else
                        viewModelSelector = 1
                } catch (e: Exception) {
                    viewModelSelector = -1
                }
            },
            label = { Text("날짜 입력 (예 : 2024-05-20") },
            modifier = Modifier.fillMaxWidth()
        )
        LaunchedEffect(lat, lon, targetDate) {
            Log.d("WeatherScreen", "lat: $lat, lon: $lon")
            if (viewModelSelector == 1) {
                val viewModel = foreViewModel
                viewModel.fetchDailyWeather(lat, lon, targetDate, targetDate)

            } else if (viewModelSelector == 0) {
                val viewModel = pastViewModel
                viewModel.fetchDailyWeather(lat, lon, targetDate, targetDate)
            } else {
                val viewModel = foreViewModel
                viewModel.fetchDailyWeather(lat, lon, targetTimeStr, targetTimeStr)
            }
        }
        if (viewModelSelector == 0) {
            temperature_max = pastViewModel.temperature_max.collectAsState().value
            temperature_min = pastViewModel.temperature_min.collectAsState().value
            precipitation_sum = pastViewModel.precipitation_sum.collectAsState().value
            sunrise = pastViewModel.sunrise.collectAsState().value
            sunset = pastViewModel.sunset.collectAsState().value
            windSpeed_max = pastViewModel.windSpeed_max.collectAsState().value
            weatherCode = pastViewModel.weatherCode.collectAsState().value
        } else {
            temperature_max = foreViewModel.temperature_max.collectAsState().value
            temperature_min = foreViewModel.temperature_min.collectAsState().value
            precipitation_sum = foreViewModel.precipitation_sum.collectAsState().value
            sunrise = foreViewModel.sunrise.collectAsState().value
            sunset = foreViewModel.sunset.collectAsState().value
            windSpeed_max = foreViewModel.windSpeed_max.collectAsState().value
            weatherCode = foreViewModel.weatherCode.collectAsState().value
        }
        temperature_max.let {
            Text("최고 온도: $it°C", style = MaterialTheme.typography.titleLarge)
        } ?: Text("최고 온도 정보를 불러오는 중...")
        temperature_min.let {
            Text("최저 온도: $it°C", style = MaterialTheme.typography.titleLarge)
        } ?: Text("최저 온도 정보를 불러오는 중...")
        precipitation_sum.let {
            Text("강수량: ${it}mm", style = MaterialTheme.typography.titleLarge)
        } ?: Text("강수량 정보를 불러오는 중...")
        sunrise.let {
            Text("일출: ${it}", style = MaterialTheme.typography.titleLarge)
        } ?: Text("일출 정보를 불러오는 중...")
        sunset.let {
            Text("일몰: ${it}", style = MaterialTheme.typography.titleLarge)
        } ?: Text("일몰 정보를 불러오는 중...")
        windSpeed_max.let {
            Text("최대 풍속: ${it}km/h", style = MaterialTheme.typography.titleLarge)
        } ?: Text("최대 풍속 정보를 불러오는 중...")
        WeatherCodeConverter(weatherCode[0])
    }
}