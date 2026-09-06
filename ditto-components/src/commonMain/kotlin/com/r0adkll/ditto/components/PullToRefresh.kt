package com.r0adkll.ditto.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ElevationLevel
import kotlin.math.roundToInt

/** Pull distance and refresh progress for [PullToRefreshBox]. */
@Stable
public class PullToRefreshState internal constructor(private val thresholdPx: Float) {
  private val anim = Animatable(0f)
  internal var raw: Float by mutableFloatStateOf(0f)

  /** Current visual offset in px (drag-resisted). */
  public val distance: Float get() = if (anim.isRunning) anim.value else raw

  /** 0..1 toward the release threshold, may exceed 1 while over-pulled. */
  public val progress: Float get() = (distance / thresholdPx).coerceAtLeast(0f)

  internal suspend fun animateTo(target: Float) {
    anim.snapTo(raw)
    anim.animateTo(target) { raw = value }
    raw = target
  }

  internal val threshold: Float get() = thresholdPx
}

@Composable
public fun rememberPullToRefreshState(): PullToRefreshState {
  val density = LocalDensity.current
  return remember { PullToRefreshState(with(density) { 72.dp.toPx() }) }
}

/**
 * Wraps scrolling [content] and triggers [onRefresh] when pulled past the threshold. Android and
 * Desktop float an indicator over the content; Apple shifts the content down and shows the spoked
 * activity indicator in the revealed space, UIRefreshControl-style.
 */
@Composable
public fun PullToRefreshBox(
  isRefreshing: Boolean,
  onRefresh: () -> Unit,
  modifier: Modifier = Modifier,
  state: PullToRefreshState = rememberPullToRefreshState(),
  enabled: Boolean = true,
  content: @Composable () -> Unit,
) {
  val idiom = DittoTheme.idiom
  val shiftsContent = idiom == Idiom.Apple
  val holdDistance = state.threshold * 0.85f

  LaunchedEffect(isRefreshing) {
    if (isRefreshing) state.animateTo(holdDistance) else state.animateTo(0f)
  }

  val connection = remember(state, enabled) {
    object : NestedScrollConnection {
      override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (!enabled || isRefreshing || available.y >= 0f || state.raw <= 0f) return Offset.Zero
        val consumed = (-available.y).coerceAtMost(state.raw)
        state.raw -= consumed
        return Offset(0f, -consumed)
      }

      override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        if (!enabled || isRefreshing || available.y <= 0f || source != NestedScrollSource.UserInput) return Offset.Zero
        state.raw += available.y * 0.5f
        return Offset(0f, available.y)
      }

      override suspend fun onPreFling(available: Velocity): Velocity {
        if (state.raw <= 0f || isRefreshing) return Velocity.Zero
        if (state.raw >= state.threshold) {
          onRefresh()
          state.animateTo(holdDistance)
        } else {
          state.animateTo(0f)
        }
        return Velocity.Zero
      }
    }
  }

  Box(modifier.nestedScroll(connection)) {
    Box(Modifier.then(if (shiftsContent) Modifier.offset { IntOffset(0, state.distance.roundToInt()) } else Modifier)) {
      content()
    }
    PullIndicator(state, isRefreshing, floating = !shiftsContent, modifier = Modifier.align(Alignment.TopCenter))
  }
}

@Composable
private fun PullIndicator(state: PullToRefreshState, refreshing: Boolean, floating: Boolean, modifier: Modifier) {
  val visible = state.distance > 0.5f || refreshing
  if (!visible) return
  val progress = state.progress.coerceIn(0f, 1f)
  if (floating) {
    Surface(
      modifier = modifier
        .offset { IntOffset(0, (state.distance - 40.dp.toPx()).roundToInt()) }
        .alpha(progress)
        .graphicsLayer { rotationZ = if (refreshing) 0f else progress * 180f },
      shape = DittoTheme.shapes.full,
      color = DittoTheme.colors.surfaceRaised,
      elevation = ElevationLevel.Level3,
    ) {
      Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
        if (refreshing) {
          CircularProgressIndicator(style = ProgressIndicatorDefaults.style().copy(circularSize = 24.dp, circularStroke = 2.5.dp))
        } else {
          CircularProgressIndicator(progress = { progress * 0.75f }, style = ProgressIndicatorDefaults.style().copy(circularSize = 24.dp, circularStroke = 2.5.dp))
        }
      }
    }
  } else {
    Box(
      modifier.fillMaxWidth().height(with(LocalDensity.current) { state.distance.toDp() }),
      contentAlignment = Alignment.Center,
    ) {
      Box(Modifier.alpha(progress)) {
        if (refreshing) {
          CircularProgressIndicator()
        } else {
          CircularProgressIndicator(progress = { progress }, style = ProgressIndicatorDefaults.style().copy(color = DittoTheme.colors.onSurfaceVariant))
        }
      }
    }
  }
}
