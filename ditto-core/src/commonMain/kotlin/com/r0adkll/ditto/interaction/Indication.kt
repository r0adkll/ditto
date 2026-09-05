package com.r0adkll.ditto.interaction

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.LocalContentColor
import kotlinx.coroutines.launch

/**
 * Press feedback is idiom-specific (ADR-008): Android ripples (and scales, see [pressScale]),
 * Desktop draws a flat state layer, Apple fades the content.
 *
 * Hover feedback is drawn whenever a hover interaction arrives, which only happens with a pointer.
 */
public fun dittoIndication(idiom: Idiom): IndicationNodeFactory = when (idiom) {
  Idiom.Android -> RippleIndication()
  Idiom.Apple -> OpacityIndication(pressedOpacity = 0.55f, hoveredAlpha = 0.06f)
  Idiom.Desktop -> StateLayerIndication(pressedAlpha = 0.1f, hoveredAlpha = 0.06f, focusedAlpha = 0f)
}

/** Draws the ambient content color over the node at an alpha driven by press / hover / focus. */
@Stable
public class StateLayerIndication(
  private val pressedAlpha: Float,
  private val hoveredAlpha: Float,
  private val focusedAlpha: Float,
  private val color: Color = Color.Unspecified,
) : IndicationNodeFactory {
  override fun create(interactionSource: InteractionSource): DelegatableNode =
    StateLayerNode(interactionSource, pressedAlpha, hoveredAlpha, focusedAlpha, color)

  override fun equals(other: Any?): Boolean = other is StateLayerIndication &&
    pressedAlpha == other.pressedAlpha && hoveredAlpha == other.hoveredAlpha &&
    focusedAlpha == other.focusedAlpha && color == other.color

  override fun hashCode(): Int = listOf(pressedAlpha, hoveredAlpha, focusedAlpha, color).hashCode()
}

private class StateLayerNode(
  private val interactionSource: InteractionSource,
  private val pressedAlpha: Float,
  private val hoveredAlpha: Float,
  private val focusedAlpha: Float,
  private val color: Color,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {
  private val alpha = Animatable(0f)
  private var pressed = false
  private var hovered = false
  private var focused = false

  override fun onAttach() {
    coroutineScope.launch {
      interactionSource.interactions.collect { interaction ->
        when (interaction) {
          is PressInteraction.Press -> pressed = true
          is PressInteraction.Release, is PressInteraction.Cancel -> pressed = false
          is HoverInteraction.Enter -> hovered = true
          is HoverInteraction.Exit -> hovered = false
          is FocusInteraction.Focus -> focused = true
          is FocusInteraction.Unfocus -> focused = false
        }
        animateTo(target(interaction))
      }
    }
  }

  private fun target(latest: Interaction): Float = when {
    pressed -> pressedAlpha
    hovered -> hoveredAlpha
    focused -> focusedAlpha
    else -> 0f
  }.also { if (latest is PressInteraction.Press) invalidateDraw() }

  private fun animateTo(value: Float) {
    coroutineScope.launch {
      alpha.animateTo(value, tween(if (value > alpha.value) 60 else 180)) { invalidateDraw() }
    }
  }

  override fun ContentDrawScope.draw() {
    drawContent()
    val a = alpha.value
    if (a > 0f) {
      val base = if (color == Color.Unspecified) currentValueOf(LocalContentColor) else color
      drawRect(base.copy(alpha = base.alpha * a))
    }
  }
}

/** Fades the content while pressed (the iOS touch-down feel) and draws a faint hover layer. */
@Stable
public class OpacityIndication(
  private val pressedOpacity: Float,
  private val hoveredAlpha: Float,
) : IndicationNodeFactory {
  override fun create(interactionSource: InteractionSource): DelegatableNode =
    OpacityNode(interactionSource, pressedOpacity, hoveredAlpha)

  override fun equals(other: Any?): Boolean = other is OpacityIndication &&
    pressedOpacity == other.pressedOpacity && hoveredAlpha == other.hoveredAlpha

  override fun hashCode(): Int = 31 * pressedOpacity.hashCode() + hoveredAlpha.hashCode()
}

private class OpacityNode(
  private val interactionSource: InteractionSource,
  private val pressedOpacity: Float,
  private val hoveredAlpha: Float,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {
  private val opacity = Animatable(1f)
  private val hover = Animatable(0f)
  private val layerPaint = Paint()

  override fun onAttach() {
    coroutineScope.launch {
      interactionSource.interactions.collect { interaction ->
        when (interaction) {
          is PressInteraction.Press -> launch { opacity.animateTo(pressedOpacity, tween(40)) { invalidateDraw() } }
          is PressInteraction.Release, is PressInteraction.Cancel ->
            launch { opacity.animateTo(1f, tween(220)) { invalidateDraw() } }
          is HoverInteraction.Enter -> launch { hover.animateTo(hoveredAlpha, tween(80)) { invalidateDraw() } }
          is HoverInteraction.Exit -> launch { hover.animateTo(0f, tween(160)) { invalidateDraw() } }
        }
      }
    }
  }

  override fun ContentDrawScope.draw() {
    val o = opacity.value
    if (o >= 1f) {
      drawContent()
    } else {
      layerPaint.alpha = o
      drawIntoCanvas { canvas ->
        canvas.saveLayer(Rect(0f, 0f, size.width, size.height), layerPaint)
        drawContent()
        canvas.restore()
      }
    }
    val h = hover.value
    if (h > 0f) {
      val base = currentValueOf(LocalContentColor)
      drawRect(base.copy(alpha = base.alpha * h))
    }
  }
}
