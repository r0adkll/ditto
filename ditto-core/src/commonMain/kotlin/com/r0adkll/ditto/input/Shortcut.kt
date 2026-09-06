package com.r0adkll.ditto.input

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

/** Whether this platform's primary shortcut modifier is ⌘ (macOS) rather than Ctrl. */
public expect fun platformUsesMetaForShortcuts(): Boolean

/**
 * A keyboard shortcut described platform-neutrally: [primary] means ⌘ on macOS and Ctrl elsewhere.
 * Use [label] to show it in menus ("⌘R" / "Ctrl+R") and [matches] to test a key event.
 */
@Immutable
public data class Shortcut(
  val key: Key,
  val primary: Boolean = false,
  val shift: Boolean = false,
  val alt: Boolean = false,
  /** Explicit Ctrl on macOS (rarely wanted); on other platforms this is the same as [primary]. */
  val ctrl: Boolean = false,
) {
  public fun matches(event: KeyEvent): Boolean {
    if (event.type != KeyEventType.KeyDown || event.key != key) return false
    val mac = platformUsesMetaForShortcuts()
    val wantMeta = primary && mac
    val wantCtrl = ctrl || (primary && !mac)
    return event.isMetaPressed == wantMeta &&
      event.isCtrlPressed == wantCtrl &&
      event.isShiftPressed == shift &&
      event.isAltPressed == alt
  }

  /** Human-readable form using the platform's glyphs. */
  public fun label(): String {
    val mac = platformUsesMetaForShortcuts()
    val parts = mutableListOf<String>()
    if (mac) {
      if (ctrl) parts += "⌃"
      if (alt) parts += "⌥"
      if (shift) parts += "⇧"
      if (primary) parts += "⌘"
      parts += keyName(key, mac = true)
      return parts.joinToString("")
    }
    if (primary || ctrl) parts += "Ctrl"
    if (alt) parts += "Alt"
    if (shift) parts += "Shift"
    parts += keyName(key, mac = false)
    return parts.joinToString("+")
  }

  private fun keyName(key: Key, mac: Boolean): String = when (key) {
    Key.Enter -> if (mac) "↩" else "Enter"
    Key.Escape -> if (mac) "⎋" else "Esc"
    Key.Backspace -> if (mac) "⌫" else "Backspace"
    Key.Delete -> if (mac) "⌦" else "Del"
    Key.Tab -> if (mac) "⇥" else "Tab"
    Key.Spacebar -> "Space"
    Key.DirectionUp -> "↑"
    Key.DirectionDown -> "↓"
    Key.DirectionLeft -> "←"
    Key.DirectionRight -> "→"
    Key.Comma -> ","
    Key.Period -> "."
    Key.Slash -> "/"
    Key.Minus -> "-"
    Key.Equals -> "="
    else -> key.toString().removePrefix("Key: ").removePrefix("Key(").removeSuffix(")").let { raw ->
      // Fall back to the last token of the Key's debug name, e.g. "Key: R" -> "R".
      raw.substringAfterLast(' ').uppercase().take(12)
    }
  }
}

/**
 * Runs [action] when [shortcut] is pressed anywhere inside this node (preview phase, so it wins
 * over children). Returns `true` to consume the event.
 */
public fun Modifier.onShortcut(shortcut: Shortcut, action: () -> Unit): Modifier =
  onPreviewKeyEvent { event -> if (shortcut.matches(event)) { action(); true } else false }

/** Several shortcuts at once; the first match wins. */
public fun Modifier.onShortcuts(vararg bindings: Pair<Shortcut, () -> Unit>): Modifier =
  onPreviewKeyEvent { event ->
    val hit = bindings.firstOrNull { it.first.matches(event) } ?: return@onPreviewKeyEvent false
    hit.second()
    true
  }
