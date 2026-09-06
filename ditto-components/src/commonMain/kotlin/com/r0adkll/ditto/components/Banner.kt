package com.r0adkll.ditto.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.LocalContentColor
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.theme.DittoTheme

public enum class BannerKind { Info, Success, Warning, Error }

@Immutable
public class BannerStyle(
  public val shape: Shape,
  public val tintAlpha: Float,
  public val border: Boolean,
  /** Left accent stripe (Android/Desktop) instead of a full border. */
  public val stripeWidth: Dp,
  public val padding: Dp,
) {
  public fun copy(
    shape: Shape = this.shape,
    tintAlpha: Float = this.tintAlpha,
    border: Boolean = this.border,
    stripeWidth: Dp = this.stripeWidth,
    padding: Dp = this.padding,
  ): BannerStyle = BannerStyle(shape, tintAlpha, border, stripeWidth, padding)

  override fun equals(other: Any?): Boolean = other is BannerStyle &&
    shape == other.shape && tintAlpha == other.tintAlpha && border == other.border && stripeWidth == other.stripeWidth && padding == other.padding

  override fun hashCode(): Int = listOf(shape, tintAlpha, border, stripeWidth, padding).hashCode()
  override fun toString(): String = "BannerStyle(shape=$shape)"
}

public val LocalBannerStyle: ProvidableCompositionLocal<BannerStyle?> = staticCompositionLocalOf { null }

public object BannerDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): BannerStyle {
    val shapes = DittoTheme.shapes
    val spacing = DittoTheme.spacing
    return when (idiom) {
      Idiom.Android -> BannerStyle(shapes.medium, 0.12f, false, 4.dp, spacing.lg)
      Idiom.Apple -> BannerStyle(shapes.medium, 0.12f, false, 0.dp, spacing.lg)
      Idiom.Desktop -> BannerStyle(shapes.small, 0.10f, true, 0.dp, spacing.md)
    }
  }

  @Composable
  @ReadOnlyComposable
  public fun color(kind: BannerKind): Color = when (kind) {
    BannerKind.Info -> DittoTheme.colors.accent
    BannerKind.Success -> DittoTheme.colors.success
    BannerKind.Warning -> DittoTheme.colors.warning
    BannerKind.Error -> DittoTheme.colors.error
  }
}

/**
 * An inline status message: tinted container, kind-colored icon, optional [title], [actions]
 * row and dismiss glyph. Announced politely to screen readers.
 */
@Composable
public fun Banner(
  text: String,
  modifier: Modifier = Modifier,
  kind: BannerKind = BannerKind.Info,
  title: String? = null,
  icon: ImageVector? = null,
  onDismiss: (() -> Unit)? = null,
  actions: (@Composable () -> Unit)? = null,
  style: BannerStyle? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalBannerStyle.current ?: DittoTheme.styleOverrides.resolve(BannerDefaults.style())
  val tone = BannerDefaults.color(kind)
  val colors = DittoTheme.colors
  val container = tone.copy(alpha = style.tintAlpha).compositeOver(colors.surface)
  val spacing = DittoTheme.spacing
  Row(
    modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Min)
      .clip(style.shape)
      .background(container)
      .then(if (style.border) Modifier.border(BorderStroke(DittoTheme.dimens.borderWidth, tone.copy(alpha = 0.4f)), style.shape) else Modifier)
      .semantics { liveRegion = LiveRegionMode.Polite },
  ) {
    if (style.stripeWidth > 0.dp) Box(Modifier.width(style.stripeWidth).fillMaxHeight().background(tone))
    Row(Modifier.padding(style.padding).weight(1f), verticalAlignment = Alignment.Top) {
      Icon(icon ?: defaultIcon(kind), contentDescription = kind.name, tint = tone)
      Spacer(Modifier.width(spacing.md))
      Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        if (title != null) Text(title, style = DittoTheme.typography.subheading)
        Text(text, style = DittoTheme.typography.bodySmall, color = colors.onSurface)
        if (actions != null) {
          Spacer(Modifier.height(spacing.xs))
          CompositionLocalProvider(LocalContentColor provides tone) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) { actions() }
          }
        }
      }
      if (onDismiss != null) {
        Spacer(Modifier.width(spacing.sm))
        IconButton(onClick = onDismiss, style = IconButtonDefaults.style(IconButtonVariant.Standard).copy(size = 24.dp, iconSize = 16.dp)) {
          Icon(DittoIcons.close, contentDescription = "Dismiss")
        }
      }
    }
  }
}

@Composable
@ReadOnlyComposable
private fun defaultIcon(kind: BannerKind): ImageVector = when (kind) {
  BannerKind.Info -> DittoIcons.more
  BannerKind.Success -> DittoIcons.check
  BannerKind.Warning -> DittoIcons.indeterminate
  BannerKind.Error -> DittoIcons.clear
}
