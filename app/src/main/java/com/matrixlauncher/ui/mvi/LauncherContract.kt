package com.matrixlauncher.ui.mvi

import androidx.compose.runtime.Immutable
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.AppShortcutModel
import com.matrixlauncher.domain.model.BatteryInfo
import com.matrixlauncher.domain.model.CalendarEventInfo
import com.matrixlauncher.domain.model.DotDensity
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.DoubleTapAction
import com.matrixlauncher.domain.model.LauncherSettings
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.ScreenTimeStats
import com.matrixlauncher.domain.model.SwipeGestureAction
import com.matrixlauncher.domain.model.WeatherInfo
import com.matrixlauncher.domain.model.WebSearchProvider
import com.matrixlauncher.ui.common.SystemSettingShortcut

enum class LauncherScreen {
    HOME,
    DRAWER,
    SETTINGS
}

enum class HapticFeedbackType {
    TICK,
    CLICK,
    HEAVY_CLICK,
    DOUBLE_CLICK
}

@Immutable
data class LauncherUiState(
    val isLoading: Boolean = true,
    val allApps: List<AppModel> = emptyList(),
    val hiddenApps: List<AppModel> = emptyList(),
    val pinnedFavorites: List<AppModel> = emptyList(),
    val searchQuery: String = "",
    val filteredApps: List<AppModel> = emptyList(),
    val matchedShortcuts: List<SystemSettingShortcut> = emptyList(),
    val calculatedResult: String? = null,
    val batteryInfo: BatteryInfo = BatteryInfo(),
    val screenTimeStats: ScreenTimeStats = ScreenTimeStats(),
    val weatherInfo: WeatherInfo = WeatherInfo(),
    val calendarEvent: CalendarEventInfo = CalendarEventInfo(),
    val settings: LauncherSettings = LauncherSettings(),
    val selectedAppForMenu: AppModel? = null,
    val selectedAppShortcuts: List<AppShortcutModel> = emptyList(),
    val selectedAppForRename: AppModel? = null,
    val mindfulAppPendingLaunch: AppModel? = null,
    val mindfulSecondsRemaining: Int = 0,
    val currentScreen: LauncherScreen = LauncherScreen.HOME,
    val errorMessage: String? = null
)

sealed interface LauncherIntent {
    data object RefreshApps : LauncherIntent
    data class SearchQueryChanged(val query: String) : LauncherIntent
    data class LaunchApp(val app: AppModel) : LauncherIntent
    data class LaunchAppShortcut(val shortcut: AppShortcutModel) : LauncherIntent
    data class ToggleFavorite(val packageName: String) : LauncherIntent
    data class SetCustomLabel(val packageName: String, val label: String?) : LauncherIntent
    data class SetAppHidden(val packageName: String, val isHidden: Boolean) : LauncherIntent
    data class OpenAppInfo(val app: AppModel) : LauncherIntent
    data class UninstallApp(val app: AppModel) : LauncherIntent
    data class OpenContextMenu(val app: AppModel?) : LauncherIntent
    data class OpenRenameDialog(val app: AppModel?) : LauncherIntent
    data class NavigateTo(val screen: LauncherScreen) : LauncherIntent
    data object ExpandNotificationShade : LauncherIntent
    data object OpenDefaultLauncherSettings : LauncherIntent
    data object OpenCalendar : LauncherIntent

    // Enhanced Customizations
    data class UpdateAccentColor(val color: AccentColor) : LauncherIntent
    data class UpdateCustomAccentHex(val hex: String) : LauncherIntent
    data class UpdateDotDensity(val density: DotDensity) : LauncherIntent
    data class UpdateDotShape(val shape: DotShape) : LauncherIntent
    data class UpdateScrollerAlignment(val alignment: ScrollerAlignment) : LauncherIntent
    data class UpdateDoubleTapAction(val action: DoubleTapAction) : LauncherIntent
    data class UpdateSwipeLeftAction(val action: SwipeGestureAction) : LauncherIntent
    data class UpdateSwipeRightAction(val action: SwipeGestureAction) : LauncherIntent
    data class UpdateSearchProvider(val provider: WebSearchProvider) : LauncherIntent
    data class ToggleTimeFormat(val is24Hour: Boolean) : LauncherIntent
    data class ToggleScreenTime(val show: Boolean) : LauncherIntent
    data class ToggleBatteryBar(val show: Boolean) : LauncherIntent
    data class ToggleScratchpad(val show: Boolean) : LauncherIntent
    data class UpdateScratchpadNote(val note: String) : LauncherIntent
    data class ToggleHaptics(val enabled: Boolean) : LauncherIntent
    data class ToggleShader(val enabled: Boolean) : LauncherIntent
    data class ToggleCrtScanlines(val enabled: Boolean) : LauncherIntent
    data class ToggleAutoFocusSearch(val enabled: Boolean) : LauncherIntent
    data class UpdateMaxFavorites(val count: Int) : LauncherIntent
    data class UpdateMindfulPause(val seconds: Int) : LauncherIntent
    data class ToggleMindfulApp(val packageName: String) : LauncherIntent

    // Power Actions
    data object PerformDoubleTapAction : LauncherIntent
    data class PerformSwipeAction(val action: SwipeGestureAction) : LauncherIntent
    data class LaunchWebSearch(val query: String, val provider: WebSearchProvider) : LauncherIntent
    data class LaunchShortcut(val shortcut: SystemSettingShortcut) : LauncherIntent
    data object CancelMindfulLaunch : LauncherIntent
    data object ConfirmMindfulLaunch : LauncherIntent
    data class ImportConfig(val json: String) : LauncherIntent
}

sealed interface LauncherEffect {
    data class ShowToast(val message: String) : LauncherEffect
    data class PerformHaptic(val type: HapticFeedbackType) : LauncherEffect
}
