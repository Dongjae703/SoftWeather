package com.example.softweather.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.softweather.database.AppDatabase
import com.example.softweather.database.LocationDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DBViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "softWeather_db"
    ).build()

    private val locationDao = db.locationDAO()

    fun insertLocationSmart(
        location: LocationDB,
        fallbackName: String?,
        onResult: (success: Boolean, usedFallback: Boolean, finalName: String) -> Unit
    ) {
        viewModelScope.launch {
            val existsByLatLon = locationDao.getLocationByLatLon(location.lat, location.lon)
            Log.d("existsByLatLon","{$existsByLatLon}")
            if (existsByLatLon != null) {
                onResult(false, false, location.l_name)
                return@launch
            }

            val existsByName = locationDao.getLocationByName(location.l_name)
            Log.d("existsByname","{$existsByName}")
            if (existsByName == null) {
                // displayName 자체가 유일함
                val maxOrder = locationDao.getMaxSortOrder() ?: 0
                locationDao.insert(location.copy(sortOrder = maxOrder + 1))
                onResult(true, false, location.l_name)
            } else if (fallbackName != null) {
                // fallbackName으로 이름 바꾸기
                val existsFallback = locationDao.getLocationByName(fallbackName)
                if (existsFallback == null) {
                    val fallbackLocation = location.copy(l_name = fallbackName)
                    val maxOrder = locationDao.getMaxSortOrder() ?: 0
                    locationDao.insert(fallbackLocation.copy(sortOrder = maxOrder + 1))
                    onResult(true, true, fallbackName)
                } else {
                    onResult(false, true, fallbackName)
                }
            } else {
                onResult(false, false, location.l_name)
            }
        }
    }


    // 위치 전부 가져오기
    fun loadAllLocations(onResult: (List<LocationDB>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val locations = locationDao.getAll()
            onResult(locations)
        }
    }

    fun deleteLocationById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            locationDao.deleteLocationById(id)
        }
    }

    fun deleteLocations(locations: List<LocationDB>) {
        viewModelScope.launch {
            locations.forEach {
                locationDao.deleteLocationById(it.l_id)
            }
        }
    }

    val locationListFlow: Flow<List<LocationDB>> = locationDao.getAllSortedFlow()


    fun updateSortOrder(newList: List<LocationDB>) {
        viewModelScope.launch {
            newList.forEachIndexed { index, loc ->
                val descendingOrder = newList.size - index
                locationDao.updateSortOrder(loc.l_id, order = descendingOrder)
            }
        }
    }
}