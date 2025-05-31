package com.example.softweather.ui.implement.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.softweather.model.LocationHolder
import com.example.softweather.model.Routes
import com.example.softweather.ui.implement.tool.NavigationBarTemplete
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(lname:String, lat: Double, lon: Double, navController: NavController) {
    val currentRoute = Routes.MainScreen.route
    var selectedTab by remember { mutableStateOf("홈") }
    val now = LocalDate.now(ZoneId.of("Asia/Seoul"))
    val targetTimeStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    LocationHolder.lat = lat
    LocationHolder.lon = lon

//    val context = LocalContext.current
//
//
//    var locationName by remember { mutableStateOf("현재 위치") }
//
//
//    LaunchedEffect(lat, lon) {
//        val geocoder = Geocoder(context, Locale.getDefault())
//        val addresses = geocoder.getFromLocation(lat, lon, 1)
//        if (!addresses.isNullOrEmpty()) {
//            locationName = addresses[0].featureName ?: addresses[0].locality ?: "현재 위치"
//        }
//
//    }

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

                    IconButton(onClick = {
                        navController.navigate("map/${lat}/${lon}")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "지도"

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
                NavigationBarTemplete(selectedTab, onTabSelected = {selectedTab=it},currentRoute,navController)

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

            WeatherInfoScreen(targetTimeStr, lname, lat, lon)


        }
    }
}