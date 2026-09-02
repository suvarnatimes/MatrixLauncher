package com.matrixlauncher.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.AppShortcutModel
import com.matrixlauncher.domain.model.DotDensity
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.IconStyle
import com.matrixlauncher.domain.model.LauncherSettings
import com.matrixlauncher.domain.model.PlacedWidget
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.WebSearchProvider
import com.matrixlauncher.domain.repository.AppDatabaseRepository
import com.matrixlauncher.domain.repository.LauncherAppsRepository
import com.matrixlauncher.domain.repository.PreferencesRepository
import com.matrixlauncher.ui.common.CalculatorEngine
import com.matrixlauncher.ui.common.ConfigBackupHelper
import com.matrixlauncher.ui.common.FuzzySearch
import com.matrixlauncher.ui.common.SystemSettingShortcut
import com.matrixlauncher.ui.common.SystemSettingsShortcuts
import com.matrixlauncher.ui.mvi.HapticFeedbackType
import com.matrixlauncher.ui.mvi.LauncherEffect
import com.matrixlauncher.ui.mvi.LauncherIntent
import com.matrixlauncher.ui.mvi.LauncherScreen
import com.matrixlauncher.ui.mvi.LauncherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val launcherAppsRepository: LauncherAppsRepository,
    private val preferencesRepository: PreferencesRepository,
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    private val _effectFlow = MutableSharedFlow<LauncherEffect>()
    val effectFlow: SharedFlow<LauncherEffect> = _effectFlow.asSharedFlow()

    private val rawInstalledApps = MutableStateFlow<List<AppModel>>(emptyList())
    private var mindfulTimerJob: Job? = null

    init {
        loadInstalledApps()
        observePackageEvents()
        observeSettingsAndData()
        observeTelemetry()
        checkDefaultLauncherStatus()
    }

    fun onIntent(intent: LauncherIntent) {
        when (intent) {
            is LauncherIntent.RefreshApps -> {
                loadInstalledApps()
                checkDefaultLauncherStatus()
            }
            is LauncherIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is LauncherIntent.LaunchApp -> handleAppLaunchRequest(intent.app)
            is LauncherIntent.LaunchAppShortcut -> launchAppShortcut(intent.shortcut)
            is LauncherIntent.ToggleFavorite -> toggleFavorite(intent.packageName)
            is LauncherIntent.SetCustomLabel -> setCustomLabel(intent.packageName, intent.label)
            is LauncherIntent.SetAppHidden -> setAppHidden(intent.packageName, intent.isHidden)
            is LauncherIntent.OpenAppInfo -> launcherAppsRepository.openAppInfo(intent.app)
            is LauncherIntent.UninstallApp -> launcherAppsRepository.requestUninstall(intent.app)
            is LauncherIntent.OpenContextMenu -> openContextMenuForApp(intent.app)
            is LauncherIntent.OpenRenameDialog -> _uiState.update { it.copy(selectedAppForRename = intent.app, selectedAppForMenu = null) }
            is LauncherIntent.NavigateTo -> navigateTo(intent.screen)
            is LauncherIntent.ExpandNotificationShade -> launcherAppsRepository.expandNotificationShade()
            is LauncherIntent.OpenDefaultLauncherSettings -> {
                launcherAppsRepository.openDefaultLauncherSettings()
                checkDefaultLauncherStatus()
            }
            is LauncherIntent.OpenCalendar -> launcherAppsRepository.launchCalendar()

            // Gestures Execution
            is LauncherIntent.PerformGestureAction -> executeGestureAction(intent.actionString)
            is LauncherIntent.UpdateGestureAction -> updateGestureAction(intent.gestureKey, intent.actionString)

            // Free-form Drag & Drop Widgets
            is LauncherIntent.UpdatePlacedWidgets -> updatePlacedWidgets(intent.widgets)
            is LauncherIntent.UpdateCustomUserName -> updateCustomUserName(intent.name)
            is LauncherIntent.SetNameStyleIndex -> setNameStyleIndex(intent.index)
            is LauncherIntent.SetCrossStyleIndex -> setCrossStyleIndex(intent.index)
            is LauncherIntent.SetClockStyleIndex -> setClockStyleIndex(intent.index)

            // Scale Setters
            is LauncherIntent.SetCrossSizeScale -> setCrossSizeScale(intent.scale)
            is LauncherIntent.SetNameSizeScale -> setNameSizeScale(intent.scale)
            is LauncherIntent.SetTimeSizeScale -> setTimeSizeScale(intent.scale)
            is LauncherIntent.SetDateSizeScale -> setDateSizeScale(intent.scale)
            is LauncherIntent.SetBatterySizeScale -> setBatterySizeScale(intent.scale)

            // Bible Verse
            is LauncherIntent.CycleBibleVerse -> cycleBibleVerse()
            is LauncherIntent.SetCustomBibleVerse -> setCustomBibleVerse(intent.verse)

            is LauncherIntent.ToggleBatterySaver -> toggleBatterySaver(intent.enabled)

            // Icon Customization Studio
            is LauncherIntent.UpdateIconStyle -> updateIconStyle(intent.style)
            is LauncherIntent.UpdateAppIcon -> updateAppIcon(intent.packageName, intent.iconUri, intent.colorHex, intent.glyphName, intent.shape)
            is LauncherIntent.UploadAppIconImage -> uploadAppIconImage(intent.packageName, intent.uri)
            is LauncherIntent.ResetAppIcon -> resetAppIcon(intent.packageName)

            // Theme & Controls
            is LauncherIntent.UpdateAccentColor -> updateAccentColor(intent.color)
            is LauncherIntent.UpdateCustomAccentHex -> updateCustomAccentHex(intent.hex)
            is LauncherIntent.UpdateDotDensity -> updateDotDensity(intent.density)
            is LauncherIntent.UpdateDotShape -> updateDotShape(intent.shape)
            is LauncherIntent.UpdateScrollerAlignment -> updateScrollerAlignment(intent.alignment)
            is LauncherIntent.UpdateSearchProvider -> updateSearchProvider(intent.provider)
            is LauncherIntent.ToggleTimeFormat -> updateTimeFormat(intent.is24Hour)
            is LauncherIntent.ToggleScreenTime -> toggleScreenTime(intent.show)
            is LauncherIntent.ToggleBatteryBar -> toggleBatteryBar(intent.show)
            is LauncherIntent.ToggleScratchpad -> toggleScratchpad(intent.show)
            is LauncherIntent.UpdateScratchpadNote -> updateScratchpadNote(intent.note)
            is LauncherIntent.ToggleHaptics -> toggleHaptics(intent.enabled)
            is LauncherIntent.ToggleShader -> toggleShader(intent.enabled)
            is LauncherIntent.ToggleCrtScanlines -> toggleCrtScanlines(intent.enabled)
            is LauncherIntent.ToggleAutoFocusSearch -> toggleAutoFocusSearch(intent.enabled)
            is LauncherIntent.UpdateMaxFavorites -> updateMaxFavorites(intent.count)
            is LauncherIntent.UpdateMindfulPause -> updateMindfulPause(intent.seconds)
            is LauncherIntent.ToggleMindfulApp -> toggleMindfulApp(intent.packageName)

            // Actions
            is LauncherIntent.LaunchWebSearch -> launchWebSearch(intent.query, intent.provider)
            is LauncherIntent.LaunchShortcut -> handleLaunchShortcut(intent.shortcut)
            is LauncherIntent.CancelMindfulLaunch -> cancelMindfulLaunch()
            is LauncherIntent.ConfirmMindfulLaunch -> confirmMindfulLaunch()
            is LauncherIntent.ImportConfig -> importConfig(intent.json)
        }
    }

    private fun executeGestureAction(actionString: String) {
        viewModelScope.launch {
            emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.TICK))
            when {
                actionString.startsWith("APP:") -> {
                    val pkg = actionString.removePrefix("APP:")
                    val targetApp = _uiState.value.allApps.firstOrNull { it.packageName == pkg }
                    if (targetApp != null) {
                        handleAppLaunchRequest(targetApp)
                    } else {
                        emitEffect(LauncherEffect.ShowToast("App not found"))
                    }
                }
                actionString == "EXPAND_NOTIFICATIONS" -> launcherAppsRepository.expandNotificationShade()
                actionString == "OPEN_DRAWER" -> navigateTo(LauncherScreen.DRAWER)
                actionString == "OPEN_SEARCH" -> {
                    _uiState.update { it.copy(currentScreen = LauncherScreen.DRAWER) }
                }
                actionString == "OPEN_SETTINGS" -> navigateTo(LauncherScreen.SETTINGS)
                actionString == "TOGGLE_TORCH" -> {
                    val isOn = launcherAppsRepository.toggleTorch()
                    emitEffect(LauncherEffect.ShowToast(if (isOn) "TORCH [ON]" else "TORCH [OFF]"))
                }
                actionString == "TOGGLE_BATTERY_SAVER" -> {
                    val current = _uiState.value.settings.batterySaverEnabled
                    toggleBatterySaver(!current)
                }
                actionString == "OPEN_CAMERA" -> launcherAppsRepository.launchCamera()
                actionString == "LOCK_SCREEN" -> emitEffect(LauncherEffect.ShowToast("Locking screen"))
                else -> {}
            }
        }
    }

    private fun updateGestureAction(gestureKey: String, actionString: String) = viewModelScope.launch {
        when (gestureKey) {
            "SWIPE_DOWN" -> preferencesRepository.setSwipeDownAction(actionString)
            "SWIPE_UP" -> preferencesRepository.setSwipeUpAction(actionString)
            "SWIPE_LEFT" -> preferencesRepository.setSwipeLeftAction(actionString)
            "SWIPE_RIGHT" -> preferencesRepository.setSwipeRightAction(actionString)
            "DOUBLE_TAP" -> preferencesRepository.setDoubleTapAction(actionString)
            "PINCH_IN" -> preferencesRepository.setPinchInAction(actionString)
            "PINCH_OUT" -> preferencesRepository.setPinchOutAction(actionString)
            "TWO_FINGER_SWIPE_DOWN" -> preferencesRepository.setTwoFingerSwipeDownAction(actionString)
            "TWO_FINGER_SWIPE_UP" -> preferencesRepository.setTwoFingerSwipeUpAction(actionString)
        }
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.TICK))
        emitEffect(LauncherEffect.ShowToast("GESTURE ACTION UPDATED"))
    }

    private fun updatePlacedWidgets(widgets: List<PlacedWidget>) = viewModelScope.launch {
        preferencesRepository.setPlacedWidgets(widgets)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.TICK))
    }

    private fun updateCustomUserName(name: String) = viewModelScope.launch {
        preferencesRepository.setCustomUserName(name)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
        emitEffect(LauncherEffect.ShowToast("NAME UPDATED"))
    }

    private fun setNameStyleIndex(index: Int) = viewModelScope.launch {
        preferencesRepository.setNameStyleIndex(index)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
    }

    private fun setCrossStyleIndex(index: Int) = viewModelScope.launch {
        preferencesRepository.setCrossStyleIndex(index)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
    }

    private fun setClockStyleIndex(index: Int) = viewModelScope.launch {
        preferencesRepository.setClockStyleIndex(index)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
    }

    private fun setCrossSizeScale(scale: Float) = viewModelScope.launch {
        preferencesRepository.setCrossSizeScale(scale)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
    }

    private fun setNameSizeScale(scale: Float) = viewModelScope.launch {
        preferencesRepository.setNameSizeScale(scale)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
    }

    private fun setTimeSizeScale(scale: Float) = viewModelScope.launch {
        preferencesRepository.setTimeSizeScale(scale)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
    }

    private fun setDateSizeScale(scale: Float) = viewModelScope.launch {
        preferencesRepository.setDateSizeScale(scale)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
    }

    private fun setBatterySizeScale(scale: Float) = viewModelScope.launch {
        preferencesRepository.setBatterySizeScale(scale)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
    }

    private fun cycleBibleVerse() = viewModelScope.launch {
        val next = _uiState.value.settings.bibleVerseIndex + 1
        preferencesRepository.setBibleVerseIndex(next)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
    }

    private fun setCustomBibleVerse(verse: String) = viewModelScope.launch {
        preferencesRepository.setCustomBibleVerse(verse)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
    }

    private fun toggleBatterySaver(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.setBatterySaverEnabled(enabled)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.HEAVY_CLICK))
        emitEffect(LauncherEffect.ShowToast(if (enabled) "BATTERY SAVER [ON]" else "BATTERY SAVER [OFF]"))
    }

    private fun checkDefaultLauncherStatus() {
        val isDefault = launcherAppsRepository.isDefaultLauncher()
        _uiState.update { it.copy(isDefaultLauncher = isDefault) }
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val apps = launcherAppsRepository.getInstalledApps()
                rawInstalledApps.value = apps
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun observePackageEvents() {
        launcherAppsRepository.observePackageChanges()
            .onEach {
                loadInstalledApps()
            }
            .launchIn(viewModelScope)
    }

    private fun observeSettingsAndData() {
        combine(
            rawInstalledApps,
            preferencesRepository.settingsFlow,
            appDatabaseRepository.observeAllCustomizations()
        ) { rawApps, settings, customizations ->
            val customizationMap = customizations.associateBy { it.packageName }

            val decoratedApps = rawApps.map { app ->
                val entity = customizationMap[app.packageName]
                val isFav = settings.favoritePackageNames.contains(app.packageName)
                app.copy(
                    customLabel = entity?.customLabel,
                    isHidden = entity?.isHidden ?: false,
                    isFavorite = isFav,
                    customIconUri = entity?.customIconUri,
                    customIconColorHex = entity?.customIconColorHex,
                    customGlyphName = entity?.customGlyphName,
                    customIconShape = entity?.customIconShape
                )
            }

            val visibleApps = decoratedApps.filter { !it.isHidden }
                .sortedBy { it.displayLabel.lowercase() }
            val hiddenList = decoratedApps.filter { it.isHidden }
                .sortedBy { it.displayLabel.lowercase() }

            val favorites = visibleApps.filter { it.isFavorite }.take(settings.maxFavoritesCount)

            val currentQuery = _uiState.value.searchQuery
            val filtered = FuzzySearch.filterApps(visibleApps, currentQuery)
            val shortcuts = SystemSettingsShortcuts.searchShortcuts(currentQuery)
            val calcResult = CalculatorEngine.evaluate(currentQuery)

            _uiState.update { current ->
                current.copy(
                    allApps = visibleApps,
                    recentApps = visibleApps.take(6),
                    hiddenApps = hiddenList,
                    pinnedFavorites = favorites,
                    filteredApps = filtered,
                    matchedShortcuts = shortcuts,
                    calculatedResult = calcResult,
                    settings = settings,
                    isLoading = false
                )
            }
        }
        .flowOn(Dispatchers.Default)
        .launchIn(viewModelScope)
    }

    private fun observeTelemetry() {
        launcherAppsRepository.observeBatteryInfo()
            .onEach { battery ->
                _uiState.update { it.copy(batteryInfo = battery) }
            }
            .launchIn(viewModelScope)

        launcherAppsRepository.observeScreenTimeStats()
            .onEach { stats ->
                _uiState.update { it.copy(screenTimeStats = stats) }
            }
            .launchIn(viewModelScope)

        launcherAppsRepository.observeWeatherInfo()
            .onEach { weather ->
                _uiState.update { it.copy(weatherInfo = weather) }
            }
            .launchIn(viewModelScope)

        launcherAppsRepository.observeUpcomingCalendarEvent()
            .onEach { event ->
                _uiState.update { it.copy(calendarEvent = event) }
            }
            .launchIn(viewModelScope)
    }

    private fun onSearchQueryChanged(query: String) {
        val filtered = FuzzySearch.filterApps(_uiState.value.allApps, query)
        val shortcuts = SystemSettingsShortcuts.searchShortcuts(query)
        val calcResult = CalculatorEngine.evaluate(query)

        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredApps = filtered,
                matchedShortcuts = shortcuts,
                calculatedResult = calcResult
            )
        }
    }

    private fun openContextMenuForApp(app: AppModel?) {
        if (app == null) {
            _uiState.update { it.copy(selectedAppForMenu = null, selectedAppShortcuts = emptyList()) }
            return
        }

        viewModelScope.launch {
            val shortcuts = launcherAppsRepository.getShortcutsForApp(app)
            _uiState.update {
                it.copy(
                    selectedAppForMenu = app,
                    selectedAppShortcuts = shortcuts
                )
            }
        }
    }

    private fun launchAppShortcut(shortcut: AppShortcutModel) {
        viewModelScope.launch {
            emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
            val res = launcherAppsRepository.startShortcut(shortcut)
            res.onFailure {
                emitEffect(LauncherEffect.ShowToast("Failed to launch shortcut"))
            }
            _uiState.update { it.copy(selectedAppForMenu = null) }
        }
    }

    private fun handleAppLaunchRequest(app: AppModel) {
        val settings = _uiState.value.settings
        val isMindfulApp = settings.mindfulAppPackages.contains(app.packageName) && settings.mindfulPauseSeconds > 0

        if (isMindfulApp) {
            startMindfulCountdown(app, settings.mindfulPauseSeconds)
        } else {
            executeDirectAppLaunch(app)
        }
    }

    private fun startMindfulCountdown(app: AppModel, seconds: Int) {
        mindfulTimerJob?.cancel()
        _uiState.update {
            it.copy(
                mindfulAppPendingLaunch = app,
                mindfulSecondsRemaining = seconds
            )
        }

        mindfulTimerJob = viewModelScope.launch {
            emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.HEAVY_CLICK))
            for (sec in seconds downTo 1) {
                _uiState.update { it.copy(mindfulSecondsRemaining = sec) }
                delay(1000L)
            }
            _uiState.update { it.copy(mindfulSecondsRemaining = 0) }
            executeDirectAppLaunch(app)
            cancelMindfulLaunch()
        }
    }

    private fun cancelMindfulLaunch() {
        mindfulTimerJob?.cancel()
        mindfulTimerJob = null
        _uiState.update {
            it.copy(
                mindfulAppPendingLaunch = null,
                mindfulSecondsRemaining = 0
            )
        }
    }

    private fun confirmMindfulLaunch() {
        val app = _uiState.value.mindfulAppPendingLaunch
        if (app != null) {
            mindfulTimerJob?.cancel()
            mindfulTimerJob = null
            executeDirectAppLaunch(app)
            cancelMindfulLaunch()
        }
    }

    private fun executeDirectAppLaunch(app: AppModel) {
        viewModelScope.launch {
            emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
            val result = launcherAppsRepository.launchApp(app)
            result.onFailure {
                emitEffect(LauncherEffect.ShowToast("Failed to launch ${app.displayLabel}"))
            }
        }
    }

    private fun launchWebSearch(query: String, provider: WebSearchProvider) {
        val encoded = java.net.URLEncoder.encode(query, "UTF-8")
        val fullUrl = "${provider.searchUrl}$encoded"
        launcherAppsRepository.launchWebSearch(fullUrl)
    }

    private fun handleLaunchShortcut(shortcut: SystemSettingShortcut) {
        viewModelScope.launch {
            emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
        }
    }

    private fun toggleFavorite(packageName: String) {
        viewModelScope.launch {
            emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.TICK))
            preferencesRepository.toggleFavorite(packageName)
            _uiState.update { it.copy(selectedAppForMenu = null) }
        }
    }

    private fun setCustomLabel(packageName: String, label: String?) {
        viewModelScope.launch {
            emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.TICK))
            appDatabaseRepository.setCustomLabel(packageName, label)
            _uiState.update { it.copy(selectedAppForRename = null) }
        }
    }

    private fun setAppHidden(packageName: String, isHidden: Boolean) {
        viewModelScope.launch {
            emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.TICK))
            appDatabaseRepository.setPackageHidden(packageName, isHidden)
            _uiState.update { it.copy(selectedAppForMenu = null) }
        }
    }

    // Icon Studio operations
    private fun updateIconStyle(style: IconStyle) = viewModelScope.launch {
        preferencesRepository.setIconStyle(style)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.TICK))
    }

    private fun updateAppIcon(
        packageName: String,
        iconUri: String?,
        colorHex: String?,
        glyphName: String?,
        shape: String?
    ) = viewModelScope.launch {
        appDatabaseRepository.updateIconCustomization(packageName, iconUri, colorHex, glyphName, shape)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.CLICK))
        emitEffect(LauncherEffect.ShowToast("ICON SAVED"))
    }

    private fun uploadAppIconImage(packageName: String, uri: Uri) = viewModelScope.launch {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val savedUri = launcherAppsRepository.saveCustomIconImage(packageName, inputStream)
                appDatabaseRepository.updateIconCustomization(packageName, savedUri, null, null, null)
                emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.HEAVY_CLICK))
                emitEffect(LauncherEffect.ShowToast("CUSTOM PNG ICON APPLIED"))
            }
        } catch (e: Exception) {
            emitEffect(LauncherEffect.ShowToast("FAILED TO LOAD IMAGE"))
        }
    }

    private fun resetAppIcon(packageName: String) = viewModelScope.launch {
        appDatabaseRepository.resetIconCustomization(packageName)
        emitEffect(LauncherEffect.PerformHaptic(HapticFeedbackType.TICK))
        emitEffect(LauncherEffect.ShowToast("ICON RESET TO DEFAULT"))
    }

    private fun navigateTo(screen: LauncherScreen) {
        if (screen == LauncherScreen.HOME || screen == LauncherScreen.SETTINGS) {
            checkDefaultLauncherStatus()
        }

        _uiState.update { current ->
            if (screen == LauncherScreen.HOME) {
                current.copy(
                    currentScreen = screen,
                    searchQuery = "",
                    filteredApps = current.allApps,
                    matchedShortcuts = emptyList(),
                    calculatedResult = null,
                    selectedAppForMenu = null,
                    selectedAppShortcuts = emptyList(),
                    selectedAppForRename = null
                )
            } else {
                current.copy(currentScreen = screen)
            }
        }
    }

    private fun updateAccentColor(color: AccentColor) = viewModelScope.launch { preferencesRepository.setAccentColor(color) }
    private fun updateCustomAccentHex(hex: String) = viewModelScope.launch { preferencesRepository.setCustomAccentHex(hex) }
    private fun updateDotDensity(density: DotDensity) = viewModelScope.launch { preferencesRepository.setDotDensity(density) }
    private fun updateDotShape(shape: DotShape) = viewModelScope.launch { preferencesRepository.setDotShape(shape) }
    private fun updateScrollerAlignment(alignment: ScrollerAlignment) = viewModelScope.launch { preferencesRepository.setScrollerAlignment(alignment) }
    private fun updateSearchProvider(provider: WebSearchProvider) = viewModelScope.launch { preferencesRepository.setSearchProvider(provider) }
    private fun updateTimeFormat(is24Hour: Boolean) = viewModelScope.launch { preferencesRepository.set24HourClock(is24Hour) }
    private fun toggleScreenTime(show: Boolean) = viewModelScope.launch { preferencesRepository.setShowScreenTimeGlance(show) }
    private fun toggleBatteryBar(show: Boolean) = viewModelScope.launch { preferencesRepository.setShowBatteryDotBar(show) }
    private fun toggleScratchpad(show: Boolean) = viewModelScope.launch { preferencesRepository.setShowScratchpad(show) }
    private fun updateScratchpadNote(note: String) = viewModelScope.launch { preferencesRepository.setScratchpadNote(note) }
    private fun toggleHaptics(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setHapticsEnabled(enabled) }
    private fun toggleShader(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setAgslShaderEnabled(enabled) }
    private fun toggleCrtScanlines(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setCrtScanlinesEnabled(enabled) }
    private fun toggleAutoFocusSearch(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setAutoFocusSearch(enabled) }
    private fun updateMaxFavorites(count: Int) = viewModelScope.launch { preferencesRepository.setMaxFavoritesCount(count) }
    private fun updateMindfulPause(seconds: Int) = viewModelScope.launch { preferencesRepository.setMindfulPauseSeconds(seconds) }
    private fun toggleMindfulApp(packageName: String) = viewModelScope.launch { preferencesRepository.toggleMindfulApp(packageName) }

    private fun importConfig(json: String) {
        viewModelScope.launch {
            val backup = ConfigBackupHelper.importFromJson(json)
            if (backup != null) {
                preferencesRepository.setAccentColor(backup.settings.accentColor)
                preferencesRepository.setDotDensity(backup.settings.dotDensity)
                preferencesRepository.setDotShape(backup.settings.dotShape)
                preferencesRepository.setScrollerAlignment(backup.settings.scrollerAlignment)
                preferencesRepository.set24HourClock(backup.settings.is24HourClock)
                preferencesRepository.setMaxFavoritesCount(backup.settings.maxFavoritesCount)
                preferencesRepository.setCustomUserName(backup.settings.customUserName)

                backup.customLabels.forEach { (pkg, label) ->
                    appDatabaseRepository.setCustomLabel(pkg, label)
                }
                backup.hiddenPackages.forEach { pkg ->
                    appDatabaseRepository.setPackageHidden(pkg, true)
                }

                emitEffect(LauncherEffect.ShowToast("CONFIGURATION RESTORED SUCCESSFULLY"))
            } else {
                emitEffect(LauncherEffect.ShowToast("INVALID CONFIGURATION JSON"))
            }
        }
    }

    private suspend fun emitEffect(effect: LauncherEffect) {
        _effectFlow.emit(effect)
    }
}
