package com.r0adkll.ditto.icons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.LocalIdiom

/**
 * The tiny set of system glyphs components need themselves (ADR-015, ADR-024), resolved per
 * idiom: filled Material-style glyphs on Android, stroked glyphs on Apple and Desktop (Apple a
 * touch heavier). App icons belong in a separate icon module, not here.
 */
public object DittoIcons {
  public val back: ImageVector @Composable @ReadOnlyComposable get() = back(LocalIdiom.current)
  public val forward: ImageVector @Composable @ReadOnlyComposable get() = forward(LocalIdiom.current)
  public val chevronDown: ImageVector @Composable @ReadOnlyComposable get() = chevronDown(LocalIdiom.current)
  public val chevronRight: ImageVector @Composable @ReadOnlyComposable get() = chevronRight(LocalIdiom.current)
  public val close: ImageVector @Composable @ReadOnlyComposable get() = close(LocalIdiom.current)
  public val check: ImageVector @Composable @ReadOnlyComposable get() = check(LocalIdiom.current)
  public val clear: ImageVector @Composable @ReadOnlyComposable get() = clear(LocalIdiom.current)
  public val more: ImageVector @Composable @ReadOnlyComposable get() = more(LocalIdiom.current)
  public val search: ImageVector @Composable @ReadOnlyComposable get() = search(LocalIdiom.current)
  public val dropdown: ImageVector @Composable @ReadOnlyComposable get() = dropdown(LocalIdiom.current)
  public val visibility: ImageVector @Composable @ReadOnlyComposable get() = visibility(LocalIdiom.current)
  public val visibilityOff: ImageVector @Composable @ReadOnlyComposable get() = visibilityOff(LocalIdiom.current)
  public val indeterminate: ImageVector @Composable @ReadOnlyComposable get() = indeterminate(LocalIdiom.current)

  public fun back(idiom: Idiom): ImageVector = cached("back", idiom) {
    when (idiom) {
      Idiom.Android -> filled("back", "M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z")
      Idiom.Apple -> stroked("back", idiom, "M15 18l-6-6 6-6")
      Idiom.Desktop -> stroked("back", idiom, "M19 12H5", "M12 19l-7-7 7-7")
    }
  }

  public fun forward(idiom: Idiom): ImageVector = cached("forward", idiom) {
    when (idiom) {
      Idiom.Android -> filled("forward", "M12 4l-1.41 1.41L16.17 11H4v2h12.17l-5.58 5.59L12 20l8-8z")
      Idiom.Apple -> stroked("forward", idiom, "M9 18l6-6-6-6")
      Idiom.Desktop -> stroked("forward", idiom, "M5 12h14", "M12 5l7 7-7 7")
    }
  }

  public fun chevronDown(idiom: Idiom): ImageVector = cached("chevronDown", idiom) {
    when (idiom) {
      Idiom.Android -> filled("chevronDown", "M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z")
      else -> stroked("chevronDown", idiom, "M6 9l6 6 6-6")
    }
  }

  public fun chevronRight(idiom: Idiom): ImageVector = cached("chevronRight", idiom) {
    when (idiom) {
      Idiom.Android -> filled("chevronRight", "M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z")
      else -> stroked("chevronRight", idiom, "M9 18l6-6-6-6")
    }
  }

  public fun close(idiom: Idiom): ImageVector = cached("close", idiom) {
    when (idiom) {
      Idiom.Android -> filled("close", "M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z")
      else -> stroked("close", idiom, "M18 6L6 18", "M6 6l12 12")
    }
  }

  public fun check(idiom: Idiom): ImageVector = cached("check", idiom) {
    when (idiom) {
      Idiom.Android -> filled("check", "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z")
      else -> stroked("check", idiom, "M20 6L9 17l-5-5")
    }
  }

  public fun clear(idiom: Idiom): ImageVector = cached("clear", idiom) {
    when (idiom) {
      Idiom.Android -> filled(
        "clear",
        "M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z",
      )
      else -> stroked("clear", idiom, "M12 2a10 10 0 1 0 0 20 10 10 0 1 0 0-20z", "M15 9l-6 6", "M9 9l6 6")
    }
  }

  public fun more(idiom: Idiom): ImageVector = cached("more", idiom) {
    when (idiom) {
      Idiom.Android -> filled(
        "more",
        "M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z",
      )
      else -> filled(
        "more",
        "M13.5 12a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zM20.5 12a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zM6.5 12a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z",
      )
    }
  }

  public fun search(idiom: Idiom): ImageVector = cached("search", idiom) {
    when (idiom) {
      Idiom.Android -> filled(
        "search",
        "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z",
      )
      else -> stroked("search", idiom, "M11 3a8 8 0 1 0 0 16 8 8 0 1 0 0-16z", "M21 21l-4.35-4.35")
    }
  }

  public fun dropdown(idiom: Idiom): ImageVector = cached("dropdown", idiom) {
    when (idiom) {
      Idiom.Android -> filled("dropdown", "M7 10l5 5 5-5z")
      Idiom.Apple -> stroked("dropdown", idiom, "M7 9l5 5 5-5")
      Idiom.Desktop -> stroked("dropdown", idiom, "M7 15l5 5 5-5", "M7 9l5-5 5 5")
    }
  }

  public fun visibility(idiom: Idiom): ImageVector = cached("visibility", idiom) {
    when (idiom) {
      Idiom.Android -> filled(
        "visibility",
        "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z",
      )
      else -> stroked("visibility", idiom, "M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z", "M12 9a3 3 0 1 0 0 6 3 3 0 1 0 0-6z")
    }
  }

  public fun visibilityOff(idiom: Idiom): ImageVector = cached("visibilityOff", idiom) {
    when (idiom) {
      Idiom.Android -> filled(
        "visibilityOff",
        "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z",
      )
      else -> stroked(
        "visibilityOff", idiom,
        "M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94",
        "M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19",
        "M14.12 14.12a3 3 0 1 1-4.24-4.24",
        "M1 1l22 22",
      )
    }
  }

  public fun indeterminate(idiom: Idiom): ImageVector = cached("indeterminate", idiom) {
    when (idiom) {
      Idiom.Android -> filled("indeterminate", "M19 13H5v-2h14v2z")
      else -> stroked("indeterminate", idiom, "M5 12h14")
    }
  }

  private val cache = HashMap<String, ImageVector>()

  private inline fun cached(name: String, idiom: Idiom, build: () -> ImageVector): ImageVector {
    val key = "$name/$idiom"
    return cache[key] ?: build().also { cache[key] = it }
  }

  private fun filled(name: String, pathData: String): ImageVector =
    ImageVector.Builder(
      name = "ditto.$name",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 24f,
      viewportHeight = 24f,
    ).addPath(
      pathData = addPathNodes(pathData),
      pathFillType = PathFillType.NonZero,
      fill = SolidColor(Color.Black),
    ).build()

  private fun stroked(name: String, idiom: Idiom, vararg pathData: String): ImageVector {
    val width = if (idiom == Idiom.Apple) 2.25f else 2f
    val builder = ImageVector.Builder(
      name = "ditto.$name",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 24f,
      viewportHeight = 24f,
    )
    pathData.forEach { d ->
      builder.addPath(
        pathData = addPathNodes(d),
        stroke = SolidColor(Color.Black),
        strokeLineWidth = width,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
      )
    }
    return builder.build()
  }
}
