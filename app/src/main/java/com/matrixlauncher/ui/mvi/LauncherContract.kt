package com.matrixlauncher.ui.mvi

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.AppShortcutModel
import com.matrixlauncher.domain.model.BatteryInfo
import com.matrixlauncher.domain.model.CalendarEventInfo
import com.matrixlauncher.domain.model.DotDensity
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.HomeWidgetType
import com.matrixlauncher.domain.model.IconStyle
import com.matrixlauncher.domain.model.LauncherSettings
import com.matrixlauncher.domain.model.PlacedWidget
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.ScreenTimeStats
import com.matrixlauncher.domain.model.WeatherInfo
import com.matrixlauncher.domain.model.WebSearchProvider
import com.matrixlauncher.ui.common.SystemSettingShortcut

enum class LauncherScreen {
    HOME,
    DRAWER,
    SETTINGS,
    ICON_STUDIO
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
    val isDefaultLauncher: Boolean = false,
    val allApps: List<AppModel> = emptyList(),
    val recentApps: List<AppModel> = emptyList(),
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

    // Gestures Execution & Configuration
    data class PerformGestureAction(val actionString: String) : LauncherIntent
    data class UpdateGestureAction(val gestureKey: String, val actionString: String) : LauncherIntent

    // Free-form Drag & Drop Widgets Placement
    data class UpdatePlacedWidgets(val widgets: List<PlacedWidget>) : LauncherIntent
    data class UpdateCustomUserName(val name: String) : LauncherIntent
    data class SetNameStyleIndex(val index: Int) : LauncherIntent
    data class SetCrossStyleIndex(val index: Int) : LauncherIntent
    data class SetClockStyleIndex(val index: Int) : LauncherIntent

    // Scales for All Elements
    data class SetCrossSizeScale(val scale: Float) : LauncherIntent
    data class SetNameSizeScale(val scale: Float) : LauncherIntent
    data class SetTimeSizeScale(val scale: Float) : LauncherIntent
    data class SetDateSizeScale(val scale: Float) : LauncherIntent
    data class SetBatterySizeScale(val scale: Float) : LauncherIntent

    // Bible Verse
    data object CycleBibleVerse : LauncherIntent
    data class SetCustomBibleVerse(val verse: String) : LauncherIntent

    data class ToggleBatterySaver(val enabled: Boolean) : LauncherIntent

    // Icon Customization Studio
    data class UpdateIconStyle(val style: IconStyle) : LauncherIntent
    data class UpdateAppIcon(
        val packageName: String,
        val iconUri: String?,
        val colorHex: String?,
        val glyphName: String?,
        val shape: String?
    ) : LauncherIntent
    data class UploadAppIconImage(val packageName: String, val uri: Uri) : LauncherIntent
    data class ResetAppIcon(val packageName: String) : LauncherIntent

    // Theme & Controls
    data class UpdateAccentColor(val color: AccentColor) : LauncherIntent
    data class UpdateCustomAccentHex(val hex: String) : LauncherIntent
    data class UpdateDotDensity(val density: DotDensity) : LauncherIntent
    data class UpdateDotShape(val shape: DotShape) : LauncherIntent
    data class UpdateScrollerAlignment(val alignment: ScrollerAlignment) : LauncherIntent
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
