package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.theme.DittoTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SliderInteractionTest {
  @Test
  fun steppedSliderMovesWhenDragged() = runComposeUiTest {
    var value by mutableFloatStateOf(0f)
    setContent {
      DittoTheme {
        Slider(value = value, onValueChange = { value = it }, steps = 4, modifier = Modifier.width(300.dp).testTag("slider"))
      }
    }
    onNodeWithTag("slider").performTouchInput { swipeRight(startX = 20f, endX = 150f) }
    waitForIdle()
    assertTrue(value > 0f, "stepped slider should move on drag, was $value")
    // Steps of 0.2: the emitted value must sit on a step.
    val step = 0.2f
    val nearest = (value / step).let { kotlin.math.round(it) } * step
    assertEquals(nearest, value, 0.001f)
  }

  @Test
  fun continuousSliderFollowsDrag() = runComposeUiTest {
    var value by mutableFloatStateOf(0.5f)
    setContent {
      DittoTheme {
        Slider(value = value, onValueChange = { value = it }, modifier = Modifier.width(300.dp).testTag("slider"))
      }
    }
    onNodeWithTag("slider").performTouchInput { swipeRight(startX = 150f, endX = 280f) }
    waitForIdle()
    assertTrue(value > 0.8f, "continuous slider should follow the drag, was $value")
  }
}

@OptIn(ExperimentalTestApi::class)
class SliderPressTest {
  @Test
  fun pressJumpsToPointerThenDrags() = runComposeUiTest {
    var value by mutableFloatStateOf(0.1f)
    setContent {
      DittoTheme {
        Slider(value = value, onValueChange = { value = it }, modifier = Modifier.width(300.dp).testTag("slider"))
      }
    }
    // Press near 80% without moving: the thumb should jump there.
    onNodeWithTag("slider").performTouchInput { down(androidx.compose.ui.geometry.Offset(240f, centerY)); up() }
    waitForIdle()
    assertTrue(value > 0.7f && value < 0.9f, "press should jump to ~0.8, was $value")
    // Then drag from a new press point.
    onNodeWithTag("slider").performTouchInput { swipeRight(startX = 60f, endX = 90f) }
    waitForIdle()
    assertTrue(value > 0.15f && value < 0.45f, "drag from 20% to 30% should land near 0.3, was $value")
  }
}

@OptIn(ExperimentalTestApi::class)
class RangeSliderInteractionTest {
  @Test
  fun pressGrabsNearestThumbAndDrags() = runComposeUiTest {
    var range by androidx.compose.runtime.mutableStateOf(0.2f..0.8f)
    setContent {
      DittoTheme {
        RangeSlider(value = range, onValueChange = { range = it }, modifier = Modifier.width(300.dp).testTag("range"))
      }
    }
    // Press near the end thumb and drag left: only the end should move.
    onNodeWithTag("range").performTouchInput { swipeLeft(startX = 230f, endX = 160f) }
    waitForIdle()
    assertEquals(0.2f, range.start, 0.05f)
    assertTrue(range.endInclusive < 0.65f && range.endInclusive > 0.45f, "end should follow drag, was ${range.endInclusive}")
  }
}
