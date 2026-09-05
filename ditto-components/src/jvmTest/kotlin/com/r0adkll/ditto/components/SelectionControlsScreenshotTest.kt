package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import kotlin.test.Test

class SelectionControlsScreenshotTest {
  @Test
  fun switches() = assertScreenshot("switch-states", width = 760, height = 400) {
    DittoPreviewMatrix {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Switch(checked = true, onCheckedChange = {})
        Switch(checked = false, onCheckedChange = {})
        Switch(checked = true, onCheckedChange = {}, enabled = false)
        Switch(checked = false, onCheckedChange = {}, enabled = false)
      }
    }
  }

  @Test
  fun checkboxes() = assertScreenshot("checkbox-states", width = 760, height = 400) {
    DittoPreviewMatrix {
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = true, onCheckedChange = {})
        Checkbox(checked = false, onCheckedChange = {})
        TriStateCheckbox(state = ToggleableState.Indeterminate, onClick = {})
        Checkbox(checked = true, onCheckedChange = {}, enabled = false)
        Checkbox(checked = false, onCheckedChange = {}, enabled = false)
      }
    }
  }

  @Test
  fun radioButtons() = assertScreenshot("radio-states", width = 760, height = 400) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
          RadioButton(selected = true, onClick = {})
          RadioButton(selected = false, onClick = {})
          RadioButton(selected = true, onClick = {}, enabled = false)
          RadioButton(selected = false, onClick = {}, enabled = false)
        }
      }
    }
  }
}
