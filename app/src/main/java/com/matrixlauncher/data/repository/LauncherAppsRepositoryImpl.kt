package com.matrixlauncher.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.role.RoleManager
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.graphics.Rect
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.os.UserHandle
import android.os.UserManager
import android.provider.CalendarContract
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.AppShortcutModel
import com.matrixlauncher.domain.model.BatteryInfo
import com.matrixlauncher.domain.model.CalendarEventInfo
import com.matrixlauncher.domain.model.PackageChangeEvent
import com.matrixlauncher.domain.model.ScreenTimeStats
import com.matrixlauncher.domain.model.WeatherCondition
import com.matrixlauncher.domain.model.WeatherInfo
import com.matrixlauncher.domain.repository.LauncherAppsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LauncherAppsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LauncherAppsRepository {

    private val launcherApps: LauncherApps? = try {
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
    } catch (e: Throwable) {
        null
    }

    private val userManager: UserManager? = try {
        context.getSystemService(Context.USER_SERVICE) as? UserManager
    } catch (e: Throwable) {
        null
    }

    private val packageManager: PackageManager = context.packageManager
    private val usageStatsManager = try {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
    } catch (e: Throwable) {
        null
    }

    private val batteryManager = try {
        context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
    } catch (e: Throwable) {
        null
    }

    private val cameraManager = try {
        context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    } catch (e: Throwable) {
        null
    }

    private var isTorchOn = false

    override suspend fun getInstalledApps(): List<AppModel> = withContext(Dispatchers.IO) {
        val appList = mutableListOf<AppModel>()
        val currentUserHandle = Process.myUserHandle()
        val userProfiles = try {
            userManager?.userProfiles ?: listOf(currentUserHandle)
        } catch (e: Throwable) {
            listOf(currentUserHandle)
        }

        var loadedViaLauncherApps = false

        if (launcherApps != null) {
            for (profile in userProfiles) {
                val userSerial = try {
                    userManager?.getSerialNumberForUser(profile) ?: 0L
                } catch (e: Throwable) {
                    0L
                }
                val isWorkProfile = profile != currentUserHandle
                val activities: List<LauncherActivityInfo> = try {
                    launcherApps.getActivityList(null, profile)
                } catch (e: Throwable) {
                    emptyList()
                }

                if (activities.isNotEmpty()) {
                    loadedViaLauncherApps = true
                }

                for (activity in activities) {
                    val pkgName = activity.applicationInfo.packageName
                    if (pkgName == context.packageName) {
                        continue
                    }

                    val appModel = AppModel(
                        packageName = pkgName,
                        activityName = activity.componentName.className,
                        label = activity.label?.toString() ?: pkgName,
                        userHandle = profile,
                        userSerial = userSerial,
                        isWorkProfile = isWorkProfile,
                        installTime = try {
                            activity.firstInstallTime
                        } catch (e: Throwable) {
                            0L
                        }
                    )
                    appList.add(appModel)
                }
            }
        }

        // Robust fallback using PackageManager queryIntentActivities
        if (!loadedViaLauncherApps || appList.isEmpty()) {
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos: List<ResolveInfo> = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.queryIntentActivities(
                        mainIntent,
                        PackageManager.ResolveInfoFlags.of(0)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.queryIntentActivities(mainIntent, 0)
                }
            } catch (e: Throwable) {
                emptyList()
            }

            for (resolveInfo in resolveInfos) {
                val pkgName = resolveInfo.activityInfo.packageName
                if (pkgName == context.packageName) continue

                val label = try {
                    resolveInfo.loadLabel(packageManager).toString()
                } catch (e: Throwable) {
                    pkgName
                }

                val appModel = AppModel(
                    packageName = pkgName,
                    activityName = resolveInfo.activityInfo.name,
                    label = label,
                    userHandle = currentUserHandle,
                    userSerial = 0L,
                    isWorkProfile = false
                )
                if (appList.none { it.packageName == pkgName }) {
                    appList.add(appModel)
                }
            }
        }

        appList.sortedBy { it.displayLabel.lowercase() }
    }

    override fun observePackageChanges(): Flow<PackageChangeEvent> = callbackFlow {
        if (launcherApps == null) {
            awaitClose { }
            return@callbackFlow
        }

        val callback = object : LauncherApps.Callback() {
            override fun onPackageAdded(packageName: String, user: UserHandle) {
                trySend(PackageChangeEvent.PackageAdded(packageName, user))
            }

            override fun onPackageRemoved(packageName: String, user: UserHandle) {
                trySend(PackageChangeEvent.PackageRemoved(packageName, user))
            }

            override fun onPackageChanged(packageName: String, user: UserHandle) {
                trySend(PackageChangeEvent.PackageChanged(packageName, user))
            }

            override fun onPackagesAvailable(
                packageNames: Array<String>,
                user: UserHandle,
                replacing: Boolean
            ) {
                trySend(PackageChangeEvent.PackagesAvailable(packageNames, user))
            }

            override fun onPackagesUnavailable(
                packageNames: Array<String>,
                user: UserHandle,
                replacing: Boolean
            ) {
                trySend(PackageChangeEvent.PackagesUnavailable(packageNames, user))
            }
        }

        try {
            val mainHandler = Handler(Looper.getMainLooper())
            launcherApps.registerCallback(callback, mainHandler)
        } catch (e: Throwable) {
            // Ignore if callback registration not permitted
        }

        awaitClose {
            try {
                launcherApps.unregisterCallback(callback)
            } catch (e: Throwable) {
                // Ignore
            }
        }
    }

    override fun launchApp(
        app: AppModel,
        sourceBounds: Rect?,
        opts: Bundle?
    ): Result<Unit> {
        return runCatching {
            val component = ComponentName(app.packageName, app.activityName)
            val user = app.userHandle ?: Process.myUserHandle()
            if (launcherApps != null) {
                try {
                    launcherApps.startMainActivity(component, user, sourceBounds, opts)
                    return@runCatching
                } catch (e: Throwable) {
                    // Fallback to PackageManager launch
                }
            }
            val intent = packageManager.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent, opts)
            }
        }
    }

    override suspend fun getShortcutsForApp(app: AppModel): List<AppShortcutModel> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1 || launcherApps == null) return emptyList()

        return try {
            val query = LauncherApps.ShortcutQuery().apply {
                setPackage(app.packageName)
                setQueryFlags(
                    LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or
                    LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or
                    LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED
                )
            }
            val user = app.userHandle ?: Process.myUserHandle()
            val shortcuts = launcherApps.getShortcuts(query, user) ?: emptyList()

            shortcuts.map { shortcut ->
                AppShortcutModel(
                    id = shortcut.id,
                    packageName = shortcut.`package`,
                    shortLabel = shortcut.shortLabel?.toString() ?: shortcut.id,
                    longLabel = shortcut.longLabel?.toString(),
                    userHandle = user,
                    isDynamic = shortcut.isDynamic,
                    isPinned = shortcut.isPinned
                )
            }
        } catch (e: Throwable) {
            emptyList()
        }
    }

    override fun startShortcut(shortcut: AppShortcutModel): Result<Unit> {
        return runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && launcherApps != null) {
                val user = shortcut.userHandle ?: Process.myUserHandle()
                launcherApps.startShortcut(shortcut.packageName, shortcut.id, null, null, user)
            }
        }
    }

    override fun openAppInfo(app: AppModel) {
        val component = ComponentName(app.packageName, app.activityName)
        val user = app.userHandle ?: Process.myUserHandle()
        try {
            launcherApps?.startAppDetailsActivity(component, user, null, null)
        } catch (e: Throwable) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", app.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
            } catch (ignored: Throwable) {}
        }
    }

    override fun requestUninstall(app: AppModel) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.fromParts("package", app.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (ignored: Throwable) {}
    }

    override fun observeBatteryInfo(): Flow<BatteryInfo> = callbackFlow {
        // First, directly query BatteryManager property safely
        val initialCapacity = try {
            batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: -1
        } catch (e: Throwable) {
            -1
        }
        val isChargingDirect = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                batteryManager?.isCharging == true
            } else false
        } catch (e: Throwable) {
            false
        }

        if (initialCapacity in 0..100) {
            trySend(BatteryInfo(level = initialCapacity, isCharging = isChargingDirect))
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent == null) return
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                val batteryPct = if (level >= 0 && scale > 0) {
                    ((level.toFloat() / scale.toFloat()) * 100).toInt()
                } else {
                    100
                }
                trySend(
                    BatteryInfo(
                        level = batteryPct.coerceIn(0, 100),
                        isCharging = isCharging
                    )
                )
            }
        }

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        var isRegistered = false
        try {
            val registeredIntent = ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_EXPORTED
            )
            isRegistered = true
            if (registeredIntent != null) {
                val level = registeredIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = registeredIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val status = registeredIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                val batteryPct = if (level >= 0 && scale > 0) {
                    ((level.toFloat() / scale.toFloat()) * 100).toInt()
                } else {
                    100
                }
                trySend(
                    BatteryInfo(
                        level = batteryPct.coerceIn(0, 100),
                        isCharging = isCharging
                    )
                )
            }
        } catch (e: Throwable) {
            // If registerReceiver fails, fallback is already emitted via batteryManager
        }

        awaitClose {
            if (isRegistered) {
                try {
                    context.unregisterReceiver(receiver)
                } catch (e: Throwable) {
                    // Ignore
                }
            }
        }
    }

    override fun observeScreenTimeStats(): Flow<ScreenTimeStats> = flow {
        val hasPermission = hasUsageStatsPermission()
        if (!hasPermission || usageStatsManager == null) {
            emit(ScreenTimeStats(totalMillisToday = 0L, hasPermission = false))
            return@flow
        }

        try {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startTime = calendar.timeInMillis
            val endTime = System.currentTimeMillis()

            val usageStats = try {
                usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    startTime,
                    endTime
                )
            } catch (e: Throwable) {
                emptyList()
            }

            val totalTime = usageStats?.sumOf { it.totalTimeInForeground } ?: 0L
            val topApp = usageStats?.maxByOrNull { it.totalTimeInForeground }?.packageName

            emit(
                ScreenTimeStats(
                    totalMillisToday = totalTime,
                    hasPermission = true,
                    topAppPackage = topApp
                )
            )
        } catch (e: Throwable) {
            emit(ScreenTimeStats(totalMillisToday = 0L, hasPermission = false))
        }
    }

    override fun observeWeatherInfo(): Flow<WeatherInfo> = flow {
        emit(
            WeatherInfo(
                temperatureCelsius = 24,
                condition = WeatherCondition.CLEAR,
                location = "LOCAL",
                isAvailable = true
            )
        )
    }

    override fun observeUpcomingCalendarEvent(): Flow<CalendarEventInfo> = flow {
        val hasCalendarPermission = try {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED
        } catch (e: Throwable) {
            false
        }

        if (!hasCalendarPermission) {
            emit(CalendarEventInfo(hasEvent = false))
            return@flow
        }

        try {
            val now = System.currentTimeMillis()
            val endOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.timeInMillis

            val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
            ContentUris.appendId(builder, now)
            ContentUris.appendId(builder, endOfDay)

            val cursor: Cursor? = context.contentResolver.query(
                builder.build(),
                arrayOf(
                    CalendarContract.Instances.TITLE,
                    CalendarContract.Instances.BEGIN,
                    CalendarContract.Instances.ALL_DAY
                ),
                null,
                null,
                "${CalendarContract.Instances.BEGIN} ASC LIMIT 1"
            )

            if (cursor != null && cursor.moveToFirst()) {
                val title = cursor.getString(0) ?: "Event"
                val beginMillis = cursor.getLong(1)
                val allDay = cursor.getInt(2) == 1

                val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
                val timeStr = if (allDay) "ALL DAY" else timeFormat.format(Date(beginMillis))

                emit(
                    CalendarEventInfo(
                        title = title,
                        timeFormatted = timeStr,
                        isAllDay = allDay,
                        hasEvent = true
                    )
                )
                cursor.close()
            } else {
                cursor?.close()
                emit(CalendarEventInfo(hasEvent = false))
            }
        } catch (e: Throwable) {
            emit(CalendarEventInfo(hasEvent = false))
        }
    }

    override fun hasUsageStatsPermission(): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Throwable) {
            false
        }
    }

    override fun isDefaultLauncher(): Boolean {
        return try {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
            }
            val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.resolveActivity(homeIntent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
            } else {
                @Suppress("DEPRECATION")
                packageManager.resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
            }
            resolveInfo?.activityInfo?.packageName == context.packageName
        } catch (e: Throwable) {
            false
        }
    }

    override fun openDefaultLauncherSettings() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = context.getSystemService(Context.ROLE_SERVICE) as? RoleManager
                if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                    if (!roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                        return
                    }
                }
            }

            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else {
                Intent(Settings.ACTION_HOME_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(intent)
        } catch (e: Throwable) {
            try {
                val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
            } catch (ignored: Throwable) {}
        }
    }

    @SuppressLint("WrongConstant")
    override fun expandNotificationShade() {
        try {
            val statusBarService = context.getSystemService("statusbar")
            val statusBarManagerClass = Class.forName("android.app.StatusBarManager")
            val expandMethod = statusBarManagerClass.getMethod("expandNotificationsPanel")
            expandMethod.invoke(statusBarService)
        } catch (e: Throwable) {
            // Fallback
        }
    }

    override fun toggleTorch(): Boolean {
        if (cameraManager == null) return false
        return try {
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return false
            isTorchOn = !isTorchOn
            cameraManager.setTorchMode(cameraId, isTorchOn)
            isTorchOn
        } catch (e: Throwable) {
            false
        }
    }

    override fun launchCamera() {
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Throwable) {
            // Fallback
        }
    }

    override fun launchCalendar() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_CALENDAR)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Throwable) {
            val fallback = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("content://com.android.calendar/time")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(fallback)
            } catch (ignored: Throwable) {}
        }
    }

    override fun launchWebSearch(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Throwable) {
            // Fallback
        }
    }

    override suspend fun saveCustomIconImage(
        packageName: String,
        inputStream: InputStream
    ): String = withContext(Dispatchers.IO) {
        val iconsDir = File(context.filesDir, "custom_icons")
        if (!iconsDir.exists()) {
            iconsDir.mkdirs()
        }
        val safeFileName = "${packageName.replace('.', '_')}_icon_${System.currentTimeMillis()}.png"
        val targetFile = File(iconsDir, safeFileName)

        FileOutputStream(targetFile).use { output ->
            inputStream.copyTo(output)
        }

        Uri.fromFile(targetFile).toString()
    }
}
