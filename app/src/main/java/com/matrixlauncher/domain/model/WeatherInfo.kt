package com.matrixlauncher.domain.model

import androidx.compose.runtime.Immutable

enum class WeatherCondition(val label: String, val dotSymbol: String) {
    CLEAR("CLEAR", "SUN"),
    CLOUDY("CLOUDY", "CLD"),
    RAIN("RAIN", "RAIN"),
    STORM("THUNDER", "STRM"),
    SNOW("SNOW", "SNOW"),
    WIND("WINDY", "WIND");

    companion object {
        fun fromCode(code: Int): WeatherCondition {
            return when (code) {
                0, 1 -> CLEAR
                2, 3 -> CLOUDY
                51, 53, 55, 61, 63, 65 -> RAIN
                95, 96, 99 -> STORM
                71, 73, 75, 85, 86 -> SNOW
                else -> CLEAR
            }
        }
    }
}

@Immutable
data class WeatherInfo(
    val temperatureCelsius: Int = 22,
    val condition: WeatherCondition = WeatherCondition.CLEAR,
    val location: String = "LOCAL",
    val isAvailable: Boolean = true
) {
    val formattedString: String
        get() = "${temperatureCelsius}°C // ${condition.label}"
}

@Immutable
data class CalendarEventInfo(
    val title: String = "",
    val timeFormatted: String = "",
    val isAllDay: Boolean = false,
    val hasEvent: Boolean = false
) {
    val formattedGlance: String
        get() = if (hasEvent) "$timeFormatted // $title".uppercase() else ""
}
