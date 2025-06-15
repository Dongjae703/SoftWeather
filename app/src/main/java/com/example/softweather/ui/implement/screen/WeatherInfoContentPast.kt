package com.example.softweather.ui.implement.screen

import DateConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.softweather.ui.implement.font.NotoSansKR
import com.example.softweather.ui.implement.tool.WeatherCodeConverter
import com.example.softweather.ui.implement.tool.WeatherIconGetter
import com.example.softweather.ui.implement.tool.getBodyTemperature
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WeatherInfoContentPast(
    date: String,
    location: String,
    weather: WeatherMergedModel
) {
    val targetDate = LocalDate.parse(date)
    val formatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formatterHour = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00")

    val fullDates = (0..14).map { targetDate.plusDays(it.toLong()) }

    val pastHourlyMap = weather.hourly.groupBy { it.time.take(10) }
    val mergedHourly = fullDates.flatMap { dateObj ->
        val key = dateObj.format(formatterDay)
        pastHourlyMap[key] ?: emptyList()
    }

    val mergedDaily = weather.daily.sortedBy { it.date }

    val nowTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    val targetTimeStr = nowTime.format(formatterHour)
    val hourlyList = (0 until 15 * 24).map { hourOffset ->
        targetDate.atStartOfDay().plusHours(hourOffset.toLong()).format(formatterHour)
    }
    val temperatureHourly = mergedHourly.map { it.temperature }
    val humidityList = mergedHourly.map { it.humidity }
    val weatherCodeHourly = mergedHourly.map { it.code }

    val tempMaxList = mergedDaily.map { it.tempMax }
    val tempMinList = mergedDaily.map { it.tempMin }
    val precipitationList = mergedDaily.map { it.precip }
    val sunriseList = mergedDaily.map { it.sunrise }
    val sunsetList = mergedDaily.map { it.sunset }
    val windMaxList = mergedDaily.map { it.windMax }
    val weatherCodeDaily = mergedDaily.map { it.code }

    val averTemp = temperatureHourly.take(24).sum()/24
    val averHumidity = humidityList.take(24).sum()/24

    val idx = hourlyList.indexOf(targetTimeStr)
    val utilHourlyList = if (idx != -1) hourlyList.drop(idx) else hourlyList
    val rh = humidityList.getOrNull(idx) ?: averHumidity
    val bodyTemp = getBodyTemperature(averTemp, windMaxList[0], rh)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.65f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(date, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = NotoSansKR)
                        Text(location, fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
                        Text("${"%.1f".format(averTemp)}° (평균)", fontSize = 36.sp, fontWeight = FontWeight.Bold, fontFamily = NotoSansKR)
                        Text(WeatherIconGetter(weatherCodeDaily.getOrNull(0) ?: 120), fontSize = 24.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
                        Text(WeatherCodeConverter(weatherCodeDaily.getOrNull(0) ?: 120), fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
                        Text("최고 : ${tempMaxList[0]}°  최저 : ${tempMinList[0]}°", fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = NotoSansKR)
                    }
                }

                item {
                    val sunsetDay = LocalDateTime.parse(sunsetList[0]).format(formatterDay)
                    val sunsetText = if (LocalDate.parse(sunsetDay).isAfter(LocalDate.parse(date))) "(익일)" else ""

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        WeatherDetailRow("평균 습도", "$averHumidity %")
                        WeatherDetailRow("체감온도", "${"%.1f".format(bodyTemp)}°")
                        WeatherDetailRow("강수량", precipitationList[0]?.let { "$it mm" } ?: "정보 없음")
                        WeatherDetailRow(
                            "일출/일몰",
                            "${
                                DateConverter(
                                    sunriseList[0] ?: "누락",
                                    useHour = true,
                                    useMin = true
                                )
                            } / $sunsetText${
                                DateConverter(
                                    sunsetList[0] ?: "누락",
                                    useHour = true,
                                    useMin = true
                                )
                            }"
                        )
                        WeatherDetailRow("최대 풍속", "${windMaxList[0]}m/s")
                    }
                }
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(utilHourlyList.take(26)) { hourly ->
                val hIdx = hourlyList.indexOf(hourly)
                WeatherHourItem(hourly, weatherCodeHourly.getOrNull(hIdx) ?: 120,
                    temperatureHourly.getOrNull(hIdx) ?: 99.0
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.35f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            itemsIndexed(fullDates) { index, daily ->
                val max = tempMaxList.getOrNull(index)
                val min = tempMinList.getOrNull(index)
                WeatherDayItem(daily.format(formatterDay), weatherCodeDaily.getOrNull(index) ?: 120, min, max)
            }
        }
    }
}


data class DailyTuple(
    val date: String,
    val tempMax: Double?,
    val tempMin: Double?,
    val precip: Double?,
    val sunrise: String?,
    val sunset: String?,
    val windMax: Double?,
    val code: Int?
)

data class HourlyTuple(
    val time: String, val temperature: Double, val humidity: Int, val code: Int
)

