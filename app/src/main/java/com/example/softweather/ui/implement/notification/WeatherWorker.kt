package com.example.softweather.ui.implement.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.softweather.R
import com.example.softweather.model.RetrofitInstance
import com.example.softweather.ui.implement.tool.IsRainDecider
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume

class WeatherWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val fusedClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        val currentDay = LocalDate.now(ZoneId.of("Asia/Seoul"))

        val (lastCode, lastTime) = getLastAlertInfo(applicationContext)
        val currentTime = System.currentTimeMillis()
        val sixHours = 6 * 60 * 60 * 1000L

        val location = try {
            getLocationSuspend(fusedClient)
        } catch (e: SecurityException) {
            Log.e("Worker", "위치 권한 없음", e)
            return Result.failure()
        } ?: return Result.failure()

        val result = try {
            RetrofitInstance.hourlyWeatherApi.getHourlyWeather(
                lat = location.latitude,
                lon = location.longitude,
                start = currentDay.toString(),
                end = currentDay.plusDays(1L).toString()
            )
        } catch (e: Exception) {
            Log.e("Worker", "API 호출 실패", e)
            return Result.failure()
        }
        val currentHour = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val idx = result.hourly.time.indexOf(currentHour)

        Log.e("notification","idx")
        try {
            val hourlyWeatherCode = result.hourly.weather_code.subList(idx,idx+6)
            if (hourlyWeatherCode.any{ IsRainDecider(it) == 1 } && (1 != lastCode || (currentTime - lastTime >= sixHours))) {
                sendNotification("비 가능성 있음", "자세한 정보를 날씨 앱에서 확인하세요")
                setLastAlertInfo(applicationContext, 1, currentTime)
            }else if (hourlyWeatherCode.any{ IsRainDecider(it) == 2 && (2 != lastCode || (currentTime - lastTime >= sixHours)) }){
                sendNotification("눈 가능성 있음", "자세한 정보를 날씨 앱에서 확인하세요")
                setLastAlertInfo(applicationContext, 2, currentTime)
            }else if (hourlyWeatherCode.any{ IsRainDecider(it) == 3 && (3 != lastCode || (currentTime - lastTime >= sixHours)) }){
                sendNotification("천둥번개 주의", "자세한 정보를 날씨 앱에서 확인하세요")
                setLastAlertInfo(applicationContext, 3, currentTime)
            }
            return Result.success()
        }catch (e : Exception){
            return Result.failure()
        }
    }

    private suspend fun getLocationSuspend(fusedClient: FusedLocationProviderClient): Location? =
        suspendCancellableCoroutine { cont ->
            val request = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            try {
                fusedClient.getCurrentLocation(request, null)
                    .addOnSuccessListener { cont.resume(it) }
                    .addOnFailureListener { cont.resume(null) }
            } catch (e: SecurityException) {
                cont.cancel(e)
            }
        }

    fun getLastAlertInfo(context: Context): Pair<Int, Long> {
        val prefs = context.getSharedPreferences("weather_alert_prefs", Context.MODE_PRIVATE)
        val code = prefs.getInt("last_alert_code", 0) // 기본값 0
        val time = prefs.getLong("last_alert_time", 0L)
        return code to time
    }

    fun setLastAlertInfo(context: Context, code: Int, time: Long) {
        val prefs = context.getSharedPreferences("weather_alert_prefs", Context.MODE_PRIVATE)
        prefs.edit() {
            putInt("last_alert_code", code)
                .putLong("last_alert_time", time)
        }
    }



    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "weather_channel",
                "날씨 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(applicationContext, "weather_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_cloud) // 적절한 아이콘 리소스
            .build()

        notificationManager.notify(1, notification)
    }
}
