package com.example.softweather.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location:LocationDB)

    @Query("SELECT * FROM location")
    suspend fun getAll() : List<LocationDB>
}