package com.example.softweather.navgraph

import SplashScreen
import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.softweather.model.Routes
import com.example.softweather.ui.implement.screen.CardListScreen
import com.example.softweather.ui.implement.screen.MainScreen
import com.example.softweather.ui.implement.screen.MapScreen
import com.example.softweather.ui.implement.screen.PastScreen
import com.example.softweather.ui.implement.screen.ScheduleScreen
import com.example.softweather.ui.implement.screen.SearchScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Routes.SplashScreen.route) {
        composable(Routes.SplashScreen.route) {
            SplashScreen(onPermissionGranted = { lat, lon ->
                navController.navigate(
                    Routes.MainScreen.createRoute(
                        lat.toString(),
                        lon.toString(),
                        "현재 위치"
                    )
                ) {
                    popUpTo("splash") { inclusive = true }
                }
            }
            )
        }
        composable(
            route = Routes.MainScreen.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType },
                navArgument("locationName"){ type = NavType.StringType}
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()
            val locationName = backStackEntry.arguments?.getString("locationName")
            if (lat != null && lon != null && locationName != null) {

                MainScreen(locationName ,lat, lon, navController)
            }
        }
        composable(
            route = Routes.MapScreen.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()
            if (lat != null && lon != null) {
                MapScreen(lat, lon, { newLat, newLon ->
                    val encodedLocation = Uri.encode("현재 위치")
                    navController.navigate(Routes.MainScreen.createRoute(newLat.toString(),newLon.toString(),encodedLocation)) {
                        popUpTo(Routes.MainScreen.route) { inclusive = true }
                    }
                }, navController)

            } else {
                Text("위치 정보 오류")
            }
        }
        composable(Routes.SearchScreen.route) {
            SearchScreen(navController)
        }
        composable(Routes.ScheduleScreen.route){
            ScheduleScreen(navController)
        }
        composable(Routes.PastScreen.route){
            PastScreen(navController)
        }

        composable(Routes.CardListScreen.route){
            CardListScreen(navController)
        }
    }

}