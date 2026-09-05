package com.r0adkll.ditto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.mutableIntStateOf
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
  require(tabs.isNotEmpty()) { "TabRow needs at least one tab" }
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalTabsStyle.current ?: TabsDefaults.style()
  val motion = DittoTheme.motion
  val pointer = LocalInputCapabilities.current.pointer
  val haptics = rememberToggleHaptics()
  var widthPx by remember { mutableIntStateOf(0) }
  val density = LocalDensity.current
  val tabWidth = with(density) { widthPx.toDp() } / tabs.size
  val labelWidths = remember(tabs) { mutableMapOf<Int, Int>() }
  val selectedLabelWidth = with(density) { (labelWidths[selectedIndex] ?: 0).toDp() }
  val indicatorWidth = if (style.indicatorFitsLabel && selectedLabelWidth > 0.dp) selectedLabelWidth else tabWidth
  val indicatorOffset by animateDpAsState(tabWidth * selectedIndex + (tabWidth - indicatorWidth) / 2, motion.springFor())
  val animatedIndicatorWidth by animateDpAsState(indicatorWidth, motion.springFor())

  Column(modifier.fillMaxWidth().onSizeChanged { widthPx = it.width }) {
    Box(Modifier.fillMaxWidth().height(style.height).selectableGroup()) {
      Row(Modifier.fillMaxWidth().height(style.height)) {
        tabs.forEachIndexed { index, label ->
          val selected = index == selectedIndex
          val interactionSource = remember { MutableInteractionSource() }
          val color by animateColorAsState(
            if (selected) style.selectedContentColor else style.contentColor,
            tween(motion.durationShort),
          )
          Box(
            Modifier
              .weight(1f)
              .height(style.height)
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
              .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              label,
              style = style.textStyle,
              color = if (enabled) color else color.copy(alpha = DittoTheme.colors.disabledAlpha),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier
                .padding(horizontal = DittoTheme.spacing.sm)
                .onSizeChanged { labelWidths[index] = it.width },
            )
          }
        }
      }
      if (widthPx > 0) {
        Box(
          Modifier
            .align(Alignment.BottomStart)
            .offset { IntOffset(indicatorOffset.roundToPx(), 0) }
            .width(animatedIndicatorWidth)
            .height(style.indicatorHeight)
            .background(style.indicatorColor, style.indicatorShape),
        )
      }
    }
    HorizontalDivider(color = style.dividerColor)
  }
}
