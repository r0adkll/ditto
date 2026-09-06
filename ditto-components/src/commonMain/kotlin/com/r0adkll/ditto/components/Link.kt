package com.r0adkll.ditto.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.LocalTextStyle
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.theme.DittoTheme

/**
 * Inline text that navigates. Accent-colored; Desktop underlines on hover, Android always
 * underlines, Apple never does (iOS convention). [external] appends a small arrow glyph.
 */
@Composable
public fun Link(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  external: Boolean = false,
  style: TextStyle = LocalTextStyle.current,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val hovered by interactionSource.collectIsHoveredAsState()
  val pointer = LocalInputCapabilities.current.pointer
  val colors = DittoTheme.colors
  val color = if (enabled) colors.accent else colors.onSurface.copy(alpha = colors.disabledAlpha)
  val underline = when (DittoTheme.idiom) {
    Idiom.Android -> true
    Idiom.Apple -> false
    Idiom.Desktop -> hovered
  }
  Row(
    modifier
      .focusRing(interactionSource, DittoTheme.shapes.extraSmall)
      .clickable(interactionSource, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
      .then(if (pointer) Modifier.hoverable(interactionSource).pointerHoverIcon(PointerIcon.Hand) else Modifier),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(text, style = style.copy(textDecoration = if (underline) TextDecoration.Underline else TextDecoration.None), color = color)
    if (external) {
      Spacer(Modifier.width(2.dp))
      Icon(DittoIcons.forward, contentDescription = "opens externally", tint = color, size = (style.fontSize.value * 0.9f).dp)
    }
  }
}
