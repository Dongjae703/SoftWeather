package com.example.softweather.ui.implement.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.softweather.R
import com.example.softweather.ui.implement.notification.DailySummaryWorker.DailySummaryUtil.runSummaryTask
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DailySummaryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
//        val fusedClient = LocationServices.getFusedLocationProviderClient(applicationContext)
//        val currentDay = LocalDate.now(ZoneId.of("Asia/Seoul"))
//
//        val location = try {
//            getLocationSuspend(fusedClient)
//        } catch (e: SecurityException) {
//            Log.e("Worker", "위치 권한 없음", e)
//            return Result.failure()
//        } ?: return Result.failure()
//
//        val hourlyResult = try {
//            RetrofitInstance.hourlyWeatherApi.getHourlyWeather(
//                lat = location.latitude,
//                lon = location.longitude,
//                start = currentDay.toString(),
//                end = currentDay.plusDays(1L).toString()
//            )
//        } catch (e: Exception) {
//            Log.e("Worker", "API 호출 실패", e)
//            return Result.failure()
//        }
//
//        val dailyResult = try {
//            RetrofitInstance.dailyWeatherApi.getDailyForecast(
//                lat = location.latitude,
//                lon = location.longitude,
//                startDate = currentDay.toString(),
//                endDate = currentDay.toString(),
//            )
//        } catch (e: Exception) {
//            Log.e("Worker", "API 호출 실패", e)
//            return Result.failure()
//        }
//        val summaryWeather = dailyResult.daily.weathercode
//        val tempMax = dailyResult.daily.temperature_2m_max
//        val tempMin = dailyResult.daily.temperature_2m_min
//        val hourlyWeather = hourlyResult.hourly.weather_code
//        var message = "최고 ${tempMax}도, 최저 ${tempMin}도,"
//        if (hourlyWeather.take(20).any{ IsRainDecider(it) == 1 }){
//            message+=" 비가 오는 구간이 있습니다. 자세한 내용은 앱을 통해 확인하세요."
//        }else if (hourlyWeather.take(20).any{ IsRainDecider(it) == 2 }) {
//            message+=" 눈이 오는 구간이 있습니다. 자세한 내용은 앱을 통해 확인하세요."
//        }else if (hourlyWeather.take(20).any{ IsRainDecider(it) == 3 }) {
//            message+=" 천둥번개가 있는 구간이 있습니다. 자세한 내용은 앱을 통해 확인하세요."
//        }else{
//            message+=" 좋은 날씨인 것 같습니다. 자세한 내용은 앱을 통해 확인하세요."
//        }
//        sendNotification("오늘의 날씨는 ${WeatherCodeConverter(summaryWeather.getOrNull(0))}", message)
//        return Result.success()
        return if (runSummaryTask(applicationContext)) Result.success()
        else Result.failure()
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
    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "daily_channel",
                "하루 요약 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(applicationContext, "daily_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_cloud) // 적절한 아이콘
            .build()

        notificationManager.notify(100, notification) // ID는 고유하게 (ex. 100)
    }
    object DailySummaryUtil {
        suspend fun runSummaryTask(context: Context): Boolean {
            // 위치 권한 검사
            val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasFineLocationPermission) {
                Log.e("DailySummary", "위치 권한 없음")
                return false
            }

            val fusedClient = LocationServices.getFusedLocationProviderClient(context)

            val location = suspendCancellableCoroutine<Location?> { cont ->
                fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { cont.resume(it) }
                    .addOnFailureListener { cont.resume(null) }
            }

            if (location == null) {
                Log.e("DailySummary", "위치 못 가져옴")
                return false
            }

            // 알림 채널 ID
            val channelId = "daily_summary_channel"
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "날씨 알림 테스트 채널",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("테스트 알림")
                .setContentText("위치: ${location.latitude}, ${location.longitude}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(999, notification)

            return true
        }
    }
}
