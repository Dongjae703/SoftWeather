package com.example.softweather.ui.mockup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.softweather.ui.implement.tool.BottomBarItem
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenMockup(lat:Double,lon:Double,navController: NavController) {
    var selectedTab by remember { mutableStateOf("홈") }
    val now = LocalDate.now(ZoneId.of("Asia/Seoul"))
    val targetTimeStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    Scaffold(
        modifier = Modifier
            .background(Color.White),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {},
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Globe Icon"
                        )
                    }
                }
            )

        },
        bottomBar = {
            Column {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant // 연회색
                )
                // 실제 바텀 네비게이션 바
                NavigationBar(containerColor = Color.White) {
                    BottomBarItem("홈", Icons.Outlined.Home, selectedTab == "홈") { selectedTab = "홈" }
                    BottomBarItem("검색", Icons.Outlined.Search, selectedTab == "검색") { selectedTab = "검색" }
                    BottomBarItem("일정", Icons.Outlined.Event, selectedTab == "일정") { selectedTab = "일정" }
                    BottomBarItem("과거", Icons.Outlined.History, selectedTab == "과거") { selectedTab = "과거" }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFEFEFEF)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant // 연회색
            )
//            WeatherInfoScreen(targetTimeStr, "현재 위치",lat,lon)

        }
    }
}



//@Preview(showBackground = true)
//@Composable
//fun PreviewMainScreenMockup() {
//    MainScreenMockup("","",)
//}