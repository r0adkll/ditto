package com.r0adkll.ditto.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.r0adkll.ditto.theme.DittoTheme

/** The preferred color for content (text, icons) drawn on the current container. */
public val LocalContentColor: ProvidableCompositionLocal<Color> = compositionLocalOf { Color.Black }

/** The ambient text style; [Text] merges it with its own. */
public val LocalTextStyle: ProvidableCompositionLocal<TextStyle> =
  compositionLocalOf(structuralEqualityPolicy()) { TextStyle.Default }

/** Merges [value] into the ambient text style for [content]. */
@Composable
public fun ProvideTextStyle(value: TextStyle, content: @Composable () -> Unit) {
  val merged = LocalTextStyle.current.merge(value)
  CompositionLocalProvider(LocalTextStyle provides merged, content = content)
}

/**
 * The content color paired with [backgroundColor] in the current theme, falling back to the
 * ambient [LocalContentColor] for colors the theme does not know about.
 */
@Composable
@ReadOnlyComposable
public fun contentColorFor(backgroundColor: Color): Color =
  DittoTheme.colors.contentColorFor(backgroundColor).takeIf { it != Color.Unspecified } ?: LocalContentColor.current
