package com.r0adkll.ditto.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.theme.DittoTheme
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Link is built on [androidx.compose.ui.text.LinkAnnotation] rather than a clickable Row, so the
 * platform sees a link instead of a button. These pin the parts of that which are easy to lose:
 * that the annotation is really there, that a disabled link is not focusable or clickable, and that
 * the external arrow says what it means.
 */
@OptIn(ExperimentalTestApi::class)
class LinkSemanticsTest {
  @Test
  fun linkIsClickableAndInvokesOnClick() = runComposeUiTest {
    var clicks = 0
    setContent {
      DittoTheme(idiom = Idiom.Desktop) { Link("Learn more", onClick = { clicks++ }) }
    }
    onAllNodes(hasClickAction()).assertCountEquals(1)
    onAllNodes(hasClickAction())[0].performClick()
    assertEquals(1, clicks)
  }

  @Test
  fun disabledLinkHasNoLinkAtAll() = runComposeUiTest {
    setContent {
      DittoTheme(idiom = Idiom.Desktop) { Link("Learn more", onClick = {}, enabled = false) }
    }
    // No annotation means nothing to focus, click or announce as a link — rather than a link that
    // looks dimmed and still takes a tab stop.
    onAllNodes(hasClickAction()).assertCountEquals(0)
  }

  @Test
  fun externalArrowIsDescribed() = runComposeUiTest {
    setContent {
      DittoTheme(idiom = Idiom.Desktop) { Link("Docs", onClick = {}, external = true) }
    }
    onNodeWithContentDescription("opens externally").assertExists()
  }
}
