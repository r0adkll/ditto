package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.withKeyDown
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.input.Shortcut
import com.r0adkll.ditto.input.platformUsesMetaForShortcuts
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.foundation.Text
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class KeyboardInteractionTest {
  @Test
  fun treeArrowsMoveSelectionAndExpand() = runComposeUiTest {
    val state = TreeState(expanded = emptySet(), selected = "a")
    val roots = listOf(TreeNode("a", "A", children = listOf(TreeNode("a1", "A1"))), TreeNode("b", "B"))
    setContent {
      DittoTheme(idiom = Idiom.Desktop) {
        Tree(roots, state = state, modifier = Modifier.width(200.dp).height(200.dp).testTag("tree"))
      }
    }
    onNodeWithTag("tree").requestFocus()
    onNodeWithTag("tree").performKeyInput { pressKey(Key.DirectionRight) }   // expand A
    waitForIdle()
    assertTrue("a" in state.expanded)
    onNodeWithTag("tree").performKeyInput { pressKey(Key.DirectionDown) }    // -> A1
    waitForIdle()
    assertEquals("a1", state.selected)
    onNodeWithTag("tree").performKeyInput { pressKey(Key.DirectionLeft) }    // -> parent A
    waitForIdle()
    assertEquals("a", state.selected)
    onNodeWithTag("tree").performKeyInput { pressKey(Key.DirectionLeft) }    // collapse A
    waitForIdle()
    assertTrue("a" !in state.expanded)
    onNodeWithTag("tree").performKeyInput { pressKey(Key.DirectionDown) }    // -> B
    waitForIdle()
    assertEquals("b", state.selected)
  }

  @Test
  fun segmentedControlArrowsChangeSelection() = runComposeUiTest {
    var index by mutableIntStateOf(0)
    setContent {
      DittoTheme(idiom = Idiom.Desktop) {
        SegmentedControl(options = listOf("A", "B", "C"), selectedIndex = index, onSelect = { index = it }, modifier = Modifier.width(240.dp).testTag("seg"))
      }
    }
    onNodeWithTag("seg").requestFocus()
    onNodeWithTag("seg").performKeyInput { pressKey(Key.DirectionRight); pressKey(Key.DirectionRight); pressKey(Key.DirectionLeft) }
    waitForIdle()
    assertEquals(1, index)
  }

  @Test
  fun shortcutScopeFiresBinding() = runComposeUiTest {
    var fired by mutableStateOf(false)
    setContent {
      DittoTheme(idiom = Idiom.Desktop) {
        ShortcutScope(Shortcut(Key.K, primary = true) to { fired = true }, modifier = Modifier.testTag("scope")) {
          Column { Text("content") }
        }
      }
    }
    waitForIdle()
    onNodeWithTag("scope").performKeyInput {
      if (platformUsesMetaForShortcuts()) withKeyDown(Key.MetaLeft) { pressKey(Key.K) } else withKeyDown(Key.CtrlLeft) { pressKey(Key.K) }
    }
    waitForIdle()
    assertTrue(fired, "primary+K should fire")
  }

  @Test
  fun shortcutLabelsFollowPlatform() {
    val s = Shortcut(Key.R, primary = true, shift = true)
    val label = s.label()
    if (platformUsesMetaForShortcuts()) assertEquals("⇧⌘R", label) else assertEquals("Ctrl+Shift+R", label)
  }
}
