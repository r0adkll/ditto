package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import kotlin.test.Test

class MenusTabsSheetsScreenshotTest {
  @Test
  fun checkableAndSubmenu() = assertScreenshot("menu-checkable-submenu", width = 760, height = 560) {
    DittoPreviewMatrix {
      MenuContent {
        CheckableMenuItem("Show completed", checked = true, onCheckedChange = {})
        CheckableMenuItem("Show archived", checked = false, onCheckedChange = {})
        MenuDivider()
        SubmenuItem("Sort by", leadingIcon = { Icon(DittoIcons.more, null) }) {
          MenuItem("Title", onClick = {})
        }
        MenuItem("Rename", onClick = {})
      }
    }
  }

  @Test
  fun iconAndScrollableTabs() = assertScreenshot("tabs-icons-scrollable", width = 760, height = 520) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TabRow(
          tabs = listOf(TabItem("Search", DittoIcons.search), TabItem("Done", DittoIcons.check), TabItem("More", DittoIcons.more)),
          selectedIndex = 1,
          onSelect = {},
        )
        TabRow(
          tabs = listOf(TabItem(icon = DittoIcons.search), TabItem(icon = DittoIcons.check), TabItem(icon = DittoIcons.more)),
          selectedIndex = 0,
          onSelect = {},
        )
        TabRow(
          tabs = listOf("Recent", "Favorites", "Podcasts", "Audiobooks", "Series").map { TabItem(it) },
          selectedIndex = 2,
          onSelect = {},
          scrollable = true,
        )
      }
    }
  }

  @Test
  fun sheetWithDetents() = assertScreenshot("sheet-detents", width = 760, height = 360) {
    DittoPreviewMatrix {
      SheetContent {
        Column(Modifier.fillMaxWidth().height(80.dp)) {
          Text("Detents: Medium, Full", modifier = Modifier.width(160.dp))
        }
      }
    }
  }
}
