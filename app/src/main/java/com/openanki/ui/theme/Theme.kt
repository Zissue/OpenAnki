package com.openanki.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Sage,
    onPrimary = Paper,
    secondary = Moss,
    onSecondary = Paper,
    tertiary = Clay,
    onTertiary = Ink,
    background = Fog,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Sand,
    onSurfaceVariant = Slate,
    outline = Mist
)

private val DarkColors = darkColorScheme(
    primary = Sage,
    onPrimary = Night,
    secondary = Clay,
    onSecondary = Night,
    tertiary = Moss,
    onTertiary = Night,
    background = Night,
    onBackground = Paper,
    surface = NightSurface,
    onSurface = Paper,
    surfaceVariant = NightElevated,
    onSurfaceVariant = Mist,
    outline = Color(0xFF2F332A)
)

@Composable
fun OpenAnkiTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = OpenAnkiTypography,
        content = content
    )
}
