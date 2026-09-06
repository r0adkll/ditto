package com.r0adkll.ditto.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.LocalContentColor
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.interaction.pressScale
import com.r0adkll.ditto.theme.DittoTheme

@Immutable
public class FabStyle(
  public val size: Dp,
  public val shape: Shape,
  public val containerColor: Color,
  public val contentColor: Color,
  public val shadow: Dp,
  public val iconSize: Dp,
  public val textStyle: TextStyle,
  public val extendedPadding: Dp,
) {
  public fun copy(
    size: Dp = this.size,
    shape: Shape = this.shape,
    containerColor: Color = this.containerColor,
    contentColor: Color = this.contentColor,
    shadow: Dp = this.shadow,
    iconSize: Dp = this.iconSize,
    textStyle: TextStyle = this.textStyle,
    extendedPadding: Dp = this.extendedPadding,
  ): FabStyle = FabStyle(size, shape, containerColor, contentColor, shadow, iconSize, textStyle, extendedPadding)

  override fun equals(other: Any?): Boolean = other is FabStyle &&
    size == other.size && shape == other.shape && containerColor == other.containerColor && contentColor == other.contentColor &&
    shadow == other.shadow && iconSize == other.iconSize && textStyle == other.textStyle && extendedPadding == other.extendedPadding

  override fun hashCode(): Int = listOf(size, shape, containerColor, contentColor, shadow, iconSize, textStyle, extendedPadding).hashCode()
  override fun toString(): String = "FabStyle(size=$size)"
}

public val LocalFabStyle: ProvidableCompositionLocal<FabStyle?> = staticCompositionLocalOf { null }

public object FabDefaults {
  /** Android: M3-esque rounded square. Apple/Desktop: a prominent round accent button (ADR: FAB is an Android concept). */
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): FabStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val type = DittoTheme.typography
    return when (idiom) {
      Idiom.Android -> FabStyle(56.dp, shapes.large, colors.accent, colors.onAccent, 6.dp, 24.dp, type.label, 20.dp)
      Idiom.Apple -> FabStyle(56.dp, shapes.full, colors.accent, colors.onAccent, 8.dp, 24.dp, type.label, 20.dp)
      Idiom.Desktop -> FabStyle(48.dp, shapes.full, colors.accent, colors.onAccent, 4.dp, 20.dp, type.label, 16.dp)
    }
  }
}

/** The primary screen action. Pass [text] for the extended form. */
@Composable
public fun FloatingActionButton(
  onClick: () -> Unit,
  icon: ImageVector,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  text: String? = null,
  enabled: Boolean = true,
  style: FabStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalFabStyle.current ?: FabDefaults.style()
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val pointer = LocalInputCapabilities.current.pointer
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  Row(
    modifier
      .pressScale(interactionSource, enabled)
      .focusRing(interactionSource, style.shape)
      .then(if (style.shadow > 0.dp && enabled) Modifier.shadow(style.shadow, style.shape) else Modifier)
      .background(style.containerColor.copy(alpha = style.containerColor.alpha * alpha), style.shape)
      .clip(style.shape)
      .clickable(interactionSource, LocalIndication.current, enabled = enabled, role = Role.Button, onClick = onClick)
      .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
      .defaultMinSize(minWidth = style.size, minHeight = style.size)
      .then(if (text != null) Modifier.padding(horizontal = style.extendedPadding) else Modifier),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    CompositionLocalProvider(LocalContentColor provides style.contentColor.copy(alpha = alpha)) {
      Icon(icon, contentDescription = if (text == null) contentDescription else null, size = style.iconSize)
      if (text != null) {
        Spacer(Modifier.width(DittoTheme.spacing.sm))
        Text(text, style = style.textStyle)
      }
    }
  }
}
