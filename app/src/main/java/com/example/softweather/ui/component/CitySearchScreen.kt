package com.example.softweather.ui.component

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.softweather.database.LocationDB
import com.example.softweather.model.NominatimPlace
import com.example.softweather.model.Routes
import com.example.softweather.viewmodel.DBViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.launch

@Composable
fun CitySearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<NominatimPlace>() }
    val suggestions = remember { mutableStateListOf<AutocompletePrediction>() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val dbViewModel: DBViewModel =
        viewModel<DBViewModel>(factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application))

    val placeClient = remember { Places.createClient(context) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                if (query.length >= 2) {
//                        val resultNominatim = RetrofitInstance.nominatimApi.searchPlace(query)
                    val result = FindAutocompletePredictionsRequest.builder()
                        .setQuery(query)
                        .build() // KR ON OFF 기능 추후 추가할 수 도
                    placeClient.findAutocompletePredictions(result)
                        .addOnSuccessListener { response ->
                            suggestions.clear()
                            suggestions.addAll(response.autocompletePredictions)
                        }
                        .addOnFailureListener { exception ->
                            suggestions.clear()
                        }
                } else {
                    suggestions.clear()
                }
            },
            label = { Text("도시 검색") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn {
            items(suggestions.toList()) { prediction ->
                TextButton(
                    onClick = {
                        val placeId = prediction.placeId
                        val placeField =
                            listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
                        val placeRQ = FetchPlaceRequest.newInstance(placeId, placeField)
                        placeClient.fetchPlace(placeRQ).addOnSuccessListener { response ->
                            val place = response.place
                            val latLng = place.latLng
                            if (latLng != null) {
                                coroutineScope.launch {
                                    dbViewModel.insertLocation(
                                        LocationDB(
                                            l_name = place.name ?: place.address
                                            ?: prediction.getFullText(null).toString(),
                                            lat = latLng.latitude.toString(),
                                            lon = latLng.longitude.toString()
                                        )
                                    )
                                }

                                navController.navigate(
                                    Routes.MainScreen.createRoute(
                                        latLng.latitude.toString(),
                                        latLng.longitude.toString(),
                                        place.name?:prediction.getFullText(null).toString()
                                    )
                                )
                            }
                        }
                    }
                ) {
                    Text(prediction.getFullText(null).toString())
                }
            }
        }
    }
}