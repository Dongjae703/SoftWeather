package com.example.softweather.model

import com.example.softweather.model.currentday.OpenMeteoApi
import com.example.softweather.model.daily.DailyPastWeatherApi
import com.example.softweather.model.daily.DailyWeatherApi
import com.example.softweather.model.hourly.HourlyPastWeatherApi
import com.example.softweather.model.hourly.HourlyWeatherApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

//    private val nominatimClient = OkHttpClient.Builder()
//        .addInterceptor { chain ->
//            val request = chain.request().newBuilder()
//                .header("User-Agent", "MyWeatherApp/1.0")
//                .build()
//            chain.proceed(request)
//        }
//        .build()

    private val defaultClient = OkHttpClient.Builder().build()
//
//    val nominatimApi: NominatimService by lazy {
//        Retrofit.Builder()
//            .baseUrl("https://nominatim.openstreetmap.org/")
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .client(nominatimClient)
//            .build()
//            .create(NominatimService::class.java)
//    }
//    val googlePlaceApi: NominatimService by lazy {
//        Retrofit.Builder()
//            .baseUrl("https://maps.googleapis.com/")
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .client(nominatimClient)
//            .build()
//            .create(NominatimService::class.java)
//    }

    val openMeteoApi: OpenMeteoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(defaultClient)
            .build()
            .create(OpenMeteoApi::class.java)
    }

    val hourlyWeatherApi: HourlyWeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(defaultClient)
            .build()
            .create(HourlyWeatherApi::class.java)
    }

    val hourlyPastWeatherApi: HourlyPastWeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://archive-api.open-meteo.com/v1/era5/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(defaultClient)
            .build()
            .create(HourlyPastWeatherApi::class.java)
    }

    val dailyWeatherApi: DailyWeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(defaultClient)
            .build()
            .create(DailyWeatherApi::class.java)
    }

    val dailyPastWeatherApi: DailyPastWeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://archive-api.open-meteo.com/v1/era5/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(defaultClient)
            .build()
            .create(DailyPastWeatherApi::class.java)
    }
}
