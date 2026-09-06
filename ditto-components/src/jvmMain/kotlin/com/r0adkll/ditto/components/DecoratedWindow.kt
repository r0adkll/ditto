package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.theme.DittoTheme

/**
 * An undecorated desktop window with a Ditto-styled title bar: draggable area, centered title,
 * minimize / maximize / close controls. The controls sit at the end (Windows/Linux convention);
 * on macOS you would normally keep the native window and use `Window` instead.
 *
 * Call inside `application { }` and wrap the content in a `DittoTheme`.
 */
@Composable
public fun DecoratedWindow(
  onCloseRequest: () -> Unit,
  title: String,
  state: WindowState = rememberWindowState(),
  titleBarActions: @Composable () -> Unit = {},
  content: @Composable FrameWindowScope.() -> Unit,
) {
  Window(onCloseRequest = onCloseRequest, state = state, title = title, undecorated = true) {
    Column(Modifier.fillMaxSize()) {
      TitleBar(title = title, state = state, onClose = onCloseRequest, actions = titleBarActions)
      Box(Modifier.fillMaxSize()) { content() }
    }
  }
}

/** The title bar used by [DecoratedWindow]; 40dp, `surfaceRaised`, hairline underneath. */
@Composable
public fun FrameWindowScope.TitleBar(
  title: String,
  state: WindowState,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
  actions: @Composable () -> Unit = {},
) {
  val colors = DittoTheme.colors
  Surface(color = colors.surfaceRaised, contentColor = colors.onSurface, modifier = modifier.fillMaxWidth()) {
    Column {
      WindowDraggableArea {
        Row(Modifier.fillMaxWidth().height(40.dp).padding(horizontal = DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
          Box(Modifier.weight(1f)) { actions() }
          Text(title, style = DittoTheme.typography.label, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
          Row(Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
            IconButton(icon = DittoIcons.indeterminate, contentDescription = "Minimize", onClick = { state.isMinimized = true }, tooltip = false)
            IconButton(
              icon = DittoIcons.dropdown,
              contentDescription = if (state.placement == WindowPlacement.Maximized) "Restore" else "Maximize",
              onClick = { state.placement = if (state.placement == WindowPlacement.Maximized) WindowPlacement.Floating else WindowPlacement.Maximized },
              tooltip = false,
            )
            IconButton(icon = DittoIcons.close, contentDescription = "Close", onClick = onClose, tooltip = false)
          }
        }
      }
      HorizontalDivider()
    }
  }
}
