package com.r0adkll.ditto.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.theme.DittoTheme

public object SearchBarDefaults {
  /** Android: 56dp pill on `surfaceOverlay`. Apple: 36dp rounded neutral fill. Desktop: the bordered input. */
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): TextFieldStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val base = TextFieldDefaults.style(idiom)
    return when (idiom) {
      Idiom.Android -> base.copy(
        shape = shapes.full,
        minHeight = 56.dp,
        containerColor = colors.surfaceOverlay,
        borderWidth = 0.dp,
        borderColor = Color.Transparent,
        focusedBorderWidth = 0.dp,
        focusedBorderColor = Color.Transparent,
        contentPadding = PaddingValues(horizontal = DittoTheme.spacing.lg, vertical = DittoTheme.spacing.sm),
      )
      Idiom.Apple -> base.copy(
        minHeight = 36.dp,
        shape = shapes.small,
        contentPadding = PaddingValues(horizontal = DittoTheme.spacing.sm, vertical = DittoTheme.spacing.xs),
        iconColor = colors.onSurfaceVariant,
      )
      Idiom.Desktop -> base
    }
  }
}

/**
 * A text field dressed as a search input: leading search glyph, clear button while text is
 * present, search IME action. Wire [onSearch] to run the query.
 */
@Composable
public fun SearchBar(
  state: TextFieldState,
  modifier: Modifier = Modifier,
  placeholder: String = "Search",
  onSearch: ((String) -> Unit)? = null,
  enabled: Boolean = true,
  style: TextFieldStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: SearchBarDefaults.style()
  TextField(
    state = state,
    modifier = modifier,
    enabled = enabled,
    placeholder = placeholder,
    leadingIcon = { Icon(DittoIcons.search, contentDescription = null) },
    trailingIcon = if (state.text.isNotEmpty()) {
      {
        IconButton(
          onClick = { state.clearText() },
          style = IconButtonDefaults.style(IconButtonVariant.Standard).copy(size = 28.dp, iconSize = 18.dp, contentColor = style.iconColor),
        ) { Icon(DittoIcons.clear, contentDescription = "Clear search") }
      }
    } else {
      null
    },
    lineLimits = TextFieldLineLimits.SingleLine,
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    onKeyboardAction = if (onSearch != null) KeyboardActionHandler { onSearch(state.text.toString()) } else null,
    style = style,
    interactionSource = interactionSource,
  )
}
