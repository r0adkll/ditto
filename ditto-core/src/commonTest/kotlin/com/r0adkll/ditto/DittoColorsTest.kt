package com.r0adkll.ditto

import androidx.compose.ui.graphics.Color
import com.r0adkll.ditto.theme.DittoDefaults
import com.r0adkll.ditto.tokens.Contrast
import com.r0adkll.ditto.tokens.DittoColors
import com.r0adkll.ditto.tokens.Neutrals
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DittoColorsTest {
  private val accents = listOf(
    DittoDefaults.Accent,
    Color(0xFFE53935), // red
    Color(0xFF2E7D32), // green
    Color(0xFF6A1B9A), // purple
    Color(0xFFFFB300), // amber — forces a dark onAccent
    Color(0xFF111111), // near black
  )

  @Test
  fun neutralAndStatusPairsMeetWcagTargetsForAnyAccent() {
    for (accent in accents) {
      for (dark in listOf(false, true)) {
        for (neutrals in Neutrals.entries) {
          val scheme = DittoColors.from(accent, dark = dark, neutrals = neutrals)
          // The accent itself is the app's choice; a saturated red cannot reach 4.5:1 with any
          // on-color. validateContrast() reports that; the neutral and status pairs must always pass.
          val failures = scheme.validateContrast().filterNot { it.startsWith("accent on surface") || it.startsWith("onAccent on accent") }
          assertTrue(failures.isEmpty(), "accent=$accent dark=$dark neutrals=$neutrals: $failures")
        }
      }
    }
  }

  @Test
  fun defaultAccentPassesEverythingInBothModes() {
    for (dark in listOf(false, true)) {
      val scheme = DittoColors.from(DittoDefaults.Accent, dark = dark)
      assertTrue(scheme.validateContrast().isEmpty(), "dark=$dark: ${scheme.validateContrast()}")
    }
  }

  @Test
  fun onAccentIsTheBestAvailableChoiceEvenWhenNeitherPasses() {
    val red = DittoColors.from(Color(0xFFE53935), dark = false)
    val white = Contrast.ratio(Color.White, red.accent)
    val black = Contrast.ratio(red.neutrals[12], red.accent)
    assertEquals(maxOf(white, black), Contrast.ratio(red.onAccent, red.accent), 0.0001f)
    assertTrue(red.validateContrast().any { it.startsWith("onAccent on accent") })
  }

  @Test
  fun onAccentPicksTheHigherContrastOption() {
    val amber = DittoColors.from(Color(0xFFFFB300), dark = false)
    assertTrue(Contrast.ratio(amber.onAccent, amber.accent) >= Contrast.BodyText)
    val navy = DittoColors.from(Color(0xFF1A237E), dark = false)
    assertEquals(Color.White, navy.onAccent)
  }

  @Test
  fun contentColorForKnowsSurfaceRoles() {
    val scheme = DittoColors.from(DittoDefaults.Accent, dark = false)
    assertEquals(scheme.onAccent, scheme.contentColorFor(scheme.accent))
    assertEquals(scheme.onSurface, scheme.contentColorFor(scheme.surfaceRaised))
    assertEquals(Color.Unspecified, scheme.contentColorFor(Color.Magenta))
  }

  @Test
  fun contrastRatioIsSymmetricAndBounded() {
    assertEquals(21f, Contrast.ratio(Color.White, Color.Black), 0.01f)
    assertEquals(Contrast.ratio(Color.Red, Color.White), Contrast.ratio(Color.White, Color.Red), 0.0001f)
  }
}
