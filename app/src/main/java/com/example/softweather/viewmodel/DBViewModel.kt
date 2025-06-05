package com.example.softweather.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.softweather.database.AppDatabase
import com.example.softweather.database.LocationDB
import com.example.softweather.database.ScheduleDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun insertLocationIfNotDuplicate(location: LocationDB, onInserted: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = locationDao.getLocationByName(location.l_name)
            if (existing == null) {
                locationDao.insert(location)
                withContext(Dispatchers.Main) {
                    onInserted(true)
                } // 성공적으로 삽입됨
            } else {
                withContext(Dispatchers.Main) {
                    onInserted(false)
                } // 이미 존재함
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


//    fun getLocationById(onResult: (List<LocationDB>) -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = locationDao.getID()
//            onResult(result)
//        }
//    }

//    fun getAllLocationsSorted(onResult: (List<LocationDB>) -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = locationDao.getAllSorted()
//            onResult(result)
//        }
//    }

    fun deleteLocationById(id: Int, onDeleted: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            locationDao.deleteLocationById(id)
            onDeleted()
        }
    }
    val locationListFlow: StateFlow<List<LocationDB>> =
        locationDao.getAllSortedFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}