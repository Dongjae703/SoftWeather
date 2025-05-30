package com.example.softweather.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.softweather.database.AppDatabase
import com.example.softweather.database.LocationDB
import com.example.softweather.database.ScheduleDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DBViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "softWeather_db"
    ).build()

    private val scheduleDao = db.scheduleDAO()
    private val locationDao = db.locationDAO()

    // 일정 삽입
    fun insertSchedule(schedule: ScheduleDB) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleDao.insert(schedule)
        }
    }

    // 일정 전부 가져오기
    fun loadAllSchedules(onResult: (List<ScheduleDB>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val schedules = scheduleDao.getAll()
            onResult(schedules)
        }
    }

    // 위치 삽입
    fun insertLocation(location: LocationDB) {
        viewModelScope.launch(Dispatchers.IO) {
            locationDao.insert(location)
        }
    }

    // 위치 전부 가져오기
    fun loadAllLocations(onResult: (List<LocationDB>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val locations = locationDao.getAll()
            onResult(locations)
        }
    }
}