package com.r0adkll.ditto.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.components.HorizontalDivider
import com.r0adkll.ditto.components.IconButton
import com.r0adkll.ditto.components.SegmentedControl
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.catalog.brand.CatalogIcons
import com.r0adkll.ditto.catalog.brand.DittoBrand
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.platformIdiom
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ColorMode

/**
 * One addressable example. [id] is a stable slug used in URLs (`?id=buttons`) and by the docs
 * site to embed a single demo, so **renaming an id breaks published pages** — treat them as API.
 */
@Immutable
class DemoItem(
  val id: String,
  val title: String,
  val content: @Composable () -> Unit,
)

/**
 * Every demo the catalog can show, in display order. The full catalog renders all of them; the
 * docs site embeds one at a time by [DemoItem.id].
 */
@Composable
fun catalogDemos(): List<DemoItem> = listOf(
  // Actions
  DemoItem("button", "Button") { ButtonDemo() },
  DemoItem("icon-button", "Icon button") { IconButtonDemo() },
  DemoItem("toggle-button", "Toggle button") { ToggleButtonDemo() },
  DemoItem("fab", "Floating action button") { FabDemo() },
  // Selection
  DemoItem("switch", "Switch") { SwitchDemo() },
  DemoItem("checkbox", "Checkbox") { CheckboxDemo() },
  DemoItem("radio-button", "Radio button") { RadioButtonDemo() },
  // Text input
  DemoItem("text-field", "Text field") { TextFieldDemo() },
  DemoItem("search-bar", "Search bar") { SearchBarDemo() },
  DemoItem("combo-box", "Combo box") { ComboBoxDemo() },
  // Value pickers
  DemoItem("slider", "Slider") { SliderDemo() },
  DemoItem("range-slider", "Range slider") { RangeSliderDemo() },
  DemoItem("segmented-control", "Segmented control") { SegmentedControlDemo() },
  DemoItem("tabs", "Tabs") { TabsDemo() },
  // Containment
  DemoItem("card", "Card") { CardDemo() },
  DemoItem("list-item", "List item") { ListItemDemo() },
  DemoItem("chip", "Chip") { ChipDemo() },
  DemoItem("badge", "Badge") { BadgeDemo() },
  DemoItem("banner", "Banner") { BannerDemo() },
  // Overlays
  DemoItem("tooltip", "Tooltip") { TooltipDemo() },
  DemoItem("menu", "Menu") { MenuDemo() },
  DemoItem("dialog", "Dialog") { DialogDemo() },
  DemoItem("sheet", "Sheet") { SheetDemo() },
  DemoItem("snackbar", "Snackbar") { SnackbarDemo() },
  DemoItem("progress-indicator", "Progress indicator") { ProgressIndicatorDemo() },
  DemoItem("pull-to-refresh", "Pull to refresh") { PullToRefreshDemo() },
  // Navigation
  DemoItem("navigation", "Navigation bar, rail and sidebar") { NavigationDemo2() },
  DemoItem("top-bar", "Top bar and scaffold") { TopBarDemo() },
  // Desktop
  DemoItem("link", "Link") { LinkDemo() },
  DemoItem("tree", "Tree") { TreeDemo() },
  DemoItem("split-pane", "Split pane") { SplitPaneDemo() },
  DemoItem("scrollbar", "Scrollbar") { ScrollbarDemo() },
  // Foundations
  DemoItem("colors", "Colors") { ColorsDemo() },
  DemoItem("typography", "Typography") { TypographyDemo() },
  DemoItem("shapes", "Shapes") { ShapesDemo() },
  DemoItem("spacing", "Spacing") { SpacingDemo() },
  DemoItem("elevation", "Elevation") { ElevationDemo() },
  DemoItem("icons", "System icons") { IconsDemo() },
)

/**
 * Renders one demo on its own for embedding in a docs page: a compact chrome bar with an idiom
 * selector and a light/dark toggle, then the demo centred beneath it.
 *
 * The selector lives *inside* the frame on purpose. Switching idiom from the host page would mean
 * changing the iframe `src`, which re-instantiates the whole wasm runtime; here it is a
 * recomposition, which is what makes runtime idiom switching worth showing at all.
 *
 * [idiom] and [colorMode] seed the controls, so a deep link like `?id=switch&idiom=apple` still
 * opens on the right variant.
 */
@Composable
fun DemoScreen(
  id: String,
  idiom: Idiom = platformIdiom(),
  colorMode: ColorMode = ColorMode.System,
) {
  var selectedIdiom by remember(idiom) { mutableStateOf(idiom) }
  var selectedMode by remember(colorMode) { mutableStateOf(colorMode) }
  val demo = catalogDemos().firstOrNull { it.id == id }
  val dark = when (selectedMode) {
    ColorMode.Light -> false
    ColorMode.Dark -> true
    ColorMode.System -> isSystemInDarkTheme()
  }

  // The chrome is always Desktop-idiom so it stays put while the content below it changes.
  DittoTheme(accent = DittoBrand.Violet, idiom = Idiom.Desktop, colorMode = selectedMode) {
    Surface(color = DittoTheme.colors.background, modifier = Modifier.fillMaxSize()) {
      Column(Modifier.fillMaxSize()) {
        Row(
          Modifier
            .fillMaxWidth()
            .padding(horizontal = DittoTheme.spacing.md, vertical = DittoTheme.spacing.sm),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm),
        ) {
          SegmentedControl(
            options = Idiom.entries.map { it.name },
            selectedIndex = Idiom.entries.indexOf(selectedIdiom),
            onSelect = { selectedIdiom = Idiom.entries[it] },
            modifier = Modifier.width(230.dp),
          )
          Spacer(Modifier.weight(1f))
          IconButton(
            icon = if (dark) CatalogIcons.sun else CatalogIcons.moon,
            contentDescription = "Toggle light and dark",
            onClick = { selectedMode = if (dark) ColorMode.Light else ColorMode.Dark },
          )
        }
        HorizontalDivider()
        Box(
          Modifier.fillMaxSize().padding(DittoTheme.spacing.lg),
          contentAlignment = Alignment.Center,
        ) {
          DittoTheme(accent = DittoBrand.Violet, idiom = selectedIdiom, colorMode = selectedMode) {
            if (demo != null) {
              demo.content()
            } else {
              Text("No demo registered for id \"$id\"", color = DittoTheme.colors.error)
            }
          }
        }
      }
    }
  }
}
