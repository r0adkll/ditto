package com.r0adkll.ditto.components

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.theme.DittoTheme

@Composable
public actual fun VerticalScrollbar(scrollState: ScrollState, modifier: Modifier) {
  CompositionLocalProvider(LocalScrollbarStyle provides dittoScrollbarStyle()) {
    androidx.compose.foundation.VerticalScrollbar(rememberScrollbarAdapter(scrollState), modifier)
  }
}

@Composable
public actual fun VerticalScrollbar(listState: LazyListState, modifier: Modifier) {
  CompositionLocalProvider(LocalScrollbarStyle provides dittoScrollbarStyle()) {
    androidx.compose.foundation.VerticalScrollbar(rememberScrollbarAdapter(listState), modifier)
  }
}

@Composable
private fun dittoScrollbarStyle(): ScrollbarStyle {
  val colors = DittoTheme.colors
  return ScrollbarStyle(
    minimalHeight = 24.dp,
    thickness = 8.dp,
    shape = DittoTheme.shapes.full,
    hoverDurationMillis = DittoTheme.motion.durationShort,
    unhoverColor = colors.onSurface.copy(alpha = 0.22f),
    hoverColor = colors.onSurface.copy(alpha = 0.45f),
  )
}
