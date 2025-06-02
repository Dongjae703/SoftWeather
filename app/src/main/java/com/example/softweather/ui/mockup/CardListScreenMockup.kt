package com.example.softweather.ui.mockup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable


@Composable
fun CardListScreenMockup() {
    val mockLocations = remember {
        mutableStateListOf(
            LocationWeather("건국대학교", 37.5404, 127.0796, "☀️", "맑음", 26, 17),
            LocationWeather("혜화역", 37.5822, 127.0010, "🌥️", "흐림", 24, 18),
            LocationWeather("금돼지식당", 37.5593, 126.9930, "🌧️", "비", 22, 16)
        )
    }

    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<LocationWeather>() }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            mockLocations.apply {
                val item = removeAt(from.index)
                add(if (from.index < to.index) to.index - 1 else to.index, item)
            }
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
                            mockLocations.removeAll(selectedItems)
                            selectedItems.clear()
                            isSelectionMode = false
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
                        onClick = { /* 확인 동작 */ },
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
            state = reorderState.listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
                .reorderable(reorderState)
                .detectReorderAfterLongPress(reorderState),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mockLocations, key = { it.name }) { location ->
                ReorderableItem(reorderState, key = location.name) { isDragging ->
                    val elevation = if (isDragging) 4.dp else 0.dp
                    LocationWeatherCard(
                        data = location,
                        isSelectionMode = isSelectionMode,
                        isChecked = selectedItems.contains(location),
                        onCheckChange = { checked ->
                            if (checked) selectedItems.add(location) else selectedItems.remove(location)
                        },
                        modifier = Modifier.shadow(elevation)
                    )
                }
            }
        }
    }
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
                    Text(text = data.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "위도: ${data.lat}, 경도: ${data.lng}",
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
    val lat: Double,
    val lng: Double,
    val weatherIcon: String,
    val weatherDesc: String,
    val maxTemp: Int,
    val minTemp: Int
)



@Preview
@Composable
private fun CardListPrev() {
    CardListScreenMockup()
}