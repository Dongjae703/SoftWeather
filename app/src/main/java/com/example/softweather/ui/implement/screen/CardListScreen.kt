package com.example.softweather.ui.implement.screen

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.softweather.database.LocationDB
import com.example.softweather.model.WeatherUIState
import com.example.softweather.ui.implement.tool.WeatherCodeConverter
import com.example.softweather.ui.implement.tool.WeatherIconGetter
import com.example.softweather.viewmodel.DBViewModel
import com.example.softweather.viewmodel.WeatherRepoViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun CardListScreen(navController: NavController) {
    val context = LocalContext.current
    val dbViewModel: DBViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )
    val weatherRepoVM: WeatherRepoViewModel = viewModel()
    val locations by dbViewModel.locationListFlow.collectAsState(emptyList())
    val today = remember { LocalDate.now(ZoneId.of("Asia/Seoul")).toString() }
    val mutableLocations = remember(locations) { locations.toMutableStateList() }
    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<LocationDB>() }
    val listState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(
        lazyListState = listState,
        onMove = { from, to ->
            mutableLocations.move(from.index, to.index)
        }
    )

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (isSelectionMode) selectedItems.clear()
                        isSelectionMode = !isSelectionMode
                    },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(if (isSelectionMode) "취소" else "선택", fontSize = 16.sp)
                }

                if (isSelectionMode) {
                    OutlinedButton(
                        onClick = {
                            val toDelete = selectedItems.toList()


                            dbViewModel.updateSortOrder(mutableLocations.toList())

                            selectedItems.clear()
                            isSelectionMode = false

                            mutableLocations.removeAll(toDelete)
                            dbViewModel.deleteLocations(toDelete)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .alpha(0.5f),
                        border = BorderStroke(1.dp, Color.Red),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("삭제", fontSize = 16.sp, color = Color.Red)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            dbViewModel.updateSortOrder(mutableLocations)
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("확인", fontSize = 16.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mutableLocations, key = { it.l_id }) { loc ->
                val locationKey = loc.l_id.toString()
                LaunchedEffect(locationKey) {
                    weatherRepoVM.loadWeatherData(
                        lat = loc.lat.toDouble(),
                        lon = loc.lon.toDouble(),
                        key = locationKey,
                        startDate = today,
                        endDate = today
                    )
                }

                val weatherState by weatherRepoVM.getWeatherState(locationKey).collectAsState()

                if (weatherState is WeatherUIState.Loading) return@items

                val (weatherCode, temp, max, min) = when (weatherState) {
                    is WeatherUIState.Success -> {
                        val data = weatherState as WeatherUIState.Success
                        WeatherSummary(
                            data.current?.currentWeather?.weathercode?:120,
                            data.current?.currentWeather?.temperature?:99.9,
                            data.daily.daily.temperature_2m_max.firstOrNull() ?: 0.0,
                            data.daily.daily.temperature_2m_min.firstOrNull() ?: 0.0,
                        )
                    }

                    is WeatherUIState.Error -> WeatherSummary(120, 99.9 , 99.9, 99.9)
                    else -> WeatherSummary(120, 99.9, 99.9, 99.9)
                }

                ReorderableItem(reorderableState, key = loc.l_id) { isDragging ->
                    LocationWeatherCard(
                        data = LocationWeather(
                            name = loc.l_name,
                            lat = loc.lat,
                            lng = loc.lon,
                            weatherIcon = WeatherIconGetter(weatherCode),
                            weatherDesc = WeatherCodeConverter(weatherCode),
                            maxTemp = max,
                            minTemp = min,
                            l_id = loc.l_id,
                            currentTemp = temp
                        ),
                        isSelectionMode = isSelectionMode,
                        isChecked = selectedItems.contains(loc),
                        onCheckChange = { checked ->
                            if (checked) selectedItems.add(loc) else selectedItems.remove(loc)
                        },
                        modifier = Modifier
                            .shadow(if (isDragging) 4.dp else 0.dp)
                            .draggableHandle()
                    )
                }
            }
        }
    }
}

fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from == to) return
    val item = removeAt(from)
    add(if (from < to) to - 1 else to, item)
}
@Composable
fun LocationWeatherCard(
    data: LocationWeather,
    isSelectionMode: Boolean,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color.Black),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelectionMode) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = onCheckChange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column {
                    Row {
                        Text(text = data.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier= Modifier.width(8.dp))
                        Text(text = data.currentTemp.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                    Text(
                        text = "위도: ${"%.5f".format(data.lat.toDouble())}, 경도: ${"%.5f".format(data.lng.toDouble())}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = data.weatherIcon, fontSize = 24.sp)
                Text(text = data.weatherDesc, fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = "${data.maxTemp}° / ${data.minTemp}°",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class LocationWeather(
    val name: String,
    val lat: String,
    val lng: String,
    val weatherIcon: String,
    val weatherDesc: String,
    val maxTemp: Double,
    val minTemp: Double,
    val l_id : Int,
    val currentTemp : Double
)

data class WeatherSummary(
    val weatherCode: Int,
    val temp: Double,
    val max: Double,
    val min: Double
)