package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import kotlin.test.Test

class NavigationScreenshotTest {
  @Test
  fun navigationBar() = assertScreenshot("navigation-bar", width = 760, height = 340) {
    DittoPreviewMatrix {
      NavigationBar(windowInsets = WindowInsets(0)) {
        NavigationItem(selected = true, onClick = {}, icon = DittoIcons.search, label = "Search")
        NavigationItem(selected = false, onClick = {}, icon = DittoIcons.check, label = "Done", badge = "3")
        NavigationItem(selected = false, onClick = {}, icon = DittoIcons.more, label = "More")
      }
    }
  }

  @Test
  fun railAndSidebar() = assertScreenshot("navigation-rail-sidebar", width = 760, height = 560) {
    DittoPreviewMatrix {
      Row(Modifier.height(200.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        NavigationRail(windowInsets = WindowInsets(0)) {
          NavigationItem(selected = true, onClick = {}, icon = DittoIcons.search, label = "Find")
          NavigationItem(selected = false, onClick = {}, icon = DittoIcons.check, label = "Done")
        }
        Sidebar(windowInsets = WindowInsets(0), modifier = Modifier.fillMaxWidth(), style = SidebarSized()) {
          SidebarItem(selected = true, onClick = {}, label = "Library", icon = DittoIcons.search)
          SidebarItem(selected = false, onClick = {}, label = "Queue", icon = DittoIcons.check, badge = "12")
          SidebarItem(selected = false, onClick = {}, label = "Settings", icon = DittoIcons.more)
        }
      }
    }
  }

  @Test
  fun chipsAndBadges() = assertScreenshot("chips-badges", width = 760, height = 460) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Chip("Fiction", selected = true, onClick = {})
          Chip("Audio", onClick = {})
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Chip("Removable", onDismiss = {})
          Chip("Disabled", onClick = {}, enabled = false)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
          Badge("3")
          Badge("99+")
          Badge()
          BadgedBox(badge = "7") { Text("Inbox") }
        }
      }
    }
  }

  @Test
  fun sheet() = assertScreenshot("sheet", width = 760, height = 440) {
    DittoPreviewMatrix {
      SheetContent {
        ListItem(headline = "Share", onClick = {})
        ListItem(headline = "Add to queue", onClick = {})
      }
    }
  }

  @Test
  fun radioGroupAndSlotTitle() = assertScreenshot("radio-group", width = 760, height = 400) {
    DittoPreviewMatrix {
      Column {
        RadioGroup(options = listOf("Small", "Medium", "Large"), selectedIndex = 1, onSelect = {})
      }
    }
  }
}

@androidx.compose.runtime.Composable
private fun SidebarSized(): NavigationContainerStyle = NavigationDefaults.sidebarStyle().copy(size = 120.dp)
