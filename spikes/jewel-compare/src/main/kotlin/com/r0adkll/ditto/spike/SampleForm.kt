package com.r0adkll.ditto.spike

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.components.Button
import com.r0adkll.ditto.components.ButtonVariant
import com.r0adkll.ditto.components.Card
import com.r0adkll.ditto.components.CardVariant
import com.r0adkll.ditto.components.Checkbox
import com.r0adkll.ditto.components.HorizontalDivider
import com.r0adkll.ditto.components.ListItem
import com.r0adkll.ditto.components.RadioGroup
import com.r0adkll.ditto.components.SegmentedControl
import com.r0adkll.ditto.components.Slider
import com.r0adkll.ditto.components.Switch
import com.r0adkll.ditto.components.TabRow
import com.r0adkll.ditto.components.TextField
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ColorMode

/**
 * The same "settings form" content, once per design system, so the comparison is about the
 * systems and not the layout. The Ditto half is the Desktop idiom (ADR-023: shadcn/Radix-style).
 */
@Composable
fun DittoSampleForm(dark: Boolean) {
  DittoTheme(idiom = Idiom.Desktop, colorMode = if (dark) ColorMode.Dark else ColorMode.Light) {
    Surface(color = DittoTheme.colors.background) {
      Column(Modifier.width(420.dp).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Ditto · Desktop idiom", style = DittoTheme.typography.heading)
        var tab by remember { mutableStateOf(0) }
        TabRow(tabs = listOf("General", "Appearance", "Advanced"), selectedIndex = tab, onSelect = { tab = it })
        TextField(state = rememberTextFieldState("Campfire"), label = "Project name", modifier = Modifier.fillMaxWidth())
        TextField(state = rememberTextFieldState(), placeholder = "Search settings", leadingIcon = { Icon(DittoIcons.search, null) }, modifier = Modifier.fillMaxWidth())
        var seg by remember { mutableStateOf(0) }
        SegmentedControl(options = listOf("Light", "Dark", "System"), selectedIndex = seg, onSelect = { seg = it }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
          var a by remember { mutableStateOf(true) }
          var b by remember { mutableStateOf(false) }
          Checkbox(checked = a, onCheckedChange = { a = it }); Text("Auto-save")
          Checkbox(checked = b, onCheckedChange = { b = it }); Text("Telemetry")
          var on by remember { mutableStateOf(true) }
          Switch(checked = on, onCheckedChange = { on = it })
        }
        var radio by remember { mutableStateOf(1) }
        RadioGroup(options = listOf("Compact", "Comfortable"), selectedIndex = radio, onSelect = { radio = it })
        var vol by remember { mutableStateOf(0.6f) }
        Slider(value = vol, onValueChange = { vol = it })
        Card(variant = CardVariant.Outlined, modifier = Modifier.fillMaxWidth()) {
          ListItem(headline = "Keymap", supporting = "macOS", onClick = {})
          HorizontalDivider()
          ListItem(headline = "Plugins", supporting = "12 installed", onClick = {})
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End), modifier = Modifier.fillMaxWidth()) {
          Button(text = "Cancel", onClick = {}, variant = ButtonVariant.Outlined)
          Button(text = "Apply", onClick = {})
        }
      }
    }
  }
}
