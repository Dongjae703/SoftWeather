package com.example.softweather.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleDB)

    @Query("SELECT * FROM SCHEDULE")
    suspend fun getAll() : List<ScheduleDB>

    @Query("SELECT * FROM schedule WHERE lastDate >= :today ORDER BY startDate ASC, lastDate ASC, s_id ASC")
    fun getFutureSchedules(today: String): Flow<List<ScheduleDB>>

    @Query("SELECT * FROM schedule WHERE lastDate >= :today ORDER BY startDate ASC, lastDate ASC, s_id ASC")
    suspend fun getFutureSchedulesSuspend(today: String): List<ScheduleDB> //worker 전용

    @Query("DELETE FROM schedule WHERE s_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM schedule WHERE startDate = :start AND lastDate = :end LIMIT 1")
    suspend fun getScheduleByDateRange(start: String, end: String): ScheduleDB?
}