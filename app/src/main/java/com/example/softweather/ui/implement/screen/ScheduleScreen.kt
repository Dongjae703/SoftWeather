package com.example.softweather.ui.implement.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.softweather.model.Routes
import com.example.softweather.ui.implement.tool.NavigationBarTemplete
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun ScheduleScreen(navController: NavController) {
    val currentRoute = Routes.MainScreen.route
    var selectedTab by remember { mutableStateOf("일정") }
    var selectedYM by remember { mutableStateOf(YearMonth.now()) }
    Scaffold(
        modifier = Modifier.background(Color.White),
        containerColor = Color.White,
        bottomBar = {
            Column {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
                NavigationBarTemplete(
                    selectedTab,
                    onTabSelected = { selectedTab = it },
                    currentRoute,
                    navController
                )
            }
        }
    )


    { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            //연월 드롭다운 선택기(완성된 상태입니다.)
            YearMonthPickerField(
                selected = selectedYM,
                onChange = { selectedYM = it }
            )

            Spacer(modifier = Modifier.height(12.dp))
            CalendarGrid(month = selectedYM)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(8.dp))
            Text("등록된 일정", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("- 부산여행", fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp, top = 4.dp))

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            ScheduleItem(
                icon = Icons.Outlined.WbSunny,
                date = "7월 26일",
                location = "부산",
                temp = "25°",
                weatherDescription = "맑음"
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            ScheduleItem(
                icon = Icons.Outlined.Cloud,
                date = "7월 27일",
                location = "부산",
                temp = "23°",
                weatherDescription = "부분적으로 흐림"
            )
        }
    }
}

//달력 아래에 뜨는거 표시
@Composable
fun ScheduleItem(
    icon: ImageVector,
    date: String,
    location: String,
    temp: String,
    weatherDescription: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 날씨 아이콘
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .padding(end = 12.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        // 날짜, 위치, 기호
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = date,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$location  $temp",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        //날씨코드
        Text(
            text = weatherDescription,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

//자체 제작 캘린더
@Composable
fun CalendarGrid(month: YearMonth) {
    val days = remember(month) { generateCalendarDates(month) }

    Column {
        // 요일 헤더
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        }

        // 그리드
        days.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day?.dayOfMonth?.toString() ?: "",
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 빈 칸으로 라인 채우기 (안 채우면 가중치 이상해서 모양 무너짐)
                if (week.size < 7) {
                    repeat(7 - week.size) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}


fun generateCalendarDates(month: YearMonth): List<LocalDate?> {
    val firstDay = month.atDay(1)
    val lastDay = month.atEndOfMonth()
    val dayOfWeek = firstDay.dayOfWeek.value % 7  // 일요일=0, 월=1, ..., 토=6

    val days = mutableListOf<LocalDate?>()
    repeat(dayOfWeek) { days.add(null) }

    for (day in 1..lastDay.dayOfMonth) {
        days.add(month.atDay(day))
    }

    return days
}

@Composable
fun YearMonthPickerField(
    selected: YearMonth,
    onChange: (YearMonth) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = "${selected.year}년 ${selected.monthValue}월",
            onValueChange = {},
            readOnly = true,
            label = { Text("날짜 선택") },
            trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null) },
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )

        // 클릭만 감지하는 투명 박스 ( OutlinedTextField는 클리커블이 자꾸 말썽 피워서 그냥 투명블록 만들었어요)
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    showBottomSheet = true
                }
        )
    }

    if (showBottomSheet) {
        YearMonthPickerBottomSheet(
            initial = selected,
            onDismiss = { showBottomSheet = false },
            onConfirm = {
                onChange(it)
                showBottomSheet = false
            }
        )
    }
}

@Composable
fun YearMonthPickerBottomSheet(
    initial: YearMonth,
    onDismiss: () -> Unit,
    onConfirm: (YearMonth) -> Unit
) {
    val years = (2001..2077).toList()
    val months = (1..12).toList()

    var selectedYear by remember { mutableStateOf(initial.year) }
    var selectedMonth by remember { mutableStateOf(initial.monthValue) }

    val yearListState =
        rememberLazyListState(initialFirstVisibleItemIndex = years.indexOf(initial.year))
    val monthListState =
        rememberLazyListState(initialFirstVisibleItemIndex = months.indexOf(initial.monthValue))

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("연도 / 월 선택", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(16.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp * 6), // 최대 6개씩 보이게
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LazyColumn(
                        state = yearListState,
                        modifier = Modifier.weight(1f)
                    ) {
                        items(years) { year ->
                            Text(
                                text = "${year}년",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedYear = year
                                    }
                                    .padding(vertical = 12.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = if (year == selectedYear) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    LazyColumn(
                        state = monthListState,
                        modifier = Modifier.weight(1f)
                    ) {
                        items(months) { month ->
                            Text(
                                text = "${month}월",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMonth = month
                                    }
                                    .padding(vertical = 12.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = if (month == selectedMonth) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("취소")
                    }
                    TextButton(onClick = {
                        onConfirm(YearMonth.of(selectedYear, selectedMonth))
                    }) {
                        Text("확인")
                    }
                }
            }
        }
    }
}