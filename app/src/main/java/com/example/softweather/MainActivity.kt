package com.example.softweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.softweather.model.permission.requestPermission
import com.example.softweather.navgraph.NavGraph
import com.example.softweather.ui.theme.GurumeMeguriTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBezEgctIzXiH2u5bd5m79fzSOinpf4IvA")
        }
        requestPermission(this)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavGraph(navController)
//            MainScreenMockup()
//            SearchScreenMockup()
//            ScheduleScreenMockup()
//            PastScreenMockup()
//            GoogleMapScreenMockup()
//        WeatherInfoScreenMockup("2025-05-15","건국대학교")
//            WeatherInfoScreen("2025-05-26", "건국대학교",37.5409,127.0795)
//        NotificationMockup()
//           CardListScreenMockup()
//            MainScreenMockup(52.0,64.0, navController)
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GurumeMeguriTheme {
        Greeting("Android")
    }
}