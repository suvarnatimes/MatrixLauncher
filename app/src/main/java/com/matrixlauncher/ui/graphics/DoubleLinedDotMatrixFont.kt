package com.matrixlauncher.ui.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Procedural Double-Lined Bold Dot-Matrix Font Engine.
 * Every letter is rendered with 2-dot thick bold strokes as seen in the reference design.
 */
object DoubleLinedDotMatrixFont {

    data class GlyphData(val width: Int, val height: Int, val rows: Array<String>)

    // Double-lined bold character bitmasks (7 rows tall, 6 to 9 cols wide)
    val GLYPHS: Map<Char, GlyphData> = mapOf(
        'A' to GlyphData(7, 7, arrayOf(
            "0111110",
            "1100011",
            "1100011",
            "1111111",
            "1111111",
            "1100011",
            "1100011"
        )),
        'B' to GlyphData(7, 7, arrayOf(
            "1111110",
            "1100011",
            "1111110",
            "1111110",
            "1100011",
            "1100011",
            "1111110"
        )),
        'C' to GlyphData(7, 7, arrayOf(
            "0111110",
            "1111111",
            "1100000",
            "1100000",
            "1100000",
            "1111111",
            "0111110"
        )),
        'D' to GlyphData(7, 7, arrayOf(
            "1111100",
            "1100110",
            "1100011",
            "1100011",
            "1100011",
            "1100110",
            "1111100"
        )),
        'E' to GlyphData(6, 7, arrayOf(
            "111111",
            "111111",
            "110000",
            "111110",
            "110000",
            "111111",
            "111111"
        )),
        'F' to GlyphData(6, 7, arrayOf(
            "111111",
            "111111",
            "110000",
            "111110",
            "110000",
            "110000",
            "110000"
        )),
        'G' to GlyphData(7, 7, arrayOf(
            "0111110",
            "1111111",
            "1100000",
            "1101111",
            "1100011",
            "1111111",
            "0111110"
        )),
        'H' to GlyphData(7, 7, arrayOf(
            "1100011",
            "1100011",
            "1111111",
            "1111111",
            "1100011",
            "1100011",
            "1100011"
        )),
        'I' to GlyphData(6, 7, arrayOf(
            "111111",
            "111111",
            "001100",
            "001100",
            "001100",
            "111111",
            "111111"
        )),
        'J' to GlyphData(6, 7, arrayOf(
            "000011",
            "000011",
            "000011",
            "000011",
            "110011",
            "111111",
            "011110"
        )),
        'K' to GlyphData(7, 7, arrayOf(
            "1100011",
            "1100110",
            "1111100",
            "1111000",
            "1111100",
            "1100110",
            "1100011"
        )),
        'L' to GlyphData(6, 7, arrayOf(
            "110000",
            "110000",
            "110000",
            "110000",
            "110000",
            "111111",
            "111111"
        )),
        'M' to GlyphData(8, 7, arrayOf(
            "11000011",
            "11100111",
            "11111111",
            "11011011",
            "11000011",
            "11000011",
            "11000011"
        )),
        'N' to GlyphData(7, 7, arrayOf(
            "1100011",
            "1110011",
            "1111011",
            "1101111",
            "1100111",
            "1100011",
            "1100011"
        )),
        'O' to GlyphData(7, 7, arrayOf(
            "0111110",
            "1111111",
            "1100011",
            "1100011",
            "1100011",
            "1111111",
            "0111110"
        )),
        'P' to GlyphData(7, 7, arrayOf(
            "1111110",
            "1100011",
            "1100011",
            "1111110",
            "1100000",
            "1100000",
            "1100000"
        )),
        'Q' to GlyphData(7, 7, arrayOf(
            "0111110",
            "1100011",
            "1100011",
            "1100011",
            "1101111",
            "1111111",
            "0111111"
        )),
        'R' to GlyphData(7, 7, arrayOf(
            "1111110",
            "1100011",
            "1100011",
            "1111110",
            "1111000",
            "1101100",
            "1100111"
        )),
        'S' to GlyphData(7, 7, arrayOf(
            "0111111",
            "1111111",
            "1100000",
            "0111110",
            "0000011",
            "1111111",
            "1111110"
        )),
        'T' to GlyphData(7, 7, arrayOf(
            "1111111",
            "1111111",
            "0011000",
            "0011000",
            "0011000",
            "0011000",
            "0011000"
        )),
        'U' to GlyphData(7, 7, arrayOf(
            "1100011",
            "1100011",
            "1100011",
            "1100011",
            "1100011",
            "1111111",
            "0111110"
        )),
        'V' to GlyphData(7, 7, arrayOf(
            "1100011",
            "1100011",
            "1100011",
            "0110110",
            "0110110",
            "0011100",
            "0001000"
        )),
        'W' to GlyphData(9, 7, arrayOf(
            "110000011",
            "110000011",
            "110000011",
            "110010011",
            "110111011",
            "111101111",
            "011000110"
        )),
        'X' to GlyphData(7, 7, arrayOf(
            "1100011",
            "0110110",
            "0011100",
            "0011100",
            "0110110",
            "1100011",
            "1100011"
        )),
        'Y' to GlyphData(7, 7, arrayOf(
            "1100011",
            "0110110",
            "0011100",
            "0011000",
            "0011000",
            "0011000",
            "0011000"
        )),
        'Z' to GlyphData(7, 7, arrayOf(
            "1111111",
            "1111111",
            "0000110",
            "0011000",
            "0110000",
            "1111111",
            "1111111"
        )),
        ' ' to GlyphData(4, 7, arrayOf(
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000"
        ))
    )

    fun getGlyph(char: Char): GlyphData {
        val upper = char.uppercaseChar()
        return GLYPHS[upper] ?: GLYPHS[' ' ]!!
    }

    fun DrawScope.drawDoubleLinedGlyph(
        char: Char,
        topLeft: Offset,
        dotRadius: Float,
        dotSpacing: Float,
        activeColor: Color
    ): Float {
        val glyph = getGlyph(char)
        val rows = glyph.rows
        for (r in 0 until glyph.height) {
            val rowStr = rows[r]
            val cy = topLeft.y + r * dotSpacing + dotRadius
            for (c in 0 until glyph.width) {
                if (c < rowStr.length && rowStr[c] == '1') {
                    val cx = topLeft.x + c * dotSpacing + dotRadius
                    drawCircle(
                        color = activeColor,
                        radius = dotRadius,
                        center = Offset(cx, cy)
                    )
                }
            }
        }
        return glyph.width * dotSpacing
    }

    fun DrawScope.drawDoubleLinedText(
        text: String,
        topLeft: Offset,
        dotRadius: Float,
        dotSpacing: Float,
        charSpacing: Float,
        activeColor: Color
    ): Float {
        var currentX = topLeft.x
        for (char in text) {
            val glyphWidth = drawDoubleLinedGlyph(
                char = char,
                topLeft = Offset(currentX, topLeft.y),
                dotRadius = dotRadius,
                dotSpacing = dotSpacing,
                activeColor = activeColor
            )
            currentX += glyphWidth + charSpacing
        }
        return currentX - topLeft.x
    }

    fun calculateDoubleLinedTextWidth(
        text: String,
        dotSpacing: Float,
        charSpacing: Float
    ): Float {
        var totalWidth = 0f
        for ((index, char) in text.withIndex()) {
            val glyph = getGlyph(char)
            totalWidth += glyph.width * dotSpacing
            if (index < text.length - 1) {
                totalWidth += charSpacing
            }
        }
        return totalWidth
    }

    fun calculateDoubleLinedTextHeight(dotRadius: Float, dotSpacing: Float): Float {
        return 6 * dotSpacing + dotRadius * 2
    }
}
