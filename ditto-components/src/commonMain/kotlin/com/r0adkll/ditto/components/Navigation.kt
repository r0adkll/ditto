package com.r0adkll.ditto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ElevationLevel

/** Shared look of one destination in a bar, rail or sidebar. */
@Immutable
public class NavigationItemStyle(
  public val indicatorColor: Color,
  public val indicatorShape: Shape,
  /** Indicator sits behind the icon only (Android pill) rather than the whole item. */
  public val indicatorBehindIcon: Boolean,
  public val indicatorWidth: Dp,
  public val indicatorHeight: Dp,
  public val selectedIconColor: Color,
  public val iconColor: Color,
  public val selectedLabelColor: Color,
  public val labelColor: Color,
  public val labelStyle: TextStyle,
  public val iconSize: Dp,
  public val itemShape: Shape,
) {
  public fun copy(
    indicatorColor: Color = this.indicatorColor,
    indicatorShape: Shape = this.indicatorShape,
    indicatorBehindIcon: Boolean = this.indicatorBehindIcon,
    indicatorWidth: Dp = this.indicatorWidth,
    indicatorHeight: Dp = this.indicatorHeight,
    selectedIconColor: Color = this.selectedIconColor,
    iconColor: Color = this.iconColor,
    selectedLabelColor: Color = this.selectedLabelColor,
    labelColor: Color = this.labelColor,
    labelStyle: TextStyle = this.labelStyle,
    iconSize: Dp = this.iconSize,
    itemShape: Shape = this.itemShape,
  ): NavigationItemStyle = NavigationItemStyle(
    indicatorColor, indicatorShape, indicatorBehindIcon, indicatorWidth, indicatorHeight, selectedIconColor, iconColor,
    selectedLabelColor, labelColor, labelStyle, iconSize, itemShape,
  )

  override fun equals(other: Any?): Boolean = other is NavigationItemStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "NavigationItemStyle(indicatorColor=$indicatorColor)"
  private fun fields(): List<Any?> = listOf(
    indicatorColor, indicatorShape, indicatorBehindIcon, indicatorWidth, indicatorHeight, selectedIconColor, iconColor,
    selectedLabelColor, labelColor, labelStyle, iconSize, itemShape,
  )
}

@Immutable
public class NavigationContainerStyle(
  public val containerColor: Color,
  public val contentColor: Color,
  public val size: Dp,
  public val elevation: ElevationLevel,
  public val hairline: Boolean,
  public val itemStyle: NavigationItemStyle,
  public val padding: Dp,
) {
  public fun copy(
    containerColor: Color = this.containerColor,
    contentColor: Color = this.contentColor,
    size: Dp = this.size,
    elevation: ElevationLevel = this.elevation,
    hairline: Boolean = this.hairline,
    itemStyle: NavigationItemStyle = this.itemStyle,
    padding: Dp = this.padding,
  ): NavigationContainerStyle = NavigationContainerStyle(containerColor, contentColor, size, elevation, hairline, itemStyle, padding)

  override fun equals(other: Any?): Boolean = other is NavigationContainerStyle &&
    containerColor == other.containerColor && contentColor == other.contentColor && size == other.size &&
    elevation == other.elevation && hairline == other.hairline && itemStyle == other.itemStyle && padding == other.padding

  override fun hashCode(): Int = listOf(containerColor, contentColor, size, elevation, hairline, itemStyle, padding).hashCode()
  override fun toString(): String = "NavigationContainerStyle(size=$size)"
}

public val LocalNavigationBarStyle: ProvidableCompositionLocal<NavigationContainerStyle?> = staticCompositionLocalOf { null }
public val LocalNavigationRailStyle: ProvidableCompositionLocal<NavigationContainerStyle?> = staticCompositionLocalOf { null }
public val LocalSidebarStyle: ProvidableCompositionLocal<NavigationContainerStyle?> = staticCompositionLocalOf { null }
internal val LocalNavigationItemStyle: ProvidableCompositionLocal<NavigationItemStyle?> = staticCompositionLocalOf { null }

public object NavigationDefaults {
  @Composable
  @ReadOnlyComposable
  private fun itemStyle(idiom: Idiom, wholeItem: Boolean): NavigationItemStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val type = DittoTheme.typography
    return when (idiom) {
      Idiom.Android -> NavigationItemStyle(
        indicatorColor = colors.accent.copy(alpha = ButtonDefaults.TonalContainerAlpha),
        indicatorShape = shapes.full,
        indicatorBehindIcon = !wholeItem,
        indicatorWidth = 64.dp,
        indicatorHeight = 32.dp,
        selectedIconColor = colors.accent,
        iconColor = colors.onSurfaceVariant,
        selectedLabelColor = colors.onSurface,
        labelColor = colors.onSurfaceVariant,
        labelStyle = type.caption.copy(fontWeight = FontWeight.Medium),
        iconSize = 24.dp,
        itemShape = if (wholeItem) shapes.full else shapes.none,
      )
      Idiom.Apple -> NavigationItemStyle(
        indicatorColor = colors.accent.copy(alpha = 0.12f),
        indicatorShape = shapes.small,
        indicatorBehindIcon = false,
        indicatorWidth = 0.dp,
        indicatorHeight = 0.dp,
        selectedIconColor = colors.accent,
        iconColor = colors.neutrals[9],
        selectedLabelColor = colors.accent,
        labelColor = colors.neutrals[9],
        labelStyle = type.caption.copy(fontSize = 10.sp),
        iconSize = 26.dp,
        itemShape = if (wholeItem) shapes.small else shapes.none,
      )
      Idiom.Desktop -> NavigationItemStyle(
        indicatorColor = colors.accent.copy(alpha = ButtonDefaults.TonalContainerAlpha),
        indicatorShape = shapes.small,
        indicatorBehindIcon = false,
        indicatorWidth = 0.dp,
        indicatorHeight = 0.dp,
        selectedIconColor = colors.accent,
        iconColor = colors.onSurfaceVariant,
        selectedLabelColor = colors.onSurface,
        labelColor = colors.onSurfaceVariant,
        labelStyle = type.bodySmall,
        iconSize = 18.dp,
        itemShape = shapes.small,
      )
    }
  }

  /** Bottom bar: Android 80dp with pills, Apple 49dp tab bar with hairline, Desktop 56dp. */
  @Composable
  @ReadOnlyComposable
  public fun barStyle(idiom: Idiom = DittoTheme.idiom): NavigationContainerStyle {
    val colors = DittoTheme.colors
    return when (idiom) {
      Idiom.Android -> NavigationContainerStyle(colors.surfaceOverlay, colors.onSurface, 80.dp, ElevationLevel.Level0, false, itemStyle(idiom, false), 0.dp)
      Idiom.Apple -> NavigationContainerStyle(colors.surfaceRaised, colors.onSurface, 49.dp, ElevationLevel.Level0, true, itemStyle(idiom, false), 0.dp)
      Idiom.Desktop -> NavigationContainerStyle(colors.surface, colors.onSurface, 56.dp, ElevationLevel.Level0, true, itemStyle(idiom, true), 4.dp)
    }
  }

  /** Vertical rail for medium widths. */
  @Composable
  @ReadOnlyComposable
  public fun railStyle(idiom: Idiom = DittoTheme.idiom): NavigationContainerStyle {
    val colors = DittoTheme.colors
    return when (idiom) {
      Idiom.Android -> NavigationContainerStyle(colors.surface, colors.onSurface, 80.dp, ElevationLevel.Level0, false, itemStyle(idiom, false).copy(indicatorWidth = 56.dp), 0.dp)
      Idiom.Apple -> NavigationContainerStyle(colors.surfaceRaised, colors.onSurface, 72.dp, ElevationLevel.Level0, true, itemStyle(idiom, true).copy(labelStyle = DittoTheme.typography.caption), 8.dp)
      Idiom.Desktop -> NavigationContainerStyle(colors.neutrals[if (colors.isDark) 2 else 2], colors.onSurface, 64.dp, ElevationLevel.Level0, true, itemStyle(idiom, true), 8.dp)
    }
  }

  /** Expanded sidebar for wide layouts. */
  @Composable
  @ReadOnlyComposable
  public fun sidebarStyle(idiom: Idiom = DittoTheme.idiom): NavigationContainerStyle {
    val colors = DittoTheme.colors
    val type = DittoTheme.typography
    return when (idiom) {
      Idiom.Android -> NavigationContainerStyle(colors.surfaceRaised, colors.onSurface, 300.dp, ElevationLevel.Level0, false, itemStyle(idiom, true).copy(labelStyle = type.label), 12.dp)
      Idiom.Apple -> NavigationContainerStyle(colors.neutrals[if (colors.isDark) 2 else 2], colors.onSurface, 320.dp, ElevationLevel.Level0, true, itemStyle(idiom, true).copy(labelStyle = type.body, iconColor = colors.accent, selectedLabelColor = colors.onSurface, labelColor = colors.onSurface), 16.dp)
      Idiom.Desktop -> NavigationContainerStyle(colors.neutrals[if (colors.isDark) 2 else 2], colors.onSurface, 240.dp, ElevationLevel.Level0, true, itemStyle(idiom, true), 8.dp)
    }
  }
}

/** Bottom destinations. Place in `Scaffold(bottomBar = ...)`; consumes the bottom insets. */
@Composable
public fun NavigationBar(
  modifier: Modifier = Modifier,
  style: NavigationContainerStyle? = null,
  windowInsets: WindowInsets = BottomBarInsets,
  content: @Composable RowScope.() -> Unit,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalNavigationBarStyle.current ?: NavigationDefaults.barStyle()
  Surface(modifier.fillMaxWidth(), color = style.containerColor, contentColor = style.contentColor, elevation = style.elevation) {
    Column {
      if (style.hairline) HorizontalDivider()
      CompositionLocalProvider(LocalNavigationItemStyle provides style.itemStyle) {
        Row(
          Modifier.fillMaxWidth().windowInsetsPadding(windowInsets).height(style.size).padding(style.padding).selectableGroup(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically,
          content = content,
        )
      }
    }
  }
}

/** Side destinations for medium widths. Consumes the vertical + start insets. */
@Composable
public fun NavigationRail(
  modifier: Modifier = Modifier,
  style: NavigationContainerStyle? = null,
  header: (@Composable () -> Unit)? = null,
  windowInsets: WindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start),
  content: @Composable ColumnScope.() -> Unit,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalNavigationRailStyle.current ?: NavigationDefaults.railStyle()
  Surface(modifier.fillMaxHeight(), color = style.containerColor, contentColor = style.contentColor, elevation = style.elevation) {
    Row {
      CompositionLocalProvider(LocalNavigationItemStyle provides style.itemStyle) {
        Column(
          Modifier.fillMaxHeight().windowInsetsPadding(windowInsets).width(style.size).padding(style.padding).selectableGroup(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.xs),
        ) {
          if (header != null) {
            header()
            Spacer(Modifier.height(DittoTheme.spacing.md))
          }
          content()
        }
      }
      if (style.hairline) VerticalDivider()
    }
  }
}

/** Expanded navigation for wide layouts. Consumes the vertical + start insets. */
@Composable
public fun Sidebar(
  modifier: Modifier = Modifier,
  style: NavigationContainerStyle? = null,
  header: (@Composable () -> Unit)? = null,
  windowInsets: WindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start),
  content: @Composable ColumnScope.() -> Unit,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalSidebarStyle.current ?: NavigationDefaults.sidebarStyle()
  Surface(modifier.fillMaxHeight(), color = style.containerColor, contentColor = style.contentColor, elevation = style.elevation) {
    Row {
      CompositionLocalProvider(LocalNavigationItemStyle provides style.itemStyle.copy(indicatorBehindIcon = false)) {
        Column(
          Modifier.fillMaxHeight().windowInsetsPadding(windowInsets).width(style.size).padding(style.padding).selectableGroup(),
          verticalArrangement = Arrangement.spacedBy(DittoTheme.spacing.xxs),
        ) {
          if (header != null) {
            header()
            Spacer(Modifier.height(DittoTheme.spacing.md))
          }
          content()
        }
      }
      if (style.hairline) VerticalDivider()
    }
  }
}

/** A destination in a [NavigationBar] or [NavigationRail]: icon above an optional label. */
@Composable
public fun NavigationItem(
  selected: Boolean,
  onClick: () -> Unit,
  icon: ImageVector,
  label: String?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  badge: String? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  val style = LocalNavigationItemStyle.current ?: NavigationDefaults.barStyle().itemStyle
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val motion = DittoTheme.motion
  val pointer = LocalInputCapabilities.current.pointer
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val iconColor by animateColorAsState(if (selected) style.selectedIconColor else style.iconColor, tween(motion.durationShort))
  val labelColor by animateColorAsState(if (selected) style.selectedLabelColor else style.labelColor, tween(motion.durationShort))
  val indicator by animateColorAsState(if (selected) style.indicatorColor else Color.Transparent, tween(motion.durationShort))
  val haptics = rememberToggleHaptics()

  Column(
    modifier
      .focusRing(interactionSource, style.itemShape)
      .clip(style.itemShape)
      .then(if (!style.indicatorBehindIcon) Modifier.background(indicator.copy(alpha = indicator.alpha * alpha)) else Modifier)
      .selectable(
        selected = selected,
        interactionSource = interactionSource,
        indication = LocalIndication.current,
        enabled = enabled,
        role = Role.Tab,
        onClick = { if (!selected) { haptics.selected(); onClick() } },
      )
      .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
      .padding(horizontal = DittoTheme.spacing.sm, vertical = DittoTheme.spacing.xs),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Box(
      Modifier
        .then(if (style.indicatorBehindIcon) Modifier.size(style.indicatorWidth, style.indicatorHeight).background(indicator.copy(alpha = indicator.alpha * alpha), style.indicatorShape) else Modifier),
      contentAlignment = Alignment.Center,
    ) {
      BadgedBox(badge = badge) {
        Icon(icon, contentDescription = if (label == null) "" else null, tint = iconColor.copy(alpha = alpha), size = style.iconSize)
      }
    }
    if (label != null) {
      Spacer(Modifier.height(DittoTheme.spacing.xs))
      Text(label, style = style.labelStyle, color = labelColor.copy(alpha = alpha), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
  }
}

/** A row destination in a [Sidebar]: icon beside the label. */
@Composable
public fun SidebarItem(
  selected: Boolean,
  onClick: () -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  enabled: Boolean = true,
  badge: String? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  val style = LocalNavigationItemStyle.current ?: NavigationDefaults.sidebarStyle().itemStyle
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val motion = DittoTheme.motion
  val pointer = LocalInputCapabilities.current.pointer
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val iconColor by animateColorAsState(if (selected) style.selectedIconColor else style.iconColor, tween(motion.durationShort))
  val labelColor by animateColorAsState(if (selected) style.selectedLabelColor else style.labelColor, tween(motion.durationShort))
  val indicator by animateColorAsState(if (selected) style.indicatorColor else Color.Transparent, tween(motion.durationShort))
  val spacing = DittoTheme.spacing
  val height = DittoTheme.dimens.listRowHeight

  Row(
    modifier
      .fillMaxWidth()
      .height(height)
      .focusRing(interactionSource, style.itemShape)
      .clip(style.itemShape)
      .background(indicator.copy(alpha = indicator.alpha * alpha))
      .selectable(
        selected = selected,
        interactionSource = interactionSource,
        indication = LocalIndication.current,
        enabled = enabled,
        role = Role.Tab,
        onClick = onClick,
      )
      .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
      .padding(horizontal = spacing.md),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(spacing.md),
  ) {
    if (icon != null) Icon(icon, contentDescription = null, tint = iconColor.copy(alpha = alpha), size = style.iconSize)
    Text(label, style = style.labelStyle, color = labelColor.copy(alpha = alpha), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
    if (badge != null) Badge(badge)
  }
}
