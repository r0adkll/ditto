package com.r0adkll.ditto.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.r0adkll.ditto.Idiom
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
 * Renders a single demo on its own, themed and padded, for embedding in a docs page (one lazy
 * iframe per component). Unknown ids render a visible message rather than a blank frame, so a
 * broken embed is obvious on the page instead of silently empty.
 */
@Composable
fun DemoScreen(
  id: String,
  idiom: Idiom = platformIdiom(),
  colorMode: ColorMode = ColorMode.System,
) {
  DittoTheme(accent = DittoBrand.Violet, idiom = idiom, colorMode = colorMode) {
    Surface(color = DittoTheme.colors.background, modifier = Modifier.fillMaxSize()) {
      Box(Modifier.fillMaxSize().padding(DittoTheme.spacing.lg)) {
        val demo = catalogDemos().firstOrNull { it.id == id }
        if (demo != null) {
          demo.content()
        } else {
          Text("No demo registered for id \"$id\"", color = DittoTheme.colors.error)
        }
      }
    }
  }
}
