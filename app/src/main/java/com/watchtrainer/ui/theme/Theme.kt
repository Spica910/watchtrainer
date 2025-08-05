package com.watchtrainer.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

private val DarkColorPalette = Colors(
    primary = md_theme_dark_primary,
    primaryVariant = md_theme_dark_primaryVariant,
    secondary = md_theme_dark_secondary,
    secondaryVariant = md_theme_dark_secondaryVariant,
    background = md_theme_dark_background,
    surface = md_theme_dark_surface,
    error = md_theme_dark_error,
    onPrimary = md_theme_dark_onPrimary,
    onSecondary = md_theme_dark_onSecondary,
    onBackground = md_theme_dark_onBackground,
    onSurface = md_theme_dark_onSurface,
    onError = md_theme_dark_onError
)

@Composable
fun WatchTrainerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        content = content
    )
}