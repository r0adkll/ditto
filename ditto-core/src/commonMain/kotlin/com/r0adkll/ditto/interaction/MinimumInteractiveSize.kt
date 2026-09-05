package com.r0adkll.ditto.interaction

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import com.r0adkll.ditto.theme.LocalDittoDimens
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Reserves at least the idiom's minimum interactive size (ADR-021) for the touch target while
 * letting the visual stay smaller, centering it inside the reserved area.
 */
public fun Modifier.minimumInteractiveSize(): Modifier = this then MinimumInteractiveSizeElement

private object MinimumInteractiveSizeElement : ModifierNodeElement<MinimumInteractiveSizeNode>() {
  override fun create(): MinimumInteractiveSizeNode = MinimumInteractiveSizeNode()
  override fun update(node: MinimumInteractiveSizeNode) = Unit
  override fun InspectorInfo.inspectableProperties() {
    name = "minimumInteractiveSize"
  }
  override fun equals(other: Any?): Boolean = other === this
  override fun hashCode(): Int = "minimumInteractiveSize".hashCode()
}

private class MinimumInteractiveSizeNode : Modifier.Node(), LayoutModifierNode, CompositionLocalConsumerModifierNode {
  override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
    val min = currentValueOf(LocalDittoDimens).minInteractiveSize.roundToPx()
    val placeable = measurable.measure(constraints)
    val width = max(placeable.width, min.coerceAtMost(constraints.maxWidth))
    val height = max(placeable.height, min.coerceAtMost(constraints.maxHeight))
    return layout(width, height) {
      placeable.place(((width - placeable.width) / 2f).roundToInt(), ((height - placeable.height) / 2f).roundToInt())
    }
  }
}
