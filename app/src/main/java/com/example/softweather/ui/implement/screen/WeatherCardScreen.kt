package com.example.softweather.ui.implement.screen

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.softweather.model.Routes
import com.example.softweather.viewmodel.DBViewModel
import com.example.softweather.viewmodel.PastWeatherRepoViewModel
import com.example.softweather.viewmodel.WeatherRepoViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WeatherCardScreen(date: LocalDate, isPast: Boolean,navController: NavController) {
    val context = LocalContext.current
    val dbViewModel: DBViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )
    val weatherRepoVM: WeatherRepoViewModel = viewModel()
    val weatherPastRepoVM: PastWeatherRepoViewModel = viewModel()
    val locations by dbViewModel.locationListFlow.collectAsState(emptyList())
    val pagerState = rememberPagerState(initialPage = 0) { locations.size }
    val coroutineScope = rememberCoroutineScope()

    val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
    val endDate = date.plusDays(15)

    val pastEnd = today.minusDays(1)
    val futureStart = today

    val pastRange: ClosedRange<LocalDate>? = if (date <= pastEnd) {
        date..minOf(pastEnd, endDate)
    } else null

    val futureRange: ClosedRange<LocalDate>? = if (endDate >= futureStart) {
        maxOf(futureStart, date)..endDate
    } else null

    LaunchedEffect(locations.size) {
        if (locations.isNotEmpty()) {
            pagerState.scrollToPage(pagerState.currentPage.coerceIn(0, locations.lastIndex))
        }
    }

    if (locations.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("해당 지역 정보를 찾을 수 없습니다.")
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
            beyondViewportPageCount = 2,
            flingBehavior = PagerDefaults.flingBehavior(pagerState),
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val loc = locations[page]
            Log.e("wcs page","sortOrder ${loc.sortOrder}")
            val lat = loc.lat.toDoubleOrNull()
            val lon = loc.lon.toDoubleOrNull()
            if (lat != null && lon != null) {
                val locationKey = "%.5f,%.5f".format(lat, lon)

                LaunchedEffect(locationKey, date) {
                    pastRange?.let {
                        weatherPastRepoVM.loadPastWeather(
                            lat, lon, locationKey,
                            it.start.format(DateTimeFormatter.ISO_DATE),
                            it.endInclusive.format(DateTimeFormatter.ISO_DATE)
                        )
                    }
                    futureRange?.let {
                        weatherRepoVM.loadWeatherData(
                            lat, lon, locationKey,
                            it.start.format(DateTimeFormatter.ISO_DATE),
                            it.endInclusive.format(DateTimeFormatter.ISO_DATE)
                        )
                    }
                }

                WeatherInfoScreenWithCache(
                    locationKey = locationKey,
                    weatherRepoVM = weatherRepoVM,
                    pastRepoVM = weatherPastRepoVM,
                    locationName = loc.l_name,
                    lat = lat,
                    lon = lon,
                    date = date.format(DateTimeFormatter.ISO_DATE),
                    isPast = isPast
                )
            } else {
                Text("⚠️ 위경도 정보가 올바르지 않습니다.")
            }
        }

        // 왼쪽 화살표
        if (pagerState.currentPage > 0) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
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
        if (pagerState.currentPage < locations.lastIndex) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                    contentDescription = "다음 지역",
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp).alpha(0.5f)
                )
            }
        }
        MoreOptionsMenu(
            onListClick = {navController.navigate(Routes.CardListScreen.route)},
            onDeleteClick ={  val currentLocation = locations[pagerState.currentPage]
                dbViewModel.deleteLocationById(currentLocation.l_id)
            }

        )
    }
}


@Composable
fun MoreOptionsMenu(
    onListClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "더보기 메뉴",
                tint = Color.DarkGray,
                modifier = Modifier
                    .size(32.dp)
                    .alpha(0.5f)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.White
        ) {
            DropdownMenuItem(
                text = { Text("리스트로 보기") },
                onClick = {
                    expanded = false
                    onListClick()
                }
            )
            DropdownMenuItem(
                text = { Text("삭제") },
                onClick = {
                    expanded = false
                    onDeleteClick()
                }
            )
        }
    }
}