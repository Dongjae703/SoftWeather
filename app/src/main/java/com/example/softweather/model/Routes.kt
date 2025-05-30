package com.example.softweather.model

sealed class Routes(val route: String) {
    object CitySearchScreen : Routes("citySearch")
    object WeatherScreen : Routes("weather/{lat}/{lon}") {
        fun createRoute(lat: String, lon: String): String = "weather/$lat/$lon"
    }
    object DailyWeatherScreen : Routes("dailyWeather/{lat}/{lon}"){
        fun createRoute(lat: String, lon: String): String = "dailyWeather/$lat/$lon"
    }
    object SplashScreen : Routes("splash")
    object MainScreen : Routes("mainScreen/{lat}/{lon}"){
        fun createRoute(lat: String, lon: String): String ="mainScreen/$lat/$lon"
    }
}