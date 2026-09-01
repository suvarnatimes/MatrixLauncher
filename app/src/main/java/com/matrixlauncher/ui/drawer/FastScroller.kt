package com.matrixlauncher.ui.drawer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.calculateDotMatrixTextHeight
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.calculateDotMatrixTextWidth
import com.matrixlauncher.ui.graphics.DotMatrixCanvas.drawDotMatrixGlyph
import com.matrixlauncher.ui.theme.DarkSurface
import com.matrixlauncher.ui.theme.DotInactiveColor
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.White

private val ALPHABET = listOf('#') + ('A'..'Z').toList()

@Composable
fun FastScroller(
    modifier: Modifier = Modifier,
    availableLetters: Set<Char>,
    onLetterSelected: (Char) -> Unit,
    onDragStateChanged: (Boolean) -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    var totalHeightPx by remember { mutableStateOf(1f) }
    var selectedLetter by remember { mutableStateOf<Char?>(null) }
    var isDragging by remember { mutableStateOf(false) }

    fun processTouch(yOffset: Float) {
        val clampedY = yOffset.coerceIn(0f, totalHeightPx)
        val itemHeight = totalHeightPx / ALPHABET.size
        val index = (clampedY / itemHeight).toInt().coerceIn(0, ALPHABET.lastIndex)
        val letter = ALPHABET[index]
        if (selectedLetter != letter) {
            selectedLetter = letter
            onLetterSelected(letter)
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(28.dp)
            .onGloballyPositioned { coordinates ->
                totalHeightPx = coordinates.size.height.toFloat()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        isDragging = true
                        onDragStateChanged(true)
                        processTouch(offset.y)
                        tryAwaitRelease()
                        isDragging = false
                        selectedLetter = null
                        onDragStateChanged(false)
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        onDragStateChanged(true)
                        processTouch(offset.y)
                    },
                    onDragEnd = {
                        isDragging = false
                        selectedLetter = null
                        onDragStateChanged(false)
                    },
                    onDragCancel = {
                        isDragging = false
                        selectedLetter = null
                        onDragStateChanged(false)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        processTouch(change.position.y)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ALPHABET.forEach { letter ->
                val isAvailable = availableLetters.contains(letter)
                val isCurrent = selectedLetter == letter

                Text(
                    text = letter.toString(),
                    color = when {
                        isCurrent -> accent.primaryColor
                        isAvailable -> White.copy(alpha = 0.7f)
                        else -> DotInactiveColor.copy(alpha = 0.4f)
                    },
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Floating Dot Matrix Letter Preview Overlay
        if (isDragging && selectedLetter != null) {
            DotMatrixLetterPreview(
                char = selectedLetter!!,
                accentColor = accent,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset { IntOffset(x = -160, y = 0) }
            )
        }
    }
}

@Composable
fun DotMatrixLetterPreview(
    char: Char,
    accentColor: AccentColor,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val dotRadius = with(density) { 3.5.dp.toPx() }
    val dotSpacing = with(density) { 9.dp.toPx() }

    val widthDp = with(density) { ((4 * dotSpacing) + dotRadius * 2).toDp() }
    val heightDp = with(density) { ((6 * dotSpacing) + dotRadius * 2).toDp() }

    Box(
        modifier = modifier
            .background(DarkSurface, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .width(widthDp)
                .height(heightDp)
        ) {
            drawDotMatrixGlyph(
                char = char,
                topLeft = Offset.Zero,
                dotRadius = dotRadius,
                dotSpacing = dotSpacing,
                activeColor = accentColor.primaryColor,
                inactiveColor = DotInactiveColor
            )
        }
    }
}
