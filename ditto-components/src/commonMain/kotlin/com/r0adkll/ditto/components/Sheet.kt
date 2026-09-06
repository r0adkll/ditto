package com.r0adkll.ditto.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshotFlow
import com.composeunstyled.DragIndication
import com.composeunstyled.ModalBottomSheetState
import com.composeunstyled.Scrim
import com.composeunstyled.Sheet
import com.composeunstyled.UnstyledModalBottomSheet
import com.composeunstyled.rememberModalBottomSheetState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog as ComposeDialog
import androidx.compose.ui.window.DialogProperties
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ElevationLevel

@Immutable
public class SheetStyle(
  public val shape: Shape,
  public val containerColor: Color,
  public val scrimColor: Color,
  public val elevation: ElevationLevel,
  public val dragHandle: Boolean,
  public val dragHandleColor: Color,
  /** Desktop: present as a centered dialog instead of a bottom sheet. */
  public val centered: Boolean,
  public val maxWidth: Dp,
  public val contentPadding: Dp,
) {
  public fun copy(
    shape: Shape = this.shape,
    containerColor: Color = this.containerColor,
    scrimColor: Color = this.scrimColor,
    elevation: ElevationLevel = this.elevation,
    dragHandle: Boolean = this.dragHandle,
    dragHandleColor: Color = this.dragHandleColor,
    centered: Boolean = this.centered,
    maxWidth: Dp = this.maxWidth,
    contentPadding: Dp = this.contentPadding,
  ): SheetStyle = SheetStyle(shape, containerColor, scrimColor, elevation, dragHandle, dragHandleColor, centered, maxWidth, contentPadding)

  override fun equals(other: Any?): Boolean = other is SheetStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "SheetStyle(centered=$centered)"
  private fun fields(): List<Any?> = listOf(shape, containerColor, scrimColor, elevation, dragHandle, dragHandleColor, centered, maxWidth, contentPadding)
}

public val LocalSheetStyle: ProvidableCompositionLocal<SheetStyle?> = staticCompositionLocalOf { null }

/** A resting height for a bottom sheet. */
public sealed interface SheetDetent {
  /** Wraps the content's own height (the default single detent). */
  public data object Content : SheetDetent

  /** A fraction of the available height. */
  public data class Fraction(val fraction: Float) : SheetDetent

  /** The full available height below the top inset. */
  public data object Full : SheetDetent

  public companion object {
    public val Medium: SheetDetent = Fraction(0.5f)
  }
}

/** Current and target detent for a [ModalSheet] with several [SheetDetent]s. */
@Stable
public class SheetState internal constructor(
  public val detents: List<SheetDetent>,
  initial: SheetDetent,
  internal val engine: ModalBottomSheetState,
) {
  init {
    require(detents.isNotEmpty()) { "A sheet needs at least one detent" }
    require(initial in detents) { "initial detent must be one of detents" }
  }

  internal val initial: SheetDetent = initial

  /** The detent the sheet is resting at (or heading to). */
  public val currentDetent: SheetDetent
    get() = detents.firstOrNull { it.identifier() == engine.currentDetent.identifier } ?: initial

  /** Animates to [detent]. */
  public fun animateTo(detent: SheetDetent) {
    require(detent in detents) { "detent must be one of detents" }
    engine.targetDetent = detent.toEngine()
  }
}

internal fun SheetDetent.identifier(): String = when (this) {
  SheetDetent.Content -> "content"
  is SheetDetent.Fraction -> "fraction-$fraction"
  SheetDetent.Full -> "full"
}

/** Ditto detents → Unstyled detents (height lambdas of container/sheet height). */
internal fun SheetDetent.toEngine(): com.composeunstyled.SheetDetent = when (this) {
  SheetDetent.Content -> com.composeunstyled.SheetDetent(identifier()) { _, sheetHeight -> sheetHeight }
  is SheetDetent.Fraction -> com.composeunstyled.SheetDetent(identifier()) { containerHeight, _ -> containerHeight * fraction.coerceIn(0f, 1f) }
  SheetDetent.Full -> com.composeunstyled.SheetDetent.FullyExpanded
}

@Composable
public fun rememberSheetState(
  detents: List<SheetDetent> = listOf(SheetDetent.Content),
  initial: SheetDetent = detents.first(),
): SheetState {
  val engineDetents = remember(detents) { listOf(com.composeunstyled.SheetDetent.Hidden) + detents.map { it.toEngine() } }
  val engine = rememberModalBottomSheetState(initialDetent = com.composeunstyled.SheetDetent.Hidden, detents = engineDetents)
  return remember(detents, engine) { SheetState(detents, initial, engine) }
}

public object SheetDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): SheetStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    return when (idiom) {
      Idiom.Android -> SheetStyle(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = colors.surfaceRaised,
        scrimColor = Color.Black.copy(alpha = 0.32f),
        elevation = ElevationLevel.Level1,
        dragHandle = true,
        dragHandleColor = colors.onSurfaceVariant.copy(alpha = 0.4f),
        centered = false,
        maxWidth = 640.dp,
        contentPadding = 0.dp,
      )
      Idiom.Apple -> SheetStyle(
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        containerColor = colors.surfaceRaised,
        scrimColor = Color.Black.copy(alpha = 0.4f),
        elevation = ElevationLevel.Level0,
        dragHandle = true,
        dragHandleColor = colors.neutrals[7],
        centered = false,
        maxWidth = 720.dp,
        contentPadding = 0.dp,
      )
      Idiom.Desktop -> SheetStyle(
        shape = shapes.large,
        containerColor = colors.surface,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        elevation = ElevationLevel.Level3,
        dragHandle = false,
        dragHandleColor = Color.Transparent,
        centered = true,
        maxWidth = 560.dp,
        contentPadding = 0.dp,
      )
    }
  }
}

/**
 * Modal surface for secondary tasks: a bottom sheet that slides up and can be swiped away on the
 * mobile idioms, a centered dialog on Desktop (ADR-023). Tapping the scrim dismisses.
 */
@Composable
public fun ModalSheet(
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  state: SheetState = rememberSheetState(),
  style: SheetStyle? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalSheetStyle.current ?: DittoTheme.styleOverrides.resolve(SheetDefaults.style())
  if (!style.centered) {
    BottomSheetLayout(onDismissRequest, modifier, state, style, content)
    return
  }
  ComposeDialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false),
  ) {
    if (style.centered) {
      Box(Modifier.fillMaxSize().background(style.scrimColor).clickable(remember { MutableInteractionSource() }, null) { onDismissRequest() }, contentAlignment = Alignment.Center) {
        Surface(
          modifier = modifier.widthIn(max = style.maxWidth).padding(DittoTheme.spacing.xl).clickable(remember { MutableInteractionSource() }, null) {},
          shape = style.shape,
          color = style.containerColor,
          elevation = style.elevation,
        ) {
          Column(content = content)
        }
      }
    }
  }
}

@Composable
private fun BottomSheetLayout(
  onDismissRequest: () -> Unit,
  modifier: Modifier,
  state: SheetState,
  style: SheetStyle,
  content: @Composable ColumnScope.() -> Unit,
) {
  val engine = state.engine
  // Slide in on first composition; the engine animates from Hidden to the initial detent.
  LaunchedEffect(engine) { engine.animateTo(state.initial.toEngine()) }
  // Dismissed when the engine settles at Hidden (swipe past the lowest detent or scrim tap).
  LaunchedEffect(engine) {
    snapshotFlow { engine.isIdle && engine.currentDetent == com.composeunstyled.SheetDetent.Hidden }
      .collect { hidden -> if (hidden) onDismissRequest() }
  }
  UnstyledModalBottomSheet(
    state = engine,
    onDismiss = onDismissRequest,
    overlay = { Scrim(scrimColor = style.scrimColor) },
  ) {
    Sheet(modifier.widthIn(max = style.maxWidth).fillMaxWidth()) {
      Surface(shape = style.shape, color = style.containerColor, elevation = style.elevation, modifier = Modifier.fillMaxWidth()) {
        Column(
          Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
            .padding(style.contentPadding),
        ) {
          if (style.dragHandle) {
            // DragIndication carries the expand/collapse/dismiss semantics; the pill is its modifier.
            Box(Modifier.fillMaxWidth().padding(vertical = DittoTheme.spacing.md), contentAlignment = Alignment.Center) {
              DragIndication(Modifier.width(36.dp).height(4.dp).background(style.dragHandleColor, DittoTheme.shapes.full))
            }
          }
          content()
        }
      }
    }
  }
}

/** The sheet surface alone, for previews. */
@Composable
public fun SheetContent(
  modifier: Modifier = Modifier,
  style: SheetStyle? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalSheetStyle.current ?: DittoTheme.styleOverrides.resolve(SheetDefaults.style())
  Surface(modifier = modifier.fillMaxWidth(), shape = style.shape, color = style.containerColor, elevation = style.elevation) {
    Column {
      if (style.dragHandle) {
        Box(Modifier.fillMaxWidth().padding(vertical = DittoTheme.spacing.md), contentAlignment = Alignment.Center) {
          Box(Modifier.width(36.dp).height(4.dp).background(style.dragHandleColor, DittoTheme.shapes.full))
        }
      }
      content()
    }
  }
}
