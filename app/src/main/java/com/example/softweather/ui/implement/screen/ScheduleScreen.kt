package com.example.softweather.ui.implement.screen

import DateConverter
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.softweather.database.ScheduleDB
import com.example.softweather.model.Routes
import com.example.softweather.model.WeatherUIState
import com.example.softweather.ui.implement.tool.NavigationBarTemplete
import com.example.softweather.ui.implement.tool.WeatherCodeConverter
import com.example.softweather.ui.implement.tool.WeatherIconGetter
import com.example.softweather.viewmodel.ScheduleDBViewModel
import com.example.softweather.viewmodel.WeatherRepoViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@Composable
fun ScheduleScreen(navController: NavController) {
    val currentRoute = Routes.ScheduleScreen.route
    val context = LocalContext.current
    val placeClient = remember { Places.createClient(context) }
    var selectedTab by remember { mutableStateOf("일정") }
    var selectedYM by remember { mutableStateOf(YearMonth.now(ZoneId.of("Asia/Seoul"))) }

    var showAddDialog by remember { mutableStateOf(false) }
    var scheduleTitle by remember { mutableStateOf("") }

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    val scheduleViewModel: ScheduleDBViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )

    val schedules by scheduleViewModel.scheduleListFlow.collectAsState(initial = emptyList())
    Log.e("scheduleScreen", "${schedules.size}")

    val weatherRepoVM: WeatherRepoViewModel = viewModel()

    Scaffold(
        modifier = Modifier.background(Color.White),
        containerColor = Color.White,
        floatingActionButton = {
            if (startDate != null) {
                FloatingActionButton(
                    onClick = {
                        scheduleViewModel.isScheduleDateDuplicate(startDate.toString(), endDate.toString()) { isDuplicate ->
                            if (isDuplicate) {
                                Toast.makeText(context, "이미 같은 날짜 범위의 일정이 존재합니다", Toast.LENGTH_SHORT).show()
                                return@isScheduleDateDuplicate
                            }
                            val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
                            if (startDate!!.isBefore(today.minusDays(3))) {
                                Toast.makeText(context, "시작일이 오늘의 3일이상 이전의 일정입니다.", Toast.LENGTH_SHORT).show()
                                return@isScheduleDateDuplicate
                            }
                            if (endDate?.isAfter(startDate!!.plusDays(14L)) == true) {
                                Toast.makeText(context, "종료일과 시작일이 15일 이상 차이납니다.", Toast.LENGTH_SHORT).show()
                                return@isScheduleDateDuplicate
                            }
                            showAddDialog = true
                        }
                    },
                    containerColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "일정 추가")
                }
            }
        },
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { selectedYM = selectedYM.minusMonths(1) },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBackIosNew,
                        contentDescription = "이전 달"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                //연월 드롭다운 선택기(완성된 상태입니다.)
                YearMonthPickerField(
                    selected = selectedYM,
                    onChange = { selectedYM = it },
                    modifier = Modifier.weight(0.8f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { selectedYM = selectedYM.plusMonths(1) },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                        contentDescription = "다음 달"
                    )
                }
            }


            Spacer(modifier = Modifier.height(12.dp))
            CalendarGrid(
                month = selectedYM,
                startDate = startDate,
                endDate = endDate,
                onDateSelected = { clickedDate ->
                    if (startDate == null) {
                        startDate = clickedDate
                    } else if (endDate == null && clickedDate.isAfter(startDate)) {
                        endDate = clickedDate
                    } else {
                        startDate = clickedDate
                        endDate = null
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Circle,
                    contentDescription = "빨간점",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Text(": 시작일", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.Circle,
                    contentDescription = "빨간점",
                    tint = Color.Green,
                    modifier = Modifier.size(16.dp)
                )
                Text(": 종료일", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(10.dp))
                Text("이후 날씨 확인만을 위한 일정입니다.", fontWeight = FontWeight.Medium, fontSize = 10.sp)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(8.dp))
            Text("등록된 일정", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(schedules) { schedule ->
                    val start = LocalDate.parse(schedule.startDate)
                    val end = LocalDate.parse(schedule.lastDate)
                    val lat = schedule.lat
                    val lon = schedule.lon
                    val key = "${schedule.lat}_${lon}_${lat}"
                    val lastAllowed = LocalDate.now(ZoneId.of("Asia/Seoul")).plusDays(14)  // 오늘 포함 15일
                    val actualEnd = minOf(end, lastAllowed).toString()
                    Log.d("OpenMeteoURL", "start=${schedule.startDate}, end=$actualEnd")
                    if (!start.isAfter(lastAllowed)) {
                        LaunchedEffect(key) {
                            weatherRepoVM.loadWeatherData(
                                lat = schedule.lat.toDouble(),
                                lon = schedule.lon.toDouble(),
                                key = key,
                                startDate = schedule.startDate,
                                endDate = actualEnd
                            )
                        }
                    }else {
                        LaunchedEffect(key) {
                            weatherRepoVM.setDummyWeather(
                                key = key,
                                start = start,
                                end = end
                            )
                        }
                    }
                    val weatherState by weatherRepoVM.getWeatherState(key).collectAsState()

                    when (weatherState) {
                        is WeatherUIState.Success -> {
                            val data = weatherState as WeatherUIState.Success
                            val dateRange = generateSequence(start) { it.plusDays(1) }
                                .takeWhile { !it.isAfter(end) }
                                .toList()

                            val availableDates = data.daily.daily.time.map { LocalDate.parse(it) }
                            val weatherList = dateRange.map { date ->
                                if (date in availableDates) {
                                    val idx = availableDates.indexOf(date)
                                    ScheduleDayInfo(
                                        date = date.toString(),
                                        location = schedule.location,
                                        tempMax = data.daily.daily.temperature_2m_max.getOrNull(idx),
                                        tempMin = data.daily.daily.temperature_2m_min.getOrNull(idx),
                                        weatherCode = data.daily.daily.weathercode.getOrNull(idx) ?: 120
                                    )
                                } else {
                                    ScheduleDayInfo(
                                        date = date.toString(),
                                        location = schedule.location,
                                        tempMax = null,
                                        tempMin = null,
                                        weatherCode = 120
                                    )
                                }
                            }



                            Log.e("success","${data.daily.daily.temperature_2m_max}")
                            ExpandableScheduleItemGroup(
                                title = schedule.title,
                                startDate = start,
                                endDate = end,
                                weatherList = weatherList,
                                schedule = schedule
                            )
                        }

                        is WeatherUIState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        else -> {
                            Text(
                                text = "날씨 정보를 불러오지 못했습니다: ${(weatherState as WeatherUIState.Error).message}",
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }

        }
        if (showAddDialog && startDate != null) {
            AddScheduleDialog(
                startDate = startDate,
                endDate = endDate,
                placeClient = placeClient,
                context = context,
                scheduleViewModel = scheduleViewModel,
                onDismiss = { showAddDialog = false }
            )
        }

    }
}

data class ScheduleDayInfo(
    val date: String,
    val location: String,
    val tempMax: Double?,
    val tempMin: Double?,
    val weatherCode: Int
)

@Composable
fun ExpandableScheduleItemGroup(
    title: String,
    startDate: LocalDate,
    endDate: LocalDate,
    weatherList: List<ScheduleDayInfo>,
    schedule: ScheduleDB
) {
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scheduleViewModel: ScheduleDBViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )
    var expandedDelete by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row (horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = { expandedDelete = true },
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "더보기",
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = expandedDelete,
                    onDismissRequest = { expandedDelete = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("삭제") },
                        onClick = {
                            expandedDelete = false
                            scheduleViewModel.deleteScheduleById(schedule.s_id)
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${startDate} ~ ${endDate}",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        val visibleList = if (expanded) weatherList else weatherList.take(1)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = if (expanded) 300.dp else 72.dp)
        ) {
            items(visibleList) { dayInfo ->
                ScheduleItem(
                    date = dayInfo.date,
                    location = dayInfo.location,
                    tempMax = dayInfo.tempMax,
                    tempMin = dayInfo.tempMin,
                    weatherCode = dayInfo.weatherCode
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (expanded)
                    Icons.Outlined.KeyboardArrowUp
                else
                    Icons.Outlined.KeyboardArrowDown,
                contentDescription = "펼치기",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

//달력 아래에 뜨는거 표시
@Composable
fun ScheduleItem(
    date: String,
    location: String,
    tempMax: Double?,
    tempMin: Double?,
    weatherCode: Int,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = WeatherIconGetter(weatherCode),
            fontSize = 36.sp,
            color = MaterialTheme.colorScheme.primary
        ) // 아이콘

        Column(modifier = Modifier.weight(1f)) {
            Text(text = DateConverter(date, useYear = true, useMonthDay = true), fontSize = 13.sp)
            Text(text = "$location  최고:${if (tempMax == 999.0) "--" else tempMax?:"--"} / 최저:${if (tempMin == 999.0) "--" else tempMin?:"--"}", fontWeight = FontWeight.Bold)
        }

        Text(
            text = if (weatherCode !=120) WeatherCodeConverter(weatherCode) else "--",
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 8.dp)
        )

    }
}

//자체 제작 캘린더
@Composable
fun CalendarGrid(
    month: YearMonth,
    startDate: LocalDate?,
    endDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val days = remember(month) { generateCalendarDates(month) }
    val boundaryDate = remember { LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(3) }

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

        days.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { day ->
                    val backgroundColor = when {
                        day == startDate -> Color.Red.copy(alpha = 0.3f)
                        startDate != null && endDate != null && day != null &&
                                !day.isBefore(startDate) && !day.isAfter(endDate) -> Color.Green.copy(
                            alpha = 0.3f
                        )

                        else -> Color.Transparent
                    }
                    val isBoundary = day == boundaryDate
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .drawBehind {
                                if (isBoundary) {
                                    drawLine(
                                        color = Color.Gray,
                                        start = Offset(0f, 0f),
                                        end = Offset(0f, size.height),
                                        strokeWidth = 4f
                                    )
                                }
                            }
                            .background(backgroundColor)
                            .clickable(enabled = day != null) {
                                day?.let { onDateSelected(it) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day?.dayOfMonth?.toString() ?: "",
                            textAlign = TextAlign.Center
                        )
                    }
                }

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
    onChange: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    startDate: LocalDate?,
    endDate: LocalDate?,
    placeClient: PlacesClient,
    context: Context,
    scheduleViewModel: ScheduleDBViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var l_name by remember { mutableStateOf<String?>(null) }
    val suggestions = remember { mutableStateListOf<AutocompletePrediction>() }
    var expanded by remember { mutableStateOf(false) }

    val actualStart = startDate ?: return
    val actualEnd = endDate ?: startDate

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("일정 추가", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                Text("선택 날짜: ${actualStart} ~ ${actualEnd}", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("일정 제목") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 장소 자동완성
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            l_name = null
                            if (it.text.isNotBlank()) {
                                val request = FindAutocompletePredictionsRequest.builder()
                                    .setQuery(it.text)
                                    .build()

                                placeClient.findAutocompletePredictions(request)
                                    .addOnSuccessListener { response ->
                                        suggestions.clear()
                                        suggestions.addAll(response.autocompletePredictions)
                                        expanded = suggestions.isNotEmpty()
                                    }
                                    .addOnFailureListener {
                                        suggestions.clear()
                                        expanded = false
                                    }
                            } else {
                                suggestions.clear()
                                expanded = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryEditable,
                                enabled = true
                            ),
                        label = { Text("장소 검색") },
                        trailingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "검색")
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = Color.White
                    ) {
                        suggestions.forEach { prediction ->
                            val name = prediction.getPrimaryText(null).toString()
                            val fullText = prediction.getFullText(null).toString()

                            DropdownMenuItem(
                                text = { Text(fullText) },
                                onClick = {
                                    searchText = TextFieldValue(fullText)
                                    l_name = name
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("취소")
                    }
                    TextButton(onClick = {
                        if (title.isBlank()) {
                            Toast.makeText(context, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }

                        val prediction = suggestions.firstOrNull()
                        val nameToInsert = l_name ?: prediction?.getPrimaryText(null)?.toString()

                        if (!nameToInsert.isNullOrBlank() && prediction != null) {
                            val placeId = prediction.placeId
                            val placeFields = listOf(
                                Place.Field.DISPLAY_NAME,
                                Place.Field.FORMATTED_ADDRESS,
                                Place.Field.LOCATION
                            )

                            val request = FetchPlaceRequest.builder(placeId, placeFields).build()
                            placeClient.fetchPlace(request)
                                .addOnSuccessListener { response ->
                                    val place = response.place
                                    val latLng = place.location
                                    val name = place.displayName ?: nameToInsert

                                    if (latLng != null) {
                                        val schedule = ScheduleDB(
                                            title = title,
                                            startDate = actualStart.toString(),
                                            lastDate = actualEnd.toString(),
                                            location = name,
                                            lat = latLng.latitude.toString(),
                                            lon = latLng.longitude.toString()
                                        )

                                        scheduleViewModel.insertSchedule(schedule)
                                        Toast.makeText(
                                            context,
                                            "$name 일정 저장 완료",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onDismiss()
                                    }
                                    }
                                }
                    }) {
                        Text("확인")
                    }
                }
            }
        }
    }
}



