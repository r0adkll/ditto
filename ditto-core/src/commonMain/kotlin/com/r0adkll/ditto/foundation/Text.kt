package com.r0adkll.ditto.foundation

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

/**
 * Ditto's text primitive. Uses the ambient [LocalTextStyle] and [LocalContentColor] unless a
 * [style] or [color] is given.
 */
@Composable
public fun Text(
  text: String,
  modifier: Modifier = Modifier,
  color: Color = Color.Unspecified,
  style: TextStyle = LocalTextStyle.current,
  textAlign: TextAlign? = null,
  overflow: TextOverflow = TextOverflow.Clip,
  softWrap: Boolean = true,
  maxLines: Int = Int.MAX_VALUE,
  minLines: Int = 1,
  onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
  Text(
    text = AnnotatedString(text),
    modifier = modifier,
    color = color,
    style = style,
    textAlign = textAlign,
    overflow = overflow,
    softWrap = softWrap,
    maxLines = maxLines,
    minLines = minLines,
    onTextLayout = onTextLayout,
  )
}

@Composable
public fun Text(
  text: AnnotatedString,
  modifier: Modifier = Modifier,
  color: Color = Color.Unspecified,
  style: TextStyle = LocalTextStyle.current,
  textAlign: TextAlign? = null,
  overflow: TextOverflow = TextOverflow.Clip,
  softWrap: Boolean = true,
  maxLines: Int = Int.MAX_VALUE,
  minLines: Int = 1,
  inlineContent: Map<String, InlineTextContent> = emptyMap(),
  onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
  val resolvedColor = when {
    color != Color.Unspecified -> color
    style.color != Color.Unspecified -> style.color
    else -> LocalContentColor.current
  }
  val merged = style.merge(
    color = resolvedColor,
    textAlign = textAlign ?: TextAlign.Unspecified,
  )
  BasicText(
    text = text,
    modifier = modifier,
    style = merged,
    onTextLayout = onTextLayout,
    overflow = overflow,
    softWrap = softWrap,
    maxLines = maxLines,
    minLines = minLines,
    inlineContent = inlineContent,
  )
}
