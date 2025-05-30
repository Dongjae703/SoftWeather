package com.example.softweather.ui.implement.tool

import java.time.LocalDate

fun DayofWeekConverter(date : String):String{
    val parseDate = LocalDate.parse(date)
    return when(parseDate.dayOfWeek){
        java.time.DayOfWeek.MONDAY->"월"
        java.time.DayOfWeek.TUESDAY->"화"
        java.time.DayOfWeek.WEDNESDAY->"수"
        java.time.DayOfWeek.THURSDAY->"목"
        java.time.DayOfWeek.FRIDAY->"금"
        java.time.DayOfWeek.SATURDAY->"토"
        java.time.DayOfWeek.SUNDAY->"일"
    }
}