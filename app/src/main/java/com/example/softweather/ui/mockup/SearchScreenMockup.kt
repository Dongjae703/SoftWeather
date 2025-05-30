package com.example.softweather.ui.mockup

import androidx.compose.foundation.background
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
fun SearchScreenMockup() {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedTab by remember { mutableStateOf("검색") }

    Scaffold(
        modifier = Modifier.background(Color.White),
        containerColor = Color.White,
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
            //검색창
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("지역을 검색하세요") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search Icon"
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
            //중간에 날씨 카드 창
            WeatherCardMockup("2025-05-15","건국대학교")

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchScreenMockup() {
    SearchScreenMockup()
}
