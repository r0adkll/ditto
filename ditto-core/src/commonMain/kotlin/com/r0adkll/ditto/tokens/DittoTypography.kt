package com.r0adkll.ditto.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.r0adkll.ditto.Idiom

/**
 * The eight-style type scale (ADR-009). Idioms map these onto their native families, sizes and
 * weights; apps rarely need more than this.
 */
@Immutable
public class DittoTypography(
  public val display: TextStyle,
  public val title: TextStyle,
  public val heading: TextStyle,
  public val subheading: TextStyle,
  public val body: TextStyle,
  public val bodySmall: TextStyle,
  public val label: TextStyle,
  public val caption: TextStyle,
) {
  public fun copy(
    display: TextStyle = this.display,
    title: TextStyle = this.title,
    heading: TextStyle = this.heading,
    subheading: TextStyle = this.subheading,
    body: TextStyle = this.body,
    bodySmall: TextStyle = this.bodySmall,
    label: TextStyle = this.label,
    caption: TextStyle = this.caption,
  ): DittoTypography = DittoTypography(display, title, heading, subheading, body, bodySmall, label, caption)

  /** Applies [transform] to every style, e.g. to swap the font family or platform text style. */
  public fun map(transform: (TextStyle) -> TextStyle): DittoTypography = DittoTypography(
    transform(display), transform(title), transform(heading), transform(subheading),
    transform(body), transform(bodySmall), transform(label), transform(caption),
  )

  override fun equals(other: Any?): Boolean = other is DittoTypography &&
    display == other.display && title == other.title && heading == other.heading &&
    subheading == other.subheading && body == other.body && bodySmall == other.bodySmall &&
    label == other.label && caption == other.caption

  override fun hashCode(): Int = listOf(display, title, heading, subheading, body, bodySmall, label, caption).hashCode()
  override fun toString(): String = "DittoTypography(body=$body)"

  public companion object {
    /**
     * Default scale for [idiom]. All idioms use [fontFamily] (system default unless given), differing
     * in size, weight and tracking (ADR-026).
     */
    public fun forIdiom(
      idiom: Idiom,
      fontFamily: FontFamily = FontFamily.Default,
      density: DittoDensity = DittoDensity.forIdiom(idiom),
    ): DittoTypography = when (idiom) {
      Idiom.Android -> DittoTypography(
        display = style(fontFamily, 45.sp, 52.sp, FontWeight.Normal, 0.sp),
        title = style(fontFamily, 28.sp, 36.sp, FontWeight.Normal, 0.sp),
        heading = style(fontFamily, 22.sp, 28.sp, FontWeight.Medium, 0.sp),
        subheading = style(fontFamily, 16.sp, 24.sp, FontWeight.Medium, 0.15.sp),
        body = style(fontFamily, 16.sp, 24.sp, FontWeight.Normal, 0.5.sp),
        bodySmall = style(fontFamily, 14.sp, 20.sp, FontWeight.Normal, 0.25.sp),
        label = style(fontFamily, 14.sp, 20.sp, FontWeight.Medium, 0.1.sp),
        caption = style(fontFamily, 12.sp, 16.sp, FontWeight.Normal, 0.4.sp),
      )
      Idiom.Apple -> DittoTypography(
        display = style(fontFamily, 34.sp, 41.sp, FontWeight.Bold, 0.37.sp),
        title = style(fontFamily, 28.sp, 34.sp, FontWeight.Bold, 0.36.sp),
        heading = style(fontFamily, 22.sp, 28.sp, FontWeight.SemiBold, 0.35.sp),
        subheading = style(fontFamily, 17.sp, 22.sp, FontWeight.SemiBold, (-0.41).sp),
        body = style(fontFamily, 17.sp, 22.sp, FontWeight.Normal, (-0.41).sp),
        bodySmall = style(fontFamily, 15.sp, 20.sp, FontWeight.Normal, (-0.24).sp),
        label = style(fontFamily, 17.sp, 22.sp, FontWeight.Medium, (-0.41).sp),
        caption = style(fontFamily, 12.sp, 16.sp, FontWeight.Normal, 0.sp),
      )
      Idiom.Desktop -> if (density == DittoDensity.Compact) {
        // Int UI-class density: 13sp body, small 11, headings 16/22/25.
        DittoTypography(
          display = style(fontFamily, 25.sp, 30.sp, FontWeight.Bold, (-0.25).sp),
          title = style(fontFamily, 22.sp, 28.sp, FontWeight.Bold, (-0.25).sp),
          heading = style(fontFamily, 16.sp, 22.sp, FontWeight.SemiBold, 0.sp),
          subheading = style(fontFamily, 14.sp, 20.sp, FontWeight.SemiBold, 0.sp),
          body = style(fontFamily, 13.sp, 18.sp, FontWeight.Normal, 0.sp),
          bodySmall = style(fontFamily, 12.sp, 16.sp, FontWeight.Normal, 0.sp),
          label = style(fontFamily, 13.sp, 18.sp, FontWeight.Medium, 0.sp),
          caption = style(fontFamily, 11.sp, 14.sp, FontWeight.Normal, 0.sp),
        )
      } else DittoTypography(
        display = style(fontFamily, 36.sp, 40.sp, FontWeight.SemiBold, (-0.5).sp),
        title = style(fontFamily, 24.sp, 32.sp, FontWeight.SemiBold, (-0.25).sp),
        heading = style(fontFamily, 18.sp, 26.sp, FontWeight.SemiBold, 0.sp),
        subheading = style(fontFamily, 15.sp, 22.sp, FontWeight.Medium, 0.sp),
        body = style(fontFamily, 14.sp, 20.sp, FontWeight.Normal, 0.sp),
        bodySmall = style(fontFamily, 13.sp, 18.sp, FontWeight.Normal, 0.sp),
        label = style(fontFamily, 14.sp, 20.sp, FontWeight.Medium, 0.sp),
        caption = style(fontFamily, 12.sp, 16.sp, FontWeight.Normal, 0.sp),
      )
    }

    private fun style(
      family: FontFamily,
      size: TextUnit,
      lineHeight: TextUnit,
      weight: FontWeight,
      letterSpacing: TextUnit,
    ): TextStyle = TextStyle(
      fontFamily = family,
      fontSize = size,
      lineHeight = lineHeight,
      fontWeight = weight,
      letterSpacing = letterSpacing,
    )
  }
}
