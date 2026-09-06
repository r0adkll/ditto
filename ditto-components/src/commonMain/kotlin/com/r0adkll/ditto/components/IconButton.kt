package com.r0adkll.ditto.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.LocalContentColor
import com.r0adkll.ditto.foundation.LocalIconSize
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.interaction.minimumInteractiveSize
import com.r0adkll.ditto.interaction.pressScale
import com.r0adkll.ditto.theme.DittoTheme

/** Container treatments for icon buttons. */
public enum class IconButtonVariant {
  Standard,
  Filled,
  Tonal,
  Outlined,
}

@Immutable
public class IconButtonStyle(
  public val containerColor: Color,
  public val contentColor: Color,
  public val disabledContainerColor: Color,
  public val disabledContentColor: Color,
  public val shape: Shape,
  public val border: BorderStroke?,
  /** Visual container size; the hit target is at least the idiom minimum (ADR-021). */
  public val size: Dp,
  public val iconSize: Dp,
) {
  public fun copy(
    containerColor: Color = this.containerColor,
    contentColor: Color = this.contentColor,
    disabledContainerColor: Color = this.disabledContainerColor,
    disabledContentColor: Color = this.disabledContentColor,
    shape: Shape = this.shape,
    border: BorderStroke? = this.border,
    size: Dp = this.size,
    iconSize: Dp = this.iconSize,
  ): IconButtonStyle = IconButtonStyle(
    containerColor, contentColor, disabledContainerColor, disabledContentColor, shape, border, size, iconSize,
  )

  override fun equals(other: Any?): Boolean = other is IconButtonStyle &&
    containerColor == other.containerColor && contentColor == other.contentColor &&
    disabledContainerColor == other.disabledContainerColor && disabledContentColor == other.disabledContentColor &&
    shape == other.shape && border == other.border && size == other.size && iconSize == other.iconSize

  override fun hashCode(): Int = listOf(
    containerColor, contentColor, disabledContainerColor, disabledContentColor, shape, border, size, iconSize,
  ).hashCode()

  override fun toString(): String = "IconButtonStyle(containerColor=$containerColor, size=$size)"
}

@Immutable
public data class IconButtonStyles(
  val standard: IconButtonStyle? = null,
  val filled: IconButtonStyle? = null,
  val tonal: IconButtonStyle? = null,
  val outlined: IconButtonStyle? = null,
) {
  public operator fun get(variant: IconButtonVariant): IconButtonStyle? = when (variant) {
    IconButtonVariant.Standard -> standard
    IconButtonVariant.Filled -> filled
    IconButtonVariant.Tonal -> tonal
    IconButtonVariant.Outlined -> outlined
  }
}

public val LocalIconButtonStyles: ProvidableCompositionLocal<IconButtonStyles> =
  staticCompositionLocalOf { IconButtonStyles() }

public object IconButtonDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(variant: IconButtonVariant, idiom: Idiom = DittoTheme.idiom): IconButtonStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val dimens = DittoTheme.dimens
    val disabledContent = colors.onSurface.copy(alpha = colors.disabledAlpha)
    val disabledContainer = colors.onSurface.copy(alpha = 0.12f)
    val size = dimens.iconButtonSize
    val shape = when (idiom) {
      Idiom.Android, Idiom.Apple -> shapes.full
      Idiom.Desktop -> shapes.small
    }
    val standard = IconButtonStyle(
      containerColor = Color.Transparent,
      contentColor = if (idiom == Idiom.Apple) colors.accent else colors.onSurfaceVariant,
      disabledContainerColor = Color.Transparent,
      disabledContentColor = disabledContent,
      shape = shape,
      border = null,
      size = size,
      iconSize = dimens.iconSize,
    )
    return when (variant) {
      IconButtonVariant.Standard -> standard
      IconButtonVariant.Filled -> standard.copy(
        containerColor = colors.accent,
        contentColor = colors.onAccent,
        disabledContainerColor = disabledContainer,
      )
      IconButtonVariant.Tonal -> standard.copy(
        containerColor = colors.accent.copy(alpha = ButtonDefaults.TonalContainerAlpha),
        contentColor = colors.accent,
        disabledContainerColor = disabledContainer,
      )
      IconButtonVariant.Outlined -> standard.copy(
        contentColor = if (idiom == Idiom.Desktop) colors.onSurface else colors.accent,
        border = BorderStroke(dimens.borderWidth, if (idiom == Idiom.Desktop) colors.outlineVariant else colors.outline),
      )
    }
  }

  @Composable
  @ReadOnlyComposable
  internal fun resolve(explicit: IconButtonStyle?, variant: IconButtonVariant): IconButtonStyle =
    explicit ?: LocalIconButtonStyles.current[variant] ?: DittoTheme.styleOverrides.resolve(style(variant), variant)
}

/**
 * A button whose only content is an icon. [content] should be an [com.r0adkll.ditto.foundation.Icon]
 * with a content description (ADR-021).
 */
@Composable
public fun IconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: IconButtonStyle? = null,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  IconButtonImpl(IconButtonVariant.Standard, onClick, modifier, enabled, style, interactionSource, content)
}

@Composable
public fun FilledIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: IconButtonStyle? = null,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  IconButtonImpl(IconButtonVariant.Filled, onClick, modifier, enabled, style, interactionSource, content)
}

@Composable
public fun TonalIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: IconButtonStyle? = null,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  IconButtonImpl(IconButtonVariant.Tonal, onClick, modifier, enabled, style, interactionSource, content)
}

@Composable
public fun OutlinedIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: IconButtonStyle? = null,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  IconButtonImpl(IconButtonVariant.Outlined, onClick, modifier, enabled, style, interactionSource, content)
}

@Composable
private fun IconButtonImpl(
  variant: IconButtonVariant,
  onClick: () -> Unit,
  modifier: Modifier,
  enabled: Boolean,
  explicitStyle: IconButtonStyle?,
  interactionSource: MutableInteractionSource?,
  content: @Composable () -> Unit,
) {
  val style = IconButtonDefaults.resolve(explicitStyle, variant)
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val containerColor = if (enabled) style.containerColor else style.disabledContainerColor
  val contentColor = if (enabled) style.contentColor else style.disabledContentColor
  val border = if (enabled) style.border else style.border?.let { BorderStroke(it.width, contentColor.copy(alpha = 0.3f)) }
  val pointer = LocalInputCapabilities.current.pointer

  Box(
    modifier = modifier
      .minimumInteractiveSize()
      .size(style.size)
      .pressScale(interactionSource, enabled)
      .focusRing(interactionSource, style.shape)
      .then(if (border != null) Modifier.border(border, style.shape) else Modifier)
      .background(containerColor, style.shape)
      .clip(style.shape)
      .clickable(
        interactionSource = interactionSource,
        indication = LocalIndication.current,
        enabled = enabled,
        role = Role.Button,
        onClick = onClick,
      )
      .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier),
    contentAlignment = Alignment.Center,
  ) {
    CompositionLocalProvider(LocalContentColor provides contentColor, LocalIconSize provides style.iconSize) {
      content()
    }
  }
}
