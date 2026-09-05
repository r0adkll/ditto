package com.r0adkll.ditto.material3

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.DittoColors
import com.r0adkll.ditto.tokens.DittoShapes
import com.r0adkll.ditto.tokens.DittoTypography

/**
 * Hosts Material3 components inside the enclosing [DittoTheme]: derives a `MaterialTheme` whose
 * colors, typography and shapes come from Ditto's tokens, so an app can migrate screen by screen
 * (ADR-004). Call inside `DittoTheme { }`.
 *
 * The Android idiom uses [MaterialExpressiveTheme] (matching Campfire); other idioms use the
 * standard theme so M3 components stay visually calmer.
 */
@Composable
public fun DittoMaterialTheme(
  expressive: Boolean = DittoTheme.idiom == Idiom.Android,
  content: @Composable () -> Unit,
) {
  val colors = DittoTheme.colors
  val typography = DittoTheme.typography
  val shapes = DittoTheme.shapes
  val colorScheme = remember(colors) { colors.toMaterialColorScheme() }
  val m3Typography = remember(typography) { typography.toMaterialTypography() }
  val m3Shapes = remember(shapes) { shapes.toMaterialShapes() }
  if (expressive) {
    MaterialExpressiveTheme(
      colorScheme = colorScheme,
      motionScheme = MotionScheme.expressive(),
      shapes = m3Shapes,
      typography = m3Typography,
      content = content,
    )
  } else {
    MaterialTheme(
      colorScheme = colorScheme,
      motionScheme = MotionScheme.standard(),
      shapes = m3Shapes,
      typography = m3Typography,
      content = content,
    )
  }
}

/**
 * Maps Ditto's neutral ramp + accent onto Material's role set. There is no tonal palette
 * (ADR-019), so containers are the accent at low alpha over the surface, secondary and tertiary
 * reuse the accent, and the surface-container ladder walks the neutral ramp.
 */
public fun DittoColors.toMaterialColorScheme(): ColorScheme {
  val n = neutrals
  val accentContainer = accent.copy(alpha = 0.14f).compositeOver(surface)
  val onAccentContainer = if (isDark) accent.lighten(0.35f) else accent.darken(0.45f)
  val errorContainer = error.copy(alpha = 0.14f).compositeOver(surface)
  val onErrorContainer = if (isDark) error.lighten(0.35f) else error.darken(0.45f)
  val inverseSurface = n[12]
  return if (isDark) {
    darkColorScheme(
      primary = accent,
      onPrimary = onAccent,
      primaryContainer = accentContainer,
      onPrimaryContainer = onAccentContainer,
      inversePrimary = accent.darken(0.3f),
      secondary = onSurfaceVariant,
      onSecondary = n[1],
      secondaryContainer = n[4],
      onSecondaryContainer = n[12],
      tertiary = accent,
      onTertiary = onAccent,
      tertiaryContainer = accentContainer,
      onTertiaryContainer = onAccentContainer,
      background = background,
      onBackground = onBackground,
      surface = surface,
      onSurface = onSurface,
      surfaceVariant = n[4],
      onSurfaceVariant = onSurfaceVariant,
      surfaceTint = accent,
      inverseSurface = n[12],
      inverseOnSurface = n[2],
      error = error,
      onError = onError,
      errorContainer = errorContainer,
      onErrorContainer = onErrorContainer,
      outline = outline,
      outlineVariant = outlineVariant,
      scrim = Color.Black,
      surfaceBright = n[5],
      surfaceContainerLowest = n[1],
      surfaceContainerLow = n[2],
      surfaceContainer = n[3],
      surfaceContainerHigh = n[4],
      surfaceContainerHighest = n[5],
      surfaceDim = n[1],
    )
  } else {
    lightColorScheme(
      primary = accent,
      onPrimary = onAccent,
      primaryContainer = accentContainer,
      onPrimaryContainer = onAccentContainer,
      inversePrimary = accent.lighten(0.4f),
      secondary = onSurfaceVariant,
      onSecondary = n[1],
      secondaryContainer = n[3],
      onSecondaryContainer = n[12],
      tertiary = accent,
      onTertiary = onAccent,
      tertiaryContainer = accentContainer,
      onTertiaryContainer = onAccentContainer,
      background = background,
      onBackground = onBackground,
      surface = surface,
      onSurface = onSurface,
      surfaceVariant = n[3],
      onSurfaceVariant = onSurfaceVariant,
      surfaceTint = accent,
      inverseSurface = inverseSurface,
      inverseOnSurface = n[1],
      error = error,
      onError = onError,
      errorContainer = errorContainer,
      onErrorContainer = onErrorContainer,
      outline = outline,
      outlineVariant = outlineVariant,
      scrim = Color.Black,
      surfaceBright = n[1],
      surfaceContainerLowest = Color.White,
      surfaceContainerLow = n[2],
      surfaceContainer = n[3],
      surfaceContainerHigh = n[4],
      surfaceContainerHighest = n[5],
      surfaceDim = n[4],
    )
  }
}

/** Spreads Ditto's eight styles across Material's fifteen; adjacent M3 sizes share a Ditto style. */
public fun DittoTypography.toMaterialTypography(): Typography = Typography(
  displayLarge = display,
  displayMedium = display,
  displaySmall = title,
  headlineLarge = title,
  headlineMedium = title,
  headlineSmall = heading,
  titleLarge = heading,
  titleMedium = subheading,
  titleSmall = label,
  bodyLarge = body,
  bodyMedium = bodySmall,
  bodySmall = caption,
  labelLarge = label,
  labelMedium = label,
  labelSmall = caption,
)

public fun DittoShapes.toMaterialShapes(): Shapes = Shapes(
  extraSmall = extraSmall,
  small = small,
  medium = medium,
  large = large,
  extraLarge = extraLarge,
)

private fun Color.lighten(amount: Float): Color = Color(
  red + (1f - red) * amount,
  green + (1f - green) * amount,
  blue + (1f - blue) * amount,
  alpha,
)

private fun Color.darken(amount: Float): Color = Color(red * (1f - amount), green * (1f - amount), blue * (1f - amount), alpha)
