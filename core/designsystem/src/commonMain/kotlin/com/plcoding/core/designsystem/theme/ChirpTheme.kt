package com.plcoding.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun ChirpTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedScheme = if (darkTheme) DarkExtendedColors else LightExtendedColors
    
    CompositionLocalProvider(LocalExtendedColors provides extendedScheme) {
        MaterialTheme(
            colorScheme = colors,
            typography = Typography,
            content = content,
        )
    }
}