package com.example.softweather.ui.mockup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun PastScreenMockup() {
    var selectedTab by remember { mutableStateOf("과거") }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var searchedDate by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
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
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            //날짜 검색 바
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("예: 2023.05.01") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "검색 아이콘",
                        modifier = Modifier.clickable {
                            if (searchText.text.isNotBlank()) {
                                searchedDate = searchText.text
                            }
                        }
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.large,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    disabledBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            //검색 결과 표시 ( 검색 아이콘 누르면 뜨는게 의도)
            searchedDate?.let {
                WeatherCardMockup(searchedDate!!,"건국대학교")
            }

        }
    }
}

@Preview
@Composable
private fun PastPrev() {
    PastScreenMockup()
}