package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

class OverlaysScreenshotTest {
  @Test
  fun menu() = assertScreenshot("menu", width = 760, height = 620) {
    DittoPreviewMatrix {
      MenuContent {
        MenuItem("Share", onClick = {}, leadingIcon = { Icon(DittoIcons.forward, null) })
        MenuItem("Rename", onClick = {}, trailingIcon = { Text("⌘R") })
        MenuDivider()
        MenuItem("Disabled", onClick = {}, enabled = false)
        MenuItem("Delete", onClick = {}, destructive = true, leadingIcon = { Icon(DittoIcons.close, null) })
      }
    }
  }

  @Test
  fun alertDialog() = assertScreenshot("alert-dialog", width = 1000, height = 640) {
    DittoPreviewMatrix(cellWidth = 320.dp) {
      AlertDialogContent(
        title = "Delete recording?",
        text = "This removes the file from every device. You can't undo this.",
        confirmButton = { Button(text = "Delete", onClick = {}) },
        dismissButton = { Button(text = "Cancel", onClick = {}, variant = ButtonVariant.Text) },
      )
    }
  }

  @Test
  fun progress() = assertScreenshot("progress", width = 760, height = 460, time = 350.milliseconds) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
          CircularProgressIndicator()
          CircularProgressIndicator(progress = { 0.65f })
        }
        LinearProgressIndicator(progress = { 0.65f })
        LinearProgressIndicator()
      }
    }
  }
}
