package com.r0adkll.ditto.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ElevationLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

@Immutable
public class TooltipStyle(
  public val containerColor: Color,
  public val contentColor: Color,
  public val shape: Shape,
  public val padding: PaddingValues,
  public val textStyle: TextStyle,
  /** Gap between the anchor and the tooltip. */
  public val offset: Dp,
  public val maxWidth: Dp,
  public val elevation: ElevationLevel,
) {
  public fun copy(
    containerColor: Color = this.containerColor,
    contentColor: Color = this.contentColor,
    shape: Shape = this.shape,
    padding: PaddingValues = this.padding,
    textStyle: TextStyle = this.textStyle,
    offset: Dp = this.offset,
    maxWidth: Dp = this.maxWidth,
    elevation: ElevationLevel = this.elevation,
  ): TooltipStyle = TooltipStyle(containerColor, contentColor, shape, padding, textStyle, offset, maxWidth, elevation)

  override fun equals(other: Any?): Boolean = other is TooltipStyle &&
    containerColor == other.containerColor && contentColor == other.contentColor && shape == other.shape &&
    padding == other.padding && textStyle == other.textStyle && offset == other.offset && maxWidth == other.maxWidth &&
    elevation == other.elevation

  override fun hashCode(): Int = listOf(containerColor, contentColor, shape, padding, textStyle, offset, maxWidth, elevation).hashCode()
  override fun toString(): String = "TooltipStyle(containerColor=$containerColor)"
}

public val LocalTooltipStyle: ProvidableCompositionLocal<TooltipStyle?> = staticCompositionLocalOf { null }

public object TooltipDefaults {
  /** Hover delay before showing, and how long a long-press tooltip stays after release. */
  public const val HoverDelayMillis: Long = 500
  public const val LongPressLingerMillis: Long = 1500

  /** An inverse surface in every idiom: dark on light, light on dark. */
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): TooltipStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val type = DittoTheme.typography
    val spacing = DittoTheme.spacing
    return when (idiom) {
      Idiom.Android -> TooltipStyle(
        containerColor = colors.neutrals[if (colors.isDark) 12 else 11],
        contentColor = colors.neutrals[if (colors.isDark) 1 else 1],
        shape = shapes.extraSmall,
        padding = PaddingValues(horizontal = spacing.sm, vertical = spacing.xs),
        textStyle = type.caption,
        offset = spacing.xs,
        maxWidth = 200.dp,
        elevation = ElevationLevel.Level0,
      )
      Idiom.Apple -> TooltipStyle(
        containerColor = colors.neutrals[if (colors.isDark) 11 else 12],
        contentColor = colors.neutrals[1],
        shape = shapes.small,
        padding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        textStyle = type.caption,
        offset = spacing.sm,
        maxWidth = 240.dp,
        elevation = ElevationLevel.Level2,
      )
      Idiom.Desktop -> TooltipStyle(
        containerColor = colors.neutrals[if (colors.isDark) 12 else 12],
        contentColor = colors.neutrals[1],
        shape = shapes.small,
        padding = PaddingValues(horizontal = spacing.md, vertical = spacing.xs),
        textStyle = type.caption,
        offset = spacing.xs,
        maxWidth = 280.dp,
        elevation = ElevationLevel.Level1,
      )
    }
  }
}

/**
 * Shows [text] above [content] on hover (with a pointer, after [TooltipDefaults.HoverDelayMillis])
 * or on long-press (touch). The long-press detector observes in the initial pass and never
 * consumes, so [content] keeps its own click handling.
 */
@Composable
public fun Tooltip(
  text: String,
  modifier: Modifier = Modifier,
  style: TooltipStyle? = null,
  content: @Composable () -> Unit,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalTooltipStyle.current ?: DittoTheme.styleOverrides.resolve(TooltipDefaults.style())
  val hoverSource = remember { MutableInteractionSource() }
  val hovered by hoverSource.collectIsHoveredAsState()
  val pointer = LocalInputCapabilities.current.pointer
  var visible by remember { mutableStateOf(false) }
  var longPressed by remember { mutableStateOf(false) }
  var releases by remember { mutableStateOf(0) }
  var focusedChild by remember { mutableStateOf(false) }
  val keyboard = LocalInputCapabilities.current.keyboard

  LaunchedEffect(hovered, longPressed, releases, focusedChild) {
    when {
      longPressed -> visible = true
      // Keyboard users get the tooltip when they tab onto the anchor.
      focusedChild && keyboard -> {
        delay(TooltipDefaults.HoverDelayMillis)
        visible = true
      }
      hovered -> {
        delay(TooltipDefaults.HoverDelayMillis)
        visible = true
      }
      // Long-press released: linger so the text can be read, then hide.
      releases > 0 && visible -> {
        delay(TooltipDefaults.LongPressLingerMillis)
        visible = false
      }
      else -> visible = false
    }
  }

  Box(
    modifier
      .then(if (pointer) Modifier.hoverable(hoverSource) else Modifier)
      .onFocusChanged { focusedChild = it.hasFocus }
      .pointerInput(Unit) {
        awaitEachGesture {
          awaitFirstDownInitial()
          val released = withTimeoutOrNull(LongPressMillis) { waitForUpInitial() }
          if (released == null) {
            longPressed = true
            waitForUpInitial()
            longPressed = false
            releases++
          }
        }
      },
  ) {
    content()
    if (visible) {
      Popup(popupPositionProvider = remember(style.offset) { AboveAnchor(style.offset.value) }) {
        TooltipBubble(text, style = style)
      }
    }
  }
}

/** The tooltip surface itself, without any trigger. Useful for previews and custom anchoring. */
@Composable
public fun TooltipBubble(
  text: String,
  modifier: Modifier = Modifier,
  style: TooltipStyle? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalTooltipStyle.current ?: DittoTheme.styleOverrides.resolve(TooltipDefaults.style())
  Surface(
    color = style.containerColor,
    contentColor = style.contentColor,
    shape = style.shape,
    elevation = style.elevation,
    modifier = modifier.widthIn(max = style.maxWidth),
  ) {
    Text(text, style = style.textStyle, modifier = Modifier.padding(style.padding))
  }
}

/** An icon button that announces itself with a tooltip, using [contentDescription] as the text. */
@Composable
public fun IconButton(
  icon: ImageVector,
  contentDescription: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  variant: IconButtonVariant = IconButtonVariant.Standard,
  tooltip: Boolean = true,
  style: IconButtonStyle? = null,
) {
  val button: @Composable () -> Unit = {
    val content: @Composable () -> Unit = { Icon(icon, contentDescription) }
    when (variant) {
      IconButtonVariant.Standard -> IconButton(onClick, modifier, enabled, style, content = content)
      IconButtonVariant.Filled -> FilledIconButton(onClick, modifier, enabled, style, content = content)
      IconButtonVariant.Tonal -> TonalIconButton(onClick, modifier, enabled, style, content = content)
      IconButtonVariant.Outlined -> OutlinedIconButton(onClick, modifier, enabled, style, content = content)
    }
  }
  if (tooltip) Tooltip(text = contentDescription) { button() } else button()
}

private const val LongPressMillis = 500L

private class AboveAnchor(private val gapDp: Float) : PopupPositionProvider {
  override fun calculatePosition(
    anchorBounds: IntRect,
    windowSize: IntSize,
    layoutDirection: LayoutDirection,
    popupContentSize: IntSize,
  ): IntOffset {
    val gap = gapDp.toInt()
    val x = (anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2)
      .coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0))
    val above = anchorBounds.top - popupContentSize.height - gap
    val y = if (above >= 0) above else anchorBounds.bottom + gap
    return IntOffset(x, y)
  }
}

private suspend fun androidx.compose.ui.input.pointer.AwaitPointerEventScope.awaitFirstDownInitial() {
  while (true) {
    val event = awaitPointerEvent(PointerEventPass.Initial)
    if (event.changes.any { it.pressed && !it.previousPressed }) return
  }
}

private suspend fun androidx.compose.ui.input.pointer.AwaitPointerEventScope.waitForUpInitial() {
  while (true) {
    val event = awaitPointerEvent(PointerEventPass.Initial)
    if (event.changes.all { !it.pressed }) return
  }
}
