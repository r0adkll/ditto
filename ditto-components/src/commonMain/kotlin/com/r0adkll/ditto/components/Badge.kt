package com.r0adkll.ditto.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.theme.DittoTheme

@Immutable
public class BadgeStyle(
  public val containerColor: Color,
  public val contentColor: Color,
  public val size: Dp,
  public val dotSize: Dp,
  public val textStyle: TextStyle,
  public val horizontalPadding: Dp,
) {
  public fun copy(
    containerColor: Color = this.containerColor,
    contentColor: Color = this.contentColor,
    size: Dp = this.size,
    dotSize: Dp = this.dotSize,
    textStyle: TextStyle = this.textStyle,
    horizontalPadding: Dp = this.horizontalPadding,
  ): BadgeStyle = BadgeStyle(containerColor, contentColor, size, dotSize, textStyle, horizontalPadding)

  override fun equals(other: Any?): Boolean = other is BadgeStyle &&
    containerColor == other.containerColor && contentColor == other.contentColor && size == other.size &&
    dotSize == other.dotSize && textStyle == other.textStyle && horizontalPadding == other.horizontalPadding

  override fun hashCode(): Int = listOf(containerColor, contentColor, size, dotSize, textStyle, horizontalPadding).hashCode()
  override fun toString(): String = "BadgeStyle(size=$size)"
}

public val LocalBadgeStyle: ProvidableCompositionLocal<BadgeStyle?> = staticCompositionLocalOf { null }

public object BadgeDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): BadgeStyle {
    val colors = DittoTheme.colors
    val type = DittoTheme.typography
    return when (idiom) {
      Idiom.Android -> BadgeStyle(colors.error, colors.onError, 16.dp, 6.dp, type.caption.copy(fontSize = 11.sp()), 4.dp)
      Idiom.Apple -> BadgeStyle(colors.error, colors.onError, 18.dp, 8.dp, type.caption.copy(fontSize = 12.sp()), 5.dp)
      Idiom.Desktop -> BadgeStyle(colors.accent, colors.onAccent, 16.dp, 6.dp, type.caption.copy(fontSize = 11.sp()), 5.dp)
    }
  }
}

private fun Int.sp() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)

/** A small count / status pill. `null` [text] renders a dot. */
@Composable
public fun Badge(
  text: String? = null,
  modifier: Modifier = Modifier,
  style: BadgeStyle? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalBadgeStyle.current ?: DittoTheme.styleOverrides.resolve(BadgeDefaults.style())
  val shape = DittoTheme.shapes.full
  if (text == null) {
    Box(modifier.size(style.dotSize).background(style.containerColor, shape))
  } else {
    Box(
      modifier
        .defaultMinSize(minWidth = style.size, minHeight = style.size)
        .background(style.containerColor, shape)
        .padding(horizontal = style.horizontalPadding),
      contentAlignment = Alignment.Center,
    ) {
      Text(text, style = style.textStyle, color = style.contentColor, maxLines = 1)
    }
  }
}

/** Overlays a [Badge] on the top-end corner of [content]. `null` [badge] draws nothing. */
@Composable
public fun BadgedBox(
  badge: String?,
  modifier: Modifier = Modifier,
  showDot: Boolean = false,
  content: @Composable () -> Unit,
) {
  Box(modifier) {
    content()
    if (badge != null || showDot) {
      Badge(
        text = badge,
        modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-4).dp),
      )
    }
  }
}
