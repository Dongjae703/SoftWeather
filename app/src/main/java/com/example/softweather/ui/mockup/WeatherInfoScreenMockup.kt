package com.example.softweather.ui.mockup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherInfoScreenMockup(
    date: String,
    location: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        //기본적인 정보
        Column(
            modifier = Modifier
//                .weight(1f) 가중치 적절하게 조절하기 위한 수단
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = date, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text(text = location, fontSize = 18.sp)
            Text(text = "12°", fontSize = 52.sp, fontWeight = FontWeight.Bold)
            Text(text = "☁️", fontSize = 36.sp)
            Text(text = "약간 흐림", fontSize = 14.sp)
            Text(text = "최고 : 14°  최저 : 11°", fontSize = 14.sp)
        }

        // 상세 정보
        Column(
            modifier = Modifier
                .weight(0.8f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            WeatherDetailRowMock(label = "습도", value = "68%")
            WeatherDetailRowMock(label = "체감온도", value = "11°")
            WeatherDetailRowMock(label = "풍속", value = "3.4 m/s", icon = Icons.Outlined.ArrowOutward)
            WeatherDetailRowMock(label = "강수량", value = "2.1 mm")
            WeatherDetailRowMock(label = "일출/일몰", value = "05:32 / 19:12")
            WeatherDetailRowMock(label = "최대 풍속", value = "6.8 m/s")
        }

        // 시간별
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherHourItemMock(time = "오전 9시", weatherCode = "약간 흐림", temp = "13°")
            WeatherHourItemMock(time = "오전 10시", weatherCode = "흐림", temp = "15°")
            WeatherHourItemMock(time = "오전 11시", weatherCode = "안개", temp = "16°")
            WeatherHourItemMock(time = "오후 12시", weatherCode = "흐림", temp = "17°")
        }
        Spacer(modifier = Modifier.height(8.dp))
        // 날짜별 최대 15일까지
        Column(
            modifier = Modifier
//                .weight(1f) q
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            WeatherDayItemMock(day = "오늘", icon = "\u2601\ufe0f", description = "약간 흐림", avgTemp = "14°")
            WeatherDayItemMock(day = "일", icon = "\u2601\ufe0f", description = "흐림", avgTemp = "18°")
            WeatherDayItemMock(day = "월", icon = "☀️", description = "맑음", avgTemp = "23°")
            WeatherDayItemMock(day = "화", icon = "\u2601\ufe0f", description = "흐림", avgTemp = "25°")
        }
    }
}
//상세 날씨 만드는 툴
@Composable
fun WeatherDetailRowMock(label: String, value: String, icon: ImageVector? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(start = 2.dp),
                    tint = Color.Gray
                )
            }
            Text(text = value, fontSize = 14.sp)
        }
    }
}
//시간별 날씨 만드는 툴
@Composable
fun WeatherHourItemMock(time: String, weatherCode: String, temp: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = time, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "\u2601\ufe0f", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = weatherCode, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = temp, fontSize = 13.sp)
    }
}
//날짜별 날씨 만드는 툴
@Composable
fun WeatherDayItemMock(day: String, icon: String, description: String, avgTemp: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = day, modifier = Modifier.width(40.dp), fontSize = 14.sp)
        Text(text = icon, fontSize = 22.sp, modifier = Modifier.padding(horizontal = 6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = description, fontSize = 12.sp, color = Color.Gray)
        }
        Text(text = avgTemp, fontSize = 14.sp)
    }
}
@Preview
@Composable
private fun WInfoPrev() {
    WeatherInfoScreenMockup("2024-10-05", "건국대학교")
}