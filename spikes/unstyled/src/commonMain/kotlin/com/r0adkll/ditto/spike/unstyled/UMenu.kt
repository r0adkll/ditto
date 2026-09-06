package com.r0adkll.ditto.spike.unstyled

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledDropdownMenu
import com.composeunstyled.UnstyledDropdownMenuItem
import com.composeunstyled.DropdownMenuPanel
import com.r0adkll.ditto.components.MenuDefaults
import com.r0adkll.ditto.components.MenuStyle
import com.r0adkll.ditto.foundation.LocalContentColor
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.input.Shortcut
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.theme.DittoTheme
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Ditto's menu look on Unstyled's dropdown behaviour (anchoring, keyboard, dismissal, transitions).
 * Gaps vs Ditto's `Menu.kt`: no submenus, no flip-to-other-side, no first-item autofocus control.
 */
@Composable
fun UDropdownMenu(
  expanded: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  style: MenuStyle = MenuDefaults.style(),
  items: @Composable () -> Unit,
  anchor: @Composable () -> Unit,
) {
  UnstyledDropdownMenu(
    expanded = expanded,
    onExpandedChange = onExpandedChange,
    modifier = modifier,
    panel = {
      DropdownMenuPanel(enter = fadeIn(), exit = fadeOut()) {
        Surface(
          modifier = Modifier.widthIn(min = style.minWidth, max = style.maxWidth),
          shape = style.shape,
          color = style.containerColor,
          elevation = style.elevation,
          border = style.border,
        ) {
          Column(Modifier.padding(style.contentPadding)) { items() }
        }
      }
    },
    anchor = anchor,
  )
}

@Composable
fun UMenuItem(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shortcut: Shortcut? = null,
  destructive: Boolean = false,
  leadingIcon: (@Composable () -> Unit)? = null,
  style: MenuStyle = MenuDefaults.style(),
) {
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val color = (if (destructive) DittoTheme.colors.error else style.itemContentColor).copy(alpha = alpha)
  UnstyledDropdownMenuItem(
    onClick = onClick,
    enabled = enabled,
    indication = LocalIndication.current,
    modifier = modifier
      .fillMaxWidth()
      .clip(style.itemShape)
      .defaultMinSize(minHeight = style.itemMinHeight)
      .padding(style.itemPadding),
  ) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      CompositionLocalProvider(LocalContentColor provides color) {
        if (leadingIcon != null) { Box { leadingIcon() }; Spacer(Modifier.width(style.itemIconSpacing)) }
        Text(text, style = style.itemTextStyle, color = color, modifier = Modifier.weight(1f))
        if (shortcut != null) { Spacer(Modifier.width(style.itemIconSpacing)); Text(shortcut.label(), style = style.itemTextStyle, color = style.itemIconColor.copy(alpha = alpha)) }
      }
    }
  }
}
