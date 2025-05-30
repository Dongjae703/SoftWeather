package com.example.softweather.ui.mockup

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
fun WeatherCardMockup(date : String , location : String ,modifier: Modifier = Modifier) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        WeatherInfoScreenMockup(date = date, location = location)

        IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "점 세 개",
                tint = Color.Gray
            )
        }

        //드롭다운 위치 조정 필요 ( 현재는 아마 왼쪽 위에 뜸)
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("삭제") },
                onClick = {
                    menuExpanded = false
                    // TODO: 삭제 로직 연결
                }
            )
        }


        IconButton(
            onClick = { /* 구현 필요 */ },
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

        // 오른쪽 화살표
        IconButton(
            onClick = { /* 구현 필요 */ },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowForwardIos, //밑줄 쳐졌지만 사용 가능하니 신경 안쓰셔도 됩니다.
                contentDescription = "다음 지역",
                tint = Color.Gray,
                modifier = Modifier.size(32.dp).alpha(0.5f)
            )
        }
    }
}