package com.example.softweather.ui.implement.tool

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.softweather.model.LocationHolder.lat
import com.example.softweather.model.LocationHolder.locationName
import com.example.softweather.model.LocationHolder.lon
import com.example.softweather.model.Routes

@Composable
fun NavigationBarTemplete(selectedTab:String,onTabSelected : (String) -> Unit,currentRoute:String,navController:NavController) {

    NavigationBar(containerColor = Color.White) {
        BottomBarItem("홈", Icons.Outlined.Home, selectedTab == "홈") {
            onTabSelected("홈")
            if (currentRoute != Routes.MainScreen.route) {
                navController.navigate(
                    Routes.MainScreen.createRoute(lat.toString(), lon.toString(), locationName)
                ) {
                    popUpTo(Routes.MainScreen.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
        }
        BottomBarItem("검색", Icons.Outlined.Search, selectedTab == "검색") {
            onTabSelected("검색")
            if (currentRoute != Routes.SearchScreen.route) {
                navController.navigate(Routes.SearchScreen.route){
                    popUpTo(Routes.MainScreen.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
        }
        BottomBarItem("일정", Icons.Outlined.Event, selectedTab == "일정") {
            onTabSelected("일정")
            if (currentRoute != Routes.SceduleScreen.route) {
                navController.navigate(Routes.SceduleScreen.route){
                    popUpTo(Routes.MainScreen.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
        }
        BottomBarItem("과거", Icons.Outlined.History, selectedTab == "과거") {
            onTabSelected("과거")
            if (currentRoute != Routes.PastScreen.route) {
                navController.navigate(Routes.PastScreen.route){
                    popUpTo(Routes.MainScreen.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
        }
    }
}