package com.matrixlauncher.ui.common

import com.matrixlauncher.domain.model.AppModel

object FuzzySearch {

    data class MatchResult(
        val isMatch: Boolean,
        val score: Int,
        val matchedIndices: List<Int> = emptyList()
    )

    /**
     * Calculates fuzzy match score between query and text.
     * Higher score indicates a better match.
     */
    fun match(query: String, text: String): MatchResult {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) {
            return MatchResult(isMatch = true, score = 0)
        }

        val q = trimmedQuery.lowercase()
        val t = text.lowercase()

        // 1. Exact match bonus
        if (t == q) {
            return MatchResult(
                isMatch = true,
                score = 1000,
                matchedIndices = text.indices.toList()
            )
        }

        // 2. Exact prefix bonus
        if (t.startsWith(q)) {
            return MatchResult(
                isMatch = true,
                score = 800 - (t.length - q.length),
                matchedIndices = q.indices.toList()
            )
        }

        // 3. Word start bonus (e.g. "gc" matches "Google Chrome")
        val words = text.split(" ", "_", "-", ".")
        val acronym = words.mapNotNull { it.firstOrNull()?.lowercaseChar() }.joinToString("")
        if (acronym.startsWith(q)) {
            return MatchResult(isMatch = true, score = 600)
        }

        // 4. Subsequence fuzzy match with consecutive scoring & boundary bonuses
        var qIdx = 0
        var score = 0
        var consecutive = 0
        val matchedIndices = mutableListOf<Int>()

        for (tIdx in t.indices) {
            if (qIdx < q.length && t[tIdx] == q[qIdx]) {
                matchedIndices.add(tIdx)
                var charScore = 10

                // Consecutive match bonus
                if (consecutive > 0) {
                    charScore += (consecutive * 15)
                }
                consecutive++

                // Word boundary bonus
                val isWordBoundary = tIdx == 0 ||
                        t[tIdx - 1] == ' ' ||
                        t[tIdx - 1] == '_' ||
                        t[tIdx - 1] == '-' ||
                        (text[tIdx].isUpperCase() && text[tIdx - 1].isLowerCase())

                if (isWordBoundary) {
                    charScore += 40
                }

                score += charScore
                qIdx++
            } else {
                consecutive = 0
            }
        }

        val isFullMatch = qIdx == q.length
        return if (isFullMatch) {
            // Penalize long text distances
            val lengthPenalty = (t.length - q.length) * 2
            MatchResult(
                isMatch = true,
                score = (score - lengthPenalty).coerceAtLeast(1),
                matchedIndices = matchedIndices
            )
        } else {
            MatchResult(isMatch = false, score = -1)
        }
    }

    /**
     * Filters and sorts an app list based on query score.
     */
    fun filterApps(apps: List<AppModel>, query: String): List<AppModel> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return apps
        }

        return apps
            .map { app ->
                val labelMatch = match(trimmed, app.displayLabel)
                val pkgMatch = match(trimmed, app.packageName)
                val bestScore = maxOf(labelMatch.score, pkgMatch.score)
                val isMatch = labelMatch.isMatch || pkgMatch.isMatch
                Triple(app, isMatch, bestScore)
            }
            .filter { it.second }
            .sortedByDescending { it.third }
            .map { it.first }
    }
}
