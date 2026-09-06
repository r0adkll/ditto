package com.r0adkll.ditto.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.Key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.theme.DittoTheme

/**
 * A single-select field: looks like a [TextField], opens a [DropdownMenu] of [options]. Reuses
 * [TextFieldStyle] so it lines up with text inputs in a form. Pass `null` [selectedIndex] to show
 * the [placeholder].
 */
@Composable
public fun ComboBox(
  options: List<String>,
  selectedIndex: Int?,
  onSelect: (Int) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  label: String? = null,
  placeholder: String = "Select",
  style: TextFieldStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = TextFieldDefaults.resolve(style)
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  var open by remember { mutableStateOf(false) }
  val pointer = LocalInputCapabilities.current.pointer
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val selected = selectedIndex?.let { options.getOrNull(it) }
  val borderColor = (if (open) style.focusedBorderColor else style.borderColor).copy(alpha = alpha)
  val borderWidth = if (open) maxOf(style.focusedBorderWidth, style.borderWidth) else style.borderWidth

  Column(modifier) {
    if (label != null) {
      Text(label, style = style.labelStyle, color = style.labelColor.copy(alpha = alpha))
      Spacer(Modifier.height(DittoTheme.spacing.xs))
    }
    Box {
      Row(
        Modifier
          .fillMaxWidth()
          .focusRing(interactionSource, style.shape)
          .clip(style.shape)
          .background(style.containerColor.copy(alpha = style.containerColor.alpha * alpha), style.shape)
          .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, style.shape) else Modifier)
          .clickable(interactionSource, LocalIndication.current, enabled = enabled, role = Role.DropdownList) { open = true }
          .onPreviewKeyEvent { event ->
            if (!enabled || event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
            when (event.key) {
              Key.DirectionDown, Key.Spacebar, Key.Enter -> { open = true; true }
              else -> false
            }
          }
          .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
          .semantics { contentDescription = selected ?: placeholder }
          .defaultMinSize(minHeight = style.minHeight)
          .padding(style.contentPadding),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          selected ?: placeholder,
          style = style.textStyle,
          color = (if (selected != null) style.textColor else style.placeholderColor).copy(alpha = alpha),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(style.iconSpacing))
        Icon(
          DittoIcons.chevronDown,
          contentDescription = null,
          tint = style.iconColor.copy(alpha = alpha),
          modifier = Modifier.rotate(if (open) 180f else 0f),
        )
      }
      DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
        options.forEachIndexed { index, option ->
          MenuItem(
            text = option,
            onClick = { open = false; onSelect(index) },
            leadingIcon = if (index == selectedIndex) ({ Icon(DittoIcons.check, null) }) else null,
          )
        }
      }
    }
  }
}
