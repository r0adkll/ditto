package com.r0adkll.ditto.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.components.*
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.theme.DittoTheme
import kotlinx.coroutines.launch

/**
 * One demo per component, small enough to sit in a docs-page iframe and showing only that
 * component. Each owns its state so it can be embedded anywhere, including several at once.
 */

@Composable private fun Stack(content: @Composable ColumnScopeAlias.() -> Unit) =
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md), content = content)

private typealias ColumnScopeAlias = androidx.compose.foundation.layout.ColumnScope

@Composable private fun Line(content: @Composable () -> Unit) =
  Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) { content() }

// ---------------------------------------------------------------- actions

@Composable
internal fun ButtonDemo() = Stack {
  Line {
    Button(onClick = {}) { Text("Filled") }
    TonalButton(onClick = {}) { Text("Tonal") }
    OutlinedButton(onClick = {}) { Text("Outlined") }
    TextButton(onClick = {}) { Text("Text") }
  }
  Line {
    Button(text = "Continue", onClick = {}, leadingIcon = DittoIcons.check)
    Button(text = "Disabled", onClick = {}, enabled = false)
  }
}

@Composable
internal fun IconButtonDemo() = Line {
  IconButton(onClick = {}) { Icon(DittoIcons.more, "More") }
  FilledIconButton(onClick = {}) { Icon(DittoIcons.check, "Done") }
  TonalIconButton(onClick = {}) { Icon(DittoIcons.search, "Search") }
  OutlinedIconButton(onClick = {}) { Icon(DittoIcons.close, "Close") }
  IconButton(onClick = {}, enabled = false) { Icon(DittoIcons.back, "Back") }
}

@Composable
internal fun ToggleButtonDemo() {
  var on by remember { mutableStateOf(true) }
  var bold by remember { mutableStateOf(false) }
  Line {
    ToggleButton(checked = on, onCheckedChange = { on = it }) { Icon(DittoIcons.check, null); Text(if (on) " On" else " Off") }
    ToggleButton(checked = bold, onCheckedChange = { bold = it }) { Text("Bold") }
    ToggleButton(checked = true, onCheckedChange = {}, enabled = false) { Text("Disabled") }
  }
}

@Composable
internal fun FabDemo() = Line {
  FloatingActionButton(onClick = {}, icon = DittoIcons.check, contentDescription = "Add")
  FloatingActionButton(onClick = {}, icon = DittoIcons.search, contentDescription = null, text = "Search")
}

// ---------------------------------------------------------------- selection

@Composable
internal fun SwitchDemo() {
  var wifi by remember { mutableStateOf(true) }
  var cell by remember { mutableStateOf(false) }
  Line {
    Switch(checked = wifi, onCheckedChange = { wifi = it })
    Switch(checked = cell, onCheckedChange = { cell = it })
    Switch(checked = true, onCheckedChange = null, enabled = false)
    Switch(checked = false, onCheckedChange = null, enabled = false)
  }
}

@Composable
internal fun CheckboxDemo() {
  var a by remember { mutableStateOf(true) }
  var b by remember { mutableStateOf(false) }
  var tri by remember { mutableStateOf(ToggleableState.Indeterminate) }
  Line {
    Checkbox(checked = a, onCheckedChange = { a = it })
    Checkbox(checked = b, onCheckedChange = { b = it })
    TriStateCheckbox(
      state = tri,
      onClick = {
        tri = when (tri) {
          ToggleableState.On -> ToggleableState.Off
          ToggleableState.Off -> ToggleableState.Indeterminate
          ToggleableState.Indeterminate -> ToggleableState.On
        }
      },
    )
    Checkbox(checked = true, onCheckedChange = null, enabled = false)
  }
}

@Composable
internal fun RadioButtonDemo() {
  var size by remember { mutableIntStateOf(1) }
  RadioGroup(options = listOf("Small", "Medium", "Large"), selectedIndex = size, onSelect = { size = it }, modifier = Modifier.width(220.dp))
}

// ---------------------------------------------------------------- text input

@Composable
internal fun TextFieldDemo() = Stack {
  val email = rememberTextFieldState("not-an-email")
  Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
    TextField(state = rememberTextFieldState(), label = "Name", placeholder = "Ada Lovelace", modifier = Modifier.fillMaxWidth())
    TextField(
      state = email,
      label = "Email",
      isError = !email.text.contains('@'),
      supportingText = if (email.text.contains('@')) "Looks good" else "Enter a valid address",
      modifier = Modifier.fillMaxWidth(),
    )
    TextField(state = rememberTextFieldState("Read only"), enabled = false, modifier = Modifier.fillMaxWidth())
  }
}

@Composable
internal fun SearchBarDemo() {
  Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
    SearchBar(state = rememberTextFieldState(), modifier = Modifier.fillMaxWidth())
    SearchBar(state = rememberTextFieldState("Sherlock"), modifier = Modifier.fillMaxWidth())
  }
}

@Composable
internal fun ComboBoxDemo() {
  var appearance by remember { mutableStateOf<Int?>(1) }
  Column(Modifier.width(280.dp), verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
    ComboBox(options = listOf("Light", "Dark", "System"), selectedIndex = appearance, onSelect = { appearance = it }, label = "Appearance", modifier = Modifier.fillMaxWidth())
    EditableComboBox(
      state = rememberTextFieldState(),
      options = listOf("Sherlock Holmes", "Sherwood Anderson", "Mary Shelley"),
      label = "Author",
      placeholder = "Type to search",
      modifier = Modifier.fillMaxWidth(),
    )
  }
}

// ---------------------------------------------------------------- value pickers

@Composable
internal fun SliderDemo() {
  var value by remember { mutableStateOf(0.35f) }
  var stepped by remember { mutableStateOf(0.5f) }
  Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm)) {
    Slider(value = value, onValueChange = { value = it })
    Slider(value = stepped, onValueChange = { stepped = it }, steps = 4)
    Slider(value = 0.8f, onValueChange = {}, enabled = false)
  }
}

@Composable
internal fun RangeSliderDemo() {
  var range by remember { mutableStateOf(0.2f..0.7f) }
  Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm)) {
    RangeSlider(value = range, onValueChange = { range = it })
    RangeSlider(value = 0.25f..0.75f, onValueChange = {}, steps = 3)
  }
}

@Composable
internal fun SegmentedControlDemo() {
  var segment by remember { mutableIntStateOf(1) }
  Column(Modifier.width(320.dp)) {
    SegmentedControl(options = listOf("Day", "Week", "Month"), selectedIndex = segment, onSelect = { segment = it }, modifier = Modifier.fillMaxWidth())
  }
}

@Composable
internal fun TabsDemo() {
  var tab by remember { mutableIntStateOf(0) }
  var scrollTab by remember { mutableIntStateOf(2) }
  Column(Modifier.width(340.dp), verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.lg)) {
    TabRow(tabs = listOf("Books", "Authors", "Series"), selectedIndex = tab, onSelect = { tab = it })
    TabRow(
      tabs = listOf(TabItem("Search", DittoIcons.search), TabItem("Done", DittoIcons.check), TabItem("More", DittoIcons.more)),
      selectedIndex = tab,
      onSelect = { tab = it },
    )
    TabRow(
      tabs = listOf("Recent", "Favorites", "Podcasts", "Audiobooks", "Series").map { TabItem(it) },
      selectedIndex = scrollTab,
      onSelect = { scrollTab = it },
      scrollable = true,
    )
  }
}

// ---------------------------------------------------------------- containment

@Composable
internal fun CardDemo() = Line {
  CardVariant.entries.forEach { variant ->
    Card(variant = variant, onClick = {}, modifier = Modifier.width(150.dp)) {
      Column(Modifier.padding(DittoTheme.spacing.lg)) {
        Text(variant.name, style = DittoTheme.typography.subheading)
        Text("Tap me", style = DittoTheme.typography.bodySmall, color = DittoTheme.colors.onSurfaceVariant)
      }
    }
  }
}

@Composable
internal fun ListItemDemo() {
  var wifi by remember { mutableStateOf(true) }
  Card(variant = CardVariant.Outlined, modifier = Modifier.width(340.dp)) {
    ListItem(
      headline = "Wi-Fi",
      supporting = if (wifi) "Connected" else "Off",
      leading = { Icon(DittoIcons.check, null) },
      trailing = { Switch(checked = wifi, onCheckedChange = { wifi = it }) },
    )
    HorizontalDivider(startIndent = DittoTheme.spacing.lg)
    ListItem(headline = "Notifications", supporting = "Sounds, badges, banners", onClick = {})
    HorizontalDivider(startIndent = DittoTheme.spacing.lg)
    ListItem(headline = "About", onClick = {})
  }
}

@Composable
internal fun ChipDemo() {
  var chips by remember { mutableStateOf(setOf(0)) }
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm)) {
    Line {
      listOf("Fiction", "Audio", "Podcasts").forEachIndexed { i, label ->
        Chip(label, selected = i in chips, onClick = { chips = if (i in chips) chips - i else chips + i })
      }
    }
    Line {
      Chip("Removable", onDismiss = {})
      Chip("Disabled", onClick = {}, enabled = false)
    }
  }
}

@Composable
internal fun BadgeDemo() = Line {
  Badge("3")
  Badge("99+")
  Badge()
  BadgedBox(badge = "7") { Text("Inbox") }
}

@Composable
internal fun BannerDemo() {
  var shown by remember { mutableStateOf(true) }
  Column(Modifier.width(400.dp), verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
    Banner("Your library is up to date.", kind = BannerKind.Success)
    if (shown) {
      Banner(
        "Sync failed. Check your connection and try again.",
        kind = BannerKind.Error,
        title = "Sync error",
        onDismiss = { shown = false },
        actions = { Button(text = "Retry", onClick = {}, variant = ButtonVariant.Text) },
      )
    }
  }
}

// ---------------------------------------------------------------- overlays

@Composable
internal fun TooltipDemo() = Line {
  Text("Hover or long-press:", style = DittoTheme.typography.bodySmall)
  IconButton(icon = DittoIcons.more, contentDescription = "More options", onClick = {})
  IconButton(icon = DittoIcons.search, contentDescription = "Search the library", onClick = {})
}

@Composable
internal fun MenuDemo() {
  var open by remember { mutableStateOf(false) }
  var showCompleted by remember { mutableStateOf(true) }
  Box {
    Button(text = "Open menu", onClick = { open = true }, variant = ButtonVariant.Tonal, leadingIcon = DittoIcons.chevronDown)
    DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
      MenuItem("Share", onClick = { open = false }, leadingIcon = { Icon(DittoIcons.forward, null) })
      MenuItem("Rename", onClick = { open = false })
      MenuDivider()
      CheckableMenuItem("Show completed", checked = showCompleted, onCheckedChange = { showCompleted = it })
      SubmenuItem("Sort by") {
        listOf("Title", "Author", "Recent").forEach { MenuItem(it, onClick = { open = false }) }
      }
      MenuItem("Delete", onClick = { open = false }, destructive = true, leadingIcon = { Icon(DittoIcons.close, null) })
    }
  }
}

@Composable
internal fun DialogDemo() {
  var open by remember { mutableStateOf(false) }
  Button(text = "Show dialog", onClick = { open = true }, variant = ButtonVariant.Outlined)
  if (open) {
    AlertDialog(
      onDismissRequest = { open = false },
      title = "Delete recording?",
      text = "This removes the file from every device. You can't undo this.",
      confirmButton = { Button(text = "Delete", onClick = { open = false }) },
      dismissButton = { Button(text = "Cancel", onClick = { open = false }, variant = ButtonVariant.Text) },
    )
  }
}

@Composable
internal fun SheetDemo() {
  var open by remember { mutableStateOf(false) }
  Button(text = "Open sheet", onClick = { open = true }, variant = ButtonVariant.Tonal)
  if (open) {
    val state = rememberSheetState(detents = listOf(SheetDetent.Medium, SheetDetent.Full), initial = SheetDetent.Medium)
    ModalSheet(onDismissRequest = { open = false }, state = state) {
      Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(
          "Drag up for the full detent",
          style = DittoTheme.typography.label,
          color = DittoTheme.colors.onSurfaceVariant,
          modifier = Modifier.padding(horizontal = DittoTheme.spacing.lg),
        )
        (1..20).forEach { ListItem(headline = "Row $it", onClick = { open = false }) }
      }
    }
  }
}

@Composable
internal fun SnackbarDemo() {
  val host = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  Box(Modifier.fillMaxWidth().height(140.dp)) {
    Button(
      text = "Show snackbar",
      onClick = { scope.launch { host.showSnackbar("Saved to library", actionLabel = "Undo") } },
      variant = ButtonVariant.Tonal,
    )
    SnackbarHost(host, modifier = Modifier.align(Alignment.BottomCenter))
  }
}

@Composable
internal fun ProgressIndicatorDemo() {
  var progress by remember { mutableStateOf(0.4f) }
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
    Line {
      CircularProgressIndicator()
      CircularProgressIndicator(progress = { progress })
      Button(text = "+10%", onClick = { progress = (progress + 0.1f).let { if (it > 1f) 0f else it } }, variant = ButtonVariant.Text)
    }
    Column(Modifier.width(260.dp), verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm)) {
      LinearProgressIndicator(progress = { progress })
      LinearProgressIndicator()
    }
  }
}

@Composable
internal fun PullToRefreshDemo() {
  var refreshing by remember { mutableStateOf(false) }
  Column(Modifier.width(320.dp).height(200.dp)) {
    Text("Pull down to refresh", style = DittoTheme.typography.bodySmall, color = DittoTheme.colors.onSurfaceVariant)
    PullToRefreshBox(isRefreshing = refreshing, onRefresh = { refreshing = true }, modifier = Modifier.fillMaxSize()) {
      Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        (1..12).forEach { ListItem(headline = "Row $it") }
      }
    }
  }
}

// ---------------------------------------------------------------- navigation

@Composable
internal fun NavigationDemo2() {
  var dest by remember { mutableIntStateOf(0) }
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.lg)) {
    NavigationBar(windowInsets = WindowInsets(0), modifier = Modifier.width(340.dp)) {
      NavigationItem(selected = dest == 0, onClick = { dest = 0 }, icon = DittoIcons.search, label = "Search")
      NavigationItem(selected = dest == 1, onClick = { dest = 1 }, icon = DittoIcons.check, label = "Done", badge = "3")
      NavigationItem(selected = dest == 2, onClick = { dest = 2 }, icon = DittoIcons.more, label = "More")
    }
    Row(Modifier.height(170.dp), horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.lg)) {
      NavigationRail(windowInsets = WindowInsets(0)) {
        NavigationItem(selected = dest == 0, onClick = { dest = 0 }, icon = DittoIcons.search, label = "Find")
        NavigationItem(selected = dest == 1, onClick = { dest = 1 }, icon = DittoIcons.check, label = "Done")
      }
      Sidebar(windowInsets = WindowInsets(0)) {
        SidebarItem(selected = dest == 0, onClick = { dest = 0 }, label = "Library", icon = DittoIcons.search)
        SidebarItem(selected = dest == 1, onClick = { dest = 1 }, label = "Queue", icon = DittoIcons.check, badge = "12")
        SidebarItem(selected = dest == 2, onClick = { dest = 2 }, label = "Settings", icon = DittoIcons.more)
      }
    }
  }
}

@Composable
internal fun TopBarDemo() {
  val scroll = rememberTopBarScrollBehavior()
  Column(Modifier.width(340.dp).height(240.dp)) {
    Scaffold(
      topBar = {
        TopBar(
          title = "Library",
          variant = TopBarVariant.Large,
          scrollBehavior = scroll,
          navigationIcon = { BackButton(onClick = {}) },
          actions = { IconButton(icon = DittoIcons.search, contentDescription = "Search", onClick = {}, tooltip = false) },
          windowInsets = WindowInsets(0),
        )
      },
      contentWindowInsets = WindowInsets(0),
    ) { padding ->
      Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
        (1..10).forEach { ListItem(headline = "Row $it", onClick = {}) }
      }
    }
  }
}

// ---------------------------------------------------------------- desktop

@Composable
internal fun LinkDemo() = Line {
  Link("Learn more", onClick = {})
  Link("Docs", onClick = {}, external = true)
  Link("Disabled", onClick = {}, enabled = false)
}

@Composable
internal fun TreeDemo() {
  val state = rememberTreeState(expanded = setOf("lib", "books"), selected = "b1")
  Box(Modifier.width(260.dp).height(200.dp)) {
    Tree(
      roots = listOf(
        TreeNode(
          "lib", "Library", DittoIcons.more,
          children = listOf(
            TreeNode("books", "Books", children = listOf(TreeNode("b1", "Dune"), TreeNode("b2", "Hyperion"))),
            TreeNode("pod", "Podcasts"),
          ),
        ),
        TreeNode("set", "Settings", DittoIcons.check),
      ),
      state = state,
    )
  }
}

@Composable
internal fun SplitPaneDemo() {
  Box(Modifier.width(400.dp).height(200.dp)) {
    HorizontalSplitPane(
      state = rememberSplitPaneState(0.4f),
      first = {
        Column(Modifier.fillMaxSize().padding(DittoTheme.spacing.sm)) {
          Text("Drag the divider", style = DittoTheme.typography.bodySmall)
        }
      },
      second = {
        val scroll = rememberScrollState()
        Box(Modifier.fillMaxSize()) {
          Column(Modifier.fillMaxSize().verticalScroll(scroll).padding(DittoTheme.spacing.sm)) {
            (1..20).forEach { Text("Detail line $it", style = DittoTheme.typography.bodySmall) }
          }
          VerticalScrollbar(scroll, Modifier.align(Alignment.CenterEnd).fillMaxHeight())
        }
      },
    )
  }
}

@Composable
internal fun ScrollbarDemo() {
  val scroll = rememberScrollState()
  Box(Modifier.width(260.dp).height(180.dp)) {
    Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
      (1..25).forEach { Text("Row $it", style = DittoTheme.typography.body) }
    }
    VerticalScrollbar(scroll, Modifier.align(Alignment.CenterEnd).fillMaxHeight())
  }
}
