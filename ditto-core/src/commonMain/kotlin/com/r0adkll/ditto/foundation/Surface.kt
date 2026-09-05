package com.r0adkll.ditto.foundation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.DittoColors
import com.r0adkll.ditto.tokens.ElevationLevel

/**
 * A container that paints a background, clips to [shape], renders its [elevation] the way the
 * active idiom does (shadow, ramp step, border; ADR-022), and provides [contentColor].
 */
@Composable
public fun Surface(
  modifier: Modifier = Modifier,
  shape: Shape = RectangleShape,
  color: Color = DittoTheme.colors.surface,
  contentColor: Color = contentColorFor(color),
  elevation: ElevationLevel = ElevationLevel.Level0,
  border: BorderStroke? = null,
  content: @Composable () -> Unit,
) {
  val colors = DittoTheme.colors
  val style = DittoTheme.elevation[elevation]
  val resolvedColor = colors.surfaceAtElevation(color, style.surfaceStep)
  val resolvedBorder = border ?: if (style.border) {
    BorderStroke(DittoTheme.dimens.borderWidth, colors.outlineVariant)
  } else {
    null
  }
  CompositionLocalProvider(LocalContentColor provides contentColor) {
    Box(
      modifier
        .then(if (style.shadow > 0.dp) Modifier.shadow(style.shadow, shape, clip = false) else Modifier)
        .then(if (resolvedBorder != null) Modifier.border(resolvedBorder, shape) else Modifier)
        .background(resolvedColor, shape)
        .clip(shape),
      propagateMinConstraints = true,
    ) {
      content()
    }
  }
}

/**
 * Steps [color] up the neutral ramp by [steps] if it is a ramp color; other colors are returned
 * unchanged. This is how dark mode separates surfaces without tonal palettes (ADR-022).
 */
public fun DittoColors.surfaceAtElevation(color: Color, steps: Int): Color {
  if (steps <= 0) return color
  val index = neutrals.asList().indexOf(color)
  if (index < 0) return color
  val target = (index + 1 + steps).coerceAtMost(12)
  return neutrals[target]
}

@Composable
@ReadOnlyComposable
internal fun surfaceColorAtElevation(color: Color, level: ElevationLevel): Color =
  DittoTheme.colors.surfaceAtElevation(color, DittoTheme.elevation[level].surfaceStep)
