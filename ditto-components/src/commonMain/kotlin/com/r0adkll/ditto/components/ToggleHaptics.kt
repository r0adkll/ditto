package com.r0adkll.ditto.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.theme.DittoTheme

/**
 * Selection controls fire haptics on the Apple idiom only (ADR-026), through Compose's own
 * [LocalHapticFeedback] so no expect/actual is needed; other platforms are a no-op.
 */
@Stable
internal class ToggleHaptics(private val haptics: HapticFeedback?, private val enabled: Boolean) {
  fun toggled(on: Boolean) {
    if (!enabled) return
    haptics?.performHapticFeedback(if (on) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
  }

  fun selected() {
    if (!enabled) return
    haptics?.performHapticFeedback(HapticFeedbackType.SegmentTick)
  }
}

@Composable
internal fun rememberToggleHaptics(): ToggleHaptics {
  val enabled = DittoTheme.idiom == Idiom.Apple
  val haptics = LocalHapticFeedback.current
  return remember(haptics, enabled) { ToggleHaptics(haptics, enabled) }
}
