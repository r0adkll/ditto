package com.r0adkll.ditto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.LocalContentColor
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.theme.DittoTheme

@Immutable
public class TextFieldStyle(
  public val shape: Shape,
  public val minHeight: Dp,
  public val contentPadding: PaddingValues,
  public val containerColor: Color,
  public val borderWidth: Dp,
  public val borderColor: Color,
  public val focusedBorderWidth: Dp,
  public val focusedBorderColor: Color,
  public val errorColor: Color,
  public val textStyle: TextStyle,
  public val textColor: Color,
  public val placeholderColor: Color,
  public val labelStyle: TextStyle,
  public val labelColor: Color,
  public val supportingStyle: TextStyle,
  public val supportingColor: Color,
  public val cursorColor: Color,
  public val iconColor: Color,
  public val iconSpacing: Dp,
) {
  public fun copy(
    shape: Shape = this.shape,
    minHeight: Dp = this.minHeight,
    contentPadding: PaddingValues = this.contentPadding,
    containerColor: Color = this.containerColor,
    borderWidth: Dp = this.borderWidth,
    borderColor: Color = this.borderColor,
    focusedBorderWidth: Dp = this.focusedBorderWidth,
    focusedBorderColor: Color = this.focusedBorderColor,
    errorColor: Color = this.errorColor,
    textStyle: TextStyle = this.textStyle,
    textColor: Color = this.textColor,
    placeholderColor: Color = this.placeholderColor,
    labelStyle: TextStyle = this.labelStyle,
    labelColor: Color = this.labelColor,
    supportingStyle: TextStyle = this.supportingStyle,
    supportingColor: Color = this.supportingColor,
    cursorColor: Color = this.cursorColor,
    iconColor: Color = this.iconColor,
    iconSpacing: Dp = this.iconSpacing,
  ): TextFieldStyle = TextFieldStyle(
    shape, minHeight, contentPadding, containerColor, borderWidth, borderColor, focusedBorderWidth, focusedBorderColor,
    errorColor, textStyle, textColor, placeholderColor, labelStyle, labelColor, supportingStyle, supportingColor,
    cursorColor, iconColor, iconSpacing,
  )

  override fun equals(other: Any?): Boolean = other is TextFieldStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "TextFieldStyle(shape=$shape, minHeight=$minHeight)"

  private fun fields(): List<Any?> = listOf(
    shape, minHeight, contentPadding, containerColor, borderWidth, borderColor, focusedBorderWidth, focusedBorderColor,
    errorColor, textStyle, textColor, placeholderColor, labelStyle, labelColor, supportingStyle, supportingColor,
    cursorColor, iconColor, iconSpacing,
  )
}

public val LocalTextFieldStyle: ProvidableCompositionLocal<TextFieldStyle?> = staticCompositionLocalOf { null }

public object TextFieldDefaults {
  /**
   * Android: outlined, 56dp, border thickens to accent on focus. Apple: filled cell, 44dp, no border.
   * Desktop: 36dp bordered input; the keyboard focus ring does the focus work (ADR-023).
   */
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): TextFieldStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val type = DittoTheme.typography
    val spacing = DittoTheme.spacing
    val base = TextFieldStyle(
      shape = shapes.small,
      minHeight = 36.dp,
      contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
      containerColor = colors.surface,
      borderWidth = DittoTheme.dimens.borderWidth,
      borderColor = colors.outlineVariant,
      focusedBorderWidth = DittoTheme.dimens.borderWidth,
      focusedBorderColor = colors.accent,
      errorColor = colors.error,
      textStyle = type.body,
      textColor = colors.onSurface,
      placeholderColor = colors.onSurfaceVariant,
      labelStyle = type.label,
      labelColor = colors.onSurface,
      supportingStyle = type.caption,
      supportingColor = colors.onSurfaceVariant,
      cursorColor = colors.accent,
      iconColor = colors.onSurfaceVariant,
      iconSpacing = spacing.sm,
    )
    return when (idiom) {
      Idiom.Android -> base.copy(
        shape = shapes.extraSmall,
        minHeight = 56.dp,
        contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
        containerColor = Color.Transparent,
        borderColor = colors.outline,
        focusedBorderWidth = 2.dp,
        labelStyle = type.bodySmall,
        labelColor = colors.onSurfaceVariant,
        iconSpacing = spacing.md,
      )
      Idiom.Apple -> base.copy(
        shape = shapes.small,
        minHeight = 44.dp,
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        containerColor = colors.surfaceOverlay,
        borderWidth = 0.dp,
        borderColor = Color.Transparent,
        focusedBorderWidth = 0.dp,
        focusedBorderColor = Color.Transparent,
        labelStyle = type.subheading,
      )
      Idiom.Desktop -> base
    }
  }

  @Composable
  @ReadOnlyComposable
  internal fun resolve(explicit: TextFieldStyle?): TextFieldStyle = explicit ?: LocalTextFieldStyle.current ?: style()
}

/**
 * Single- or multi-line text input driven by a [TextFieldState] (the primary API, ADR-024).
 *
 * [label] renders above the field in every idiom, [placeholder] inside it while empty, and
 * [supportingText] below; [isError] recolors the border and supporting text.
 */
@Composable
public fun TextField(
  state: TextFieldState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  label: String? = null,
  placeholder: String? = null,
  supportingText: String? = null,
  isError: Boolean = false,
  leadingIcon: (@Composable () -> Unit)? = null,
  trailingIcon: (@Composable () -> Unit)? = null,
  lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  onKeyboardAction: KeyboardActionHandler? = null,
  inputTransformation: InputTransformation? = null,
  outputTransformation: OutputTransformation? = null,
  style: TextFieldStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = TextFieldDefaults.resolve(style)
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val textColor = if (enabled) style.textColor else style.textColor.copy(alpha = DittoTheme.colors.disabledAlpha)

  ProvideSelectionColors(style) {
    BasicTextField(
      state = state,
      modifier = modifier,
      enabled = enabled,
      readOnly = readOnly,
      inputTransformation = inputTransformation,
      textStyle = style.textStyle.copy(color = textColor),
      keyboardOptions = keyboardOptions,
      onKeyboardAction = onKeyboardAction,
      lineLimits = lineLimits,
      interactionSource = interactionSource,
      cursorBrush = SolidColor(style.cursorColor),
      outputTransformation = outputTransformation,
      decorator = { inner ->
        TextFieldDecoration(
          style = style,
          enabled = enabled,
          isError = isError,
          hasText = state.text.isNotEmpty(),
          label = label,
          placeholder = placeholder,
          supportingText = supportingText,
          leadingIcon = leadingIcon,
          trailingIcon = trailingIcon,
          interactionSource = interactionSource,
          innerTextField = inner,
        )
      },
    )
  }
}

/** Migration overload with the classic `value` / `onValueChange` contract. Prefer the state-based API. */
@Composable
public fun TextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  label: String? = null,
  placeholder: String? = null,
  supportingText: String? = null,
  isError: Boolean = false,
  leadingIcon: (@Composable () -> Unit)? = null,
  trailingIcon: (@Composable () -> Unit)? = null,
  singleLine: Boolean = true,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  minLines: Int = 1,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  style: TextFieldStyle? = null,
  interactionSource: MutableInteractionSource? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = TextFieldDefaults.resolve(style)
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val textColor = if (enabled) style.textColor else style.textColor.copy(alpha = DittoTheme.colors.disabledAlpha)

  ProvideSelectionColors(style) {
    BasicTextField(
      value = value,
      onValueChange = onValueChange,
      modifier = modifier,
      enabled = enabled,
      readOnly = readOnly,
      textStyle = style.textStyle.copy(color = textColor),
      keyboardOptions = keyboardOptions,
      keyboardActions = keyboardActions,
      singleLine = singleLine,
      maxLines = maxLines,
      minLines = minLines,
      visualTransformation = visualTransformation,
      interactionSource = interactionSource,
      cursorBrush = SolidColor(style.cursorColor),
      decorationBox = { inner ->
        TextFieldDecoration(
          style = style,
          enabled = enabled,
          isError = isError,
          hasText = value.isNotEmpty(),
          label = label,
          placeholder = placeholder,
          supportingText = supportingText,
          leadingIcon = leadingIcon,
          trailingIcon = trailingIcon,
          interactionSource = interactionSource,
          innerTextField = inner,
        )
      },
    )
  }
}

@Composable
private fun ProvideSelectionColors(style: TextFieldStyle, content: @Composable () -> Unit) {
  val selection = remember(style.cursorColor) {
    TextSelectionColors(handleColor = style.cursorColor, backgroundColor = style.cursorColor.copy(alpha = 0.3f))
  }
  CompositionLocalProvider(LocalTextSelectionColors provides selection, content = content)
}

@Composable
private fun TextFieldDecoration(
  style: TextFieldStyle,
  enabled: Boolean,
  isError: Boolean,
  hasText: Boolean,
  label: String?,
  placeholder: String?,
  supportingText: String?,
  leadingIcon: (@Composable () -> Unit)?,
  trailingIcon: (@Composable () -> Unit)?,
  interactionSource: MutableInteractionSource,
  innerTextField: @Composable () -> Unit,
) {
  val focused by interactionSource.collectIsFocusedAsState()
  val motion = DittoTheme.motion
  val disabledAlpha = DittoTheme.colors.disabledAlpha
  val alpha = if (enabled) 1f else disabledAlpha
  val targetBorderColor = when {
    isError -> style.errorColor
    focused -> style.focusedBorderColor
    else -> style.borderColor
  }
  val borderColor by animateColorAsState(targetBorderColor.copy(alpha = targetBorderColor.alpha * alpha), tween(motion.durationShort))
  val borderWidth = if (focused || isError) maxOf(style.focusedBorderWidth, style.borderWidth) else style.borderWidth
  val spacing = DittoTheme.spacing

  Column {
    if (label != null) {
      Text(
        label,
        style = style.labelStyle,
        color = (if (isError) style.errorColor else style.labelColor).copy(alpha = alpha),
      )
      Spacer(Modifier.height(spacing.xs))
    }
    Row(
      Modifier
        .focusRing(interactionSource, style.shape)
        .defaultMinSize(minHeight = style.minHeight)
        .background(style.containerColor.copy(alpha = style.containerColor.alpha * alpha), style.shape)
        .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, style.shape) else Modifier)
        .padding(style.contentPadding),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      CompositionLocalProvider(LocalContentColor provides style.iconColor.copy(alpha = alpha)) {
        if (leadingIcon != null) {
          leadingIcon()
          Spacer(Modifier.width(style.iconSpacing))
        }
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
          if (!hasText && placeholder != null) {
            Text(placeholder, style = style.textStyle, color = style.placeholderColor.copy(alpha = alpha), maxLines = 1)
          }
          innerTextField()
        }
        if (trailingIcon != null) {
          Spacer(Modifier.width(style.iconSpacing))
          trailingIcon()
        }
      }
    }
    if (supportingText != null) {
      Spacer(Modifier.height(spacing.xs))
      Text(
        supportingText,
        style = style.supportingStyle,
        color = (if (isError) style.errorColor else style.supportingColor).copy(alpha = alpha),
      )
    }
  }
}
