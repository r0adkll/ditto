package com.r0adkll.ditto.catalog.brand

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Glyphs the catalog's own chrome needs that Ditto itself does not ship. ADR-015 keeps the core
 * icon set to glyphs components draw themselves — a checkmark, a chevron — so app-level pictograms
 * like these live with the app that uses them. Drawn in the same stroked style as the Desktop
 * idiom's icons, because the demo chrome is always Desktop.
 */
internal object CatalogIcons {
  /** Shown while the panel is dark: tapping it turns the lights on. */
  val sun: ImageVector = stroked(
    "sun",
    "M12 8a4 4 0 1 0 0 8 4 4 0 1 0 0-8z",
    "M12 2v2", "M12 20v2", "M4.93 4.93l1.41 1.41", "M17.66 17.66l1.41 1.41",
    "M2 12h2", "M20 12h2", "M6.34 17.66l-1.41 1.41", "M19.07 4.93l-1.41 1.41",
  )

  /** Shown while the panel is light: tapping it turns the lights off. */
  val moon: ImageVector = stroked("moon", "M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9z")

  private fun stroked(name: String, vararg pathData: String): ImageVector {
    val builder = ImageVector.Builder(
      name = "catalog.$name",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 24f,
      viewportHeight = 24f,
    )
    pathData.forEach { d ->
      builder.addPath(
        pathData = addPathNodes(d),
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
      )
    }
    return builder.build()
  }
}
