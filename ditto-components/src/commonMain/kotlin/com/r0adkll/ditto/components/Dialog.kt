package com.r0adkll.ditto.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog as ComposeDialog
import androidx.compose.ui.window.DialogProperties
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.LocalContentColor
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ElevationLevel

@Immutable
public class DialogStyle(
  public val shape: Shape,
  public val containerColor: Color,
  public val elevation: ElevationLevel,
  public val border: BorderStroke?,
  public val minWidth: Dp,
  public val maxWidth: Dp,
  public val contentPadding: PaddingValues,
  public val titleStyle: TextStyle,
  public val titleColor: Color,
  public val textStyle: TextStyle,
  public val textColor: Color,
  /** Apple: centered title/text and full-width buttons separated by hairlines. */
  public val centered: Boolean,
  public val iconColor: Color,
) {
  public fun copy(
    shape: Shape = this.shape,
    containerColor: Color = this.containerColor,
    elevation: ElevationLevel = this.elevation,
    border: BorderStroke? = this.border,
    minWidth: Dp = this.minWidth,
    maxWidth: Dp = this.maxWidth,
    contentPadding: PaddingValues = this.contentPadding,
    titleStyle: TextStyle = this.titleStyle,
    titleColor: Color = this.titleColor,
    textStyle: TextStyle = this.textStyle,
    textColor: Color = this.textColor,
    centered: Boolean = this.centered,
    iconColor: Color = this.iconColor,
  ): DialogStyle = DialogStyle(
    shape, containerColor, elevation, border, minWidth, maxWidth, contentPadding, titleStyle, titleColor, textStyle,
    textColor, centered, iconColor,
  )

  override fun equals(other: Any?): Boolean = other is DialogStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "DialogStyle(shape=$shape)"
  private fun fields(): List<Any?> = listOf(
    shape, containerColor, elevation, border, minWidth, maxWidth, contentPadding, titleStyle, titleColor, textStyle,
    textColor, centered, iconColor,
  )
}

public val LocalDialogStyle: ProvidableCompositionLocal<DialogStyle?> = staticCompositionLocalOf { null }

public object DialogDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): DialogStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val type = DittoTheme.typography
    val spacing = DittoTheme.spacing
    val dimens = DittoTheme.dimens
    return when (idiom) {
      Idiom.Android -> DialogStyle(
        shape = shapes.extraLarge,
        containerColor = colors.surfaceRaised,
        elevation = ElevationLevel.Level3,
        border = null,
        minWidth = 280.dp,
        maxWidth = 560.dp,
        contentPadding = PaddingValues(spacing.xl),
        titleStyle = type.heading,
        titleColor = colors.onSurface,
        textStyle = type.bodySmall,
        textColor = colors.onSurfaceVariant,
        centered = false,
        iconColor = colors.accent,
      )
      Idiom.Apple -> DialogStyle(
        shape = shapes.medium,
        containerColor = colors.surfaceRaised,
        elevation = ElevationLevel.Level4,
        border = null,
        minWidth = 270.dp,
        maxWidth = 270.dp,
        contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.xl),
        titleStyle = type.subheading,
        titleColor = colors.onSurface,
        textStyle = type.caption,
        textColor = colors.onSurface,
        centered = true,
        iconColor = colors.accent,
      )
      Idiom.Desktop -> DialogStyle(
        shape = shapes.large,
        containerColor = colors.surface,
        elevation = ElevationLevel.Level3,
        border = BorderStroke(dimens.borderWidth, colors.outlineVariant),
        minWidth = 320.dp,
        maxWidth = 512.dp,
        contentPadding = PaddingValues(spacing.xl),
        titleStyle = type.heading,
        titleColor = colors.onSurface,
        textStyle = type.bodySmall,
        textColor = colors.onSurfaceVariant,
        centered = false,
        iconColor = colors.accent,
      )
    }
  }

  @Composable
  @ReadOnlyComposable
  internal fun resolve(explicit: DialogStyle?): DialogStyle =
    explicit ?: LocalDialogStyle.current ?: DittoTheme.styleOverrides.resolve(style())
}

/** A modal container with idiom-appropriate surface, sizing and scrim. [content] is free-form. */
@Composable
public fun Dialog(
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  properties: DialogProperties = DialogProperties(),
  style: DialogStyle? = null,
  content: @Composable () -> Unit,
) {
  @Suppress("NAME_SHADOWING")
  val style = DialogDefaults.resolve(style)
  ComposeDialog(onDismissRequest = onDismissRequest, properties = properties) {
    DialogSurface(modifier, style) { content() }
  }
}

/**
 * A title / message / actions dialog. Android and Desktop right-align the buttons; Apple centers
 * the text and renders the buttons full-width, separated by hairlines.
 */
@Composable
public fun AlertDialog(
  onDismissRequest: () -> Unit,
  confirmButton: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  dismissButton: (@Composable () -> Unit)? = null,
  icon: (@Composable () -> Unit)? = null,
  title: String? = null,
  text: String? = null,
  properties: DialogProperties = DialogProperties(),
  style: DialogStyle? = null,
) {
  ComposeDialog(onDismissRequest = onDismissRequest, properties = properties) {
    AlertDialogContent(
      confirmButton = confirmButton,
      modifier = modifier,
      dismissButton = dismissButton,
      icon = icon,
      title = title,
      text = text,
      style = style,
    )
  }
}

/** The alert dialog's surface without the window, for previews and embedding. */
@Composable
public fun AlertDialogContent(
  confirmButton: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  dismissButton: (@Composable () -> Unit)? = null,
  icon: (@Composable () -> Unit)? = null,
  title: String? = null,
  text: String? = null,
  style: DialogStyle? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = DialogDefaults.resolve(style)
  val spacing = DittoTheme.spacing
  val align = if (style.centered) Alignment.CenterHorizontally else Alignment.Start
  val textAlign = if (style.centered) TextAlign.Center else TextAlign.Start

  DialogSurface(modifier, style, padContent = false) {
    Column {
      Column(Modifier.padding(style.contentPadding), horizontalAlignment = align) {
        if (icon != null) {
          CompositionLocalProvider(LocalContentColor provides style.iconColor) { Box { icon() } }
          Spacer(Modifier.height(spacing.lg))
        }
        if (title != null) {
          Text(title, style = style.titleStyle, color = style.titleColor, textAlign = textAlign)
          if (text != null) Spacer(Modifier.height(if (style.centered) spacing.xs else spacing.lg))
        }
        if (text != null) {
          Text(text, style = style.textStyle, color = style.textColor, textAlign = textAlign)
        }
      }
      if (style.centered) {
        AppleActions(confirmButton, dismissButton)
      } else {
        Row(
          Modifier.fillMaxWidth().padding(
            start = spacing.xl,
            end = spacing.xl,
            bottom = spacing.xl,
          ),
          horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.End),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          if (dismissButton != null) dismissButton()
          confirmButton()
        }
      }
    }
  }
}

@Composable
private fun AppleActions(confirmButton: @Composable () -> Unit, dismissButton: (@Composable () -> Unit)?) {
  val stretch = ButtonDefaults.style(ButtonVariant.Text).copy(
    shape = DittoTheme.shapes.none,
    minHeight = 44.dp,
    contentPadding = PaddingValues(0.dp),
  )
  HorizontalDivider()
  CompositionLocalProvider(LocalButtonStyles provides ButtonStyles(text = stretch, filled = stretch, tonal = stretch, outlined = stretch)) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
      if (dismissButton != null) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { dismissButton() }
        VerticalDivider()
      }
      Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { confirmButton() }
    }
  }
}

@Composable
private fun DialogSurface(
  modifier: Modifier,
  style: DialogStyle,
  padContent: Boolean = true,
  content: @Composable () -> Unit,
) {
  Surface(
    modifier = modifier.widthIn(min = style.minWidth, max = style.maxWidth).width(IntrinsicSize.Max),
    shape = style.shape,
    color = style.containerColor,
    elevation = style.elevation,
    border = style.border,
  ) {
    if (padContent) Box(Modifier.padding(style.contentPadding)) { content() } else content()
  }
}
