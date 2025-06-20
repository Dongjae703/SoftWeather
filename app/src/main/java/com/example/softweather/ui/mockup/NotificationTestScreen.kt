package com.example.softweather.ui.mockup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.softweather.ui.implement.notification.DailySummaryWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NotificationTestScreen() {
    val context = LocalContext.current
    var resultText by remember { mutableStateOf("알림 생성 테스트 대기 중") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val success = DailySummaryWorker.DailySummaryUtil.runSummaryTask(context)
                withContext(Dispatchers.Main) {
                    resultText = if (success) "알림 생성 성공" else "알림 생성 실패"
                }
            }
        }) {
            Text("테스트 알림 만들기")
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(resultText)
    }
}