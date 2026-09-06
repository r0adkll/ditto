package com.r0adkll.ditto.spike

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import com.r0adkll.ditto.screenshot.assertScreenshot
import java.io.File
import kotlin.test.Test

/** Produces the side-by-side comparison images (light + dark) into build/compare for the vault. */
class CompareScreenshotTest {
  @Test
  fun sideBySide() = assertScreenshot("ditto-vs-jewel", width = 880, height = 1500, goldenRoot = File("build/compare")) {
    Column {
      Row { DittoSampleForm(dark = false); JewelSampleForm(dark = false) }
      Row { DittoSampleForm(dark = true); JewelSampleForm(dark = true) }
    }
  }
}
