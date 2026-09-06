package com.r0adkll.ditto.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.catalog.brand.DittoBrand
import com.r0adkll.ditto.components.SnackbarHost
import com.r0adkll.ditto.components.SnackbarHostState
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
fun catalogDemos(snackbars: SnackbarHostState = remember { SnackbarHostState() }): List<DemoItem> = listOf(
  DemoItem("buttons", "Buttons") { ButtonsDemo() },
  DemoItem("icon-buttons", "Icon buttons") { IconButtonsDemo() },
  DemoItem("selection-controls", "Selection controls") { SelectionControlsDemo() },
  DemoItem("text-fields", "Text fields") { TextFieldsDemo() },
  DemoItem("controls", "Segments, tabs, sliders") { ControlsDemo() },
  DemoItem("lists-cards", "Lists and cards") { ListsDemo() },
  DemoItem("overlays", "Menus, dialogs, progress") { OverlaysDemo() },
  DemoItem("navigation", "Navigation, chips, badges, sheets") { NavigationDemo() },
  DemoItem("actions", "Toggles, FAB, search, snackbar, pull to refresh") { ActionsDemo(snackbars) },
  DemoItem("desktop", "Combo box, links, banners, tree, split pane") { DesktopDemo() },
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
  val snackbars = remember { SnackbarHostState() }
  DittoTheme(accent = DittoBrand.Violet, idiom = idiom, colorMode = colorMode) {
    Surface(color = DittoTheme.colors.background, modifier = Modifier.fillMaxSize()) {
      Box(Modifier.fillMaxSize().padding(DittoTheme.spacing.lg)) {
        val demo = catalogDemos(snackbars).firstOrNull { it.id == id }
        if (demo != null) {
          demo.content()
        } else {
          Text("No demo registered for id \"$id\"", color = DittoTheme.colors.error)
        }
        SnackbarHost(snackbars, modifier = Modifier.align(Alignment.BottomCenter))
      }
    }
  }
}
