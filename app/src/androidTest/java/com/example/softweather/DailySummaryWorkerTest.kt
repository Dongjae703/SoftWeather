package com.example.softweather

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.softweather.ui.implement.notification.DailySummaryWorker
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DailySummaryWorkerTest {

    @Test
    fun testDoWorkReturnsSuccess() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val worker = TestListenableWorkerBuilder<DailySummaryWorker>(
            context = context
        ).build()

        val result = worker.doWork()

        assertTrue(result is ListenableWorker.Result.Success)
    }
}