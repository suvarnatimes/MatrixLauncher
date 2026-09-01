package com.matrixlauncher.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class BatteryInfo(
    val level: Int = 100,
    val isCharging: Boolean = false,
    val isPowerSave: Boolean = false
) {
    /**
     * Number of active dots to illuminate on a 10-dot bar.
     */
    val activeDotsOnTenScale: Int
        get() = (level / 10).coerceIn(0, 10)
}
