package com.example.injector.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = IcebergCyan,
    secondary = IcebergNavy,
    tertiary = IcebergWhiteTransparent,
    background = IcebergDarkBlue,
    surface = IcebergDarkBlue,
    onPrimary = IcebergDarkBlue,
    onSecondary = IcebergCyan,
    onTertiary = IcebergCyan,
    onBackground = IcebergCyan,
    onSurface = IcebergCyan,
)

private val LightColorScheme = lightColorScheme(
    primary = IcebergCyan,
    secondary = IcebergNavy,
    tertiary = IcebergWhiteTransparent,
    background = IcebergDarkBlue, // Force Dark/Iceberg look even in light mode for now
    surface = IcebergDarkBlue,
    onPrimary = IcebergDarkBlue,
    onSecondary = IcebergCyan,
    onTertiary = IcebergCyan,
    onBackground = IcebergCyan,
    onSurface = IcebergCyan,
)

@Composable
fun InjectorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to enforce Iceberg Tech look
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