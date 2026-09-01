package com.matrixlauncher.ui.common

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Immutable

@Immutable
data class SystemSettingShortcut(
    val id: String,
    val title: String,
    val description: String,
    val intentAction: String
)

object SystemSettingsShortcuts {

    val ALL_SHORTCUTS = listOf(
        SystemSettingShortcut("wifi", "WI-FI SETTINGS", "Configure wireless networks", Settings.ACTION_WIFI_SETTINGS),
        SystemSettingShortcut("bluetooth", "BLUETOOTH", "Pair & manage devices", Settings.ACTION_BLUETOOTH_SETTINGS),
        SystemSettingShortcut("battery", "BATTERY & POWER", "Usage, saver & optimization", Settings.ACTION_BATTERY_SAVER_SETTINGS),
        SystemSettingShortcut("display", "DISPLAY & BRIGHTNESS", "Screen timeout, dark theme", Settings.ACTION_DISPLAY_SETTINGS),
        SystemSettingShortcut("sound", "SOUND & VIBRATION", "Volume, do not disturb", Settings.ACTION_SOUND_SETTINGS),
        SystemSettingShortcut("storage", "STORAGE MANAGER", "Internal storage & files", Settings.ACTION_INTERNAL_STORAGE_SETTINGS),
        SystemSettingShortcut("apps", "APPLICATION MANAGER", "App permissions & defaults", Settings.ACTION_APPLICATION_SETTINGS),
        SystemSettingShortcut("location", "LOCATION ACCESS", "GPS & location services", Settings.ACTION_LOCATION_SOURCE_SETTINGS),
        SystemSettingShortcut("security", "SECURITY & PRIVACY", "Screen lock & biometric", Settings.ACTION_SECURITY_SETTINGS),
        SystemSettingShortcut("network", "NETWORK & INTERNET", "Mobile data & SIM cards", Settings.ACTION_WIRELESS_SETTINGS),
        SystemSettingShortcut("date", "DATE & TIME", "Time zones & 24h clock", Settings.ACTION_DATE_SETTINGS),
        SystemSettingShortcut("accessibility", "ACCESSIBILITY", "Screen reader & display text", Settings.ACTION_ACCESSIBILITY_SETTINGS)
    )

    fun searchShortcuts(query: String): List<SystemSettingShortcut> {
        val trimmed = query.trim().lowercase()
        if (trimmed.length < 2) return emptyList()

        return ALL_SHORTCUTS.filter { shortcut ->
            shortcut.id.contains(trimmed) ||
            shortcut.title.lowercase().contains(trimmed) ||
            shortcut.description.lowercase().contains(trimmed)
        }
    }

    fun launch(context: Context, shortcut: SystemSettingShortcut) {
        val intent = Intent(shortcut.intentAction).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        }
    }
}
