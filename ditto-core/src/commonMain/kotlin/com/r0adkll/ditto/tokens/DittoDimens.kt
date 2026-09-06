package com.r0adkll.ditto.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom

/**
 * Idiom- and density-specific dimensions that are neither spacing nor shape (ADR-021: hit targets).
 * Components read control sizes from here so a [DittoDensity] change re-sizes everything at once.
 */
@Immutable
public data class DittoDimens(
  /** Minimum interactive target. Android 48dp, Apple 44pt, Desktop 32dp (28 compact). */
  val minInteractiveSize: Dp,
  /** Default icon size inside components. */
  val iconSize: Dp,
  /** Default 1px-ish border width, expressed in dp. */
  val borderWidth: Dp,
  /** Focus ring stroke width. */
  val focusRingWidth: Dp,
  /** Buttons, segmented controls, toggle buttons. */
  val controlHeight: Dp,
  /** Text fields and search bars. */
  val fieldHeight: Dp,
  /** Standard icon button container. */
  val iconButtonSize: Dp,
  /** Single-line list rows and sidebar items. */
  val listRowHeight: Dp,
  /** Two-line list rows. */
  val listRowHeightTwoLine: Dp,
  /** Menu rows. */
  val menuItemHeight: Dp,
  /** Tab rows. */
  val tabHeight: Dp,
) {
  public companion object {
    public fun forIdiom(idiom: Idiom, density: DittoDensity = DittoDensity.forIdiom(idiom)): DittoDimens = when (idiom) {
      Idiom.Android -> DittoDimens(
        minInteractiveSize = 48.dp, iconSize = 24.dp, borderWidth = 1.dp, focusRingWidth = 2.dp,
        controlHeight = 40.dp, fieldHeight = 56.dp, iconButtonSize = 40.dp,
        listRowHeight = 56.dp, listRowHeightTwoLine = 72.dp, menuItemHeight = 48.dp, tabHeight = 48.dp,
      )
      Idiom.Apple -> DittoDimens(
        minInteractiveSize = 44.dp, iconSize = 22.dp, borderWidth = 0.5.dp, focusRingWidth = 2.dp,
        controlHeight = 44.dp, fieldHeight = 44.dp, iconButtonSize = 36.dp,
        listRowHeight = 44.dp, listRowHeightTwoLine = 60.dp, menuItemHeight = 44.dp, tabHeight = 44.dp,
      )
      Idiom.Desktop -> when (density) {
        DittoDensity.Comfortable -> DittoDimens(
          minInteractiveSize = 32.dp, iconSize = 18.dp, borderWidth = 1.dp, focusRingWidth = 2.dp,
          controlHeight = 36.dp, fieldHeight = 36.dp, iconButtonSize = 32.dp,
          listRowHeight = 40.dp, listRowHeightTwoLine = 56.dp, menuItemHeight = 32.dp, tabHeight = 40.dp,
        )
        DittoDensity.Compact -> DittoDimens(
          minInteractiveSize = 28.dp, iconSize = 16.dp, borderWidth = 1.dp, focusRingWidth = 2.dp,
          controlHeight = 28.dp, fieldHeight = 28.dp, iconButtonSize = 24.dp,
          listRowHeight = 28.dp, listRowHeightTwoLine = 44.dp, menuItemHeight = 24.dp, tabHeight = 32.dp,
        )
      }
    }.let { if (idiom != Idiom.Desktop && density == DittoDensity.Compact) it.compactMobile() else it }

    // Compact on a mobile idiom trims heights but keeps touch targets honest.
    private fun DittoDimens.compactMobile(): DittoDimens = copy(
      controlHeight = controlHeight - 8.dp,
      fieldHeight = fieldHeight - 8.dp,
      listRowHeight = listRowHeight - 8.dp,
      listRowHeightTwoLine = listRowHeightTwoLine - 8.dp,
      menuItemHeight = menuItemHeight - 8.dp,
    )
  }
}
