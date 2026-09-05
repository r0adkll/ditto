package com.r0adkll.ditto.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.focusGroup
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.LocalContentColor
import com.r0adkll.ditto.foundation.ProvideTextStyle
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ElevationLevel

@Immutable
public class MenuStyle(
  public val shape: Shape,
  public val containerColor: Color,
  public val elevation: ElevationLevel,
  public val border: BorderStroke?,
  public val minWidth: Dp,
  public val maxWidth: Dp,
  public val contentPadding: PaddingValues,
  public val itemMinHeight: Dp,
  public val itemPadding: PaddingValues,
  public val itemShape: Shape,
  public val itemTextStyle: TextStyle,
  public val itemContentColor: Color,
  public val itemIconColor: Color,
  public val itemIconSpacing: Dp,
  /** Apple-style hairlines between items. */
  public val separators: Boolean,
) {
  public fun copy(
    shape: Shape = this.shape,
    containerColor: Color = this.containerColor,
    elevation: ElevationLevel = this.elevation,
    border: BorderStroke? = this.border,
    minWidth: Dp = this.minWidth,
    maxWidth: Dp = this.maxWidth,
    contentPadding: PaddingValues = this.contentPadding,
    itemMinHeight: Dp = this.itemMinHeight,
    itemPadding: PaddingValues = this.itemPadding,
    itemShape: Shape = this.itemShape,
    itemTextStyle: TextStyle = this.itemTextStyle,
    itemContentColor: Color = this.itemContentColor,
    itemIconColor: Color = this.itemIconColor,
    itemIconSpacing: Dp = this.itemIconSpacing,
    separators: Boolean = this.separators,
  ): MenuStyle = MenuStyle(
    shape, containerColor, elevation, border, minWidth, maxWidth, contentPadding, itemMinHeight, itemPadding, itemShape,
    itemTextStyle, itemContentColor, itemIconColor, itemIconSpacing, separators,
  )

  override fun equals(other: Any?): Boolean = other is MenuStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "MenuStyle(shape=$shape)"
  private fun fields(): List<Any?> = listOf(
    shape, containerColor, elevation, border, minWidth, maxWidth, contentPadding, itemMinHeight, itemPadding, itemShape,
    itemTextStyle, itemContentColor, itemIconColor, itemIconSpacing, separators,
  )
}

public val LocalMenuStyle: ProvidableCompositionLocal<MenuStyle?> = staticCompositionLocalOf { null }

public object MenuDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): MenuStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val type = DittoTheme.typography
    val spacing = DittoTheme.spacing
    val dimens = DittoTheme.dimens
    return when (idiom) {
      Idiom.Android -> MenuStyle(
        shape = shapes.extraSmall,
        containerColor = colors.surfaceOverlay,
        elevation = ElevationLevel.Level2,
        border = null,
        minWidth = 112.dp,
        maxWidth = 280.dp,
        contentPadding = PaddingValues(vertical = spacing.sm),
        itemMinHeight = 48.dp,
        itemPadding = PaddingValues(horizontal = spacing.md),
        itemShape = shapes.none,
        itemTextStyle = type.body,
        itemContentColor = colors.onSurface,
        itemIconColor = colors.onSurfaceVariant,
        itemIconSpacing = spacing.md,
        separators = false,
      )
      Idiom.Apple -> MenuStyle(
        shape = shapes.medium,
        containerColor = colors.surfaceRaised,
        elevation = ElevationLevel.Level3,
        border = null,
        minWidth = 250.dp,
        maxWidth = 250.dp,
        contentPadding = PaddingValues(0.dp),
        itemMinHeight = 44.dp,
        itemPadding = PaddingValues(horizontal = spacing.lg),
        itemShape = shapes.none,
        itemTextStyle = type.body,
        itemContentColor = colors.onSurface,
        itemIconColor = colors.onSurface,
        itemIconSpacing = spacing.md,
        separators = true,
      )
      Idiom.Desktop -> MenuStyle(
        shape = shapes.small,
        containerColor = colors.surface,
        elevation = ElevationLevel.Level2,
        border = BorderStroke(dimens.borderWidth, colors.outlineVariant),
        minWidth = 160.dp,
        maxWidth = 320.dp,
        contentPadding = PaddingValues(spacing.xs),
        itemMinHeight = 32.dp,
        itemPadding = PaddingValues(horizontal = spacing.sm),
        itemShape = shapes.extraSmall,
        itemTextStyle = type.bodySmall,
        itemContentColor = colors.onSurface,
        itemIconColor = colors.onSurfaceVariant,
        itemIconSpacing = spacing.sm,
        separators = false,
      )
    }
  }

  @Composable
  @ReadOnlyComposable
  internal fun resolve(explicit: MenuStyle?): MenuStyle = explicit ?: LocalMenuStyle.current ?: style()
}

/**
 * A popup menu anchored to the composable it is placed in. Dismisses on outside tap, back, or
 * Escape. [content] is a column of [MenuItem]s (and optionally [MenuDivider]s).
 */
@Composable
public fun DropdownMenu(
  expanded: Boolean,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  offset: DpOffset = DpOffset.Zero,
  style: MenuStyle? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  if (!expanded) return
  @Suppress("NAME_SHADOWING")
  val style = MenuDefaults.resolve(style)
  val density = LocalDensity.current
  val provider = remember(offset, density) {
    with(density) { BelowAnchor(offset.x.roundToPx(), offset.y.roundToPx(), 4.dp.roundToPx()) }
  }
  Popup(
    popupPositionProvider = provider,
    onDismissRequest = onDismissRequest,
    properties = PopupProperties(focusable = true),
  ) {
    MenuContent(modifier, style, autoFocus = true, content = content)
  }
}

/**
 * The menu surface without the popup, for previews and custom hosts. Arrow keys move between
 * items; the first item takes focus when a keyboard is present so the menu is operable without a
 * pointer (ADR-021).
 */
@Composable
public fun MenuContent(
  modifier: Modifier = Modifier,
  style: MenuStyle? = null,
  autoFocus: Boolean = false,
  content: @Composable ColumnScope.() -> Unit,
) {
  @Suppress("NAME_SHADOWING")
  val style = MenuDefaults.resolve(style)
  val focusManager = LocalFocusManager.current
  val firstItem = remember { MenuFocus(FocusRequester()) }
  val keyboard = LocalInputCapabilities.current.keyboard
  LaunchedEffect(keyboard, autoFocus) {
    if (autoFocus && keyboard && firstItem.claimed) runCatching { firstItem.requester.requestFocus() }
  }
  CompositionLocalProvider(LocalMenuStyle provides style, LocalMenuFocus provides firstItem) {
    Surface(
      modifier = modifier
        .widthIn(min = style.minWidth, max = style.maxWidth)
        .width(IntrinsicSize.Max)
        .onPreviewKeyEvent { event ->
          if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
          when (event.key) {
            Key.DirectionDown -> focusManager.moveFocus(FocusDirection.Down)
            Key.DirectionUp -> focusManager.moveFocus(FocusDirection.Up)
            else -> false
          }
        },
      shape = style.shape,
      color = style.containerColor,
      elevation = style.elevation,
      border = style.border,
    ) {
      Column(Modifier.padding(style.contentPadding).focusGroup(), content = content)
    }
  }
}

internal class MenuFocus(val requester: FocusRequester) {
  var claimed: Boolean = false
}

internal val LocalMenuFocus: ProvidableCompositionLocal<MenuFocus?> = staticCompositionLocalOf { null }

/** One row of a [DropdownMenu]. */
@Composable
public fun MenuItem(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  leadingIcon: (@Composable () -> Unit)? = null,
  trailingIcon: (@Composable () -> Unit)? = null,
  destructive: Boolean = false,
  interactionSource: MutableInteractionSource? = null,
) {
  val style = MenuDefaults.resolve(null)
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val pointer = LocalInputCapabilities.current.pointer
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val contentColor = (if (destructive) DittoTheme.colors.error else style.itemContentColor).copy(alpha = alpha)
  val iconColor = (if (destructive) DittoTheme.colors.error else style.itemIconColor).copy(alpha = alpha)
  val menuFocus = LocalMenuFocus.current
  val isFirst = remember(menuFocus) { menuFocus != null && !menuFocus.claimed && enabled.also { if (it) menuFocus.claimed = true } }

  Row(
    modifier
      .fillMaxWidth()
      .then(if (isFirst && menuFocus != null) Modifier.focusRequester(menuFocus.requester) else Modifier)
      .focusRing(interactionSource, style.itemShape)
      .clip(style.itemShape)
      .clickable(interactionSource, LocalIndication.current, enabled = enabled, role = Role.Button, onClick = onClick)
      .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
      .defaultMinSize(minHeight = style.itemMinHeight)
      .padding(style.itemPadding),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (leadingIcon != null) {
      CompositionLocalProvider(LocalContentColor provides iconColor) { Box { leadingIcon() } }
      Spacer(Modifier.width(style.itemIconSpacing))
    }
    Text(text, style = style.itemTextStyle, color = contentColor, modifier = Modifier.weight(1f))
    if (trailingIcon != null) {
      Spacer(Modifier.width(style.itemIconSpacing))
      CompositionLocalProvider(LocalContentColor provides iconColor) {
        ProvideTextStyle(style.itemTextStyle) { Box { trailingIcon() } }
      }
    }
  }
  if (style.separators) HorizontalDivider()
}

/** A separator between menu groups; no-op visual difference on idioms that already draw separators. */
@Composable
public fun MenuDivider(modifier: Modifier = Modifier) {
  val style = MenuDefaults.resolve(null)
  if (style.separators) return
  HorizontalDivider(modifier.padding(vertical = DittoTheme.spacing.xs))
}

private class BelowAnchor(private val dx: Int, private val dy: Int, private val gap: Int) : PopupPositionProvider {
  override fun calculatePosition(
    anchorBounds: IntRect,
    windowSize: IntSize,
    layoutDirection: LayoutDirection,
    popupContentSize: IntSize,
  ): IntOffset {
    val preferredX = if (layoutDirection == LayoutDirection.Ltr) anchorBounds.left + dx else anchorBounds.right - popupContentSize.width - dx
    val x = preferredX.coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0))
    val below = anchorBounds.bottom + gap + dy
    val y = if (below + popupContentSize.height <= windowSize.height) below else (anchorBounds.top - gap - popupContentSize.height - dy).coerceAtLeast(0)
    return IntOffset(x, y)
  }
}
