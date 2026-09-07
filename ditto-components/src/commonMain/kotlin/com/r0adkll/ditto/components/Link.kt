package com.r0adkll.ditto.components

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isUnspecified
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.LocalTextStyle
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.theme.DittoTheme

/** How a link marks itself as one, beyond its colour. */
public enum class LinkUnderline {
  /** Underlined at rest — the Material convention. */
  Always,

  /** Colour only, as on iOS. */
  Never,

  /** Underlined under the pointer, as desktop UIs do. */
  OnHover,
}

/**
 * How links are drawn, both standalone ([Link]) and inline inside an `AnnotatedString`.
 *
 * There is no size or shape here on purpose: a link is text, so it takes the metrics of whatever
 * it is written into. [textStyle] is `null` in the defaults, meaning "inherit `LocalTextStyle`".
 */
@Immutable
public class LinkStyle(
  public val textStyle: TextStyle?,
  public val color: Color,
  public val disabledColor: Color,
  public val underline: LinkUnderline,
) {
  public fun copy(
    textStyle: TextStyle? = this.textStyle,
    color: Color = this.color,
    disabledColor: Color = this.disabledColor,
    underline: LinkUnderline = this.underline,
  ): LinkStyle = LinkStyle(textStyle, color, disabledColor, underline)

  override fun equals(other: Any?): Boolean = other is LinkStyle &&
    textStyle == other.textStyle && color == other.color &&
    disabledColor == other.disabledColor && underline == other.underline

  override fun hashCode(): Int = listOf(textStyle, color, disabledColor, underline).hashCode()
  override fun toString(): String = "LinkStyle(underline=$underline)"
}

public val LocalLinkStyle: ProvidableCompositionLocal<LinkStyle?> = staticCompositionLocalOf { null }

public object LinkDefaults {
  /** The idiom default, before any [LocalLinkStyle] or override. */
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): LinkStyle {
    val colors = DittoTheme.colors
    return LinkStyle(
      textStyle = null,
      color = colors.accent,
      disabledColor = colors.onSurface.copy(alpha = colors.disabledAlpha),
      underline = when (idiom) {
        Idiom.Android -> LinkUnderline.Always
        Idiom.Apple -> LinkUnderline.Never
        Idiom.Desktop -> LinkUnderline.OnHover
      },
    )
  }

  /** Explicit [style] > [LocalLinkStyle] > the idiom default with app overrides applied. */
  @Composable
  @ReadOnlyComposable
  public fun resolve(style: LinkStyle? = null): LinkStyle =
    style ?: LocalLinkStyle.current ?: DittoTheme.styleOverrides.resolve(style())

  /**
   * The idiom's link styling as [TextLinkStyles], for a link written **inside** an
   * `AnnotatedString` — which is where most links live:
   *
   * ```
   * Text(
   *   buildAnnotatedString {
   *     append("By continuing you agree to the ")
   *     withLink(LinkAnnotation.Url(termsUrl, LinkDefaults.textLinkStyles())) { append("Terms") }
   *   },
   * )
   * ```
   *
   * `Text` hands these to the platform's own link handling, so the link carries link semantics and
   * opens through the platform URI handler rather than being a click listener on some text.
   */
  @Composable
  @ReadOnlyComposable
  public fun textLinkStyles(style: LinkStyle? = null): TextLinkStyles {
    val resolved = resolve(style)
    val rest = SpanStyle(
      color = resolved.color,
      textDecoration = if (resolved.underline == LinkUnderline.Always) TextDecoration.Underline else TextDecoration.None,
    )
    val underlined = rest.copy(textDecoration = TextDecoration.Underline)
    return TextLinkStyles(
      style = rest,
      // Focus underlines in every idiom, including the one that never underlines otherwise: a
      // focus indicator that is only a colour change is not an indicator.
      focusedStyle = underlined,
      hoveredStyle = if (resolved.underline == LinkUnderline.Never) rest else underlined,
      pressedStyle = rest.copy(color = resolved.color.copy(alpha = 0.7f)),
    )
  }
}

// The inline slot for the external arrow. Its *alternate* text is a plain space on purpose: that
// string lands in the node's semantics text, and "↗" there is read aloud as the name of the glyph.
// The meaning travels in the icon's contentDescription instead.
private const val EXTERNAL_MARKER = "ditto.link.external"

/**
 * A standalone link: a whole string that navigates.
 *
 * This is the *block* case — a "Learn more" on its own line. A link **inside a sentence** is not
 * this component; build it with [LinkDefaults.textLinkStyles] and `withLink`, which is the same
 * styling through the same mechanism.
 *
 * Built on [LinkAnnotation] rather than a clickable `Row` so the platform sees a link. Compose has
 * no `Role.Link`, so a hand-rolled one can only announce itself as a button.
 */
@Composable
public fun Link(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  external: Boolean = false,
  style: LinkStyle? = null,
) {
  val resolved = LinkDefaults.resolve(style)
  val textStyle = resolved.textStyle ?: LocalTextStyle.current
  val linkStyles = LinkDefaults.textLinkStyles(resolved)

  val annotated = buildAnnotatedString {
    if (enabled) {
      // The listener is what makes this a Clickable rather than a Url: the caller decides what
      // navigating means, and the arrow sits inside the link so it underlines and clicks with it.
      withLink(LinkAnnotation.Clickable(tag = text, styles = linkStyles, linkInteractionListener = { onClick() })) {
        append(text)
        if (external) appendInlineContent(EXTERNAL_MARKER, " ")
      }
    } else {
      // A disabled link is not a link: no annotation, so nothing to focus or activate.
      withStyle(SpanStyle(color = resolved.disabledColor)) {
        append(text)
        if (external) appendInlineContent(EXTERNAL_MARKER, " ")
      }
    }
  }

  val arrowColor = if (enabled) resolved.color else resolved.disabledColor
  // The glyph is sized from the text it follows rather than the ambient icon size, which is a
  // control-sized 16–24dp and would tower over a caption.
  val fontSize = textStyle.fontSize
  val arrowSize = with(LocalDensity.current) { if (fontSize.isUnspecified) 14.dp else fontSize.toDp() }
  Text(
    text = annotated,
    modifier = modifier,
    style = textStyle,
    inlineContent = if (!external) {
      emptyMap()
    } else {
      mapOf(
        EXTERNAL_MARKER to InlineTextContent(
          // Sized in em so the glyph tracks the text it follows, at any font scale.
          Placeholder(width = 1.05.em, height = 1.05.em, placeholderVerticalAlign = PlaceholderVerticalAlign.Center),
        ) {
          Icon(DittoIcons.external, contentDescription = "opens externally", tint = arrowColor, size = arrowSize)
        },
      )
    },
  )
}
