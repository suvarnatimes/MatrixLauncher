package com.matrixlauncher.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matrixlauncher.domain.model.BatteryInfo
import com.matrixlauncher.domain.model.CalendarEventInfo
import com.matrixlauncher.domain.model.HomeWidgetType
import com.matrixlauncher.domain.model.WeatherInfo
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.calculateDotMatrixTextHeight
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.calculateDotMatrixTextWidth
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotBar
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotMatrixText
import com.matrixlauncher.ui.home.DotMatrixClock
import com.matrixlauncher.ui.theme.DarkSurface
import com.matrixlauncher.ui.theme.DotInactiveColor
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.OffWhite
import com.matrixlauncher.ui.theme.SurfaceCard
import com.matrixlauncher.ui.theme.White

@Composable
fun DotMatrixWidgetsContainer(
    enabledWidgets: List<HomeWidgetType>,
    batteryInfo: BatteryInfo,
    weatherInfo: WeatherInfo,
    calendarEvent: CalendarEventInfo,
    scratchpadNote: String,
    is24Hour: Boolean,
    showBatteryBar: Boolean,
    onCalendarClick: () -> Unit,
    onScratchpadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        enabledWidgets.forEach { widgetType ->
            when (widgetType) {
                HomeWidgetType.CLOCK -> {
                    DotMatrixClock(
                        is24Hour = is24Hour,
                        batteryInfo = batteryInfo,
                        showBatteryBar = showBatteryBar
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

                HomeWidgetType.QUOTE -> {
                    DotMatrixQuoteWidget()
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
    val density = LocalDensity.current
    val accent = LocalMatrixAccentColor.current

    Box(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .background(DarkSurface.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
            .border(1.dp, DotInactiveColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = accent.primaryColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                val weatherText = "${weatherInfo.temperatureCelsius}°C // ${weatherInfo.condition.label}"
                val wRadius = with(density) { 1.2.dp.toPx() }
                val wSpacing = with(density) { 3.6.dp.toPx() }
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
                        activeColor = White,
                        inactiveColor = DotInactiveColor.copy(alpha = 0.3f)
                    )
                }
            }

            Text(
                text = "HUM 62%",
                color = DotInactiveColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
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
            .fillMaxWidth(0.9f)
            .background(DarkSurface.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
            .border(1.dp, DotInactiveColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Storage Meter
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SdStorage,
                    contentDescription = null,
                    tint = OffWhite,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "STORAGE 64%",
                    color = OffWhite,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }

            // RAM Meter
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
                    fontSize = 10.sp
                )
            }

            // Battery %
            val bDotRadius = with(density) { 1.2.dp.toPx() }
            val bDotSpacing = with(density) { 3.5.dp.toPx() }
            val bTotal = 6
            val bActive = ((batteryInfo.level / 100f) * bTotal).toInt()

            Canvas(modifier = Modifier.width(22.dp).height(8.dp)) {
                drawDotBar(
                    totalDots = bTotal,
                    activeDots = bActive,
                    topLeft = Offset.Zero,
                    dotRadius = bDotRadius,
                    dotSpacing = bDotSpacing,
                    activeColor = if (batteryInfo.isCharging) accent.primaryColor else White,
                    inactiveColor = DotInactiveColor.copy(alpha = 0.4f)
                )
            }
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
            .fillMaxWidth(0.9f)
            .background(SurfaceCard.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .border(1.dp, DotInactiveColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "NOTE // ${note.uppercase()}",
            color = accent.primaryColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp,
            maxLines = 1
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
            .fillMaxWidth(0.9f)
            .background(SurfaceCard.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .border(1.dp, accent.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
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
                color = if (calendarEvent.hasEvent) White else DotInactiveColor,
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
    val quotes = listOf(
        "SIMPLICITY IS THE ULTIMATE SOPHISTICATION",
        "FOCUS ON WHAT MATTERS // CUT THE NOISE",
        "RETRO SPEED // MODERN PERFORMANCE",
        "BATTERY EFFICIENCY MEETS CYBERPUNK"
    )
    val quote = quotes.first()

    Box(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .background(DarkSurface.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .border(1.dp, DotInactiveColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "“$quote”",
            color = OffWhite.copy(alpha = 0.8f),
            fontFamily = FontFamily.Monospace,
            fontSize = 9.5.sp,
            letterSpacing = 0.5.sp,
            maxLines = 1
        )
    }
}
