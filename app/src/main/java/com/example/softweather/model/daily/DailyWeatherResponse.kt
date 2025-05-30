package com.example.softweather.model.daily

data class DailyWeatherResponse(
    val daily: DailyData
)

data class DailyData(
    val time: List<String>,                    // 날짜 (yyyy-MM-dd)
    val temperature_2m_max: List<Double>,      // 최고기온
    val temperature_2m_min: List<Double>,      // 최저기온
    val weathercode: List<Int>,                // 날씨코드
    val precipitation_sum: List<Double>,       // 강수량 (mm)
    val sunrise: List<String>,                 // 일출 시각 (yyyy-MM-ddTHH:mm)
    val sunset: List<String>,                  // 일몰 시각 (yyyy-MM-ddTHH:mm)
    val windspeed_10m_max: List<Double>, // 최대 풍속 (m/s)
)