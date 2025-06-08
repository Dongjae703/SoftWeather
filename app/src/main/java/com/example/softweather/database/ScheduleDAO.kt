package com.example.softweather.database

import androidx.room.Dao
import androidx.room.Delete
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

    @Query("""
        SELECT * FROM schedule 
        ORDER BY startDate ASC, lastDate ASC, s_id ASC
    """)
    fun getAllSorted(): Flow<List<ScheduleDB>>

    @Delete
    suspend fun deleteSchedule(schedule: ScheduleDB)

    @Query("DELETE FROM schedule WHERE s_id = :id")
    suspend fun deleteById(id: Int)
}