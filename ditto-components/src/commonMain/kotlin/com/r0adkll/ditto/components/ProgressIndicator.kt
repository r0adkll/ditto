package com.r0adkll.ditto.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.theme.DittoTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Immutable
public class ProgressIndicatorStyle(
  public val color: Color,
  public val trackColor: Color,
  public val circularSize: Dp,
  public val circularStroke: Dp,
  public val linearHeight: Dp,
  public val strokeCap: StrokeCap,
  /** Apple-style spoked activity indicator for indeterminate circular progress. */
  public val spokes: Boolean,
  /** Android Expressive: gap between the active bar and the track plus a stop dot. */
  public val linearGap: Dp,
  /** Color of Apple's spoked activity indicator (progress bars still use [color]). */
  public val spokeColor: Color = color,
) {
  public fun copy(
    color: Color = this.color,
    trackColor: Color = this.trackColor,
    circularSize: Dp = this.circularSize,
    circularStroke: Dp = this.circularStroke,
    linearHeight: Dp = this.linearHeight,
    strokeCap: StrokeCap = this.strokeCap,
    spokes: Boolean = this.spokes,
    linearGap: Dp = this.linearGap,
    spokeColor: Color = this.spokeColor,
  ): ProgressIndicatorStyle = ProgressIndicatorStyle(color, trackColor, circularSize, circularStroke, linearHeight, strokeCap, spokes, linearGap, spokeColor)

  override fun equals(other: Any?): Boolean = other is ProgressIndicatorStyle &&
    color == other.color && trackColor == other.trackColor && circularSize == other.circularSize &&
    circularStroke == other.circularStroke && linearHeight == other.linearHeight && strokeCap == other.strokeCap &&
    spokes == other.spokes && linearGap == other.linearGap && spokeColor == other.spokeColor

  override fun hashCode(): Int = listOf(color, trackColor, circularSize, circularStroke, linearHeight, strokeCap, spokes, linearGap, spokeColor).hashCode()
  override fun toString(): String = "ProgressIndicatorStyle(color=$color)"
}

public val LocalProgressIndicatorStyle: ProvidableCompositionLocal<ProgressIndicatorStyle?> = staticCompositionLocalOf { null }

public object ProgressIndicatorDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): ProgressIndicatorStyle {
    val colors = DittoTheme.colors
    return when (idiom) {
      Idiom.Android -> ProgressIndicatorStyle(
        color = colors.accent,
        trackColor = colors.accent.copy(alpha = 0.16f),
        circularSize = 40.dp,
        circularStroke = 4.dp,
        linearHeight = 4.dp,
        strokeCap = StrokeCap.Round,
        spokes = false,
        linearGap = 4.dp,
      )
      Idiom.Apple -> ProgressIndicatorStyle(
        color = colors.accent,
        trackColor = colors.neutrals[5],
        circularSize = 20.dp,
        circularStroke = 2.dp,
        linearHeight = 4.dp,
        strokeCap = StrokeCap.Round,
        spokes = true,
        linearGap = 0.dp,
        spokeColor = colors.onSurfaceVariant,
      )
      Idiom.Desktop -> ProgressIndicatorStyle(
        color = colors.accent,
        trackColor = colors.neutrals[if (colors.isDark) 5 else 4],
        circularSize = 20.dp,
        circularStroke = 2.dp,
        linearHeight = 8.dp,
        strokeCap = StrokeCap.Round,
        spokes = false,
        linearGap = 0.dp,
      )
    }
  }

  @Composable
  @ReadOnlyComposable
  internal fun resolve(explicit: ProgressIndicatorStyle?): ProgressIndicatorStyle =
    explicit ?: LocalProgressIndicatorStyle.current ?: style()
}

/**
 * Circular progress. `null` [progress] is indeterminate: a rotating arc, or Apple's spoked
 * activity indicator. Determinate progress is in 0..1.
 */
@Composable
public fun CircularProgressIndicator(
  progress: (() -> Float)? = null,
  modifier: Modifier = Modifier,
  style: ProgressIndicatorStyle? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = ProgressIndicatorDefaults.resolve(style)
  val semantics = if (progress != null) Modifier.progressSemantics(progress().coerceIn(0f, 1f)) else Modifier.progressSemantics()
  if (progress == null) {
    val transition = rememberInfiniteTransition()
    val phase by transition.animateFloat(
      0f, 1f,
      infiniteRepeatable(tween(if (style.spokes) 800 else 1200, easing = LinearEasing), RepeatMode.Restart),
    )
    Canvas(modifier.then(semantics).size(style.circularSize)) {
      if (style.spokes) drawSpokes(phase, style.spokeColor) else drawIndeterminateArc(phase, style)
    }
  } else {
    Canvas(modifier.then(semantics).size(style.circularSize)) {
      val stroke = Stroke(style.circularStroke.toPx(), cap = style.strokeCap)
      val inset = stroke.width / 2
      val arcSize = Size(size.width - stroke.width, size.height - stroke.width)
      drawArc(style.trackColor, 0f, 360f, false, Offset(inset, inset), arcSize, style = stroke)
      drawArc(style.color, -90f, 360f * progress().coerceIn(0f, 1f), false, Offset(inset, inset), arcSize, style = stroke)
    }
  }
}

/** Linear progress. `null` [progress] is indeterminate (a sliding segment). */
@Composable
public fun LinearProgressIndicator(
  progress: (() -> Float)? = null,
  modifier: Modifier = Modifier,
  style: ProgressIndicatorStyle? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = ProgressIndicatorDefaults.resolve(style)
  val semantics = if (progress != null) Modifier.progressSemantics(progress().coerceIn(0f, 1f)) else Modifier.progressSemantics()
  val phase: Float = if (progress == null) {
    val transition = rememberInfiniteTransition()
    val p by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart))
    p
  } else {
    0f
  }
  Canvas(modifier.then(semantics).fillMaxWidth().height(style.linearHeight)) {
    val h = size.height
    val cap = style.strokeCap
    val y = h / 2
    fun bar(color: Color, from: Float, to: Float) {
      if (to - from <= 0f) return
      val pad = if (cap == StrokeCap.Round) h / 2 else 0f
      drawLine(color, Offset(from + pad, y), Offset((to - pad).coerceAtLeast(from + pad), y), strokeWidth = h, cap = cap)
    }
    if (progress != null) {
      val end = size.width * progress().coerceIn(0f, 1f)
      val gap = style.linearGap.toPx()
      bar(style.color, 0f, end)
      bar(style.trackColor, if (end > 0f) end + gap else 0f, size.width)
      if (gap > 0f) drawCircle(style.color, radius = h / 2, center = Offset(size.width - h / 2, y))
    } else {
      bar(style.trackColor, 0f, size.width)
      val segment = size.width * 0.35f
      val travel = size.width + segment
      val start = phase * travel - segment
      bar(style.color, start.coerceAtLeast(0f), (start + segment).coerceAtMost(size.width))
    }
  }
}

private fun DrawScope.drawIndeterminateArc(phase: Float, style: ProgressIndicatorStyle) {
  val stroke = Stroke(style.circularStroke.toPx(), cap = style.strokeCap)
  val inset = stroke.width / 2
  val arcSize = Size(size.width - stroke.width, size.height - stroke.width)
  // Head and tail move at different speeds so the arc breathes between 30° and 270°.
  val sweep = 30f + 240f * (0.5f - 0.5f * cos(phase * 2f * PI.toFloat()))
  rotate(phase * 360f * 2f) {
    drawArc(style.color, -90f, sweep, false, Offset(inset, inset), arcSize, style = stroke)
  }
}

private fun DrawScope.drawSpokes(phase: Float, color: Color) {
  val spokes = 8
  val radius = size.minDimension / 2
  val inner = radius * 0.5f
  val width = radius * 0.22f
  val center = Offset(size.width / 2, size.height / 2)
  val lit = (phase * spokes).toInt()
  for (i in 0 until spokes) {
    val angle = (i.toFloat() / spokes) * 2f * PI.toFloat() - PI.toFloat() / 2
    val distance = ((i - lit + spokes) % spokes)
    val alpha = 1f - distance.toFloat() / spokes
    drawLine(
      color.copy(alpha = color.alpha * (0.25f + 0.75f * alpha)),
      Offset(center.x + cos(angle) * inner, center.y + sin(angle) * inner),
      Offset(center.x + cos(angle) * radius, center.y + sin(angle) * radius),
      strokeWidth = width,
      cap = StrokeCap.Round,
    )
  }
}
