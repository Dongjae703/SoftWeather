package com.example.softweather.ui.implement.screen

import DateConverter
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.softweather.model.RetrofitInstance
import com.example.softweather.model.daily.DailyPastWeatherViewModelFactory
import com.example.softweather.model.daily.DailyWeatherViewModelFactory
import com.example.softweather.model.hourly.HourlyPastWeatherViewModelFactory
import com.example.softweather.model.hourly.HourlyWeatherViewModelFactory
import com.example.softweather.ui.implement.font.NotoSansKR
import com.example.softweather.ui.implement.tool.DayofWeekConverter
import com.example.softweather.ui.implement.tool.WeatherCodeConverter
import com.example.softweather.ui.implement.tool.WeatherIconGetter
import com.example.softweather.ui.implement.tool.getBodyTemperature
import com.example.softweather.viewmodel.DailyPastWeatherViewModel
import com.example.softweather.viewmodel.DailyWeatherViewModel
import com.example.softweather.viewmodel.HourlyPastWeatherViewModel
import com.example.softweather.viewmodel.HourlyWeatherViewModel
import com.example.softweather.viewmodel.WeatherViewModel
import com.example.softweather.viewmodel.WeatherViewModelFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WeatherInfoScreen(
    date: String, location: String, lat: Double, lon: Double
) {
    val now = LocalDate.now(ZoneId.of("Asia/Seoul"))
    val targetDate = LocalDate.parse(date)
    val isPast = targetDate.isBefore(now)
    val formatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dailyList = (0..14).map { offset -> targetDate.plusDays(offset.toLong()).format(formatterDay) }
    Log.d("dailyList","$dailyList")
    val (pastDates, futureDates) = dailyList.partition { LocalDate.parse(it,formatterDay).isBefore(now.minusDays(2L)) }

    val nowTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    val formatterHour = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00")
    val targetTimeStr = nowTime.format(formatterHour)

    val hourlyPastVM = viewModel<HourlyPastWeatherViewModel>(factory = HourlyPastWeatherViewModelFactory(RetrofitInstance.hourlyPastWeatherApi))
    val hourlyForeVM = viewModel<HourlyWeatherViewModel>(factory = HourlyWeatherViewModelFactory(RetrofitInstance.hourlyWeatherApi))
    val dailyPastVM = viewModel<DailyPastWeatherViewModel>(factory = DailyPastWeatherViewModelFactory(RetrofitInstance.dailyPastWeatherApi))
    val dailyForeVM = viewModel<DailyWeatherViewModel>(factory = DailyWeatherViewModelFactory(RetrofitInstance.dailyWeatherApi))

    val currentVM = viewModel<WeatherViewModel>(factory = WeatherViewModelFactory(RetrofitInstance.openMeteoApi))

    val hourlyList = hourlyPastVM.timeList.collectAsState().value+hourlyForeVM.timeList.collectAsState().value
    val humidityList = hourlyPastVM.humidity.collectAsState().value+hourlyForeVM.humidity.collectAsState().value
    val temperatureHourly = hourlyPastVM.temperature.collectAsState().value+hourlyForeVM.temperature.collectAsState().value
    val weatherCodeHourly = hourlyPastVM.weatherCode.collectAsState().value+hourlyForeVM.weatherCode.collectAsState().value

    val tempMaxList = dailyPastVM.temperature_max.collectAsState().value+dailyForeVM.temperature_max.collectAsState().value
    val tempMinList = dailyPastVM.temperature_min.collectAsState().value+dailyForeVM.temperature_min.collectAsState().value
    val precipitationList = dailyPastVM.precipitation_sum.collectAsState().value+dailyForeVM.precipitation_sum.collectAsState().value
    val sunriseList = dailyPastVM.sunrise.collectAsState().value+dailyForeVM.sunrise.collectAsState().value
    val sunsetList = dailyPastVM.sunset.collectAsState().value+dailyForeVM.sunset.collectAsState().value
    val windMaxList = dailyPastVM.windSpeed_max.collectAsState().value+dailyForeVM.windSpeed_max.collectAsState().value
    val weatherCodeDaily = dailyPastVM.weatherCode.collectAsState().value+dailyForeVM.weatherCode.collectAsState().value

    val temperature by currentVM.temperature.collectAsState()
    val windSpeed by currentVM.windSpeed.collectAsState()
    val windDirection by currentVM.windDirection.collectAsState()
    val weatherCodeNow by currentVM.weatherCode.collectAsState()

    var utilHourlyList = hourlyList
    if (!isPast) {
        val idx = hourlyList.indexOf(targetTimeStr)
        if (idx != -1) utilHourlyList = hourlyList.drop(idx)
    }

    LaunchedEffect(Unit) {
        Log.e("WeatherInfo", "🔥 API 요청 시작")
        Log.e("WeatherInfo", "📍 위도: $lat, 경도: $lon")
        Log.e("WeatherInfo", "📅 기준 날짜: $targetDate")
        Log.e("WeatherInfo", "🕒 시간 요청 범위: ${dailyList.first()} ~ ${dailyList.getOrNull(3) ?: dailyList.last()}")
        Log.e("WeatherInfo", "📆 일일 요청 범위: ${dailyList.first()} ~ ${dailyList.last()}")

        try {
            currentVM.fetchWeather(lat, lon)
            Log.e("WeatherInfo", "✅ 현재 날씨 fetch 완료")

            if (pastDates.isNotEmpty()) {
                hourlyPastVM.fetchHourlyWeather(lat, lon, pastDates.first(), pastDates.last())
            }
            if (futureDates.isNotEmpty()) {
                hourlyForeVM.fetchHourlyWeather(lat, lon, futureDates.first(), futureDates.last())
            }
            Log.e("WeatherInfo", "✅ 시간별 날씨 fetch 완료")

            if (pastDates.isNotEmpty()) {
                dailyPastVM.fetchDailyWeather(lat, lon, pastDates.first(), pastDates.last())
            }
            if (futureDates.isNotEmpty()) {
                dailyForeVM.fetchDailyWeather(lat, lon, futureDates.first(), futureDates.last())
            }
            Log.e("WeatherInfo", "✅ 일별 날씨 fetch 완료")
        } catch (e: Exception) {
            Log.e("WeatherInfo", "❌ 예외 발생: ${e.localizedMessage}")
        }
    }

    if (
        temperature != null &&
        tempMaxList.isNotEmpty() &&
        tempMinList.isNotEmpty() &&
        precipitationList.isNotEmpty() &&
        sunriseList.isNotEmpty() &&
        sunsetList.isNotEmpty() &&
        windMaxList.isNotEmpty() &&
        weatherCodeDaily.isNotEmpty() &&
        humidityList.isNotEmpty() &&
        temperatureHourly.isNotEmpty() &&
        weatherCodeHourly.isNotEmpty() &&
        hourlyList.isNotEmpty()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            val index = hourlyList.indexOf(targetTimeStr)
            val rh = humidityList.getOrNull(index) ?: 50
            val bodyTemp = getBodyTemperature(temperature,windSpeed,rh)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 240.dp)
                        .weight(0.3f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(date, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = NotoSansKR)
                    Text(location, fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
                    Text("${temperature}°", fontSize = 48.sp, fontWeight = FontWeight.Bold, fontFamily = NotoSansKR)
                    Text(WeatherIconGetter(weatherCodeNow), fontSize = 36.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
                    Text(WeatherCodeConverter(weatherCodeNow), fontSize = 24.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
                    Text("최고 : ${tempMaxList[0]}°  최저 : ${tempMinList[0]}°", fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp) // 기존보다 최소 높이를 여유 있게 줌
                        .weight(0.3f), // 기존 0.25f에서 약간 늘려줌
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val sunsetDay=LocalDateTime.parse(sunsetList[0]).format(formatterDay)
                    var sunsetText = ""
                    if (LocalDate.parse(sunsetDay).isBefore(LocalDate.parse(date))){
                        sunsetText = "(익일)"
                    }
                    WeatherDetailRow("습도", humidityList[index]?.let { "$it %" }?:"정보 없음")
                    WeatherDetailRow("체감온도", bodyTemp?.let { "${"%.1f".format(bodyTemp)}°" }?:"정보 없음")
                    WeatherDetailRow("풍속", windSpeed?.let { "$it m/s" }?:"정보 없음",
                        windDirection?:1080.0
                    )
                    WeatherDetailRow("강수량", precipitationList[0]?.let { "$it mm" }?:"정보 없음")
                    WeatherDetailRow(
                        "일출/일몰",
                        "${DateConverter(sunriseList[0]?:"오류", useHour=true, useMin=true)} / ${sunsetText}${DateConverter(sunsetList[0]?:"오류", useHour=true, useMin=true)}"
                    )
                    WeatherDetailRow("최대 풍속", "${windMaxList[0]}m/s")
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp) // weight 대신 고정 height
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Log.d("infoScreen","${utilHourlyList.size}")
                    Log.d("infoScreen","${temperatureHourly.size}")
                    items(utilHourlyList.take(26)) { hourly ->
                        val idx = hourlyList.indexOf(hourly)
                        WeatherHourItem(hourly, weatherCodeHourly[idx]?:120, temperatureHourly[idx]?:0.0)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.35f),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    itemsIndexed(dailyList) { index, daily ->
                        val max = tempMaxList.getOrNull(index)
                        val min = tempMinList.getOrNull(index)
                        WeatherDayItem(daily, weatherCodeDaily[index], min,max)
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("날씨 정보를 불러오는 중 입니다...", fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
        }
    }
}

@Composable
fun WeatherDetailRow(label: String, value: String, windDirection : Double = 1080.0) {
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(DateConverter(time, useHour = true), fontSize = 13.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
        Spacer(modifier = Modifier.height(2.dp))
        Text(WeatherIconGetter(weatherCode), fontSize = 24.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
        Spacer(modifier = Modifier.height(2.dp))
        Text(WeatherCodeConverter(weatherCode), fontSize = 12.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
        Spacer(modifier = Modifier.height(2.dp))
        Text("${temperature}°", fontSize = 13.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
    }
}

@Composable
fun WeatherDayItem(day: String, weatherCode: Int?, temperatureMin: Double?, temperatureMax: Double?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(DayofWeekConverter(day), modifier = Modifier.width(40.dp), fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
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


        Text("$temperatureMin°/$temperatureMax", fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
    }
}