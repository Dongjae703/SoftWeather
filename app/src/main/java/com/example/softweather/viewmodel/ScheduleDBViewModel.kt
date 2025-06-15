package com.example.softweather.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.softweather.database.AppDatabase
import com.example.softweather.database.ScheduleDB
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class ScheduleDBViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "softweather-db"
    ).build()

    private val scheduleDao = db.scheduleDAO()

    val today = LocalDate.now(ZoneId.of("Asia/Seoul")).toString()
    val scheduleListFlow = scheduleDao.getFutureSchedules(today)


    fun insertSchedule(schedule: ScheduleDB) {
        viewModelScope.launch {
            scheduleDao.insert(schedule)
        }
    }

    fun isScheduleDateDuplicate(start: String, end: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val existing = scheduleDao.getScheduleByDateRange(start, end)
            onResult(existing != null)
        }
    }

    fun deleteScheduleById(id: Int) {
        viewModelScope.launch {
            scheduleDao.deleteById(id)
        }
    }
}
