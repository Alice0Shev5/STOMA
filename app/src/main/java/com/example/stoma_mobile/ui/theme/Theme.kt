package com.example.stoma_mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = StomaBlue,
    onPrimary = Color.White,
    secondary = StomaTeal,
    background = StomaSurface,
    surface = Color.White,
    error = StomaRed
)

private val DarkColors = darkColorScheme(
    primary = StomaBlue,
    secondary = StomaTeal,
    background = StomaSurfaceDark,
    surface = Color(0xFF1C1F24),
    error = StomaRed
)

@Composable
fun StomaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = StomaTypography,
        content = content
    )
}