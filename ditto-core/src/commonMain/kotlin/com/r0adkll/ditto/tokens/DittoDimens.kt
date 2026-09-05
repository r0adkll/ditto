package com.r0adkll.ditto.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom

/** Idiom-specific dimensions that are neither spacing nor shape (ADR-021: hit targets). */
@Immutable
public data class DittoDimens(
  /** Minimum interactive target. Android 48dp, Apple 44pt, Desktop 32dp with a pointer. */
  val minInteractiveSize: Dp,
  /** Default icon size inside components. */
  val iconSize: Dp,
  /** Default 1px-ish border width, expressed in dp. */
  val borderWidth: Dp,
  /** Focus ring stroke width. */
  val focusRingWidth: Dp,
) {
  public companion object {
    public fun forIdiom(idiom: Idiom): DittoDimens = when (idiom) {
      Idiom.Android -> DittoDimens(minInteractiveSize = 48.dp, iconSize = 24.dp, borderWidth = 1.dp, focusRingWidth = 2.dp)
      Idiom.Apple -> DittoDimens(minInteractiveSize = 44.dp, iconSize = 22.dp, borderWidth = 0.5.dp, focusRingWidth = 2.dp)
      Idiom.Desktop -> DittoDimens(minInteractiveSize = 32.dp, iconSize = 18.dp, borderWidth = 1.dp, focusRingWidth = 2.dp)
    }
  }
}
