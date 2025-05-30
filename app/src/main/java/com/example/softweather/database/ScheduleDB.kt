package com.example.softweather.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class ScheduleDB(
    @PrimaryKey(autoGenerate = true) val s_id: Int = 0,
    val title: String,
    val startDate: String,
    val lastDate : String,// "2025-05-17"
    val location: String, // 연관된 지역 이름
    val lat: String,
    val lon: String
)
