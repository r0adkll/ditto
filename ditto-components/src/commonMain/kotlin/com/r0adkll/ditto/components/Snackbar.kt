package com.r0adkll.ditto.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ElevationLevel
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

public enum class SnackbarDuration(internal val millis: kotlin.Long) {
  Short(4000),
  Long(10000),
  Indefinite(kotlin.Long.MAX_VALUE),
}

public enum class SnackbarResult { Dismissed, ActionPerformed }

/** One queued message. */
@Stable
public class SnackbarData internal constructor(
  public val message: String,
  public val actionLabel: String?,
  public val duration: SnackbarDuration,
  private val continuation: CancellableContinuation<SnackbarResult>,
) {
  public fun performAction() { if (continuation.isActive) continuation.resume(SnackbarResult.ActionPerformed) }
  public fun dismiss() { if (continuation.isActive) continuation.resume(SnackbarResult.Dismissed) }
}

/** Queue for [SnackbarHost]; one message shows at a time. Hoist it and pass to both. */
@Stable
public class SnackbarHostState {
  private val mutex = Mutex()
  public var current: SnackbarData? by mutableStateOf(null)
    private set

  public suspend fun showSnackbar(
    message: String,
    actionLabel: String? = null,
    duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Long,
  ): SnackbarResult = mutex.withLock {
    try {
      suspendCancellableCoroutine { cont -> current = SnackbarData(message, actionLabel, duration, cont) }
    } finally {
      current = null
    }
  }
}

@Immutable
public class SnackbarStyle(
  public val containerColor: Color,
  public val contentColor: Color,
  public val actionColor: Color,
  public val shape: Shape,
  public val elevation: ElevationLevel,
  public val border: BorderStroke?,
  public val padding: PaddingValues,
  public val textStyle: TextStyle,
  public val maxWidth: Dp,
  /** Where the host places the message. */
  public val alignment: Alignment,
  public val margin: Dp,
) {
  public fun copy(
    containerColor: Color = this.containerColor,
    contentColor: Color = this.contentColor,
    actionColor: Color = this.actionColor,
    shape: Shape = this.shape,
    elevation: ElevationLevel = this.elevation,
    border: BorderStroke? = this.border,
    padding: PaddingValues = this.padding,
    textStyle: TextStyle = this.textStyle,
    maxWidth: Dp = this.maxWidth,
    alignment: Alignment = this.alignment,
    margin: Dp = this.margin,
  ): SnackbarStyle = SnackbarStyle(containerColor, contentColor, actionColor, shape, elevation, border, padding, textStyle, maxWidth, alignment, margin)

  override fun equals(other: Any?): Boolean = other is SnackbarStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "SnackbarStyle(alignment=$alignment)"
  private fun fields(): List<Any?> = listOf(containerColor, contentColor, actionColor, shape, elevation, border, padding, textStyle, maxWidth, alignment, margin)
}

public val LocalSnackbarStyle: ProvidableCompositionLocal<SnackbarStyle?> = staticCompositionLocalOf { null }

public object SnackbarDefaults {
  /** Android: inverse bar, bottom center. Apple: floating toast capsule. Desktop: bordered toast, bottom end. */
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): SnackbarStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val type = DittoTheme.typography
    val spacing = DittoTheme.spacing
    val dimens = DittoTheme.dimens
    return when (idiom) {
      Idiom.Android -> SnackbarStyle(
        containerColor = colors.neutrals[if (colors.isDark) 12 else 12],
        contentColor = colors.neutrals[1],
        actionColor = if (colors.isDark) colors.accent else colors.neutrals[1],
        shape = shapes.extraSmall,
        elevation = ElevationLevel.Level3,
        border = null,
        padding = PaddingValues(start = spacing.lg, end = spacing.sm, top = spacing.sm, bottom = spacing.sm),
        textStyle = type.bodySmall,
        maxWidth = 600.dp,
        alignment = Alignment.BottomCenter,
        margin = spacing.lg,
      )
      Idiom.Apple -> SnackbarStyle(
        containerColor = colors.surfaceRaised,
        contentColor = colors.onSurface,
        actionColor = colors.accent,
        shape = shapes.full,
        elevation = ElevationLevel.Level4,
        border = null,
        padding = PaddingValues(horizontal = spacing.lg, vertical = spacing.md),
        textStyle = type.bodySmall,
        maxWidth = 420.dp,
        alignment = Alignment.BottomCenter,
        margin = spacing.xl,
      )
      Idiom.Desktop -> SnackbarStyle(
        containerColor = colors.surface,
        contentColor = colors.onSurface,
        actionColor = colors.accent,
        shape = shapes.medium,
        elevation = ElevationLevel.Level3,
        border = BorderStroke(dimens.borderWidth, colors.outlineVariant),
        padding = PaddingValues(start = spacing.lg, end = spacing.sm, top = spacing.sm, bottom = spacing.sm),
        textStyle = type.bodySmall,
        maxWidth = 380.dp,
        alignment = Alignment.BottomEnd,
        margin = spacing.lg,
      )
    }
  }
}

/** Shows the [state]'s current message with the idiom's placement and timing. Put in `Scaffold(snackbarHost = ...)`. */
@Composable
public fun SnackbarHost(
  state: SnackbarHostState,
  modifier: Modifier = Modifier,
  style: SnackbarStyle? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalSnackbarStyle.current ?: DittoTheme.styleOverrides.resolve(SnackbarDefaults.style())
  val current = state.current
  LaunchedEffect(current) {
    if (current != null && current.duration != SnackbarDuration.Indefinite) {
      delay(current.duration.millis)
      current.dismiss()
    }
  }
  // Keep the last message around during the exit animation.
  var lastShown by remember { mutableStateOf<SnackbarData?>(null) }
  if (current != null) lastShown = current
  Box(modifier.fillMaxWidth().padding(style.margin), contentAlignment = style.alignment) {
    AnimatedVisibility(
      visible = current != null,
      enter = fadeIn() + slideInVertically { it / 2 },
      exit = fadeOut() + slideOutVertically { it / 2 },
    ) {
      lastShown?.let { data -> Snackbar(data, style = style) }
    }
  }
}

/** The message surface. */
@Composable
public fun Snackbar(
  data: SnackbarData,
  modifier: Modifier = Modifier,
  style: SnackbarStyle? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalSnackbarStyle.current ?: DittoTheme.styleOverrides.resolve(SnackbarDefaults.style())
  Surface(
    modifier = modifier.widthIn(max = style.maxWidth).semantics { liveRegion = LiveRegionMode.Polite },
    shape = style.shape,
    color = style.containerColor,
    contentColor = style.contentColor,
    elevation = style.elevation,
    border = style.border,
  ) {
    Row(Modifier.padding(style.padding), verticalAlignment = Alignment.CenterVertically) {
      Text(data.message, style = style.textStyle, modifier = Modifier.weight(1f))
      if (data.actionLabel != null) {
        Spacer(Modifier.width(DittoTheme.spacing.sm))
        TextButton(
          onClick = { data.performAction() },
          style = ButtonDefaults.style(ButtonVariant.Text).copy(contentColor = style.actionColor, minHeight = 32.dp),
        ) { Text(data.actionLabel) }
      }
    }
  }
}
