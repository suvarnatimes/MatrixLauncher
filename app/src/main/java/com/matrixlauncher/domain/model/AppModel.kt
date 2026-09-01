package com.matrixlauncher.domain.model

import android.os.UserHandle
import androidx.compose.runtime.Immutable

/**
 * Represents an installed application item in MatrixLauncher.
 */
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
    val installTime: Long = 0L
) {
    /**
     * Unique key for caching and diffing across multi-user environments.
     */
    val uniqueKey: String
        get() = "$packageName/$activityName#$userSerial"

    /**
     * The visible name displayed on the UI (respecting custom user rename).
     */
    val displayLabel: String
        get() = customLabel?.takeIf { it.isNotBlank() } ?: label

    /**
     * First character for alphabetical section grouping.
     */
    val sectionHeader: Char
        get() = displayLabel.firstOrNull()?.uppercaseChar()?.let { char ->
            if (char in 'A'..'Z') char else '#'
        } ?: '#'
}
