package com.r0adkll.ditto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.shadow
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
public class SegmentedControlStyle(
  public val height: Dp,
  public val shape: Shape,
  public val segmentShape: Shape,
  public val containerColor: Color,
  public val containerBorder: BorderStroke?,
  public val containerPadding: Dp,
  public val selectedColor: Color,
  public val selectedContentColor: Color,
  public val contentColor: Color,
  public val selectedShadow: Dp,
  public val textStyle: TextStyle,
  /** Android: draw dividers between segments instead of a sliding thumb. */
  public val dividers: Boolean,
) {
  public fun copy(
    height: Dp = this.height,
    shape: Shape = this.shape,
    segmentShape: Shape = this.segmentShape,
    containerColor: Color = this.containerColor,
    containerBorder: BorderStroke? = this.containerBorder,
    containerPadding: Dp = this.containerPadding,
    selectedColor: Color = this.selectedColor,
    selectedContentColor: Color = this.selectedContentColor,
    contentColor: Color = this.contentColor,
    selectedShadow: Dp = this.selectedShadow,
    textStyle: TextStyle = this.textStyle,
    dividers: Boolean = this.dividers,
  ): SegmentedControlStyle = SegmentedControlStyle(
    height, shape, segmentShape, containerColor, containerBorder, containerPadding, selectedColor, selectedContentColor,
    contentColor, selectedShadow, textStyle, dividers,
  )

  override fun equals(other: Any?): Boolean = other is SegmentedControlStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "SegmentedControlStyle(height=$height)"
  private fun fields(): List<Any?> = listOf(
    height, shape, segmentShape, containerColor, containerBorder, containerPadding, selectedColor, selectedContentColor,
    contentColor, selectedShadow, textStyle, dividers,
  )
}

public val LocalSegmentedControlStyle: ProvidableCompositionLocal<SegmentedControlStyle?> = staticCompositionLocalOf { null }

public object SegmentedControlDefaults {
  /** Apple: gray well with a sliding white thumb. Desktop: same idea, squarer. Android: outlined segments. */
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): SegmentedControlStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val type = DittoTheme.typography
    val dimens = DittoTheme.dimens
    return when (idiom) {
      Idiom.Android -> SegmentedControlStyle(
        height = 40.dp,
        shape = shapes.full,
        segmentShape = shapes.none,
        containerColor = Color.Transparent,
        containerBorder = BorderStroke(dimens.borderWidth, colors.outline),
        containerPadding = 0.dp,
        selectedColor = colors.accent.copy(alpha = ButtonDefaults.TonalContainerAlpha),
        selectedContentColor = colors.accent,
        contentColor = colors.onSurface,
        selectedShadow = 0.dp,
        textStyle = type.label,
        dividers = true,
      )
      Idiom.Apple -> SegmentedControlStyle(
        height = 32.dp,
        shape = shapes.small,
        segmentShape = shapes.extraSmall,
        containerColor = colors.neutrals[if (colors.isDark) 4 else 4],
        containerBorder = null,
        containerPadding = 2.dp,
        selectedColor = if (colors.isDark) colors.neutrals[7] else Color.White,
        selectedContentColor = colors.onSurface,
        contentColor = colors.onSurface,
        selectedShadow = 2.dp,
        textStyle = type.bodySmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
        dividers = false,
      )
      Idiom.Desktop -> SegmentedControlStyle(
        height = 36.dp,
        shape = shapes.small,
        segmentShape = shapes.extraSmall,
        containerColor = colors.neutrals[if (colors.isDark) 3 else 3],
        containerBorder = null,
        containerPadding = 4.dp,
        selectedColor = if (colors.isDark) colors.neutrals[6] else Color.White,
        selectedContentColor = colors.onSurface,
        contentColor = colors.onSurfaceVariant,
        selectedShadow = 1.dp,
        textStyle = type.label,
        dividers = false,
      )
    }
  }
}

/**
 * A single-choice control of equally sized segments. Android renders outlined segments with a
 * tonal fill; Apple and Desktop slide a raised thumb under the selection.
 */
@Composable
public fun SegmentedControl(
  options: List<String>,
  selectedIndex: Int,
  onSelect: (Int) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: SegmentedControlStyle? = null,
) {
  require(options.isNotEmpty()) { "SegmentedControl needs at least one option" }
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalSegmentedControlStyle.current ?: SegmentedControlDefaults.style()
  val motion = DittoTheme.motion
  val pointer = LocalInputCapabilities.current.pointer
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val haptics = rememberToggleHaptics()
  var widthPx by remember { mutableIntStateOf(0) }
  val density = LocalDensity.current
  val innerWidth = with(density) { widthPx.toDp() } - style.containerPadding * 2
  val segmentWidth = if (options.isEmpty()) 0.dp else innerWidth / options.size
  val thumbOffset by animateDpAsState(segmentWidth * selectedIndex, motion.springFor())

  Box(
    modifier
      .height(style.height)
      .clip(style.shape)
      .then(if (style.containerBorder != null) Modifier.border(style.containerBorder, style.shape) else Modifier)
      .background(style.containerColor.copy(alpha = style.containerColor.alpha * alpha))
      .onSizeChanged { widthPx = it.width }
      .selectableGroup(),
  ) {
    if (!style.dividers && widthPx > 0) {
      Box(
        Modifier
          .offset { IntOffset(thumbOffset.roundToPx(), 0) }
          .padding(style.containerPadding)
          .width(segmentWidth)
          .fillMaxHeight()
          .then(if (style.selectedShadow > 0.dp && enabled) Modifier.shadow(style.selectedShadow, style.segmentShape) else Modifier)
          .background(style.selectedColor.copy(alpha = style.selectedColor.alpha * alpha), style.segmentShape),
      )
    }
    Row(Modifier.padding(style.containerPadding).fillMaxHeight()) {
      options.forEachIndexed { index, label ->
        val selected = index == selectedIndex
        val interactionSource = remember { MutableInteractionSource() }
        val fill by animateColorAsState(
          if (style.dividers && selected) style.selectedColor else Color.Transparent,
          tween(motion.durationShort),
        )
        val content by animateColorAsState(
          if (selected) style.selectedContentColor else style.contentColor,
          tween(motion.durationShort),
        )
        if (style.dividers && index > 0) {
          VerticalDivider(color = style.containerBorder?.brush?.let { DittoTheme.colors.outline } ?: DittoTheme.colors.outlineVariant)
        }
        Box(
          Modifier
            .weight(1f)
            .fillMaxHeight()
            .focusRing(interactionSource, style.segmentShape)
            .clip(style.segmentShape)
            .background(fill.copy(alpha = fill.alpha * alpha))
            .selectable(
              selected = selected,
              interactionSource = interactionSource,
              indication = if (style.dividers) LocalIndication.current else null,
              enabled = enabled,
              role = Role.RadioButton,
              onClick = { if (!selected) { haptics.selected(); onSelect(index) } },
            )
            .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            label,
            style = style.textStyle,
            color = content.copy(alpha = content.alpha * alpha),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = DittoTheme.spacing.sm),
          )
        }
      }
    }
  }
}
