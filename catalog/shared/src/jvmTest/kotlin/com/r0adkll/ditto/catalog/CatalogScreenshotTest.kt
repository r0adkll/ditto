package com.r0adkll.ditto.catalog

import com.r0adkll.ditto.screenshot.assertScreenshot
import kotlin.test.Test

/** Smoke test: the whole catalog composes and renders in the platform default idiom. */
class CatalogScreenshotTest {
  @Test
  fun catalogRenders() = assertScreenshot("catalog", width = 1100, height = 1500) {
    CatalogApp()
  }
}
