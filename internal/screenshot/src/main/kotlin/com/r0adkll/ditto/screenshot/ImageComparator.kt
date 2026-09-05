package com.r0adkll.ditto.screenshot

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Surface
import kotlin.math.abs

/**
 * Neighborhood-tolerant comparison, modeled on Compose Hot Reload's screenshot check: every
 * pixel in [actual] must find a pixel within [radius] in [expected] whose channels are all within
 * [colorTolerance]. This forgives anti-aliasing shifts and fails on real layout changes.
 */
public class ImageComparator(
  private val colorTolerance: Float = 0.02f,
  private val radius: Int = 2,
  /** Fraction of pixels allowed to mismatch before the comparison fails. */
  private val maxMismatchFraction: Float = 0.0005f,
) {
  public class Result(public val matches: Boolean, public val description: String, public val diff: Image?)

  public fun compare(expected: Image, actual: Image): Result {
    if (expected.width != actual.width || expected.height != actual.height) {
      return Result(false, "size ${actual.width}x${actual.height} != golden ${expected.width}x${expected.height}", null)
    }
    val e = expected.pixels()
    val a = actual.pixels()
    val w = expected.width
    val h = expected.height
    val diff = Bitmap().apply { allocPixels(ImageInfo(w, h, ColorType.RGBA_8888, ColorAlphaType.PREMUL)) }
    var mismatches = 0
    val tol = (colorTolerance * 255).toInt()
    for (y in 0 until h) {
      for (x in 0 until w) {
        val i = (y * w + x) * 4
        val matched = neighborhoodMatch(e, a, i, x, y, w, h, tol)
        if (!matched) mismatches++
        diff.erase(if (matched) Color.makeARGB(255, 255, 255, 255) else Color.makeARGB(255, 255, 0, 0), org.jetbrains.skia.IRect.makeXYWH(x, y, 1, 1))
      }
    }
    val fraction = mismatches.toFloat() / (w * h)
    val ok = fraction <= maxMismatchFraction
    return Result(ok, "$mismatches mismatching pixels (${(fraction * 100)}%)", if (ok) null else Image.makeFromBitmap(diff))
  }

  private fun neighborhoodMatch(e: ByteArray, a: ByteArray, i: Int, x: Int, y: Int, w: Int, h: Int, tol: Int): Boolean {
    if (channelsMatch(e, a, i, i, tol)) return true
    for (dy in -radius..radius) {
      for (dx in -radius..radius) {
        val nx = x + dx
        val ny = y + dy
        if (nx < 0 || ny < 0 || nx >= w || ny >= h) continue
        if (channelsMatch(e, a, (ny * w + nx) * 4, i, tol)) return true
      }
    }
    return false
  }

  private fun channelsMatch(e: ByteArray, a: ByteArray, ei: Int, ai: Int, tol: Int): Boolean {
    for (c in 0 until 4) {
      if (abs((e[ei + c].toInt() and 0xFF) - (a[ai + c].toInt() and 0xFF)) > tol) return false
    }
    return true
  }

  /** Decoded PNGs are lazy; drawing into a raster surface gives both images identical pixel layouts. */
  private fun Image.pixels(): ByteArray {
    val surface = Surface.makeRaster(ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.UNPREMUL))
    surface.canvas.drawImage(this, 0f, 0f)
    val bitmap = Bitmap().apply { allocPixels(surface.imageInfo) }
    check(surface.readPixels(bitmap, 0, 0)) { "readPixels failed for ${width}x$height image" }
    return bitmap.readPixels() ?: error("no pixels")
  }
}
