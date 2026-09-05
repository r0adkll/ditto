package com.r0adkll.ditto

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.r0adkll.ditto.tokens.NeutralRamp
import com.r0adkll.ditto.tokens.Neutrals
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NeutralRampTest {
  @Test
  fun lightRampGetsDarkerStepByStep() {
    val ramp = NeutralRamp.generate(Neutrals.Cool, dark = false)
    val lums = ramp.asList().map { it.luminance() }
    assertEquals(12, lums.size)
    lums.zipWithNext().forEach { (a, b) -> assertTrue(a > b, "expected $a > $b") }
  }

  @Test
  fun darkRampGetsLighterStepByStep() {
    val ramp = NeutralRamp.generate(Neutrals.Warm, dark = true)
    val lums = ramp.asList().map { it.luminance() }
    lums.zipWithNext().forEach { (a, b) -> assertTrue(a < b, "expected $a < $b") }
  }

  @Test
  fun pureRampHasNoChroma() {
    val ramp = NeutralRamp.generate(Neutrals.Pure, dark = false)
    ramp.asList().forEach { c ->
      assertEquals(c.red, c.green, 0.001f)
      assertEquals(c.green, c.blue, 0.001f)
    }
  }

  @Test
  fun tintedRampFollowsAccentHue() {
    val red = NeutralRamp.generate(Neutrals.Tinted, dark = false, accent = Color(0xFFE53935))
    val mid = red[6]
    assertTrue(mid.red > mid.blue, "tinted-by-red neutral should lean red: $mid")
  }

  @Test
  fun stepsAreOneBased() {
    val ramp = NeutralRamp.generate(Neutrals.Cool, dark = false)
    assertEquals(ramp.asList().first(), ramp[1])
    assertEquals(ramp.asList().last(), ramp[12])
  }
}
