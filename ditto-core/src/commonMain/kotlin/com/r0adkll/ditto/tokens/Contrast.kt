package com.r0adkll.ditto.tokens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

/** WCAG 2 contrast utilities (ADR-022). */
public object Contrast {
  /** Minimum ratio for body text. */
  public const val BodyText: Float = 4.5f

  /** Minimum ratio for large text and non-text UI. */
  public const val LargeTextAndUi: Float = 3f

  /** WCAG 2 contrast ratio between two opaque colors, in 1..21. */
  public fun ratio(foreground: Color, background: Color): Float {
    val fg = foreground.compositeOver(background).luminance() + 0.05f
    val bg = background.luminance() + 0.05f
    return max(fg, bg) / min(fg, bg)
  }

  /** Picks whichever of [light] / [dark] contrasts better against [background]. */
  public fun onColor(
    background: Color,
    light: Color = Color.White,
    dark: Color = Color(0xFF111111),
  ): Color = if (ratio(light, background) >= ratio(dark, background)) light else dark
}
