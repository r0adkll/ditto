package com.r0adkll.ditto.input

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * What input the user can plausibly use right now. Hover, cursor, and focus-ring visuals are a
 * function of these capabilities, not of the idiom (ADR-008): the idiom only decides how they look.
 */
@Immutable
public data class InputCapabilities(
  /** A hover-capable pointer (mouse / trackpad) is present. */
  val pointer: Boolean,
  /** A hardware keyboard is present, so focus must be visible. */
  val keyboard: Boolean,
) {
  public companion object {
    public val Touch: InputCapabilities = InputCapabilities(pointer = false, keyboard = false)
    public val PointerAndKeyboard: InputCapabilities = InputCapabilities(pointer = true, keyboard = true)
  }
}

/** Best-effort default for the current platform. Apps may override for e.g. Android with a mouse. */
public expect fun platformInputCapabilities(): InputCapabilities

public val LocalInputCapabilities: ProvidableCompositionLocal<InputCapabilities> =
  staticCompositionLocalOf { platformInputCapabilities() }
