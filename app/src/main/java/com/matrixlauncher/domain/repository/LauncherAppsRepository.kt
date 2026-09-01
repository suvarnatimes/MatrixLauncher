package com.matrixlauncher.domain.repository

import android.graphics.Rect
import android.os.Bundle
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.AppShortcutModel
import com.matrixlauncher.domain.model.BatteryInfo
import com.matrixlauncher.domain.model.CalendarEventInfo
import com.matrixlauncher.domain.model.PackageChangeEvent
import com.matrixlauncher.domain.model.ScreenTimeStats
import com.matrixlauncher.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface LauncherAppsRepository {
    /**
     * Query all launchable user apps across personal and work profiles.
     */
    suspend fun getInstalledApps(): List<AppModel>

    /**
     * Stream real-time package install, update, and uninstall events.
     */
    fun observePackageChanges(): Flow<PackageChangeEvent>

    /**
     * Launch an application with multi-user awareness.
     */
    fun launchApp(app: AppModel, sourceBounds: Rect? = null, opts: Bundle? = null): Result<Unit>

    /**
     * Retrieve Android dynamic and pinned shortcuts for an application.
     */
    suspend fun getShortcutsForApp(app: AppModel): List<AppShortcutModel>

    /**
     * Start a specific app shortcut.
     */
    fun startShortcut(shortcut: AppShortcutModel): Result<Unit>

    /**
     * Open system App Info screen for a package.
     */
    fun openAppInfo(app: AppModel)

    /**
     * Request system uninstallation of an application.
     */
    fun requestUninstall(app: AppModel)

    /**
     * Stream real-time battery status safely.
     */
    fun observeBatteryInfo(): Flow<BatteryInfo>

    /**
     * Stream daily screen time statistics.
     */
    fun observeScreenTimeStats(): Flow<ScreenTimeStats>

    /**
     * Stream weather updates.
     */
    fun observeWeatherInfo(): Flow<WeatherInfo>

    /**
     * Stream upcoming calendar event safely with permission checks.
     */
    fun observeUpcomingCalendarEvent(): Flow<CalendarEventInfo>

    /**
     * Check if Usage Stats permission is granted.
     */
    fun hasUsageStatsPermission(): Boolean

    /**
     * Check if MatrixLauncher is currently the default home launcher.
     */
    fun isDefaultLauncher(): Boolean

    /**
     * Open Android Home Settings or RoleManager dialog to set default launcher.
     */
    fun openDefaultLauncherSettings()

    /**
     * Expand the Android system notification shade.
     */
    fun expandNotificationShade()

    /**
     * Toggle device flashlight / torch.
     */
    fun toggleTorch(): Boolean

    /**
     * Launch camera capture intent.
     */
    fun launchCamera()

    /**
     * Launch default calendar app.
     */
    fun launchCalendar()

    /**
     * Launch browser search URL.
     */
    fun launchWebSearch(url: String)

    /**
     * Save an uploaded custom PNG/JPEG icon to internal storage and return its file URI string.
     */
    suspend fun saveCustomIconImage(packageName: String, inputStream: InputStream): String
}
