package com.example.softweather.ui.implement.screen

import android.app.Application
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.softweather.database.LocationDB
import com.example.softweather.ui.mockup.WeatherInfoScreenMockup
import com.example.softweather.viewmodel.DBViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WeatherCardScreen(l_id: Int, navController: NavController) {
    var menuExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dbViewModel: DBViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )

    var location by remember { mutableStateOf<LocationDB?>(null) }
    var allLocations by remember { mutableStateOf<List<LocationDB>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(Unit) {
        dbViewModel.getAllLocationsSorted { locations ->
            allLocations = locations
            val index = locations.indexOfFirst { it.l_id == l_id }
            if (index != -1) {
                currentIndex = index
                location = locations[index]
            }
        }
    }

    location?.let { loc ->
        Box(modifier = Modifier.fillMaxSize()) {
            WeatherInfoScreen(
                date = getTodayDateString(),
                location = loc.l_name,
                lat = loc.lat.toDouble(),
                lon = loc.lon.toDouble()
            )

            // 점 세 개 메뉴
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "더보기",
                    tint = Color.Gray
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("삭제") },
                    onClick = {
                        menuExpanded = false
                        dbViewModel.deleteLocationById(loc.l_id) {
                            // 삭제 후 다음 지역으로 이동 or 뒤로가기
                            if (allLocations.size > 1) {
                                val newList = allLocations.filter { it.l_id != loc.l_id }
                                val newIndex = (currentIndex).coerceAtMost(newList.size - 1)
                                location = newList.getOrNull(newIndex)
                                currentIndex = newIndex
                                allLocations = newList
                            } else {
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }

            // 왼쪽 화살표
            if (currentIndex > 0) {
                IconButton(
                    onClick = {
                        location = allLocations[currentIndex - 1]
                        currentIndex -= 1
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBackIosNew,
                        contentDescription = "이전 지역",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp).alpha(0.5f)
                    )
                }
            }

            // 오른쪽 화살표
            if (currentIndex < allLocations.lastIndex) {
                IconButton(
                    onClick = {
                        location = allLocations[currentIndex + 1]
                        currentIndex += 1
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowForwardIos,
                        contentDescription = "다음 지역",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp).alpha(0.5f)
                    )
                }
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("해당 지역 정보를 찾을 수 없습니다.")
        }
    }
}

fun getTodayDateString(): String {
    val now = LocalDate.now(ZoneId.of("Asia/Seoul"))
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return now.format(formatter)
}