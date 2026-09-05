package com.r0adkll.ditto.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.LocalContentColor
import com.r0adkll.ditto.foundation.ProvideTextStyle
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.theme.DittoTheme

@Immutable
public class ListItemStyle(
  public val minHeight: Dp,
  public val minHeightTwoLine: Dp,
  public val contentPadding: PaddingValues,
  public val shape: Shape,
  public val containerColor: Color,
  public val headlineStyle: TextStyle,
  public val headlineColor: Color,
  public val supportingStyle: TextStyle,
  public val supportingColor: Color,
  public val leadingColor: Color,
  public val trailingColor: Color,
  public val leadingSpacing: Dp,
  public val trailingSpacing: Dp,
  /** Apple-style disclosure chevron appended to clickable rows that have no trailing slot. */
  public val disclosureIndicator: Boolean,
) {
  public fun copy(
    minHeight: Dp = this.minHeight,
    minHeightTwoLine: Dp = this.minHeightTwoLine,
    contentPadding: PaddingValues = this.contentPadding,
    shape: Shape = this.shape,
    containerColor: Color = this.containerColor,
    headlineStyle: TextStyle = this.headlineStyle,
    headlineColor: Color = this.headlineColor,
    supportingStyle: TextStyle = this.supportingStyle,
    supportingColor: Color = this.supportingColor,
    leadingColor: Color = this.leadingColor,
    trailingColor: Color = this.trailingColor,
    leadingSpacing: Dp = this.leadingSpacing,
    trailingSpacing: Dp = this.trailingSpacing,
    disclosureIndicator: Boolean = this.disclosureIndicator,
  ): ListItemStyle = ListItemStyle(
    minHeight, minHeightTwoLine, contentPadding, shape, containerColor, headlineStyle, headlineColor, supportingStyle,
    supportingColor, leadingColor, trailingColor, leadingSpacing, trailingSpacing, disclosureIndicator,
  )

  override fun equals(other: Any?): Boolean = other is ListItemStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "ListItemStyle(minHeight=$minHeight)"

  private fun fields(): List<Any?> = listOf(
    minHeight, minHeightTwoLine, contentPadding, shape, containerColor, headlineStyle, headlineColor, supportingStyle,
    supportingColor, leadingColor, trailingColor, leadingSpacing, trailingSpacing, disclosureIndicator,
  )
}

public val LocalListItemStyle: ProvidableCompositionLocal<ListItemStyle?> = staticCompositionLocalOf { null }

public object ListItemDefaults {
  /**
   * Android: 56/72dp rows, 16dp padding. Apple: 44dp rows, disclosure chevron on tappable rows,
   * transparent so a grouped `Card` supplies the cell background. Desktop: 40dp, rounded hover.
   */
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): ListItemStyle {
    val colors = DittoTheme.colors
    val type = DittoTheme.typography
    val spacing = DittoTheme.spacing
    val shapes = DittoTheme.shapes
    return when (idiom) {
      Idiom.Android -> ListItemStyle(
        minHeight = 56.dp,
        minHeightTwoLine = 72.dp,
        contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
        shape = shapes.none,
        containerColor = Color.Transparent,
        headlineStyle = type.body,
        headlineColor = colors.onSurface,
        supportingStyle = type.bodySmall,
        supportingColor = colors.onSurfaceVariant,
        leadingColor = colors.onSurfaceVariant,
        trailingColor = colors.onSurfaceVariant,
        leadingSpacing = spacing.lg,
        trailingSpacing = spacing.lg,
        disclosureIndicator = false,
      )
      Idiom.Apple -> ListItemStyle(
        minHeight = 44.dp,
        minHeightTwoLine = 60.dp,
        contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
        shape = shapes.none,
        containerColor = Color.Transparent,
        headlineStyle = type.body,
        headlineColor = colors.onSurface,
        supportingStyle = type.bodySmall,
        supportingColor = colors.onSurfaceVariant,
        leadingColor = colors.accent,
        trailingColor = colors.neutrals[8],
        leadingSpacing = spacing.md,
        trailingSpacing = spacing.sm,
        disclosureIndicator = true,
      )
      Idiom.Desktop -> ListItemStyle(
        minHeight = 40.dp,
        minHeightTwoLine = 56.dp,
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.xs),
        shape = shapes.small,
        containerColor = Color.Transparent,
        headlineStyle = type.body,
        headlineColor = colors.onSurface,
        supportingStyle = type.bodySmall,
        supportingColor = colors.onSurfaceVariant,
        leadingColor = colors.onSurfaceVariant,
        trailingColor = colors.onSurfaceVariant,
        leadingSpacing = spacing.md,
        trailingSpacing = spacing.md,
        disclosureIndicator = false,
      )
    }
  }
}

/**
 * One row of a list: [headline] with optional [supporting] text and [leading] / [trailing] slots.
 * Pass [onClick] for a tappable row; the Apple idiom then appends a disclosure chevron unless a
 * [trailing] slot is given. Separators are the container's job ([HorizontalDivider]).
 */
@Composable
public fun ListItem(
  headline: String,
  modifier: Modifier = Modifier,
  supporting: String? = null,
  leading: (@Composable () -> Unit)? = null,
  trailing: (@Composable () -> Unit)? = null,
  onClick: (() -> Unit)? = null,
  enabled: Boolean = true,
  style: ListItemStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalListItemStyle.current ?: ListItemDefaults.style()
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val pointer = LocalInputCapabilities.current.pointer
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val minHeight = if (supporting != null) style.minHeightTwoLine else style.minHeight
  val showDisclosure = onClick != null && trailing == null && style.disclosureIndicator

  Row(
    modifier
      .fillMaxWidth()
      .focusRing(interactionSource, style.shape)
      .clip(style.shape)
      .background(style.containerColor)
      .then(
        if (onClick != null) {
          Modifier
            .clickable(interactionSource, LocalIndication.current, enabled = enabled, onClick = onClick)
            .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
        } else {
          Modifier
        },
      )
      .defaultMinSize(minHeight = minHeight)
      .padding(style.contentPadding),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (leading != null) {
      CompositionLocalProvider(LocalContentColor provides style.leadingColor.copy(alpha = alpha)) {
        Box(contentAlignment = Alignment.Center) { leading() }
      }
      Spacer(Modifier.width(style.leadingSpacing))
    }
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
      Text(headline, style = style.headlineStyle, color = style.headlineColor.copy(alpha = alpha))
      if (supporting != null) {
        Text(supporting, style = style.supportingStyle, color = style.supportingColor.copy(alpha = alpha))
      }
    }
    if (trailing != null || showDisclosure) {
      Spacer(Modifier.width(style.trailingSpacing))
      CompositionLocalProvider(LocalContentColor provides style.trailingColor.copy(alpha = alpha)) {
        ProvideTextStyle(style.supportingStyle) {
          if (trailing != null) {
            Box(contentAlignment = Alignment.Center) { trailing() }
          } else {
            Icon(DittoIcons.chevronRight, contentDescription = null, size = 18.dp)
          }
        }
      }
    }
  }
}
