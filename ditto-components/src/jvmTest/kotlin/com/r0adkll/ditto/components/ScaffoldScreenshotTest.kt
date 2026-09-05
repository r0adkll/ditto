package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import kotlin.test.Test

class ScaffoldScreenshotTest {
  @Test
  fun smallTopBar() = assertScreenshot("scaffold-small", width = 760, height = 620) {
    DittoPreviewMatrix { Screen(TopBarVariant.Small) }
  }

  @Test
  fun largeTopBar() = assertScreenshot("scaffold-large", width = 760, height = 720) {
    DittoPreviewMatrix { Screen(TopBarVariant.Large) }
  }

  @Composable
  private fun Screen(variant: TopBarVariant) {
    Scaffold(
      modifier = Modifier.size(width = 208.dp, height = 240.dp),
      contentWindowInsets = WindowInsets(0),
      topBar = {
        TopBar(
          title = "Library",
          variant = variant,
          navigationIcon = { BackButton(onClick = {}) },
          actions = { IconButton(icon = DittoIcons.search, contentDescription = "Search", onClick = {}, tooltip = false) },
          windowInsets = WindowInsets(0),
        )
      },
    ) { padding ->
      Column(Modifier.fillMaxSize().padding(padding)) {
        ListItem(headline = "First row", supporting = "Under the bar", leading = { Icon(DittoIcons.check, null) })
        HorizontalDivider(startIndent = 16.dp)
        ListItem(headline = "Second row", onClick = {})
      }
    }
  }
}
