package com.example.softweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.softweather.model.permission.requestPermission
import com.example.softweather.navgraph.NavGraph
import com.example.softweather.ui.implement.notification.DailySummaryWorker
import com.example.softweather.ui.implement.notification.ScheduleMonitorWorker
import com.example.softweather.ui.implement.notification.WeatherWorker
import com.google.android.libraries.places.api.Places
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val workRequest = PeriodicWorkRequestBuilder<WeatherWorker>(
            1, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "weather_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        val dailyRequest = PeriodicWorkRequestBuilder<DailySummaryWorker>(
            1, TimeUnit.DAYS
        ).setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "daily_summary_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyRequest
        )

        val scheduleMonitorRequest = PeriodicWorkRequestBuilder<ScheduleMonitorWorker>(
            1, TimeUnit.DAYS
        ).setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "schedule_monitor_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            scheduleMonitorRequest
        )

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBezEgctIzXiH2u5bd5m79fzSOinpf4IvA")
        }
        requestPermission(this)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavGraph(navController)
//            MainScreenMockup()
//            PastScreen(navController)
//            ScheduleScreenMockup()
//            PastScreenMockup()
//            GoogleMapScreenMockup()
//        WeatherInfoScreenMockup("2025-05-15","건국대학교")
//            WeatherInfoScreen("2025-05-26", "건국대학교",37.5409,127.0795)
//        NotificationMockup()
//           CardListScreenMockup()
//            MainScreenMockup(52.0,64.0, navController)
        }

    }
}

fun calculateInitialDelay(): Long {
    val now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
    var nextRun = now.withHour(4).withMinute(0).withSecond(0).withNano(0)

    if (now >= nextRun) {
        // 현재 시간이 4시 이후면 다음 날 4시로
        nextRun = nextRun.plusDays(1)
    }

    return Duration.between(now, nextRun).toMillis()
}