package com.r0adkll.ditto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import com.r0adkll.ditto.foundation.Icon
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.theme.DittoTheme

@Immutable
public class TabsStyle(
  public val height: Dp,
  public val indicatorHeight: Dp,
  public val indicatorColor: Color,
  public val indicatorShape: Shape,
  /** Indicator hugs the label (Android Expressive) instead of spanning the tab. */
  public val indicatorFitsLabel: Boolean,
  public val selectedContentColor: Color,
  public val contentColor: Color,
  public val textStyle: TextStyle,
  public val dividerColor: Color,
  public val tabShape: Shape,
) {
  public fun copy(
    height: Dp = this.height,
    indicatorHeight: Dp = this.indicatorHeight,
    indicatorColor: Color = this.indicatorColor,
    indicatorShape: Shape = this.indicatorShape,
    indicatorFitsLabel: Boolean = this.indicatorFitsLabel,
    selectedContentColor: Color = this.selectedContentColor,
    contentColor: Color = this.contentColor,
    textStyle: TextStyle = this.textStyle,
    dividerColor: Color = this.dividerColor,
    tabShape: Shape = this.tabShape,
  ): TabsStyle = TabsStyle(
    height, indicatorHeight, indicatorColor, indicatorShape, indicatorFitsLabel, selectedContentColor, contentColor,
    textStyle, dividerColor, tabShape,
  )

  override fun equals(other: Any?): Boolean = other is TabsStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "TabsStyle(height=$height)"
  private fun fields(): List<Any?> = listOf(
    height, indicatorHeight, indicatorColor, indicatorShape, indicatorFitsLabel, selectedContentColor, contentColor,
    textStyle, dividerColor, tabShape,
  )
}

public val LocalTabsStyle: ProvidableCompositionLocal<TabsStyle?> = staticCompositionLocalOf { null }

public object TabsDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): TabsStyle {
    val colors = DittoTheme.colors
    val type = DittoTheme.typography
    val shapes = DittoTheme.shapes
    return when (idiom) {
      Idiom.Android -> TabsStyle(
        height = 48.dp,
        indicatorHeight = 3.dp,
        indicatorColor = colors.accent,
        indicatorShape = shapes.full,
        indicatorFitsLabel = true,
        selectedContentColor = colors.accent,
        contentColor = colors.onSurfaceVariant,
        textStyle = type.label,
        dividerColor = colors.outlineVariant,
        tabShape = shapes.none,
      )
      Idiom.Apple -> TabsStyle(
        height = 44.dp,
        indicatorHeight = 2.dp,
        indicatorColor = colors.accent,
        indicatorShape = shapes.full,
        indicatorFitsLabel = false,
        selectedContentColor = colors.accent,
        contentColor = colors.onSurfaceVariant,
        textStyle = type.label,
        dividerColor = colors.outlineVariant,
        tabShape = shapes.none,
      )
      Idiom.Desktop -> TabsStyle(
        height = 40.dp,
        indicatorHeight = 2.dp,
        indicatorColor = colors.accent,
        indicatorShape = shapes.none,
        indicatorFitsLabel = false,
        selectedContentColor = colors.onSurface,
        contentColor = colors.onSurfaceVariant,
        textStyle = type.label,
        dividerColor = colors.outlineVariant,
        tabShape = shapes.extraSmall,
      )
    }
  }
}

/** A tab with a label and/or an icon. */
@Immutable
public data class TabItem(
  val label: String? = null,
  val icon: ImageVector? = null,
) {
  init { require(label != null || icon != null) { "A tab needs a label or an icon" } }
}

/**
 * Equal-width text tabs with an animated indicator and a hairline underneath. On iOS, prefer
 * [SegmentedControl] for view switching inside a screen; tabs are still right for top-level
 * paged content.
 */
@Composable
public fun TabRow(
  tabs: List<String>,
  selectedIndex: Int,
  onSelect: (Int) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: TabsStyle? = null,
) {
  TabRow(
    tabs = remember(tabs) { tabs.map { TabItem(label = it) } },
    selectedIndex = selectedIndex,
    onSelect = onSelect,
    modifier = modifier,
    enabled = enabled,
    scrollable = false,
    style = style,
  )
}

/**
 * Tabs with icons and/or labels. Fixed tabs share the width equally; [scrollable] tabs size to
 * their content and scroll horizontally, keeping the selection in view.
 */
@Composable
public fun TabRow(
  tabs: List<TabItem>,
  selectedIndex: Int,
  onSelect: (Int) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  scrollable: Boolean = false,
  style: TabsStyle? = null,
) {
  require(tabs.isNotEmpty()) { "TabRow needs at least one tab" }
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalTabsStyle.current ?: TabsDefaults.style()
  val motion = DittoTheme.motion
  val pointer = LocalInputCapabilities.current.pointer
  val haptics = rememberToggleHaptics()
  val density = LocalDensity.current
  val hasIcons = tabs.any { it.icon != null }
  val hasLabels = tabs.any { it.label != null }
  val height = if (hasIcons && hasLabels) style.height + 16.dp else style.height
  val scrollState = rememberScrollState()

  // Measured x/width per tab (px), for the indicator.
  val bounds = remember(tabs) { mutableStateMapOf<Int, Pair<Int, Int>>() }
  val labelWidths = remember(tabs) { mutableStateMapOf<Int, Int>() }
  val selectedBounds = bounds[selectedIndex]
  val targetX = selectedBounds?.first ?: 0
  val targetW = selectedBounds?.second ?: 0
  val fitLabel = style.indicatorFitsLabel && (labelWidths[selectedIndex] ?: 0) > 0
  val indicatorTargetW = if (fitLabel) labelWidths[selectedIndex]!! else targetW
  val indicatorTargetX = targetX + (targetW - indicatorTargetW) / 2

  // Snap on the first measurement, animate afterwards (first frame must be right).
  val indicatorX = remember { Animatable(0f) }
  val indicatorW = remember { Animatable(0f) }
  var measured by remember { mutableStateOf(false) }
  LaunchedEffect(indicatorTargetX, indicatorTargetW, targetW) {
    if (targetW == 0) return@LaunchedEffect
    if (!measured) {
      measured = true
      indicatorX.snapTo(indicatorTargetX.toFloat())
      indicatorW.snapTo(indicatorTargetW.toFloat())
    } else {
      launch { indicatorX.animateTo(indicatorTargetX.toFloat(), motion.spring) }
      launch { indicatorW.animateTo(indicatorTargetW.toFloat(), motion.spring) }
    }
  }
  if (scrollable) {
    var scrolledOnce by remember { mutableStateOf(false) }
    LaunchedEffect(selectedIndex, selectedBounds, scrollState.viewportSize) {
      val b = selectedBounds ?: return@LaunchedEffect
      val viewport = scrollState.viewportSize
      if (viewport <= 0) return@LaunchedEffect
      val start = b.first
      val end = b.first + b.second
      val target = when {
        start < scrollState.value -> start
        end > scrollState.value + viewport -> end - viewport
        else -> null
      }
      if (target != null) {
        // First placement snaps so the initial frame is right; later selections animate.
        if (scrolledOnce) scrollState.animateScrollTo(target) else scrollState.scrollTo(target)
      }
      scrolledOnce = true
    }
  }

  Column(modifier.fillMaxWidth()) {
    Box(Modifier.fillMaxWidth().height(height).selectableGroup()) {
      Row(
        Modifier
          .then(if (scrollable) Modifier.horizontalScroll(scrollState) else Modifier.fillMaxWidth())
          .height(height),
      ) {
        tabs.forEachIndexed { index, tab ->
          val selected = index == selectedIndex
          val interactionSource = remember { MutableInteractionSource() }
          val color by animateColorAsState(
            if (selected) style.selectedContentColor else style.contentColor,
            tween(motion.durationShort),
          )
          val tint = if (enabled) color else color.copy(alpha = DittoTheme.colors.disabledAlpha)
          Column(
            Modifier
              .then(if (scrollable) Modifier.widthIn(min = 90.dp) else Modifier.weight(1f))
              .height(height)
              .onPlaced { coords -> bounds[index] = coords.positionInParent().x.toInt() to coords.size.width }
              .focusRing(interactionSource, style.tabShape)
              .clip(style.tabShape)
              .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                role = Role.Tab,
                onClick = { if (!selected) { haptics.selected(); onSelect(index) } },
              )
              .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
              .padding(horizontal = DittoTheme.spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
          ) {
            if (tab.icon != null) {
              Icon(tab.icon, contentDescription = if (tab.label == null) "Tab ${index + 1}" else null, tint = tint, size = DittoTheme.dimens.iconSize)
              if (tab.label != null) Spacer(Modifier.height(DittoTheme.spacing.xs))
            }
            if (tab.label != null) {
              Text(
                tab.label,
                style = style.textStyle,
                color = tint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.onSizeChanged { labelWidths[index] = it.width },
              )
            }
          }
        }
      }
      if (measured) {
        Box(
          Modifier
            .align(Alignment.BottomStart)
            .offset { IntOffset(indicatorX.value.roundToInt() - (if (scrollable) scrollState.value else 0), 0) }
            .width(with(density) { indicatorW.value.toDp() })
            .height(style.indicatorHeight)
            .background(style.indicatorColor, style.indicatorShape),
        )
      }
    }
    HorizontalDivider(color = style.dividerColor)
  }
}
