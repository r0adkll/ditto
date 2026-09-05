package com.r0adkll.ditto.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

class ButtonScreenshotTest {
  @Test
  fun variants() = assertScreenshot("button-variants", width = 760, height = 560) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = {}) { Text("Filled") }
        TonalButton(onClick = {}) { Text("Tonal") }
        OutlinedButton(onClick = {}) { Text("Outlined") }
        TextButton(onClick = {}) { Text("Text") }
      }
    }
  }

  @Test
  fun disabledAndIcons() = assertScreenshot("button-disabled-icons", width = 760, height = 520) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(text = "Disabled", onClick = {}, enabled = false)
        Button(text = "Outlined off", onClick = {}, enabled = false, variant = ButtonVariant.Outlined)
        Button(text = "Continue", onClick = {}, leadingIcon = DittoIcons.check)
        Button(text = "Back", onClick = {}, variant = ButtonVariant.Text, leadingIcon = DittoIcons.back)
      }
    }
  }

  @Test
  fun pressed() = assertScreenshot("button-pressed", width = 760, height = 360, time = 400.milliseconds) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = {}, interactionSource = pressedSource()) { Text("Pressed") }
        TonalButton(onClick = {}, interactionSource = pressedSource()) { Text("Pressed") }
      }
    }
  }

  @Test
  fun iconButtons() = assertScreenshot("iconbutton-variants", width = 760, height = 360) {
    DittoPreviewMatrix {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(onClick = {}) { Icon(DittoIcons.more, "More") }
        FilledIconButton(onClick = {}) { Icon(DittoIcons.check, "Done") }
        TonalIconButton(onClick = {}) { Icon(DittoIcons.search, "Search") }
        OutlinedIconButton(onClick = {}) { Icon(DittoIcons.close, "Close") }
        IconButton(onClick = {}, enabled = false) { Icon(DittoIcons.back, "Back") }
      }
    }
  }

  /** Emits the press after the indication node has subscribed; a `tryEmit` at construction is lost. */
  @Composable
  private fun pressedSource(): MutableInteractionSource {
    val source = remember { MutableInteractionSource() }
    LaunchedEffect(source) { source.emit(PressInteraction.Press(Offset(24f, 16f))) }
    return source
  }
}

@Composable
private fun Text(text: String) = com.r0adkll.ditto.foundation.Text(text)
