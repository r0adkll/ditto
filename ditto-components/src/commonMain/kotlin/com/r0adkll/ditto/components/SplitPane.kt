package com.r0adkll.ditto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import com.r0adkll.ditto.interaction.focusRing
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.Key
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.theme.DittoTheme

/** Divider position as a fraction of the available size. */
@Stable
public class SplitPaneState(initialFraction: Float) {
  public var fraction: Float by mutableFloatStateOf(initialFraction.coerceIn(0f, 1f))
}

@Composable
public fun rememberSplitPaneState(initialFraction: Float = 0.3f): SplitPaneState {
  var saved by rememberSaveable { mutableFloatStateOf(initialFraction) }
  val state = remember { SplitPaneState(saved) }
  saved = state.fraction
  return state
}

/**
 * Two panes side by side with a draggable divider. The divider is a hairline that thickens and
 * tints on hover/drag; the hit area is wider than the line.
 */
@Composable
public fun HorizontalSplitPane(
  first: @Composable () -> Unit,
  second: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  state: SplitPaneState = rememberSplitPaneState(),
  minFirst: Dp = 120.dp,
  minSecond: Dp = 120.dp,
) {
  SplitPane(first, second, modifier, state, minFirst, minSecond, horizontal = true)
}

/** Two panes stacked with a draggable divider. */
@Composable
public fun VerticalSplitPane(
  first: @Composable () -> Unit,
  second: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  state: SplitPaneState = rememberSplitPaneState(),
  minFirst: Dp = 80.dp,
  minSecond: Dp = 80.dp,
) {
  SplitPane(first, second, modifier, state, minFirst, minSecond, horizontal = false)
}

@Composable
private fun SplitPane(
  first: @Composable () -> Unit,
  second: @Composable () -> Unit,
  modifier: Modifier,
  state: SplitPaneState,
  minFirst: Dp,
  minSecond: Dp,
  horizontal: Boolean,
) {
  BoxWithConstraints(modifier.fillMaxSize()) {
    val total = if (horizontal) maxWidth else maxHeight
    val totalPx = if (horizontal) constraints.maxWidth.toFloat() else constraints.maxHeight.toFloat()
    val minF = if (total > 0.dp) (minFirst / total).coerceIn(0f, 1f) else 0f
    val maxF = if (total > 0.dp) (1f - minSecond / total).coerceIn(0f, 1f) else 1f
    val fraction = state.fraction.coerceIn(minF, maxOf(minF, maxF))
    val firstSize = total * fraction
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val dragged by interactionSource.collectIsDraggedAsState()
    val pointer = LocalInputCapabilities.current.pointer
    val colors = DittoTheme.colors
    val lineColor by animateColorAsState(if (hovered || dragged) colors.accent else colors.outlineVariant)
    val lineWidth = if (hovered || dragged) 2.dp else DittoTheme.dimens.borderWidth
    val hit = 8.dp
    val drag = rememberDraggableState { delta ->
      if (totalPx > 0f) state.fraction = (state.fraction + delta / totalPx).coerceIn(minF, maxOf(minF, maxF))
    }

    // The divider is a control, not just a drag target: a 1dp hairline you can only grab with a
    // pointer makes the layout unreachable by keyboard, and Compose's common API has no resize
    // cursor to hint at it either. Arrows nudge it by a fixed 16dp so the step is predictable at
    // any pane width; Home and End snap to the minimums.
    val upper = maxOf(minF, maxF)
    val stepPx = with(LocalDensity.current) { 16.dp.toPx() }
    val step = if (totalPx > 0f) stepPx / totalPx else 0.02f
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    fun nudge(delta: Float) {
      state.fraction = (state.fraction + delta).coerceIn(minF, upper)
    }
    val resizable = Modifier
      .focusRing(interactionSource, DittoTheme.shapes.extraSmall)
      .onKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
        when (event.key) {
          Key.DirectionRight -> if (horizontal) nudge(if (rtl) -step else step) else return@onKeyEvent false
          Key.DirectionLeft -> if (horizontal) nudge(if (rtl) step else -step) else return@onKeyEvent false
          Key.DirectionDown -> if (!horizontal) nudge(step) else return@onKeyEvent false
          Key.DirectionUp -> if (!horizontal) nudge(-step) else return@onKeyEvent false
          Key.MoveHome -> state.fraction = minF
          Key.MoveEnd -> state.fraction = upper
          else -> return@onKeyEvent false
        }
        true
      }
      .focusable(interactionSource = interactionSource)
      .progressSemantics(fraction, minF..upper)
      .semantics {
        contentDescription = "Resize panes"
        setProgress { target -> state.fraction = target.coerceIn(minF, upper); true }
      }

    if (horizontal) {
      Box(Modifier.width(firstSize).fillMaxHeight()) { first() }
      Box(Modifier.offset { IntOffset(firstSize.roundToPx(), 0) }.width(total - firstSize).fillMaxHeight()) { second() }
      Box(
        Modifier
          .offset { IntOffset((firstSize - hit / 2).roundToPx(), 0) }
          .width(hit)
          .fillMaxHeight()
          .then(if (pointer) Modifier.hoverable(interactionSource).pointerHoverIcon(PointerIcon.Hand) else Modifier)
          .draggable(drag, Orientation.Horizontal, interactionSource = interactionSource)
          .then(resizable),
        contentAlignment = Alignment.Center,
      ) { Box(Modifier.width(lineWidth).fillMaxHeight().background(lineColor)) }
    } else {
      Box(Modifier.height(firstSize).fillMaxWidth()) { first() }
      Box(Modifier.offset { IntOffset(0, firstSize.roundToPx()) }.height(total - firstSize).fillMaxWidth()) { second() }
      Box(
        Modifier
          .offset { IntOffset(0, (firstSize - hit / 2).roundToPx()) }
          .height(hit)
          .fillMaxWidth()
          .then(if (pointer) Modifier.hoverable(interactionSource).pointerHoverIcon(PointerIcon.Hand) else Modifier)
          .draggable(drag, Orientation.Vertical, interactionSource = interactionSource)
          .then(resizable),
        contentAlignment = Alignment.Center,
      ) { Box(Modifier.height(lineWidth).fillMaxWidth().background(lineColor)) }
    }
  }
}

