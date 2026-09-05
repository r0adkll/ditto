package com.r0adkll.ditto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.interaction.minimumInteractiveSize
import com.r0adkll.ditto.theme.DittoTheme

@Immutable
public class CheckboxStyle(
  public val size: Dp,
  public val shape: Shape,
  public val borderWidth: Dp,
  public val checkedColor: Color,
  public val uncheckedBorderColor: Color,
  public val checkmarkColor: Color,
) {
  public fun copy(
    size: Dp = this.size,
    shape: Shape = this.shape,
    borderWidth: Dp = this.borderWidth,
    checkedColor: Color = this.checkedColor,
    uncheckedBorderColor: Color = this.uncheckedBorderColor,
    checkmarkColor: Color = this.checkmarkColor,
  ): CheckboxStyle = CheckboxStyle(size, shape, borderWidth, checkedColor, uncheckedBorderColor, checkmarkColor)

  override fun equals(other: Any?): Boolean = other is CheckboxStyle &&
    size == other.size && shape == other.shape && borderWidth == other.borderWidth && checkedColor == other.checkedColor &&
    uncheckedBorderColor == other.uncheckedBorderColor && checkmarkColor == other.checkmarkColor

  override fun hashCode(): Int = listOf(size, shape, borderWidth, checkedColor, uncheckedBorderColor, checkmarkColor).hashCode()
  override fun toString(): String = "CheckboxStyle(size=$size, shape=$shape)"
}

public val LocalCheckboxStyle: ProvidableCompositionLocal<CheckboxStyle?> = staticCompositionLocalOf { null }

public object CheckboxDefaults {
  /** Android and Desktop draw a rounded square; Apple draws the iOS-style selection circle. */
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): CheckboxStyle {
    val colors = DittoTheme.colors
    return when (idiom) {
      Idiom.Android -> CheckboxStyle(
        size = 18.dp,
        shape = RoundedCornerShape(2.dp),
        borderWidth = 2.dp,
        checkedColor = colors.accent,
        uncheckedBorderColor = colors.onSurfaceVariant,
        checkmarkColor = colors.onAccent,
      )
      Idiom.Apple -> CheckboxStyle(
        size = 22.dp,
        shape = DittoTheme.shapes.full,
        borderWidth = 1.5.dp,
        checkedColor = colors.accent,
        uncheckedBorderColor = colors.neutrals[if (colors.isDark) 8 else 8],
        checkmarkColor = colors.onAccent,
      )
      Idiom.Desktop -> CheckboxStyle(
        size = 16.dp,
        shape = RoundedCornerShape(4.dp),
        borderWidth = 1.dp,
        checkedColor = colors.accent,
        uncheckedBorderColor = colors.outline,
        checkmarkColor = colors.onAccent,
      )
    }
  }
}

/** A binary checkbox. Pass `null` for [onCheckedChange] to render read-only. */
@Composable
public fun Checkbox(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: CheckboxStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val haptics = rememberToggleHaptics()
  val pointer = LocalInputCapabilities.current.pointer
  CheckboxImpl(
    state = ToggleableState(checked),
    modifier = modifier.then(
      if (onCheckedChange != null) {
        Modifier
          .clip(DittoTheme.shapes.full)
          .toggleable(
            value = checked,
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            enabled = enabled,
            role = Role.Checkbox,
            onValueChange = { haptics.toggled(it); onCheckedChange(it) },
          )
          .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
      } else {
        Modifier
      },
    ),
    enabled = enabled,
    explicitStyle = style,
    interactionSource = interactionSource,
  )
}

/** A checkbox that can also be [ToggleableState.Indeterminate], e.g. a parent of a mixed list. */
@Composable
public fun TriStateCheckbox(
  state: ToggleableState,
  onClick: (() -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: CheckboxStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val haptics = rememberToggleHaptics()
  val pointer = LocalInputCapabilities.current.pointer
  CheckboxImpl(
    state = state,
    modifier = modifier.then(
      if (onClick != null) {
        Modifier
          .clip(DittoTheme.shapes.full)
          .triStateToggleable(
            state = state,
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            enabled = enabled,
            role = Role.Checkbox,
            onClick = { haptics.toggled(state != ToggleableState.On); onClick() },
          )
          .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
      } else {
        Modifier
      },
    ),
    enabled = enabled,
    explicitStyle = style,
    interactionSource = interactionSource,
  )
}

@Composable
private fun CheckboxImpl(
  state: ToggleableState,
  modifier: Modifier,
  enabled: Boolean,
  explicitStyle: CheckboxStyle?,
  interactionSource: MutableInteractionSource,
) {
  val style = explicitStyle ?: LocalCheckboxStyle.current ?: CheckboxDefaults.style()
  val motion = DittoTheme.motion
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val on = state != ToggleableState.Off
  val fill by animateColorAsState(if (on) style.checkedColor else Color.Transparent, tween(motion.durationShort))
  val borderColor by animateColorAsState(if (on) style.checkedColor else style.uncheckedBorderColor, tween(motion.durationShort))

  Box(
    modifier
      .minimumInteractiveSize()
      .focusRing(interactionSource, style.shape)
      .size(style.size)
      .border(style.borderWidth, borderColor.copy(alpha = borderColor.alpha * alpha), style.shape)
      .background(fill.copy(alpha = fill.alpha * alpha), style.shape),
    contentAlignment = Alignment.Center,
  ) {
    when (state) {
      ToggleableState.On -> Icon(
        DittoIcons.check,
        contentDescription = null,
        tint = style.checkmarkColor.copy(alpha = alpha),
        size = style.size * 0.8f,
      )
      ToggleableState.Indeterminate -> Icon(
        DittoIcons.indeterminate,
        contentDescription = null,
        tint = style.checkmarkColor.copy(alpha = alpha),
        size = style.size * 0.8f,
      )
      ToggleableState.Off -> Unit
    }
  }
}
