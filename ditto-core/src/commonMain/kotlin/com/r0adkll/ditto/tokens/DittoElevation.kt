package com.r0adkll.ditto.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom

/** Elevation levels 0..4 (ADR-009). */
public enum class ElevationLevel {
  Level0,
  Level1,
  Level2,
  Level3,
  Level4,
}

/**
 * How one elevation level renders: a shadow, a step up the neutral ramp, and/or a border
 * (ADR-022). Idioms tune the triple; the level names stay universal.
 */
@Immutable
public data class ElevationStyle(
  val shadow: Dp,
  /** How many neutral-ramp steps above `surface` the container sits. */
  val surfaceStep: Int,
  val border: Boolean,
)

@Immutable
public class DittoElevation(
  public val level0: ElevationStyle,
  public val level1: ElevationStyle,
  public val level2: ElevationStyle,
  public val level3: ElevationStyle,
  public val level4: ElevationStyle,
) {
  public operator fun get(level: ElevationLevel): ElevationStyle = when (level) {
    ElevationLevel.Level0 -> level0
    ElevationLevel.Level1 -> level1
    ElevationLevel.Level2 -> level2
    ElevationLevel.Level3 -> level3
    ElevationLevel.Level4 -> level4
  }

  override fun equals(other: Any?): Boolean = other is DittoElevation &&
    level0 == other.level0 && level1 == other.level1 && level2 == other.level2 && level3 == other.level3 && level4 == other.level4

  override fun hashCode(): Int = listOf(level0, level1, level2, level3, level4).hashCode()
  override fun toString(): String = "DittoElevation(level1=$level1)"

  public companion object {
    /**
     * Shared rule: light mode separates with shadow, dark mode steps up the ramp. Android leans
     * on shadow + step, Apple on borders / grouped backgrounds, Desktop on 1px border + faint shadow.
     */
    public fun forIdiom(idiom: Idiom, dark: Boolean): DittoElevation = when (idiom) {
      Idiom.Android -> DittoElevation(
        level0 = ElevationStyle(0.dp, 0, false),
        level1 = ElevationStyle(if (dark) 0.dp else 1.dp, if (dark) 1 else 0, false),
        level2 = ElevationStyle(if (dark) 1.dp else 3.dp, if (dark) 2 else 0, false),
        level3 = ElevationStyle(if (dark) 2.dp else 6.dp, if (dark) 3 else 0, false),
        level4 = ElevationStyle(if (dark) 3.dp else 8.dp, if (dark) 4 else 0, false),
      )
      Idiom.Apple -> DittoElevation(
        level0 = ElevationStyle(0.dp, 0, false),
        level1 = ElevationStyle(0.dp, 1, false),
        level2 = ElevationStyle(0.dp, 2, false),
        level3 = ElevationStyle(if (dark) 0.dp else 8.dp, if (dark) 3 else 1, false),
        level4 = ElevationStyle(if (dark) 0.dp else 16.dp, if (dark) 4 else 1, false),
      )
      Idiom.Desktop -> DittoElevation(
        level0 = ElevationStyle(0.dp, 0, false),
        level1 = ElevationStyle(0.dp, if (dark) 1 else 0, true),
        level2 = ElevationStyle(if (dark) 0.dp else 1.dp, if (dark) 2 else 0, true),
        level3 = ElevationStyle(if (dark) 2.dp else 4.dp, if (dark) 3 else 0, true),
        level4 = ElevationStyle(if (dark) 4.dp else 8.dp, if (dark) 4 else 0, true),
      )
    }
  }
}
