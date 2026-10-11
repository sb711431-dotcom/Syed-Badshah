package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class VyroAccentTheme(val label: String, val primary: Color, val secondary: Color) {
  VIOLET("Electric Violet", VyroViolet, VyroCyan),
  CYAN("Cyber Cyan", VyroCyan, VyroViolet),
  SUNSET("Sunset Flare", VyroAccentSunset, VyroAccentAmber),
  EMERALD("Matrix Green", VyroAccentEmerald, VyroCyan)
}

val LocalVyroAccent = staticCompositionLocalOf { VyroAccentTheme.VIOLET }

@Composable
fun MyApplicationTheme(
  accentTheme: VyroAccentTheme = VyroAccentTheme.VIOLET,
  content: @Composable () -> Unit,
) {
  val colorScheme = darkColorScheme(
    primary = accentTheme.primary,
    onPrimary = Color.White,
    primaryContainer = accentTheme.primary.copy(alpha = 0.2f),
    onPrimaryContainer = accentTheme.primary,
    secondary = accentTheme.secondary,
    onSecondary = Color.White,
    secondaryContainer = accentTheme.secondary.copy(alpha = 0.2f),
    onSecondaryContainer = accentTheme.secondary,
    tertiary = VyroPink,
    background = VyroBlack,
    onBackground = VyroTextPrimary,
    surface = VyroDarkSurface,
    onSurface = VyroTextPrimary,
    surfaceVariant = VyroCardSurface,
    onSurfaceVariant = VyroTextSecondary,
    outline = VyroBorder,
    error = VyroHeartRed
  )

  CompositionLocalProvider(LocalVyroAccent provides accentTheme) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
