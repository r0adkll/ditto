package com.r0adkll.ditto.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.components.Button
import com.r0adkll.ditto.components.ButtonVariant
import com.r0adkll.ditto.catalog.brand.DittoBrand
import com.r0adkll.ditto.components.AlertDialog
import com.r0adkll.ditto.components.FloatingActionButton
import com.r0adkll.ditto.components.PullToRefreshBox
import com.r0adkll.ditto.components.SearchBar
import com.r0adkll.ditto.components.SnackbarHost
import com.r0adkll.ditto.components.SnackbarHostState
import com.r0adkll.ditto.components.ToggleButton
import com.r0adkll.ditto.components.BackButton
import com.r0adkll.ditto.components.Badge
import com.r0adkll.ditto.components.Banner
import com.r0adkll.ditto.components.BannerKind
import com.r0adkll.ditto.components.ComboBox
import com.r0adkll.ditto.components.EditableComboBox
import com.r0adkll.ditto.components.ShortcutScope
import com.r0adkll.ditto.input.Shortcut
import androidx.compose.ui.input.key.Key
import com.r0adkll.ditto.components.HorizontalSplitPane
import com.r0adkll.ditto.components.Link
import com.r0adkll.ditto.components.Tree
import com.r0adkll.ditto.components.TreeNode
import com.r0adkll.ditto.components.VerticalScrollbar
import com.r0adkll.ditto.components.rememberSplitPaneState
import com.r0adkll.ditto.components.rememberTreeState
import com.r0adkll.ditto.components.RangeSlider
import com.r0adkll.ditto.components.BadgedBox
import com.r0adkll.ditto.components.CheckableMenuItem
import com.r0adkll.ditto.components.Chip
import com.r0adkll.ditto.components.SheetDetent
import com.r0adkll.ditto.components.SubmenuItem
import com.r0adkll.ditto.components.TabItem
import com.r0adkll.ditto.components.rememberSheetState
import com.r0adkll.ditto.components.ModalSheet
import com.r0adkll.ditto.components.NavigationBar
import com.r0adkll.ditto.components.NavigationItem
import com.r0adkll.ditto.components.NavigationRail
import com.r0adkll.ditto.components.RadioGroup
import com.r0adkll.ditto.components.Sidebar
import com.r0adkll.ditto.components.SidebarItem
import com.r0adkll.ditto.components.Card
import com.r0adkll.ditto.components.CircularProgressIndicator
import com.r0adkll.ditto.components.DropdownMenu
import com.r0adkll.ditto.components.LinearProgressIndicator
import com.r0adkll.ditto.components.MenuDivider
import com.r0adkll.ditto.components.MenuItem
import com.r0adkll.ditto.components.CardVariant
import com.r0adkll.ditto.components.HorizontalDivider
import com.r0adkll.ditto.components.ListItem
import com.r0adkll.ditto.components.TextField
import com.r0adkll.ditto.components.Checkbox
import com.r0adkll.ditto.components.RadioButton
import com.r0adkll.ditto.components.Scaffold
import com.r0adkll.ditto.components.SegmentedControl
import com.r0adkll.ditto.components.Slider
import com.r0adkll.ditto.components.TabRow
import com.r0adkll.ditto.components.TopBar
import com.r0adkll.ditto.components.TopBarVariant
import com.r0adkll.ditto.components.rememberTopBarScrollBehavior
import com.r0adkll.ditto.components.Switch
import com.r0adkll.ditto.components.TriStateCheckbox
import com.r0adkll.ditto.components.FilledIconButton
import com.r0adkll.ditto.components.IconButton
import com.r0adkll.ditto.components.OutlinedButton
import com.r0adkll.ditto.components.OutlinedIconButton
import com.r0adkll.ditto.components.TextButton
import com.r0adkll.ditto.components.TonalButton
import com.r0adkll.ditto.components.TonalIconButton
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.platformIdiom
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.theme.DittoStyleOverrides
import com.r0adkll.ditto.theme.dittoStyleOverrides
import com.r0adkll.ditto.components.ButtonStyle
import com.r0adkll.ditto.components.CardStyle
import com.r0adkll.ditto.components.TextFieldStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import com.r0adkll.ditto.tokens.ColorMode
import com.r0adkll.ditto.tokens.ElevationLevel
import com.r0adkll.ditto.tokens.Neutrals

private val Accents = listOf(
  "Ditto" to DittoBrand.Violet,
  "Blue" to Color(0xFF3B6CF6),
  "Green" to Color(0xFF15803D),
  "Amber" to Color(0xFFF59E0B),
  "Rose" to Color(0xFFE11D48),
  "Ink" to Color(0xFF111827),
)

/** The living spec: every token and component, switchable across idioms and color modes. */
@Composable
fun CatalogApp() {
  var idiom by remember { mutableStateOf(platformIdiom()) }
  var colorMode by remember { mutableStateOf(ColorMode.System) }
  var accent by remember { mutableStateOf(Accents.first().second) }
  var neutrals by remember { mutableStateOf(Neutrals.Cool) }
  var sharpCorners by remember { mutableStateOf(false) }
  // App-wide tweak as a transform of each idiom's default (ADR-029): survives idiom/density switches.
  val overrides = remember(sharpCorners) {
    if (!sharpCorners) DittoStyleOverrides.Empty else dittoStyleOverrides {
      override<ButtonStyle> { it.copy(shape = RoundedCornerShape(2.dp)) }
      override<TextFieldStyle> { it.copy(shape = RoundedCornerShape(2.dp)) }
      override<CardStyle> { it.copy(shape = RoundedCornerShape(4.dp)) }
    }
  }

  DittoTheme(accent = accent, neutrals = neutrals, idiom = idiom, colorMode = colorMode, styleOverrides = overrides) {
    val scroll = rememberTopBarScrollBehavior()
    val snackbars = remember { SnackbarHostState() }
    var refreshing by remember { mutableStateOf(false) }
    LaunchedEffect(refreshing) { if (refreshing) { delay(1500); refreshing = false } }
    val scope = rememberCoroutineScope()
    ShortcutScope(
      Shortcut(Key.D, primary = true) to { colorMode = if (colorMode == ColorMode.Dark) ColorMode.Light else ColorMode.Dark },
      Shortcut(Key.K, primary = true) to { scope.launch { snackbars.showSnackbar("Shortcut ${Shortcut(Key.K, primary = true).label()} pressed") } },
    ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      snackbarHost = { SnackbarHost(snackbars) },
      floatingAction = { FloatingActionButton(onClick = { refreshing = true }, icon = DittoIcons.forward, contentDescription = "Refresh demo") },
      topBar = {
        TopBar(
          title = { Text("Ditto") },
          largeTitle = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
              Image(DittoBrand.mascot(), contentDescription = null, modifier = Modifier.size(40.dp))
              Text("Ditto")
            }
          },
          variant = TopBarVariant.Large,
          scrollBehavior = scroll,
          actions = {
            IconButton(
              icon = if (colorMode == ColorMode.Dark) DittoIcons.visibilityOff else DittoIcons.visibility,
              contentDescription = "Toggle dark mode",
              onClick = { colorMode = if (colorMode == ColorMode.Dark) ColorMode.Light else ColorMode.Dark },
            )
          },
        )
      },
    ) { padding ->
      PullToRefreshBox(isRefreshing = refreshing, onRefresh = { refreshing = true }, modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
      Column(
        Modifier
          .fillMaxSize()
          .nestedScroll(scroll.connection)
          .verticalScroll(rememberScrollState())
          .padding(bottom = padding.calculateBottomPadding())
          .padding(DittoTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.xl),
      ) {
        Header(
          idiom = idiom, onIdiom = { idiom = it },
          colorMode = colorMode, onColorMode = { colorMode = it },
          accent = accent, onAccent = { accent = it },
          neutrals = neutrals, onNeutrals = { neutrals = it },
          sharpCorners = sharpCorners, onSharpCorners = { sharpCorners = it },
        )
        catalogDemos().forEach { demo ->
          Section(demo.title) { demo.content() }
        }
      }
      }
    }
    }
  }
}

@Composable
private fun Header(
  idiom: Idiom, onIdiom: (Idiom) -> Unit,
  colorMode: ColorMode, onColorMode: (ColorMode) -> Unit,
  accent: Color, onAccent: (Color) -> Unit,
  neutrals: Neutrals, onNeutrals: (Neutrals) -> Unit,
  sharpCorners: Boolean, onSharpCorners: (Boolean) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
    Text(
      "One component API, three idioms. Tap the controls to re-render everything below.",
      style = DittoTheme.typography.bodySmall,
      color = DittoTheme.colors.onSurfaceVariant,
    )
    Chooser("Idiom", Idiom.entries, idiom, { it.name }, onIdiom)
    Chooser("Color mode", ColorMode.entries, colorMode, { it.name }, onColorMode)
    Chooser("Neutrals", Neutrals.entries, neutrals, { it.name }, onNeutrals)
    Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
      Text("Overrides", style = DittoTheme.typography.label, modifier = Modifier.width(88.dp))
      Switch(checked = sharpCorners, onCheckedChange = onSharpCorners)
      Text("Sharp corners app-wide", style = DittoTheme.typography.bodySmall, color = DittoTheme.colors.onSurfaceVariant)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
      Text("Accent", style = DittoTheme.typography.label, modifier = Modifier.width(88.dp))
      Accents.forEach { (name, color) ->
        val selected = color == accent
        Box(
          Modifier
            .size(28.dp)
            .border(1.dp, DittoTheme.colors.outlineVariant, DittoTheme.shapes.full)
            .background(color, DittoTheme.shapes.full)
            .padding(2.dp),
        ) {
          IconButton(onClick = { onAccent(color) }, modifier = Modifier.size(24.dp)) {
            if (selected) Icon(DittoIcons.check, contentDescription = "$name selected", tint = Color.White, size = 16.dp)
            else Spacer(Modifier.size(1.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun <T> Chooser(label: String, options: List<T>, selected: T, name: (T) -> String, onSelect: (T) -> Unit) {
  Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
    Text(label, style = DittoTheme.typography.label, modifier = Modifier.width(88.dp))
    options.forEach { option ->
      if (option == selected) {
        TonalButton(onClick = { onSelect(option) }) { Text(name(option)) }
      } else {
        TextButton(onClick = { onSelect(option) }) { Text(name(option)) }
      }
    }
  }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
    Text(title, style = DittoTheme.typography.heading)
    Surface(
      shape = DittoTheme.shapes.large,
      elevation = ElevationLevel.Level1,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Box(Modifier.padding(DittoTheme.spacing.lg)) { content() }
    }
  }
}

@Composable
internal fun ColorsDemo() {
  val c = DittoTheme.colors
  val roles = listOf(
    "accent" to c.accent, "onAccent" to c.onAccent, "background" to c.background, "onBackground" to c.onBackground,
    "surface" to c.surface, "surfaceRaised" to c.surfaceRaised, "surfaceOverlay" to c.surfaceOverlay,
    "onSurface" to c.onSurface, "onSurfaceVariant" to c.onSurfaceVariant, "outline" to c.outline,
    "outlineVariant" to c.outlineVariant, "error" to c.error, "success" to c.success, "warning" to c.warning,
  )
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm)) {
    roles.chunked(2).forEach { pair ->
      Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.lg)) {
        pair.forEach { (name, color) ->
          Row(Modifier.width(220.dp), horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(24.dp).background(color, DittoTheme.shapes.extraSmall).background(Color.Transparent))
            Text(name, style = DittoTheme.typography.bodySmall)
          }
        }
      }
    }
    Text("Neutral ramp", style = DittoTheme.typography.label)
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
      c.neutrals.asList().forEach { step -> Box(Modifier.size(width = 24.dp, height = 32.dp).background(step)) }
    }
  }
}

@Composable
internal fun TypographyDemo() {
  val t = DittoTheme.typography
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.xs)) {
    Text("Display", style = t.display)
    Text("Title", style = t.title)
    Text("Heading", style = t.heading)
    Text("Subheading", style = t.subheading)
    Text("Body — the quick brown fox jumps over the lazy dog.", style = t.body)
    Text("Body small — the quick brown fox jumps over the lazy dog.", style = t.bodySmall)
    Text("Label", style = t.label)
    Text("Caption", style = t.caption, color = DittoTheme.colors.onSurfaceVariant)
  }
}

@Composable
internal fun ShapesDemo() {
  val s = DittoTheme.shapes
  Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
    listOf("xs" to s.extraSmall, "sm" to s.small, "md" to s.medium, "lg" to s.large, "xl" to s.extraLarge, "full" to s.full)
      .forEach { (name, shape) ->
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(Modifier.size(48.dp).background(DittoTheme.colors.accent, shape))
          Text(name, style = DittoTheme.typography.caption)
        }
      }
  }
}

@Composable
internal fun SpacingDemo() {
  val sp = DittoTheme.spacing
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.xs)) {
    listOf("xxs" to sp.xxs, "xs" to sp.xs, "sm" to sp.sm, "md" to sp.md, "lg" to sp.lg, "xl" to sp.xl, "xxl" to sp.xxl, "xxxl" to sp.xxxl)
      .forEach { (name, size) ->
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm)) {
          Text(name, style = DittoTheme.typography.caption, modifier = Modifier.width(32.dp))
          Box(Modifier.height(8.dp).width(size).background(DittoTheme.colors.accent))
          Text(formatDp(size), style = DittoTheme.typography.caption, color = DittoTheme.colors.onSurfaceVariant)
        }
      }
    Text("density ×${sp.density}", style = DittoTheme.typography.caption, color = DittoTheme.colors.onSurfaceVariant)
  }
}

@Composable
internal fun ElevationDemo() {
  Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.lg)) {
    ElevationLevel.entries.forEach { level ->
      Surface(shape = DittoTheme.shapes.medium, elevation = level, modifier = Modifier.size(72.dp)) {
        Box(contentAlignment = Alignment.Center) { Text(level.name.removePrefix("Level"), style = DittoTheme.typography.label) }
      }
    }
  }
}

@Composable
internal fun IconsDemo() {
  val icons = listOf(
    "back" to DittoIcons.back, "forward" to DittoIcons.forward, "chevronDown" to DittoIcons.chevronDown,
    "chevronRight" to DittoIcons.chevronRight, "close" to DittoIcons.close, "check" to DittoIcons.check,
    "clear" to DittoIcons.clear, "more" to DittoIcons.more, "search" to DittoIcons.search,
    "dropdown" to DittoIcons.dropdown, "visibility" to DittoIcons.visibility,
    "visibilityOff" to DittoIcons.visibilityOff, "indeterminate" to DittoIcons.indeterminate,
  )
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
    icons.chunked(7).forEach { row ->
      Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.lg)) {
        row.forEach { (name, icon) ->
          Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, contentDescription = name)
            Text(name, style = DittoTheme.typography.caption, color = DittoTheme.colors.onSurfaceVariant)
          }
        }
      }
    }
  }
}

@Composable
private fun ContrastReport() {
  val failures = DittoTheme.colors.validateContrast()
  val color = if (failures.isEmpty()) DittoTheme.colors.success else DittoTheme.colors.warning
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.xs)) {
    Text(
      if (failures.isEmpty()) "WCAG 2 contrast: all role pairs pass" else "WCAG 2 contrast: ${failures.size} failing pairs",
      style = DittoTheme.typography.label,
      color = color,
    )
    failures.forEach { Text(it, style = DittoTheme.typography.caption, color = DittoTheme.colors.onSurfaceVariant) }
  }
}

private fun formatDp(dp: Dp): String {
  val tenths = (dp.value * 10).roundToInt()
  return if (tenths % 10 == 0) "${tenths / 10}dp" else "${tenths / 10}.${tenths % 10}dp"
}
