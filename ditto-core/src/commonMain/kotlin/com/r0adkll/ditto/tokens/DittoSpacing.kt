package com.r0adkll.ditto.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom

/**
 * Fixed spacing scale times an idiom density multiplier (ADR-009). Desktop wants ~85% of mobile
 * spacing everywhere, which is a multiplier, not a second scale.
 */
@Immutable
public class DittoSpacing(
  /** Multiplier applied to the base scale. 1.0 on mobile idioms, 0.85 on Desktop. */
  public val density: Float = 1f,
) {
  public val xxs: Dp = 2.dp * density
  public val xs: Dp = 4.dp * density
  public val sm: Dp = 8.dp * density
  public val md: Dp = 12.dp * density
  public val lg: Dp = 16.dp * density
  public val xl: Dp = 24.dp * density
  public val xxl: Dp = 32.dp * density
  public val xxxl: Dp = 48.dp * density

  override fun equals(other: Any?): Boolean = other is DittoSpacing && other.density == density
  override fun hashCode(): Int = density.hashCode()
  override fun toString(): String = "DittoSpacing(density=$density)"

  public companion object {
    public fun forIdiom(idiom: Idiom): DittoSpacing = when (idiom) {
      Idiom.Android, Idiom.Apple -> DittoSpacing(1f)
      Idiom.Desktop -> DittoSpacing(0.85f)
    }
  }
}
