package com.example.softweather.ui.implement.screen

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.softweather.viewmodel.DBViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val currentRoute = Routes.SearchScreen.route

    val context = LocalContext.current
    val dbViewModel: DBViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )

    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var l_name by remember { mutableStateOf<String?>(null) }
    val suggestions = remember { mutableStateListOf<AutocompletePrediction>() }
    val placeClient = remember { Places.createClient(context) }
    var expanded by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf("검색") }


    Scaffold(
        modifier = Modifier.background(Color.White),
        containerColor = Color.White,
        bottomBar = {
            Column {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
                NavigationBarTemplete(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    currentRoute = currentRoute,
                    navController = navController
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        l_name = null
                        if (it.text.isNotBlank()) {
                            val request = FindAutocompletePredictionsRequest.builder()
                                .setQuery(it.text)
                                .build()

                            placeClient.findAutocompletePredictions(request)
                                .addOnSuccessListener { response ->
                                    suggestions.clear()
                                    suggestions.addAll(response.autocompletePredictions)
                                    expanded = suggestions.isNotEmpty()
                                }
                        } else {
                            suggestions.clear()
                            expanded = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryEditable,
                            enabled = true
                        ),
                    label = { Text("장소 검색") },
                    trailingIcon = {
                        IconButton(onClick = {
                            val prediction = suggestions.firstOrNull()
                            val nameToInsert = l_name ?: prediction?.getPrimaryText(null)?.toString()

                            if (!nameToInsert.isNullOrBlank() && prediction != null) {
                                val placeId = prediction.placeId
                                val placeFields = listOf(
                                    Place.Field.DISPLAY_NAME,
                                    Place.Field.FORMATTED_ADDRESS,
                                    Place.Field.LOCATION
                                )

                                val request = FetchPlaceRequest.builder(placeId, placeFields).build()
                                placeClient.fetchPlace(request)
                                    .addOnSuccessListener { response ->
                                        val place = response.place
                                        val latLng = place.location

                                        if (latLng != null) {
                                            val name = place.displayName ?: nameToInsert

                                            val location = LocationDB(
                                                l_name = name,
                                                lat = latLng.latitude.toString(),
                                                lon = latLng.longitude.toString()
                                            )

                                            dbViewModel.insertLocationIfNotDuplicateSorted(location) { inserted ->
                                                if (inserted) {
                                                    Toast.makeText(context, "$name 추가됨", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "$name 은 이미 등록되어 있음", Toast.LENGTH_SHORT).show()
                                                }
                                                expanded = false
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("SearchScreen", "Place fetch 실패: ${e.message}", e)
                                    }
                            }
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "검색")
                        }
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    suggestions.forEach { prediction ->
                        val name = prediction.getPrimaryText(null).toString()
                        val fullText = prediction.getFullText(null).toString()

                        DropdownMenuItem(
                            text = { Text(fullText) },
                            onClick = {
                                searchText = TextFieldValue(fullText)
                                l_name = name
                                expanded = false
                            }
                        )
                    }
                }
            }
            WeatherCardScreen(
                date = LocalDate.now(),
                isPast = false,
                navController = navController
            )
        }
    }
}