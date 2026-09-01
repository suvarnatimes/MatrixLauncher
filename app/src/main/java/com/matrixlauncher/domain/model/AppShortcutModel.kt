package com.matrixlauncher.domain.model

import android.content.pm.ShortcutInfo
import android.os.UserHandle
import androidx.compose.runtime.Immutable

@Immutable
data class AppShortcutModel(
    val id: String,
    val packageName: String,
    val shortLabel: String,
    val longLabel: String? = null,
    val userHandle: UserHandle? = null,
    val isDynamic: Boolean = false,
    val isPinned: Boolean = false
) {
    val displayLabel: String
        get() = shortLabel.ifBlank { longLabel ?: id }
}
