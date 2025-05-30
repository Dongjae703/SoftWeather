package com.example.softweather.model.hourly

import retrofit2.http.GET
import retrofit2.http.Query

interface HourlyWeatherApi {
    @GET("forecast")
    suspend fun getHourlyWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,weather_code",
        @Query("timezone") timezone: String ="Asia/Seoul",
        @Query("start_date") start: String,
        @Query("end_date") end: String
    ): HourlyWeatherResponse
}