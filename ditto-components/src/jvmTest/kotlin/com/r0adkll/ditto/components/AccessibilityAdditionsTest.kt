package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.theme.DittoTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * The accessibility gaps closed on 2026-09-06. Each of these was reachable only by pointer or
 * announced as nameless before, which is exactly the kind of thing that regresses silently.
 */
@OptIn(ExperimentalTestApi::class)
class AccessibilityAdditionsTest {
  @Test
  fun textFieldTakesItsLabelAsAName() = runComposeUiTest {
    setContent {
      DittoTheme(idiom = Idiom.Desktop) {
        TextField(state = rememberTextFieldState(), label = "Email", modifier = Modifier.testTag("field"))
      }
    }
    // The label draws above the field, so without explicit semantics this node is unnamed.
    onNodeWithTag("field").assert(hasContentDescription("Email"))
  }

  @Test
  fun textFieldErrorReachesSemantics() = runComposeUiTest {
    setContent {
      DittoTheme(idiom = Idiom.Desktop) {
        TextField(
          state = rememberTextFieldState("nope"),
          label = "Email",
          isError = true,
          supportingText = "Enter a valid address",
          modifier = Modifier.testTag("field"),
        )
      }
    }
    val config = onNodeWithTag("field").fetchSemanticsNode().config
    assertEquals("Enter a valid address", config[SemanticsProperties.Error])
  }

  @Test
  fun rangeSliderHasATargetPerThumb() = runComposeUiTest {
    setContent {
      DittoTheme(idiom = Idiom.Desktop) {
        RangeSlider(value = 0.2f..0.8f, onValueChange = {}, modifier = Modifier.width(200.dp))
      }
    }
    // One conflated "range" node is not addressable; each end needs its own.
    onNodeWithContentDescription("Range start").assertExists()
    onNodeWithContentDescription("Range end").assertExists()
  }

  @Test
  fun rangeSliderArrowsMoveOneThumbOnly() = runComposeUiTest {
    var range = 0.2f..0.8f
    setContent {
      DittoTheme(idiom = Idiom.Desktop) {
        RangeSlider(value = range, onValueChange = { range = it }, modifier = Modifier.width(200.dp))
      }
    }
    onNodeWithContentDescription("Range start").requestFocus()
    onNodeWithContentDescription("Range start").performKeyInput { pressKey(Key.DirectionRight) }
    assertTrue(range.start > 0.2f, "start should move, was ${range.start}")
    assertEquals(0.8f, range.endInclusive, "the other thumb must stay put")
  }

  @Test
  fun splitPaneDividerResizesFromTheKeyboard() = runComposeUiTest {
    val state = SplitPaneState(0.5f)
    setContent {
      DittoTheme(idiom = Idiom.Desktop) {
        HorizontalSplitPane(
          first = {},
          second = {},
          state = state,
          modifier = Modifier.width(400.dp).height(200.dp),
        )
      }
    }
    onNodeWithContentDescription("Resize panes").requestFocus()
    onNodeWithContentDescription("Resize panes").performKeyInput { pressKey(Key.DirectionRight) }
    assertTrue(state.fraction > 0.5f, "divider should move right, was ${state.fraction}")
  }

  @Test
  fun toggleIconButtonReportsItsCheckedState() = runComposeUiTest {
    var on = false
    setContent {
      DittoTheme(idiom = Idiom.Desktop) {
        ToggleIconButton(checked = on, onCheckedChange = { on = it }, modifier = Modifier.testTag("toggle")) {
          com.r0adkll.ditto.foundation.Icon(com.r0adkll.ditto.icons.DittoIcons.check, contentDescription = "Mute")
        }
      }
    }
    val config = onNodeWithTag("toggle").fetchSemanticsNode().config
    assertNotNull(config[SemanticsProperties.ToggleableState], "should expose a toggleable state")
  }
}
