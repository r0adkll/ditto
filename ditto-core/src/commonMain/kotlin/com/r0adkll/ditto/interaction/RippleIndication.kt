package com.r0adkll.ditto.interaction

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.unit.Dp
import com.r0adkll.ditto.foundation.LocalContentColor

/**
 * The Android idiom's press feedback (ADR-018): a ripple expanding from the touch point, drawn in
 * the ambient content color. Built on Compose's `material-ripple` node so it is a platform
 * `RippleDrawable` on Android and a Skia-drawn ripple everywhere else — no Material3 involved.
 */
@Stable
public class RippleIndication(
  private val bounded: Boolean = true,
  private val radius: Dp = Dp.Unspecified,
  private val color: Color = Color.Unspecified,
  private val alpha: RippleAlpha = DefaultAlpha,
) : IndicationNodeFactory {
  override fun create(interactionSource: InteractionSource): DelegatableNode =
    RippleDelegateNode(interactionSource, bounded, radius, color, alpha)

  override fun equals(other: Any?): Boolean = other is RippleIndication &&
    bounded == other.bounded && radius == other.radius && color == other.color && alpha == other.alpha

  override fun hashCode(): Int = listOf(bounded, radius, color, alpha).hashCode()

  public companion object {
    public val DefaultAlpha: RippleAlpha =
      RippleAlpha(draggedAlpha = 0.16f, focusedAlpha = 0.1f, hoveredAlpha = 0.08f, pressedAlpha = 0.1f)
  }
}

private class RippleDelegateNode(
  interactionSource: InteractionSource,
  bounded: Boolean,
  radius: Dp,
  private val color: Color,
  alpha: RippleAlpha,
) : DelegatingNode(), CompositionLocalConsumerModifierNode {
  @Suppress("unused")
  private val ripple = delegate(
    createRippleModifierNode(
      interactionSource = interactionSource,
      bounded = bounded,
      radius = radius,
      color = ColorProducer { if (color == Color.Unspecified) currentValueOf(LocalContentColor) else color },
      rippleAlpha = { alpha },
    ),
  )
}
