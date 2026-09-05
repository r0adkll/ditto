package com.r0adkll.ditto.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ElevationLevel

/** The four button emphases. Each idiom renders them in its own way (ADR-023). */
public enum class ButtonVariant {
  Filled,
  Tonal,
  Outlined,
  Text,
}

/**
 * Everything a button needs to draw itself. Idioms produce defaults via [ButtonDefaults.style];
 * apps override per call (`style =`) or per subtree ([LocalButtonStyles]) (ADR-007, ADR-014).
 */
@Immutable
public class ButtonStyle(
  public val containerColor: Color,
  public val contentColor: Color,
  public val disabledContainerColor: Color,
  public val disabledContentColor: Color,
  public val shape: Shape,
  public val border: BorderStroke?,
  public val minHeight: Dp,
  public val minWidth: Dp,
  public val contentPadding: PaddingValues,
  public val textStyle: TextStyle,
  public val elevation: ElevationLevel,
  public val iconSize: Dp,
  public val iconSpacing: Dp,
) {
  public fun copy(
    containerColor: Color = this.containerColor,
    contentColor: Color = this.contentColor,
    disabledContainerColor: Color = this.disabledContainerColor,
    disabledContentColor: Color = this.disabledContentColor,
    shape: Shape = this.shape,
    border: BorderStroke? = this.border,
    minHeight: Dp = this.minHeight,
    minWidth: Dp = this.minWidth,
    contentPadding: PaddingValues = this.contentPadding,
    textStyle: TextStyle = this.textStyle,
    elevation: ElevationLevel = this.elevation,
    iconSize: Dp = this.iconSize,
    iconSpacing: Dp = this.iconSpacing,
  ): ButtonStyle = ButtonStyle(
    containerColor, contentColor, disabledContainerColor, disabledContentColor, shape, border,
    minHeight, minWidth, contentPadding, textStyle, elevation, iconSize, iconSpacing,
  )

  override fun equals(other: Any?): Boolean = other is ButtonStyle &&
    containerColor == other.containerColor && contentColor == other.contentColor &&
    disabledContainerColor == other.disabledContainerColor && disabledContentColor == other.disabledContentColor &&
    shape == other.shape && border == other.border && minHeight == other.minHeight && minWidth == other.minWidth &&
    contentPadding == other.contentPadding && textStyle == other.textStyle && elevation == other.elevation &&
    iconSize == other.iconSize && iconSpacing == other.iconSpacing

  override fun hashCode(): Int = listOf(
    containerColor, contentColor, disabledContainerColor, disabledContentColor, shape, border, minHeight,
    minWidth, contentPadding, textStyle, elevation, iconSize, iconSpacing,
  ).hashCode()

  override fun toString(): String = "ButtonStyle(containerColor=$containerColor, shape=$shape)"
}

/** Per-variant overrides for a subtree. `null` entries fall back to the idiom default. */
@Immutable
public data class ButtonStyles(
  val filled: ButtonStyle? = null,
  val tonal: ButtonStyle? = null,
  val outlined: ButtonStyle? = null,
  val text: ButtonStyle? = null,
) {
  public operator fun get(variant: ButtonVariant): ButtonStyle? = when (variant) {
    ButtonVariant.Filled -> filled
    ButtonVariant.Tonal -> tonal
    ButtonVariant.Outlined -> outlined
    ButtonVariant.Text -> text
  }
}

public val LocalButtonStyles: ProvidableCompositionLocal<ButtonStyles> = staticCompositionLocalOf { ButtonStyles() }

public object ButtonDefaults {
  /** Alpha used for tonal containers: accent over surface (ADR-022, no second color). */
  public const val TonalContainerAlpha: Float = 0.12f

  /** The idiom default for [variant], before any [LocalButtonStyles] override. */
  @Composable
  @ReadOnlyComposable
  public fun style(variant: ButtonVariant, idiom: Idiom = DittoTheme.idiom): ButtonStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val type = DittoTheme.typography
    val dimens = DittoTheme.dimens
    val spacing = DittoTheme.spacing
    val disabledContainer = colors.onSurface.copy(alpha = 0.12f)
    val disabledContent = colors.onSurface.copy(alpha = colors.disabledAlpha)
    val tonalContainer = colors.accent.copy(alpha = TonalContainerAlpha)

    return when (idiom) {
      Idiom.Android -> {
        val base = ButtonStyle(
          containerColor = colors.accent,
          contentColor = colors.onAccent,
          disabledContainerColor = disabledContainer,
          disabledContentColor = disabledContent,
          shape = shapes.full,
          border = null,
          minHeight = 40.dp,
          minWidth = 58.dp,
          contentPadding = PaddingValues(horizontal = spacing.xl, vertical = spacing.sm),
          textStyle = type.label,
          elevation = ElevationLevel.Level0,
          iconSize = 18.dp,
          iconSpacing = spacing.sm,
        )
        when (variant) {
          ButtonVariant.Filled -> base
          ButtonVariant.Tonal -> base.copy(containerColor = tonalContainer, contentColor = colors.accent)
          ButtonVariant.Outlined -> base.copy(
            containerColor = Color.Transparent,
            contentColor = colors.accent,
            border = BorderStroke(dimens.borderWidth, colors.outline),
            disabledContainerColor = Color.Transparent,
          )
          ButtonVariant.Text -> base.copy(
            containerColor = Color.Transparent,
            contentColor = colors.accent,
            disabledContainerColor = Color.Transparent,
            contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
          )
        }
      }
      Idiom.Apple -> {
        val base = ButtonStyle(
          containerColor = colors.accent,
          contentColor = colors.onAccent,
          disabledContainerColor = disabledContainer,
          disabledContentColor = disabledContent,
          shape = shapes.medium,
          border = null,
          minHeight = 44.dp,
          minWidth = 44.dp,
          contentPadding = PaddingValues(horizontal = spacing.xl, vertical = spacing.md),
          textStyle = type.label,
          elevation = ElevationLevel.Level0,
          iconSize = 20.dp,
          iconSpacing = spacing.sm,
        )
        when (variant) {
          ButtonVariant.Filled -> base
          ButtonVariant.Tonal -> base.copy(containerColor = colors.accent.copy(alpha = 0.15f), contentColor = colors.accent)
          ButtonVariant.Outlined -> base.copy(
            containerColor = Color.Transparent,
            contentColor = colors.accent,
            border = BorderStroke(1.dp, colors.accent),
            disabledContainerColor = Color.Transparent,
          )
          ButtonVariant.Text -> base.copy(
            containerColor = Color.Transparent,
            contentColor = colors.accent,
            disabledContainerColor = Color.Transparent,
            contentPadding = PaddingValues(horizontal = spacing.sm, vertical = spacing.sm),
            minWidth = 0.dp,
          )
        }
      }
      Idiom.Desktop -> {
        val base = ButtonStyle(
          containerColor = colors.accent,
          contentColor = colors.onAccent,
          disabledContainerColor = disabledContainer,
          disabledContentColor = disabledContent,
          shape = shapes.small,
          border = null,
          minHeight = 36.dp,
          minWidth = 64.dp,
          contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
          textStyle = type.label,
          elevation = ElevationLevel.Level0,
          iconSize = 16.dp,
          iconSpacing = spacing.sm,
        )
        when (variant) {
          ButtonVariant.Filled -> base
          ButtonVariant.Tonal -> base.copy(containerColor = tonalContainer, contentColor = colors.accent)
          ButtonVariant.Outlined -> base.copy(
            containerColor = colors.surface,
            contentColor = colors.onSurface,
            border = BorderStroke(dimens.borderWidth, colors.outlineVariant),
            disabledContainerColor = Color.Transparent,
          )
          ButtonVariant.Text -> base.copy(
            containerColor = Color.Transparent,
            contentColor = colors.onSurface,
            disabledContainerColor = Color.Transparent,
            contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
          )
        }
      }
    }
  }

  /** Resolution order: explicit > [LocalButtonStyles] > idiom default (ADR-014). */
  @Composable
  @ReadOnlyComposable
  internal fun resolve(explicit: ButtonStyle?, variant: ButtonVariant): ButtonStyle =
    explicit ?: LocalButtonStyles.current[variant] ?: style(variant)
}
