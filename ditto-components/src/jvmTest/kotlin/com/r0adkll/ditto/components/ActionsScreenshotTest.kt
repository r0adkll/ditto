package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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

class ActionsScreenshotTest {
  @Test
  fun toggleAndFab() = assertScreenshot("toggle-fab", width = 760, height = 520) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
          ToggleButton(checked = true, onCheckedChange = {}) { Icon(DittoIcons.check, null); Text(" On") }
          ToggleButton(checked = false, onCheckedChange = {}) { Text("Off") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
          ToggleButton(checked = true, onCheckedChange = {}, enabled = false) { Text("Disabled") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
          FloatingActionButton(onClick = {}, icon = DittoIcons.check, contentDescription = "Add")
          FloatingActionButton(onClick = {}, icon = DittoIcons.search, contentDescription = null, text = "Search")
        }
      }
    }
  }

  @Test
  fun searchAndSnackbar() = assertScreenshot("search-snackbar", width = 760, height = 520, time = 800.milliseconds) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SearchBar(state = rememberTextFieldState(), modifier = Modifier.fillMaxWidth())
        SearchBar(state = rememberTextFieldState("Sherlock"), modifier = Modifier.fillMaxWidth())
        val host = remember { SnackbarHostState() }
        LaunchedEffect(host) { host.showSnackbar("Saved to library", actionLabel = "Undo", duration = SnackbarDuration.Indefinite) }
        SnackbarHost(host, style = SnackbarDefaults.style().copy(margin = 0.dp))
      }
    }
  }
}
