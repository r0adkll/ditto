package com.r0adkll.ditto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.LocalContentColor
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.interaction.minimumInteractiveSize
import com.r0adkll.ditto.theme.DittoTheme

@Immutable
public class ChipStyle(
  public val height: Dp,
  public val shape: Shape,
  public val containerColor: Color,
  public val selectedContainerColor: Color,
  public val contentColor: Color,
  public val selectedContentColor: Color,
  public val border: BorderStroke?,
  public val selectedBorder: BorderStroke?,
  public val textStyle: TextStyle,
  public val horizontalPadding: Dp,
  public val iconSize: Dp,
  /** Show a check glyph as the leading icon while selected (Android). */
  public val checkWhenSelected: Boolean,
) {
  public fun copy(
    height: Dp = this.height,
    shape: Shape = this.shape,
    containerColor: Color = this.containerColor,
    selectedContainerColor: Color = this.selectedContainerColor,
    contentColor: Color = this.contentColor,
    selectedContentColor: Color = this.selectedContentColor,
    border: BorderStroke? = this.border,
    selectedBorder: BorderStroke? = this.selectedBorder,
    textStyle: TextStyle = this.textStyle,
    horizontalPadding: Dp = this.horizontalPadding,
    iconSize: Dp = this.iconSize,
    checkWhenSelected: Boolean = this.checkWhenSelected,
  ): ChipStyle = ChipStyle(
    height, shape, containerColor, selectedContainerColor, contentColor, selectedContentColor, border, selectedBorder,
    textStyle, horizontalPadding, iconSize, checkWhenSelected,
  )

  override fun equals(other: Any?): Boolean = other is ChipStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "ChipStyle(height=$height)"
  private fun fields(): List<Any?> = listOf(
    height, shape, containerColor, selectedContainerColor, contentColor, selectedContentColor, border, selectedBorder,
    textStyle, horizontalPadding, iconSize, checkWhenSelected,
  )
}

public val LocalChipStyle: ProvidableCompositionLocal<ChipStyle?> = staticCompositionLocalOf { null }

public object ChipDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): ChipStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val type = DittoTheme.typography
    val dimens = DittoTheme.dimens
    val tonal = colors.accent.copy(alpha = ButtonDefaults.TonalContainerAlpha)
    return when (idiom) {
      Idiom.Android -> ChipStyle(
        height = 32.dp,
        shape = shapes.small,
        containerColor = Color.Transparent,
        selectedContainerColor = tonal,
        contentColor = colors.onSurfaceVariant,
        selectedContentColor = colors.accent,
        border = BorderStroke(dimens.borderWidth, colors.outline),
        selectedBorder = null,
        textStyle = type.label,
        horizontalPadding = 12.dp,
        iconSize = 18.dp,
        checkWhenSelected = true,
      )
      Idiom.Apple -> ChipStyle(
        height = 32.dp,
        shape = shapes.full,
        containerColor = colors.neutrals[3],
        selectedContainerColor = colors.accent,
        contentColor = colors.onSurface,
        selectedContentColor = colors.onAccent,
        border = null,
        selectedBorder = null,
        textStyle = type.label.copy(fontSize = type.bodySmall.fontSize),
        horizontalPadding = 14.dp,
        iconSize = 16.dp,
        checkWhenSelected = false,
      )
      Idiom.Desktop -> ChipStyle(
        height = 28.dp,
        shape = shapes.full,
        containerColor = colors.surface,
        selectedContainerColor = colors.accent,
        contentColor = colors.onSurface,
        selectedContentColor = colors.onAccent,
        border = BorderStroke(dimens.borderWidth, colors.outlineVariant),
        selectedBorder = null,
        textStyle = type.caption.copy(fontWeight = type.label.fontWeight),
        horizontalPadding = 10.dp,
        iconSize = 14.dp,
        checkWhenSelected = false,
      )
    }
  }
}

/**
 * A compact filter / tag. Toggle [selected] via [onClick]; pass [onDismiss] for a removable chip
 * (trailing close glyph). Read-only when both are `null`.
 */
@Composable
public fun Chip(
  text: String,
  modifier: Modifier = Modifier,
  selected: Boolean = false,
  onClick: (() -> Unit)? = null,
  onDismiss: (() -> Unit)? = null,
  enabled: Boolean = true,
  leadingIcon: ImageVector? = null,
  style: ChipStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalChipStyle.current ?: ChipDefaults.style()
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val motion = DittoTheme.motion
  val pointer = LocalInputCapabilities.current.pointer
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val container by animateColorAsState(if (selected) style.selectedContainerColor else style.containerColor, tween(motion.durationShort))
  val content by animateColorAsState(if (selected) style.selectedContentColor else style.contentColor, tween(motion.durationShort))
  val border = if (selected) style.selectedBorder else style.border
  val icon = if (selected && style.checkWhenSelected) DittoIcons.check else leadingIcon
  val haptics = rememberToggleHaptics()

  Row(
    modifier
      .minimumInteractiveSize()
      .focusRing(interactionSource, style.shape)
      .then(if (border != null) Modifier.border(BorderStroke(border.width, border.brush), style.shape) else Modifier)
      .background(container.copy(alpha = container.alpha * alpha), style.shape)
      .clip(style.shape)
      .then(
        if (onClick != null) {
          Modifier
            .selectable(
              selected = selected,
              interactionSource = interactionSource,
              indication = LocalIndication.current,
              enabled = enabled,
              role = Role.Checkbox,
              onClick = { haptics.toggled(!selected); onClick() },
            )
            .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
        } else {
          Modifier
        },
      )
      .defaultMinSize(minHeight = style.height)
      .padding(horizontal = style.horizontalPadding),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    CompositionLocalProvider(LocalContentColor provides content.copy(alpha = content.alpha * alpha)) {
      if (icon != null) {
        Icon(icon, contentDescription = null, size = style.iconSize)
        Spacer(Modifier.width(DittoTheme.spacing.xs + 2.dp))
      }
      Text(text, style = style.textStyle, color = content.copy(alpha = content.alpha * alpha), maxLines = 1)
      if (onDismiss != null) {
        Spacer(Modifier.width(DittoTheme.spacing.xs + 2.dp))
        IconButton(
          onClick = onDismiss,
          enabled = enabled,
          style = IconButtonDefaults.style(IconButtonVariant.Standard).copy(size = style.iconSize + 4.dp, iconSize = style.iconSize, contentColor = content),
        ) { Icon(DittoIcons.close, contentDescription = "Remove $text") }
      }
    }
  }
}
