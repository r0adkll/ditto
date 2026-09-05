package com.r0adkll.ditto.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.theme.DittoTheme

/**
 * A hairline separator. Defaults to the idiom border width and `outlineVariant`; [startIndent]
 * gives the Apple-style inset that aligns with list content.
 */
@Composable
public fun HorizontalDivider(
  modifier: Modifier = Modifier,
  thickness: Dp = DittoTheme.dimens.borderWidth,
  color: Color = DittoTheme.colors.outlineVariant,
  startIndent: Dp = 0.dp,
  endIndent: Dp = 0.dp,
) {
  Canvas(modifier.fillMaxWidth().height(thickness).padding(start = startIndent, end = endIndent)) {
    val y = size.height / 2
    drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = size.height)
  }
}

@Composable
public fun VerticalDivider(
  modifier: Modifier = Modifier,
  thickness: Dp = DittoTheme.dimens.borderWidth,
  color: Color = DittoTheme.colors.outlineVariant,
  topIndent: Dp = 0.dp,
  bottomIndent: Dp = 0.dp,
) {
  Canvas(modifier.fillMaxHeight().width(thickness).padding(top = topIndent, bottom = bottomIndent)) {
    val x = size.width / 2
    drawLine(color, Offset(x, 0f), Offset(x, size.height), strokeWidth = size.width)
  }
}
