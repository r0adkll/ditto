package com.r0adkll.ditto.interaction

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.theme.DittoTheme

/**
 * Draws a keyboard focus ring around [shape] while the node is focused **and** a keyboard is
 * present (ADR-008). Touch-only platforms draw nothing.
 */
public fun Modifier.focusRing(
  interactionSource: InteractionSource,
  shape: Shape,
  color: Color = Color.Unspecified,
  width: Dp = Dp.Unspecified,
  gap: Dp = 2.dp,
): Modifier = composed {
  val keyboard = LocalInputCapabilities.current.keyboard
  val focused by interactionSource.collectIsFocusedAsState()
  val ringColor = if (color == Color.Unspecified) DittoTheme.colors.accent else color
  val ringWidth = if (width == Dp.Unspecified) DittoTheme.dimens.focusRingWidth else width
  if (!keyboard) return@composed Modifier
  drawWithContent {
    drawContent()
    if (focused) {
      val inset = gap.toPx() + ringWidth.toPx() / 2f
      val outlineSize = Size(size.width + inset * 2, size.height + inset * 2)
      translate(-inset, -inset) {
        drawOutline(
          outline = shape.createOutline(outlineSize, layoutDirection, this),
          color = ringColor,
          style = Stroke(ringWidth.toPx()),
        )
      }
    }
  }
}
