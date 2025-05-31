package com.example.softweather.ui.component

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.softweather.model.RetrofitInstance
import com.example.softweather.model.hourly.HourlyWeatherViewModelFactory
import com.example.softweather.viewmodel.DBViewModel
import com.example.softweather.viewmodel.HourlyWeatherViewModel
import com.example.softweather.viewmodel.WeatherViewModel
import com.example.softweather.viewmodel.WeatherViewModelFactory
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WeatherScreen(lat: Double, lon: Double, navController: NavController) {
    val viewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(RetrofitInstance.openMeteoApi)
    )
    val hourViewModel : HourlyWeatherViewModel = viewModel(
        factory = HourlyWeatherViewModelFactory(RetrofitInstance.hourlyWeatherApi)
    )
    val context = LocalContext.current
    val dbViewModel : DBViewModel = viewModel<DBViewModel>(factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application))
    val locationText = remember { mutableStateOf("위치 정보 없음") }

    val temperature by viewModel.temperature.collectAsState()
    val windSpeed by viewModel.windSpeed.collectAsState()
    val windDirection by viewModel.windDirection.collectAsState()
    val weatherCode by viewModel.weatherCode.collectAsState()
    val huminity by hourViewModel.humidity.collectAsState()

    val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00")
    val targetTimeStr = now.format(formatter)

    var hourlyList = hourViewModel.timeList.collectAsState().value
    val index = hourlyList.indexOf(targetTimeStr)


    LaunchedEffect(lat, lon) {
        Log.d("WeatherScreen", "lat: $lat, lon: $lon")
        viewModel.fetchWeather(lat, lon)
        hourViewModel.fetchHourlyWeather(lat, lon,targetTimeStr,targetTimeStr)
        dbViewModel.loadAllLocations { list ->
            locationText.value = list.joinToString("\n") { it.l_name + "/"+ it.lat + "/" + it.lon }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        temperature?.let { temp ->
            Text("현재 온도: $temp°C", style = MaterialTheme.typography.titleLarge)

            val windKmh = windSpeed
            val rh = huminity[index]?.toDouble()
            var bodyTemp: Double? = null

            if (temp <= 10.0 && windKmh != null && windKmh >= 4.8) {
                bodyTemp = 13.12 + 0.6215 * temp - 11.37 * Math.pow(
                    windKmh,
                    0.16
                ) + 0.3965 * temp * Math.pow(windKmh, 0.16)
            } else if (temp >= 27.0 && rh != null && rh >= 40.0) {
                val tempF = temp * 9 / 5 + 32
                val indexF =
                    -42.379 + 2.04901523 * tempF + 10.14333127 * rh -
                            0.22475541 * tempF * rh - 0.00683783 * tempF * tempF -
                            0.05481717 * rh * rh + 0.00122874 * tempF * tempF * rh +
                            0.00085282 * tempF * rh * rh - 0.00000199 * tempF * tempF * rh * rh
                bodyTemp = (indexF - 32) * 5 / 9
            }

            bodyTemp?.let {
                Text("현재 체감온도: ${"%.1f".format(it)}°C", style = MaterialTheme.typography.titleLarge)
            } ?: Text("체감 온도 없음")
        }?: Text("온도 정보를 불러오는 중...")
        windSpeed?.let {
            Text("현재 풍속: ${it}km/h", style = MaterialTheme.typography.titleLarge)
        } ?: Text("풍속 정보를 불러오는 중...")
        windDirection?.let {
            Text("현재 풍향: $it°", style = MaterialTheme.typography.titleLarge)
        } ?: Text("풍향 정보를 불러오는 중...")
        huminity?.let {
            Text("현재 습도: $it%", style = MaterialTheme.typography.titleLarge)
        } ?: Text("습도 정보를 불러오는 중...")

//        WeatherCodeConverter(weatherCode)
//        Button(onClick = {navController.navigate( Routes..createRoute(lat.toString(), lon.toString()))}) { }
//        Text(locationText.value)
    }
}