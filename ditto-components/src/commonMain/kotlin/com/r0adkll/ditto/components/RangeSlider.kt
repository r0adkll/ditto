package com.r0adkll.ditto.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.theme.DittoTheme
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Two-thumb slider selecting a sub-range. Pressing grabs the nearest thumb; thumbs cannot cross.
 * Shares [SliderStyle] with [Slider].
 */
@Composable
public fun RangeSlider(
  value: ClosedFloatingPointRange<Float>,
  onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
  steps: Int = 0,
  onValueChangeFinished: (() -> Unit)? = null,
  style: SliderStyle? = null,
) {
  require(steps >= 0) { "steps must be >= 0" }
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalSliderStyle.current ?: SliderDefaults.style()
  val pointer = LocalInputCapabilities.current.pointer
  val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val onChange by rememberUpdatedState(onValueChange)
  val onFinished by rememberUpdatedState(onValueChangeFinished)
  var widthPx by remember { mutableFloatStateOf(0f) }
  val span = valueRange.endInclusive - valueRange.start

  fun snap(raw: Float): Float {
    val clamped = raw.coerceIn(valueRange.start, valueRange.endInclusive)
    if (steps == 0 || span == 0f) return clamped
    val stepSize = span / (steps + 1)
    return valueRange.start + ((clamped - valueRange.start) / stepSize).roundToInt() * stepSize
  }
  fun fractionOf(v: Float): Float = if (span == 0f) 0f else ((v - valueRange.start) / span).coerceIn(0f, 1f)
  fun valueAtX(px: Float): Float {
    val f = (px / widthPx).coerceIn(0f, 1f)
    return valueRange.start + (if (rtl) 1f - f else f) * span
  }

  var rawStart by remember { mutableFloatStateOf(value.start) }
  var rawEnd by remember { mutableFloatStateOf(value.endInclusive) }
  var activeThumb by remember { mutableStateOf<Int?>(null) }
  if (activeThumb == null) {
    rawStart = value.start
    rawEnd = value.endInclusive
  }

  Box(
    modifier
      .fillMaxWidth()
      .height(style.touchHeight)
      .semantics {
        contentDescription = "Range ${value.start} to ${value.endInclusive}"
        if (!enabled) disabled()
      }
      .onSizeChanged { widthPx = it.width.toFloat() }
      .then(
        if (enabled) {
          Modifier
            .pointerInput(valueRange, steps, rtl) {
              awaitEachGesture {
                val down = awaitFirstDown()
                down.consume()
                if (widthPx <= 0f) return@awaitEachGesture
                val pressed = valueAtX(down.position.x)
                val thumb = if (abs(pressed - rawStart) <= abs(pressed - rawEnd)) 0 else 1
                activeThumb = thumb
                fun apply(raw: Float) {
                  if (thumb == 0) rawStart = raw.coerceIn(valueRange.start, rawEnd) else rawEnd = raw.coerceIn(rawStart, valueRange.endInclusive)
                  val next = snap(rawStart)..snap(rawEnd)
                  if (next != value) onChange(next)
                }
                apply(pressed)
                drag(down.id) { change ->
                  val delta = (if (rtl) -change.positionChange().x else change.positionChange().x) / widthPx * span
                  apply((if (thumb == 0) rawStart else rawEnd) + delta)
                  change.consume()
                }
                activeThumb = null
                onFinished?.invoke()
              }
            }
            .then(if (pointer) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
        } else {
          Modifier
        },
      ),
  ) {
    Canvas(Modifier.fillMaxWidth().height(style.touchHeight)) {
      drawRange(style, fractionOf(value.start), fractionOf(value.endInclusive), steps, rtl, alpha, activeThumb)
    }
  }
}

private fun DrawScope.drawRange(style: SliderStyle, startF: Float, endF: Float, steps: Int, rtl: Boolean, alpha: Float, active: Int?) {
  val trackH = style.trackHeight.toPx()
  val thumbW = style.thumbWidth.toPx()
  val thumbH = style.thumbHeight.toPx()
  val gap = style.trackGap.toPx()
  val cy = size.height / 2
  val pad = maxOf(thumbW / 2, trackH / 2)
  val usable = size.width - pad * 2
  fun xOf(f: Float) = pad + usable * (if (rtl) 1f - f else f)
  val xs = xOf(startF)
  val xe = xOf(endF)
  val left = minOf(xs, xe)
  val right = maxOf(xs, xe)
  val radius = CornerRadius(trackH / 2)
  fun track(color: Color, from: Float, to: Float) {
    if (to - from <= 0.5f) return
    drawRoundRect(color.copy(alpha = color.alpha * alpha), Offset(from, cy - trackH / 2), Size(to - from, trackH), radius)
  }
  track(style.inactiveTrackColor, pad, left - gap - thumbW / 2)
  track(style.activeTrackColor, left + gap + thumbW / 2, right - gap - thumbW / 2)
  track(style.inactiveTrackColor, right + gap + thumbW / 2, size.width - pad)
  if (gap <= 0f) {
    track(style.inactiveTrackColor, pad, size.width - pad)
    track(style.activeTrackColor, left, right)
  }
  if (steps > 0) {
    val tick = (trackH * 0.25f).coerceAtLeast(1.5f)
    for (i in 0..steps + 1) drawCircle(style.tickColor.copy(alpha = style.tickColor.alpha * alpha), tick, Offset(pad + usable * i / (steps + 1), cy))
  }
  listOf(xs to 0, xe to 1).forEach { (x, index) ->
    val w = if (active == index && gap > 0f) thumbW * 0.5f else thumbW
    if (style.thumbShadow > 0.dp) drawCircle(Color.Black.copy(alpha = 0.18f * alpha), thumbW / 2 + 1.dp.toPx(), Offset(x, cy + 1.5.dp.toPx()))
    if (thumbW == thumbH) {
      drawCircle(style.thumbColor.copy(alpha = style.thumbColor.alpha * alpha), thumbW / 2, Offset(x, cy))
      if (style.thumbBorderWidth > 0.dp) {
        drawCircle(style.thumbBorderColor.copy(alpha = style.thumbBorderColor.alpha * alpha), thumbW / 2 - style.thumbBorderWidth.toPx() / 2, Offset(x, cy), style = Stroke(style.thumbBorderWidth.toPx()))
      }
    } else {
      drawRoundRect(style.thumbColor.copy(alpha = style.thumbColor.alpha * alpha), Offset(x - w / 2, cy - thumbH / 2), Size(w, thumbH), CornerRadius(w / 2))
    }
  }
}
