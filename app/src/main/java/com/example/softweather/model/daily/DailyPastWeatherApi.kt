package com.example.softweather.model.daily

import retrofit2.http.GET
import retrofit2.http.Query

interface DailyPastWeatherApi {
    @GET(".")
    suspend fun getDailyArchive(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,weathercode,precipitation_sum,sunrise,sunset,windspeed_10m_max",
        @Query("timezone") timezone: String = "Asia/Seoul",
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): DailyWeatherResponse
}