package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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

/**
 * The title bar used by [DecoratedWindow]; 40dp, `surfaceRaised`, hairline underneath. macOS:
 * traffic-light controls at the start; Windows/Linux: glyph controls at the end with a red close
 * hover. Double-click toggles maximize.
 */
@Composable
public fun FrameWindowScope.TitleBar(
  title: String,
  state: WindowState,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
  actions: @Composable () -> Unit = {},
) {
  val colors = DittoTheme.colors
  val mac = remember { System.getProperty("os.name")?.lowercase()?.contains("mac") == true }
  fun toggleMaximize() {
    state.placement = if (state.placement == WindowPlacement.Maximized) WindowPlacement.Floating else WindowPlacement.Maximized
  }
  Surface(color = colors.surfaceRaised, contentColor = colors.onSurface, modifier = modifier.fillMaxWidth()) {
    Column {
      WindowDraggableArea(Modifier.combinedClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onDoubleClick = { toggleMaximize() }, onClick = {})) {
        Row(Modifier.fillMaxWidth().height(40.dp).padding(horizontal = DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
          if (mac) {
            TrafficLights(onClose = onClose, onMinimize = { state.isMinimized = true }, onZoom = { toggleMaximize() })
            Spacer(Modifier.width(DittoTheme.spacing.md))
          }
          Box(Modifier.weight(1f)) { actions() }
          Text(title, style = DittoTheme.typography.label, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
          Row(Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
            if (!mac) {
              WindowGlyph(DittoIcons.indeterminate, "Minimize") { state.isMinimized = true }
              WindowGlyph(DittoIcons.dropdown, if (state.placement == WindowPlacement.Maximized) "Restore" else "Maximize") { toggleMaximize() }
              WindowGlyph(DittoIcons.close, "Close", danger = true, onClick = onClose)
            }
          }
        }
      }
      HorizontalDivider()
    }
  }
}

@Composable
private fun TrafficLights(onClose: () -> Unit, onMinimize: () -> Unit, onZoom: () -> Unit) {
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
    listOf(
      Triple(Color(0xFFFF5F57), "Close", onClose),
      Triple(Color(0xFFFEBC2E), "Minimize", onMinimize),
      Triple(Color(0xFF28C840), "Zoom", onZoom),
    ).forEach { (color, label, action) ->
      Box(
        Modifier
          .size(12.dp)
          .clip(CircleShape)
          .background(color)
          .clickable(role = Role.Button, onClickLabel = label, onClick = action)
          .semantics { contentDescription = label },
      )
    }
  }
}

@Composable
private fun WindowGlyph(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, danger: Boolean = false, onClick: () -> Unit) {
  val source = remember { MutableInteractionSource() }
  val hovered by source.collectIsHoveredAsState()
  val colors = DittoTheme.colors
  val bg = when {
    hovered && danger -> Color(0xFFE81123)
    hovered -> colors.onSurface.copy(alpha = 0.08f)
    else -> Color.Transparent
  }
  Box(
    Modifier
      .size(width = 46.dp, height = 40.dp)
      .background(bg)
      .hoverable(source)
      .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
      .semantics { contentDescription = label },
    contentAlignment = Alignment.Center,
  ) {
    Icon(icon, contentDescription = null, tint = if (hovered && danger) Color.White else colors.onSurfaceVariant, size = 14.dp)
  }
}
