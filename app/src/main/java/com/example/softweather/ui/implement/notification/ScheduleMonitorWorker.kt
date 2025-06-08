package com.example.softweather.ui.implement.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.softweather.R
import com.example.softweather.database.AppDatabase
import com.example.softweather.database.ScheduleDB
import com.example.softweather.model.RetrofitInstance.dailyWeatherApi
import com.example.softweather.ui.implement.tool.IsRainDecider
import java.time.LocalDate

class ScheduleMonitorWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "softweather-db"
        ).build()
        val scheduleDao = db.scheduleDAO()
        val schedules : List<ScheduleDB> = scheduleDao.getAll() // 📌 suspend fun getAll(): List<ScheduleEntity>

        if (schedules.isEmpty()) return Result.success()

        for (schedule in schedules) {
            val lat = schedule.lat.toDouble()
            val lon = schedule.lon.toDouble()
            val scheduleStart = LocalDate.parse(schedule.startDate)
            val scheduleEndRaw = LocalDate.parse(schedule.lastDate)
            val endLimit = LocalDate.now().plusDays(14)
            val scheduleEnd = minOf(scheduleEndRaw, endLimit)

            val weatherResult = dailyWeatherApi.getDailyForecast(
                lat = lat,
                lon = lon,
                startDate = scheduleStart.toString(),
                endDate = scheduleEnd.toString()
            )

            val hasSevere = weatherResult.daily.weathercode.any {
                val code = IsRainDecider(it)
                code == 1 || code == 2 || code == 3
            }

            if (hasSevere) {
                sendNotification(
                    "날씨 주의: ${schedule.title}",
                    "${schedule.startDate} ~ ${schedule.lastDate} 일정 중 비/눈/천둥 예보가 있습니다."
                )
                break
            }
        }
        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "schedule_alert_channel",
                "일정 날씨 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "schedule_alert_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_cloud) // 적절한 아이콘
            .build()

        manager.notify(200, notification)
    }
}
