package com.r0adkll.ditto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.interaction.minimumInteractiveSize
import com.r0adkll.ditto.theme.DittoTheme

@Immutable
public class SwitchStyle(
  public val trackWidth: Dp,
  public val trackHeight: Dp,
  public val thumbSizeChecked: Dp,
  public val thumbSizeUnchecked: Dp,
  public val trackShape: Shape,
  public val checkedTrackColor: Color,
  public val uncheckedTrackColor: Color,
  public val checkedThumbColor: Color,
  public val uncheckedThumbColor: Color,
  public val uncheckedTrackBorder: BorderStroke?,
  public val thumbShadow: Dp,
  /** Android-style check glyph inside the thumb when on. */
  public val showCheckIcon: Boolean,
) {
  public fun copy(
    trackWidth: Dp = this.trackWidth,
    trackHeight: Dp = this.trackHeight,
    thumbSizeChecked: Dp = this.thumbSizeChecked,
    thumbSizeUnchecked: Dp = this.thumbSizeUnchecked,
    trackShape: Shape = this.trackShape,
    checkedTrackColor: Color = this.checkedTrackColor,
    uncheckedTrackColor: Color = this.uncheckedTrackColor,
    checkedThumbColor: Color = this.checkedThumbColor,
    uncheckedThumbColor: Color = this.uncheckedThumbColor,
    uncheckedTrackBorder: BorderStroke? = this.uncheckedTrackBorder,
    thumbShadow: Dp = this.thumbShadow,
    showCheckIcon: Boolean = this.showCheckIcon,
  ): SwitchStyle = SwitchStyle(
    trackWidth, trackHeight, thumbSizeChecked, thumbSizeUnchecked, trackShape, checkedTrackColor, uncheckedTrackColor,
    checkedThumbColor, uncheckedThumbColor, uncheckedTrackBorder, thumbShadow, showCheckIcon,
  )

  override fun equals(other: Any?): Boolean = other is SwitchStyle &&
    trackWidth == other.trackWidth && trackHeight == other.trackHeight && thumbSizeChecked == other.thumbSizeChecked &&
    thumbSizeUnchecked == other.thumbSizeUnchecked && trackShape == other.trackShape &&
    checkedTrackColor == other.checkedTrackColor && uncheckedTrackColor == other.uncheckedTrackColor &&
    checkedThumbColor == other.checkedThumbColor && uncheckedThumbColor == other.uncheckedThumbColor &&
    uncheckedTrackBorder == other.uncheckedTrackBorder && thumbShadow == other.thumbShadow && showCheckIcon == other.showCheckIcon

  override fun hashCode(): Int = listOf(
    trackWidth, trackHeight, thumbSizeChecked, thumbSizeUnchecked, trackShape, checkedTrackColor, uncheckedTrackColor,
    checkedThumbColor, uncheckedThumbColor, uncheckedTrackBorder, thumbShadow, showCheckIcon,
  ).hashCode()

  override fun toString(): String = "SwitchStyle(track=${trackWidth}x$trackHeight)"
}

public val LocalSwitchStyle: ProvidableCompositionLocal<SwitchStyle?> = staticCompositionLocalOf { null }

public object SwitchDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): SwitchStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    return when (idiom) {
      Idiom.Android -> SwitchStyle(
        trackWidth = 52.dp,
        trackHeight = 32.dp,
        thumbSizeChecked = 24.dp,
        thumbSizeUnchecked = 16.dp,
        trackShape = shapes.full,
        checkedTrackColor = colors.accent,
        uncheckedTrackColor = colors.surfaceOverlay,
        checkedThumbColor = colors.onAccent,
        uncheckedThumbColor = colors.outline,
        uncheckedTrackBorder = BorderStroke(2.dp, colors.outline),
        thumbShadow = 0.dp,
        showCheckIcon = true,
      )
      Idiom.Apple -> SwitchStyle(
        trackWidth = 51.dp,
        trackHeight = 31.dp,
        thumbSizeChecked = 27.dp,
        thumbSizeUnchecked = 27.dp,
        trackShape = shapes.full,
        checkedTrackColor = colors.accent,
        uncheckedTrackColor = colors.neutrals[if (colors.isDark) 6 else 7],
        checkedThumbColor = Color.White,
        uncheckedThumbColor = Color.White,
        uncheckedTrackBorder = null,
        thumbShadow = 2.dp,
        showCheckIcon = false,
      )
      Idiom.Desktop -> SwitchStyle(
        trackWidth = 44.dp,
        trackHeight = 24.dp,
        thumbSizeChecked = 20.dp,
        thumbSizeUnchecked = 20.dp,
        trackShape = shapes.full,
        checkedTrackColor = colors.accent,
        uncheckedTrackColor = colors.neutrals[if (colors.isDark) 6 else 6],
        checkedThumbColor = if (colors.isDark) colors.onSurface else Color.White,
        uncheckedThumbColor = if (colors.isDark) colors.onSurface else Color.White,
        uncheckedTrackBorder = null,
        thumbShadow = 0.dp,
        showCheckIcon = false,
      )
    }
  }
}

/**
 * A binary toggle. Pass `null` for [onCheckedChange] to render a read-only switch.
 *
 * Renders in the idiom's native proportions; on the Apple idiom toggling fires haptics (ADR-026).
 */
@Composable
public fun Switch(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: SwitchStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalSwitchStyle.current ?: SwitchDefaults.style()
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val motion = DittoTheme.motion
  val disabledAlpha = DittoTheme.colors.disabledAlpha
  val pointer = LocalInputCapabilities.current.pointer
  val haptics = rememberToggleHaptics()

  val trackColor by animateColorAsState(
    if (checked) style.checkedTrackColor else style.uncheckedTrackColor,
    tween(motion.durationShort),
  )
  val thumbColor by animateColorAsState(
    if (checked) style.checkedThumbColor else style.uncheckedThumbColor,
    tween(motion.durationShort),
  )
  val thumbSize by animateDpAsState(if (checked) style.thumbSizeChecked else style.thumbSizeUnchecked, motion.springFor())
  val thumbOffset by animateDpAsState(
    if (checked) style.trackWidth - style.thumbSizeChecked - (style.trackHeight - style.thumbSizeChecked) / 2 else (style.trackHeight - style.thumbSizeUnchecked) / 2,
    motion.springFor(),
  )
  val alpha = if (enabled) 1f else disabledAlpha
  val border = style.uncheckedTrackBorder?.takeIf { !checked }

  Box(
    modifier
      .then(
        if (onCheckedChange != null) {
          Modifier.toggleable(
            value = checked,
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = Role.Switch,
            onValueChange = { haptics.toggled(it); onCheckedChange(it) },
          )
        } else {
          Modifier
        },
      )
      .then(if (pointer && enabled && onCheckedChange != null) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
      .minimumInteractiveSize()
      .focusRing(interactionSource, style.trackShape)
      .size(style.trackWidth, style.trackHeight)
      .then(if (border != null) Modifier.border(BorderStroke(border.width, border.brush), style.trackShape) else Modifier)
      .background(trackColor.copy(alpha = trackColor.alpha * alpha), style.trackShape),
    contentAlignment = Alignment.CenterStart,
  ) {
    Box(
      Modifier
        .offset { IntOffset(thumbOffset.roundToPx(), 0) }
        .size(thumbSize)
        .then(if (style.thumbShadow > 0.dp && enabled) Modifier.shadow(style.thumbShadow, DittoTheme.shapes.full) else Modifier)
        .background(thumbColor.copy(alpha = thumbColor.alpha * alpha), DittoTheme.shapes.full),
      contentAlignment = Alignment.Center,
    ) {
      if (style.showCheckIcon && checked) {
        Icon(
          DittoIcons.check,
          contentDescription = null,
          tint = style.checkedTrackColor.copy(alpha = alpha),
          size = (thumbSize.value * 0.66f).dp,
        )
      }
    }
  }
}
