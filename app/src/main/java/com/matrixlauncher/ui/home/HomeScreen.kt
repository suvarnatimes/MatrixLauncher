package com.matrixlauncher.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.BatteryInfo
import com.matrixlauncher.domain.model.CalendarEventInfo
import com.matrixlauncher.domain.model.ScreenTimeStats
import com.matrixlauncher.domain.model.WeatherInfo
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.calculateDotMatrixTextHeight
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.calculateDotMatrixTextWidth
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotArrow
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotBar
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotMatrixText
import com.matrixlauncher.ui.theme.Black
import com.matrixlauncher.ui.theme.DarkSurface
import com.matrixlauncher.ui.theme.DotInactiveColor
import com.matrixlauncher.ui.theme.DotMatrixTheme
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.OffWhite
import com.matrixlauncher.ui.theme.SurfaceCard
import com.matrixlauncher.ui.theme.White

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    pinnedFavorites: List<AppModel>,
    batteryInfo: BatteryInfo,
    screenTimeStats: ScreenTimeStats,
    weatherInfo: WeatherInfo,
    calendarEvent: CalendarEventInfo,
    is24Hour: Boolean,
    showBatteryBar: Boolean,
    showScreenTime: Boolean,
    showScratchpad: Boolean,
    scratchpadNote: String,
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
                .padding(16.dp)
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Area (Clock + Date + Weather + Calendar + Scratchpad)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                DotMatrixClock(
                    is24Hour = is24Hour,
                    batteryInfo = batteryInfo,
                    showBatteryBar = showBatteryBar
                )

                // Weather Glance Row
                if (weatherInfo.isAvailable) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val weatherText = weatherInfo.formattedString
                    val wRadius = with(density) { 1.3.dp.toPx() }
                    val wSpacing = with(density) { 3.8.dp.toPx() }
                    val wWidth = calculateDotMatrixTextWidth(weatherText.length, wRadius, wSpacing)
                    val wHeight = calculateDotMatrixTextHeight(wRadius, wSpacing)

                    Canvas(
                        modifier = Modifier
                            .width(with(density) { wWidth.toDp() })
                            .height(with(density) { wHeight.toDp() })
                    ) {
                        drawDotMatrixText(
                            text = weatherText,
                            topLeft = Offset.Zero,
                            dotRadius = wRadius,
                            dotSpacing = wSpacing,
                            activeColor = OffWhite.copy(alpha = 0.8f),
                            inactiveColor = DotInactiveColor
                        )
                    }
                }

                // Upcoming Calendar Event Glance
                if (calendarEvent.hasEvent) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .background(SurfaceCard.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .clickable(onClick = onCalendarClick)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = accent.primaryColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = calendarEvent.formattedGlance,
                            color = White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                // Screen Time Quick Glance Bar
                if (showScreenTime && screenTimeStats.hasPermission) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val stDotRadius = with(density) { 1.5.dp.toPx() }
                    val stDotSpacing = with(density) { 6.dp.toPx() }
                    val stTotalDots = 10
                    val stActiveDots = (screenTimeStats.progressRatioToSixHours * stTotalDots).toInt()
                    val stBarWidth = with(density) { ((stTotalDots - 1) * stDotSpacing + stDotRadius * 2).toDp() }
                    val stBarHeight = with(density) { (stDotRadius * 2).toDp() }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val stText = "USAGE ${screenTimeStats.formattedDuration}"
                        val textWidth = calculateDotMatrixTextWidth(stText.length, stDotRadius, stDotSpacing)
                        val textHeight = calculateDotMatrixTextHeight(stDotRadius, stDotSpacing)

                        Canvas(
                            modifier = Modifier
                                .width(with(density) { textWidth.toDp() })
                                .height(with(density) { textHeight.toDp() })
                        ) {
                            drawDotMatrixText(
                                text = stText,
                                topLeft = Offset.Zero,
                                dotRadius = stDotRadius,
                                dotSpacing = stDotSpacing,
                                activeColor = OffWhite,
                                inactiveColor = DotInactiveColor
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Canvas(
                            modifier = Modifier
                                .width(stBarWidth)
                                .height(stBarHeight)
                        ) {
                            drawDotBar(
                                totalDots = stTotalDots,
                                activeDots = stActiveDots,
                                topLeft = Offset.Zero,
                                dotRadius = stDotRadius,
                                dotSpacing = stDotSpacing,
                                activeColor = accent.primaryColor,
                                inactiveColor = DotInactiveColor
                            )
                        }
                    }
                }

                // Scratchpad Sticky Note
                if (showScratchpad) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(SurfaceCard.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .border(1.dp, DotInactiveColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .clickable { isEditingScratchpad = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = scratchpadNote.uppercase(),
                            color = accent.primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            // Pinned Favorites List (Center)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
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
                    .padding(bottom = 18.dp),
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

                Spacer(modifier = Modifier.height(6.dp))

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

@Composable
private fun FavoriteAppItem(
    app: AppModel,
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
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.width(10.dp).height(10.dp)) {
            drawCircle(
                color = accentColor.primaryColor,
                radius = 2.5.dp.toPx(),
                center = Offset(size.width / 2f, size.height / 2f)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = app.displayLabel.uppercase(),
            color = White,
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.5.sp
        )

        if (app.isWorkProfile) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "[W]",
                color = accentColor.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
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
