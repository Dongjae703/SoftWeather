package com.example.softweather.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.softweather.database.AppDatabase
import com.example.softweather.database.ScheduleDB
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ScheduleDBViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "softweather-db"
    ).build()

    private val scheduleDao = db.scheduleDAO()

    val scheduleListFlow: Flow<List<ScheduleDB>> = scheduleDao.getAllSorted()

    fun insertSchedule(schedule: ScheduleDB) {
        viewModelScope.launch {
            scheduleDao.insert(schedule)
        }
    }

    fun deleteSchedule(schedule: ScheduleDB) {
        viewModelScope.launch {
            scheduleDao.deleteSchedule(schedule)
        }
    }

    fun deleteScheduleById(id: Int) {
        viewModelScope.launch {
            scheduleDao.deleteById(id)
        }
    }
}
