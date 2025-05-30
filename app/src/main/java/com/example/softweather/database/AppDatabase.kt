package com.example.softweather.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ScheduleDB::class, LocationDB::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDAO(): ScheduleDAO
    abstract fun locationDAO(): LocationDAO
}
