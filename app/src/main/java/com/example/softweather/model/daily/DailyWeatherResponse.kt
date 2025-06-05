package com.example.softweather.model.daily

data class DailyWeatherResponse(
    val daily: DailyData
)

data class DailyData(
    var time: List<String>,                    // 날짜 (yyyy-MM-dd)
    var temperature_2m_max: List<Double>,      // 최고기온
    var temperature_2m_min: List<Double>,      // 최저기온
    var weathercode: List<Int>,                // 날씨코드
    var precipitation_sum: List<Double>,       // 강수량 (mm)
    var sunrise: List<String>,                 // 일출 시각 (yyyy-MM-ddTHH:mm)
    var sunset: List<String>,                  // 일몰 시각 (yyyy-MM-ddTHH:mm)
    var windspeed_10m_max: List<Double>, // 최대 풍속 (m/s)
)