package com.matrixlauncher.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.matrixlauncher.ui.theme.AccentAmber
import com.matrixlauncher.ui.theme.AccentCyan
import com.matrixlauncher.ui.theme.AccentEmerald
import com.matrixlauncher.ui.theme.AccentPurple
import com.matrixlauncher.ui.theme.AccentRed
import com.matrixlauncher.ui.theme.AccentWhite
import java.util.UUID

enum class AccentColor(val displayName: String, val primaryColor: Color) {
    CRIMSON("CRIMSON", AccentRed),
    EMERALD("EMERALD", AccentEmerald),
    AMBER("AMBER", AccentAmber),
    CYAN("CYAN", AccentCyan),
    PURPLE("PURPLE", AccentPurple),
    WHITE("WHITE", AccentWhite),
    CUSTOM("CUSTOM", AccentRed)
}

enum class DotDensity(val label: String, val dotRadiusDp: Float, val spacingDp: Float) {
    COMPACT("COMPACT", 1.0f, 3.2f),
    STANDARD("STANDARD", 1.5f, 4.5f),
    SPARSE("SPARSE", 2.0f, 6.0f)
}

enum class DotShape(val label: String) {
    CIRCLE("CIRCLE ●"),
    SQUARE("SQUARE ■"),
    ROUNDED_CRT("ROUNDED CRT ▪")
}

enum class IconStyle(val label: String, val description: String) {
    DOT_MATRIX_STOCK("RETRO DOT-MATRIX", "Procedural pixel icons for stock apps + monograms"),
    DOT_MATRIX_MONOGRAM("DOT MONOGRAMS", "Minimal glowing dot-matrix letter badges"),
    SYSTEM_DEFAULT("SYSTEM ICONS", "Android standard application icons"),
    TEXT_ONLY("TEXT ONLY", "Ultra-minimal typography only")
}

enum class HomeWidgetType(val title: String, val description: String) {
    COMBINED_HERO("HERO CLOCK & NAME", "Date + Big Name + Time + Battery (as in screenshot)"),
    JESUS_CROSS("JESUS CROSS", "Double-bordered outline cross, triple crosses, or radiant Celtic cross"),
    CUSTOM_NAME("STANDALONE BIG NAME", "3 styles: Bold Monolith, Framed Badge, or Cyber Flanked"),
    CLOCK("STANDALONE CLOCK", "3 styles: Classic Digital, Stacked 2-Line, or Compact"),
    WEATHER("LIVE WEATHER GLANCE", "Current temperature, LED condition, and humidity"),
    TELEMETRY("DEVICE TELEMETRY", "Storage meter, RAM usage, and battery gauge"),
    SCRATCHPAD("QUICK SCRATCHPAD", "Interactive sticky note on your home screen"),
    CALENDAR("CALENDAR AGENDA", "Live upcoming event banner and countdown"),
    RECENT_APPS("RECENT APPS DOCK", "Quick-access dot matrix row for recent apps"),
    STATUS_BAR_GLANCE("LED STATUS BAR", "Minimalist status indicators & notification glance"),
    QUOTE("DAILY RETRO QUOTE", "Motivational minimalist dot-matrix quote")
}

enum class ScrollerAlignment(val label: String) {
    RIGHT("RIGHT EDGE"),
    LEFT("LEFT EDGE")
}

enum class DoubleTapAction(val label: String) {
    NONE("DO NOTHING"),
    TOGGLE_TORCH("TOGGLE FLASHLIGHT"),
    OPEN_SEARCH("OPEN SEARCH"),
    OPEN_SETTINGS("OPEN SETTINGS"),
    OPEN_CAMERA("OPEN CAMERA"),
    LOCK_SCREEN("LOCK SCREEN")
}

enum class SwipeGestureAction(val label: String) {
    NONE("DO NOTHING"),
    OPEN_CAMERA("OPEN CAMERA"),
    OPEN_SEARCH("OPEN SEARCH"),
    OPEN_SETTINGS("OPEN SETTINGS")
}

enum class WebSearchProvider(val label: String, val searchUrl: String) {
    DUCK_DUCK_GO("DUCKDUCKGO", "https://duckduckgo.com/?q="),
    GOOGLE("GOOGLE", "https://www.google.com/search?q="),
    BRAVE("BRAVE", "https://search.brave.com/search?q="),
    BING("BING", "https://www.bing.com/search?q=")
}

@Immutable
data class PlacedWidget(
    val id: String = UUID.randomUUID().toString(),
    val type: HomeWidgetType,
    val xPercent: Float = 0.5f,
    val yPercent: Float = 0.5f,
    val styleIndex: Int = 0
)

@Immutable
data class LauncherSettings(
    val accentColor: AccentColor = AccentColor.EMERALD,
    val customAccentHex: String = "#00E676",
    val dotDensity: DotDensity = DotDensity.STANDARD,
    val dotShape: DotShape = DotShape.CIRCLE,
    val iconStyle: IconStyle = IconStyle.DOT_MATRIX_STOCK,

    // Placed widgets with free-form position placement
    val placedWidgets: List<PlacedWidget> = listOf(
        PlacedWidget(
            id = "hero_clock",
            type = HomeWidgetType.COMBINED_HERO,
            xPercent = 0.5f,
            yPercent = 0.16f,
            styleIndex = 0
        ),
        PlacedWidget(
            id = "jesus_cross",
            type = HomeWidgetType.JESUS_CROSS,
            xPercent = 0.5f,
            yPercent = 0.56f,
            styleIndex = 0
        )
    ),

    val enabledWidgets: List<HomeWidgetType> = listOf(
        HomeWidgetType.COMBINED_HERO,
        HomeWidgetType.JESUS_CROSS
    ),

    val scrollerAlignment: ScrollerAlignment = ScrollerAlignment.RIGHT,
    val defaultSearchProvider: WebSearchProvider = WebSearchProvider.DUCK_DUCK_GO,

    // Gesture Action Targets
    val swipeDownAction: String = "EXPAND_NOTIFICATIONS",
    val swipeUpAction: String = "OPEN_DRAWER",
    val swipeLeftAction: String = "NONE",
    val swipeRightAction: String = "NONE",
    val doubleTapAction: String = "TOGGLE_TORCH",
    val pinchInAction: String = "OPEN_SETTINGS",
    val pinchOutAction: String = "OPEN_DRAWER",
    val twoFingerSwipeDownAction: String = "OPEN_SEARCH",
    val twoFingerSwipeUpAction: String = "OPEN_DRAWER",

    // Custom Name & Widget Styles
    val customUserName: String = "MICHEL",
    val nameStyleIndex: Int = 0,
    val crossStyleIndex: Int = 0,
    val clockStyleIndex: Int = 0,

    val is24HourClock: Boolean = true,
    val showScreenTimeGlance: Boolean = true,
    val showBatteryDotBar: Boolean = true,
    val showScratchpad: Boolean = true,
    val scratchpadNote: String = "TAP TO WRITE NOTE",
    val hapticsEnabled: Boolean = true,
    val agslShaderEnabled: Boolean = true,
    val enableCrtScanlines: Boolean = true,
    val autoFocusSearch: Boolean = false,
    val maxFavoritesCount: Int = 8,
    val mindfulPauseSeconds: Int = 0,
    val mindfulAppPackages: Set<String> = emptySet(),
    val favoritePackageNames: Set<String> = emptySet()
)
