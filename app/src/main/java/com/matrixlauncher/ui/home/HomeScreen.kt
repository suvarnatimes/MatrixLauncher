package com.matrixlauncher.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
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
import com.matrixlauncher.domain.model.IconStyle
import com.matrixlauncher.domain.model.PlacedWidget
import com.matrixlauncher.domain.model.ScreenTimeStats
import com.matrixlauncher.domain.model.WeatherInfo
import com.matrixlauncher.ui.common.detectMatrixGestures
import com.matrixlauncher.ui.graphics.DotMatrixAppIcon
import com.matrixlauncher.ui.theme.Black
import com.matrixlauncher.ui.theme.DarkSurface
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.SurfaceCard
import com.matrixlauncher.ui.theme.TextPrimary
import com.matrixlauncher.ui.theme.TextSecondary
import com.matrixlauncher.ui.widgets.FreeFormWidgetsContainer

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    pinnedFavorites: List<AppModel>,
    recentApps: List<AppModel> = emptyList(),
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
    customUserName: String,
    nameStyleIndex: Int,
    crossStyleIndex: Int,
    clockStyleIndex: Int,
    iconStyle: IconStyle,
    dotShape: DotShape,
    placedWidgets: List<PlacedWidget>,
    mindfulPendingApp: AppModel?,
    mindfulSecondsRemaining: Int,
    onWidgetsChange: (List<PlacedWidget>) -> Unit,
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit,
    onCalendarClick: () -> Unit,
    onUpdateScratchpadNote: (String) -> Unit,
    onCancelMindfulLaunch: () -> Unit,
    onConfirmMindfulLaunch: () -> Unit,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onTwoFingerSwipeUp: () -> Unit,
    onTwoFingerSwipeDown: () -> Unit,
    onPinchIn: () -> Unit,
    onPinchOut: () -> Unit,
    onDoubleTap: () -> Unit,
    onSetDefaultLauncherClick: () -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    var isEditingScratchpad by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .detectMatrixGestures(
                onSwipeUp = onSwipeUp,
                onSwipeDown = onSwipeDown,
                onSwipeLeft = onSwipeLeft,
                onSwipeRight = onSwipeRight,
                onTwoFingerSwipeUp = onTwoFingerSwipeUp,
                onTwoFingerSwipeDown = onTwoFingerSwipeDown,
                onPinchIn = onPinchIn,
                onPinchOut = onPinchOut,
                onDoubleTap = onDoubleTap
            )
    ) {
        // Free-Form Positioned Widgets Layer (Drag & Drop anywhere on screen)
        FreeFormWidgetsContainer(
            placedWidgets = placedWidgets,
            recentApps = recentApps,
            batteryInfo = batteryInfo,
            weatherInfo = weatherInfo,
            calendarEvent = calendarEvent,
            scratchpadNote = scratchpadNote,
            customUserName = customUserName,
            nameStyleIndex = nameStyleIndex,
            crossStyleIndex = crossStyleIndex,
            clockStyleIndex = clockStyleIndex,
            is24Hour = is24Hour,
            showBatteryBar = showBatteryBar,
            iconStyle = iconStyle,
            dotShape = dotShape,
            onWidgetsChange = onWidgetsChange,
            onAppClick = onAppClick,
            onCalendarClick = onCalendarClick,
            onScratchpadClick = { isEditingScratchpad = true },
            modifier = Modifier.fillMaxSize()
        )

        // Default Launcher Notice Banner (if not default)
        AnimatedVisibility(
            visible = !isDefaultLauncher,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
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
                            color = TextPrimary,
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

        // Pinned Favorites List on Bottom (if any pinned apps exist)
        if (pinnedFavorites.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
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
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconStyle != IconStyle.TEXT_ONLY) {
            DotMatrixAppIcon(
                app = app,
                iconStyle = iconStyle,
                dotShape = dotShape,
                sizeDp = 24.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
        } else {
            Canvas(modifier = Modifier.size(6.dp)) {
                drawCircle(
                    color = accentColor.primaryColor,
                    radius = 2.dp.toPx()
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        Text(
            text = app.displayLabel.uppercase(),
            color = TextPrimary,
            fontFamily = FontFamily.Monospace,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )

        if (app.isWorkProfile) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "[W]",
                color = accentColor.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
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
                color = TextPrimary,
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
                    color = TextPrimary,
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
                    color = TextSecondary,
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
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Is opening ${app.displayLabel.uppercase()} intentional right now?",
                    color = TextSecondary,
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
                    color = TextSecondary,
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
