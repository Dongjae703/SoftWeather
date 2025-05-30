package com.example.softweather.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScheduleDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleDB)

    @Query("SELECT * FROM SCHEDULE")
    suspend fun getAll() : List<ScheduleDB>
}