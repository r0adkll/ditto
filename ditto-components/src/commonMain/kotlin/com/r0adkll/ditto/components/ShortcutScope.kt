package com.r0adkll.ditto.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import com.r0adkll.ditto.input.Shortcut
import com.r0adkll.ditto.input.onShortcuts

/**
 * Registers window-level shortcuts for everything inside [content]. Key events tunnel through
 * this node in the preview phase, so bindings fire regardless of which child has focus. Takes
 * initial focus so shortcuts work before the user clicks anything.
 */
@Composable
public fun ShortcutScope(
  vararg bindings: Pair<Shortcut, () -> Unit>,
  modifier: Modifier = Modifier,
  requestInitialFocus: Boolean = true,
  content: @Composable () -> Unit,
) {
  val requester = remember { FocusRequester() }
  LaunchedEffect(requestInitialFocus) { if (requestInitialFocus) runCatching { requester.requestFocus() } }
  Box(
    modifier
      .onShortcuts(*bindings)
      .focusRequester(requester)
      .focusProperties { canFocus = true }
      .focusable(),
  ) {
    content()
  }
}
