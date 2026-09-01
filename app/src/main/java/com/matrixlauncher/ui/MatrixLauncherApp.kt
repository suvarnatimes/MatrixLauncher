package com.matrixlauncher.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    var vDragAccumulator by remember { mutableFloatStateOf(0f) }
    val vDraggableState = rememberDraggableState { delta ->
        vDragAccumulator += delta
    }

    var hDragAccumulator by remember { mutableFloatStateOf(0f) }
    val hDraggableState = rememberDraggableState { delta ->
        hDragAccumulator += delta
    }

    DotMatrixTheme(
        accentColor = uiState.settings.accentColor,
        dotDensity = uiState.settings.dotDensity
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (uiState.currentScreen == LauncherScreen.HOME) {
                        Modifier
                            .draggable(
                                state = vDraggableState,
                                orientation = Orientation.Vertical,
                                onDragStopped = { velocity ->
                                    if (vDragAccumulator < -80f || velocity < -500f) {
                                        viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.DRAWER))
                                    } else if (vDragAccumulator > 80f || velocity > 500f) {
                                        viewModel.onIntent(LauncherIntent.ExpandNotificationShade)
                                    }
                                    vDragAccumulator = 0f
                                }
                            )
                            .draggable(
                                state = hDraggableState,
                                orientation = Orientation.Horizontal,
                                onDragStopped = { velocity ->
                                    if (hDragAccumulator < -80f || velocity < -500f) {
                                        viewModel.onIntent(LauncherIntent.PerformSwipeAction(uiState.settings.swipeLeftAction))
                                    } else if (hDragAccumulator > 80f || velocity > 500f) {
                                        viewModel.onIntent(LauncherIntent.PerformSwipeAction(uiState.settings.swipeRightAction))
                                    }
                                    hDragAccumulator = 0f
                                }
                            )
                    } else {
                        Modifier
                    }
                )
        ) {
            // High-Performance Cached Dot Grid Background
            DotGridBackground(
                dotDensity = uiState.settings.dotDensity,
                dotShape = uiState.settings.dotShape,
                enableShader = uiState.settings.agslShaderEnabled,
                enableCrtScanlines = uiState.settings.enableCrtScanlines
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
                            iconStyle = uiState.settings.iconStyle,
                            dotShape = uiState.settings.dotShape,
                            enabledWidgets = uiState.settings.enabledWidgets,
                            mindfulPendingApp = uiState.mindfulAppPendingLaunch,
                            mindfulSecondsRemaining = uiState.mindfulSecondsRemaining,
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
                            onDoubleTap = {
                                viewModel.onIntent(LauncherIntent.PerformDoubleTapAction)
                            },
                            onSwipeUpClick = {
                                viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.DRAWER))
                            },
                            onSetDefaultLauncherClick = {
                                onRequestSetDefaultLauncher()
                            },
                            onSettingsClick = {
                                viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.SETTINGS))
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
                            onEnabledWidgetsChange = { widgets ->
                                viewModel.onIntent(LauncherIntent.UpdateEnabledWidgets(widgets))
                            },
                            onOpenIconStudio = {
                                viewModel.onIntent(LauncherIntent.NavigateTo(LauncherScreen.ICON_STUDIO))
                            },
                            onScrollerAlignmentChange = { align ->
                                viewModel.onIntent(LauncherIntent.UpdateScrollerAlignment(align))
                            },
                            onDoubleTapActionChange = { action ->
                                viewModel.onIntent(LauncherIntent.UpdateDoubleTapAction(action))
                            },
                            onSwipeLeftActionChange = { action ->
                                viewModel.onIntent(LauncherIntent.UpdateSwipeLeftAction(action))
                            },
                            onSwipeRightActionChange = { action ->
                                viewModel.onIntent(LauncherIntent.UpdateSwipeRightAction(action))
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
