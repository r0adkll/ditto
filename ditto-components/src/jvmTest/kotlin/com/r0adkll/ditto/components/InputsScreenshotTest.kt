package com.r0adkll.ditto.components

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.preview.DittoPreviewMatrix
import com.r0adkll.ditto.screenshot.assertScreenshot
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

class InputsScreenshotTest {
  @Test
  fun textFields() = assertScreenshot("textfield-states", width = 760, height = 900, time = 300.milliseconds) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextField(state = rememberTextFieldState("Hello"), label = "Label", modifier = Modifier.fillMaxWidth())
        TextField(state = rememberTextFieldState(), placeholder = "Placeholder", modifier = Modifier.fillMaxWidth())
        TextField(
          state = rememberTextFieldState("Focused"),
          interactionSource = focusedSource(),
          leadingIcon = { Icon(DittoIcons.search, null) },
          modifier = Modifier.fillMaxWidth(),
        )
        TextField(
          state = rememberTextFieldState("Bad value"),
          isError = true,
          supportingText = "Something is wrong",
          trailingIcon = { Icon(DittoIcons.clear, "Clear") },
          modifier = Modifier.fillMaxWidth(),
        )
        TextField(state = rememberTextFieldState("Disabled"), enabled = false, modifier = Modifier.fillMaxWidth())
      }
    }
  }

  @Test
  fun listItemsAndDividers() = assertScreenshot("listitem-states", width = 760, height = 720) {
    DittoPreviewMatrix {
      Card(variant = CardVariant.Outlined, modifier = Modifier.fillMaxWidth()) {
        ListItem(headline = "Wi-Fi", supporting = "Connected", leading = { Icon(DittoIcons.check, null) }, onClick = {})
        HorizontalDivider(startIndent = 16.dp)
        ListItem(headline = "Notifications", trailing = { Switch(checked = true, onCheckedChange = null) })
        HorizontalDivider(startIndent = 16.dp)
        ListItem(headline = "About", onClick = {})
        HorizontalDivider(startIndent = 16.dp)
        ListItem(headline = "Disabled row", supporting = "Not available", onClick = {}, enabled = false)
      }
    }
  }

  @Test
  fun cards() = assertScreenshot("card-variants", width = 760, height = 560) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CardVariant.entries.forEach { variant ->
          Card(variant = variant, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
              Text(variant.name)
            }
          }
        }
      }
    }
  }

  @Test
  fun tooltipBubble() = assertScreenshot("tooltip", width = 760, height = 300) {
    DittoPreviewMatrix {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TooltipBubble(text = "Search everything")
        IconButton(icon = DittoIcons.search, contentDescription = "Search", onClick = {})
      }
    }
  }

  @Composable
  private fun focusedSource(): MutableInteractionSource {
    val source = remember { MutableInteractionSource() }
    LaunchedEffect(source) { source.emit(FocusInteraction.Focus()) }
    return source
  }
}

