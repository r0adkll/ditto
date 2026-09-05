package com.r0adkll.ditto.material3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import kotlin.test.Test

class InteropScreenshotTest {
  @Test
  fun material3InsideDitto() = assertScreenshot("m3-interop", width = 760, height = 620) {
    DittoPreviewMatrix {
      DittoMaterialTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {}) { Text("M3") }
            FilledTonalButton(onClick = {}) { Text("Tonal") }
          }
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = {}) { Text("Outlined") }
            Switch(checked = true, onCheckedChange = {})
          }
          Card { Text("M3 Card on Ditto tokens", modifier = Modifier.padding(16.dp)) }
        }
      }
    }
  }
}
