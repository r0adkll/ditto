package com.r0adkll.ditto.interaction

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.r0adkll.ditto.theme.DittoTheme

/**
 * Scales the node down while pressed using the idiom's motion tokens (ADR-018). No-op for idioms
 * whose `pressScale` is 1.
 */
public fun Modifier.pressScale(interactionSource: InteractionSource, enabled: Boolean = true): Modifier = composed {
  val motion = DittoTheme.motion
  if (motion.pressScale >= 1f || !enabled) return@composed Modifier
  val pressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(if (pressed) motion.pressScale else 1f, motion.spring)
  graphicsLayer {
    scaleX = scale
    scaleY = scale
  }
}
