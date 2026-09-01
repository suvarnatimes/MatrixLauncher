package com.matrixlauncher.data.repository

import com.matrixlauncher.data.local.datastore.PreferencesManager
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.DotDensity
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.HomeWidgetType
import com.matrixlauncher.domain.model.IconStyle
import com.matrixlauncher.domain.model.LauncherSettings
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.WebSearchProvider
import com.matrixlauncher.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : PreferencesRepository {

    override val settingsFlow: Flow<LauncherSettings> = preferencesManager.launcherSettingsFlow

    override suspend fun setAccentColor(accent: AccentColor) = preferencesManager.setAccentColor(accent)
    override suspend fun setCustomAccentHex(hex: String) = preferencesManager.setCustomAccentHex(hex)
    override suspend fun setDotDensity(density: DotDensity) = preferencesManager.setDotDensity(density)
    override suspend fun setDotShape(shape: DotShape) = preferencesManager.setDotShape(shape)
    override suspend fun setIconStyle(style: IconStyle) = preferencesManager.setIconStyle(style)
    override suspend fun setEnabledWidgets(widgets: List<HomeWidgetType>) = preferencesManager.setEnabledWidgets(widgets)
    override suspend fun setScrollerAlignment(alignment: ScrollerAlignment) = preferencesManager.setScrollerAlignment(alignment)

    override suspend fun setSwipeDownAction(action: String) = preferencesManager.setSwipeDownAction(action)
    override suspend fun setSwipeUpAction(action: String) = preferencesManager.setSwipeUpAction(action)
    override suspend fun setSwipeLeftAction(action: String) = preferencesManager.setSwipeLeftAction(action)
    override suspend fun setSwipeRightAction(action: String) = preferencesManager.setSwipeRightAction(action)
    override suspend fun setDoubleTapAction(action: String) = preferencesManager.setDoubleTapAction(action)
    override suspend fun setPinchInAction(action: String) = preferencesManager.setPinchInAction(action)
    override suspend fun setPinchOutAction(action: String) = preferencesManager.setPinchOutAction(action)
    override suspend fun setTwoFingerSwipeDownAction(action: String) = preferencesManager.setTwoFingerSwipeDownAction(action)
    override suspend fun setTwoFingerSwipeUpAction(action: String) = preferencesManager.setTwoFingerSwipeUpAction(action)

    override suspend fun setSearchProvider(provider: WebSearchProvider) = preferencesManager.setSearchProvider(provider)
    override suspend fun set24HourClock(is24Hour: Boolean) = preferencesManager.set24HourClock(is24Hour)
    override suspend fun setShowScreenTimeGlance(show: Boolean) = preferencesManager.setShowScreenTimeGlance(show)
    override suspend fun setShowBatteryDotBar(show: Boolean) = preferencesManager.setShowBatteryDotBar(show)
    override suspend fun setShowScratchpad(show: Boolean) = preferencesManager.setShowScratchpad(show)
    override suspend fun setScratchpadNote(note: String) = preferencesManager.setScratchpadNote(note)
    override suspend fun setHapticsEnabled(enabled: Boolean) = preferencesManager.setHapticsEnabled(enabled)
    override suspend fun setAgslShaderEnabled(enabled: Boolean) = preferencesManager.setAgslShaderEnabled(enabled)
    override suspend fun setCrtScanlinesEnabled(enabled: Boolean) = preferencesManager.setCrtScanlinesEnabled(enabled)
    override suspend fun setAutoFocusSearch(enabled: Boolean) = preferencesManager.setAutoFocusSearch(enabled)
    override suspend fun setMaxFavoritesCount(count: Int) = preferencesManager.setMaxFavoritesCount(count)
    override suspend fun setMindfulPauseSeconds(seconds: Int) = preferencesManager.setMindfulPauseSeconds(seconds)
    override suspend fun toggleMindfulApp(packageName: String) = preferencesManager.toggleMindfulApp(packageName)
    override suspend fun toggleFavorite(packageName: String) = preferencesManager.toggleFavorite(packageName)
    override suspend fun removeFavorite(packageName: String) = preferencesManager.removeFavorite(packageName)
}
