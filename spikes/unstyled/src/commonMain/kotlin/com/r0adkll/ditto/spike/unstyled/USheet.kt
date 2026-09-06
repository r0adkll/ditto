package com.r0adkll.ditto.spike.unstyled

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composeunstyled.DragIndication
import com.composeunstyled.ModalBottomSheetState
import com.composeunstyled.Sheet
import com.composeunstyled.Scrim
import com.composeunstyled.SheetDetent
import com.composeunstyled.UnstyledModalBottomSheet
import com.composeunstyled.rememberModalBottomSheetState
import com.r0adkll.ditto.components.SheetDefaults
import com.r0adkll.ditto.components.SheetStyle
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.theme.DittoTheme

/** Medium detent = half the container. Unstyled's detents are lambdas of container/sheet height. */
val MediumDetent: SheetDetent = SheetDetent("medium") { containerHeight, _ -> containerHeight * 0.5f }

@Composable
fun rememberUSheetState(detents: List<SheetDetent> = listOf(SheetDetent.Hidden, MediumDetent, SheetDetent.FullyExpanded)): ModalBottomSheetState =
  rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden, detents = detents)

/**
 * Ditto's sheet look on Unstyled's modal bottom sheet behaviour (anchored drag physics, detents,
 * nested scroll, IME offset, scrim, dismissal). Compare with `Sheet.kt` (~300 lines).
 */
@Composable
fun UModalSheet(
  state: ModalBottomSheetState,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  style: SheetStyle = SheetDefaults.style(),
  content: @Composable ColumnScope.() -> Unit,
) {
  UnstyledModalBottomSheet(
    state = state,
    onDismiss = onDismiss,
    overlay = { Scrim(scrimColor = style.scrimColor) },
  ) {
    Sheet(modifier.widthIn(max = style.maxWidth).fillMaxWidth()) {
      Surface(shape = style.shape, color = style.containerColor, elevation = style.elevation, modifier = Modifier.fillMaxWidth()) {
        Column {
          if (style.dragHandle) {
            // DragIndication is the a11y-actionable drag handle; it draws nothing, so the pill is its modifier.
            Box(Modifier.fillMaxWidth().padding(vertical = DittoTheme.spacing.md), contentAlignment = Alignment.Center) {
              DragIndication(Modifier.width(36.dp).height(4.dp).background(style.dragHandleColor, DittoTheme.shapes.full))
            }
          }
          content()
        }
      }
    }
  }
}
