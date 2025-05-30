package com.example.softweather.model.currentday

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {
    @GET("forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("timezone") timezone : String = "Asia/Seoul",
        @Query("current_weather") currentWeather: Boolean = true
    ): OpenMeteoResponse
}
