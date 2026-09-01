package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = WhiteThemePrimary,
    onPrimary = WhiteThemeOnPrimary,
    primaryContainer = WhiteThemePrimaryContainer,
    onPrimaryContainer = WhiteThemeOnPrimaryContainer,
    secondary = WhiteThemeSecondary,
    onSecondary = WhiteThemeOnSecondary,
    secondaryContainer = WhiteThemeSecondaryContainer,
    onSecondaryContainer = WhiteThemeOnSecondaryContainer,
    tertiary = WhiteThemeTertiary,
    onTertiary = WhiteThemeOnTertiary,
    tertiaryContainer = WhiteThemeTertiaryContainer,
    onTertiaryContainer = WhiteThemeOnTertiaryContainer,
    background = WhiteThemeBackground,
    onBackground = WhiteThemeOnBackground,
    surface = WhiteThemeSurface,
    onSurface = WhiteThemeOnSurface,
    surfaceVariant = WhiteThemeSurfaceVariant,
    onSurfaceVariant = WhiteThemeOnSurfaceVariant,
    outline = WhiteThemeOutline,
    outlineVariant = WhiteThemeOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkThemePrimary,
    onPrimary = DarkThemeOnPrimary,
    primaryContainer = DarkThemePrimaryContainer,
    onPrimaryContainer = DarkThemeOnPrimaryContainer,
    secondary = DarkThemeSecondary,
    onSecondary = DarkThemeOnSecondary,
    secondaryContainer = DarkThemeSecondaryContainer,
    onSecondaryContainer = DarkThemeOnSecondaryContainer,
    tertiary = DarkThemeTertiary,
    background = DarkThemeBackground,
    onBackground = DarkThemeOnBackground,
    surface = DarkThemeSurface,
    onSurface = DarkThemeOnSurface,
    surfaceVariant = DarkThemeSurfaceVariant,
    onSurfaceVariant = DarkThemeOnSurfaceVariant,
    outline = DarkThemeOutline,
    outlineVariant = DarkThemeOutlineVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our food palette for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
