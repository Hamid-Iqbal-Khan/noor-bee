package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = MinimalBlue,
    secondary = LightBlueGrey,
    tertiary = BorderGrey,
    background = DeepNavy,
    surface = Color(0xFF0F2537),
    onPrimary = Color.White,
    onSecondary = DeepNavy,
    onBackground = MinimalWhite,
    onSurface = MinimalWhite
  )

private val LightColorScheme =
  lightColorScheme(
    primary = MinimalBlue,
    secondary = LightBlueGrey,
    tertiary = BorderGrey,
    background = MinimalWhite,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = DeepNavy,
    onTertiary = SlateGrey,
    onBackground = Color(0xFF1A1C1E),
    onSurface = Color(0xFF1A1C1E),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Set default to false to protect our bespoke Clean Minimalism visual theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
