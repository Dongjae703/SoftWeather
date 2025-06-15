package com.example.softweather.ui.implement.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.softweather.R
import com.example.softweather.model.RetrofitInstance
import com.example.softweather.ui.implement.tool.IsRainDecider
import com.example.softweather.ui.implement.tool.WeatherCodeConverter
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import java.time.ZoneId
import kotlin.coroutines.resume

class DailySummaryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return if (DailySummaryUtil.runSummaryTask(applicationContext)) Result.success()
        else Result.failure()
    }

    object DailySummaryUtil {
        suspend fun runSummaryTask(context: Context): Boolean {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val currentDay = LocalDate.now(ZoneId.of("Asia/Seoul"))

            val location = try {
                suspendCancellableCoroutine<Location?> { cont ->
                    val request = CurrentLocationRequest.Builder()
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .build()
                    fusedClient.getCurrentLocation(request, null)
                        .addOnSuccessListener { cont.resume(it) }
                        .addOnFailureListener { cont.resume(null) }
                }
            } catch (e: SecurityException) {
                Log.e("SummaryTask", "위치 권한 없음", e)
                return false
            } ?: return false

            val hourlyResult = try {
                RetrofitInstance.hourlyWeatherApi.getHourlyWeather(
                    lat = location.latitude,
                    lon = location.longitude,
                    start = currentDay.toString(),
                    end = currentDay.plusDays(1L).toString()
                )
            } catch (e: Exception) {
                Log.e("SummaryTask", "hourly API 실패", e)
                return false
            }

            val dailyResult = try {
                RetrofitInstance.dailyWeatherApi.getDailyForecast(
                    lat = location.latitude,
                    lon = location.longitude,
                    startDate = currentDay.toString(),
                    endDate = currentDay.toString(),
                )
            } catch (e: Exception) {
                Log.e("SummaryTask", "daily API 실패", e)
                return false
            }

            val summaryWeather = dailyResult.daily.weathercode
            val tempMax = dailyResult.daily.temperature_2m_max
            val tempMin = dailyResult.daily.temperature_2m_min
            val hourlyWeather = hourlyResult.hourly.weather_code
            var message = "최고 ${tempMax.getOrNull(0)?:999}도, 최저 ${tempMin.getOrNull(0)?:-999}도,"
            if (hourlyWeather.take(20).any { IsRainDecider(it) == 1 }) {
                message += " 비가 오는 구간이 있습니다. 자세한 내용은 앱을 통해 확인하세요."
            } else if (hourlyWeather.take(20).any { IsRainDecider(it) == 2 }) {
                message += " 눈이 오는 구간이 있습니다. 자세한 내용은 앱을 통해 확인하세요."
            } else if (hourlyWeather.take(20).any { IsRainDecider(it) == 3 }) {
                message += " 천둥번개가 있는 구간이 있습니다. 자세한 내용은 앱을 통해 확인하세요."
            } else {
                message += " 좋은 날씨인 것 같습니다. 자세한 내용은 앱을 통해 확인하세요."
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "daily_channel",
                    "하루 요약 알림",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(context, "daily_channel")
                .setContentTitle("오늘의 날씨는 ${WeatherCodeConverter(summaryWeather.getOrNull(0))}")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_cloud)
                .build()

            notificationManager.notify(100, notification)

            return true
        }
    }
}
