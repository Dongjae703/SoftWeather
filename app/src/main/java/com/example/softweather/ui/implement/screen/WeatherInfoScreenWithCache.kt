package com.example.softweather.ui.implement.screen

import DateConverter
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.softweather.model.WeatherUIState
import com.example.softweather.model.daily.DailyData
import com.example.softweather.model.hourly.HourlyData
import com.example.softweather.ui.implement.font.NotoSansKR
import com.example.softweather.ui.implement.tool.DayofWeekConverter
import com.example.softweather.ui.implement.tool.WeatherCodeConverter
import com.example.softweather.ui.implement.tool.WeatherIconGetter
import com.example.softweather.viewmodel.PastWeatherRepoViewModel
import com.example.softweather.viewmodel.WeatherRepoViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WeatherInfoScreenWithCache(
    locationKey: String,
    weatherRepoVM: WeatherRepoViewModel,
    pastRepoVM: PastWeatherRepoViewModel,
    locationName: String,
    lat: Double,
    lon: Double,
    date: String,
    isPast: Boolean
) {
    val currentState by weatherRepoVM.getWeatherState(locationKey)
        .collectAsState(initial = WeatherUIState.Loading)
    val pastState by pastRepoVM.getWeatherState(locationKey)
        .collectAsState(initial = WeatherUIState.Loading)

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val targetDate = LocalDate.parse(date, formatter)
    val endDate = targetDate.plusDays(14L)
    LaunchedEffect(locationKey) {
        Log.d("WeatherVM", "loadWeatherData 호출됨: lat=$lat, lon=$lon, start=$date, end=$endDate")
        weatherRepoVM.loadWeatherData(lat, lon, locationKey, date, endDate.toString())
        if(isPast)
            pastRepoVM.loadPastWeather(lat, lon, locationKey, date, endDate.toString())
    }

    if (isPast) {
        Log.e("Cache", pastState.toString())
        Log.e("Cache2", currentState.toString())
        // ✅ 과거: 두 개 병합
        when {
            pastState is WeatherUIState.Success && (currentState is WeatherUIState.Success || currentState is WeatherUIState.Error) -> {
                val past = pastState as WeatherUIState.Success
                val isAllPast = currentState is WeatherUIState.Error
                if (isAllPast) {

                    val today = LocalDate.now(ZoneId.of("Asia/Seoul"))

                    val mergedDaily = DailyData(
                        time = mutableListOf(),
                        temperature_2m_max = mutableListOf(),
                        temperature_2m_min = mutableListOf(),
                        weathercode = mutableListOf(),
                        precipitation_sum = mutableListOf(),
                        sunrise = mutableListOf(),
                        sunset = mutableListOf(),
                        windspeed_10m_max = mutableListOf()
                    )

                    past.daily.daily.time.forEachIndexed { i, dateStr ->
                        val dateLocal = LocalDate.parse(dateStr, formatter)
                        if (dateLocal.isBefore(today)) {
                            mergedDaily.time += dateStr
                            mergedDaily.temperature_2m_max += past.daily.daily.temperature_2m_max[i]
                            mergedDaily.temperature_2m_min += past.daily.daily.temperature_2m_min[i]
                            mergedDaily.weathercode += past.daily.daily.weathercode[i]
                            mergedDaily.precipitation_sum += past.daily.daily.precipitation_sum[i]
                            mergedDaily.sunrise += past.daily.daily.sunrise[i]
                            mergedDaily.sunset += past.daily.daily.sunset[i]
                            mergedDaily.windspeed_10m_max += past.daily.daily.windspeed_10m_max[i]
                        }
                    }
                    val formatterHour = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00")
                    val todayDateTime = LocalDate.now(ZoneId.of("Asia/Seoul")).atStartOfDay()

                    val mergedHourly = HourlyData(
                        time = mutableListOf(),
                        temperature_2m = mutableListOf(),
                        relative_humidity_2m = mutableListOf(),
                        weather_code = mutableListOf()
                    )

                    past.hourly.hourly.time.forEachIndexed { i, timeStr ->
                        val dateTime = LocalDateTime.parse(timeStr, formatterHour)
                        if (dateTime.isBefore(todayDateTime)) {
                            mergedHourly.time += timeStr
                            mergedHourly.temperature_2m += past.hourly.hourly.temperature_2m[i]
                            mergedHourly.relative_humidity_2m += past.hourly.hourly.relative_humidity_2m[i]
                            mergedHourly.weather_code += past.hourly.hourly.weather_code[i]
                        }
                    }

                    val weatherModel = WeatherMergedModel(
                        daily = mergedDaily.time.indices.map { i ->
                            DailyTuple(
                                date = mergedDaily.time[i],
                                tempMax = mergedDaily.temperature_2m_max[i],
                                tempMin = mergedDaily.temperature_2m_min[i],
                                precip = mergedDaily.precipitation_sum[i],
                                sunrise = mergedDaily.sunrise[i],
                                sunset = mergedDaily.sunset[i],
                                windMax = mergedDaily.windspeed_10m_max[i],
                                code = mergedDaily.weathercode[i]
                            )
                        },
                        hourly = mergedHourly.time.indices.map { i ->
                            HourlyTuple(
                                time = mergedHourly.time[i],
                                temperature = mergedHourly.temperature_2m[i],
                                humidity = mergedHourly.relative_humidity_2m[i],
                                code = mergedHourly.weather_code[i]
                            )
                        }
                    )
                    WeatherInfoContentPast(
                        date = date,
                        location = locationName,
                        weather = weatherModel
                    )
                } else{
                val future = currentState as WeatherUIState.Success

                val today = LocalDate.now(ZoneId.of("Asia/Seoul"))

                val mergedDaily = DailyData(
                    time = mutableListOf(),
                    temperature_2m_max = mutableListOf(),
                    temperature_2m_min = mutableListOf(),
                    weathercode = mutableListOf(),
                    precipitation_sum = mutableListOf(),
                    sunrise = mutableListOf(),
                    sunset = mutableListOf(),
                    windspeed_10m_max = mutableListOf()
                )

                past.daily.daily.time.forEachIndexed { i, dateStr ->
                    val dateLocal = LocalDate.parse(dateStr, formatter)
                    if (dateLocal.isBefore(today)) {
                        mergedDaily.time += dateStr
                        mergedDaily.temperature_2m_max += past.daily.daily.temperature_2m_max[i]
                        mergedDaily.temperature_2m_min += past.daily.daily.temperature_2m_min[i]
                        mergedDaily.weathercode += past.daily.daily.weathercode[i]
                        mergedDaily.precipitation_sum += past.daily.daily.precipitation_sum[i]
                        mergedDaily.sunrise += past.daily.daily.sunrise[i]
                        mergedDaily.sunset += past.daily.daily.sunset[i]
                        mergedDaily.windspeed_10m_max += past.daily.daily.windspeed_10m_max[i]
                    }
                }


                future.daily.daily.time.forEachIndexed { i, dateStr ->
                    val dateLocal = LocalDate.parse(dateStr, formatter)
                    if (!dateLocal.isBefore(today)) {
                        mergedDaily.time += dateStr
                        mergedDaily.temperature_2m_max += future.daily.daily.temperature_2m_max[i]
                        mergedDaily.temperature_2m_min += future.daily.daily.temperature_2m_min[i]
                        mergedDaily.weathercode += future.daily.daily.weathercode[i]
                        mergedDaily.precipitation_sum += future.daily.daily.precipitation_sum[i]
                        mergedDaily.sunrise += future.daily.daily.sunrise[i]
                        mergedDaily.sunset += future.daily.daily.sunset[i]
                        mergedDaily.windspeed_10m_max += future.daily.daily.windspeed_10m_max[i]
                    }
                }

                val formatterHour = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00")
                val todayDateTime = today.atStartOfDay()

                val mergedHourly = HourlyData(
                    time = mutableListOf(),
                    temperature_2m = mutableListOf(),
                    relative_humidity_2m = mutableListOf(),
                    weather_code = mutableListOf()
                )

                past.hourly.hourly.time.forEachIndexed { i, timeStr ->
                    val dateTime = LocalDateTime.parse(timeStr, formatterHour)
                    if (dateTime.isBefore(todayDateTime)) {
                        mergedHourly.time += timeStr
                        mergedHourly.temperature_2m += past.hourly.hourly.temperature_2m[i]
                        mergedHourly.relative_humidity_2m += past.hourly.hourly.relative_humidity_2m[i]
                        mergedHourly.weather_code += past.hourly.hourly.weather_code[i]
                    }
                }

                future.hourly.hourly.time.forEachIndexed { i, timeStr ->
                    val dateTime = LocalDateTime.parse(timeStr, formatterHour)
                    if (!dateTime.isBefore(todayDateTime)) {
                        mergedHourly.time += timeStr
                        mergedHourly.temperature_2m += future.hourly.hourly.temperature_2m[i]
                        mergedHourly.relative_humidity_2m += future.hourly.hourly.relative_humidity_2m[i]
                        mergedHourly.weather_code += future.hourly.hourly.weather_code[i]
                    }
                }

                val weatherModel = WeatherMergedModel(
                    daily = mergedDaily.time.indices.map { i ->
                        DailyTuple(
                            date = mergedDaily.time[i],
                            tempMax = mergedDaily.temperature_2m_max[i],
                            tempMin = mergedDaily.temperature_2m_min[i],
                            precip = mergedDaily.precipitation_sum[i],
                            sunrise = mergedDaily.sunrise[i],
                            sunset = mergedDaily.sunset[i],
                            windMax = mergedDaily.windspeed_10m_max[i],
                            code = mergedDaily.weathercode[i]
                        )
                    },
                    hourly = mergedHourly.time.indices.map { i ->
                        HourlyTuple(
                            time = mergedHourly.time[i],
                            temperature = mergedHourly.temperature_2m[i],
                            humidity = mergedHourly.relative_humidity_2m[i],
                            code = mergedHourly.weather_code[i]
                        )
                    }
                )
                WeatherInfoContentPast(
                    date = date,
                    location = locationName,
                    weather = weatherModel
                )
            }


        }

        pastState is WeatherUIState.Loading || currentState is WeatherUIState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        pastState is WeatherUIState.Error && currentState is WeatherUIState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("오류: ${(pastState as WeatherUIState.Error).message}")
            }
        }

        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("날씨 데이터를 불러오는 중...")
            }
        }
    }
} else {
    // ✅ 미래: 단일 VM 사용
    when (currentState) {
        is WeatherUIState.Success -> {
            val state = currentState as WeatherUIState.Success
            WeatherInfoContent(
                date = date,
                location = locationName,
                current = state.current,
                daily = state.daily,
                hourly = state.hourly
            )
        }

        is WeatherUIState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is WeatherUIState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("오류: ${(currentState as WeatherUIState.Error).message}")
            }
        }

        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("날씨 데이터를 불러오는 중...")
            }
        }
    }
}
}

data class WeatherMergedModel(
    val daily: List<DailyTuple>,
    val hourly: List<HourlyTuple>
)

@Composable
fun WeatherDetailRow(label: String, value: String, windDirection: Double = 1080.0) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (windDirection != 1080.0) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(start = 2.dp)
                        .rotate(windDirection.toFloat()),
                    tint = Color.Gray
                )
            }
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
        }
    }
}


@Composable
fun WeatherHourItem(time: String, weatherCode: Int, temperature: Double) {
    Column(modifier = Modifier.width(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            DateConverter(time, useHour = true),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansKR
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            WeatherIconGetter(weatherCode),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansKR
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            WeatherCodeConverter(weatherCode),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansKR
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = if (temperature != 999.0)"${temperature}°" else "오류",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansKR
        )
    }
}

@Composable
fun WeatherDayItem(
    day: String,
    weatherCode: Int?,
    temperatureMin: Double?,
    temperatureMax: Double?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth().height(45.dp)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            DayofWeekConverter(day),
            modifier = Modifier.width(40.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansKR
        )
        Text(
            WeatherIconGetter(weatherCode),
            fontSize = 22.sp,
            modifier = Modifier.padding(horizontal = 6.dp),
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansKR
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                WeatherCodeConverter(weatherCode),
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                fontFamily = NotoSansKR
            )
        }


        Text(
            "$temperatureMin°/$temperatureMax",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansKR
        )
    }
}
