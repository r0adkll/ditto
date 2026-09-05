package com.r0adkll.ditto.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlin.math.abs

/**
 * Temperature presets for the neutral ramp (ADR-022). [Tinted] derives a subtle tint from the accent.
 */
public enum class Neutrals {
  Pure,
  Cool,
  Warm,
  Tinted,
}

/**
 * A 12-step neutral ramp, Radix-style (ADR-022). Step 1 is the app background, 2 a subtle
 * background, 3–5 component backgrounds, 6–8 borders, 9–10 solid fills, 11–12 text.
 *
 * Light and dark ramps are each chosen for their own ground; dark is not an inversion.
 */
@Immutable
public class NeutralRamp(steps: List<Color>) {
  init {
    require(steps.size == STEPS) { "NeutralRamp needs exactly $STEPS steps, got ${steps.size}" }
  }

  private val steps: List<Color> = steps.toList()

  /** 1-based step access, matching the Radix numbering. */
  public operator fun get(step: Int): Color {
    require(step in 1..STEPS) { "step must be in 1..$STEPS, was $step" }
    return steps[step - 1]
  }

  public fun asList(): List<Color> = steps

  override fun equals(other: Any?): Boolean = other is NeutralRamp && other.steps == steps
  override fun hashCode(): Int = steps.hashCode()
  override fun toString(): String = "NeutralRamp($steps)"

  public companion object {
    public const val STEPS: Int = 12

    private val LIGHT_LIGHTNESS = floatArrayOf(0.992f, 0.978f, 0.955f, 0.93f, 0.9f, 0.86f, 0.8f, 0.7f, 0.56f, 0.5f, 0.4f, 0.13f)
    private val DARK_LIGHTNESS = floatArrayOf(0.07f, 0.1f, 0.13f, 0.16f, 0.19f, 0.23f, 0.29f, 0.38f, 0.45f, 0.5f, 0.68f, 0.93f)

    /**
     * Generates a ramp for [neutrals] on a light or dark ground. [accent] is only consulted for
     * [Neutrals.Tinted].
     */
    public fun generate(neutrals: Neutrals, dark: Boolean, accent: Color = Color.Unspecified): NeutralRamp {
      val (hue, saturation) = when (neutrals) {
        Neutrals.Pure -> 0f to 0f
        Neutrals.Cool -> 225f to 0.06f
        Neutrals.Warm -> 40f to 0.06f
        Neutrals.Tinted -> (if (accent.isSpecified) hueOf(accent) else 225f) to 0.1f
      }
      val ladder = if (dark) DARK_LIGHTNESS else LIGHT_LIGHTNESS
      return NeutralRamp(ladder.map { l -> hsl(hue, saturation * saturationScale(l), l) })
    }

    // Keep saturation subtle at the extremes so backgrounds and text stay near-neutral.
    private fun saturationScale(lightness: Float): Float = 1f - abs(lightness - 0.5f) * 0.8f

    internal fun hsl(h: Float, s: Float, l: Float): Color {
      val c = (1f - abs(2f * l - 1f)) * s
      val hp = (h % 360f + 360f) % 360f / 60f
      val x = c * (1f - abs(hp % 2f - 1f))
      val (r1, g1, b1) = when {
        hp < 1f -> Triple(c, x, 0f)
        hp < 2f -> Triple(x, c, 0f)
        hp < 3f -> Triple(0f, c, x)
        hp < 4f -> Triple(0f, x, c)
        hp < 5f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
      }
      val m = l - c / 2f
      return Color((r1 + m).coerceIn(0f, 1f), (g1 + m).coerceIn(0f, 1f), (b1 + m).coerceIn(0f, 1f))
    }

    internal fun hueOf(color: Color): Float {
      val r = color.red
      val g = color.green
      val b = color.blue
      val max = maxOf(r, g, b)
      val min = minOf(r, g, b)
      val d = max - min
      if (d == 0f) return 0f
      val h = when (max) {
        r -> ((g - b) / d) % 6f
        g -> (b - r) / d + 2f
        else -> (r - g) / d + 4f
      }
      return (h * 60f + 360f) % 360f
    }
  }
}

private val Color.isSpecified: Boolean get() = this != Color.Unspecified
