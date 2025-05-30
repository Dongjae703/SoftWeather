package com.example.softweather.ui.implement.screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.softweather.ui.implement.font.NotoSansKR
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun SplashScreen(onPermissionGranted: (Double, Double) -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var permissionResult by remember { mutableStateOf<Boolean?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionResult = true
        } else {
            val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (showRationale) {
                Toast.makeText(context, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "설정에서 위치 권한을 허용해주세요", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        }
    }

    // 권한 요청
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionResult = true
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // 위치 요청 로직: requestLocationUpdates
    LaunchedEffect(permissionResult) {
        if (permissionResult == true) {
            val location = try {
                suspendCancellableCoroutine<Location?> { cont ->
                    val request = com.google.android.gms.location.LocationRequest.create().apply {
                        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
                        interval = 1000
                        numUpdates = 1
                    }

                    val callback = object : com.google.android.gms.location.LocationCallback() {
                        override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                            cont.resume(result.lastLocation, onCancellation = null)
                            fusedLocationClient.removeLocationUpdates(this)
                        }

                        override fun onLocationAvailability(p0: com.google.android.gms.location.LocationAvailability) {
                            super.onLocationAvailability(p0)
                        }
                    }

                    fusedLocationClient.requestLocationUpdates(request, callback, context.mainLooper)
                }
            } catch (e: Exception) {
                null
            }

            val lat = location?.latitude ?: 37.5410
            val lon = location?.longitude ?: 127.4702

            delay(1500) // 부하 줄이기 위해 1.5초 대기
            onPermissionGranted(lat, lon)
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1976D2)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SoftWeather",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = NotoSansKR
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = Color.White)
        }
    }
}


