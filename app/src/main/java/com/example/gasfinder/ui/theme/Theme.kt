package com.example.gasfinder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Green,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenDark,
    secondary = Amber,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = AmberLight,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = OnSurfaceLight,
    outline = OutlineLight
)

private val DarkColors = darkColorScheme(
    primary = GreenLight,
    onPrimary = GreenDark,
    secondary = AmberLight,
    onSecondary = GreenDark,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    outline = OutlineDark
)

/**
 * Тема приложения. Светлая и тёмная схемы заданы явно, поэтому интерфейс
 * корректно выглядит и в ночном режиме системы.
 */
@Composable
fun GasFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
