package com.r0adkll.ditto.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.Key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.border
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.theme.DittoTheme

/** A node in a [Tree]. [id] must be unique across the whole tree. */
@Immutable
public data class TreeNode(
  val id: String,
  val label: String,
  val icon: ImageVector? = null,
  val children: List<TreeNode> = emptyList(),
  val enabled: Boolean = true,
)

/** Expansion and selection state for a [Tree]. */
@Stable
public class TreeState(expanded: Set<String>, selected: String?) {
  public val expanded: MutableSet<String> = mutableStateSetOf<String>().apply { addAll(expanded) }
  public var selected: String? by mutableStateOf(selected)

  public fun toggle(id: String) { if (!expanded.remove(id)) expanded.add(id) }
}

@Composable
public fun rememberTreeState(expanded: Set<String> = emptySet(), selected: String? = null): TreeState =
  remember { TreeState(expanded, selected) }

/**
 * A collapsible hierarchy of [TreeNode]s in a lazy list. Rows indent 20dp per level and use the
 * idiom's single-line row height; the chevron rotates on expand. Clicking a row selects it;
 * clicking the chevron expands or collapses it. Expand/collapse are also exposed as semantics actions.
 */
@Composable
public fun Tree(
  roots: List<TreeNode>,
  modifier: Modifier = Modifier,
  state: TreeState = rememberTreeState(),
  onSelect: (TreeNode) -> Unit = { state.selected = it.id },
  onActivate: (TreeNode) -> Unit = {},
  listState: LazyListState = rememberLazyListState(),
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
  val flat = remember(roots, state.expanded.toSet()) { flatten(roots, state.expanded) }
  val focused by interactionSource.collectIsFocusedAsState()
  val scope = rememberCoroutineScope()

  fun indexOfSelected() = flat.indexOfFirst { it.node.id == state.selected }
  fun select(index: Int) {
    val row = flat.getOrNull(index) ?: return
    state.selected = row.node.id
    onSelect(row.node)
    scope.launch { listState.animateScrollToItem(index.coerceAtLeast(0)) }
  }
  fun parentIndex(index: Int): Int {
    val depth = flat[index].depth
    for (i in index - 1 downTo 0) if (flat[i].depth < depth) return i
    return -1
  }

  LazyColumn(
    modifier
      .selectableGroup()
      // The tree is one focus stop; arrows move the selection inside it (desktop tree convention).
      .onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown || flat.isEmpty()) return@onPreviewKeyEvent false
        val current = indexOfSelected()
        when (event.key) {
          Key.DirectionDown -> { select(if (current < 0) 0 else (current + 1).coerceAtMost(flat.lastIndex)); true }
          Key.DirectionUp -> { select(if (current < 0) 0 else (current - 1).coerceAtLeast(0)); true }
          Key.MoveHome -> { select(0); true }
          Key.MoveEnd -> { select(flat.lastIndex); true }
          Key.DirectionRight -> {
            if (current < 0) return@onPreviewKeyEvent false
            val node = flat[current].node
            when {
              node.children.isEmpty() -> false
              node.id !in state.expanded -> { state.expanded.add(node.id); true }
              else -> { select(current + 1); true }
            }
          }
          Key.DirectionLeft -> {
            if (current < 0) return@onPreviewKeyEvent false
            val node = flat[current].node
            if (node.id in state.expanded) { state.expanded.remove(node.id); true } else {
              val parent = parentIndex(current)
              if (parent >= 0) { select(parent); true } else false
            }
          }
          Key.Enter, Key.Spacebar -> {
            if (current < 0) return@onPreviewKeyEvent false
            onActivate(flat[current].node); true
          }
          else -> false
        }
      }
      .focusable(interactionSource = interactionSource),
    state = listState,
  ) {
    items(flat.size, key = { flat[it].node.id }) { index ->
      val row = flat[index]
      TreeRow(
        node = row.node,
        depth = row.depth,
        expanded = row.node.id in state.expanded,
        selected = state.selected == row.node.id,
        treeFocused = focused,
        onToggle = { state.toggle(row.node.id) },
        onSelect = { onSelect(row.node) },
        onActivate = { onActivate(row.node) },
      )
    }
  }
}

private class FlatRow(val node: TreeNode, val depth: Int)

private fun flatten(nodes: List<TreeNode>, expanded: Set<String>, depth: Int = 0, out: MutableList<FlatRow> = mutableListOf()): List<FlatRow> {
  nodes.forEach { n ->
    out += FlatRow(n, depth)
    if (n.id in expanded) flatten(n.children, expanded, depth + 1, out)
  }
  return out
}

@Composable
private fun TreeRow(
  node: TreeNode,
  depth: Int,
  expanded: Boolean,
  selected: Boolean,
  treeFocused: Boolean,
  onToggle: () -> Unit,
  onSelect: () -> Unit,
  onActivate: () -> Unit,
) {
  val colors = DittoTheme.colors
  val dimens = DittoTheme.dimens
  val interactionSource = remember { MutableInteractionSource() }
  val pointer = LocalInputCapabilities.current.pointer
  val alpha = if (node.enabled) 1f else colors.disabledAlpha
  val rotation by animateFloatAsState(if (expanded) 90f else 0f, DittoTheme.motion.spring)
  val shape = if (DittoTheme.idiom == Idiom.Desktop) DittoTheme.shapes.extraSmall else DittoTheme.shapes.none
  val indent = 20.dp
  Row(
    Modifier
      .fillMaxWidth()
      .height(dimens.listRowHeight)
      .padding(horizontal = DittoTheme.spacing.xs)
      .clip(shape)
      .background(if (selected) colors.accent.copy(alpha = ButtonDefaults.TonalContainerAlpha) else Color.Transparent)
      // Keyboard focus is on the tree; the selected row shows the ring on its behalf.
      .then(if (selected && treeFocused && LocalInputCapabilities.current.keyboard) Modifier.border(DittoTheme.dimens.focusRingWidth, colors.accent, shape) else Modifier)
      .combinedClickable(
        interactionSource = interactionSource,
        indication = LocalIndication.current,
        enabled = node.enabled,
        role = Role.Button,
        onDoubleClick = onActivate,
        onClick = onSelect,
      )
      .semantics { this.selected = selected }
      .then(if (pointer && node.enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
      .semantics { if (node.children.isNotEmpty()) { if (expanded) collapse { onToggle(); true } else expand { onToggle(); true } } }
      .padding(start = indent * depth + DittoTheme.spacing.xs, end = DittoTheme.spacing.sm),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(Modifier.size(dimens.iconSize + 4.dp), contentAlignment = Alignment.Center) {
      if (node.children.isNotEmpty()) {
        IconButton(
          onClick = onToggle,
          enabled = node.enabled,
          style = IconButtonDefaults.style(IconButtonVariant.Standard).copy(size = dimens.iconSize + 4.dp, iconSize = dimens.iconSize - 2.dp),
        ) {
          Icon(DittoIcons.chevronRight, contentDescription = if (expanded) "Collapse" else "Expand", modifier = Modifier.rotate(rotation))
        }
      }
    }
    if (node.icon != null) {
      Spacer(Modifier.width(DittoTheme.spacing.xs))
      Icon(node.icon, contentDescription = null, tint = (if (selected) colors.accent else colors.onSurfaceVariant).copy(alpha = alpha))
    }
    Spacer(Modifier.width(DittoTheme.spacing.sm))
    Text(node.label, style = DittoTheme.typography.body, color = colors.onSurface.copy(alpha = alpha), maxLines = 1, overflow = TextOverflow.Ellipsis)
  }
}
