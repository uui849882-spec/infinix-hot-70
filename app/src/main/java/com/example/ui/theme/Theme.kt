package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = IslamicGoldBright,
    onPrimary = IslamicEmeraldDark,
    primaryContainer = IslamicEmeraldContainer,
    onPrimaryContainer = IslamicGoldBright,
    secondary = IslamicGold,
    onSecondary = IslamicEmeraldDark,
    secondaryContainer = IslamicGoldContainer,
    onSecondaryContainer = IslamicGoldBright,
    tertiary = IslamicEmeraldLight,
    onTertiary = Color.White,
    background = IslamicEmeraldDark,
    onBackground = TextPrimaryDark,
    surface = IslamicSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = IslamicParchmentDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = IslamicGoldDark,
    outlineVariant = AyahFrameBorder
  )

private val LightColorScheme =
  lightColorScheme(
    primary = IslamicEmeraldMedium,
    onPrimary = Color.White,
    primaryContainer = IslamicEmeraldContainer,
    onPrimaryContainer = IslamicGoldBright,
    secondary = IslamicGoldDark,
    onSecondary = Color.White,
    secondaryContainer = IslamicGoldContainer,
    onSecondaryContainer = IslamicGoldBright,
    tertiary = IslamicGold,
    onTertiary = IslamicEmeraldDark,
    background = IslamicParchmentLight,
    onBackground = IslamicEmeraldDark,
    surface = IslamicParchmentSurface,
    onSurface = IslamicEmeraldDark,
    surfaceVariant = Color(0xFFEBE4CF),
    onSurfaceVariant = Color(0xFF2C4339),
    outline = IslamicGold,
    outlineVariant = AyahFrameBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to luxurious dark emerald & gold
  dynamicColor: Boolean = false, // Keep consistent branding
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

