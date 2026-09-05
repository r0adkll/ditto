package com.r0adkll.ditto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ElevationLevel
import kotlin.test.Test

class TokensScreenshotTest {
  @Test
  fun colorRoles() = assertScreenshot("tokens-colors", width = 760, height = 520) {
    DittoPreviewMatrix {
      val c = DittoTheme.colors
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(
          "accent" to c.accent, "surface" to c.surface, "surfaceRaised" to c.surfaceRaised,
          "surfaceOverlay" to c.surfaceOverlay, "outline" to c.outline, "error" to c.error,
          "success" to c.success, "warning" to c.warning,
        ).forEach { (name, color) ->
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(20.dp).background(color, DittoTheme.shapes.extraSmall))
            Text(name, style = DittoTheme.typography.caption)
          }
        }
      }
    }
  }

  @Test
  fun typographyScale() = assertScreenshot("tokens-typography", width = 760, height = 760) {
    DittoPreviewMatrix {
      val t = DittoTheme.typography
      Column {
        Text("Display", style = t.display)
        Text("Title", style = t.title)
        Text("Heading", style = t.heading)
        Text("Subheading", style = t.subheading)
        Text("Body text", style = t.body)
        Text("Body small", style = t.bodySmall)
        Text("Label", style = t.label)
        Text("Caption", style = t.caption)
      }
    }
  }

  @Test
  fun surfacesAndElevation() = assertScreenshot("tokens-surfaces", width = 760, height = 520) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ElevationLevel.entries.forEach { level ->
          Surface(shape = DittoTheme.shapes.medium, elevation = level, modifier = Modifier.fillMaxWidth().height(28.dp)) {
            Box(Modifier.padding(horizontal = 8.dp)) { Text(level.name, style = DittoTheme.typography.caption) }
          }
        }
      }
    }
  }

  @Test
  fun systemIcons() = assertScreenshot("tokens-icons", width = 760, height = 360) {
    DittoPreviewMatrix {
      IconGrid()
    }
  }

  @Composable
  private fun IconGrid() {
    val icons = listOf(
      DittoIcons.back, DittoIcons.forward, DittoIcons.chevronDown, DittoIcons.chevronRight, DittoIcons.close,
      DittoIcons.check, DittoIcons.clear, DittoIcons.more, DittoIcons.search, DittoIcons.dropdown,
      DittoIcons.visibility, DittoIcons.visibilityOff, DittoIcons.indeterminate,
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      icons.chunked(7).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          row.forEach { Icon(it, contentDescription = null) }
        }
      }
    }
  }
}
