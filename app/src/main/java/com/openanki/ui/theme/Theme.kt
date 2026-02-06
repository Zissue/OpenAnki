package com.openanki.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Teal,
    onPrimary = Paper,
    secondary = Sun,
    onSecondary = Ink,
    tertiary = Coral,
    onTertiary = Paper,
    background = Fog,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Color(0xFFF0F3F7),
    onSurfaceVariant = Slate,
    outline = Color(0xFFCED4DA)
)

private val DarkColors = darkColorScheme(
    primary = Mint,
    onPrimary = Night,
    secondary = Sun,
    onSecondary = Night,
    tertiary = Coral,
    onTertiary = Night,
    background = Night,
    onBackground = Paper,
    surface = NightSurface,
    onSurface = Paper,
    surfaceVariant = NightElevated,
    onSurfaceVariant = Color(0xFFC2C8D4),
    outline = Color(0xFF2B3345)
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
