package com.matrixlauncher.domain.repository

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
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val settingsFlow: Flow<LauncherSettings>
    suspend fun setAccentColor(accent: AccentColor)
    suspend fun setCustomAccentHex(hex: String)
    suspend fun setDotDensity(density: DotDensity)
    suspend fun setDotShape(shape: DotShape)
    suspend fun setIconStyle(style: IconStyle)
    suspend fun setEnabledWidgets(widgets: List<HomeWidgetType>)
    suspend fun setScrollerAlignment(alignment: ScrollerAlignment)
    suspend fun setDoubleTapAction(action: DoubleTapAction)
    suspend fun setSwipeLeftAction(action: SwipeGestureAction)
    suspend fun setSwipeRightAction(action: SwipeGestureAction)
    suspend fun setSearchProvider(provider: WebSearchProvider)
    suspend fun set24HourClock(is24Hour: Boolean)
    suspend fun setShowScreenTimeGlance(show: Boolean)
    suspend fun setShowBatteryDotBar(show: Boolean)
    suspend fun setShowScratchpad(show: Boolean)
    suspend fun setScratchpadNote(note: String)
    suspend fun setHapticsEnabled(enabled: Boolean)
    suspend fun setAgslShaderEnabled(enabled: Boolean)
    suspend fun setCrtScanlinesEnabled(enabled: Boolean)
    suspend fun setAutoFocusSearch(enabled: Boolean)
    suspend fun setMaxFavoritesCount(count: Int)
    suspend fun setMindfulPauseSeconds(seconds: Int)
    suspend fun toggleMindfulApp(packageName: String)
    suspend fun toggleFavorite(packageName: String)
    suspend fun removeFavorite(packageName: String)
}
