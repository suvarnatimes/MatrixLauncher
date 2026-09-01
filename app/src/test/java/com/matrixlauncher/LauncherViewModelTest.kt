package com.matrixlauncher

import android.graphics.Rect
import android.os.Bundle
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.AppShortcutModel
import com.matrixlauncher.domain.model.BatteryInfo
import com.matrixlauncher.domain.model.CalendarEventInfo
import com.matrixlauncher.domain.model.DotDensity
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.DoubleTapAction
import com.matrixlauncher.domain.model.LauncherSettings
import com.matrixlauncher.domain.model.PackageChangeEvent
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.ScreenTimeStats
import com.matrixlauncher.domain.model.SwipeGestureAction
import com.matrixlauncher.domain.model.WeatherInfo
import com.matrixlauncher.domain.model.WebSearchProvider
import com.matrixlauncher.domain.repository.AppDatabaseRepository
import com.matrixlauncher.domain.repository.LauncherAppsRepository
import com.matrixlauncher.domain.repository.PreferencesRepository
import com.matrixlauncher.ui.mvi.LauncherIntent
import com.matrixlauncher.ui.mvi.LauncherScreen
import com.matrixlauncher.ui.viewmodel.LauncherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LauncherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeAppsRepository = FakeLauncherAppsRepository()
    private val fakePreferencesRepository = FakePreferencesRepository()
    private val fakeDatabaseRepository = FakeAppDatabaseRepository()

    private lateinit var viewModel: LauncherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LauncherViewModel(
            launcherAppsRepository = fakeAppsRepository,
            preferencesRepository = fakePreferencesRepository,
            appDatabaseRepository = fakeDatabaseRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads installed apps and applies preferences`() = runTest(testDispatcher) {
        val sampleApps = listOf(
            AppModel("com.chrome", "Main", "Chrome"),
            AppModel("com.spotify", "Main", "Spotify")
        )
        fakeAppsRepository.installedApps = sampleApps
        viewModel.onIntent(LauncherIntent.RefreshApps)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.allApps.size)
        assertFalse(state.isLoading)
    }

    @Test
    fun `search query updates filteredApps and evaluates calculator`() = runTest(testDispatcher) {
        val sampleApps = listOf(
            AppModel("com.chrome", "Main", "Chrome"),
            AppModel("com.spotify", "Main", "Spotify")
        )
        fakeAppsRepository.installedApps = sampleApps
        viewModel.onIntent(LauncherIntent.RefreshApps)
        advanceUntilIdle()

        viewModel.onIntent(LauncherIntent.SearchQueryChanged("12 * 8"))
        val state = viewModel.uiState.value
        assertEquals("96", state.calculatedResult)
    }

    @Test
    fun `screen navigation transitions correctly`() = runTest(testDispatcher) {
        assertEquals(LauncherScreen.HOME, viewModel.uiState.value.currentScreen)

        viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.DRAWER))
        assertEquals(LauncherScreen.DRAWER, viewModel.uiState.value.currentScreen)

        viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.SETTINGS))
        assertEquals(LauncherScreen.SETTINGS, viewModel.uiState.value.currentScreen)

        viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.HOME))
        assertEquals(LauncherScreen.HOME, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun `toggling favorite updates preferences repository`() = runTest(testDispatcher) {
        viewModel.onIntent(LauncherIntent.ToggleFavorite("com.chrome"))
        advanceUntilIdle()

        assertTrue(fakePreferencesRepository.favorites.contains("com.chrome"))
    }

    @Test
    fun `custom label rename updates database repository`() = runTest(testDispatcher) {
        viewModel.onIntent(LauncherIntent.SetCustomLabel("com.chrome", "Web Browser"))
        advanceUntilIdle()

        assertEquals("Web Browser", fakeDatabaseRepository.customLabels["com.chrome"])
    }
}

// Test Doubles
private class FakeLauncherAppsRepository : LauncherAppsRepository {
    var installedApps = listOf<AppModel>()
    private val packageChanges = MutableSharedFlow<PackageChangeEvent>()
    private val batteryFlow = MutableStateFlow(BatteryInfo(level = 90))
    private val screenTimeFlow = MutableStateFlow(ScreenTimeStats(totalMillisToday = 3600000L, hasPermission = true))
    private val weatherFlow = MutableStateFlow(WeatherInfo(temperatureCelsius = 22))
    private val calendarFlow = MutableStateFlow(CalendarEventInfo(hasEvent = false))

    override suspend fun getInstalledApps(): List<AppModel> = installedApps
    override fun observePackageChanges(): Flow<PackageChangeEvent> = packageChanges.asSharedFlow()
    override fun launchApp(app: AppModel, sourceBounds: Rect?, opts: Bundle?): Result<Unit> = Result.success(Unit)
    override suspend fun getShortcutsForApp(app: AppModel): List<AppShortcutModel> = emptyList()
    override fun startShortcut(shortcut: AppShortcutModel): Result<Unit> = Result.success(Unit)
    override fun openAppInfo(app: AppModel) {}
    override fun requestUninstall(app: AppModel) {}
    override fun observeBatteryInfo(): Flow<BatteryInfo> = batteryFlow.asStateFlow()
    override fun observeScreenTimeStats(): Flow<ScreenTimeStats> = screenTimeFlow.asStateFlow()
    override fun observeWeatherInfo(): Flow<WeatherInfo> = weatherFlow.asStateFlow()
    override fun observeUpcomingCalendarEvent(): Flow<CalendarEventInfo> = calendarFlow.asStateFlow()
    override fun hasUsageStatsPermission(): Boolean = true
    override fun expandNotificationShade() {}
    override fun openDefaultLauncherSettings() {}
    override fun toggleTorch(): Boolean = true
    override fun launchCamera() {}
    override fun launchCalendar() {}
    override fun launchWebSearch(url: String) {}
}

private class FakePreferencesRepository : PreferencesRepository {
    val favorites = mutableSetOf<String>()
    private val _settingsFlow = MutableStateFlow(LauncherSettings())
    override val settingsFlow: Flow<LauncherSettings> = _settingsFlow.asStateFlow()

    override suspend fun setAccentColor(accent: AccentColor) {
        _settingsFlow.value = _settingsFlow.value.copy(accentColor = accent)
    }
    override suspend fun setCustomAccentHex(hex: String) {
        _settingsFlow.value = _settingsFlow.value.copy(customAccentHex = hex)
    }
    override suspend fun setDotDensity(density: DotDensity) {
        _settingsFlow.value = _settingsFlow.value.copy(dotDensity = density)
    }
    override suspend fun setDotShape(shape: DotShape) {
        _settingsFlow.value = _settingsFlow.value.copy(dotShape = shape)
    }
    override suspend fun setScrollerAlignment(alignment: ScrollerAlignment) {
        _settingsFlow.value = _settingsFlow.value.copy(scrollerAlignment = alignment)
    }
    override suspend fun setDoubleTapAction(action: DoubleTapAction) {
        _settingsFlow.value = _settingsFlow.value.copy(doubleTapAction = action)
    }
    override suspend fun setSwipeLeftAction(action: SwipeGestureAction) {
        _settingsFlow.value = _settingsFlow.value.copy(swipeLeftAction = action)
    }
    override suspend fun setSwipeRightAction(action: SwipeGestureAction) {
        _settingsFlow.value = _settingsFlow.value.copy(swipeRightAction = action)
    }
    override suspend fun setSearchProvider(provider: WebSearchProvider) {
        _settingsFlow.value = _settingsFlow.value.copy(defaultSearchProvider = provider)
    }
    override suspend fun set24HourClock(is24Hour: Boolean) {
        _settingsFlow.value = _settingsFlow.value.copy(is24HourClock = is24Hour)
    }
    override suspend fun setShowScreenTimeGlance(show: Boolean) {
        _settingsFlow.value = _settingsFlow.value.copy(showScreenTimeGlance = show)
    }
    override suspend fun setShowBatteryDotBar(show: Boolean) {
        _settingsFlow.value = _settingsFlow.value.copy(showBatteryDotBar = show)
    }
    override suspend fun setShowScratchpad(show: Boolean) {
        _settingsFlow.value = _settingsFlow.value.copy(showScratchpad = show)
    }
    override suspend fun setScratchpadNote(note: String) {
        _settingsFlow.value = _settingsFlow.value.copy(scratchpadNote = note)
    }
    override suspend fun setHapticsEnabled(enabled: Boolean) {
        _settingsFlow.value = _settingsFlow.value.copy(hapticsEnabled = enabled)
    }
    override suspend fun setAgslShaderEnabled(enabled: Boolean) {
        _settingsFlow.value = _settingsFlow.value.copy(agslShaderEnabled = enabled)
    }
    override suspend fun setCrtScanlinesEnabled(enabled: Boolean) {
        _settingsFlow.value = _settingsFlow.value.copy(enableCrtScanlines = enabled)
    }
    override suspend fun setAutoFocusSearch(enabled: Boolean) {
        _settingsFlow.value = _settingsFlow.value.copy(autoFocusSearch = enabled)
    }
    override suspend fun setMaxFavoritesCount(count: Int) {
        _settingsFlow.value = _settingsFlow.value.copy(maxFavoritesCount = count)
    }
    override suspend fun setMindfulPauseSeconds(seconds: Int) {
        _settingsFlow.value = _settingsFlow.value.copy(mindfulPauseSeconds = seconds)
    }
    override suspend fun toggleMindfulApp(packageName: String) {}
    override suspend fun toggleFavorite(packageName: String) {
        if (favorites.contains(packageName)) favorites.remove(packageName) else favorites.add(packageName)
        _settingsFlow.value = _settingsFlow.value.copy(favoritePackageNames = favorites.toSet())
    }
    override suspend fun removeFavorite(packageName: String) {
        favorites.remove(packageName)
        _settingsFlow.value = _settingsFlow.value.copy(favoritePackageNames = favorites.toSet())
    }
}

private class FakeAppDatabaseRepository : AppDatabaseRepository {
    val customLabels = mutableMapOf<String, String>()
    val hiddenPackages = mutableSetOf<String>()

    private val customLabelsFlow = MutableStateFlow<Map<String, String>>(emptyMap())
    private val hiddenPackagesFlow = MutableStateFlow<Set<String>>(emptySet())

    override fun observeCustomLabels(): Flow<Map<String, String>> = customLabelsFlow.asStateFlow()
    override fun observeHiddenPackages(): Flow<Set<String>> = hiddenPackagesFlow.asStateFlow()

    override suspend fun setCustomLabel(packageName: String, label: String?) {
        if (label != null) customLabels[packageName] = label else customLabels.remove(packageName)
        customLabelsFlow.value = customLabels.toMap()
    }

    override suspend fun setPackageHidden(packageName: String, isHidden: Boolean) {
        if (isHidden) hiddenPackages.add(packageName) else hiddenPackages.remove(packageName)
        hiddenPackagesFlow.value = hiddenPackages.toSet()
    }
}
