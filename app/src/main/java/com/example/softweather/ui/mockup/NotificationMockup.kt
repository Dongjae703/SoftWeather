package com.example.softweather.ui.mockup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationMockup() {
    val notifications = remember { mutableStateListOf<NotificationData>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                notifications.add(
                    NotificationData(
                        icon = "🌧️",
                        title = "Soft weather",
                        time = "now",
                        content = "현위치 (건국대학교)에 강수 예보가 있습니다."
                    )
                )
            }) { Text("강수 알림") }

            Button(onClick = {
                notifications.add(
                    NotificationData(
                        icon = "☀️",
                        title = "Soft weather",
                        time = "오전 05:00",
                        content = "오늘의 날씨\n맑음 · 12°"
                    )
                )
            }) { Text("오늘 날씨") }

            Button(onClick = {
                notifications.add(
                    NotificationData(
                        icon = "☀️",
                        title = "Soft weather",
                        time = "오전 05:00",
                        content = "일정변경\n부산여행 일정에 날씨 변동이 있습니다."
                    )
                )
            }) { Text("일정 변경") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notifications.reversed()) { notification ->
                NotificationCard(notification)
            }
        }
    }
}

@Composable
fun NotificationCard(data: NotificationData) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        color = Color(0xFFF7F9FA)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(data.icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(data.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(data.time, fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(data.content, fontSize = 13.sp, color = Color.Black)
            }
        }
    }
}

data class NotificationData(
    val icon: String,
    val title: String,
    val time: String,
    val content: String
)

@Preview
@Composable
private fun NotiPrev() {
    NotificationMockup()
}