package com.example.softweather.ui.implement.tool

fun WeatherCodeConverter(weatherCode: Int?): String {
    return when (weatherCode) {
        0 -> "맑음" // Clear sky
        1 -> "거의 맑음" // Mainly clear
        2 -> "약간 흐림" // Partly cloudy
        3 -> "흐림" // Overcast
        45 -> "안개" // Fog
        48 -> "서리 안개" // Depositing rime fog
        51 -> "약한 이슬비" // Light drizzle
        53 -> "중간 이슬비" // Moderate drizzle
        55 -> "강한 이슬비" // Dense drizzle
        56 -> "약한 냉이슬비" // Light freezing drizzle
        57 -> "강한 냉이슬비" // Dense freezing drizzle
        61 -> "약한 비" // Slight rain
        63 -> "중간 비" // Moderate rain
        65 -> "강한 비" // Heavy rain
        66 -> "약한 어는 비" // Light freezing rain
        67 -> "강한 어는 비" // Heavy freezing rain
        71 -> "약한 눈" // Slight snow
        73 -> "중간 눈" // Moderate snow
        75 -> "강한 눈" // Heavy snow
        77 -> "싸락눈" // Snow grains
        80 -> "약한 소나기" // Slight rain showers
        81 -> "중간 소나기" // Moderate rain showers
        82 -> "격렬한 소나기" // Violent rain showers
        85 -> "약한 눈소나기" // Slight snow showers
        86 -> "강한 눈소나기" // Heavy snow showers
        95 -> "천둥번개" // Thunderstorm
        96 -> "천둥,약우박" // Thunderstorm with slight hail
        99 -> "천둥,강우박" // Thunderstorm with heavy hail
        else -> "오류" // Unknown weather code
    }
}
