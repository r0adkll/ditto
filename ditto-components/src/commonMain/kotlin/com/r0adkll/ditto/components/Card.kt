package com.r0adkll.ditto.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.interaction.pressScale
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ElevationLevel

public enum class CardVariant {
  /** Lifted from the background; how depends on the idiom's elevation rendering (ADR-022). */
  Elevated,

  /** A tonal step up, no shadow. */
  Filled,

  /** Border only. */
  Outlined,
}

@Immutable
public class CardStyle(
  public val shape: Shape,
  public val containerColor: Color,
  public val contentColor: Color,
  public val elevation: ElevationLevel,
  public val border: BorderStroke?,
) {
  public fun copy(
    shape: Shape = this.shape,
    containerColor: Color = this.containerColor,
    contentColor: Color = this.contentColor,
    elevation: ElevationLevel = this.elevation,
    border: BorderStroke? = this.border,
  ): CardStyle = CardStyle(shape, containerColor, contentColor, elevation, border)

  override fun equals(other: Any?): Boolean = other is CardStyle &&
    shape == other.shape && containerColor == other.containerColor && contentColor == other.contentColor &&
    elevation == other.elevation && border == other.border

  override fun hashCode(): Int = listOf(shape, containerColor, contentColor, elevation, border).hashCode()
  override fun toString(): String = "CardStyle(shape=$shape, elevation=$elevation)"
}

@Immutable
public data class CardStyles(
  val elevated: CardStyle? = null,
  val filled: CardStyle? = null,
  val outlined: CardStyle? = null,
) {
  public operator fun get(variant: CardVariant): CardStyle? = when (variant) {
    CardVariant.Elevated -> elevated
    CardVariant.Filled -> filled
    CardVariant.Outlined -> outlined
  }
}

public val LocalCardStyles: ProvidableCompositionLocal<CardStyles> = staticCompositionLocalOf { CardStyles() }

public object CardDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(variant: CardVariant, idiom: Idiom = DittoTheme.idiom): CardStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val dimens = DittoTheme.dimens
    val shape = when (idiom) {
      Idiom.Android -> shapes.medium
      Idiom.Apple -> shapes.medium
      Idiom.Desktop -> shapes.large
    }
    return when (variant) {
      CardVariant.Elevated -> CardStyle(
        shape = shape,
        containerColor = colors.surface,
        contentColor = colors.onSurface,
        elevation = ElevationLevel.Level1,
        border = null,
      )
      CardVariant.Filled -> CardStyle(
        shape = shape,
        containerColor = colors.surfaceOverlay,
        contentColor = colors.onSurface,
        elevation = ElevationLevel.Level0,
        border = null,
      )
      CardVariant.Outlined -> CardStyle(
        shape = shape,
        containerColor = colors.surface,
        contentColor = colors.onSurface,
        elevation = ElevationLevel.Level0,
        border = BorderStroke(dimens.borderWidth, colors.outlineVariant),
      )
    }
  }

  @Composable
  @ReadOnlyComposable
  internal fun resolve(explicit: CardStyle?, variant: CardVariant): CardStyle =
    explicit ?: LocalCardStyles.current[variant] ?: style(variant)
}

/** A grouped surface. Pass [onClick] to make the whole card interactive. */
@Composable
public fun Card(
  modifier: Modifier = Modifier,
  variant: CardVariant = CardVariant.Elevated,
  onClick: (() -> Unit)? = null,
  enabled: Boolean = true,
  style: CardStyle? = null,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  @Suppress("NAME_SHADOWING")
  val style = CardDefaults.resolve(style, variant)
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val pointer = LocalInputCapabilities.current.pointer
  val interactive = onClick != null
  Surface(
    modifier = modifier
      .then(if (interactive) Modifier.pressScale(interactionSource, enabled).focusRing(interactionSource, style.shape) else Modifier),
    shape = style.shape,
    color = style.containerColor,
    contentColor = style.contentColor,
    elevation = style.elevation,
    border = style.border,
  ) {
    Column(
      if (interactive) {
        Modifier
          .clickable(interactionSource, LocalIndication.current, enabled = enabled, onClick = onClick!!)
          .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
      } else {
        Modifier
      },
      content = content,
    )
  }
}
