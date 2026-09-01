package com.matrixlauncher.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.BatteryInfo
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.calculateDotMatrixTextHeight
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.calculateDotMatrixTextWidth
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotBar
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotMatrixText
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.White
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DotMatrixClock(
    modifier: Modifier = Modifier,
    is24Hour: Boolean = true,
    batteryInfo: BatteryInfo = BatteryInfo(),
    showBatteryBar: Boolean = true
) {
    val accent = LocalMatrixAccentColor.current
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
    val dateFormatter = remember { SimpleDateFormat("EEE, MMM dd", Locale.US) }
    val amPmFormatter = remember { SimpleDateFormat("a", Locale.US) }

    val timeString = timeFormatter.format(currentTime)
    val dateString = dateFormatter.format(currentTime).uppercase(Locale.US)
    val amPmString = if (!is24Hour) amPmFormatter.format(currentTime).uppercase(Locale.US) else ""

    val density = LocalDensity.current
    val clockDotRadius = with(density) { 3.5.dp.toPx() }
    val clockDotSpacing = with(density) { 9.dp.toPx() }
    val clockCharSpacing = with(density) { 14.dp.toPx() }

    val dateDotRadius = with(density) { 1.5.dp.toPx() }
    val dateDotSpacing = with(density) { 4.2.dp.toPx() }
    val dateCharSpacing = with(density) { 6.5.dp.toPx() }

    val clockWidth = calculateDotMatrixTextWidth(timeString.length, clockDotRadius, clockDotSpacing, clockCharSpacing)
    val clockHeight = calculateDotMatrixTextHeight(clockDotRadius, clockDotSpacing)

    val dateWidth = calculateDotMatrixTextWidth(dateString.length, dateDotRadius, dateDotSpacing, dateCharSpacing)
    val dateHeight = calculateDotMatrixTextHeight(dateDotRadius, dateDotSpacing)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Date Header (No inactive background dots to avoid halo)
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

        Spacer(modifier = Modifier.height(10.dp))

        // Big Time Clock (Pure crisp glowing white active dots)
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Canvas(
                modifier = Modifier
                    .width(with(density) { clockWidth.toDp() })
                    .height(with(density) { clockHeight.toDp() })
            ) {
                drawDotMatrixText(
                    text = timeString,
                    topLeft = Offset.Zero,
                    dotRadius = clockDotRadius,
                    dotSpacing = clockDotSpacing,
                    charSpacing = clockCharSpacing,
                    activeColor = White,
                    inactiveColor = null
                )
            }

            if (!is24Hour && amPmString.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                val amPmWidth = calculateDotMatrixTextWidth(amPmString.length, dateDotRadius, dateDotSpacing, dateCharSpacing)
                Canvas(
                    modifier = Modifier
                        .width(with(density) { amPmWidth.toDp() })
                        .height(with(density) { dateHeight.toDp() })
                        .padding(bottom = 4.dp)
                ) {
                    drawDotMatrixText(
                        text = amPmString,
                        topLeft = Offset.Zero,
                        dotRadius = dateDotRadius,
                        dotSpacing = dateDotSpacing,
                        charSpacing = dateCharSpacing,
                        activeColor = accent.primaryColor,
                        inactiveColor = null
                    )
                }
            }
        }

        // Battery Dot Bar
        if (showBatteryBar) {
            Spacer(modifier = Modifier.height(12.dp))
            val batDotRadius = with(density) { 1.8.dp.toPx() }
            val batDotSpacing = with(density) { 6.5.dp.toPx() }
            val batTotalDots = 10
            val batActiveDots = batteryInfo.activeDotsOnTenScale
            val batBarWidth = with(density) { ((batTotalDots - 1) * batDotSpacing + batDotRadius * 2).toDp() }
            val batBarHeight = with(density) { (batDotRadius * 2).toDp() }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val batText = "BAT ${batteryInfo.level}%"
                val batTextWidth = calculateDotMatrixTextWidth(batText.length, dateDotRadius, dateDotSpacing, dateCharSpacing)

                Canvas(
                    modifier = Modifier
                        .width(with(density) { batTextWidth.toDp() })
                        .height(with(density) { dateHeight.toDp() })
                ) {
                    drawDotMatrixText(
                        text = batText,
                        topLeft = Offset.Zero,
                        dotRadius = dateDotRadius,
                        dotSpacing = dateDotSpacing,
                        charSpacing = dateCharSpacing,
                        activeColor = if (batteryInfo.level <= 15) AccentColor.CRIMSON.primaryColor else White,
                        inactiveColor = null
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Canvas(
                    modifier = Modifier
                        .width(batBarWidth)
                        .height(batBarHeight)
                ) {
                    drawDotBar(
                        totalDots = batTotalDots,
                        activeDots = batActiveDots,
                        topLeft = Offset.Zero,
                        dotRadius = batDotRadius,
                        dotSpacing = batDotSpacing,
                        activeColor = if (batteryInfo.isCharging) accent.primaryColor else White,
                        inactiveColor = androidx.compose.ui.graphics.Color(0xFF161616)
                    )
                }
            }
        }
    }
}
