package com.example.softweather.ui.implement.screen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri.encode
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.softweather.model.Routes
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    lat:Double,
    lon:Double,
    onConfirm: (Double,Double) -> Unit,
    navController: NavController
) {
    val initialLatLng = LatLng(lat, lon)

    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var locationName by remember { mutableStateOf("현 위치") }
    var isClick by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(lat, lon), 15f)
    }

    var selectedLatLng by remember {
        mutableStateOf(initialLatLng)
    }

    LaunchedEffect(selectedLatLng) {
        if (isClick) {
            locationName = getGeoPlaceName(
                lat = selectedLatLng.latitude,
                lon = selectedLatLng.longitude,
                apiKey = "AIzaSyBezEgctIzXiH2u5bd5m79fzSOinpf4IvA"
            )
            Log.i("mapscreen", locationName)
        }else{
            locationName = "현재 위치"
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = locationName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context, Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                location?.let {
                                    val newLatLng = LatLng(it.latitude, it.longitude)
                                    selectedLatLng = newLatLng

                                    cameraPositionState.move(
                                        CameraUpdateFactory.newLatLngZoom(newLatLng, cameraPositionState.position.zoom)
                                    )
                                }
                            }
                        }
                        isClick = false
                    },
                    border = BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("현위치")
                }

                OutlinedButton(
                    modifier =  Modifier.weight(1f),
                    onClick = {
                        onConfirm(selectedLatLng.latitude, selectedLatLng.longitude)

                        val selectedlat = selectedLatLng.latitude.toString()
                        val selectedlon = selectedLatLng.longitude.toString()
                        val encodedLocation = encode(locationName.ifBlank { "현재 위치" })
                        navController.navigate(Routes.MainScreen.createRoute(selectedlat, selectedlon, encodedLocation)) {
                            popUpTo(Routes.MainScreen.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    border = BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("확인")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Google Map
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLatLng = latLng
                    isClick = true
                }   // 터치 위치로 Marker 이동
            ){
                Marker(
                    state = MarkerState(position = selectedLatLng),
                    title = "선택된 위치"
                )
            }
        }
    }
}

//suspend fun getNearbyPlaceId(context: Context, latLng: LatLng, apiKey: String): String =
//    withContext(Dispatchers.IO) {
//        val url =
//            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${latLng.latitude},${latLng.longitude}&radius=50&key=$apiKey"
//        val request = Request.Builder().url(url).build()
//        val client = OkHttpClient()
//
//        client.newCall(request).execute().use { response ->
//            val responseBody = response.body?.string()
//            val jsonObject = JSONObject(responseBody ?: "")
//            val results = jsonObject.getJSONArray("results")
//            if (results.length() > 0) {
//                results.getJSONObject(0).getString("place_id")
//            } else {
//                throw Exception("근처 장소 없음")
//            }
//        }
//    }
//
private fun JSONArray.containsType(typeName: String): Boolean {
    for (i in 0 until length()) {
        if (getString(i) == typeName) return true
    }
    return false
}

// 실제 장소 이름 반환 함수
suspend fun getGeoPlaceName(lat: Double, lon: Double, apiKey: String): String =
    withContext(Dispatchers.IO) {
        val url = "https://maps.googleapis.com/maps/api/geocode/json" +
                "?latlng=$lat,$lon&language=ko&key=$apiKey"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: throw Exception("응답 없음")
            val json = JSONObject(responseBody)
            val results = json.getJSONArray("results")
            if (results.length() == 0) throw Exception("주소 결과 없음")

            val components = results.getJSONObject(0).getJSONArray("address_components")

            var geoCountry = ""
            var geoLevel1 = ""       // 시/도
            var geoLevel2 = ""       // 구/군
            var geoSubLocal = ""     // 동/면
            var geoRoute = ""        // 도로명

            for (i in 0 until components.length()) {
                val comp = components.getJSONObject(i)
                val types = comp.getJSONArray("types")

                when {
                    types.containsType("country") ->
                        geoCountry = comp.getString("long_name")

                    types.containsType("administrative_area_level_1") ->
                        geoLevel1 = comp.getString("long_name")

                    types.containsType("administrative_area_level_2") ->
                        geoLevel2 = comp.getString("long_name")

                    types.containsType("sublocality") || types.containsType("sublocality_level_1") ->
                        geoSubLocal = comp.getString("long_name")

                    types.containsType("route") ->
                        geoRoute = comp.getString("long_name")
                }
            }
            Log.i("api", "${geoCountry}, ${geoLevel1}, ${geoLevel2}, ${geoSubLocal}, ${geoRoute}")
            return@withContext if (geoCountry == "대한민국" || geoCountry == "South Korea" || geoCountry == "Korea") {
                listOf(geoLevel1, geoLevel2, geoSubLocal, geoRoute)
                    .filter { it.isNotEmpty() }
                    .joinToString(" ")
            } else {
                listOf(geoLevel1, geoLevel2)
                    .filter { it.isNotEmpty() }
                    .joinToString(", ")
            }
        }
    }

