package com.r0adkll.ditto.catalog.brand

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** Ditto's brand: the mascot and the purple it is drawn in. See the vault's `Design/Brand.md`. */
object DittoBrand {
  /** The Ditto palette (pokemonpalette.com/ditto), lightest to darkest. */
  val PaleMagenta: Color = Color(0xFFE2A7F4)
  val LightViolet: Color = Color(0xFFC3ACD7)

  /** `#C57BE6` — the mascot's fill and the brand colour. Too light to carry text on a light surface. */
  val BrandViolet: Color = Color(0xFFC57BE6)

  /** `#9C5AB4` — the palette's lead violet and Ditto's default accent; passes contrast in both modes. */
  val Violet: Color = Color(0xFF9C5AB4)

  val DarkViolet: Color = Color(0xFF5A1894)
  val DeepViolet: Color = Color(0xFF3A155B)

  private const val BLOB =
    "M8.76269 67.8076H71.2389C76.1913 67.8076 80.0008 63.9981 80.0008 59.0457V9.90285C80.0008 1.90285 70.8579 -2.66858 " +
      "64.7627 2.2838L42.2865 14.4743C41.1436 15.2362 39.2389 15.2362 38.096 14.4743L15.6198 1.90285C9.14364 -2.66858 " +
      "0.381736 1.5219 0.381736 9.5219V58.6648C0.000783923 63.6171 3.81031 67.8076 8.76269 67.8076Z"
  private const val VISOR =
    "M65.5248 61.3364H14.0962C9.90572 61.3364 6.09619 57.9079 6.09619 53.3364V31.2412C6.09619 27.0507 9.52476 23.2412 " +
      "14.0962 23.2412H65.5248C69.7152 23.2412 73.5248 26.6698 73.5248 31.2412V53.3364C73.5248 57.9079 70.0962 61.3364 " +
      "65.5248 61.3364Z"

  private fun circle(cx: Float, cy: Float, r: Float) =
    "M${cx - r},$cy a$r,$r 0 1,0 ${r * 2},0 a$r,$r 0 1,0 ${-r * 2},0Z"

  /**
   * The mascot. [bodyColor] fills the blob, [visorColor] the face plate and [eyeColor] the eyes,
   * so the logo can be drawn monochrome (e.g. all one colour on a busy header) when needed.
   */
  fun mascot(
    bodyColor: Color = BrandViolet,
    visorColor: Color = Color.Black,
    eyeColor: Color = Color.White,
  ): ImageVector = ImageVector.Builder(
    name = "DittoMascot",
    defaultWidth = 80.dp,
    defaultHeight = 71.dp,
    viewportWidth = 80f,
    viewportHeight = 71f,
  )
    .addPath(addPathNodes(BLOB), fill = SolidColor(bodyColor), pathFillType = PathFillType.NonZero)
    .addPath(addPathNodes(VISOR), fill = SolidColor(visorColor), pathFillType = PathFillType.NonZero)
    .addPath(addPathNodes(circle(24.7621f, 40.0004f, 5.71429f)), fill = SolidColor(eyeColor))
    .addPath(addPathNodes(circle(55.2382f, 40.0004f, 5.71429f)), fill = SolidColor(eyeColor))
    .build()
}
