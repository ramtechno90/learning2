package com.example.menuapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// For this redesign, we will focus on a light theme, similar to the Swiggy app.
private val LightColorScheme = lightColorScheme(
    primary = SwiggyOrange,
    secondary = SwiggyOrangeVariant,
    background = BackgroundGray,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = TextBlack,
    onSurface = TextBlack,
    error = Color.Red
)

@Composable
fun MenuAppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
