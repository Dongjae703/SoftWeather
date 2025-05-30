package com.example.softweather.ui.implement.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.softweather.R

class AlertWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val shouldNotify = checkCondition() // 조건 판단 (직접 구현)

        if (shouldNotify) {
            showNotification("SoftWeather", "30분마다 확인된 알림입니다.")
        }

        return Result.success()
    }

    private fun checkCondition(): Boolean {

        // 여기에 날씨나 시간 등의 조건을 넣으면 됨
        return true // 예시로 항상 true
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "weather_channel_id"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Weather Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 적절한 아이콘으로 대체
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1001, builder.build())
    }
}
