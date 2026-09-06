package com.r0adkll.ditto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
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
public class RadioButtonStyle(
  public val size: Dp,
  public val ringWidth: Dp,
  public val dotSize: Dp,
  public val selectedColor: Color,
  public val unselectedColor: Color,
  /** Apple-style: fill the circle and draw a checkmark instead of a ring + dot. */
  public val fillWhenSelected: Boolean,
  public val checkmarkColor: Color,
) {
  public fun copy(
    size: Dp = this.size,
    ringWidth: Dp = this.ringWidth,
    dotSize: Dp = this.dotSize,
    selectedColor: Color = this.selectedColor,
    unselectedColor: Color = this.unselectedColor,
    fillWhenSelected: Boolean = this.fillWhenSelected,
    checkmarkColor: Color = this.checkmarkColor,
  ): RadioButtonStyle = RadioButtonStyle(size, ringWidth, dotSize, selectedColor, unselectedColor, fillWhenSelected, checkmarkColor)

  override fun equals(other: Any?): Boolean = other is RadioButtonStyle &&
    size == other.size && ringWidth == other.ringWidth && dotSize == other.dotSize && selectedColor == other.selectedColor &&
    unselectedColor == other.unselectedColor && fillWhenSelected == other.fillWhenSelected && checkmarkColor == other.checkmarkColor

  override fun hashCode(): Int = listOf(size, ringWidth, dotSize, selectedColor, unselectedColor, fillWhenSelected, checkmarkColor).hashCode()
  override fun toString(): String = "RadioButtonStyle(size=$size)"
}

public val LocalRadioButtonStyle: ProvidableCompositionLocal<RadioButtonStyle?> = staticCompositionLocalOf { null }

public object RadioButtonDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): RadioButtonStyle {
    val colors = DittoTheme.colors
    return when (idiom) {
      Idiom.Android -> RadioButtonStyle(
        size = 20.dp,
        ringWidth = 2.dp,
        dotSize = 10.dp,
        selectedColor = colors.accent,
        unselectedColor = colors.onSurfaceVariant,
        fillWhenSelected = false,
        checkmarkColor = colors.onAccent,
      )
      Idiom.Apple -> RadioButtonStyle(
        size = 22.dp,
        ringWidth = 1.5.dp,
        dotSize = 0.dp,
        selectedColor = colors.accent,
        unselectedColor = colors.neutrals[8],
        fillWhenSelected = true,
        checkmarkColor = colors.onAccent,
      )
      Idiom.Desktop -> RadioButtonStyle(
        size = 16.dp,
        ringWidth = 1.dp,
        dotSize = 8.dp,
        selectedColor = colors.accent,
        unselectedColor = colors.outline,
        fillWhenSelected = false,
        checkmarkColor = colors.onAccent,
      )
    }
  }
}

/** One option of a mutually exclusive set. Group semantics come from the containing layout. */
@Composable
public fun RadioButton(
  selected: Boolean,
  onClick: (() -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: RadioButtonStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalRadioButtonStyle.current ?: DittoTheme.styleOverrides.resolve(RadioButtonDefaults.style())
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val haptics = rememberToggleHaptics()
  val pointer = LocalInputCapabilities.current.pointer
  val motion = DittoTheme.motion
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val circle = DittoTheme.shapes.full

  val ringColor by animateColorAsState(if (selected) style.selectedColor else style.unselectedColor, tween(motion.durationShort))
  val dot by animateDpAsState(if (selected) style.dotSize else 0.dp, motion.springFor())
  val fill by animateColorAsState(
    if (selected && style.fillWhenSelected) style.selectedColor else Color.Transparent,
    tween(motion.durationShort),
  )

  Box(
    modifier
      .then(
        if (onClick != null) {
          Modifier
            .clip(circle)
            .selectable(
              selected = selected,
              interactionSource = interactionSource,
              indication = LocalIndication.current,
              enabled = enabled,
              role = Role.RadioButton,
              onClick = { if (!selected) haptics.selected(); onClick() },
            )
            .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
        } else {
          Modifier
        },
      )
      .minimumInteractiveSize()
      .focusRing(interactionSource, circle)
      .size(style.size)
      .border(style.ringWidth, ringColor.copy(alpha = ringColor.alpha * alpha), circle)
      .background(fill.copy(alpha = fill.alpha * alpha), circle),
    contentAlignment = Alignment.Center,
  ) {
    if (style.fillWhenSelected) {
      if (selected) {
        Icon(DittoIcons.check, contentDescription = null, tint = style.checkmarkColor.copy(alpha = alpha), size = style.size * 0.75f)
      }
    } else if (dot > 0.dp) {
      Box(Modifier.size(dot).background(ringColor.copy(alpha = ringColor.alpha * alpha), circle))
    }
  }
}
