package com.r0adkll.ditto.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A vertical scrollbar for [scrollState], styled with Ditto tokens. Draws a thin overlay thumb on
 * Desktop (thicker on hover); a no-op on the touch idioms, where the platform draws its own.
 * Place it at the end of a `Box` that also holds the scrolling content.
 */
@Composable
public expect fun VerticalScrollbar(scrollState: ScrollState, modifier: Modifier = Modifier)

/** Lazy-list variant of [VerticalScrollbar]. */
@Composable
public expect fun VerticalScrollbar(listState: LazyListState, modifier: Modifier = Modifier)
