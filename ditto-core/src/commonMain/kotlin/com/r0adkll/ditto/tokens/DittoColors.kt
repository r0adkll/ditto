package com.r0adkll.ditto.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

/**
 * The universal color roles (ADR-009, ADR-022). Deliberately small and idiom-neutral: a neutral
 * ramp does the layering work and a single [accent] carries the brand. Idiom-only roles live in
 * idiom extensions (ADR-010), not here.
 *
 * Build one with [DittoColors.from] and adjust with [copy].
 */
@Immutable
public class DittoColors(
  public val accent: Color,
  public val onAccent: Color,
  public val background: Color,
  public val onBackground: Color,
  public val surface: Color,
  public val surfaceRaised: Color,
  public val surfaceOverlay: Color,
  public val onSurface: Color,
  public val onSurfaceVariant: Color,
  public val outline: Color,
  public val outlineVariant: Color,
  public val error: Color,
  public val onError: Color,
  public val success: Color,
  public val onSuccess: Color,
  public val warning: Color,
  public val onWarning: Color,
  public val isDark: Boolean,
  /** The ramp the surface roles were drawn from; idioms use it for elevation steps and hover states. */
  public val neutrals: NeutralRamp,
) {
  /** Alpha applied to content and containers in the disabled state. */
  public val disabledAlpha: Float get() = 0.38f

  public fun copy(
    accent: Color = this.accent,
    onAccent: Color = this.onAccent,
    background: Color = this.background,
    onBackground: Color = this.onBackground,
    surface: Color = this.surface,
    surfaceRaised: Color = this.surfaceRaised,
    surfaceOverlay: Color = this.surfaceOverlay,
    onSurface: Color = this.onSurface,
    onSurfaceVariant: Color = this.onSurfaceVariant,
    outline: Color = this.outline,
    outlineVariant: Color = this.outlineVariant,
    error: Color = this.error,
    onError: Color = this.onError,
    success: Color = this.success,
    onSuccess: Color = this.onSuccess,
    warning: Color = this.warning,
    onWarning: Color = this.onWarning,
    isDark: Boolean = this.isDark,
    neutrals: NeutralRamp = this.neutrals,
  ): DittoColors = DittoColors(
    accent, onAccent, background, onBackground, surface, surfaceRaised, surfaceOverlay, onSurface,
    onSurfaceVariant, outline, outlineVariant, error, onError, success, onSuccess, warning, onWarning,
    isDark, neutrals,
  )

  /** The content color conventionally paired with [color], or [Color.Unspecified] if none. */
  public fun contentColorFor(color: Color): Color = when (color) {
    accent -> onAccent
    background -> onBackground
    surface, surfaceRaised, surfaceOverlay -> onSurface
    error -> onError
    success -> onSuccess
    warning -> onWarning
    else -> Color.Unspecified
  }

  /**
   * Checks every foreground/background pairing against WCAG 2 targets (ADR-022). Returns a list of
   * human-readable failures; empty means the scheme passes.
   */
  public fun validateContrast(): List<String> {
    val failures = mutableListOf<String>()
    fun check(name: String, fg: Color, bg: Color, min: Float) {
      val ratio = Contrast.ratio(fg, bg)
      if (ratio < min) failures += "$name: ${(ratio * 100).roundToInt() / 100f} < $min"
    }
    check("onAccent on accent", onAccent, accent, Contrast.BodyText)
    check("onBackground on background", onBackground, background, Contrast.BodyText)
    check("onSurface on surface", onSurface, surface, Contrast.BodyText)
    check("onSurface on surfaceRaised", onSurface, surfaceRaised, Contrast.BodyText)
    check("onSurface on surfaceOverlay", onSurface, surfaceOverlay, Contrast.BodyText)
    check("onSurfaceVariant on surface", onSurfaceVariant, surface, Contrast.BodyText)
    check("outline on surface", outline, surface, Contrast.LargeTextAndUi)
    check("accent on surface", accent, surface, Contrast.LargeTextAndUi)
    check("onError on error", onError, error, Contrast.BodyText)
    check("onSuccess on success", onSuccess, success, Contrast.BodyText)
    check("onWarning on warning", onWarning, warning, Contrast.BodyText)
    return failures
  }

  override fun equals(other: Any?): Boolean = other is DittoColors &&
    accent == other.accent && onAccent == other.onAccent && background == other.background &&
    onBackground == other.onBackground && surface == other.surface && surfaceRaised == other.surfaceRaised &&
    surfaceOverlay == other.surfaceOverlay && onSurface == other.onSurface &&
    onSurfaceVariant == other.onSurfaceVariant && outline == other.outline && outlineVariant == other.outlineVariant &&
    error == other.error && onError == other.onError && success == other.success && onSuccess == other.onSuccess &&
    warning == other.warning && onWarning == other.onWarning && isDark == other.isDark && neutrals == other.neutrals

  override fun hashCode(): Int {
    var h = accent.hashCode()
    listOf(
      onAccent, background, onBackground, surface, surfaceRaised, surfaceOverlay, onSurface, onSurfaceVariant,
      outline, outlineVariant, error, onError, success, onSuccess, warning, onWarning,
    ).forEach { h = 31 * h + it.hashCode() }
    h = 31 * h + isDark.hashCode()
    h = 31 * h + neutrals.hashCode()
    return h
  }

  override fun toString(): String = "DittoColors(accent=$accent, isDark=$isDark)"

  public companion object {
    /**
     * Derives a complete scheme from one [accent] and a [neutrals] preset (ADR-022). Everything
     * else is computed: surfaces from the ramp, on-colors by contrast.
     */
    public fun from(
      accent: Color,
      dark: Boolean,
      neutrals: Neutrals = Neutrals.Cool,
    ): DittoColors {
      val ramp = NeutralRamp.generate(neutrals, dark, accent)
      val onAccent = Contrast.onColor(accent, light = Color.White, dark = ramp[if (dark) 1 else 12])
      return if (dark) {
        DittoColors(
          accent = accent,
          onAccent = onAccent,
          background = ramp[1],
          onBackground = ramp[12],
          surface = ramp[2],
          surfaceRaised = ramp[3],
          surfaceOverlay = ramp[4],
          onSurface = ramp[12],
          onSurfaceVariant = ramp[11],
          outline = ramp[9],
          outlineVariant = ramp[7],
          error = Color(0xFFF2555A),
          onError = Color(0xFF1C0506),
          success = Color(0xFF46C27A),
          onSuccess = Color(0xFF05170C),
          warning = Color(0xFFF5B547),
          onWarning = Color(0xFF1F1400),
          isDark = true,
          neutrals = ramp,
        )
      } else {
        DittoColors(
          accent = accent,
          onAccent = onAccent,
          background = ramp[1],
          onBackground = ramp[12],
          surface = ramp[1],
          surfaceRaised = ramp[2],
          surfaceOverlay = ramp[3],
          onSurface = ramp[12],
          onSurfaceVariant = ramp[11],
          outline = ramp[9],
          outlineVariant = ramp[7],
          error = Color(0xFFC62828),
          onError = Color.White,
          success = Color(0xFF15803D),
          onSuccess = Color.White,
          warning = Color(0xFF92400E),
          onWarning = Color.White,
          isDark = false,
          neutrals = ramp,
        )
      }
    }
  }
}
