package com.r0adkll.ditto.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.PopupProperties
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.icons.DittoIcons

/**
 * A text field with suggestions: typing filters [options] (case-insensitive contains), ↑/↓ move
 * the highlight, Enter or click fills the field and calls [onSelect], Escape closes. The popup is
 * not focusable, so typing keeps going to the field.
 */
@Composable
public fun EditableComboBox(
  state: TextFieldState,
  options: List<String>,
  modifier: Modifier = Modifier,
  onSelect: (String) -> Unit = {},
  enabled: Boolean = true,
  label: String? = null,
  placeholder: String? = null,
  maxSuggestions: Int = 8,
  style: TextFieldStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val focused by interactionSource.collectIsFocusedAsState()
  var open by remember { mutableStateOf(false) }
  var highlight by remember { mutableIntStateOf(0) }
  val query = state.text.toString()
  val suggestions = remember(query, options) {
    options.filter { it.contains(query, ignoreCase = true) && it != query }.take(maxSuggestions)
  }
  LaunchedEffect(focused, query) { open = focused && suggestions.isNotEmpty() }
  LaunchedEffect(suggestions) { highlight = 0 }

  fun choose(value: String) {
    state.setTextAndPlaceCursorAtEnd(value)
    onSelect(value)
    open = false
  }

  Box(modifier) {
    TextField(
      state = state,
      enabled = enabled,
      label = label,
      placeholder = placeholder,
      trailingIcon = { Icon(DittoIcons.chevronDown, contentDescription = null) },
      style = style,
      interactionSource = interactionSource,
      modifier = Modifier.onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        when (event.key) {
          Key.DirectionDown -> { if (!open && suggestions.isNotEmpty()) open = true else highlight = (highlight + 1).coerceAtMost(suggestions.lastIndex); true }
          Key.DirectionUp -> { if (open) { highlight = (highlight - 1).coerceAtLeast(0); true } else false }
          Key.Enter -> { if (open && suggestions.isNotEmpty()) { choose(suggestions[highlight]); true } else false }
          Key.Escape -> { if (open) { open = false; true } else false }
          else -> false
        }
      },
    )
    if (open && enabled) {
      // Non-focusable so keystrokes stay in the field; dismissal is handled by focus/Escape.
      DropdownMenu(
        expanded = true,
        onDismissRequest = { open = false },
        properties = PopupProperties(focusable = false),
      ) {
        suggestions.forEachIndexed { index, option ->
          MenuItem(
            text = option,
            onClick = { choose(option) },
            leadingIcon = if (index == highlight) ({ Icon(DittoIcons.chevronRight, null) }) else null,
          )
        }
      }
    }
  }
}
