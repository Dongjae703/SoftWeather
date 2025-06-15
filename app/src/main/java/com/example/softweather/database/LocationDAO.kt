package com.example.softweather.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location:LocationDB)

    @Query("SELECT * FROM location")
    suspend fun getAll() : List<LocationDB>


    @Query("DELETE FROM location WHERE l_id = :id")
    suspend fun deleteLocationById(id: Int)

    @Query("SELECT * FROM location WHERE l_name = :name LIMIT 1")
    suspend fun getLocationByName(name: String): LocationDB?

    @Query("SELECT * FROM location WHERE lat = :lat AND lon = :lon LIMIT 1")
    suspend fun getLocationByLatLon(lat: String, lon: String): LocationDB?

    @Query("SELECT * FROM location ORDER BY sortOrder DESC")
    fun getAllSortedFlow(): Flow<List<LocationDB>>

    @Query("UPDATE location SET sortOrder = :order WHERE l_id = :id")
    suspend fun updateSortOrder(id: Int, order: Int)

    @Transaction
    suspend fun updateAllSortOrders(locations: List<LocationDB>) {
        locations.forEachIndexed { index, loc ->
            updateSortOrder(loc.l_id, index)
        }
    }

    @Query("SELECT MAX(sortOrder) FROM location")
    suspend fun getMaxSortOrder(): Int?
}