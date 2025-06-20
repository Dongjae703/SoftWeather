package com.example.softweather.dummy

import com.squareup.moshi.Json

data class NominatimPlace(
    val lat: String,
    val lon: String,
    @Json(name = "display_name") val display_name: String
)
