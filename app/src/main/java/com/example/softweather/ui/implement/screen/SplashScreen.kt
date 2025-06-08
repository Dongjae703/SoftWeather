import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.core.content.ContextCompat
import com.example.softweather.ui.implement.font.NotoSansKR
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine

@Composable
fun SplashScreen(onPermissionGranted: (Double, Double) -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var fineGranted by remember { mutableStateOf(false) }
    var notificationGranted by remember { mutableStateOf(Build.VERSION.SDK_INT < 33) }
    var locationFetched by remember { mutableStateOf(false) }

    // 위치 권한 런처
    val fineLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        fineGranted = granted
    }

    // 알림 권한 런처
    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationGranted = granted
    }

    // 권한 상태 체크
    LaunchedEffect(Unit) {
        fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        notificationGranted = if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    // 두 권한 다 허용되면 위치 받아오기
    LaunchedEffect(fineGranted, notificationGranted) {
        if (fineGranted && notificationGranted && !locationFetched) {
            locationFetched = true // 중복 호출 방지
            val location = try {
                suspendCancellableCoroutine<Location?> { cont ->
                    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
                        .setMaxUpdates(1).build()
                    val callback = object : com.google.android.gms.location.LocationCallback() {
                        override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                            cont.resume(result.lastLocation, onCancellation = null)
                            fusedLocationClient.removeLocationUpdates(this)
                        }
                    }
                    fusedLocationClient.requestLocationUpdates(request, callback, context.mainLooper)
                }
            } catch (e: Exception) {
                null
            }

            val lat = location?.latitude ?: 37.5410
            val lon = location?.longitude ?: 127.4702
            delay(1000)
            onPermissionGranted(lat, lon)
        }
    }

    // UI
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1976D2)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "SoftWeather", fontSize = 32.sp,
                fontWeight = FontWeight.Bold, color = Color.White, fontFamily = NotoSansKR
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (!fineGranted) {
                Button(onClick = {
                    fineLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }, colors = ButtonColors(Color.White,Color.White,Color.White,Color.White)) {
                    Text("위치 권한 요청", color = Color.Black)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (Build.VERSION.SDK_INT >= 33 && !notificationGranted) {
                Button(onClick = {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }, colors = ButtonColors(Color.White,Color.White,Color.White,Color.White)) {
                    Text("알림 권한 요청", color = Color.Black)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (fineGranted && notificationGranted) {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("위치 정보를 가져오는 중...", color = Color.White)
            }
        }
    }
}
