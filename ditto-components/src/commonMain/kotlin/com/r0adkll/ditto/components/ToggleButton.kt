package com.r0adkll.ditto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CornerBasedShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.LocalContentColor
import com.r0adkll.ditto.foundation.ProvideTextStyle
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.interaction.focusRing
import com.r0adkll.ditto.interaction.minimumInteractiveSize
import com.r0adkll.ditto.interaction.pressScale
import com.r0adkll.ditto.theme.DittoTheme

@Immutable
public class ToggleButtonStyle(
  public val uncheckedShape: CornerBasedShape,
  public val checkedShape: CornerBasedShape,
  public val uncheckedContainerColor: Color,
  public val checkedContainerColor: Color,
  public val uncheckedContentColor: Color,
  public val checkedContentColor: Color,
  public val uncheckedBorder: BorderStroke?,
  public val minHeight: Dp,
  public val minWidth: Dp,
  public val contentPadding: PaddingValues,
  public val textStyle: TextStyle,
) {
  public fun copy(
    uncheckedShape: CornerBasedShape = this.uncheckedShape,
    checkedShape: CornerBasedShape = this.checkedShape,
    uncheckedContainerColor: Color = this.uncheckedContainerColor,
    checkedContainerColor: Color = this.checkedContainerColor,
    uncheckedContentColor: Color = this.uncheckedContentColor,
    checkedContentColor: Color = this.checkedContentColor,
    uncheckedBorder: BorderStroke? = this.uncheckedBorder,
    minHeight: Dp = this.minHeight,
    minWidth: Dp = this.minWidth,
    contentPadding: PaddingValues = this.contentPadding,
    textStyle: TextStyle = this.textStyle,
  ): ToggleButtonStyle = ToggleButtonStyle(
    uncheckedShape, checkedShape, uncheckedContainerColor, checkedContainerColor, uncheckedContentColor,
    checkedContentColor, uncheckedBorder, minHeight, minWidth, contentPadding, textStyle,
  )

  override fun equals(other: Any?): Boolean = other is ToggleButtonStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "ToggleButtonStyle(minHeight=$minHeight)"
  private fun fields(): List<Any?> = listOf(
    uncheckedShape, checkedShape, uncheckedContainerColor, checkedContainerColor, uncheckedContentColor,
    checkedContentColor, uncheckedBorder, minHeight, minWidth, contentPadding, textStyle,
  )
}

public val LocalToggleButtonStyle: ProvidableCompositionLocal<ToggleButtonStyle?> = staticCompositionLocalOf { null }

public object ToggleButtonDefaults {
  /** Android: shape morphs square → pill as it turns on (Expressive). Apple/Desktop: fill change only. */
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): ToggleButtonStyle {
    val colors = DittoTheme.colors
    val shapes = DittoTheme.shapes
    val type = DittoTheme.typography
    val spacing = DittoTheme.spacing
    val dimens = DittoTheme.dimens
    return when (idiom) {
      Idiom.Android -> ToggleButtonStyle(
        uncheckedShape = shapes.medium,
        checkedShape = shapes.full,
        uncheckedContainerColor = colors.accent.copy(alpha = ButtonDefaults.TonalContainerAlpha),
        checkedContainerColor = colors.accent,
        uncheckedContentColor = colors.accent,
        checkedContentColor = colors.onAccent,
        uncheckedBorder = null,
        minHeight = 40.dp,
        minWidth = 48.dp,
        contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
        textStyle = type.label,
      )
      Idiom.Apple -> ToggleButtonStyle(
        uncheckedShape = shapes.small,
        checkedShape = shapes.small,
        uncheckedContainerColor = colors.neutrals[3],
        checkedContainerColor = colors.accent,
        uncheckedContentColor = colors.accent,
        checkedContentColor = colors.onAccent,
        uncheckedBorder = null,
        minHeight = 36.dp,
        minWidth = 44.dp,
        contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
        textStyle = type.label,
      )
      Idiom.Desktop -> ToggleButtonStyle(
        uncheckedShape = shapes.small,
        checkedShape = shapes.small,
        uncheckedContainerColor = Color.Transparent,
        checkedContainerColor = colors.neutrals[if (colors.isDark) 5 else 4],
        uncheckedContentColor = colors.onSurfaceVariant,
        checkedContentColor = colors.onSurface,
        uncheckedBorder = BorderStroke(dimens.borderWidth, colors.outlineVariant),
        minHeight = 36.dp,
        minWidth = 36.dp,
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        textStyle = type.label,
      )
    }
  }
}

/** A button that stays pressed. On Android the container morphs from rounded square to pill as it turns on. */
@Composable
public fun ToggleButton(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: ToggleButtonStyle? = null,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalToggleButtonStyle.current ?: ToggleButtonDefaults.style()
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val motion = DittoTheme.motion
  val pointer = LocalInputCapabilities.current.pointer
  val alpha = if (enabled) 1f else DittoTheme.colors.disabledAlpha
  val haptics = rememberToggleHaptics()
  val container by animateColorAsState(if (checked) style.checkedContainerColor else style.uncheckedContainerColor, tween(motion.durationShort))
  val content by animateColorAsState(if (checked) style.checkedContentColor else style.uncheckedContentColor, tween(motion.durationShort))
  val morph by animateFloatAsState(if (checked) 1f else 0f, motion.spring)
  val shape = remember(style.uncheckedShape, style.checkedShape) { MorphShape(style.uncheckedShape, style.checkedShape) }
  shape.fraction = morph
  val border = style.uncheckedBorder?.takeIf { !checked }

  Row(
    modifier
      .minimumInteractiveSize()
      .pressScale(interactionSource, enabled)
      .focusRing(interactionSource, shape)
      .then(if (border != null) Modifier.border(border, shape) else Modifier)
      .background(container.copy(alpha = container.alpha * alpha), shape)
      .clip(shape)
      .toggleable(
        value = checked,
        interactionSource = interactionSource,
        indication = LocalIndication.current,
        enabled = enabled,
        role = Role.Checkbox,
        onValueChange = { haptics.toggled(it); onCheckedChange(it) },
      )
      .then(if (pointer && enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
      .defaultMinSize(minWidth = style.minWidth, minHeight = style.minHeight)
      .padding(style.contentPadding),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    CompositionLocalProvider(LocalContentColor provides content.copy(alpha = content.alpha * alpha)) {
      ProvideTextStyle(style.textStyle) { content() }
    }
  }
}

/** Interpolates corner radii between two rounded shapes; used for the Expressive shape morph. */
internal class MorphShape(private val from: CornerBasedShape, private val to: CornerBasedShape) : Shape {
  var fraction: Float = 0f

  override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
    fun lerp(a: Float, b: Float) = a + (b - a) * fraction.coerceIn(0f, 1f)
    val tl = lerp(from.topStart.toPx(size, density), to.topStart.toPx(size, density))
    val tr = lerp(from.topEnd.toPx(size, density), to.topEnd.toPx(size, density))
    val br = lerp(from.bottomEnd.toPx(size, density), to.bottomEnd.toPx(size, density))
    val bl = lerp(from.bottomStart.toPx(size, density), to.bottomStart.toPx(size, density))
    val max = size.minDimension / 2
    return Outline.Rounded(
      RoundRect(
        left = 0f, top = 0f, right = size.width, bottom = size.height,
        topLeftCornerRadius = CornerRadius(tl.coerceAtMost(max)),
        topRightCornerRadius = CornerRadius(tr.coerceAtMost(max)),
        bottomRightCornerRadius = CornerRadius(br.coerceAtMost(max)),
        bottomLeftCornerRadius = CornerRadius(bl.coerceAtMost(max)),
      ),
    )
  }
}
