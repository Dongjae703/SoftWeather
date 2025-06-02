package com.example.softweather.ui.implement.screen

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.softweather.database.LocationDB
import com.example.softweather.model.Routes
import com.example.softweather.ui.implement.tool.NavigationBarTemplete
import com.example.softweather.ui.mockup.WeatherCardMockup
import com.example.softweather.viewmodel.DBViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(navController: NavController) {
    val currentRoute = Routes.SearchScreen.route
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedTab by remember { mutableStateOf("검색") }

    val suggestions = remember { mutableStateListOf<AutocompletePrediction>() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val dbViewModel: DBViewModel =
        viewModel<DBViewModel>(factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application))
    val placeClient = remember { Places.createClient(context) }


    Scaffold(
        modifier = Modifier.background(Color.White),
        containerColor = Color.White,
        bottomBar = {
            Column {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
                NavigationBarTemplete(selectedTab, onTabSelected = {selectedTab=it},currentRoute,navController)
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
                onValueChange = {
                    searchText = it
                    val query = it.text
                    if (query.length >= 2) {
                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(query)
                            .build()

                        placeClient.findAutocompletePredictions(request)
                            .addOnSuccessListener { response ->
                                suggestions.clear()
                                suggestions.addAll(response.autocompletePredictions)
                            }
                            .addOnFailureListener {
                                suggestions.clear()
                            }
                    } else {
                        suggestions.clear()
                    }
                },
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

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(suggestions.toList()) { prediction ->
                    TextButton(
                        onClick = {
                            val placeId = prediction.placeId
                            val placeFields = listOf(
                                Place.Field.LAT_LNG,
                                Place.Field.NAME,
                                Place.Field.ADDRESS
                            )
                            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
                            placeClient.fetchPlace(request)
                                .addOnSuccessListener { response ->
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
                                    }
                                }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(prediction.getFullText(null).toString())
                    }
                }
            }
        }
    }
}