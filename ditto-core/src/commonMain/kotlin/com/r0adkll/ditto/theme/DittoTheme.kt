package com.r0adkll.ditto.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.LocalIdiom
import com.r0adkll.ditto.foundation.LocalContentColor
import com.r0adkll.ditto.foundation.LocalTextStyle
import com.r0adkll.ditto.interaction.dittoIndication
import com.r0adkll.ditto.tokens.ColorMode
import com.r0adkll.ditto.tokens.DittoColors
import com.r0adkll.ditto.tokens.DittoDensity
import com.r0adkll.ditto.tokens.DittoDimens
import com.r0adkll.ditto.tokens.DittoElevation
import com.r0adkll.ditto.tokens.DittoMotion
import com.r0adkll.ditto.tokens.DittoShapes
import com.r0adkll.ditto.tokens.DittoSpacing
import com.r0adkll.ditto.tokens.DittoTypography
import com.r0adkll.ditto.tokens.Neutrals

public val LocalDittoColors: ProvidableCompositionLocal<DittoColors> =
  staticCompositionLocalOf { DittoColors.from(DittoDefaults.Accent, dark = false) }
public val LocalDittoTypography: ProvidableCompositionLocal<DittoTypography> =
  staticCompositionLocalOf { DittoTypography.forIdiom(Idiom.Desktop) }
public val LocalDittoShapes: ProvidableCompositionLocal<DittoShapes> =
  staticCompositionLocalOf { DittoShapes.forIdiom(Idiom.Desktop) }
public val LocalDittoSpacing: ProvidableCompositionLocal<DittoSpacing> =
  staticCompositionLocalOf { DittoSpacing.forIdiom(Idiom.Desktop) }
public val LocalDittoElevation: ProvidableCompositionLocal<DittoElevation> =
  staticCompositionLocalOf { DittoElevation.forIdiom(Idiom.Desktop, dark = false) }
public val LocalDittoMotion: ProvidableCompositionLocal<DittoMotion> =
  staticCompositionLocalOf { DittoMotion.forIdiom(Idiom.Desktop) }
public val LocalDittoDimens: ProvidableCompositionLocal<DittoDimens> =
  staticCompositionLocalOf { DittoDimens.forIdiom(Idiom.Desktop) }
public val LocalDittoDensity: ProvidableCompositionLocal<DittoDensity> =
  staticCompositionLocalOf { DittoDensity.forIdiom(Idiom.Desktop) }

/**
 * Optional hook applied to whatever typography a [DittoTheme] receives, including nested themes
 * and preview cells. Apps use it to pin one font family everywhere; the screenshot harness uses
 * it to pin a bundled test font (ADR-026).
 */
public val LocalDittoTypographyTransform: ProvidableCompositionLocal<((DittoTypography) -> DittoTypography)?> =
  staticCompositionLocalOf { null }

/** Defaults used when a theme parameter is omitted. */
public object DittoDefaults {
  /** A neutral blue that reads as "unbranded" so apps notice when they forget to set an accent. */
  public val Accent: Color = Color(0xFF3B6CF6)
}

/**
 * Entry point for a Ditto UI (ADR-011). Every token has an idiom-appropriate default, so the
 * minimal call is `DittoTheme { ... }`.
 *
 * @param idiom the visual idiom to render in; defaults to the platform's natural idiom.
 * @param colorMode light, dark, or follow the system.
 * @param lightColors scheme used in light mode; derive one with [DittoColors.from].
 * @param darkColors scheme used in dark mode.
 * @param typography defaults to [DittoTypography.forIdiom].
 * @param shapes defaults to [DittoShapes.forIdiom].
 * @param spacing defaults to [DittoSpacing.forIdiom].
 * @param elevation defaults to [DittoElevation.forIdiom].
 * @param motion defaults to [DittoMotion.forIdiom].
 * @param dimens defaults to [DittoDimens.forIdiom].
 */
@Composable
public fun DittoTheme(
  idiom: Idiom = LocalIdiom.current,
  colorMode: ColorMode = ColorMode.System,
  density: DittoDensity = DittoDensity.forIdiom(idiom),
  lightColors: DittoColors = remember { DittoColors.from(DittoDefaults.Accent, dark = false) },
  darkColors: DittoColors = remember { DittoColors.from(DittoDefaults.Accent, dark = true) },
  typography: DittoTypography = remember(idiom, density) { DittoTypography.forIdiom(idiom, density = density) },
  shapes: DittoShapes = remember(idiom) { DittoShapes.forIdiom(idiom) },
  spacing: DittoSpacing = remember(idiom) { DittoSpacing.forIdiom(idiom) },
  elevation: DittoElevation? = null,
  motion: DittoMotion = remember(idiom) { DittoMotion.forIdiom(idiom) },
  dimens: DittoDimens = remember(idiom, density) { DittoDimens.forIdiom(idiom, density) },
  content: @Composable () -> Unit,
) {
  val dark = when (colorMode) {
    ColorMode.Light -> false
    ColorMode.Dark -> true
    ColorMode.System -> isSystemInDarkTheme()
  }
  val colors = if (dark) darkColors else lightColors
  val transform = LocalDittoTypographyTransform.current
  val resolvedTypography = remember(typography, transform) { transform?.invoke(typography) ?: typography }
  val resolvedElevation = elevation ?: remember(idiom, dark) { DittoElevation.forIdiom(idiom, dark) }
  val indication = remember(idiom) { dittoIndication(idiom) }
  LaunchedEffect(colors) {
    // ADR-022: on-colors are computed, but the accent itself is the app's choice. Say so once
    // per scheme instead of silently shipping unreadable text.
    val failures = colors.validateContrast()
    if (failures.isNotEmpty()) println("Ditto: theme colors fail WCAG 2 contrast — ${failures.joinToString()}")
  }
  CompositionLocalProvider(
    LocalIdiom provides idiom,
    LocalDittoColors provides colors,
    LocalDittoTypography provides resolvedTypography,
    LocalDittoShapes provides shapes,
    LocalDittoSpacing provides spacing,
    LocalDittoElevation provides resolvedElevation,
    LocalDittoMotion provides motion,
    LocalDittoDimens provides dimens,
    LocalDittoDensity provides density,
    LocalContentColor provides colors.onBackground,
    LocalTextStyle provides resolvedTypography.body,
    LocalIndication provides indication,
  ) {
    ProvideIdiomTokens(idiom, colors, content)
  }
}

/**
 * Convenience overload: derive both color schemes from one [accent] and a [neutrals] preset
 * (ADR-022).
 */
@Composable
public fun DittoTheme(
  accent: Color,
  neutrals: Neutrals = Neutrals.Cool,
  idiom: Idiom = LocalIdiom.current,
  colorMode: ColorMode = ColorMode.System,
  density: DittoDensity = DittoDensity.forIdiom(idiom),
  content: @Composable () -> Unit,
) {
  val light = remember(accent, neutrals) { DittoColors.from(accent, dark = false, neutrals = neutrals) }
  val dark = remember(accent, neutrals) { DittoColors.from(accent, dark = true, neutrals = neutrals) }
  DittoTheme(idiom = idiom, colorMode = colorMode, density = density, lightColors = light, darkColors = dark, content = content)
}

/** Accessors for the current theme's tokens. */
public object DittoTheme {
  public val colors: DittoColors
    @Composable @ReadOnlyComposable get() = LocalDittoColors.current
  public val typography: DittoTypography
    @Composable @ReadOnlyComposable get() = LocalDittoTypography.current
  public val shapes: DittoShapes
    @Composable @ReadOnlyComposable get() = LocalDittoShapes.current
  public val spacing: DittoSpacing
    @Composable @ReadOnlyComposable get() = LocalDittoSpacing.current
  public val elevation: DittoElevation
    @Composable @ReadOnlyComposable get() = LocalDittoElevation.current
  public val motion: DittoMotion
    @Composable @ReadOnlyComposable get() = LocalDittoMotion.current
  public val dimens: DittoDimens
    @Composable @ReadOnlyComposable get() = LocalDittoDimens.current
  public val density: DittoDensity
    @Composable @ReadOnlyComposable get() = LocalDittoDensity.current
  public val idiom: Idiom
    @Composable @ReadOnlyComposable get() = LocalIdiom.current
  public val isDark: Boolean
    @Composable @ReadOnlyComposable get() = LocalDittoColors.current.isDark
}
