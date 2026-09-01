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
import com.matrixlauncher.domain.model.HomeWidgetType
import com.matrixlauncher.domain.model.IconStyle
import com.matrixlauncher.domain.model.LauncherSettings
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.SwipeGestureAction
import com.matrixlauncher.domain.model.WebSearchProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "matrix_launcher_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferencesKeys {
        val KEY_ACCENT_COLOR = stringPreferencesKey("accent_color")
        val KEY_CUSTOM_ACCENT_HEX = stringPreferencesKey("custom_accent_hex")
        val KEY_DOT_DENSITY = stringPreferencesKey("dot_density")
        val KEY_DOT_SHAPE = stringPreferencesKey("dot_shape")
        val KEY_ICON_STYLE = stringPreferencesKey("icon_style")
        val KEY_ENABLED_WIDGETS = stringSetPreferencesKey("enabled_widgets")
        val KEY_SCROLLER_ALIGNMENT = stringPreferencesKey("scroller_alignment")
        val KEY_DOUBLE_TAP_ACTION = stringPreferencesKey("double_tap_action")
        val KEY_SWIPE_LEFT_ACTION = stringPreferencesKey("swipe_left_action")
        val KEY_SWIPE_RIGHT_ACTION = stringPreferencesKey("swipe_right_action")
        val KEY_SEARCH_PROVIDER = stringPreferencesKey("search_provider")
        val KEY_24_HOUR_CLOCK = booleanPreferencesKey("is_24_hour_clock")
        val KEY_SCREEN_TIME_GLANCE = booleanPreferencesKey("show_screen_time")
        val KEY_BATTERY_DOT_BAR = booleanPreferencesKey("show_battery_bar")
        val KEY_SHOW_SCRATCHPAD = booleanPreferencesKey("show_scratchpad")
        val KEY_SCRATCHPAD_NOTE = stringPreferencesKey("scratchpad_note")
        val KEY_HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val KEY_SHADER_ENABLED = booleanPreferencesKey("shader_enabled")
        val KEY_CRT_SCANLINES = booleanPreferencesKey("crt_scanlines")
        val KEY_AUTO_FOCUS_SEARCH = booleanPreferencesKey("auto_focus_search")
        val KEY_MAX_FAVORITES = intPreferencesKey("max_favorites_count")
        val KEY_MINDFUL_PAUSE = intPreferencesKey("mindful_pause_seconds")
        val KEY_MINDFUL_APPS = stringSetPreferencesKey("mindful_app_packages")
        val KEY_FAVORITE_APPS = stringSetPreferencesKey("favorite_apps")
    }

    val launcherSettingsFlow: Flow<LauncherSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val accentName = prefs[PreferencesKeys.KEY_ACCENT_COLOR] ?: AccentColor.CRIMSON.name
            val accent = try { AccentColor.valueOf(accentName) } catch (e: Exception) { AccentColor.CRIMSON }
            val customHex = prefs[PreferencesKeys.KEY_CUSTOM_ACCENT_HEX] ?: "#FF2E2E"

            val densityName = prefs[PreferencesKeys.KEY_DOT_DENSITY] ?: DotDensity.STANDARD.name
            val density = try { DotDensity.valueOf(densityName) } catch (e: Exception) { DotDensity.STANDARD }

            val shapeName = prefs[PreferencesKeys.KEY_DOT_SHAPE] ?: DotShape.CIRCLE.name
            val shape = try { DotShape.valueOf(shapeName) } catch (e: Exception) { DotShape.CIRCLE }

            val iconStyleName = prefs[PreferencesKeys.KEY_ICON_STYLE] ?: IconStyle.DOT_MATRIX_STOCK.name
            val iconStyle = try { IconStyle.valueOf(iconStyleName) } catch (e: Exception) { IconStyle.DOT_MATRIX_STOCK }

            val enabledWidgetsSet = prefs[PreferencesKeys.KEY_ENABLED_WIDGETS]
            val enabledWidgets = if (enabledWidgetsSet != null) {
                enabledWidgetsSet.mapNotNull { name ->
                    try { HomeWidgetType.valueOf(name) } catch (e: Exception) { null }
                }
            } else {
                listOf(HomeWidgetType.CLOCK, HomeWidgetType.WEATHER, HomeWidgetType.TELEMETRY, HomeWidgetType.SCRATCHPAD)
            }

            val scrollerAlignName = prefs[PreferencesKeys.KEY_SCROLLER_ALIGNMENT] ?: ScrollerAlignment.RIGHT.name
            val scrollerAlign = try { ScrollerAlignment.valueOf(scrollerAlignName) } catch (e: Exception) { ScrollerAlignment.RIGHT }

            val doubleTapName = prefs[PreferencesKeys.KEY_DOUBLE_TAP_ACTION] ?: DoubleTapAction.TOGGLE_TORCH.name
            val doubleTap = try { DoubleTapAction.valueOf(doubleTapName) } catch (e: Exception) { DoubleTapAction.TOGGLE_TORCH }

            val swipeLeftName = prefs[PreferencesKeys.KEY_SWIPE_LEFT_ACTION] ?: SwipeGestureAction.NONE.name
            val swipeLeft = try { SwipeGestureAction.valueOf(swipeLeftName) } catch (e: Exception) { SwipeGestureAction.NONE }

            val swipeRightName = prefs[PreferencesKeys.KEY_SWIPE_RIGHT_ACTION] ?: SwipeGestureAction.NONE.name
            val swipeRight = try { SwipeGestureAction.valueOf(swipeRightName) } catch (e: Exception) { SwipeGestureAction.NONE }

            val providerName = prefs[PreferencesKeys.KEY_SEARCH_PROVIDER] ?: WebSearchProvider.DUCK_DUCK_GO.name
            val provider = try { WebSearchProvider.valueOf(providerName) } catch (e: Exception) { WebSearchProvider.DUCK_DUCK_GO }

            LauncherSettings(
                accentColor = accent,
                customAccentHex = customHex,
                dotDensity = density,
                dotShape = shape,
                iconStyle = iconStyle,
                enabledWidgets = enabledWidgets,
                scrollerAlignment = scrollerAlign,
                doubleTapAction = doubleTap,
                swipeLeftAction = swipeLeft,
                swipeRightAction = swipeRight,
                defaultSearchProvider = provider,
                is24HourClock = prefs[PreferencesKeys.KEY_24_HOUR_CLOCK] ?: true,
                showScreenTimeGlance = prefs[PreferencesKeys.KEY_SCREEN_TIME_GLANCE] ?: true,
                showBatteryDotBar = prefs[PreferencesKeys.KEY_BATTERY_DOT_BAR] ?: true,
                showScratchpad = prefs[PreferencesKeys.KEY_SHOW_SCRATCHPAD] ?: true,
                scratchpadNote = prefs[PreferencesKeys.KEY_SCRATCHPAD_NOTE] ?: "TAP TO WRITE NOTE",
                hapticsEnabled = prefs[PreferencesKeys.KEY_HAPTICS_ENABLED] ?: true,
                agslShaderEnabled = prefs[PreferencesKeys.KEY_SHADER_ENABLED] ?: true,
                enableCrtScanlines = prefs[PreferencesKeys.KEY_CRT_SCANLINES] ?: true,
                autoFocusSearch = prefs[PreferencesKeys.KEY_AUTO_FOCUS_SEARCH] ?: false,
                maxFavoritesCount = prefs[PreferencesKeys.KEY_MAX_FAVORITES] ?: 8,
                mindfulPauseSeconds = prefs[PreferencesKeys.KEY_MINDFUL_PAUSE] ?: 0,
                mindfulAppPackages = prefs[PreferencesKeys.KEY_MINDFUL_APPS] ?: emptySet(),
                favoritePackageNames = prefs[PreferencesKeys.KEY_FAVORITE_APPS] ?: emptySet()
            )
        }

    suspend fun setAccentColor(accent: AccentColor) = edit { it[PreferencesKeys.KEY_ACCENT_COLOR] = accent.name }
    suspend fun setCustomAccentHex(hex: String) = edit { it[PreferencesKeys.KEY_CUSTOM_ACCENT_HEX] = hex }
    suspend fun setDotDensity(density: DotDensity) = edit { it[PreferencesKeys.KEY_DOT_DENSITY] = density.name }
    suspend fun setDotShape(shape: DotShape) = edit { it[PreferencesKeys.KEY_DOT_SHAPE] = shape.name }
    suspend fun setIconStyle(style: IconStyle) = edit { it[PreferencesKeys.KEY_ICON_STYLE] = style.name }
    suspend fun setEnabledWidgets(widgets: List<HomeWidgetType>) = edit { it[PreferencesKeys.KEY_ENABLED_WIDGETS] = widgets.map { w -> w.name }.toSet() }
    suspend fun setScrollerAlignment(align: ScrollerAlignment) = edit { it[PreferencesKeys.KEY_SCROLLER_ALIGNMENT] = align.name }
    suspend fun setDoubleTapAction(action: DoubleTapAction) = edit { it[PreferencesKeys.KEY_DOUBLE_TAP_ACTION] = action.name }
    suspend fun setSwipeLeftAction(action: SwipeGestureAction) = edit { it[PreferencesKeys.KEY_SWIPE_LEFT_ACTION] = action.name }
    suspend fun setSwipeRightAction(action: SwipeGestureAction) = edit { it[PreferencesKeys.KEY_SWIPE_RIGHT_ACTION] = action.name }
    suspend fun setSearchProvider(provider: WebSearchProvider) = edit { it[PreferencesKeys.KEY_SEARCH_PROVIDER] = provider.name }
    suspend fun set24HourClock(is24Hour: Boolean) = edit { it[PreferencesKeys.KEY_24_HOUR_CLOCK] = is24Hour }
    suspend fun setShowScreenTimeGlance(show: Boolean) = edit { it[PreferencesKeys.KEY_SCREEN_TIME_GLANCE] = show }
    suspend fun setShowBatteryDotBar(show: Boolean) = edit { it[PreferencesKeys.KEY_BATTERY_DOT_BAR] = show }
    suspend fun setShowScratchpad(show: Boolean) = edit { it[PreferencesKeys.KEY_SHOW_SCRATCHPAD] = show }
    suspend fun setScratchpadNote(note: String) = edit { it[PreferencesKeys.KEY_SCRATCHPAD_NOTE] = note }
    suspend fun setHapticsEnabled(enabled: Boolean) = edit { it[PreferencesKeys.KEY_HAPTICS_ENABLED] = enabled }
    suspend fun setAgslShaderEnabled(enabled: Boolean) = edit { it[PreferencesKeys.KEY_SHADER_ENABLED] = enabled }
    suspend fun setCrtScanlinesEnabled(enabled: Boolean) = edit { it[PreferencesKeys.KEY_CRT_SCANLINES] = enabled }
    suspend fun setAutoFocusSearch(enabled: Boolean) = edit { it[PreferencesKeys.KEY_AUTO_FOCUS_SEARCH] = enabled }
    suspend fun setMaxFavoritesCount(count: Int) = edit { it[PreferencesKeys.KEY_MAX_FAVORITES] = count.coerceIn(1, 15) }
    suspend fun setMindfulPauseSeconds(seconds: Int) = edit { it[PreferencesKeys.KEY_MINDFUL_PAUSE] = seconds.coerceIn(0, 30) }

    suspend fun toggleMindfulApp(packageName: String) = edit { prefs ->
        val current = prefs[PreferencesKeys.KEY_MINDFUL_APPS]?.toMutableSet() ?: mutableSetOf()
        if (current.contains(packageName)) current.remove(packageName) else current.add(packageName)
        prefs[PreferencesKeys.KEY_MINDFUL_APPS] = current
    }

    suspend fun toggleFavorite(packageName: String) = edit { prefs ->
        val current = prefs[PreferencesKeys.KEY_FAVORITE_APPS]?.toMutableSet() ?: mutableSetOf()
        if (current.contains(packageName)) current.remove(packageName) else current.add(packageName)
        prefs[PreferencesKeys.KEY_FAVORITE_APPS] = current
    }

    suspend fun removeFavorite(packageName: String) = edit { prefs ->
        val current = prefs[PreferencesKeys.KEY_FAVORITE_APPS]?.toMutableSet() ?: return@edit
        current.remove(packageName)
        prefs[PreferencesKeys.KEY_FAVORITE_APPS] = current
    }

    private suspend fun edit(action: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        context.dataStore.edit { prefs ->
            action(prefs)
        }
    }
}
