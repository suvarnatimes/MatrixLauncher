package com.matrixlauncher.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.BatteryInfo
import com.matrixlauncher.domain.model.CalendarEventInfo
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.HomeWidgetType
import com.matrixlauncher.domain.model.IconStyle
import com.matrixlauncher.domain.model.PlacedWidget
import com.matrixlauncher.domain.model.WeatherInfo
import com.matrixlauncher.ui.graphics.DotMatrixAppIcon
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.calculateDotMatrixTextHeight
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.calculateDotMatrixTextWidth
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotBar
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotMatrixText
import com.matrixlauncher.ui.graphics.DoubleLinedDotMatrixFont.calculateDoubleLinedTextHeight
import com.matrixlauncher.ui.graphics.DoubleLinedDotMatrixFont.calculateDoubleLinedTextWidth
import com.matrixlauncher.ui.graphics.DoubleLinedDotMatrixFont.drawDoubleLinedText
import com.matrixlauncher.ui.home.DotMatrixClock
import com.matrixlauncher.ui.theme.Black
import com.matrixlauncher.ui.theme.DarkSurface
import com.matrixlauncher.ui.theme.DividerColor
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.SurfaceCard
import com.matrixlauncher.ui.theme.TextPrimary
import com.matrixlauncher.ui.theme.TextSecondary
import com.matrixlauncher.ui.theme.White
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FreeFormWidgetsContainer(
    placedWidgets: List<PlacedWidget>,
    recentApps: List<AppModel>,
    batteryInfo: BatteryInfo,
    weatherInfo: WeatherInfo,
    calendarEvent: CalendarEventInfo,
    scratchpadNote: String,
    customUserName: String,
    nameStyleIndex: Int,
    crossStyleIndex: Int,
    clockStyleIndex: Int,
    crossSizeScale: Float = 1.35f,
    nameSizeScale: Float = 1.0f,
    timeSizeScale: Float = 1.0f,
    dateSizeScale: Float = 1.0f,
    batterySizeScale: Float = 1.0f,
    bibleVerseIndex: Int = 0,
    is24Hour: Boolean,
    showBatteryBar: Boolean,
    iconStyle: IconStyle,
    dotShape: DotShape,
    onWidgetsChange: (List<PlacedWidget>) -> Unit,
    onAppClick: (AppModel) -> Unit,
    onCalendarClick: () -> Unit,
    onScratchpadClick: () -> Unit,
    onCycleBibleVerse: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isEditMode by remember { mutableStateOf(false) }
    var isCurrentlyDragging by remember { mutableStateOf(false) }
    var showAddWidgetSheet by remember { mutableStateOf(false) }

    val accent = LocalMatrixAccentColor.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val containerWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val containerHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)

        // Alignment Guide Lines (Visible ONLY during Edit Mode / Dragging)
        if (isEditMode) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                // Vertical Center Guide Line
                drawLine(
                    color = accent.primaryColor.copy(alpha = if (isCurrentlyDragging) 0.8f else 0.35f),
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, size.height),
                    strokeWidth = 1.5f,
                    pathEffect = pathEffect
                )

                // Horizontal Center Guide Line
                drawLine(
                    color = accent.primaryColor.copy(alpha = if (isCurrentlyDragging) 0.8f else 0.35f),
                    start = Offset(0f, centerY),
                    end = Offset(size.width, centerY),
                    strokeWidth = 1.5f,
                    pathEffect = pathEffect
                )

                // Center Bullseye Marker
                drawCircle(
                    color = accent.primaryColor.copy(alpha = 0.6f),
                    radius = 8f,
                    center = Offset(centerX, centerY)
                )
            }
        }

        // Edit Mode Top Bar
        AnimatedVisibility(
            visible = isEditMode,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(DarkSurface, RoundedCornerShape(4.dp))
                    .border(1.dp, accent.primaryColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "DRAG TO POSITION // CENTER SNAPS",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    TextButton(onClick = { showAddWidgetSheet = true }) {
                        Text(
                            text = "+ ADD",
                            color = accent.primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = {
                        isEditMode = false
                        isCurrentlyDragging = false
                    }) {
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

        // Render Placed Widgets with True Center Offset & Magnetic Snapping
        placedWidgets.forEach { widget ->
            var currentXPercent by remember(widget.id, widget.xPercent) { mutableFloatStateOf(widget.xPercent) }
            var currentYPercent by remember(widget.id, widget.yPercent) { mutableFloatStateOf(widget.yPercent) }

            var widgetWidthPx by remember(widget.id) { mutableIntStateOf(0) }
            var widgetHeightPx by remember(widget.id) { mutableIntStateOf(0) }

            // Center-anchored coordinate calculation:
            val centerX = currentXPercent * containerWidthPx
            val centerY = currentYPercent * containerHeightPx

            val targetX = (centerX - widgetWidthPx / 2f).roundToInt()
            val targetY = (centerY - widgetHeightPx / 2f).roundToInt()

            Box(
                modifier = Modifier
                    .offset { IntOffset(targetX, targetY) }
                    .onGloballyPositioned { coords ->
                        widgetWidthPx = coords.size.width
                        widgetHeightPx = coords.size.height
                    }
                    .combinedClickable(
                        onClick = {
                            if (!isEditMode) {
                                when (widget.type) {
                                    HomeWidgetType.SCRATCHPAD -> onScratchpadClick()
                                    HomeWidgetType.CALENDAR -> onCalendarClick()
                                    HomeWidgetType.BIBLE_VERSE -> onCycleBibleVerse()
                                    else -> {}
                                }
                            }
                        },
                        onLongClick = { isEditMode = !isEditMode }
                    )
                    .then(
                        if (isEditMode) {
                            Modifier.pointerInput(widget.id) {
                                detectDragGestures(
                                    onDragStart = {
                                        isCurrentlyDragging = true
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        var newX = (currentXPercent + dragAmount.x / containerWidthPx).coerceIn(0.08f, 0.92f)
                                        var newY = (currentYPercent + dragAmount.y / containerHeightPx).coerceIn(0.06f, 0.94f)

                                        // Magnetic Center Snapping (within 2.5% of midline)
                                        if (abs(newX - 0.5f) < 0.025f) newX = 0.5f
                                        if (abs(newY - 0.5f) < 0.025f) newY = 0.5f

                                        currentXPercent = newX
                                        currentYPercent = newY
                                    },
                                    onDragEnd = {
                                        isCurrentlyDragging = false
                                        val updated = placedWidgets.map {
                                            if (it.id == widget.id) it.copy(xPercent = currentXPercent, yPercent = currentYPercent) else it
                                        }
                                        onWidgetsChange(updated)
                                    },
                                    onDragCancel = {
                                        isCurrentlyDragging = false
                                    }
                                )
                            }
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.then(
                        if (isEditMode) {
                            Modifier
                                .background(DarkSurface.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                .border(1.dp, accent.primaryColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        } else Modifier
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    when (widget.type) {
                        HomeWidgetType.COMBINED_HERO -> {
                            CombinedHeroDateNameTimeWidget(
                                userName = customUserName,
                                batteryInfo = batteryInfo,
                                is24Hour = is24Hour,
                                nameScale = nameSizeScale,
                                timeScale = timeSizeScale,
                                dateScale = dateSizeScale,
                                batteryScale = batterySizeScale
                            )
                        }

                        HomeWidgetType.JESUS_CROSS -> {
                            DoubleBorderOrnateCrossWidget(
                                styleIndex = crossStyleIndex,
                                scaleMultiplier = crossSizeScale
                            )
                        }

                        HomeWidgetType.BIBLE_VERSE -> {
                            DotMatrixBibleVerseWidget(
                                verseIndex = bibleVerseIndex,
                                onCycleVerse = onCycleBibleVerse
                            )
                        }

                        HomeWidgetType.CUSTOM_NAME -> {
                            StandaloneNameWidget(
                                name = customUserName,
                                styleIndex = nameStyleIndex,
                                scaleMultiplier = nameSizeScale
                            )
                        }

                        HomeWidgetType.CLOCK -> {
                            StandaloneClockWidget(
                                is24Hour = is24Hour,
                                styleIndex = clockStyleIndex,
                                batteryInfo = batteryInfo,
                                showBatteryBar = showBatteryBar
                            )
                        }

                        HomeWidgetType.WEATHER -> {
                            DotMatrixWeatherWidget(weatherInfo = weatherInfo)
                        }

                        HomeWidgetType.TELEMETRY -> {
                            DotMatrixTelemetryWidget(batteryInfo = batteryInfo)
                        }

                        HomeWidgetType.SCRATCHPAD -> {
                            DotMatrixScratchpadWidget(note = scratchpadNote, onClick = onScratchpadClick)
                        }

                        HomeWidgetType.CALENDAR -> {
                            DotMatrixCalendarWidget(calendarEvent = calendarEvent, onClick = onCalendarClick)
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
                            DotMatrixStatusBarGlanceWidget(batteryInfo = batteryInfo, is24Hour = is24Hour)
                        }

                        HomeWidgetType.QUOTE -> {
                            DotMatrixQuoteWidget()
                        }
                    }

                    // Edit Mode Move & Delete Icons
                    if (isEditMode) {
                        IconButton(
                            onClick = {
                                val updated = placedWidgets.filter { it.id != widget.id }
                                onWidgetsChange(updated)
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                                .background(AccentColor.CRIMSON.primaryColor, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.OpenWith,
                            contentDescription = "Drag to Move",
                            tint = accent.primaryColor,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .size(16.dp)
                        )
                    }
                }
            }
        }

        // Add Widget Sheet Modal
        if (showAddWidgetSheet) {
            AddPlacedWidgetBottomSheet(
                onDismiss = { showAddWidgetSheet = false },
                onAddWidget = { type ->
                    val newWidget = PlacedWidget(
                        type = type,
                        xPercent = 0.5f,
                        yPercent = 0.5f
                    )
                    onWidgetsChange(placedWidgets + newWidget)
                    showAddWidgetSheet = false
                }
            )
        }
    }
}

/**
 * EXACT Hero Widget with Per-Element Scalability:
 * Line 1: Date in Accent Green (scaled by dateScale)
 * Line 2: DOUBLE-LINED BOLD User Name (scaled by nameScale)
 * Line 3: Digital Time (scaled by timeScale)
 * Line 4: Battery Line & Dot Gauge (scaled by batteryScale)
 */
@Composable
fun CombinedHeroDateNameTimeWidget(
    userName: String,
    batteryInfo: BatteryInfo,
    is24Hour: Boolean = true,
    nameScale: Float = 1.0f,
    timeScale: Float = 1.0f,
    dateScale: Float = 1.0f,
    batteryScale: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current
    val density = LocalDensity.current
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            val nextSecondMillis = 1000L - (System.currentTimeMillis() % 1000L)
            delay(nextSecondMillis.coerceAtLeast(100L))
        }
    }

    val timePattern = if (is24Hour) "HH:mm" else "hh:mm"
    val timeFormatter = remember(is24Hour) { SimpleDateFormat(timePattern, Locale.US) }
    val dateFormatter = remember { SimpleDateFormat("EEE MMM dd", Locale.US) }

    val timeString = timeFormatter.format(currentTime)
    val dateString = dateFormatter.format(currentTime).uppercase(Locale.US)
    val displayName = remember(userName) { userName.ifBlank { "MICHEL" }.uppercase() }

    // DOUBLE-LINED BOLD NAME METRICS (Large & 2-dot thick bold strokes!)
    val nScale = nameScale.coerceIn(0.6f, 2.4f)
    val nameDotRadius = with(density) { (2.4f * nScale).dp.toPx() }
    val nameDotSpacing = with(density) { (6.0f * nScale).dp.toPx() }
    val nameCharSpacing = with(density) { (8.0f * nScale).dp.toPx() }

    val nameWidth = calculateDoubleLinedTextWidth(displayName, nameDotSpacing, nameCharSpacing)
    val nameHeight = calculateDoubleLinedTextHeight(nameDotRadius, nameDotSpacing)

    // COMPACT SMALL TIME METRICS
    val tScale = timeScale.coerceIn(0.6f, 2.4f)
    val timeDotRadius = with(density) { (1.5f * tScale).dp.toPx() }
    val timeDotSpacing = with(density) { (4.0f * tScale).dp.toPx() }
    val timeCharSpacing = with(density) { (6.0f * tScale).dp.toPx() }

    val timeWidth = calculateDotMatrixTextWidth(timeString.length, timeDotRadius, timeDotSpacing, timeCharSpacing)
    val timeHeight = calculateDotMatrixTextHeight(timeDotRadius, timeDotSpacing)

    // COMPACT SMALL DATE METRICS
    val dScale = dateScale.coerceIn(0.6f, 2.4f)
    val dateDotRadius = with(density) { (1.0f * dScale).dp.toPx() }
    val dateDotSpacing = with(density) { (2.8f * dScale).dp.toPx() }
    val dateCharSpacing = with(density) { (4.2f * dScale).dp.toPx() }

    val dateWidth = calculateDotMatrixTextWidth(dateString.length, dateDotRadius, dateDotSpacing, dateCharSpacing)
    val dateHeight = calculateDotMatrixTextHeight(dateDotRadius, dateDotSpacing)

    Column(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Line 1: Date in Accent Color
        Canvas(
            modifier = Modifier
                .width(with(density) { dateWidth.toDp() })
                .height(with(density) { dateHeight.toDp() })
        ) {
            drawDotMatrixText(
                text = dateString,
                topLeft = Offset.Zero,
                dotRadius = dateDotRadius,
                dotSpacing = dateDotSpacing,
                charSpacing = dateCharSpacing,
                activeColor = accent.primaryColor,
                inactiveColor = null
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Line 2: DOUBLE-LINED BOLD User Name in Pure White (2-dot thick strokes)
        Canvas(
            modifier = Modifier
                .width(with(density) { nameWidth.toDp() })
                .height(with(density) { nameHeight.toDp() })
        ) {
            drawDoubleLinedText(
                text = displayName,
                topLeft = Offset.Zero,
                dotRadius = nameDotRadius,
                dotSpacing = nameDotSpacing,
                charSpacing = nameCharSpacing,
                activeColor = White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Line 3: Digital Time
        Canvas(
            modifier = Modifier
                .width(with(density) { timeWidth.toDp() })
                .height(with(density) { timeHeight.toDp() })
        ) {
            drawDotMatrixText(
                text = timeString,
                topLeft = Offset.Zero,
                dotRadius = timeDotRadius,
                dotSpacing = timeDotSpacing,
                charSpacing = timeCharSpacing,
                activeColor = White,
                inactiveColor = null
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Line 4: Small BAT 94%------
        val bScale = batteryScale.coerceIn(0.6f, 2.4f)
        val batDotRadius = with(density) { (1.0f * bScale).dp.toPx() }
        val batDotSpacing = with(density) { (2.8f * bScale).dp.toPx() }
        val batCharSpacing = with(density) { (4.2f * bScale).dp.toPx() }

        val batText = "BAT ${batteryInfo.level}%"
        val batTextWidth = calculateDotMatrixTextWidth(batText.length, batDotRadius, batDotSpacing, batCharSpacing)
        val barDotRadius = with(density) { (1.0f * bScale).dp.toPx() }
        val barDotSpacing = with(density) { (3.8f * bScale).dp.toPx() }
        val totalBarDots = 6
        val activeBarDots = ((batteryInfo.level / 100f) * totalBarDots).toInt().coerceIn(1, totalBarDots)
        val barWidth = with(density) { ((totalBarDots - 1) * barDotSpacing + barDotRadius * 2).toDp() }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Canvas(
                modifier = Modifier
                    .width(with(density) { batTextWidth.toDp() })
                    .height(with(density) { (6 * batDotSpacing + batDotRadius * 2).toDp() })
            ) {
                drawDotMatrixText(
                    text = batText,
                    topLeft = Offset.Zero,
                    dotRadius = batDotRadius,
                    dotSpacing = batDotSpacing,
                    charSpacing = batCharSpacing,
                    activeColor = TextSecondary,
                    inactiveColor = null
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            Canvas(
                modifier = Modifier
                    .width(barWidth)
                    .height(with(density) { (barDotRadius * 2).toDp() })
            ) {
                drawDotBar(
                    totalDots = totalBarDots,
                    activeDots = activeBarDots,
                    topLeft = Offset.Zero,
                    dotRadius = barDotRadius,
                    dotSpacing = barDotSpacing,
                    activeColor = TextSecondary,
                    inactiveColor = Color(0xFF1E1E1E)
                )
            }
        }
    }
}

/**
 * 3-BOX BIBLE VERSE WIDGET:
 * Box 1: Focus / Highlight Word in Large Double-Lined Bold Letters (White/Gold)
 * Box 2: Sacred Verse Text (Center Aligned, Glowing Accent Color)
 * Box 3: Chapter & Verse Citation Reference (Center Aligned, Compact Line)
 */
data class BibleVerseEntry(val keyword: String, val verseText: String, val citation: String)

val BIBLE_VERSES_LIBRARY = listOf(
    BibleVerseEntry(
        keyword = "FAITH",
        verseText = "NOW FAITH IS CONFIDENCE IN WHAT WE HOPE FOR AND ASSURANCE ABOUT WHAT WE DO NOT SEE",
        citation = "HEBREWS 11:1"
    ),
    BibleVerseEntry(
        keyword = "LOVE",
        verseText = "FOR GOD SO LOVED THE WORLD THAT HE GAVE HIS ONE AND ONLY SON THAT WHOEVER BELIEVES IN HIM SHALL NOT PERISH BUT HAVE ETERNAL LIFE",
        citation = "JOHN 3:16"
    ),
    BibleVerseEntry(
        keyword = "STRENGTH",
        verseText = "I CAN DO ALL THINGS THROUGH CHRIST WHO STRENGTHENS ME",
        citation = "PHILIPPIANS 4:13"
    ),
    BibleVerseEntry(
        keyword = "PEACE",
        verseText = "THE LORD IS MY SHEPHERD I SHALL NOT WANT HE MAKES ME LIE DOWN IN GREEN PASTURES",
        citation = "PSALM 23:1-2"
    ),
    BibleVerseEntry(
        keyword = "TRUST",
        verseText = "TRUST IN THE LORD WITH ALL YOUR HEART AND LEAN NOT ON YOUR OWN UNDERSTANDING",
        citation = "PROVERBS 3:5"
    ),
    BibleVerseEntry(
        keyword = "LIGHT",
        verseText = "YOUR WORD IS A LAMP FOR MY FEET AND A LIGHT ON MY PATH",
        citation = "PSALM 119:105"
    ),
    BibleVerseEntry(
        keyword = "HOPE",
        verseText = "THOSE WHO HOPE IN THE LORD WILL RENEW THEIR STRENGTH THEY WILL SOAR ON WINGS LIKE EAGLES",
        citation = "ISAIAH 40:31"
    ),
    BibleVerseEntry(
        keyword = "GRACE",
        verseText = "FOR IT IS BY GRACE YOU HAVE BEEN SAVED THROUGH FAITH AND THIS IS NOT FROM YOURSELVES IT IS THE GIFT OF GOD",
        citation = "EPHESIANS 2:8"
    )
)

@Composable
fun DotMatrixBibleVerseWidget(
    verseIndex: Int = 0,
    onCycleVerse: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current
    val density = LocalDensity.current

    val currentVerse = BIBLE_VERSES_LIBRARY[abs(verseIndex) % BIBLE_VERSES_LIBRARY.size]

    // Box 1: Double-Lined Highlight Word Metrics
    val keyDotRadius = with(density) { 2.2.dp.toPx() }
    val keyDotSpacing = with(density) { 5.6.dp.toPx() }
    val keyCharSpacing = with(density) { 7.5.dp.toPx() }

    val keyWidth = calculateDoubleLinedTextWidth(currentVerse.keyword, keyDotSpacing, keyCharSpacing)
    val keyHeight = calculateDoubleLinedTextHeight(keyDotRadius, keyDotSpacing)

    // Box 3: Compact Citation Reference Metrics
    val citeDotRadius = with(density) { 0.9.dp.toPx() }
    val citeDotSpacing = with(density) { 2.5.dp.toPx() }
    val citeCharSpacing = with(density) { 3.8.dp.toPx() }

    val citeFormatted = "[ ${currentVerse.citation} ]"
    val citeWidth = calculateDotMatrixTextWidth(citeFormatted.length, citeDotRadius, citeDotSpacing, citeCharSpacing)
    val citeHeight = calculateDotMatrixTextHeight(citeDotRadius, citeDotSpacing)

    Column(
        modifier = modifier
            .width(280.dp)
            .clickable(onClick = onCycleVerse)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // BOX 1: HIGHLIGHTED WORD BOX (Large Double-Lined Bold Letters)
        Box(
            modifier = Modifier
                .background(DarkSurface.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                .border(1.dp, accent.primaryColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .width(with(density) { keyWidth.toDp() })
                    .height(with(density) { keyHeight.toDp() })
            ) {
                drawDoubleLinedText(
                    text = currentVerse.keyword,
                    topLeft = Offset.Zero,
                    dotRadius = keyDotRadius,
                    dotSpacing = keyDotSpacing,
                    charSpacing = keyCharSpacing,
                    activeColor = White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // BOX 2: VERSE BOX (Center Aligned Sacred Text)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCard.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentVerse.verseText,
                color = accent.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                lineHeight = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // BOX 3: CHAPTER & VERSE CITATION DETAILS BOX (Last Line, Smaller Look)
        Box(
            modifier = Modifier
                .background(DarkSurface.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                .border(1.dp, DividerColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .width(with(density) { citeWidth.toDp() })
                    .height(with(density) { citeHeight.toDp() })
            ) {
                drawDotMatrixText(
                    text = citeFormatted,
                    topLeft = Offset.Zero,
                    dotRadius = citeDotRadius,
                    dotSpacing = citeDotSpacing,
                    charSpacing = citeCharSpacing,
                    activeColor = TextSecondary,
                    inactiveColor = null
                )
            }
        }
    }
}

/**
 * Large and Adjustable Jesus Cross Widget:
 * Style 0: Double-Bordered Outline Cross (Exact match to reference screenshot)
 * Style 1: Triple Golgotha Crosses
 * Style 2: Radiant Celtic Beaming Cross
 */
@Composable
fun DoubleBorderOrnateCrossWidget(
    styleIndex: Int = 0,
    scaleMultiplier: Float = 1.35f,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val crossPattern = remember(styleIndex % 3) {
        when (styleIndex % 3) {
            0 -> arrayOf(
                // Style 0: Grand Double-Bordered Outline Cross (Exact match to image)
                "0000001111111000000",
                "0000001000001000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "1111111011101111111",
                "1000000011100000001",
                "1011111111111111101",
                "1011111111111111101",
                "1000000011100000001",
                "1111111011101111111",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001011101000000",
                "0000001000001000000",
                "0000001111111000000"
            )
            1 -> arrayOf(
                // Style 1: Triple Golgotha Crosses
                "00000000011000000000",
                "00000000011000000000",
                "00000000011000000000",
                "00100000011000000100",
                "01110001111110001110",
                "00100000011000000100",
                "00100000011000000100",
                "00100000011000000100",
                "00100000011000000100",
                "00000000011000000000",
                "00000000011000000000",
                "00000000011000000000",
                "00000000011000000000",
                "11111111111111111111"
            )
            else -> arrayOf(
                // Style 2: Radiant Celtic Beaming Cross
                "0000000110000000",
                "0000000110000000",
                "0000110110110000",
                "0011000110001100",
                "0100000110000010",
                "1111111111111111",
                "1111111111111111",
                "0100000110000010",
                "0011000110001100",
                "0000110110110000",
                "0000000110000000",
                "0000000110000000",
                "0000000110000000",
                "0000000110000000",
                "0000011111100000"
            )
        }
    }

    val gridRows = crossPattern.size
    val gridCols = crossPattern[0].length

    val baseSpacingDp = 6.2f * scaleMultiplier.coerceIn(0.6f, 2.4f)
    val baseRadiusDp = 2.0f * scaleMultiplier.coerceIn(0.6f, 2.4f)

    val dotSpacing = with(density) { baseSpacingDp.dp.toPx() }
    val dotRadius = with(density) { baseRadiusDp.dp.toPx() }

    val widgetWidth = with(density) { (gridCols * dotSpacing).toDp() }
    val widgetHeight = with(density) { (gridRows * dotSpacing).toDp() }

    Box(
        modifier = modifier.padding(vertical = 6.dp),
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
                            color = White,
                            radius = dotRadius,
                            center = Offset(cx, cy)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3 Separate Standalone Big Name Designs:
 * Style 0: DOUBLE-LINED BOLD MONOLITH (2-dot thick strokes!)
 * Style 1: Framed LED Badge ([ NAME ])
 * Style 2: Cyber Flanked Line Name (— NAME —)
 */
@Composable
fun StandaloneNameWidget(
    name: String,
    styleIndex: Int = 0,
    scaleMultiplier: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current
    val density = LocalDensity.current
    val rawName = name.ifBlank { "MICHEL" }.uppercase()
    val scale = scaleMultiplier.coerceIn(0.6f, 2.4f)

    if (styleIndex % 3 == 0) {
        // Style 0: DOUBLE-LINED BOLD
        val nameDotRadius = with(density) { (3.0f * scale).dp.toPx() }
        val nameDotSpacing = with(density) { (7.5f * scale).dp.toPx() }
        val nameCharSpacing = with(density) { (10.0f * scale).dp.toPx() }

        val totalWidth = calculateDoubleLinedTextWidth(rawName, nameDotSpacing, nameCharSpacing)
        val totalHeight = calculateDoubleLinedTextHeight(nameDotRadius, nameDotSpacing)

        Box(modifier = modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
            Canvas(
                modifier = Modifier
                    .width(with(density) { totalWidth.toDp() })
                    .height(with(density) { totalHeight.toDp() })
            ) {
                drawDoubleLinedText(
                    text = rawName,
                    topLeft = Offset.Zero,
                    dotRadius = nameDotRadius,
                    dotSpacing = nameDotSpacing,
                    charSpacing = nameCharSpacing,
                    activeColor = White
                )
            }
        }
    } else {
        val formattedName = when (styleIndex % 3) {
            1 -> "[ $rawName ]"
            else -> "— $rawName —"
        }

        val dotRadius = with(density) { (3.2f * scale).dp.toPx() }
        val dotSpacing = with(density) { (8.5f * scale).dp.toPx() }
        val charSpacing = with(density) { (12.0f * scale).dp.toPx() }

        val totalWidth = calculateDotMatrixTextWidth(formattedName.length, dotRadius, dotSpacing, charSpacing)
        val totalHeight = calculateDotMatrixTextHeight(dotRadius, dotSpacing)

        Box(modifier = modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
            Canvas(
                modifier = Modifier
                    .width(with(density) { totalWidth.toDp() })
                    .height(with(density) { totalHeight.toDp() })
            ) {
                drawDotMatrixText(
                    text = formattedName,
                    topLeft = Offset.Zero,
                    dotRadius = dotRadius,
                    dotSpacing = dotSpacing,
                    charSpacing = charSpacing,
                    activeColor = if (styleIndex % 3 == 1) accent.primaryColor else White,
                    inactiveColor = null
                )
            }
        }
    }
}

/**
 * 3 Separate Standalone Clock Designs:
 * Style 0: Classic Horizontal Big Clock (21:41)
 * Style 1: Stacked 2-Line Digital Clock (21 over 41)
 * Style 2: Compact Retro Clock with Seconds (21:41:38)
 */
@Composable
fun StandaloneClockWidget(
    is24Hour: Boolean = true,
    styleIndex: Int = 0,
    batteryInfo: BatteryInfo = BatteryInfo(),
    showBatteryBar: Boolean = true,
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current
    val density = LocalDensity.current
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            val nextSecondMillis = 1000L - (System.currentTimeMillis() % 1000L)
            delay(nextSecondMillis.coerceAtLeast(100L))
        }
    }

    when (styleIndex % 3) {
        0 -> {
            DotMatrixClock(
                is24Hour = is24Hour,
                batteryInfo = batteryInfo,
                showBatteryBar = showBatteryBar
            )
        }
        1 -> {
            val hours = SimpleDateFormat(if (is24Hour) "HH" else "hh", Locale.US).format(currentTime)
            val minutes = SimpleDateFormat("mm", Locale.US).format(currentTime)

            val sDotRadius = with(density) { 3.5.dp.toPx() }
            val sDotSpacing = with(density) { 9.0.dp.toPx() }
            val sCharSpacing = with(density) { 12.dp.toPx() }

            val width = calculateDotMatrixTextWidth(2, sDotRadius, sDotSpacing, sCharSpacing)
            val height = calculateDotMatrixTextHeight(sDotRadius, sDotSpacing)

            Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(modifier = Modifier.width(with(density) { width.toDp() }).height(with(density) { height.toDp() })) {
                    drawDotMatrixText(hours, Offset.Zero, sDotRadius, sDotSpacing, sCharSpacing, accent.primaryColor, null)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier.width(with(density) { width.toDp() }).height(with(density) { height.toDp() })) {
                    drawDotMatrixText(minutes, Offset.Zero, sDotRadius, sDotSpacing, sCharSpacing, White, null)
                }
            }
        }
        else -> {
            val secTime = SimpleDateFormat(if (is24Hour) "HH:mm:ss" else "hh:mm:ss a", Locale.US).format(currentTime).uppercase()
            val cDotRadius = with(density) { 1.6.dp.toPx() }
            val cDotSpacing = with(density) { 4.5.dp.toPx() }
            val cCharSpacing = with(density) { 6.5.dp.toPx() }

            val cWidth = calculateDotMatrixTextWidth(secTime.length, cDotRadius, cDotSpacing, cCharSpacing)
            val cHeight = calculateDotMatrixTextHeight(cDotRadius, cDotSpacing)

            Box(modifier = modifier.padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.width(with(density) { cWidth.toDp() }).height(with(density) { cHeight.toDp() })) {
                    drawDotMatrixText(secTime, Offset.Zero, cDotRadius, cDotSpacing, cCharSpacing, White, null)
                }
            }
        }
    }
}

@Composable
fun DotMatrixWeatherWidget(
    weatherInfo: WeatherInfo,
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current

    Box(
        modifier = modifier
            .width(260.dp)
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
            .width(260.dp)
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

            // Battery Bar
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
            .width(280.dp)
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
            .width(260.dp)
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
            .width(260.dp)
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
            .width(260.dp)
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
            .width(260.dp)
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
fun AddPlacedWidgetBottomSheet(
    onDismiss: () -> Unit,
    onAddWidget: (HomeWidgetType) -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    val sheetState = rememberModalBottomSheetState()

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

            HomeWidgetType.entries.forEach { widget ->
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
