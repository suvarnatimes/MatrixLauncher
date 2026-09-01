package com.matrixlauncher.domain.model

import android.os.UserHandle
import androidx.compose.runtime.Immutable

@Immutable
data class AppModel(
    val packageName: String,
    val activityName: String,
    val label: String,
    val customLabel: String? = null,
    val userHandle: UserHandle? = null,
    val userSerial: Long = 0L,
    val isWorkProfile: Boolean = false,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val installTime: Long = 0L,
    val customIconUri: String? = null,
    val customIconColorHex: String? = null,
    val customGlyphName: String? = null,
    val customIconShape: String? = null
) {
    val displayLabel: String
        get() = customLabel ?: label

    val sectionHeader: Char
        get() {
            val firstChar = displayLabel.firstOrNull()?.uppercaseChar() ?: '#'
            return if (firstChar in 'A'..'Z') firstChar else '#'
        }

    val uniqueKey: String
        get() = "${packageName}_${activityName}_${userSerial}"
}
