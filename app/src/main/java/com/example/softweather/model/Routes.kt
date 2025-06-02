package com.example.softweather.model

import android.net.Uri

sealed class Routes(val route: String) {
//    object CitySearchScreen : Routes("citySearch")
//    object WeatherScreen : Routes("weather/{lat}/{lon}") {
//        fun createRoute(lat: String, lon: String): String = "weather/$lat/$lon"
//    }
//    object DailyWeatherScreen : Routes("dailyWeather/{lat}/{lon}"){
//        fun createRoute(lat: String, lon: String): String = "dailyWeather/$lat/$lon"
//    }
    object SplashScreen : Routes("splash")
    object MainScreen : Routes("mainScreen/{lat}/{lon}/{locationName}"){
        fun createRoute(lat: String, lon: String,locationName: String): String ="mainScreen/$lat/$lon/${Uri.encode(locationName)}"
    }

    object SearchScreen : Routes("searchScreen")
    object SceduleScreen : Routes("sceduleScreen")
    object PastScreen : Routes("pastScreen")


    object MapScreen : Routes("map/{lat}/{lon}")

}