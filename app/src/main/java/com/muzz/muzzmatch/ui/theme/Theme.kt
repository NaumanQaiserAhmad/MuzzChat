package com.example.muzzchat.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Pink = Color(0xFFE91E63)
private val Background = Color(0xFFF6F7FB)
private val IncomingBubble = Color(0xFFE9EEF5)

private val LightColors = lightColorScheme(
    primary = Pink,
    onPrimary = Color.White,
    background = Background,
    surface = Color.White,
    secondaryContainer = IncomingBubble
)

private val DarkColors = darkColorScheme()

@Composable
fun MuzzTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = androidx.compose.material3.Typography(),
        shapes = androidx.compose.material3.Shapes(),
        content = content
    )
}
