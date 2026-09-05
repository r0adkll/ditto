package com.r0adkll.ditto.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ColorMode

/**
 * Renders [content] once per cell of the 3 idioms × light/dark matrix (ADR-026). The same
 * composable feeds IDE previews and the screenshot harness, so both see the same pixels.
 */
@Composable
public fun DittoPreviewMatrix(
  cellWidth: Dp = 240.dp,
  idioms: List<Idiom> = Idiom.entries,
  modes: List<ColorMode> = listOf(ColorMode.Light, ColorMode.Dark),
  content: @Composable () -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
    modes.forEach { mode ->
      Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        idioms.forEach { idiom ->
          DittoTheme(idiom = idiom, colorMode = mode) {
            Surface(color = DittoTheme.colors.background) {
              Column(Modifier.width(cellWidth).padding(DittoTheme.spacing.lg)) {
                Text(
                  "${idiom.name} · ${mode.name}",
                  style = DittoTheme.typography.caption,
                  color = DittoTheme.colors.onSurfaceVariant,
                )
                Box(Modifier.padding(top = DittoTheme.spacing.md)) { content() }
              }
            }
          }
        }
      }
    }
  }
}
