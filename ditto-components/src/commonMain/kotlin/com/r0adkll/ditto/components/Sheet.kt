package com.r0adkll.ditto.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog as ComposeDialog
import androidx.compose.ui.window.DialogProperties
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ElevationLevel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

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
public class SheetState(
  public val detents: List<SheetDetent>,
  initial: SheetDetent,
) {
  init {
    require(detents.isNotEmpty()) { "A sheet needs at least one detent" }
    require(initial in detents) { "initial detent must be one of detents" }
  }

  public var currentDetent: SheetDetent by mutableStateOf(initial)
    internal set

  /** Set to request a programmatic move; the sheet animates and clears it. */
  internal var requested: SheetDetent? by mutableStateOf(null)

  public fun animateTo(detent: SheetDetent) {
    require(detent in detents) { "detent must be one of detents" }
    requested = detent
  }
}

@Composable
public fun rememberSheetState(
  detents: List<SheetDetent> = listOf(SheetDetent.Content),
  initial: SheetDetent = detents.first(),
): SheetState = remember(detents) { SheetState(detents, initial) }

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
  val style = style ?: LocalSheetStyle.current ?: SheetDefaults.style()
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
    } else {
      BottomSheetLayout(onDismissRequest, modifier, state, style, content)
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
  val motion = DittoTheme.motion
  val scope = rememberCoroutineScope()
  BoxWithConstraints(Modifier.fillMaxSize()) {
    val density = LocalDensity.current
    val topInset = with(density) { WindowInsets.safeDrawing.getTop(this).toFloat() }
    val containerPx = constraints.maxHeight.toFloat()
    val availablePx = (containerPx - topInset).coerceAtLeast(1f)
    var contentHeightPx by remember { mutableIntStateOf(0) }
    val maxHeightPx = state.detents.maxOf { detentHeight(it, availablePx, contentHeightPx.toFloat()) }
    fun heightOf(detent: SheetDetent) = detentHeight(detent, availablePx, contentHeightPx.toFloat())
    // Offset is measured from the fully-expanded position (offset 0 = tallest detent visible).
    fun offsetOf(detent: SheetDetent) = (maxHeightPx - heightOf(detent)).coerceAtLeast(0f)

    val offset = remember { Animatable(containerPx) }
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(contentHeightPx > 0 || state.detents.none { it is SheetDetent.Content }) {
      if (!entered && (contentHeightPx > 0 || state.detents.none { it is SheetDetent.Content })) {
        entered = true
        offset.snapTo(maxHeightPx.coerceAtLeast(1f))
        offset.animateTo(offsetOf(state.currentDetent), tween(motion.durationMedium, easing = motion.easingDecelerate))
      }
    }
    LaunchedEffect(state.requested) {
      val target = state.requested ?: return@LaunchedEffect
      state.currentDetent = target
      offset.animateTo(offsetOf(target), motion.spring)
      state.requested = null
    }

    fun dismiss() {
      scope.launch {
        offset.animateTo(maxHeightPx.coerceAtLeast(1f), tween(motion.durationShort, easing = motion.easingAccelerate))
        onDismissRequest()
      }
    }
    suspend fun settle(velocity: Float) {
      val smallest = state.detents.minByOrNull { heightOf(it) } ?: state.currentDetent
      val dismissLine = offsetOf(smallest) + heightOf(smallest) * 0.3f
      if (offset.value > dismissLine || velocity > 1800f) { dismiss(); return }
      // Nearest detent, biased by fling direction.
      val projected = offset.value + velocity * 0.15f
      val target = state.detents.minByOrNull { abs(offsetOf(it) - projected) } ?: state.currentDetent
      state.currentDetent = target
      offset.animateTo(offsetOf(target), motion.spring)
    }
    val drag = rememberDraggableState { delta -> scope.launch { offset.snapTo((offset.value + delta).coerceAtLeast(0f)) } }
    val scrimAlpha = if (maxHeightPx <= 0f) 1f else (1f - (offset.value - offsetOf(state.detents.minByOrNull { heightOf(it) }!!)).coerceAtLeast(0f) / heightOf(state.detents.minByOrNull { heightOf(it) }!!).coerceAtLeast(1f)).coerceIn(0f, 1f)
    val nested = remember {
      object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
          if (available.y < 0f && offset.value > 0f) {
            val consumed = maxOf(available.y, -offset.value)
            scope.launch { offset.snapTo(offset.value + consumed) }
            return Offset(0f, consumed)
          }
          return Offset.Zero
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
          if (available.y > 0f && source == NestedScrollSource.UserInput) {
            scope.launch { offset.snapTo(offset.value + available.y) }
            return Offset(0f, available.y)
          }
          return Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
          val resting = state.detents.any { abs(offsetOf(it) - offset.value) < 0.5f }
          if (!resting || offset.value > 0f && available.y > 0f) {
            settle(available.y)
            return available
          }
          return Velocity.Zero
        }
      }
    }
    val visibleHeightDp = with(density) { (maxHeightPx - offset.value).coerceAtLeast(0f).toDp() }
    val fixedHeight = state.detents.any { it !is SheetDetent.Content }

    Box(
      Modifier
        .fillMaxSize()
        .background(style.scrimColor.copy(alpha = style.scrimColor.alpha * scrimAlpha))
        .clickable(remember { MutableInteractionSource() }, null) { dismiss() },
      contentAlignment = Alignment.BottomCenter,
    ) {
      Surface(
        modifier = modifier
          .widthIn(max = style.maxWidth)
          .fillMaxWidth()
          .then(if (fixedHeight) Modifier.height(with(density) { maxHeightPx.toDp() }) else Modifier)
          .onSizeChanged { contentHeightPx = it.height }
          .offset { IntOffset(0, offset.value.roundToInt()) }
          .draggable(state = drag, orientation = Orientation.Vertical, onDragStopped = { velocity -> settle(velocity) })
          .nestedScroll(nested)
          .clickable(remember { MutableInteractionSource() }, null) {},
        shape = style.shape,
        color = style.containerColor,
        elevation = style.elevation,
      ) {
        Column(
          Modifier
            .then(if (fixedHeight) Modifier.height(visibleHeightDp.coerceAtLeast(0.dp)) else Modifier)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
            .padding(style.contentPadding),
        ) {
          if (style.dragHandle) {
            Box(Modifier.fillMaxWidth().padding(vertical = DittoTheme.spacing.md), contentAlignment = Alignment.Center) {
              Box(Modifier.width(36.dp).height(4.dp).background(style.dragHandleColor, DittoTheme.shapes.full))
            }
          }
          content()
        }
      }
    }
  }
}

private fun detentHeight(detent: SheetDetent, availablePx: Float, contentPx: Float): Float = when (detent) {
  SheetDetent.Content -> contentPx.coerceAtMost(availablePx)
  is SheetDetent.Fraction -> availablePx * detent.fraction.coerceIn(0f, 1f)
  SheetDetent.Full -> availablePx
}

/** The sheet surface alone, for previews. */
@Composable
public fun SheetContent(
  modifier: Modifier = Modifier,
  style: SheetStyle? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalSheetStyle.current ?: SheetDefaults.style()
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
