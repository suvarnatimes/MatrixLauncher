package com.matrixlauncher.ui.graphics

import android.graphics.Bitmap
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.matrixlauncher.domain.model.DotShape

/**
 * Algorithmic Image-to-DotMatrix Pixelator.
 * Converts arbitrary bitmap images (PNG, JPEG, logos) into authentic glowing LED dot matrix art.
 */
object DotMatrixPixelator {

    /**
     * Converts a source bitmap into a 16x16 boolean grid where true represents an illuminated LED dot.
     */
    fun pixelateToDotGrid(bitmap: Bitmap, gridResolution: Int = 16): Array<BooleanArray> {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, gridResolution, gridResolution, true)
        val grid = Array(gridResolution) { BooleanArray(gridResolution) }

        val pixels = IntArray(gridResolution * gridResolution)
        scaledBitmap.getPixels(pixels, 0, gridResolution, 0, 0, gridResolution, gridResolution)

        for (y in 0 until gridResolution) {
            for (x in 0 until gridResolution) {
                val pixel = pixels[y * gridResolution + x]
                val alpha = (pixel shr 24) and 0xFF
                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF

                // Relative luminance calculation: 0.299R + 0.587G + 0.114B
                val luminance = (0.299f * red + 0.587f * green + 0.114f * blue) / 255f

                // Dot is active if visible and has sufficient luminance or solid icon shape
                val isVisible = alpha > 80
                val isBright = luminance > 0.35f || (alpha > 180 && luminance > 0.2f)

                grid[y][x] = isVisible && isBright
            }
        }

        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle()
        }

        return grid
    }

    /**
     * Draws the pixelated dot grid onto a Compose DrawScope.
     */
    fun DrawScope.drawPixelatedDotGrid(
        grid: Array<BooleanArray>,
        dotShape: DotShape,
        activeColor: Color,
        inactiveColor: Color
    ) {
        val gridRows = grid.size
        val gridCols = grid[0].size

        val dotW = size.width / gridCols
        val dotH = size.height / gridRows
        val radius = (dotW.coerceAtMost(dotH) * 0.36f)

        for (row in 0 until gridRows) {
            val line = grid[row]
            for (col in 0 until gridCols) {
                val isActive = line[col]
                val cx = col * dotW + dotW / 2f
                val cy = row * dotH + dotH / 2f
                val color = if (isActive) activeColor else inactiveColor

                when (dotShape) {
                    DotShape.CIRCLE -> {
                        drawCircle(color = color, radius = radius, center = Offset(cx, cy))
                    }
                    DotShape.SQUARE -> {
                        val s = radius * 1.8f
                        drawRect(color = color, topLeft = Offset(cx - s / 2, cy - s / 2), size = Size(s, s))
                    }
                    DotShape.ROUNDED_CRT -> {
                        val s = radius * 1.8f
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(cx - s / 2, cy - s / 2),
                            size = Size(s, s),
                            cornerRadius = CornerRadius(radius * 0.5f, radius * 0.5f)
                        )
                    }
                }
            }
        }
    }
}
