package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.input.Shortcut
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import kotlin.test.Test

class DesktopExtrasScreenshotTest {
  @Test
  fun editableComboAndShortcutMenu() = assertScreenshot("editable-combo-shortcuts", width = 760, height = 520) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EditableComboBox(state = rememberTextFieldState("Sher"), options = listOf("Sherlock Holmes", "Sherwood"), label = "Author", modifier = Modifier.fillMaxWidth())
        MenuContent {
          MenuItem("Rename", onClick = {}, shortcut = Shortcut(Key.R, primary = true))
          MenuItem("Find", onClick = {}, shortcut = Shortcut(Key.F, primary = true, shift = true))
          MenuItem("Delete", onClick = {}, shortcut = Shortcut(Key.Backspace, primary = true), destructive = true)
        }
      }
    }
  }
}
