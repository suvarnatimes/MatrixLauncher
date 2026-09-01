package com.matrixlauncher.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.DotDensity

val LocalMatrixAccentColor = compositionLocalOf { AccentColor.CRIMSON }
val LocalMatrixDotDensity = compositionLocalOf { DotDensity.STANDARD }

@Composable
fun DotMatrixTheme(
    accentColor: AccentColor = AccentColor.CRIMSON,
    dotDensity: DotDensity = DotDensity.STANDARD,
    content: @Composable () -> Unit
) {
    val primaryColor = accentColor.primaryColor

    val darkColorScheme = darkColorScheme(
        primary = primaryColor,
        onPrimary = Black,
        primaryContainer = DarkSurface,
        onPrimaryContainer = primaryColor,
        secondary = White,
        onSecondary = Black,
        secondaryContainer = SurfaceCard,
        onSecondaryContainer = White,
        tertiary = primaryColor,
        onTertiary = Black,
        background = Black,
        onBackground = White,
        surface = DarkSurface,
        onSurface = White,
        surfaceVariant = SurfaceCard,
        onSurfaceVariant = OffWhite,
        outline = DividerColor,
        error = AccentCrimson,
        onError = White
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(
        LocalMatrixAccentColor provides accentColor,
        LocalMatrixDotDensity provides dotDensity
    ) {
        MaterialTheme(
            colorScheme = darkColorScheme,
            typography = Typography,
            content = content
        )
    }
}
