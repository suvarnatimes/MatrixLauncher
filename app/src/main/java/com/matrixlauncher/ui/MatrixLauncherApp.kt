package com.matrixlauncher.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.matrixlauncher.ui.common.HapticHelper
import com.matrixlauncher.ui.drawer.AppDrawerScreen
import com.matrixlauncher.ui.graphics.DotGridBackground
import com.matrixlauncher.ui.home.HomeScreen
import com.matrixlauncher.ui.icons.IconCustomizationScreen
import com.matrixlauncher.ui.mvi.LauncherEffect
import com.matrixlauncher.ui.mvi.LauncherIntent
import com.matrixlauncher.ui.mvi.LauncherScreen
import com.matrixlauncher.ui.settings.SettingsScreen
import com.matrixlauncher.ui.theme.DotMatrixTheme
import com.matrixlauncher.ui.viewmodel.LauncherViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MatrixLauncherApp(
    viewModel: LauncherViewModel,
    onRequestSetDefaultLauncher: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val hapticHelper = remember { HapticHelper(context) }

    LaunchedEffect(Unit) {
        viewModel.effectFlow.collectLatest { effect ->
            when (effect) {
                is LauncherEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is LauncherEffect.PerformHaptic -> {
                    hapticHelper.performHaptic(effect.type, uiState.settings.hapticsEnabled)
                }
            }
        }
    }

    BackHandler {
        when {
            uiState.selectedAppForRename != null -> {
                viewModel.onIntent(LauncherIntent.OpenRenameDialog(null))
            }
            uiState.selectedAppForMenu != null -> {
                viewModel.onIntent(LauncherIntent.OpenContextMenu(null))
            }
            uiState.mindfulAppPendingLaunch != null -> {
                viewModel.onIntent(LauncherIntent.CancelMindfulLaunch)
            }
            uiState.currentScreen == LauncherScreen.DRAWER -> {
                if (uiState.searchQuery.isNotEmpty()) {
                    viewModel.onIntent(LauncherIntent.SearchQueryChanged(""))
                } else {
                    viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.HOME))
                }
            }
            uiState.currentScreen == LauncherScreen.SETTINGS -> {
                viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.HOME))
            }
            uiState.currentScreen == LauncherScreen.ICON_STUDIO -> {
                viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.SETTINGS))
            }
            else -> {
                // Stay on Home screen
            }
        }
    }

    DotMatrixTheme(
        accentColor = uiState.settings.accentColor,
        dotDensity = uiState.settings.dotDensity
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // High-Performance Cached Dot Grid Background
            DotGridBackground(
                dotDensity = uiState.settings.dotDensity,
                dotShape = uiState.settings.dotShape,
                enableShader = uiState.settings.agslShaderEnabled && !uiState.settings.batterySaverEnabled,
                enableCrtScanlines = uiState.settings.enableCrtScanlines && !uiState.settings.batterySaverEnabled
            )

            // Animated Screen Switching
            AnimatedContent(
                targetState = uiState.currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    LauncherScreen.HOME -> {
                        HomeScreen(
                            pinnedFavorites = uiState.pinnedFavorites,
                            recentApps = uiState.recentApps,
                            batteryInfo = uiState.batteryInfo,
                            screenTimeStats = uiState.screenTimeStats,
                            weatherInfo = uiState.weatherInfo,
                            calendarEvent = uiState.calendarEvent,
                            isDefaultLauncher = uiState.isDefaultLauncher,
                            is24Hour = uiState.settings.is24HourClock,
                            showBatteryBar = uiState.settings.showBatteryDotBar,
                            showScreenTime = uiState.settings.showScreenTimeGlance,
                            showScratchpad = uiState.settings.showScratchpad,
                            scratchpadNote = uiState.settings.scratchpadNote,
                            customUserName = uiState.settings.customUserName,
                            nameStyleIndex = uiState.settings.nameStyleIndex,
                            crossStyleIndex = uiState.settings.crossStyleIndex,
                            clockStyleIndex = uiState.settings.clockStyleIndex,
                            crossSizeScale = uiState.settings.crossSizeScale,
                            nameSizeScale = uiState.settings.nameSizeScale,
                            timeSizeScale = uiState.settings.timeSizeScale,
                            dateSizeScale = uiState.settings.dateSizeScale,
                            batterySizeScale = uiState.settings.batterySizeScale,
                            bibleVerseIndex = uiState.settings.bibleVerseIndex,
                            iconStyle = uiState.settings.iconStyle,
                            dotShape = uiState.settings.dotShape,
                            placedWidgets = uiState.settings.placedWidgets,
                            mindfulPendingApp = uiState.mindfulAppPendingLaunch,
                            mindfulSecondsRemaining = uiState.mindfulSecondsRemaining,
                            onWidgetsChange = { updatedWidgets ->
                                viewModel.onIntent(LauncherIntent.UpdatePlacedWidgets(updatedWidgets))
                            },
                            onAppClick = { app ->
                                viewModel.onIntent(LauncherIntent.LaunchApp(app))
                            },
                            onAppLongClick = { app ->
                                viewModel.onIntent(LauncherIntent.OpenContextMenu(app))
                            },
                            onCalendarClick = {
                                viewModel.onIntent(LauncherIntent.OpenCalendar)
                            },
                            onUpdateScratchpadNote = { note ->
                                viewModel.onIntent(LauncherIntent.UpdateScratchpadNote(note))
                            },
                            onCancelMindfulLaunch = {
                                viewModel.onIntent(LauncherIntent.CancelMindfulLaunch)
                            },
                            onConfirmMindfulLaunch = {
                                viewModel.onIntent(LauncherIntent.ConfirmMindfulLaunch)
                            },
                            onCycleBibleVerse = {
                                viewModel.onIntent(LauncherIntent.CycleBibleVerse)
                            },
                            onSwipeUp = {
                                viewModel.onIntent(LauncherIntent.PerformGestureAction(uiState.settings.swipeUpAction))
                            },
                            onSwipeDown = {
                                viewModel.onIntent(LauncherIntent.PerformGestureAction(uiState.settings.swipeDownAction))
                            },
                            onSwipeLeft = {
                                viewModel.onIntent(LauncherIntent.PerformGestureAction(uiState.settings.swipeLeftAction))
                            },
                            onSwipeRight = {
                                viewModel.onIntent(LauncherIntent.PerformGestureAction(uiState.settings.swipeRightAction))
                            },
                            onTwoFingerSwipeUp = {
                                viewModel.onIntent(LauncherIntent.PerformGestureAction(uiState.settings.twoFingerSwipeUpAction))
                            },
                            onTwoFingerSwipeDown = {
                                viewModel.onIntent(LauncherIntent.PerformGestureAction(uiState.settings.twoFingerSwipeDownAction))
                            },
                            onPinchIn = {
                                viewModel.onIntent(LauncherIntent.PerformGestureAction(uiState.settings.pinchInAction))
                            },
                            onPinchOut = {
                                viewModel.onIntent(LauncherIntent.PerformGestureAction(uiState.settings.pinchOutAction))
                            },
                            onDoubleTap = {
                                viewModel.onIntent(LauncherIntent.PerformGestureAction(uiState.settings.doubleTapAction))
                            },
                            onSetDefaultLauncherClick = {
                                onRequestSetDefaultLauncher()
                            }
                        )
                    }

                    LauncherScreen.DRAWER -> {
                        AppDrawerScreen(
                            apps = uiState.filteredApps,
                            matchedShortcuts = uiState.matchedShortcuts,
                            calculatedResult = uiState.calculatedResult,
                            searchQuery = uiState.searchQuery,
                            autoFocusSearch = uiState.settings.autoFocusSearch,
                            iconStyle = uiState.settings.iconStyle,
                            dotShape = uiState.settings.dotShape,
                            scrollerAlignment = uiState.settings.scrollerAlignment,
                            selectedAppForMenu = uiState.selectedAppForMenu,
                            selectedAppShortcuts = uiState.selectedAppShortcuts,
                            selectedAppForRename = uiState.selectedAppForRename,
                            onSearchQueryChange = { query ->
                                viewModel.onIntent(LauncherIntent.SearchQueryChanged(query))
                            },
                            onAppClick = { app ->
                                viewModel.onIntent(LauncherIntent.LaunchApp(app))
                            },
                            onAppLongClick = { app ->
                                viewModel.onIntent(LauncherIntent.OpenContextMenu(app))
                            },
                            onShortcutClick = { shortcut ->
                                viewModel.onIntent(LauncherIntent.LaunchAppShortcut(shortcut))
                            },
                            onToggleFavorite = { pkg ->
                                viewModel.onIntent(LauncherIntent.ToggleFavorite(pkg))
                            },
                            onRenameApp = { pkg, label ->
                                viewModel.onIntent(LauncherIntent.SetCustomLabel(pkg, label))
                            },
                            onHideApp = { pkg, hidden ->
                                viewModel.onIntent(LauncherIntent.SetAppHidden(pkg, hidden))
                            },
                            onOpenIconStudio = {
                                viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.ICON_STUDIO))
                            },
                            onAppInfo = { app ->
                                viewModel.onIntent(LauncherIntent.OpenAppInfo(app))
                            },
                            onUninstall = { app ->
                                viewModel.onIntent(LauncherIntent.UninstallApp(app))
                            },
                            onWebSearch = { query, provider ->
                                viewModel.onIntent(LauncherIntent.LaunchWebSearch(query, provider))
                            },
                            onCloseContextMenu = {
                                viewModel.onIntent(LauncherIntent.OpenContextMenu(null))
                            },
                            onCloseRenameDialog = {
                                viewModel.onIntent(LauncherIntent.OpenRenameDialog(null))
                            },
                            onBackClick = {
                                viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.HOME))
                            }
                        )
                    }

                    LauncherScreen.SETTINGS -> {
                        SettingsScreen(
                            settings = uiState.settings,
                            allApps = uiState.allApps,
                            screenTimeStats = uiState.screenTimeStats,
                            hiddenApps = uiState.hiddenApps,
                            isDefaultLauncher = uiState.isDefaultLauncher,
                            onAccentColorChange = { color ->
                                viewModel.onIntent(LauncherIntent.UpdateAccentColor(color))
                            },
                            onCustomHexChange = { hex ->
                                viewModel.onIntent(LauncherIntent.UpdateCustomAccentHex(hex))
                            },
                            onDotDensityChange = { density ->
                                viewModel.onIntent(LauncherIntent.UpdateDotDensity(density))
                            },
                            onDotShapeChange = { shape ->
                                viewModel.onIntent(LauncherIntent.UpdateDotShape(shape))
                            },
                            onIconStyleChange = { style ->
                                viewModel.onIntent(LauncherIntent.UpdateIconStyle(style))
                            },
                            onCustomUserNameChange = { name ->
                                viewModel.onIntent(LauncherIntent.UpdateCustomUserName(name))
                            },
                            onNameStyleChange = { index ->
                                viewModel.onIntent(LauncherIntent.SetNameStyleIndex(index))
                            },
                            onCrossStyleChange = { index ->
                                viewModel.onIntent(LauncherIntent.SetCrossStyleIndex(index))
                            },
                            onClockStyleChange = { index ->
                                viewModel.onIntent(LauncherIntent.SetClockStyleIndex(index))
                            },
                            onCrossSizeScaleChange = { scale ->
                                viewModel.onIntent(LauncherIntent.SetCrossSizeScale(scale))
                            },
                            onNameSizeScaleChange = { scale ->
                                viewModel.onIntent(LauncherIntent.SetNameSizeScale(scale))
                            },
                            onTimeSizeScaleChange = { scale ->
                                viewModel.onIntent(LauncherIntent.SetTimeSizeScale(scale))
                            },
                            onDateSizeScaleChange = { scale ->
                                viewModel.onIntent(LauncherIntent.SetDateSizeScale(scale))
                            },
                            onBatterySizeScaleChange = { scale ->
                                viewModel.onIntent(LauncherIntent.SetBatterySizeScale(scale))
                            },
                            onToggleBatterySaver = { enabled ->
                                viewModel.onIntent(LauncherIntent.ToggleBatterySaver(enabled))
                            },
                            onOpenIconStudio = {
                                viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.ICON_STUDIO))
                            },
                            onScrollerAlignmentChange = { align ->
                                viewModel.onIntent(LauncherIntent.UpdateScrollerAlignment(align))
                            },
                            onUpdateGestureAction = { key, action ->
                                viewModel.onIntent(LauncherIntent.UpdateGestureAction(key, action))
                            },
                            onSearchProviderChange = { provider ->
                                viewModel.onIntent(LauncherIntent.UpdateSearchProvider(provider))
                            },
                            onToggle24Hour = { is24 ->
                                viewModel.onIntent(LauncherIntent.ToggleTimeFormat(is24))
                            },
                            onToggleScreenTime = { show ->
                                viewModel.onIntent(LauncherIntent.ToggleScreenTime(show))
                            },
                            onToggleBatteryBar = { show ->
                                viewModel.onIntent(LauncherIntent.ToggleBatteryBar(show))
                            },
                            onToggleScratchpad = { show ->
                                viewModel.onIntent(LauncherIntent.ToggleScratchpad(show))
                            },
                            onToggleHaptics = { enabled ->
                                viewModel.onIntent(LauncherIntent.ToggleHaptics(enabled))
                            },
                            onToggleShader = { enabled ->
                                viewModel.onIntent(LauncherIntent.ToggleShader(enabled))
                            },
                            onToggleCrtScanlines = { enabled ->
                                viewModel.onIntent(LauncherIntent.ToggleCrtScanlines(enabled))
                            },
                            onToggleAutoFocusSearch = { enabled ->
                                viewModel.onIntent(LauncherIntent.ToggleAutoFocusSearch(enabled))
                            },
                            onMaxFavoritesChange = { count ->
                                viewModel.onIntent(LauncherIntent.UpdateMaxFavorites(count))
                            },
                            onMindfulPauseChange = { seconds ->
                                viewModel.onIntent(LauncherIntent.UpdateMindfulPause(seconds))
                            },
                            onUnhideApp = { pkg ->
                                viewModel.onIntent(LauncherIntent.SetAppHidden(pkg, false))
                            },
                            onImportConfig = { json ->
                                viewModel.onIntent(LauncherIntent.ImportConfig(json))
                            },
                            onSetDefaultLauncher = {
                                onRequestSetDefaultLauncher()
                            },
                            onBackClick = {
                                viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.HOME))
                            }
                        )
                    }

                    LauncherScreen.ICON_STUDIO -> {
                        IconCustomizationScreen(
                            apps = uiState.allApps,
                            iconStyle = uiState.settings.iconStyle,
                            dotShape = uiState.settings.dotShape,
                            onUpdateAppIcon = { pkg, uri, color, glyph, shape ->
                                viewModel.onIntent(LauncherIntent.UpdateAppIcon(pkg, uri, color, glyph, shape))
                            },
                            onUploadImageForApp = { pkg, uri ->
                                viewModel.onIntent(LauncherIntent.UploadAppIconImage(pkg, uri))
                            },
                            onResetAppIcon = { pkg ->
                                viewModel.onIntent(LauncherIntent.ResetAppIcon(pkg))
                            },
                            onBackClick = {
                                viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.SETTINGS))
                            }
                        )
                    }
                }
            }
        }
    }
}
