package com.matrixlauncher.ui.graphics

/**
 * 5x7 Dot-Matrix Font Bitmap Definition.
 * Each character is represented by 7 rows, where each row is a 5-bit mask (0b00000 to 0b11111).
 * Bit 4 is the leftmost dot (column 0), Bit 0 is the rightmost dot (column 4).
 */
object DotMatrixFont {
    const val GLYPH_WIDTH = 5
    const val GLYPH_HEIGHT = 7

    val GLYPHS = mapOf(
        // Digits 0-9
        '0' to intArrayOf(
            0b01110,
            0b10001,
            0b10011,
            0b10101,
            0b11001,
            0b10001,
            0b01110
        ),
        '1' to intArrayOf(
            0b00100,
            0b01100,
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b01110
        ),
        '2' to intArrayOf(
            0b01110,
            0b10001,
            0b00001,
            0b00010,
            0b00100,
            0b01000,
            0b11111
        ),
        '3' to intArrayOf(
            0b11110,
            0b00001,
            0b00001,
            0b01110,
            0b00001,
            0b00001,
            0b11110
        ),
        '4' to intArrayOf(
            0b00010,
            0b00110,
            0b01010,
            0b10010,
            0b11111,
            0b00010,
            0b00010
        ),
        '5' to intArrayOf(
            0b11111,
            0b10000,
            0b11110,
            0b00001,
            0b00001,
            0b10001,
            0b01110
        ),
        '6' to intArrayOf(
            0b00110,
            0b01000,
            0b10000,
            0b11110,
            0b10001,
            0b10001,
            0b01110
        ),
        '7' to intArrayOf(
            0b11111,
            0b00001,
            0b00010,
            0b00100,
            0b01000,
            0b01000,
            0b01000
        ),
        '8' to intArrayOf(
            0b01110,
            0b10001,
            0b10001,
            0b01110,
            0b10001,
            0b10001,
            0b01110
        ),
        '9' to intArrayOf(
            0b01110,
            0b10001,
            0b10001,
            0b01111,
            0b00001,
            0b00010,
            0b01100
        ),

        // Uppercase Letters A-Z
        'A' to intArrayOf(
            0b01110,
            0b10001,
            0b10001,
            0b11111,
            0b10001,
            0b10001,
            0b10001
        ),
        'B' to intArrayOf(
            0b11110,
            0b10001,
            0b10001,
            0b11110,
            0b10001,
            0b10001,
            0b11110
        ),
        'C' to intArrayOf(
            0b01110,
            0b10001,
            0b10000,
            0b10000,
            0b10000,
            0b10001,
            0b01110
        ),
        'D' to intArrayOf(
            0b11100,
            0b10010,
            0b10001,
            0b10001,
            0b10001,
            0b10010,
            0b11100
        ),
        'E' to intArrayOf(
            0b11111,
            0b10000,
            0b10000,
            0b11110,
            0b10000,
            0b10000,
            0b11111
        ),
        'F' to intArrayOf(
            0b11111,
            0b10000,
            0b10000,
            0b11110,
            0b10000,
            0b10000,
            0b10000
        ),
        'G' to intArrayOf(
            0b01110,
            0b10001,
            0b10000,
            0b10111,
            0b10001,
            0b10001,
            0b01110
        ),
        'H' to intArrayOf(
            0b10001,
            0b10001,
            0b10001,
            0b11111,
            0b10001,
            0b10001,
            0b10001
        ),
        'I' to intArrayOf(
            0b01110,
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b01110
        ),
        'J' to intArrayOf(
            0b00111,
            0b00010,
            0b00010,
            0b00010,
            0b00010,
            0b10010,
            0b01100
        ),
        'K' to intArrayOf(
            0b10001,
            0b10010,
            0b10100,
            0b11000,
            0b10100,
            0b10010,
            0b10001
        ),
        'L' to intArrayOf(
            0b10000,
            0b10000,
            0b10000,
            0b10000,
            0b10000,
            0b10000,
            0b11111
        ),
        'M' to intArrayOf(
            0b10001,
            0b11011,
            0b10101,
            0b10101,
            0b10001,
            0b10001,
            0b10001
        ),
        'N' to intArrayOf(
            0b10001,
            0b11001,
            0b10101,
            0b10011,
            0b10001,
            0b10001,
            0b10001
        ),
        'O' to intArrayOf(
            0b01110,
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b01110
        ),
        'P' to intArrayOf(
            0b11110,
            0b10001,
            0b10001,
            0b11110,
            0b10000,
            0b10000,
            0b10000
        ),
        'Q' to intArrayOf(
            0b01110,
            0b10001,
            0b10001,
            0b10001,
            0b10101,
            0b10010,
            0b01101
        ),
        'R' to intArrayOf(
            0b11110,
            0b10001,
            0b10001,
            0b11110,
            0b10100,
            0b10010,
            0b10001
        ),
        'S' to intArrayOf(
            0b01111,
            0b10000,
            0b10000,
            0b01110,
            0b00001,
            0b00001,
            0b11110
        ),
        'T' to intArrayOf(
            0b11111,
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b00100
        ),
        'U' to intArrayOf(
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b01110
        ),
        'V' to intArrayOf(
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b01010,
            0b00100
        ),
        'W' to intArrayOf(
            0b10001,
            0b10001,
            0b10001,
            0b10101,
            0b10101,
            0b11011,
            0b10001
        ),
        'X' to intArrayOf(
            0b10001,
            0b10001,
            0b01010,
            0b00100,
            0b01010,
            0b10001,
            0b10001
        ),
        'Y' to intArrayOf(
            0b10001,
            0b10001,
            0b01010,
            0b00100,
            0b00100,
            0b00100,
            0b00100
        ),
        'Z' to intArrayOf(
            0b11111,
            0b00001,
            0b00010,
            0b00100,
            0b01000,
            0b10000,
            0b11111
        ),

        // Symbols & Punctuation
        ':' to intArrayOf(
            0b00000,
            0b00100,
            0b00100,
            0b00000,
            0b00100,
            0b00100,
            0b00000
        ),
        '.' to intArrayOf(
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b00110,
            0b00110
        ),
        '-' to intArrayOf(
            0b00000,
            0b00000,
            0b00000,
            0b11111,
            0b00000,
            0b00000,
            0b00000
        ),
        '_' to intArrayOf(
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b11111
        ),
        '/' to intArrayOf(
            0b00001,
            0b00010,
            0b00010,
            0b00100,
            0b01000,
            0b01000,
            0b10000
        ),
        '%' to intArrayOf(
            0b11001,
            0b11010,
            0b00100,
            0b01000,
            0b10000,
            0b01011,
            0b10011
        ),
        '>' to intArrayOf(
            0b10000,
            0b01000,
            0b00100,
            0b00010,
            0b00100,
            0b01000,
            0b10000
        ),
        '<' to intArrayOf(
            0b00001,
            0b00010,
            0b00100,
            0b01000,
            0b00100,
            0b00010,
            0b00001
        ),
        '#' to intArrayOf(
            0b01010,
            0b01010,
            0b11111,
            0b01010,
            0b11111,
            0b01010,
            0b01010
        ),
        '+' to intArrayOf(
            0b00000,
            0b00100,
            0b00100,
            0b11111,
            0b00100,
            0b00100,
            0b00000
        ),
        '!' to intArrayOf(
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b00000,
            0b00100
        ),
        '?' to intArrayOf(
            0b01110,
            0b10001,
            0b00001,
            0b00010,
            0b00100,
            0b00000,
            0b00100
        ),
        '(' to intArrayOf(
            0b00010,
            0b00100,
            0b01000,
            0b01000,
            0b01000,
            0b00100,
            0b00010
        ),
        ')' to intArrayOf(
            0b01000,
            0b00100,
            0b00010,
            0b00010,
            0b00010,
            0b00100,
            0b01000
        ),
        ' ' to intArrayOf(
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b00000
        )
    )

    private val UNKNOWN_GLYPH = intArrayOf(
        0b11111,
        0b10001,
        0b10101,
        0b10101,
        0b10001,
        0b10001,
        0b11111
    )

    /**
     * Look up glyph bitmask for a character (case-insensitive).
     */
    fun getGlyph(char: Char): IntArray {
        val upper = char.uppercaseChar()
        return GLYPHS[upper] ?: UNKNOWN_GLYPH
    }

    /**
     * Check if a specific dot at (col, row) is active for the character.
     * col: 0..4 (left to right)
     * row: 0..6 (top to bottom)
     */
    fun isDotActive(char: Char, col: Int, row: Int): Boolean {
        if (col !in 0 until GLYPH_WIDTH || row !in 0 until GLYPH_HEIGHT) return false
        val glyph = getGlyph(char)
        val rowMask = glyph[row]
        val bitShift = (GLYPH_WIDTH - 1) - col
        return ((rowMask shr bitShift) and 1) == 1
    }
}
