package com.example.softweather.ui.implement.screen

import DateConverter
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.softweather.model.currentday.OpenMeteoResponse
import com.example.softweather.model.daily.DailyWeatherResponse
import com.example.softweather.model.hourly.HourlyWeatherResponse
import com.example.softweather.ui.implement.font.NotoSansKR
import com.example.softweather.ui.implement.tool.WeatherCodeConverter
import com.example.softweather.ui.implement.tool.WeatherIconGetter
import com.example.softweather.ui.implement.tool.getBodyTemperature
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WeatherInfoContent(
    date: String,
    location: String,
    current: OpenMeteoResponse?,
    daily: DailyWeatherResponse,
    hourly: HourlyWeatherResponse
) {
    val now = LocalDate.now(ZoneId.of("Asia/Seoul"))
    val targetDate = LocalDate.parse(date)
    val isPast = targetDate.isBefore(now)
    val formatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dailyList =
        (0..14).map { offset -> targetDate.plusDays(offset.toLong()).format(formatterDay) }
    Log.d("dailyListContent", "$dailyList")
//    val (pastDates, futureDates) = dailyList.partition {
//        LocalDate.parse(it, formatterDay).isBefore(now.minusDays(2L))
//    }

    val nowTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    val formatterHour = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00")
    val targetTimeStr = nowTime.format(formatterHour)

    val hourlyList = hourly.hourly.time
    val humidityList = hourly.hourly.relative_humidity_2m
    val temperatureHourly = hourly.hourly.temperature_2m
    val weatherCodeHourly = hourly.hourly.weather_code

    val tempMaxList = daily.daily.temperature_2m_max
    val tempMinList = daily.daily.temperature_2m_min
    val precipitationList = daily.daily.precipitation_sum
    val sunriseList = daily.daily.sunrise
    val sunsetList = daily.daily.sunset
    val windMaxList = daily.daily.windspeed_10m_max
    val weatherCodeDaily = daily.daily.weathercode

    val temperature = current?.currentWeather?.temperature
    val windSpeed = current?.currentWeather?.windspeed
    val windDirection = current?.currentWeather?.winddirection
    val weatherCodeNow = current?.currentWeather?.weathercode


    var utilHourlyList = hourlyList
    if (!isPast) {
        val idx = hourlyList.indexOf(targetTimeStr)
        if (idx != -1) utilHourlyList = hourlyList.drop(idx)
    }

    if (
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
            val bodyTemp = getBodyTemperature(temperature, windSpeed, rh)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 300.dp)
                        .weight(0.35f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        date,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = NotoSansKR
                    )
                    Text(
                        location,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = NotoSansKR
                    )
                    Text(
                        "${temperature}°",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = NotoSansKR
                    )
                    Text(
                        WeatherIconGetter(weatherCodeNow),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = NotoSansKR
                    )
                    Text(
                        WeatherCodeConverter(weatherCodeNow),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = NotoSansKR
                    )
                    Text(
                        "최고 : ${tempMaxList[0]}°  최저 : ${tempMinList[0]}°",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = NotoSansKR
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp) // 기존보다 최소 높이를 여유 있게 줌
                        .weight(0.3f), // 기존 0.25f에서 약간 늘려줌
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val sunsetDay = LocalDateTime.parse(sunsetList[0]).format(formatterDay)
                    val sunsetText = if (LocalDate.parse(sunsetDay).isAfter(LocalDate.parse(date))) "(익일)" else ""
                    WeatherDetailRow("습도", humidityList.getOrNull(index)?.let { "$it %" } ?: "정보 없음")
                    WeatherDetailRow(
                        "체감온도",
                        bodyTemp?.let { "${"%.1f".format(bodyTemp)}°" } ?: "정보 없음"
                    )
                    WeatherDetailRow(
                        "풍속", windSpeed?.let { "$it m/s" } ?: "정보 없음",
                        windDirection ?: 1080.0
                    )
                    WeatherDetailRow("강수량", precipitationList.getOrNull(0)?.let { "$it mm" } ?: "정보 없음")
                    WeatherDetailRow(
                        "일출/일몰",
                        "${
                            DateConverter(
                                sunriseList.getOrNull(0) ?: "오류",
                                useHour = true,
                                useMin = true
                            )
                        } / ${sunsetText}${
                            DateConverter(
                                sunsetList.getOrNull(0) ?: "오류",
                                useHour = true,
                                useMin = true
                            )
                        }"
                    )
                    WeatherDetailRow("최대 풍속", "${windMaxList[0]}m/s")
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp) // weight 대신 고정 height
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Log.d("infoScreen", "${utilHourlyList.size}")
                    Log.d("infoScreen", "${temperatureHourly.size}")
                    items(utilHourlyList.take(26)) { hourly ->
                        val idx = hourlyList.indexOf(hourly)
                        WeatherHourItem(
                            hourly,
                            weatherCodeHourly.getOrNull(idx) ?: 120,
                            temperatureHourly.getOrNull(idx) ?: 999.0
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    itemsIndexed(dailyList) { index, daily ->
                        val max = tempMaxList.getOrNull(index)
                        val min = tempMinList.getOrNull(index)
                        WeatherDayItem(daily, weatherCodeDaily[index], min, max)
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