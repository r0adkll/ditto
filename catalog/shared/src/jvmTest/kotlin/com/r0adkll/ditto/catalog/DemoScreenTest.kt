package com.r0adkll.ditto.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.screenshot.assertScreenshot
import com.r0adkll.ditto.tokens.ColorMode
import kotlin.test.Test
import kotlin.test.assertTrue

/** The single-demo path the docs site embeds: `/catalog/?id=…&idiom=…&mode=…`. */
@OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)
class DemoScreenTest {
  @Test
  fun everyDemoIdIsUniqueAndSlugLike() {
    val ids = mutableListOf<String>()
    // Ids are public API — the docs site links to them, so a rename breaks published pages.
    val slug = Regex("[a-z0-9-]+")
    androidx.compose.ui.test.runComposeUiTest {
      setContent { catalogDemos().forEach { ids += it.id } }
    }
    assertTrue(ids.isNotEmpty(), "no demos registered")
    assertTrue(ids.distinct().size == ids.size, "duplicate demo ids: $ids")
    ids.forEach { assertTrue(slug.matches(it), "demo id is not slug-like: $it") }
  }

  /** What a docs page actually embeds: one frame, chrome bar on top, demo centred beneath. */
  @Test
  fun demoFrame() = assertScreenshot("demo-screen", width = 680, height = 420) {
    Column {
      Column(Modifier.size(660.dp, 200.dp)) {
        DemoScreen(id = "switch", idiom = Idiom.Apple, colorMode = ColorMode.Light)
      }
      Column(Modifier.size(660.dp, 200.dp)) {
        DemoScreen(id = "button", idiom = Idiom.Android, colorMode = ColorMode.Dark)
      }
    }
  }

  @Test
  fun unknownIdIsVisible() = assertScreenshot("demo-screen-unknown", width = 420, height = 140) {
    DemoScreen(id = "nope", idiom = Idiom.Desktop, colorMode = ColorMode.Light)
  }
}
