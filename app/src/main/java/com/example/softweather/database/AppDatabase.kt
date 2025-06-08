package com.example.softweather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ScheduleDB::class, LocationDB::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDAO(): ScheduleDAO
    abstract fun locationDAO(): LocationDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "WeatherDB"
                )
                    .fallbackToDestructiveMigration() // ✅ 요기 추가!
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
