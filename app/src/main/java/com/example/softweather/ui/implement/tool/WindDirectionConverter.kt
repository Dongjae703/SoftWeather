package com.example.softweather.ui.implement.tool

fun WindDirectionConverter(windDirection : Double): Float {
    when(windDirection){
        in 0.0..<22.5, in 337.5..<360.0-> return 0f
        in 22.5..<67.5 -> return 45f
        in 67.5..<112.5 -> return 90f
        in 112.5..<157.5 -> return 135f
        in 157.5..202.5-> return 180f
        in 202.5..<247.5-> return 225f
        in 247.5..292.5 -> return 270f
        in 292.5..337.5 -> return 315f
        else -> return 1080f
    }
}