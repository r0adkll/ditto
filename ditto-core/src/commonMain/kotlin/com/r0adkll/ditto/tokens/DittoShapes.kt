package com.r0adkll.ditto.tokens

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom

/** The seven-step shape scale (ADR-009). Idioms set the radii. */
@Immutable
public class DittoShapes(
  public val none: CornerBasedShape,
  public val extraSmall: CornerBasedShape,
  public val small: CornerBasedShape,
  public val medium: CornerBasedShape,
  public val large: CornerBasedShape,
  public val extraLarge: CornerBasedShape,
  public val full: CornerBasedShape,
) {
  public fun copy(
    none: CornerBasedShape = this.none,
    extraSmall: CornerBasedShape = this.extraSmall,
    small: CornerBasedShape = this.small,
    medium: CornerBasedShape = this.medium,
    large: CornerBasedShape = this.large,
    extraLarge: CornerBasedShape = this.extraLarge,
    full: CornerBasedShape = this.full,
  ): DittoShapes = DittoShapes(none, extraSmall, small, medium, large, extraLarge, full)

  override fun equals(other: Any?): Boolean = other is DittoShapes &&
    none == other.none && extraSmall == other.extraSmall && small == other.small && medium == other.medium &&
    large == other.large && extraLarge == other.extraLarge && full == other.full

  override fun hashCode(): Int = listOf(none, extraSmall, small, medium, large, extraLarge, full).hashCode()
  override fun toString(): String = "DittoShapes(medium=$medium)"

  public companion object {
    public fun forIdiom(idiom: Idiom): DittoShapes = when (idiom) {
      Idiom.Android -> DittoShapes(
        none = RoundedCornerShape(0.dp),
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(28.dp),
        full = RoundedCornerShape(50),
      )
      Idiom.Apple -> DittoShapes(
        none = RoundedCornerShape(0.dp),
        extraSmall = RoundedCornerShape(6.dp),
        small = RoundedCornerShape(10.dp),
        medium = RoundedCornerShape(14.dp),
        large = RoundedCornerShape(20.dp),
        extraLarge = RoundedCornerShape(28.dp),
        full = RoundedCornerShape(50),
      )
      Idiom.Desktop -> DittoShapes(
        none = RoundedCornerShape(0.dp),
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(6.dp),
        medium = RoundedCornerShape(8.dp),
        large = RoundedCornerShape(12.dp),
        extraLarge = RoundedCornerShape(16.dp),
        full = RoundedCornerShape(50),
      )
    }
  }
}
