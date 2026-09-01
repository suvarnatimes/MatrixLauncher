package com.matrixlauncher.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.DotDensity
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.DoubleTapAction
import com.matrixlauncher.domain.model.LauncherSettings
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.SwipeGestureAction
import com.matrixlauncher.domain.model.WebSearchProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "matrix_launcher_preferences")

class PreferencesManager(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val KEY_ACCENT_COLOR = stringPreferencesKey("accent_color")
        val KEY_CUSTOM_HEX = stringPreferencesKey("custom_accent_hex")
        val KEY_DOT_DENSITY = stringPreferencesKey("dot_density")
        val KEY_DOT_SHAPE = stringPreferencesKey("dot_shape")
        val KEY_SCROLLER_ALIGN = stringPreferencesKey("scroller_alignment")
        val KEY_DOUBLE_TAP_ACTION = stringPreferencesKey("double_tap_action")
        val KEY_SWIPE_LEFT_ACTION = stringPreferencesKey("swipe_left_action")
        val KEY_SWIPE_RIGHT_ACTION = stringPreferencesKey("swipe_right_action")
        val KEY_SEARCH_PROVIDER = stringPreferencesKey("search_provider")
        val KEY_IS_24_HOUR = booleanPreferencesKey("is_24_hour")
        val KEY_SHOW_SCREEN_TIME = booleanPreferencesKey("show_screen_time")
        val KEY_SHOW_BATTERY_BAR = booleanPreferencesKey("show_battery_bar")
        val KEY_SHOW_SCRATCHPAD = booleanPreferencesKey("show_scratchpad")
        val KEY_SCRATCHPAD_NOTE = stringPreferencesKey("scratchpad_note")
        val KEY_HAPTICS = booleanPreferencesKey("haptics_enabled")
        val KEY_AGSL_SHADER = booleanPreferencesKey("agsl_shader_enabled")
        val KEY_CRT_SCANLINES = booleanPreferencesKey("crt_scanlines_enabled")
        val KEY_AUTO_FOCUS_SEARCH = booleanPreferencesKey("auto_focus_search")
        val KEY_MAX_FAVORITES = intPreferencesKey("max_favorites")
        val KEY_MINDFUL_PAUSE = intPreferencesKey("mindful_pause_seconds")
        val KEY_MINDFUL_APPS = stringSetPreferencesKey("mindful_apps_set")
        val KEY_FAVORITES = stringSetPreferencesKey("pinned_favorites")
    }

    val settingsFlow: Flow<LauncherSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            LauncherSettings(
                accentColor = AccentColor.fromName(prefs[KEY_ACCENT_COLOR]),
                customAccentHex = prefs[KEY_CUSTOM_HEX] ?: "#FF2E2E",
                dotDensity = DotDensity.fromName(prefs[KEY_DOT_DENSITY]),
                dotShape = DotShape.fromName(prefs[KEY_DOT_SHAPE]),
                scrollerAlignment = ScrollerAlignment.fromName(prefs[KEY_SCROLLER_ALIGN]),
                doubleTapAction = DoubleTapAction.fromName(prefs[KEY_DOUBLE_TAP_ACTION]),
                swipeLeftAction = SwipeGestureAction.fromName(prefs[KEY_SWIPE_LEFT_ACTION]),
                swipeRightAction = SwipeGestureAction.fromName(prefs[KEY_SWIPE_RIGHT_ACTION]),
                defaultSearchProvider = WebSearchProvider.fromName(prefs[KEY_SEARCH_PROVIDER]),
                is24HourClock = prefs[KEY_IS_24_HOUR] ?: true,
                showScreenTimeGlance = prefs[KEY_SHOW_SCREEN_TIME] ?: true,
                showBatteryDotBar = prefs[KEY_SHOW_BATTERY_BAR] ?: true,
                showScratchpad = prefs[KEY_SHOW_SCRATCHPAD] ?: true,
                scratchpadNote = prefs[KEY_SCRATCHPAD_NOTE] ?: "TAP TO WRITE SCRATCHPAD NOTE_",
                hapticsEnabled = prefs[KEY_HAPTICS] ?: true,
                agslShaderEnabled = prefs[KEY_AGSL_SHADER] ?: true,
                enableCrtScanlines = prefs[KEY_CRT_SCANLINES] ?: false,
                autoFocusSearch = prefs[KEY_AUTO_FOCUS_SEARCH] ?: false,
                maxFavoritesCount = prefs[KEY_MAX_FAVORITES] ?: 8,
                mindfulPauseSeconds = prefs[KEY_MINDFUL_PAUSE] ?: 0,
                mindfulAppPackages = prefs[KEY_MINDFUL_APPS] ?: emptySet(),
                favoritePackageNames = prefs[KEY_FAVORITES] ?: emptySet()
            )
        }

    suspend fun setAccentColor(accent: AccentColor) {
        dataStore.edit { it[KEY_ACCENT_COLOR] = accent.name }
    }

    suspend fun setCustomAccentHex(hex: String) {
        dataStore.edit { it[KEY_CUSTOM_HEX] = hex }
    }

    suspend fun setDotDensity(density: DotDensity) {
        dataStore.edit { it[KEY_DOT_DENSITY] = density.name }
    }

    suspend fun setDotShape(shape: DotShape) {
        dataStore.edit { it[KEY_DOT_SHAPE] = shape.name }
    }

    suspend fun setScrollerAlignment(alignment: ScrollerAlignment) {
        dataStore.edit { it[KEY_SCROLLER_ALIGN] = alignment.name }
    }

    suspend fun setDoubleTapAction(action: DoubleTapAction) {
        dataStore.edit { it[KEY_DOUBLE_TAP_ACTION] = action.name }
    }

    suspend fun setSwipeLeftAction(action: SwipeGestureAction) {
        dataStore.edit { it[KEY_SWIPE_LEFT_ACTION] = action.name }
    }

    suspend fun setSwipeRightAction(action: SwipeGestureAction) {
        dataStore.edit { it[KEY_SWIPE_RIGHT_ACTION] = action.name }
    }

    suspend fun setSearchProvider(provider: WebSearchProvider) {
        dataStore.edit { it[KEY_SEARCH_PROVIDER] = provider.name }
    }

    suspend fun set24HourClock(is24Hour: Boolean) {
        dataStore.edit { it[KEY_IS_24_HOUR] = is24Hour }
    }

    suspend fun setShowScreenTimeGlance(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_SCREEN_TIME] = show }
    }

    suspend fun setShowBatteryDotBar(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_BATTERY_BAR] = show }
    }

    suspend fun setShowScratchpad(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_SCRATCHPAD] = show }
    }

    suspend fun setScratchpadNote(note: String) {
        dataStore.edit { it[KEY_SCRATCHPAD_NOTE] = note }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_HAPTICS] = enabled }
    }

    suspend fun setAgslShaderEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_AGSL_SHADER] = enabled }
    }

    suspend fun setCrtScanlinesEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_CRT_SCANLINES] = enabled }
    }

    suspend fun setAutoFocusSearch(enabled: Boolean) {
        dataStore.edit { it[KEY_AUTO_FOCUS_SEARCH] = enabled }
    }

    suspend fun setMaxFavoritesCount(count: Int) {
        dataStore.edit { it[KEY_MAX_FAVORITES] = count.coerceIn(1, 15) }
    }

    suspend fun setMindfulPauseSeconds(seconds: Int) {
        dataStore.edit { it[KEY_MINDFUL_PAUSE] = seconds.coerceIn(0, 15) }
    }

    suspend fun toggleMindfulApp(packageName: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_MINDFUL_APPS]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(packageName)) current.remove(packageName) else current.add(packageName)
            prefs[KEY_MINDFUL_APPS] = current
        }
    }

    suspend fun toggleFavorite(packageName: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITES]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(packageName)) {
                current.remove(packageName)
            } else {
                current.add(packageName)
            }
            prefs[KEY_FAVORITES] = current
        }
    }

    suspend fun removeFavorite(packageName: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITES]?.toMutableSet() ?: mutableSetOf()
            current.remove(packageName)
            prefs[KEY_FAVORITES] = current
        }
    }
}
