package com.r0adkll.ditto.interaction

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.theme.LocalDittoColors
import com.r0adkll.ditto.theme.LocalDittoDimens
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Draws a keyboard focus ring around [shape] while the node is focused **and** a keyboard is
 * present (ADR-008). Touch-only platforms draw nothing.
 *
 * [gap] is the space between the control's edge and the inside of the ring, and defaults to the
 * idiom's `focusRingGap`: Material offsets its ring, desktop UIs draw it against the border. The
 * ring's corners are grown by the offset so they stay concentric with the control's — a ring that
 * keeps the control's radius on a larger box reads as squarer than the thing it is wrapping.
 */
public fun Modifier.focusRing(
  interactionSource: InteractionSource,
  shape: Shape,
  color: Color = Color.Unspecified,
  width: Dp = Dp.Unspecified,
  gap: Dp = Dp.Unspecified,
): Modifier = this then FocusRingElement(interactionSource, shape, color, width, gap)

private data class FocusRingElement(
  val interactionSource: InteractionSource,
  val shape: Shape,
  val color: Color,
  val width: Dp,
  val gap: Dp,
) : ModifierNodeElement<FocusRingNode>() {
  override fun create(): FocusRingNode = FocusRingNode(interactionSource, shape, color, width, gap)

  override fun update(node: FocusRingNode) = node.update(interactionSource, shape, color, width, gap)

  override fun InspectorInfo.inspectableProperties() {
    name = "focusRing"
    properties["shape"] = shape
  }
}

private class FocusRingNode(
  private var interactionSource: InteractionSource,
  private var shape: Shape,
  private var color: Color,
  private var width: Dp,
  private var gap: Dp,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {
  private var focused = false
  private var collector: Job? = null

  override fun onAttach() = subscribe()

  override fun onDetach() {
    collector?.cancel()
    collector = null
  }

  fun update(interactionSource: InteractionSource, shape: Shape, color: Color, width: Dp, gap: Dp) {
    if (this.interactionSource != interactionSource) {
      this.interactionSource = interactionSource
      focused = false
      if (isAttached) subscribe()
    }
    this.shape = shape
    this.color = color
    this.width = width
    this.gap = gap
    invalidateDraw()
  }

  private fun subscribe() {
    collector?.cancel()
    collector = coroutineScope.launch {
      interactionSource.interactions.collect { interaction ->
        when (interaction) {
          is FocusInteraction.Focus -> focused = true
          is FocusInteraction.Unfocus -> focused = false
          else -> return@collect
        }
        invalidateDraw()
      }
    }
  }

  override fun ContentDrawScope.draw() {
    drawContent()
    if (!focused || !currentValueOf(LocalInputCapabilities).keyboard) return
    val ringColor = if (color == Color.Unspecified) currentValueOf(LocalDittoColors).accent else color
    val dimens = currentValueOf(LocalDittoDimens)
    val ringWidth = (if (width == Dp.Unspecified) dimens.focusRingWidth else width).toPx()
    val ringGap = (if (gap == Dp.Unspecified) dimens.focusRingGap else gap).toPx()
    val inset = ringGap + ringWidth / 2f
    val outlineSize = Size(size.width + inset * 2, size.height + inset * 2)
    // Grow every corner by the offset so the ring runs parallel to the control's edge. Without
    // this the ring keeps the control's radius on a bigger box and its corners look squared off.
    val outlineShape = (shape as? CornerBasedShape)?.let { s ->
      s.copy(
        topStart = CornerSize(s.topStart.toPx(size, this) + inset),
        topEnd = CornerSize(s.topEnd.toPx(size, this) + inset),
        bottomEnd = CornerSize(s.bottomEnd.toPx(size, this) + inset),
        bottomStart = CornerSize(s.bottomStart.toPx(size, this) + inset),
      )
    } ?: shape
    translate(-inset, -inset) {
      drawOutline(outlineShape.createOutline(outlineSize, layoutDirection, this), ringColor, style = Stroke(ringWidth))
    }
  }
}
