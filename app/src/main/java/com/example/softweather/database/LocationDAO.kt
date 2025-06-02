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

    @Query("SELECT * FROM location WHERE l_id")
    suspend fun getID() : List<LocationDB>

    @Query("SELECT * FROM location ORDER BY l_id ASC")
    suspend fun getAllSorted(): List<LocationDB>

    @Query("DELETE FROM location WHERE l_id = :id")
    suspend fun deleteLocationById(id: Int)
}