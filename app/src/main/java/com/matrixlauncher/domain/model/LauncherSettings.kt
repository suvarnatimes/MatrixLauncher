package com.matrixlauncher.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Supported retro-futuristic LED accent colors.
 */
enum class AccentColor(val label: String, val hexCode: Long, val primaryColor: Color) {
    CRIMSON("Crimson Red", 0xFFFF2E2EL, Color(0xFFFF2E2E)),
    AMBER("Matrix Amber", 0xFFFFAA00L, Color(0xFFFFAA00)),
    EMERALD("Cyber Emerald", 0xFF00E676L, Color(0xFF00E676)),
    CYAN("Neon Cyan", 0xFF00E5FFL, Color(0xFF00E5FF)),
    PURPLE("Retro Purple", 0xFFE040FBL, Color(0xFFE040FB)),
    MONOCHROME("Stark White", 0xFFFFFFFFL, Color(0xFFFFFFFF)),
    CUSTOM("Custom Hex", 0xFFFFFFFFL, Color(0xFFFFFFFF));

    companion object {
        fun fromName(name: String?): AccentColor {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: CRIMSON
        }
    }
}

/**
 * Density of the background procedural dot matrix grid.
 */
enum class DotDensity(val label: String, val spacingDp: Int, val dotRadiusDp: Float) {
    DENSE("Dense (8dp)", 8, 1.2f),
    STANDARD("Standard (12dp)", 12, 1.5f),
    SPARSE("Sparse (16dp)", 16, 1.8f);

    companion object {
        fun fromName(name: String?): DotDensity {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: STANDARD
        }
    }
}

/**
 * Shape of matrix dots across UI and background.
 */
enum class DotShape(val label: String) {
    CIRCLE("Circular Dot"),
    SQUARE("Square Pixel"),
    ROUNDED_SQUARE("CRT Pixel");

    companion object {
        fun fromName(name: String?): DotShape {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: CIRCLE
        }
    }
}

/**
 * Single-handed fast scroller rail position.
 */
enum class ScrollerAlignment(val label: String) {
    RIGHT("Right Edge (Right-Handed)"),
    LEFT("Left Edge (Left-Handed)");

    companion object {
        fun fromName(name: String?): ScrollerAlignment {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: RIGHT
        }
    }
}

/**
 * Action triggered on Home screen double tap.
 */
enum class DoubleTapAction(val label: String) {
    NONE("Disabled"),
    LOCK_SCREEN("Lock Screen"),
    TOGGLE_TORCH("Toggle Flashlight"),
    OPEN_SEARCH("Open Search"),
    OPEN_SETTINGS("Open Settings"),
    OPEN_CAMERA("Open Camera");

    companion object {
        fun fromName(name: String?): DoubleTapAction {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: TOGGLE_TORCH
        }
    }
}

/**
 * Action triggered on Home screen horizontal swipe.
 */
enum class SwipeGestureAction(val label: String) {
    NONE("Disabled"),
    OPEN_CAMERA("Open Camera"),
    OPEN_SEARCH("Open Search"),
    OPEN_SETTINGS("Open Settings");

    companion object {
        fun fromName(name: String?): SwipeGestureAction {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: NONE
        }
    }
}

/**
 * Default web search provider.
 */
enum class WebSearchProvider(val label: String, val searchUrl: String) {
    GOOGLE("Google", "https://www.google.com/search?q="),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q="),
    BRAVE("Brave Search", "https://search.brave.com/search?q="),
    WIKIPEDIA("Wikipedia", "https://en.wikipedia.org/wiki/Special:Search?search=");

    companion object {
        fun fromName(name: String?): WebSearchProvider {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: GOOGLE
        }
    }
}

/**
 * Comprehensive user configuration and customization settings.
 */
@Immutable
data class LauncherSettings(
    val accentColor: AccentColor = AccentColor.CRIMSON,
    val customAccentHex: String = "#FF2E2E",
    val dotDensity: DotDensity = DotDensity.STANDARD,
    val dotShape: DotShape = DotShape.CIRCLE,
    val scrollerAlignment: ScrollerAlignment = ScrollerAlignment.RIGHT,
    val doubleTapAction: DoubleTapAction = DoubleTapAction.TOGGLE_TORCH,
    val swipeLeftAction: SwipeGestureAction = SwipeGestureAction.OPEN_CAMERA,
    val swipeRightAction: SwipeGestureAction = SwipeGestureAction.NONE,
    val defaultSearchProvider: WebSearchProvider = WebSearchProvider.GOOGLE,
    val is24HourClock: Boolean = true,
    val showScreenTimeGlance: Boolean = true,
    val showBatteryDotBar: Boolean = true,
    val showScratchpad: Boolean = true,
    val scratchpadNote: String = "TAP TO WRITE SCRATCHPAD NOTE_",
    val hapticsEnabled: Boolean = true,
    val agslShaderEnabled: Boolean = true,
    val enableCrtScanlines: Boolean = false,
    val autoFocusSearch: Boolean = false,
    val maxFavoritesCount: Int = 8,
    val mindfulPauseSeconds: Int = 0,
    val mindfulAppPackages: Set<String> = emptySet(),
    val favoritePackageNames: Set<String> = emptySet()
) {
    /**
     * Resolves effective primary color considering custom hex.
     */
    val effectiveAccentColor: Color
        get() = if (accentColor == AccentColor.CUSTOM) {
            try {
                val cleanHex = customAccentHex.removePrefix("#")
                val colorLong = cleanHex.toLong(16) or 0xFF000000L
                Color(colorLong)
            } catch (e: Exception) {
                AccentColor.CRIMSON.primaryColor
            }
        } else {
            accentColor.primaryColor
        }
}
