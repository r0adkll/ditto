package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.contentColorFor
import com.r0adkll.ditto.theme.DittoTheme

/**
 * Screen skeleton: full-bleed [containerColor], a [topBar] and [bottomBar] that draw under the
 * system bars, a [floatingAction] anchored bottom-end, a [snackbarHost] above the bottom bar, and
 * [content] that receives the padding it must respect (bar heights plus the window insets not
 * already covered by a bar, ADR-024).
 *
 * Bars are expected to handle their own insets ([TopBar] does); [contentWindowInsets] is what
 * the content consumes, `safeDrawing` by default. Pass `WindowInsets(0)` to opt out.
 */
@Composable
public fun Scaffold(
  modifier: Modifier = Modifier,
  topBar: @Composable () -> Unit = {},
  bottomBar: @Composable () -> Unit = {},
  snackbarHost: @Composable () -> Unit = {},
  floatingAction: @Composable () -> Unit = {},
  containerColor: Color = DittoTheme.colors.background,
  contentColor: Color = contentColorFor(containerColor),
  contentWindowInsets: WindowInsets = WindowInsets.safeDrawing,
  content: @Composable (PaddingValues) -> Unit,
) {
  Surface(modifier = modifier, color = containerColor, contentColor = contentColor) {
    SubcomposeLayout { constraints ->
      val width = constraints.maxWidth
      val height = constraints.maxHeight
      val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

      val topPlaceables = subcompose(Slot.TopBar, topBar).map { it.measure(looseConstraints) }
      val topHeight = topPlaceables.maxOfOrNull { it.height } ?: 0
      val bottomPlaceables = subcompose(Slot.BottomBar, bottomBar).map { it.measure(looseConstraints) }
      val bottomHeight = bottomPlaceables.maxOfOrNull { it.height } ?: 0

      val insets = contentWindowInsets.asPaddingValues(this)
      val layoutDirection = layoutDirection
      val padding = PaddingValues(
        start = insets.calculateStartPadding(layoutDirection),
        top = if (topHeight > 0) topHeight.toDp() else insets.calculateTopPadding(),
        end = insets.calculateEndPadding(layoutDirection),
        bottom = if (bottomHeight > 0) bottomHeight.toDp() else insets.calculateBottomPadding(),
      )
      val contentPlaceables = subcompose(Slot.Content) { content(padding) }.map { it.measure(looseConstraints) }
      val fabPlaceables = subcompose(Slot.Fab, floatingAction).map { it.measure(looseConstraints) }
      val fabHeight = fabPlaceables.maxOfOrNull { it.height } ?: 0
      val fabWidth = fabPlaceables.maxOfOrNull { it.width } ?: 0
      val fabMargin = 16.dp.roundToPx()
      val bottomInset = if (bottomHeight > 0) 0 else insets.calculateBottomPadding().roundToPx()
      val endInset = insets.calculateEndPadding(layoutDirection).roundToPx()
      val snackPlaceables = subcompose(Slot.Snackbar, snackbarHost).map { it.measure(looseConstraints) }
      val snackHeight = snackPlaceables.maxOfOrNull { it.height } ?: 0
      val fabY = height - bottomHeight - bottomInset - fabMargin - fabHeight
      val fabX = if (layoutDirection == LayoutDirection.Ltr) width - endInset - fabMargin - fabWidth else insets.calculateStartPadding(layoutDirection).roundToPx() + fabMargin

      layout(width, height) {
        contentPlaceables.forEach { it.place(0, 0) }
        topPlaceables.forEach { it.place(0, 0) }
        bottomPlaceables.forEach { it.place(0, height - bottomHeight) }
        fabPlaceables.forEach { it.place(fabX, fabY) }
        val snackY = height - bottomHeight - bottomInset - snackHeight - (if (fabHeight > 0) fabHeight + fabMargin else 0)
        snackPlaceables.forEach { it.place(0, snackY) }
      }
    }
  }
}

private enum class Slot { TopBar, BottomBar, Content, Fab, Snackbar }

/** The insets a top bar should consume: status bar plus horizontal cutouts. */
public val TopBarInsets: WindowInsets
  @Composable get() = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)

/** The insets a bottom bar should consume. */
public val BottomBarInsets: WindowInsets
  @Composable get() = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
