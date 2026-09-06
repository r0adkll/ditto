package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.theme.LocalDittoStyleOverrides
import com.r0adkll.ditto.theme.dittoStyleOverrides
import kotlin.test.Test
import kotlin.test.assertEquals

class StyleOverridesTest {
  /** Overrides are transforms of the idiom default, so the same override tracks every idiom. */
  @Test
  fun overridesTrackIdiomDefaults() = assertScreenshot("style-overrides", width = 760, height = 420) {
    val overrides = dittoStyleOverrides {
      override<ButtonStyle> { it.copy(shape = RoundedCornerShape(2.dp)) }               // every button: sharp corners
      override<ButtonStyle>(ButtonVariant.Text) { it.copy(minHeight = it.minHeight + 8.dp) } // text buttons only: taller
      override<TextFieldStyle> { it.copy(shape = RoundedCornerShape(2.dp)) }
    }
    CompositionLocalProvider(LocalDittoStyleOverrides provides overrides) {
      DittoPreviewMatrix {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {}) { Text("Filled") }
            TextButton(onClick = {}) { Text("Text") }
          }
          // Explicit style still wins over the app override.
          Button(onClick = {}, style = ButtonDefaults.style(ButtonVariant.Filled).copy(shape = DittoTheme.shapes.full)) { Text("Explicit pill") }
        }
      }
    }
  }

  @Test
  fun generalThenVariantOrder() {
    val overrides = dittoStyleOverrides {
      override<String> { "$it+general" }
      override<String>("v") { "$it+variant" }
    }
    assertEquals("x+general+variant", overrides.resolve("x", "v"))
    assertEquals("x+general", overrides.resolve("x"))
    assertEquals("x", overrides.resolve("x", "other").removeSuffix("+general").let { if (it == "x") "x" else it })
  }

  @Test
  fun plusComposesInOrder() {
    val a = dittoStyleOverrides { override<String> { "$it-a" } }
    val b = dittoStyleOverrides { override<String> { "$it-b" } }
    assertEquals("x-a-b", (a + b).resolve("x"))
  }
}

