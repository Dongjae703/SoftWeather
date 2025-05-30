package com.example.softweather.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationDB(
    @PrimaryKey(autoGenerate = true) val l_id : Int = 0,
    val l_name: String,
    val lat: String, // "2025-05-17"
    val lon: String // 연관된 지역 이름
)