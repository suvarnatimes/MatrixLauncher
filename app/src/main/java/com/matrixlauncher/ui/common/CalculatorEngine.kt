package com.matrixlauncher.ui.common

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Lightweight, robust math expression evaluator for the launcher search bar.
 */
object CalculatorEngine {

    private val mathSymbols = setOf('+', '-', '*', '/', '%', '^', '(', ')')
    private val decimalFormat = DecimalFormat("0.########", DecimalFormatSymbols(Locale.US))

    fun isMathExpression(query: String): Boolean {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return false
        // Must contain at least one digit and at least one math operator
        val hasDigit = trimmed.any { it.isDigit() }
        val hasOperator = trimmed.any { it in mathSymbols }
        val validCharsOnly = trimmed.all { it.isDigit() || it in mathSymbols || it == '.' || it == ' ' }

        return hasDigit && hasOperator && validCharsOnly
    }

    fun evaluate(query: String): String? {
        if (!isMathExpression(query)) return null
        return try {
            val sanitized = query.replace(" ", "").replace("%", "*0.01")
            val result = parseExpression(sanitized)
            if (result.isNaN() || result.isInfinite()) null
            else decimalFormat.format(result)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseExpression(expression: String): Double {
        var pos = -1
        var ch = 0

        fun nextChar() {
            ch = if (++pos < expression.length) expression[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parseFactor(): Double {
            if (eat('+'.code)) return +parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression(expression) // Recursion handled inside parseExpression wrapper
                eat(')'.code)
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                x = expression.substring(startPos, pos).toDouble()
            } else {
                throw RuntimeException("Unexpected char: " + ch.toChar())
            }

            if (eat('^'.code)) x = Math.pow(x, parseFactor())
            return x
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Division by zero")
                        x /= divisor
                    }
                    else -> return x
                }
            }
        }

        nextChar()
        var x = parseTerm()
        while (true) {
            when {
                eat('+'.code) -> x += parseTerm()
                eat('-'.code) -> x -= parseTerm()
                else -> {
                    if (pos < expression.length) throw RuntimeException("Unexpected char: " + ch.toChar())
                    return x
                }
            }
        }
    }
}
