package com.matrixlauncher.domain.model

import androidx.compose.runtime.Immutable
import java.util.Locale

@Immutable
data class ScreenTimeStats(
    val totalMillisToday: Long = 0L,
    val hasPermission: Boolean = false,
    val topAppPackage: String? = null
) {
    /**
     * Formatted string e.g. "2h 45m" or "32m".
     */
    val formattedDuration: String
        get() {
            if (!hasPermission) return "--"
            val totalMinutes = totalMillisToday / (1000 * 60)
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            return when {
                hours > 0 -> String.format(Locale.US, "%dh %02dm", hours, minutes)
                else -> String.format(Locale.US, "%dm", minutes)
            }
        }

    /**
     * Segment ratio out of daily target (e.g. 6 hours benchmark, 0.0 to 1.0).
     */
    val progressRatioToSixHours: Float
        get() {
            val maxMillis = 6 * 60 * 60 * 1000L
            return (totalMillisToday.toFloat() / maxMillis).coerceIn(0f, 1f)
        }
}
