package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArtisticFlairColorScheme = darkColorScheme(
  primary = AntiqueGold,
  onPrimary = ObsidianBg,
  primaryContainer = SurfaceHighlight,
  onPrimaryContainer = BrightGold,
  secondary = BrightGold,
  onSecondary = ObsidianBg,
  secondaryContainer = SurfaceElevated,
  onSecondaryContainer = TextSoft,
  tertiary = ShadowViolet,
  onTertiary = TextPrimary,
  background = ObsidianBg,
  onBackground = TextSoft,
  surface = SurfacePanel,
  onSurface = TextSoft,
  surfaceVariant = SurfaceDark,
  onSurfaceVariant = TextMuted,
  error = CrimsonRed,
  onError = TextPrimary,
  outline = BorderSlate,
  outlineVariant = BorderLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = ArtisticFlairColorScheme,
    typography = Typography,
    content = content
  )
}
