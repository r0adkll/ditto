package com.r0adkll.ditto.spike.unstyled

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.components.MenuContent
import com.r0adkll.ditto.components.MenuItem
import com.r0adkll.ditto.components.Slider
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.input.Shortcut
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import com.r0adkll.ditto.theme.DittoTheme
import java.io.File
import kotlin.test.Test

/** Side by side: Ditto's hand-rolled component (top) vs the same look on Unstyled behaviour (bottom). */
class UnstyledParityTest {
  private val out = File("build/parity")

  @Test
  fun sliders() = assertScreenshot("parity-slider", width = 760, height = 400, goldenRoot = out) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Label("Ditto")
        Slider(value = 0.35f, onValueChange = {})
        Slider(value = 0.6f, onValueChange = {}, steps = 4)
        Label("Unstyled")
        USlider(value = 0.35f, onValueChange = {})
        USlider(value = 0.6f, onValueChange = {}, steps = 4)
      }
    }
  }

  @Test
  fun menus() = assertScreenshot("parity-menu", width = 760, height = 560, goldenRoot = out) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Label("Ditto")
        MenuContent {
          MenuItem("Rename", onClick = {}, shortcut = Shortcut(Key.R, primary = true))
          MenuItem("Delete", onClick = {}, destructive = true)
        }
        Label("Unstyled (open menu, in-tree via PortalHost)")
        UHost {
          UDropdownMenu(expanded = true, onExpandedChange = {}, items = {
            UMenuItem("Rename", onClick = {}, shortcut = Shortcut(Key.R, primary = true))
            UMenuItem("Delete", onClick = {}, destructive = true)
          }) { Text("anchor", style = DittoTheme.typography.caption) }
        }
      }
    }
  }

  @Composable
  private fun Label(text: String) = Text(text, style = DittoTheme.typography.caption, color = DittoTheme.colors.onSurfaceVariant)
}
