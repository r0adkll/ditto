package com.r0adkll.ditto.spike.unstyled

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledSlider
import com.r0adkll.ditto.components.SliderDefaults
import com.r0adkll.ditto.components.SliderStyle
import com.r0adkll.ditto.theme.DittoTheme

/**
 * Ditto's slider look on Unstyled's slider behaviour (gestures, keyboard, semantics, steps).
 * Compare with `ditto-components/.../Slider.kt` (~330 lines incl. drawing).
 */
@Composable
fun USlider(
  value: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
  steps: Int = 0,
  onValueChangeFinished: (() -> Unit)? = null,
  style: SliderStyle = SliderDefaults.style(),
) {
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val shape = DittoTheme.shapes.full
  UnstyledSlider(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier.fillMaxWidth().height(style.touchHeight),
    enabled = enabled,
    valueRange = valueRange,
    steps = steps,
    onValueChangeFinished = onValueChangeFinished,
    track = { state ->
      // Unstyled hands us SliderState (fraction, isDragging, tickFractions...) and lays the thumb over this.
      Box(Modifier.fillMaxWidth().height(style.trackHeight).background(style.inactiveTrackColor.copy(alpha = style.inactiveTrackColor.alpha * alpha), shape)) {
        Box(Modifier.fillMaxWidth(state.fraction).height(style.trackHeight).background(style.activeTrackColor.copy(alpha = style.activeTrackColor.alpha * alpha), shape))
        // Ticks come from SliderState.tickFractions.
        state.tickFractions.forEach { f ->
          Box(Modifier.fillMaxWidth(f).height(style.trackHeight), contentAlignment = Alignment.CenterEnd) {
            Box(Modifier.size((style.trackHeight.value * 0.4f).dp).background(style.tickColor.copy(alpha = style.tickColor.alpha * alpha), shape))
          }
        }
      }
    },
    thumb = { state ->
      val thumbSize = if (state.isDragging && style.trackGap > 0.dp) style.thumbWidth / 2 else style.thumbWidth
      Box(
        Modifier
          .size(width = thumbSize, height = style.thumbHeight)
          .then(if (style.thumbShadow > 0.dp && enabled) Modifier.shadow(style.thumbShadow, shape) else Modifier)
          .background(style.thumbColor.copy(alpha = style.thumbColor.alpha * alpha), shape)
          .then(if (style.thumbBorderWidth > 0.dp) Modifier.border(style.thumbBorderWidth, style.thumbBorderColor.copy(alpha = style.thumbBorderColor.alpha * alpha), shape) else Modifier),
      )
    },
  )
}
