package com.matrixlauncher.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import com.matrixlauncher.domain.model.WeatherInfo
import com.matrixlauncher.ui.graphics.DotMatrixAppIcon
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.calculateDotMatrixTextHeight
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.calculateDotMatrixTextWidth
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotBar
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotMatrixText
import com.matrixlauncher.ui.home.DotMatrixClock
import com.matrixlauncher.ui.theme.Black
import com.matrixlauncher.ui.theme.DarkSurface
import com.matrixlauncher.ui.theme.DividerColor
import com.matrixlauncher.ui.theme.DotInactiveColor
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.OffWhite
import com.matrixlauncher.ui.theme.SurfaceCard
import com.matrixlauncher.ui.theme.TextMuted
import com.matrixlauncher.ui.theme.TextPrimary
import com.matrixlauncher.ui.theme.TextSecondary
import com.matrixlauncher.ui.theme.White

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DotMatrixWidgetsContainer(
    enabledWidgets: List<HomeWidgetType>,
    recentApps: List<AppModel>,
    batteryInfo: BatteryInfo,
    weatherInfo: WeatherInfo,
    calendarEvent: CalendarEventInfo,
    scratchpadNote: String,
    customUserName: String,
    crossStyleIndex: Int,
    is24Hour: Boolean,
    showBatteryBar: Boolean,
    iconStyle: IconStyle,
    dotShape: DotShape,
    onWidgetRemove: (HomeWidgetType) -> Unit,
    onWidgetAdd: (HomeWidgetType) -> Unit,
    onAppClick: (AppModel) -> Unit,
    onCalendarClick: () -> Unit,
    onScratchpadClick: () -> Unit,
    onUpdateUserName: (String) -> Unit = {},
    onCycleCrossStyle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isEditMode by remember { mutableStateOf(false) }
    var showAddWidgetSheet by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Edit Mode Done Bar
        AnimatedVisibility(visible = isEditMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(DarkSurface, RoundedCornerShape(4.dp))
                    .border(1.dp, LocalMatrixAccentColor.current.primaryColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "EDITING HOME WIDGETS",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    TextButton(onClick = { showAddWidgetSheet = true }) {
                        Text(
                            text = "+ ADD",
                            color = LocalMatrixAccentColor.current.primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = { isEditMode = false }) {
                        Text(
                            text = "DONE",
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        enabledWidgets.forEach { widgetType ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            when (widgetType) {
                                HomeWidgetType.SCRATCHPAD -> onScratchpadClick()
                                HomeWidgetType.CALENDAR -> onCalendarClick()
                                HomeWidgetType.CUSTOM_NAME -> showEditNameDialog = true
                                HomeWidgetType.JESUS_CROSS -> onCycleCrossStyle()
                                else -> {}
                            }
                        },
                        onLongClick = { isEditMode = !isEditMode }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (widgetType) {
                    HomeWidgetType.CLOCK -> {
                        DotMatrixClock(
                            is24Hour = is24Hour,
                            batteryInfo = batteryInfo,
                            showBatteryBar = showBatteryBar
                        )
                    }

                    HomeWidgetType.CUSTOM_NAME -> {
                        DotMatrixNameWidget(
                            name = customUserName,
                            onClick = { showEditNameDialog = true }
                        )
                    }

                    HomeWidgetType.JESUS_CROSS -> {
                        DotMatrixJesusCrossWidget(
                            styleIndex = crossStyleIndex,
                            onClick = onCycleCrossStyle
                        )
                    }

                    HomeWidgetType.WEATHER -> {
                        DotMatrixWeatherWidget(
                            weatherInfo = weatherInfo
                        )
                    }

                    HomeWidgetType.TELEMETRY -> {
                        DotMatrixTelemetryWidget(
                            batteryInfo = batteryInfo
                        )
                    }

                    HomeWidgetType.SCRATCHPAD -> {
                        DotMatrixScratchpadWidget(
                            note = scratchpadNote,
                            onClick = onScratchpadClick
                        )
                    }

                    HomeWidgetType.CALENDAR -> {
                        DotMatrixCalendarWidget(
                            calendarEvent = calendarEvent,
                            onClick = onCalendarClick
                        )
                    }

                    HomeWidgetType.RECENT_APPS -> {
                        DotMatrixRecentAppsWidget(
                            recentApps = recentApps,
                            iconStyle = iconStyle,
                            dotShape = dotShape,
                            onAppClick = onAppClick
                        )
                    }

                    HomeWidgetType.STATUS_BAR_GLANCE -> {
                        DotMatrixStatusBarGlanceWidget(
                            batteryInfo = batteryInfo,
                            is24Hour = is24Hour
                        )
                    }

                    HomeWidgetType.QUOTE -> {
                        DotMatrixQuoteWidget()
                    }
                }

                // Long-Press [X] Remove Badge
                if (isEditMode) {
                    IconButton(
                        onClick = { onWidgetRemove(widgetType) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 12.dp)
                            .size(26.dp)
                            .background(AccentColor.CRIMSON.primaryColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Widget",
                            tint = Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Add Widget Sheet Modal
        if (showAddWidgetSheet) {
            AddWidgetBottomSheet(
                currentWidgets = enabledWidgets,
                onDismiss = { showAddWidgetSheet = false },
                onAddWidget = {
                    onWidgetAdd(it)
                    showAddWidgetSheet = false
                }
            )
        }

        // Edit Custom Name Dialog
        if (showEditNameDialog) {
            EditNameDialog(
                currentName = customUserName,
                onDismiss = { showEditNameDialog = false },
                onSave = {
                    onUpdateUserName(it)
                    showEditNameDialog = false
                }
            )
        }
    }
}

/**
 * Large auto-scaling custom user name widget.
 */
@Composable
fun DotMatrixNameWidget(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current
    val density = LocalDensity.current
    val displayName = remember(name) { name.ifBlank { "SUVARNA" }.uppercase() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        val availableWidthPx = with(density) { maxWidth.toPx() * 0.88f }
        val nameLength = displayName.length.coerceAtLeast(1)

        // Dynamic auto-scaling formula
        val computedDotSpacing = (availableWidthPx / (nameLength * 5.8f)).coerceIn(4.0f, 11.5f)
        val computedDotRadius = (computedDotSpacing * 0.38f).coerceIn(1.5f, 4.5f)
        val computedCharSpacing = computedDotSpacing * 1.5f

        val totalWidth = calculateDotMatrixTextWidth(displayName.length, computedDotRadius, computedDotSpacing, computedCharSpacing)
        val totalHeight = calculateDotMatrixTextHeight(computedDotRadius, computedDotSpacing)

        Canvas(
            modifier = Modifier
                .width(with(density) { totalWidth.toDp() })
                .height(with(density) { totalHeight.toDp() })
        ) {
            drawDotMatrixText(
                text = displayName,
                topLeft = Offset.Zero,
                dotRadius = computedDotRadius,
                dotSpacing = computedDotSpacing,
                charSpacing = computedCharSpacing,
                activeColor = White,
                inactiveColor = null
            )
        }
    }
}

/**
 * Large centered Jesus Cross dot-matrix widget featuring 3 styles:
 * 0: Triple Crosses (Golgotha with center tall cross)
 * 1: Radiant Beaming Latin Cross
 * 2: Celtic Halo Cross
 */
@Composable
fun DotMatrixJesusCrossWidget(
    styleIndex: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current
    val density = LocalDensity.current

    // 3 Unique Dot Matrix Cross Patterns
    val crossPattern = remember(styleIndex % 3) {
        when (styleIndex % 3) {
            0 -> arrayOf(
                // Style 0: Triple Crosses (Golgotha 3 Crosses)
                "0000000001000000000",
                "0000000001000000000",
                "0000000001000000000",
                "0010000001000000100",
                "0111000111110001110",
                "0010000001000000100",
                "0010000001000000100",
                "0010000001000000100",
                "0010000001000000100",
                "0000000001000000000",
                "0000000001000000000",
                "0000000001000000000",
                "0000000001000000000",
                "1111111111111111111"
            )
            1 -> arrayOf(
                // Style 1: Radiant Beaming Latin Cross
                "00000000100000000",
                "00000000100000000",
                "01000000100000010",
                "00100000100000100",
                "00011111111111000",
                "00100000100000100",
                "01000000100000010",
                "00000000100000000",
                "00000000100000000",
                "00000000100000000",
                "00000000100000000",
                "00000000100000000",
                "00000000100000000",
                "00000111111100000"
            )
            else -> arrayOf(
                // Style 2: Celtic Halo Cross
                "000000010000000",
                "000000010000000",
                "000011010110000",
                "001100010001100",
                "010000010000010",
                "111111111111111",
                "010000010000010",
                "001100010001100",
                "000011010110000",
                "000000010000000",
                "000000010000000",
                "000000010000000",
                "000000010000000",
                "000001111100000"
            )
        }
    }

    val gridRows = crossPattern.size
    val gridCols = crossPattern[0].length

    val dotSpacing = with(density) { 7.5.dp.toPx() }
    val dotRadius = with(density) { 2.2.dp.toPx() }

    val widgetWidth = with(density) { (gridCols * dotSpacing).toDp() }
    val widgetHeight = with(density) { (gridRows * dotSpacing).toDp() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .width(widgetWidth)
                .height(widgetHeight)
        ) {
            val startX = (size.width - gridCols * dotSpacing) / 2f
            val startY = (size.height - gridRows * dotSpacing) / 2f

            for (r in 0 until gridRows) {
                val rowLine = crossPattern[r]
                for (c in 0 until gridCols) {
                    val isActive = c < rowLine.length && rowLine[c] == '1'
                    if (isActive) {
                        val cx = startX + c * dotSpacing + dotSpacing / 2f
                        val cy = startY + r * dotSpacing + dotSpacing / 2f
                        drawCircle(
                            color = if (r == gridRows - 1 && styleIndex % 3 == 0) accent.primaryColor else White,
                            radius = dotRadius,
                            center = Offset(cx, cy)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    var nameInput by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "CUSTOM NAME BANNER",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter your name to display large in the center of the home screen:",
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                BasicTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceCard, RoundedCornerShape(4.dp))
                        .padding(12.dp),
                    textStyle = TextStyle(
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = SolidColor(accent.primaryColor),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(nameInput.trim())
                }
            ) {
                Text(
                    text = "SET NAME",
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
fun DotMatrixWeatherWidget(
    weatherInfo: WeatherInfo,
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current

    Box(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .background(DarkSurface.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = accent.primaryColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "${weatherInfo.temperatureCelsius}°C // ${weatherInfo.condition.label}",
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "LOCATION: ${weatherInfo.location}",
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }

            Text(
                text = "HUM 62%",
                color = accent.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DotMatrixTelemetryWidget(
    batteryInfo: BatteryInfo,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val accent = LocalMatrixAccentColor.current

    Box(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .background(DarkSurface.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Storage
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SdStorage,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "STORAGE 64%",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // RAM
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = null,
                    tint = accent.primaryColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RAM 4.2GB",
                    color = accent.primaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Battery % Bar
            val bDotRadius = with(density) { 1.2.dp.toPx() }
            val bDotSpacing = with(density) { 3.5.dp.toPx() }
            val bTotal = 6
            val bActive = ((batteryInfo.level / 100f) * bTotal).toInt().coerceIn(1, bTotal)

            Canvas(modifier = Modifier.width(22.dp).height(8.dp)) {
                drawDotBar(
                    totalDots = bTotal,
                    activeDots = bActive,
                    topLeft = Offset.Zero,
                    dotRadius = bDotRadius,
                    dotSpacing = bDotSpacing,
                    activeColor = if (batteryInfo.isCharging) accent.primaryColor else White,
                    inactiveColor = Color(0xFF161616)
                )
            }
        }
    }
}

@Composable
fun DotMatrixRecentAppsWidget(
    recentApps: List<AppModel>,
    iconStyle: IconStyle,
    dotShape: DotShape,
    onAppClick: (AppModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current

    if (recentApps.isEmpty()) return

    Box(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .background(DarkSurface.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "// RECENT APPLICATIONS",
                color = accent.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(recentApps.take(5)) { app ->
                    Row(
                        modifier = Modifier
                            .clickable { onAppClick(app) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DotMatrixAppIcon(
                            app = app,
                            iconStyle = iconStyle,
                            dotShape = dotShape,
                            sizeDp = 20.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = app.displayLabel.uppercase(),
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DotMatrixStatusBarGlanceWidget(
    batteryInfo: BatteryInfo,
    is24Hour: Boolean,
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current

    Box(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .background(DarkSurface.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "⚡ MATRIX OS // ACTIVE",
                color = accent.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "BAT: ${batteryInfo.level}% ${if (batteryInfo.isCharging) "[CHG]" else ""}",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun DotMatrixScratchpadWidget(
    note: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current

    Box(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .background(SurfaceCard.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "NOTE // ${note.uppercase()}",
            color = accent.primaryColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp,
            maxLines = 2
        )
    }
}

@Composable
fun DotMatrixCalendarWidget(
    calendarEvent: CalendarEventInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current

    Box(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .background(SurfaceCard.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .border(1.dp, accent.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = accent.primaryColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (calendarEvent.hasEvent) calendarEvent.formattedGlance else "NO UPCOMING EVENTS // TAP TO OPEN",
                color = if (calendarEvent.hasEvent) TextPrimary else TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun DotMatrixQuoteWidget(
    modifier: Modifier = Modifier
) {
    val quote = "FOCUS ON WHAT MATTERS // CUT THE NOISE"

    Box(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .background(DarkSurface.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "“$quote”",
            color = TextPrimary,
            fontFamily = FontFamily.Monospace,
            fontSize = 9.5.sp,
            letterSpacing = 0.5.sp,
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWidgetBottomSheet(
    currentWidgets: List<HomeWidgetType>,
    onDismiss: () -> Unit,
    onAddWidget: (HomeWidgetType) -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    val sheetState = rememberModalBottomSheetState()

    val availableWidgets = HomeWidgetType.entries.filter { !currentWidgets.contains(it) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "ADD WIDGET TO HOME SCREEN",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            if (availableWidgets.isEmpty()) {
                Text(
                    text = "ALL WIDGETS ARE ALREADY ON HOME SCREEN",
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            } else {
                availableWidgets.forEach { widget ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAddWidget(widget) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = widget.title,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = widget.description,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = accent.primaryColor
                        )
                    }
                }
            }
        }
    }
}
