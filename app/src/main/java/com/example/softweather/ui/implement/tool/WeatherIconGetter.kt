package com.example.softweather.ui.implement.tool

fun WeatherIconGetter(weatherCode: Int?): String {
    return when (weatherCode) {
        0 -> "☀️"
        1 -> "🌤️"
        2 -> "⛅"
        3 -> "☁️"
        45 -> "🌫️"
        48 -> "🌁"
        51 -> "🌦️"
        53, 55, 56, 57 -> "🌧️"
        61 -> "🌦️"
        63, 65, 66, 67 -> "🌧️"
        71, 73 -> "🌨️"
        75, 86 -> "❄️"
        77 -> "🧊"
        80 -> "🌦️"
        81, 82 -> "🌧️"
        85 -> "🌨️"
        95 -> "⛈️"
        96 -> "⚡"
        99 -> "🧊"
        else -> "❓"
    }
}