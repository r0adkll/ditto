package com.r0adkll.ditto.interaction

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import com.r0adkll.ditto.theme.LocalDittoMotion
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Scales the node down while pressed using the idiom's motion tokens (ADR-018). No-op for idioms
 * whose `pressScale` is 1, and while [enabled] is false.
 */
public fun Modifier.pressScale(interactionSource: InteractionSource, enabled: Boolean = true): Modifier =
  this then PressScaleElement(interactionSource, enabled)

private data class PressScaleElement(
  val interactionSource: InteractionSource,
  val enabled: Boolean,
) : ModifierNodeElement<PressScaleNode>() {
  override fun create(): PressScaleNode = PressScaleNode(interactionSource, enabled)
  override fun update(node: PressScaleNode) = node.update(interactionSource, enabled)
  override fun InspectorInfo.inspectableProperties() {
    name = "pressScale"
    properties["enabled"] = enabled
  }
}

private class PressScaleNode(
  private var interactionSource: InteractionSource,
  private var enabled: Boolean,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {
  private val scale = Animatable(1f)
  private var collector: Job? = null

  override fun onAttach() = subscribe()

  override fun onDetach() {
    collector?.cancel()
    collector = null
  }

  fun update(interactionSource: InteractionSource, enabled: Boolean) {
    this.enabled = enabled
    if (this.interactionSource != interactionSource) {
      this.interactionSource = interactionSource
      if (isAttached) subscribe()
    }
    if (!enabled && scale.value != 1f) coroutineScope.launch { scale.snapTo(1f); invalidateDraw() }
  }

  private fun subscribe() {
    collector?.cancel()
    collector = coroutineScope.launch {
      interactionSource.interactions.collect { interaction ->
        val motion = currentValueOf(LocalDittoMotion)
        if (motion.pressScale >= 1f || !enabled) return@collect
        val target = when (interaction) {
          is PressInteraction.Press -> motion.pressScale
          is PressInteraction.Release, is PressInteraction.Cancel -> 1f
          else -> return@collect
        }
        launch { scale.animateTo(target, motion.spring) { invalidateDraw() } }
      }
    }
  }

  override fun ContentDrawScope.draw() {
    val s = scale.value
    if (s == 1f) {
      drawContent()
    } else {
      scale(s, s) { this@draw.drawContent() }
    }
  }
}
