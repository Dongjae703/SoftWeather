package com.example.softweather.ui.implement.tool

fun IsRainDecider(weatherCode : Int?) : Int{
    return when (weatherCode) {
        0,1,2,3,45,48 -> 0
        51,53,55,56,57,61,63,65,66,67,80,81,82 -> 1
        71,73,75,77,85,86 -> 2
        95,96,99 -> 3
        else -> -1 // Unknown weather code
    }
}