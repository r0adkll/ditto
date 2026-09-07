package com.r0adkll.ditto.components

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.input.InputCapabilities
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.screenshot.assertScreenshot
import com.r0adkll.ditto.theme.DittoTheme
import kotlin.test.Test
import androidx.compose.runtime.CompositionLocalProvider

/**
 * The focus ring had twenty call sites and no screenshot at all, so its geometry — how far it sits
 * from the control, and whether its corners stay concentric with the control's — was never
 * actually looked at. It is also the one visual that only appears with a keyboard, so nobody
 * clicking through the catalog on a touchscreen would ever see it.
 */
class FocusRingScreenshotTest {
  @Test
  fun focusedControls() = assertScreenshot("focus-ring", width = 780, height = 360) {
    Column {
      listOf(Idiom.Android, Idiom.Apple, Idiom.Desktop).forEach { idiom ->
        DittoTheme(idiom = idiom) {
          // The ring only draws when a keyboard is present, which is the whole point of it.
          CompositionLocalProvider(LocalInputCapabilities provides InputCapabilities.PointerAndKeyboard) {
            Surface(color = DittoTheme.colors.background) {
              Row(
                Modifier.padding(DittoTheme.spacing.md),
                horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(idiom.name, style = DittoTheme.typography.caption, modifier = Modifier.width(60.dp))
                Focused { source -> Button(onClick = {}, interactionSource = source) { Text("Save") } }
                Focused { source -> OutlinedButton(onClick = {}, interactionSource = source) { Text("Cancel") } }
                Focused { source -> IconButton(onClick = {}, interactionSource = source) { Icon(DittoIcons.search, "Search") } }
                Focused { source -> Checkbox(checked = true, onCheckedChange = {}, interactionSource = source) }
                Focused { source ->
                  TextField(state = rememberTextFieldState("Text"), interactionSource = source, modifier = Modifier.width(140.dp))
                }
              }
            }
          }
        }
      }
    }
  }
}

/** Hands [content] an interaction source that is already focused, so the ring renders in a still. */
@Composable
private fun Focused(content: @Composable (MutableInteractionSource) -> Unit) {
  val source = remember { MutableInteractionSource() }
  LaunchedEffect(source) { source.emit(FocusInteraction.Focus()) }
  content(source)
}
