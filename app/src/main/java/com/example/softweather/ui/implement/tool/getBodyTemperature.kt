package com.example.softweather.ui.implement.tool

import kotlin.math.exp
import kotlin.math.pow

fun getBodyTemperature(temp: Double?, windSpeed: Double?, rh: Int): Double? {
    if (temp == null || windSpeed == null || rh == null) return null

    val windKmh = windSpeed * 3.6
    val rhDouble = rh.toDouble()
    // 1. Wind Chill (추위용 공식)
    val windChill = 13.12 +
            0.6215 * temp -
            11.37 * windKmh.pow(0.16) +
            0.3965 * temp * windKmh.pow(0.16)

    // 2. Sigmoid 기반 가중치 (10도 기준, 스무스 전환)
    val coldFactor = 1 / (1 + exp((temp - 10.0) * 2)) // temp < 10 → 1에 가까워짐

    // 3. Wind Chill vs 실제 기온 가중 평균 (보통 날씨용)
    val coldAdjusted = coldFactor * windChill + (1-coldFactor) * temp

    // 4. Heat Index 적용 조건: 기온 ≥ 27도, 습도 ≥ 40%
    if (temp >= 27.0 && rh >= 40) {
        val tempF = temp * 9 / 5 + 32
        val indexF = -42.379 + 2.04901523 * tempF + 10.14333127 * rh -
                0.22475541 * tempF * rh - 0.00683783 * tempF.pow(2) -
                0.05481717 * rhDouble.pow(2) + 0.00122874 * tempF.pow(2) * rh +
                0.00085282 * tempF * rhDouble.pow(2) - 0.00000199 * tempF.pow(2) * rhDouble.pow(2)
        return ((indexF - 32) * 5 / 9)
    }

    // 5. 기본: Wind Chill과 실제 기온의 자연스러운 전환 결과 사용
    return coldAdjusted
}