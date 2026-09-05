package com.r0adkll.ditto.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.theme.DittoTheme
import kotlin.math.roundToInt

@Immutable
public class SliderStyle(
  public val trackHeight: Dp,
  public val activeTrackColor: Color,
  public val inactiveTrackColor: Color,
  /** Thumb width; for Android's Expressive handle this is the narrow dimension. */
  public val thumbWidth: Dp,
  public val thumbHeight: Dp,
  public val thumbColor: Color,
  public val thumbBorderColor: Color,
  public val thumbBorderWidth: Dp,
  public val thumbShadow: Dp,
  /** Android Expressive: gap between the track halves and the handle. */
  public val trackGap: Dp,
  public val tickColor: Color,
  /** Total height reserved for hit testing. */
  public val touchHeight: Dp,
) {
  public fun copy(
    trackHeight: Dp = this.trackHeight,
    activeTrackColor: Color = this.activeTrackColor,
    inactiveTrackColor: Color = this.inactiveTrackColor,
    thumbWidth: Dp = this.thumbWidth,
    thumbHeight: Dp = this.thumbHeight,
    thumbColor: Color = this.thumbColor,
    thumbBorderColor: Color = this.thumbBorderColor,
    thumbBorderWidth: Dp = this.thumbBorderWidth,
    thumbShadow: Dp = this.thumbShadow,
    trackGap: Dp = this.trackGap,
    tickColor: Color = this.tickColor,
    touchHeight: Dp = this.touchHeight,
  ): SliderStyle = SliderStyle(
    trackHeight, activeTrackColor, inactiveTrackColor, thumbWidth, thumbHeight, thumbColor, thumbBorderColor,
    thumbBorderWidth, thumbShadow, trackGap, tickColor, touchHeight,
  )

  override fun equals(other: Any?): Boolean = other is SliderStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "SliderStyle(trackHeight=$trackHeight)"
  private fun fields(): List<Any?> = listOf(
    trackHeight, activeTrackColor, inactiveTrackColor, thumbWidth, thumbHeight, thumbColor, thumbBorderColor,
    thumbBorderWidth, thumbShadow, trackGap, tickColor, touchHeight,
  )
}

public val LocalSliderStyle: ProvidableCompositionLocal<SliderStyle?> = staticCompositionLocalOf { null }

public object SliderDefaults {
  /** Android: thick Expressive track with a bar handle. Apple: thin track, round white thumb. Desktop: thin track, bordered thumb. */
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): SliderStyle {
    val colors = DittoTheme.colors
    return when (idiom) {
      Idiom.Android -> SliderStyle(
        trackHeight = 16.dp,
        activeTrackColor = colors.accent,
        inactiveTrackColor = colors.accent.copy(alpha = 0.16f),
        thumbWidth = 4.dp,
        thumbHeight = 44.dp,
        thumbColor = colors.accent,
        thumbBorderColor = Color.Transparent,
        thumbBorderWidth = 0.dp,
        thumbShadow = 0.dp,
        trackGap = 6.dp,
        tickColor = colors.onAccent.copy(alpha = 0.6f),
        touchHeight = 48.dp,
      )
      Idiom.Apple -> SliderStyle(
        trackHeight = 4.dp,
        activeTrackColor = colors.accent,
        inactiveTrackColor = colors.neutrals[5],
        thumbWidth = 27.dp,
        thumbHeight = 27.dp,
        thumbColor = Color.White,
        thumbBorderColor = Color.Transparent,
        thumbBorderWidth = 0.dp,
        thumbShadow = 3.dp,
        trackGap = 0.dp,
        tickColor = colors.onSurfaceVariant,
        touchHeight = 44.dp,
      )
      Idiom.Desktop -> SliderStyle(
        trackHeight = 6.dp,
        activeTrackColor = colors.accent,
        inactiveTrackColor = colors.neutrals[4],
        thumbWidth = 16.dp,
        thumbHeight = 16.dp,
        thumbColor = colors.surface,
        thumbBorderColor = colors.accent,
        thumbBorderWidth = 2.dp,
        thumbShadow = 0.dp,
        trackGap = 0.dp,
        tickColor = colors.onSurfaceVariant,
        touchHeight = 32.dp,
      )
    }
  }
}

/**
 * Continuous or stepped value picker. [steps] is the number of discrete stops *between* the range
 * ends (0 = continuous). Drag or tap anywhere on the track.
 */
@Composable
public fun Slider(
  value: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
  steps: Int = 0,
  onValueChangeFinished: (() -> Unit)? = null,
  style: SliderStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  require(steps >= 0) { "steps must be >= 0" }
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalSliderStyle.current ?: SliderDefaults.style()
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val pointer = LocalInputCapabilities.current.pointer
  val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val onChange by rememberUpdatedState(onValueChange)
  val onFinished by rememberUpdatedState(onValueChangeFinished)
  var widthPx by remember { mutableFloatStateOf(0f) }
  val dragged by interactionSource.collectIsDraggedAsState()
  val pressed by interactionSource.collectIsPressedAsState()

  val span = valueRange.endInclusive - valueRange.start
  fun snap(raw: Float): Float {
    val clamped = raw.coerceIn(valueRange.start, valueRange.endInclusive)
    if (steps == 0 || span == 0f) return clamped
    val stepSize = span / (steps + 1)
    return valueRange.start + ((clamped - valueRange.start) / stepSize).roundToInt() * stepSize
  }
  fun fractionOf(v: Float): Float = if (span == 0f) 0f else ((v - valueRange.start) / span).coerceIn(0f, 1f)
  fun valueAt(px: Float): Float {
    if (widthPx <= 0f) return value
    val f = (px / widthPx).coerceIn(0f, 1f)
    val fraction = if (rtl) 1f - f else f
    return snap(valueRange.start + fraction * span)
  }

  val fraction = fractionOf(value)
  val draggable = rememberDraggableState { delta ->
    if (widthPx <= 0f) return@rememberDraggableState
    val deltaFraction = (if (rtl) -delta else delta) / widthPx
    onChange(snap(value + deltaFraction * span))
  }

  Box(
    modifier
      .fillMaxWidth()
      .height(style.touchHeight)
      .focusRing(interactionSource, DittoTheme.shapes.full)
      .progressSemantics(value, valueRange, steps)
      .semantics {
        if (!enabled) disabled()
        setProgress { target -> onChange(snap(target)); true }
      }
      .onSizeChanged { widthPx = it.width.toFloat() }
      .then(
        if (enabled) {
          Modifier
            .draggable(
              state = draggable,
              orientation = Orientation.Horizontal,
              interactionSource = interactionSource,
              onDragStopped = { onFinished?.invoke() },
            )
            .pointerInput(valueRange, steps) {
              detectTapGestures(onTap = { offset -> onChange(valueAt(offset.x)); onFinished?.invoke() })
            }
            .then(if (pointer) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
        } else {
          Modifier
        },
      ),
  ) {
    Canvas(Modifier.fillMaxWidth().height(style.touchHeight)) {
      drawSlider(style, fraction, steps, rtl, alpha, active = dragged || pressed)
    }
  }
}

private fun DrawScope.drawSlider(style: SliderStyle, fraction: Float, steps: Int, rtl: Boolean, alpha: Float, active: Boolean) {
  val trackH = style.trackHeight.toPx()
  val thumbW = style.thumbWidth.toPx()
  val thumbH = style.thumbHeight.toPx()
  val gap = style.trackGap.toPx()
  val cy = size.height / 2
  val pad = maxOf(thumbW / 2, trackH / 2)
  val usable = size.width - pad * 2
  val f = if (rtl) 1f - fraction else fraction
  val thumbX = pad + usable * f
  val radius = CornerRadius(trackH / 2)

  fun track(color: Color, from: Float, to: Float) {
    if (to - from <= 0.5f) return
    drawRoundRect(color.copy(alpha = color.alpha * alpha), Offset(from, cy - trackH / 2), Size(to - from, trackH), radius)
  }
  val activeFrom = if (rtl) thumbX + gap + thumbW / 2 else pad
  val activeTo = if (rtl) size.width - pad else thumbX - gap - thumbW / 2
  val inactiveFrom = if (rtl) pad else thumbX + gap + thumbW / 2
  val inactiveTo = if (rtl) thumbX - gap - thumbW / 2 else size.width - pad
  if (gap > 0f) {
    track(style.activeTrackColor, activeFrom, activeTo)
    track(style.inactiveTrackColor, inactiveFrom, inactiveTo)
  } else {
    track(style.inactiveTrackColor, pad, size.width - pad)
    if (rtl) track(style.activeTrackColor, thumbX, size.width - pad) else track(style.activeTrackColor, pad, thumbX)
  }
  if (steps > 0) {
    val tick = (trackH * 0.25f).coerceAtLeast(1.5f)
    for (i in 0..steps + 1) {
      val x = pad + usable * i / (steps + 1)
      drawCircle(style.tickColor.copy(alpha = style.tickColor.alpha * alpha), tick, Offset(x, cy))
    }
  }
  val h = thumbH
  val w = if (active && gap > 0f) thumbW * 0.5f else thumbW
  if (style.thumbShadow > 0.dp) {
    drawCircle(Color.Black.copy(alpha = 0.18f * alpha), thumbW / 2 + 1.dp.toPx(), Offset(thumbX, cy + 1.5.dp.toPx()))
  }
  if (thumbW == thumbH) {
    drawCircle(style.thumbColor.copy(alpha = style.thumbColor.alpha * alpha), thumbW / 2, Offset(thumbX, cy))
    if (style.thumbBorderWidth > 0.dp) {
      drawCircle(
        style.thumbBorderColor.copy(alpha = style.thumbBorderColor.alpha * alpha),
        thumbW / 2 - style.thumbBorderWidth.toPx() / 2,
        Offset(thumbX, cy),
        style = Stroke(style.thumbBorderWidth.toPx()),
      )
    }
  } else {
    drawRoundRect(
      style.thumbColor.copy(alpha = style.thumbColor.alpha * alpha),
      Offset(thumbX - w / 2, cy - h / 2),
      Size(w, h),
      CornerRadius(w / 2),
    )
  }
}
