package com.r0adkll.ditto.foundation

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.draw.paint
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.takeOrElse
import com.r0adkll.ditto.theme.DittoTheme

/**
 * Draws [imageVector] tinted with [tint] at the idiom's default icon size.
 *
 * Pass `null` for [contentDescription] only when the icon is decorative or the parent already
 * describes it (ADR-021).
 */
@Composable
public fun Icon(
  imageVector: ImageVector,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  tint: Color = LocalContentColor.current,
  size: Dp = LocalIconSize.current.takeOrElse { DittoTheme.dimens.iconSize },
) {
  Icon(
    painter = rememberVectorPainter(imageVector),
    contentDescription = contentDescription,
    modifier = modifier,
    tint = tint,
    size = size,
  )
}

@Composable
public fun Icon(
  painter: Painter,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  tint: Color = LocalContentColor.current,
  size: Dp = LocalIconSize.current.takeOrElse { DittoTheme.dimens.iconSize },
) {
  val colorFilter = if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
  val semantics = if (contentDescription != null) {
    Modifier.semantics {
      this.contentDescription = contentDescription
      this.role = Role.Image
    }
  } else {
    Modifier
  }
  androidx.compose.foundation.layout.Box(
    modifier
      .then(semantics)
      .size(size)
      .paint(painter, colorFilter = colorFilter),
  )
}
