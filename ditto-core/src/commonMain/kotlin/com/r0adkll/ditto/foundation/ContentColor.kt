package com.r0adkll.ditto.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.r0adkll.ditto.theme.DittoTheme

/** The preferred color for content (text, icons) drawn on the current container. */
public val LocalContentColor: ProvidableCompositionLocal<Color> = compositionLocalOf { Color.Black }

/**
 * The size [Icon] draws at when it isn't given one. Containers that have an opinion — buttons,
 * icon buttons, chips — provide their style's icon size here, so an `Icon` written into a content
 * slot matches the container without the caller repeating the number. `Dp.Unspecified` means
 * "no opinion": fall back to the idiom's default icon size.
 */
public val LocalIconSize: ProvidableCompositionLocal<Dp> = compositionLocalOf { Dp.Unspecified }

/** Sets the ambient [LocalIconSize] for [content]. */
@Composable
public fun ProvideIconSize(size: Dp, content: @Composable () -> Unit) {
  CompositionLocalProvider(LocalIconSize provides size, content = content)
}

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
