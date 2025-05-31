package com.example.softweather.navgraph

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.softweather.model.Routes
import com.example.softweather.ui.implement.screen.MainScreen
import com.example.softweather.ui.implement.screen.MapScreen
import com.example.softweather.ui.implement.screen.SplashScreen
import com.example.softweather.ui.mockup.MainScreenMockup

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Routes.SplashScreen.route) {
        composable(Routes.SplashScreen.route) {
            SplashScreen(onPermissionGranted = {lat, lon->
                navController.navigate(Routes.MainScreen.createRoute(lat.toString(),lon.toString())) {
                    popUpTo("splash") { inclusive = true }
                }
            }
            )
        }
        composable(
            route = Routes.MainScreen.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()
            if (lat != null && lon != null) {
                MainScreen(lat, lon, navController)
            } else {
                Text("위치 정보 오류")
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
                MapScreen(lat, lon,{ newLat, newLon ->
                    navController.navigate("mainScreen/${newLat}/${newLon}") {
                        popUpTo("mainScreen/{lat}/{lon}") { inclusive = true }
                    }
                }, navController)
            } else {
                Text("위치 정보 오류")
            }
        }
    }
}