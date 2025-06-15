package com.example.softweather.dummy

import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlaceService {
    @GET("search")
    suspend fun searchPlace(
        @Query("q") query: String,
        @Query("format") format: String = "json"
    ): List<NominatimPlace>
}