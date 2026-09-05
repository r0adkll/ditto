package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import kotlin.test.Test

class ControlsScreenshotTest {
  @Test
  fun segmentedControl() = assertScreenshot("segmented-control", width = 760, height = 360) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SegmentedControl(options = listOf("Day", "Week", "Month"), selectedIndex = 1, onSelect = {}, modifier = Modifier.fillMaxWidth())
        SegmentedControl(options = listOf("On", "Off"), selectedIndex = 0, onSelect = {}, enabled = false, modifier = Modifier.fillMaxWidth())
      }
    }
  }

  @Test
  fun tabs() = assertScreenshot("tabs", width = 760, height = 320) {
    DittoPreviewMatrix {
      TabRow(tabs = listOf("All", "Read", "Queue"), selectedIndex = 0, onSelect = {})
    }
  }

  @Test
  fun sliders() = assertScreenshot("slider", width = 760, height = 520) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Slider(value = 0.35f, onValueChange = {})
        Slider(value = 0.6f, onValueChange = {}, steps = 4)
        Slider(value = 0.8f, onValueChange = {}, enabled = false)
      }
    }
  }
}
