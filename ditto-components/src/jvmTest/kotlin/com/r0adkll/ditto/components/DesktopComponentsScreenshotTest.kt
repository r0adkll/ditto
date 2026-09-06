package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import com.r0adkll.ditto.theme.DittoTheme
import kotlin.test.Test

class DesktopComponentsScreenshotTest {
  @Test
  fun comboLinkBanner() = assertScreenshot("combo-link-banner", width = 760, height = 720) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ComboBox(options = listOf("Light", "Dark", "System"), selectedIndex = 1, onSelect = {}, label = "Appearance", modifier = Modifier.fillMaxWidth())
        ComboBox(options = listOf("A", "B"), selectedIndex = null, onSelect = {}, placeholder = "Choose one", modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          Link("Learn more", onClick = {})
          Link("Docs", onClick = {}, external = true)
          Link("Disabled", onClick = {}, enabled = false)
        }
        Banner("Your library is up to date.", kind = BannerKind.Success)
        Banner("Sync failed. Check your connection and try again.", kind = BannerKind.Error, title = "Sync error", onDismiss = {},
          actions = { Button(text = "Retry", onClick = {}, variant = ButtonVariant.Text) })
      }
    }
  }

  @Test
  fun treeAndSplitPane() = assertScreenshot("tree-splitpane", width = 760, height = 560) {
    DittoPreviewMatrix {
      Box(Modifier.fillMaxWidth().height(200.dp)) {
        HorizontalSplitPane(
          state = rememberSplitPaneState(0.72f),
          minFirst = 60.dp, minSecond = 40.dp,
          first = {
            Tree(
              roots = listOf(
                TreeNode("lib", "Library", DittoIcons.more, children = listOf(
                  TreeNode("books", "Books", children = listOf(TreeNode("b1", "Dune"), TreeNode("b2", "Hyperion"))),
                  TreeNode("pod", "Podcasts"),
                )),
                TreeNode("set", "Settings", DittoIcons.check),
              ),
              state = rememberTreeState(expanded = setOf("lib", "books"), selected = "b1"),
            )
          },
          second = {
            Surface(color = DittoTheme.colors.surfaceRaised, modifier = Modifier.fillMaxSize()) {
              Box(Modifier.padding(8.dp)) { Text("Detail", style = DittoTheme.typography.bodySmall) }
            }
          },
        )
      }
    }
  }

  @Test
  fun scrollbar() = assertScreenshot("scrollbar", width = 760, height = 300) {
    DittoPreviewMatrix {
      val scroll = rememberScrollState()
      Box(Modifier.fillMaxWidth().height(120.dp)) {
        Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
          (1..20).forEach { Text("Row $it", style = DittoTheme.typography.bodySmall) }
        }
        VerticalScrollbar(scroll, Modifier.align(Alignment.CenterEnd).fillMaxHeight())
      }
    }
  }
}
