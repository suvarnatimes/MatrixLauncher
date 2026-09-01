package com.matrixlauncher.ui.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * DrawScope extension functions for rendering dot-matrix fonts, LED indicators, and segmented dot bars.
 */
object DotMatrixCanvas {

    /**
     * Renders a single 5x7 character in dot-matrix form.
     */
    fun DrawScope.drawDotMatrixGlyph(
        char: Char,
        topLeft: Offset,
        dotRadius: Float,
        dotSpacing: Float,
        activeColor: Color,
        inactiveColor: Color? = null
    ) {
        val glyph = DotMatrixFont.getGlyph(char)
        for (row in 0 until DotMatrixFont.GLYPH_HEIGHT) {
            val rowMask = glyph[row]
            val cy = topLeft.y + row * dotSpacing + dotRadius
            for (col in 0 until DotMatrixFont.GLYPH_WIDTH) {
                val cx = topLeft.x + col * dotSpacing + dotRadius
                val bitShift = (DotMatrixFont.GLYPH_WIDTH - 1) - col
                val isActive = ((rowMask shr bitShift) and 1) == 1

                if (isActive) {
                    drawCircle(
                        color = activeColor,
                        radius = dotRadius,
                        center = Offset(cx, cy)
                    )
                } else if (inactiveColor != null && inactiveColor.alpha > 0f) {
                    drawCircle(
                        color = inactiveColor,
                        radius = dotRadius * 0.75f,
                        center = Offset(cx, cy)
                    )
                }
            }
        }
    }

    /**
     * Renders a full text string in 5x7 dot matrix format.
     */
    fun DrawScope.drawDotMatrixText(
        text: String,
        topLeft: Offset,
        dotRadius: Float,
        dotSpacing: Float,
        charSpacing: Float = dotSpacing * 1.5f,
        activeColor: Color,
        inactiveColor: Color? = null
    ): Float {
        var currentX = topLeft.x
        val charWidth = (DotMatrixFont.GLYPH_WIDTH - 1) * dotSpacing + dotRadius * 2

        for (char in text) {
            drawDotMatrixGlyph(
                char = char,
                topLeft = Offset(currentX, topLeft.y),
                dotRadius = dotRadius,
                dotSpacing = dotSpacing,
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
            currentX += charWidth + charSpacing
        }

        return currentX - topLeft.x
    }

    /**
     * Calculates the total width of a text string in dot matrix pixels.
     */
    fun calculateDotMatrixTextWidth(
        textLength: Int,
        dotRadius: Float,
        dotSpacing: Float,
        charSpacing: Float = dotSpacing * 1.5f
    ): Float {
        if (textLength <= 0) return 0f
        val charWidth = (DotMatrixFont.GLYPH_WIDTH - 1) * dotSpacing + dotRadius * 2
        return textLength * charWidth + (textLength - 1) * charSpacing
    }

    /**
     * Calculates the total height of a 5x7 dot matrix glyph.
     */
    fun calculateDotMatrixTextHeight(
        dotRadius: Float,
        dotSpacing: Float
    ): Float {
        return (DotMatrixFont.GLYPH_HEIGHT - 1) * dotSpacing + dotRadius * 2
    }

    /**
     * Renders a segmented horizontal LED dot progress bar (e.g., Battery level or Screen time).
     */
    fun DrawScope.drawDotBar(
        totalDots: Int,
        activeDots: Int,
        topLeft: Offset,
        dotRadius: Float,
        dotSpacing: Float,
        activeColor: Color,
        inactiveColor: Color
    ) {
        val clampedActive = activeDots.coerceIn(0, totalDots)
        for (i in 0 until totalDots) {
            val cx = topLeft.x + i * dotSpacing + dotRadius
            val cy = topLeft.y + dotRadius
            val isActive = i < clampedActive
            drawCircle(
                color = if (isActive) activeColor else inactiveColor,
                radius = if (isActive) dotRadius else dotRadius * 0.85f,
                center = Offset(cx, cy)
            )
        }
    }

    /**
     * Renders an animated pulsing dot-matrix chevron arrow.
     */
    fun DrawScope.drawDotArrow(
        center: Offset,
        dotRadius: Float,
        dotSpacing: Float,
        color: Color,
        pointUp: Boolean = true
    ) {
        // 5-dot chevron pattern
        val offsets = if (pointUp) {
            listOf(
                Offset(-2f * dotSpacing, dotSpacing),
                Offset(-1f * dotSpacing, 0f),
                Offset(0f, -dotSpacing),
                Offset(1f * dotSpacing, 0f),
                Offset(2f * dotSpacing, dotSpacing)
            )
        } else {
            listOf(
                Offset(-2f * dotSpacing, -dotSpacing),
                Offset(-1f * dotSpacing, 0f),
                Offset(0f, dotSpacing),
                Offset(1f * dotSpacing, 0f),
                Offset(2f * dotSpacing, -dotSpacing)
            )
        }

        for (offset in offsets) {
            drawCircle(
                color = color,
                radius = dotRadius,
                center = center + offset
            )
        }
    }
}
