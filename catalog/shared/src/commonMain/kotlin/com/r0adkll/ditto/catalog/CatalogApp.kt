package com.r0adkll.ditto.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.components.Button
import com.r0adkll.ditto.components.ButtonVariant
import com.r0adkll.ditto.components.AlertDialog
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
import com.r0adkll.ditto.tokens.ColorMode
import com.r0adkll.ditto.tokens.ElevationLevel
import com.r0adkll.ditto.tokens.Neutrals

private val Accents = listOf(
  "Blue" to Color(0xFF3B6CF6),
  "Violet" to Color(0xFF7C3AED),
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

  DittoTheme(accent = accent, neutrals = neutrals, idiom = idiom, colorMode = colorMode) {
    Surface(color = DittoTheme.colors.background, modifier = Modifier.fillMaxSize()) {
      Column(
        Modifier
          .fillMaxSize()
          .windowInsetsPadding(WindowInsets.safeDrawing)
          .verticalScroll(rememberScrollState())
          .padding(DittoTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.xl),
      ) {
        Header(
          idiom = idiom, onIdiom = { idiom = it },
          colorMode = colorMode, onColorMode = { colorMode = it },
          accent = accent, onAccent = { accent = it },
          neutrals = neutrals, onNeutrals = { neutrals = it },
        )
        Section("Buttons") { ButtonsDemo() }
        Section("Icon buttons") { IconButtonsDemo() }
        Section("Selection controls") { SelectionControlsDemo() }
        Section("Text fields") { TextFieldsDemo() }
        Section("Lists and cards") { ListsDemo() }
        Section("Menus, dialogs, progress") { OverlaysDemo() }
        Section("Colors") { ColorsDemo() }
        Section("Typography") { TypographyDemo() }
        Section("Shapes") { ShapesDemo() }
        Section("Spacing") { SpacingDemo() }
        Section("Elevation") { ElevationDemo() }
        Section("System icons") { IconsDemo() }
        ContrastReport()
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
) {
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
    Text("Ditto", style = DittoTheme.typography.display)
    Text(
      "One component API, three idioms. Tap the controls to re-render everything below.",
      style = DittoTheme.typography.bodySmall,
      color = DittoTheme.colors.onSurfaceVariant,
    )
    Chooser("Idiom", Idiom.entries, idiom, { it.name }, onIdiom)
    Chooser("Color mode", ColorMode.entries, colorMode, { it.name }, onColorMode)
    Chooser("Neutrals", Neutrals.entries, neutrals, { it.name }, onNeutrals)
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
private fun ButtonsDemo() {
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
    Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
      Button(onClick = {}) { Text("Filled") }
      TonalButton(onClick = {}) { Text("Tonal") }
      OutlinedButton(onClick = {}) { Text("Outlined") }
      TextButton(onClick = {}) { Text("Text") }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
      Button(text = "Disabled", onClick = {}, enabled = false)
      Button(text = "Disabled", onClick = {}, enabled = false, variant = ButtonVariant.Tonal)
      Button(text = "Disabled", onClick = {}, enabled = false, variant = ButtonVariant.Outlined)
      Button(text = "Disabled", onClick = {}, enabled = false, variant = ButtonVariant.Text)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
      Button(text = "Continue", onClick = {}, leadingIcon = DittoIcons.check)
      Button(text = "Search", onClick = {}, variant = ButtonVariant.Tonal, leadingIcon = DittoIcons.search)
      Button(text = "Back", onClick = {}, variant = ButtonVariant.Text, leadingIcon = DittoIcons.back)
    }
  }
}

@Composable
private fun IconButtonsDemo() {
  Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
    IconButton(onClick = {}) { Icon(DittoIcons.more, "More") }
    FilledIconButton(onClick = {}) { Icon(DittoIcons.check, "Done") }
    TonalIconButton(onClick = {}) { Icon(DittoIcons.search, "Search") }
    OutlinedIconButton(onClick = {}) { Icon(DittoIcons.close, "Close") }
    IconButton(onClick = {}, enabled = false) { Icon(DittoIcons.back, "Back") }
    FilledIconButton(onClick = {}, enabled = false) { Icon(DittoIcons.forward, "Forward") }
  }
}

@Composable
private fun SelectionControlsDemo() {
  var switchOn by remember { mutableStateOf(true) }
  var switchOff by remember { mutableStateOf(false) }
  var checkA by remember { mutableStateOf(true) }
  var checkB by remember { mutableStateOf(false) }
  var tri by remember { mutableStateOf(ToggleableState.Indeterminate) }
  var radio by remember { mutableStateOf(0) }
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
    Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
      Text("Switch", style = DittoTheme.typography.label, modifier = Modifier.width(88.dp))
      Switch(checked = switchOn, onCheckedChange = { switchOn = it })
      Switch(checked = switchOff, onCheckedChange = { switchOff = it })
      Switch(checked = true, onCheckedChange = null, enabled = false)
      Switch(checked = false, onCheckedChange = null, enabled = false)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
      Text("Checkbox", style = DittoTheme.typography.label, modifier = Modifier.width(88.dp))
      Checkbox(checked = checkA, onCheckedChange = { checkA = it })
      Checkbox(checked = checkB, onCheckedChange = { checkB = it })
      TriStateCheckbox(
        state = tri,
        onClick = { tri = when (tri) { ToggleableState.On -> ToggleableState.Off; ToggleableState.Off -> ToggleableState.Indeterminate; ToggleableState.Indeterminate -> ToggleableState.On } },
      )
      Checkbox(checked = true, onCheckedChange = null, enabled = false)
      Checkbox(checked = false, onCheckedChange = null, enabled = false)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
      Text("Radio", style = DittoTheme.typography.label, modifier = Modifier.width(88.dp))
      (0..2).forEach { i -> RadioButton(selected = radio == i, onClick = { radio = i }) }
      RadioButton(selected = true, onClick = null, enabled = false)
      RadioButton(selected = false, onClick = null, enabled = false)
    }
  }
}

@Composable
private fun TextFieldsDemo() {
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md), modifier = Modifier.width(360.dp)) {
    TextField(state = rememberTextFieldState(), label = "Name", placeholder = "Ada Lovelace", modifier = Modifier.fillMaxWidth())
    TextField(
      state = rememberTextFieldState(),
      placeholder = "Search",
      leadingIcon = { Icon(DittoIcons.search, null) },
      modifier = Modifier.fillMaxWidth(),
    )
    val email = rememberTextFieldState("not-an-email")
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
private fun ListsDemo() {
  var wifi by remember { mutableStateOf(true) }
  Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.lg)) {
    Card(variant = CardVariant.Outlined, modifier = Modifier.width(340.dp)) {
      ListItem(headline = "Wi-Fi", supporting = if (wifi) "Connected" else "Off", leading = { Icon(DittoIcons.check, null) },
        trailing = { Switch(checked = wifi, onCheckedChange = { wifi = it }) })
      HorizontalDivider(startIndent = DittoTheme.spacing.lg)
      ListItem(headline = "Notifications", supporting = "Sounds, badges, banners", onClick = {})
      HorizontalDivider(startIndent = DittoTheme.spacing.lg)
      ListItem(headline = "About", onClick = {})
    }
    Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md)) {
      CardVariant.entries.forEach { variant ->
        Card(variant = variant, onClick = {}, modifier = Modifier.width(200.dp)) {
          Column(Modifier.padding(DittoTheme.spacing.lg)) {
            Text(variant.name, style = DittoTheme.typography.subheading)
            Text("Tap me", style = DittoTheme.typography.bodySmall, color = DittoTheme.colors.onSurfaceVariant)
          }
        }
      }
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm)) {
        Text("Tooltip on hover / long-press:", style = DittoTheme.typography.bodySmall)
        IconButton(icon = DittoIcons.more, contentDescription = "More options", onClick = {})
      }
    }
  }
}

@Composable
private fun OverlaysDemo() {
  var menuOpen by remember { mutableStateOf(false) }
  var dialogOpen by remember { mutableStateOf(false) }
  var progress by remember { mutableStateOf(0.4f) }
  Column(verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.lg)) {
    Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.md), verticalAlignment = Alignment.CenterVertically) {
      Box {
        Button(text = "Open menu", onClick = { menuOpen = true }, variant = ButtonVariant.Tonal, leadingIcon = DittoIcons.chevronDown)
        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
          MenuItem("Share", onClick = { menuOpen = false }, leadingIcon = { Icon(DittoIcons.forward, null) })
          MenuItem("Rename", onClick = { menuOpen = false }, trailingIcon = { Text("⌘R") })
          MenuDivider()
          MenuItem("Disabled", onClick = {}, enabled = false)
          MenuItem("Delete", onClick = { menuOpen = false }, destructive = true, leadingIcon = { Icon(DittoIcons.close, null) })
        }
      }
      Button(text = "Show dialog", onClick = { dialogOpen = true }, variant = ButtonVariant.Outlined)
      if (dialogOpen) {
        AlertDialog(
          onDismissRequest = { dialogOpen = false },
          title = "Delete recording?",
          text = "This removes the file from every device. You can't undo this.",
          confirmButton = { Button(text = "Delete", onClick = { dialogOpen = false }) },
          dismissButton = { Button(text = "Cancel", onClick = { dialogOpen = false }, variant = ButtonVariant.Text) },
        )
      }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.lg), verticalAlignment = Alignment.CenterVertically) {
      CircularProgressIndicator()
      CircularProgressIndicator(progress = { progress })
      Column(Modifier.width(240.dp), verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.sm)) {
        LinearProgressIndicator(progress = { progress })
        LinearProgressIndicator()
      }
      Button(text = "+10%", onClick = { progress = (progress + 0.1f).let { if (it > 1f) 0f else it } }, variant = ButtonVariant.Text)
    }
  }
}

@Composable
private fun ColorsDemo() {
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
private fun TypographyDemo() {
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
private fun ShapesDemo() {
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
private fun SpacingDemo() {
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
private fun ElevationDemo() {
  Row(horizontalArrangement = Arrangement.spacedBy(DittoTheme.spacing.lg)) {
    ElevationLevel.entries.forEach { level ->
      Surface(shape = DittoTheme.shapes.medium, elevation = level, modifier = Modifier.size(72.dp)) {
        Box(contentAlignment = Alignment.Center) { Text(level.name.removePrefix("Level"), style = DittoTheme.typography.label) }
      }
    }
  }
}

@Composable
private fun IconsDemo() {
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
