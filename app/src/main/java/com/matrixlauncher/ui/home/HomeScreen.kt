package com.matrixlauncher.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.BatteryInfo
import com.matrixlauncher.domain.model.CalendarEventInfo
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.HomeWidgetType
import com.matrixlauncher.domain.model.IconStyle
import com.matrixlauncher.domain.model.ScreenTimeStats
import com.matrixlauncher.domain.model.WeatherInfo
import com.matrixlauncher.ui.graphics.DotMatrixAppIcon
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotArrow
import com.matrixlauncher.ui.theme.Black
import com.matrixlauncher.ui.theme.DarkSurface
import com.matrixlauncher.ui.theme.DotInactiveColor
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.OffWhite
import com.matrixlauncher.ui.theme.SurfaceCard
import com.matrixlauncher.ui.theme.White
import com.matrixlauncher.ui.widgets.DotMatrixWidgetsContainer

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    pinnedFavorites: List<AppModel>,
    batteryInfo: BatteryInfo,
    screenTimeStats: ScreenTimeStats,
    weatherInfo: WeatherInfo,
    calendarEvent: CalendarEventInfo,
    isDefaultLauncher: Boolean,
    is24Hour: Boolean,
    showBatteryBar: Boolean,
    showScreenTime: Boolean,
    showScratchpad: Boolean,
    scratchpadNote: String,
    iconStyle: IconStyle,
    dotShape: DotShape,
    enabledWidgets: List<HomeWidgetType>,
    mindfulPendingApp: AppModel?,
    mindfulSecondsRemaining: Int,
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit,
    onCalendarClick: () -> Unit,
    onUpdateScratchpadNote: (String) -> Unit,
    onCancelMindfulLaunch: () -> Unit,
    onConfirmMindfulLaunch: () -> Unit,
    onDoubleTap: () -> Unit,
    onSwipeUpClick: () -> Unit,
    onSetDefaultLauncherClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    val density = LocalDensity.current
    var isEditingScratchpad by remember { mutableStateOf(false) }

    val pulseAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        pulseAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTap() }
                )
            }
    ) {
        // Settings Button Top-Right
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = DotInactiveColor.copy(alpha = 0.8f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Area (Default Launcher Banner + Customizable Widgets)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Default Launcher Warning & Setup Toggle Banner
                AnimatedVisibility(
                    visible = !isDefaultLauncher,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .background(DarkSurface, RoundedCornerShape(6.dp))
                            .border(1.dp, accent.primaryColor, RoundedCornerShape(6.dp))
                            .clickable(onClick = onSetDefaultLauncherClick)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = accent.primaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "TAP TO SET AS DEFAULT LAUNCHER",
                                    color = White,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "[SET]",
                                color = accent.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dynamic Dot-Matrix Widgets Module
                DotMatrixWidgetsContainer(
                    enabledWidgets = enabledWidgets,
                    batteryInfo = batteryInfo,
                    weatherInfo = weatherInfo,
                    calendarEvent = calendarEvent,
                    scratchpadNote = scratchpadNote,
                    is24Hour = is24Hour,
                    showBatteryBar = showBatteryBar,
                    onCalendarClick = onCalendarClick,
                    onScratchpadClick = { isEditingScratchpad = true }
                )
            }

            // Pinned Favorites List (Center with custom stock icons)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                if (pinnedFavorites.isEmpty()) {
                    Text(
                        text = "NO PINNED APPS // SWIPE UP TO PIN",
                        color = DotInactiveColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    pinnedFavorites.forEach { app ->
                        FavoriteAppItem(
                            app = app,
                            iconStyle = iconStyle,
                            dotShape = dotShape,
                            accentColor = accent,
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) }
                        )
                    }
                }
            }

            // Bottom Swipe-Up Indicator
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSwipeUpClick
                    )
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val arrowRadius = with(density) { 1.5.dp.toPx() }
                val arrowSpacing = with(density) { 4.5.dp.toPx() }
                val pulseOffset = (pulseAnim.value * 6f)

                Canvas(
                    modifier = Modifier
                        .width(28.dp)
                        .height(18.dp)
                ) {
                    drawDotArrow(
                        center = Offset(size.width / 2f, (size.height / 2f) - pulseOffset),
                        dotRadius = arrowRadius,
                        dotSpacing = arrowSpacing,
                        color = accent.primaryColor.copy(alpha = 0.4f + pulseAnim.value * 0.6f),
                        pointUp = true
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "APPLICATIONS",
                    color = DotInactiveColor.copy(alpha = 0.9f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }

        // Scratchpad Edit Dialog
        if (isEditingScratchpad) {
            ScratchpadEditDialog(
                initialText = scratchpadNote,
                onDismiss = { isEditingScratchpad = false },
                onSave = {
                    onUpdateScratchpadNote(it)
                    isEditingScratchpad = false
                }
            )
        }

        // Mindful Pause Modal Dialog
        if (mindfulPendingApp != null) {
            MindfulPauseDialog(
                app = mindfulPendingApp,
                secondsRemaining = mindfulSecondsRemaining,
                onCancel = onCancelMindfulLaunch,
                onOpenNow = onConfirmMindfulLaunch
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FavoriteAppItem(
    app: AppModel,
    iconStyle: IconStyle,
    dotShape: DotShape,
    accentColor: AccentColor,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconStyle != IconStyle.TEXT_ONLY) {
            DotMatrixAppIcon(
                app = app,
                iconStyle = iconStyle,
                dotShape = dotShape,
                sizeDp = 26.dp
            )
            Spacer(modifier = Modifier.width(14.dp))
        } else {
            Canvas(modifier = Modifier.size(8.dp)) {
                drawCircle(
                    color = accentColor.primaryColor,
                    radius = 2.dp.toPx()
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
        }

        Text(
            text = app.displayLabel.uppercase(),
            color = White,
            fontFamily = FontFamily.Monospace,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.2.sp
        )

        if (app.isWorkProfile) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "[W]",
                color = accentColor.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ScratchpadEditDialog(
    initialText: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    var text by remember { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "EDIT SCRATCHPAD NOTE",
                color = White,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCard, RoundedCornerShape(4.dp))
                    .padding(12.dp),
                textStyle = TextStyle(
                    color = White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                ),
                cursorBrush = SolidColor(accent.primaryColor),
                singleLine = false,
                maxLines = 3
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(text.trim()) }) {
                Text(
                    text = "SAVE",
                    color = accent.primaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "CANCEL",
                    color = DotInactiveColor,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    )
}

@Composable
fun MindfulPauseDialog(
    app: AppModel,
    secondsRemaining: Int,
    onCancel: () -> Unit,
    onOpenNow: () -> Unit
) {
    val accent = LocalMatrixAccentColor.current

    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = DarkSurface,
        icon = {
            Icon(
                imageVector = Icons.Default.HourglassTop,
                contentDescription = null,
                tint = accent.primaryColor,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "MINDFUL PAUSE",
                color = White,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Is opening ${app.displayLabel.uppercase()} intentional right now?",
                    color = OffWhite,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$secondsRemaining",
                    color = accent.primaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onOpenNow) {
                Text(
                    text = "OPEN NOW",
                    color = OffWhite,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = accent.primaryColor, contentColor = Black),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "STAY FOCUSED",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
