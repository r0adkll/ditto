package com.r0adkll.ditto.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
public actual fun VerticalScrollbar(scrollState: ScrollState, modifier: Modifier): Unit = Unit

@Composable
public actual fun VerticalScrollbar(listState: LazyListState, modifier: Modifier): Unit = Unit

@Composable
public actual fun HorizontalScrollbar(scrollState: ScrollState, modifier: Modifier): Unit = Unit

@Composable
public actual fun HorizontalScrollbar(listState: LazyListState, modifier: Modifier): Unit = Unit
