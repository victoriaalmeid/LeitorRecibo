package com.example.leitorrecibo.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1A237E),
    secondary = Color(0xFF3949AB),
    tertiary = Color(0xFF5C6BC0)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1A237E),
    secondary = Color(0xFF3949AB),
    tertiary = Color(0xFF5C6BC0),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White
)

@Composable
fun LeitorReciboTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}