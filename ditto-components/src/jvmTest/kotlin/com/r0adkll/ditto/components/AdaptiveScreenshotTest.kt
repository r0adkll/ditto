package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ColorMode
import kotlin.test.Test

class AdaptiveScreenshotTest {
  private val destinations = listOf(
    NavigationDestination("Search", DittoIcons.search(Idiom.Desktop)),
    NavigationDestination("Done", DittoIcons.check(Idiom.Desktop), badge = "2"),
    NavigationDestination("More", DittoIcons.more(Idiom.Desktop)),
  )

  @Test
  fun navigationSuiteAcrossWidths() = assertScreenshot("navigation-suite", width = 1180, height = 300) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      listOf(320.dp to Idiom.Android, 400.dp to Idiom.Apple, 420.dp to Idiom.Desktop).forEach { (w, idiom) ->
        DittoTheme(idiom = idiom, colorMode = ColorMode.Light) {
          Surface(color = DittoTheme.colors.background) {
            Box(Modifier.size(w, 280.dp)) {
              NavigationSuite(
                destinations = destinations,
                selectedIndex = 0,
                onSelect = {},
                // Force each container type regardless of width so all three appear side by side.
                type = { width -> when { width < 340.dp -> NavigationSuiteType.Bar; width < 410.dp -> NavigationSuiteType.Rail; else -> NavigationSuiteType.Sidebar } },
              ) { padding -> Column(Modifier.fillMaxSize().padding(padding).padding(12.dp)) { Text("Content", style = DittoTheme.typography.body) } }
            }
          }
        }
      }
    }
  }

  @Test
  fun rangeSliderAndBackButton() = assertScreenshot("range-slider-back", width = 760, height = 420) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RangeSlider(value = 0.2f..0.7f, onValueChange = {})
        RangeSlider(value = 0.25f..0.75f, onValueChange = {}, steps = 3)
        Row { BackButton(onClick = {}, label = "Library") }
      }
    }
  }
}

