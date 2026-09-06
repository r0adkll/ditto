package com.r0adkll.ditto.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composeunstyled.ScrollbarState
import com.composeunstyled.Thumb
import com.composeunstyled.ThumbVisibility
import com.composeunstyled.UnstyledHorizontalScrollbar
import com.composeunstyled.UnstyledVerticalScrollbar
import com.composeunstyled.rememberScrollbarState
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.theme.DittoTheme

/**
 * A vertical scrollbar for [scrollState], styled with Ditto tokens: an 8dp overlay thumb that
 * darkens on hover. Shown on the Desktop idiom or whenever a pointer is present; the touch idioms
 * keep the platform's own indicators and render nothing. Place at the end of a `Box` beside the
 * scrolling content with `fillMaxHeight()`.
 *
 * Behaviour (drag, track press-to-page, hover, auto-hide) comes from Compose Unstyled (ADR-028);
 * none of its types appear in this API.
 */
@Composable
public fun VerticalScrollbar(scrollState: ScrollState, modifier: Modifier = Modifier) {
  if (!scrollbarsVisible()) return
  DittoVerticalScrollbar(rememberScrollbarState(scrollState), modifier)
}

/** Lazy-list variant of [VerticalScrollbar]. */
@Composable
public fun VerticalScrollbar(listState: LazyListState, modifier: Modifier = Modifier) {
  if (!scrollbarsVisible()) return
  DittoVerticalScrollbar(rememberScrollbarState(listState), modifier)
}

/** Horizontal counterpart of [VerticalScrollbar]. Place at the bottom of the `Box` with `fillMaxWidth()`. */
@Composable
public fun HorizontalScrollbar(scrollState: ScrollState, modifier: Modifier = Modifier) {
  if (!scrollbarsVisible()) return
  DittoHorizontalScrollbar(rememberScrollbarState(scrollState), modifier)
}

@Composable
public fun HorizontalScrollbar(listState: LazyListState, modifier: Modifier = Modifier) {
  if (!scrollbarsVisible()) return
  DittoHorizontalScrollbar(rememberScrollbarState(listState), modifier)
}

@Composable
private fun scrollbarsVisible(): Boolean = DittoTheme.idiom == Idiom.Desktop || LocalInputCapabilities.current.pointer

private val ThumbThickness = 8.dp
private val TrackPadding = 2.dp

@Composable
private fun DittoVerticalScrollbar(state: ScrollbarState, modifier: Modifier) {
  val source = remember { MutableInteractionSource() }
  val hovered by source.collectIsHoveredAsState()
  val color = thumbColor(hovered)
  UnstyledVerticalScrollbar(scrollbarState = state, modifier = modifier.fillMaxHeight().width(ThumbThickness + TrackPadding * 2).hoverable(source)) {
    Thumb(
      modifier = Modifier.padding(horizontal = TrackPadding).fillMaxHeight().width(ThumbThickness).background(color, DittoTheme.shapes.full),
      thumbVisibility = ThumbVisibility.AlwaysVisible,
    )
  }
}

@Composable
private fun DittoHorizontalScrollbar(state: ScrollbarState, modifier: Modifier) {
  val source = remember { MutableInteractionSource() }
  val hovered by source.collectIsHoveredAsState()
  val color = thumbColor(hovered)
  UnstyledHorizontalScrollbar(scrollbarState = state, modifier = modifier.fillMaxWidth().height(ThumbThickness + TrackPadding * 2).hoverable(source)) {
    Thumb(
      modifier = Modifier.padding(vertical = TrackPadding).fillMaxWidth().height(ThumbThickness).background(color, DittoTheme.shapes.full),
      thumbVisibility = ThumbVisibility.AlwaysVisible,
    )
  }
}

@Composable
private fun thumbColor(hovered: Boolean) = DittoTheme.colors.onSurface.copy(alpha = if (hovered) 0.45f else 0.22f)
