package com.r0adkll.ditto.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.LocalContentColor
import com.r0adkll.ditto.foundation.ProvideTextStyle
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.interaction.minimumInteractiveSize
import com.r0adkll.ditto.interaction.pressScale
import com.r0adkll.ditto.theme.DittoTheme

/**
 * High-emphasis button: filled with the accent color.
 *
 * Press feedback, hover, focus ring, hit target and semantics are handled here so callers only
 * supply content (ADR-008, ADR-021).
 */
@Composable
public fun Button(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: ButtonStyle? = null,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  ButtonImpl(ButtonVariant.Filled, onClick, modifier, enabled, style, interactionSource, content)
}

/** Medium-emphasis button: accent at low alpha over the surface (ADR-022). */
@Composable
public fun TonalButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: ButtonStyle? = null,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  ButtonImpl(ButtonVariant.Tonal, onClick, modifier, enabled, style, interactionSource, content)
}

/** Medium-emphasis button with a border and transparent container. */
@Composable
public fun OutlinedButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: ButtonStyle? = null,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  ButtonImpl(ButtonVariant.Outlined, onClick, modifier, enabled, style, interactionSource, content)
}

/** Low-emphasis button: text only. */
@Composable
public fun TextButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: ButtonStyle? = null,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  ButtonImpl(ButtonVariant.Text, onClick, modifier, enabled, style, interactionSource, content)
}

/** Convenience: a button with a label and an optional leading icon. */
@Composable
public fun Button(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  variant: ButtonVariant = ButtonVariant.Filled,
  leadingIcon: ImageVector? = null,
  style: ButtonStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  ButtonImpl(variant, onClick, modifier, enabled, style, interactionSource) {
    if (leadingIcon != null) {
      val resolved = ButtonDefaults.resolve(style, variant)
      Icon(leadingIcon, contentDescription = null, size = resolved.iconSize)
      Spacer(Modifier.width(resolved.iconSpacing))
    }
    Text(text)
  }
}

@Composable
private fun ButtonImpl(
  variant: ButtonVariant,
  onClick: () -> Unit,
  modifier: Modifier,
  enabled: Boolean,
  explicitStyle: ButtonStyle?,
  interactionSource: MutableInteractionSource?,
  content: @Composable RowScope.() -> Unit,
) {
  val style = ButtonDefaults.resolve(explicitStyle, variant)
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val containerColor = if (enabled) style.containerColor else style.disabledContainerColor
  val contentColor = if (enabled) style.contentColor else style.disabledContentColor
  val border = style.border?.takeIf { enabled } ?: style.border?.let { BorderStroke(it.width, contentColor.copy(alpha = 0.3f)) }
  val elevation = DittoTheme.elevation[style.elevation]
  val pointer = LocalInputCapabilities.current.pointer

  Row(
    modifier = modifier
      .minimumInteractiveSize()
      .pressScale(interactionSource, enabled)
      .focusRing(interactionSource, style.shape)
      .then(if (elevation.shadow > 0.dp && enabled) Modifier.shadow(elevation.shadow, style.shape) else Modifier)
      .then(if (border != null) Modifier.border(border, style.shape) else Modifier)
      .background(containerColor, style.shape)
      .clip(style.shape)
      .clickable(
        interactionSource = interactionSource,
        indication = LocalIndication.current,
        enabled = enabled,
        role = Role.Button,
        onClick = onClick,
      )
      .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
      .defaultMinSize(minWidth = style.minWidth, minHeight = style.minHeight)
      .padding(style.contentPadding),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    CompositionLocalProvider(LocalContentColor provides contentColor) {
      ProvideTextStyle(style.textStyle) {
        content()
      }
    }
  }
}
