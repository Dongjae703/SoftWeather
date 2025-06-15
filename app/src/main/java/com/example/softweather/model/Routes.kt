package com.example.softweather.model

import android.net.Uri

sealed class Routes(val route: String) {
    object SplashScreen : Routes("splash")
    object MainScreen : Routes("mainScreen/{lat}/{lon}/{locationName}"){
        fun createRoute(lat: String, lon: String,locationName: String): String ="mainScreen/$lat/$lon/${Uri.encode(locationName)}"
    }

    object SearchScreen : Routes("searchScreen")
    object ScheduleScreen : Routes("sceduleScreen")
    object PastScreen : Routes("pastScreen")


    object MapScreen : Routes("map/{lat}/{lon}")

    object CardListScreen : Routes("cardList")

}