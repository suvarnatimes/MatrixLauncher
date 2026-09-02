package com.matrixlauncher.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.DotDensity
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.HomeWidgetType
import com.matrixlauncher.domain.model.IconStyle
import com.matrixlauncher.domain.model.LauncherSettings
import com.matrixlauncher.domain.model.PlacedWidget
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.WebSearchProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
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
        val KEY_PLACED_WIDGETS_JSON = stringPreferencesKey("placed_widgets_json")
        val KEY_SCROLLER_ALIGNMENT = stringPreferencesKey("scroller_alignment")

        // Gestures
        val KEY_SWIPE_DOWN_ACTION = stringPreferencesKey("swipe_down_action")
        val KEY_SWIPE_UP_ACTION = stringPreferencesKey("swipe_up_action")
        val KEY_SWIPE_LEFT_ACTION = stringPreferencesKey("swipe_left_action")
        val KEY_SWIPE_RIGHT_ACTION = stringPreferencesKey("swipe_right_action")
        val KEY_DOUBLE_TAP_ACTION = stringPreferencesKey("double_tap_action")
        val KEY_PINCH_IN_ACTION = stringPreferencesKey("pinch_in_action")
        val KEY_PINCH_OUT_ACTION = stringPreferencesKey("pinch_out_action")
        val KEY_TWO_FINGER_SWIPE_DOWN = stringPreferencesKey("two_finger_swipe_down")
        val KEY_TWO_FINGER_SWIPE_UP = stringPreferencesKey("two_finger_swipe_up")

        // Custom Name & Widget Style Indices
        val KEY_CUSTOM_USER_NAME = stringPreferencesKey("custom_user_name")
        val KEY_NAME_STYLE_INDEX = intPreferencesKey("name_style_index")
        val KEY_CROSS_STYLE_INDEX = intPreferencesKey("cross_style_index")
        val KEY_CLOCK_STYLE_INDEX = intPreferencesKey("clock_style_index")

        // Individual Scales
        val KEY_CROSS_SIZE_SCALE = floatPreferencesKey("cross_size_scale")
        val KEY_NAME_SIZE_SCALE = floatPreferencesKey("name_size_scale")
        val KEY_TIME_SIZE_SCALE = floatPreferencesKey("time_size_scale")
        val KEY_DATE_SIZE_SCALE = floatPreferencesKey("date_size_scale")
        val KEY_BATTERY_SIZE_SCALE = floatPreferencesKey("battery_size_scale")

        // Bible Verse
        val KEY_BIBLE_VERSE_INDEX = intPreferencesKey("bible_verse_index")
        val KEY_CUSTOM_BIBLE_VERSE = stringPreferencesKey("custom_bible_verse")

        val KEY_BATTERY_SAVER = booleanPreferencesKey("battery_saver_enabled")
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
            val accentName = prefs[PreferencesKeys.KEY_ACCENT_COLOR] ?: AccentColor.EMERALD.name
            val accent = try { AccentColor.valueOf(accentName) } catch (e: Exception) { AccentColor.EMERALD }
            val customHex = prefs[PreferencesKeys.KEY_CUSTOM_ACCENT_HEX] ?: "#00E676"

            val densityName = prefs[PreferencesKeys.KEY_DOT_DENSITY] ?: DotDensity.STANDARD.name
            val density = try { DotDensity.valueOf(densityName) } catch (e: Exception) { DotDensity.STANDARD }

            val shapeName = prefs[PreferencesKeys.KEY_DOT_SHAPE] ?: DotShape.CIRCLE.name
            val shape = try { DotShape.valueOf(shapeName) } catch (e: Exception) { DotShape.CIRCLE }

            val iconStyleName = prefs[PreferencesKeys.KEY_ICON_STYLE] ?: IconStyle.DOT_MATRIX_STOCK.name
            val iconStyle = try { IconStyle.valueOf(iconStyleName) } catch (e: Exception) { IconStyle.DOT_MATRIX_STOCK }

            val scrollerAlignName = prefs[PreferencesKeys.KEY_SCROLLER_ALIGNMENT] ?: ScrollerAlignment.RIGHT.name
            val scrollerAlign = try { ScrollerAlignment.valueOf(scrollerAlignName) } catch (e: Exception) { ScrollerAlignment.RIGHT }

            val providerName = prefs[PreferencesKeys.KEY_SEARCH_PROVIDER] ?: WebSearchProvider.DUCK_DUCK_GO.name
            val provider = try { WebSearchProvider.valueOf(providerName) } catch (e: Exception) { WebSearchProvider.DUCK_DUCK_GO }

            val isBatterySaver = prefs[PreferencesKeys.KEY_BATTERY_SAVER] ?: false

            // Placed widgets JSON deserialization
            val placedJson = prefs[PreferencesKeys.KEY_PLACED_WIDGETS_JSON]
            val placedList = if (!placedJson.isNullOrBlank()) {
                try {
                    val array = JSONArray(placedJson)
                    val list = mutableListOf<PlacedWidget>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val typeName = obj.getString("type")
                        val type = try { HomeWidgetType.valueOf(typeName) } catch (e: Exception) { null }
                        if (type != null) {
                            list.add(
                                PlacedWidget(
                                    id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                                    type = type,
                                    xPercent = obj.optDouble("xPercent", 0.5).toFloat(),
                                    yPercent = obj.optDouble("yPercent", 0.5).toFloat(),
                                    styleIndex = obj.optInt("styleIndex", 0)
                                )
                            )
                        }
                    }
                    if (list.isNotEmpty()) list else defaultPlacedWidgets()
                } catch (e: Exception) {
                    defaultPlacedWidgets()
                }
            } else {
                defaultPlacedWidgets()
            }

            val enabledWidgets = placedList.map { it.type }.distinct()

            LauncherSettings(
                accentColor = accent,
                customAccentHex = customHex,
                dotDensity = density,
                dotShape = shape,
                iconStyle = iconStyle,
                placedWidgets = placedList,
                enabledWidgets = enabledWidgets,
                scrollerAlignment = scrollerAlign,
                swipeDownAction = prefs[PreferencesKeys.KEY_SWIPE_DOWN_ACTION] ?: "EXPAND_NOTIFICATIONS",
                swipeUpAction = prefs[PreferencesKeys.KEY_SWIPE_UP_ACTION] ?: "OPEN_DRAWER",
                swipeLeftAction = prefs[PreferencesKeys.KEY_SWIPE_LEFT_ACTION] ?: "NONE",
                swipeRightAction = prefs[PreferencesKeys.KEY_SWIPE_RIGHT_ACTION] ?: "NONE",
                doubleTapAction = prefs[PreferencesKeys.KEY_DOUBLE_TAP_ACTION] ?: "TOGGLE_TORCH",
                pinchInAction = prefs[PreferencesKeys.KEY_PINCH_IN_ACTION] ?: "OPEN_SETTINGS",
                pinchOutAction = prefs[PreferencesKeys.KEY_PINCH_OUT_ACTION] ?: "OPEN_DRAWER",
                twoFingerSwipeDownAction = prefs[PreferencesKeys.KEY_TWO_FINGER_SWIPE_DOWN] ?: "OPEN_SEARCH",
                twoFingerSwipeUpAction = prefs[PreferencesKeys.KEY_TWO_FINGER_SWIPE_UP] ?: "OPEN_DRAWER",
                customUserName = prefs[PreferencesKeys.KEY_CUSTOM_USER_NAME] ?: "MICHEL",
                nameStyleIndex = prefs[PreferencesKeys.KEY_NAME_STYLE_INDEX] ?: 0,
                crossStyleIndex = prefs[PreferencesKeys.KEY_CROSS_STYLE_INDEX] ?: 0,
                clockStyleIndex = prefs[PreferencesKeys.KEY_CLOCK_STYLE_INDEX] ?: 0,
                crossSizeScale = prefs[PreferencesKeys.KEY_CROSS_SIZE_SCALE] ?: 1.35f,
                nameSizeScale = prefs[PreferencesKeys.KEY_NAME_SIZE_SCALE] ?: 1.0f,
                timeSizeScale = prefs[PreferencesKeys.KEY_TIME_SIZE_SCALE] ?: 1.0f,
                dateSizeScale = prefs[PreferencesKeys.KEY_DATE_SIZE_SCALE] ?: 1.0f,
                batterySizeScale = prefs[PreferencesKeys.KEY_BATTERY_SIZE_SCALE] ?: 1.0f,
                bibleVerseIndex = prefs[PreferencesKeys.KEY_BIBLE_VERSE_INDEX] ?: 0,
                customBibleVerse = prefs[PreferencesKeys.KEY_CUSTOM_BIBLE_VERSE] ?: "",
                batterySaverEnabled = isBatterySaver,
                defaultSearchProvider = provider,
                is24HourClock = prefs[PreferencesKeys.KEY_24_HOUR_CLOCK] ?: true,
                showScreenTimeGlance = prefs[PreferencesKeys.KEY_SCREEN_TIME_GLANCE] ?: true,
                showBatteryDotBar = prefs[PreferencesKeys.KEY_BATTERY_DOT_BAR] ?: true,
                showScratchpad = prefs[PreferencesKeys.KEY_SHOW_SCRATCHPAD] ?: true,
                scratchpadNote = prefs[PreferencesKeys.KEY_SCRATCHPAD_NOTE] ?: "TAP TO WRITE NOTE",
                hapticsEnabled = if (isBatterySaver) false else (prefs[PreferencesKeys.KEY_HAPTICS_ENABLED] ?: true),
                agslShaderEnabled = if (isBatterySaver) false else (prefs[PreferencesKeys.KEY_SHADER_ENABLED] ?: true),
                enableCrtScanlines = if (isBatterySaver) false else (prefs[PreferencesKeys.KEY_CRT_SCANLINES] ?: true),
                autoFocusSearch = prefs[PreferencesKeys.KEY_AUTO_FOCUS_SEARCH] ?: false,
                maxFavoritesCount = prefs[PreferencesKeys.KEY_MAX_FAVORITES] ?: 8,
                mindfulPauseSeconds = prefs[PreferencesKeys.KEY_MINDFUL_PAUSE] ?: 0,
                mindfulAppPackages = prefs[PreferencesKeys.KEY_MINDFUL_APPS] ?: emptySet(),
                favoritePackageNames = prefs[PreferencesKeys.KEY_FAVORITE_APPS] ?: emptySet()
            )
        }

    private fun defaultPlacedWidgets(): List<PlacedWidget> = listOf(
        PlacedWidget(
            id = "hero_clock",
            type = HomeWidgetType.COMBINED_HERO,
            xPercent = 0.5f,
            yPercent = 0.14f,
            styleIndex = 0
        ),
        PlacedWidget(
            id = "jesus_cross",
            type = HomeWidgetType.JESUS_CROSS,
            xPercent = 0.5f,
            yPercent = 0.52f,
            styleIndex = 0
        )
    )

    suspend fun setPlacedWidgets(widgets: List<PlacedWidget>) = edit { prefs ->
        val array = JSONArray()
        widgets.forEach { w ->
            val obj = JSONObject().apply {
                put("id", w.id)
                put("type", w.type.name)
                put("xPercent", w.xPercent.toDouble())
                put("yPercent", w.yPercent.toDouble())
                put("styleIndex", w.styleIndex)
            }
            array.put(obj)
        }
        prefs[PreferencesKeys.KEY_PLACED_WIDGETS_JSON] = array.toString()
    }

    suspend fun setAccentColor(accent: AccentColor) = edit { it[PreferencesKeys.KEY_ACCENT_COLOR] = accent.name }
    suspend fun setCustomAccentHex(hex: String) = edit { it[PreferencesKeys.KEY_CUSTOM_ACCENT_HEX] = hex }
    suspend fun setDotDensity(density: DotDensity) = edit { it[PreferencesKeys.KEY_DOT_DENSITY] = density.name }
    suspend fun setDotShape(shape: DotShape) = edit { it[PreferencesKeys.KEY_DOT_SHAPE] = shape.name }
    suspend fun setIconStyle(style: IconStyle) = edit { it[PreferencesKeys.KEY_ICON_STYLE] = style.name }
    suspend fun setScrollerAlignment(align: ScrollerAlignment) = edit { it[PreferencesKeys.KEY_SCROLLER_ALIGNMENT] = align.name }

    suspend fun setSwipeDownAction(action: String) = edit { it[PreferencesKeys.KEY_SWIPE_DOWN_ACTION] = action }
    suspend fun setSwipeUpAction(action: String) = edit { it[PreferencesKeys.KEY_SWIPE_UP_ACTION] = action }
    suspend fun setSwipeLeftAction(action: String) = edit { it[PreferencesKeys.KEY_SWIPE_LEFT_ACTION] = action }
    suspend fun setSwipeRightAction(action: String) = edit { it[PreferencesKeys.KEY_SWIPE_RIGHT_ACTION] = action }
    suspend fun setDoubleTapAction(action: String) = edit { it[PreferencesKeys.KEY_DOUBLE_TAP_ACTION] = action }
    suspend fun setPinchInAction(action: String) = edit { it[PreferencesKeys.KEY_PINCH_IN_ACTION] = action }
    suspend fun setPinchOutAction(action: String) = edit { it[PreferencesKeys.KEY_PINCH_OUT_ACTION] = action }
    suspend fun setTwoFingerSwipeDownAction(action: String) = edit { it[PreferencesKeys.KEY_TWO_FINGER_SWIPE_DOWN] = action }
    suspend fun setTwoFingerSwipeUpAction(action: String) = edit { it[PreferencesKeys.KEY_TWO_FINGER_SWIPE_UP] = action }

    suspend fun setCustomUserName(name: String) = edit { it[PreferencesKeys.KEY_CUSTOM_USER_NAME] = name }
    suspend fun setNameStyleIndex(index: Int) = edit { it[PreferencesKeys.KEY_NAME_STYLE_INDEX] = index }
    suspend fun setCrossStyleIndex(index: Int) = edit { it[PreferencesKeys.KEY_CROSS_STYLE_INDEX] = index }
    suspend fun setClockStyleIndex(index: Int) = edit { it[PreferencesKeys.KEY_CLOCK_STYLE_INDEX] = index }

    // Scales
    suspend fun setCrossSizeScale(scale: Float) = edit { it[PreferencesKeys.KEY_CROSS_SIZE_SCALE] = scale.coerceIn(0.6f, 2.4f) }
    suspend fun setNameSizeScale(scale: Float) = edit { it[PreferencesKeys.KEY_NAME_SIZE_SCALE] = scale.coerceIn(0.6f, 2.4f) }
    suspend fun setTimeSizeScale(scale: Float) = edit { it[PreferencesKeys.KEY_TIME_SIZE_SCALE] = scale.coerceIn(0.6f, 2.4f) }
    suspend fun setDateSizeScale(scale: Float) = edit { it[PreferencesKeys.KEY_DATE_SIZE_SCALE] = scale.coerceIn(0.6f, 2.4f) }
    suspend fun setBatterySizeScale(scale: Float) = edit { it[PreferencesKeys.KEY_BATTERY_SIZE_SCALE] = scale.coerceIn(0.6f, 2.4f) }

    // Bible Verse
    suspend fun setBibleVerseIndex(index: Int) = edit { it[PreferencesKeys.KEY_BIBLE_VERSE_INDEX] = index }
    suspend fun setCustomBibleVerse(verse: String) = edit { it[PreferencesKeys.KEY_CUSTOM_BIBLE_VERSE] = verse }

    suspend fun setBatterySaverEnabled(enabled: Boolean) = edit { it[PreferencesKeys.KEY_BATTERY_SAVER] = enabled }

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
