package com.r0adkll.ditto.spike

import androidx.compose.foundation.background
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
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.RadioButtonRow
import org.jetbrains.jewel.ui.component.SegmentedControl
import org.jetbrains.jewel.ui.component.SegmentedControlButtonData
import org.jetbrains.jewel.ui.component.Slider
import org.jetbrains.jewel.ui.component.TabData
import org.jetbrains.jewel.ui.component.TabStrip
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.theme.defaultTabStyle
import org.jetbrains.jewel.ui.typography

/** The same settings form rendered with Jewel's standalone Int UI theme. */
@Composable
fun JewelSampleForm(dark: Boolean) {
  IntUiTheme(isDark = dark) {
    Column(
      Modifier.width(420.dp).background(JewelTheme.globalColors.panelBackground).padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text("Jewel · Int UI", style = JewelTheme.typography.h2TextStyle)
      var tab by remember { mutableStateOf(0) }
      TabStrip(
        tabs = listOf("General", "Appearance", "Advanced").mapIndexed { i, label ->
          TabData.Default(selected = tab == i, content = { Text(label) }, closable = false, onClick = { tab = i })
        },
        style = JewelTheme.defaultTabStyle,
      )
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Project name")
        TextField(state = rememberTextFieldState("Campfire"), modifier = Modifier.fillMaxWidth())
      }
      TextField(state = rememberTextFieldState(), placeholder = { Text("Search settings") }, modifier = Modifier.fillMaxWidth())
      var seg by remember { mutableStateOf(0) }
      SegmentedControl(
        buttons = listOf("Light", "Dark", "System").mapIndexed { i, label ->
          SegmentedControlButtonData(selected = seg == i, content = { Text(label) }, onSelect = { seg = i })
        },
        modifier = Modifier.fillMaxWidth(),
      )
      Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        var a by remember { mutableStateOf(true) }
        var b by remember { mutableStateOf(false) }
        Checkbox(checked = a, onCheckedChange = { a = it }); Text("Auto-save")
        Checkbox(checked = b, onCheckedChange = { b = it }); Text("Telemetry")
      }
      var radio by remember { mutableStateOf(1) }
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        RadioButtonRow(text = "Compact", selected = radio == 0, onClick = { radio = 0 })
        RadioButtonRow(text = "Comfortable", selected = radio == 1, onClick = { radio = 1 })
      }
      var vol by remember { mutableStateOf(0.6f) }
      Slider(value = vol, onValueChange = { vol = it })
      Column {
        Text("Keymap"); Text("macOS", style = JewelTheme.typography.medium)
        Divider(Orientation.Horizontal, Modifier.padding(vertical = 8.dp))
        Text("Plugins"); Text("12 installed", style = JewelTheme.typography.medium)
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End), modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = {}) { Text("Cancel") }
        DefaultButton(onClick = {}) { Text("Apply") }
      }
    }
  }
}
