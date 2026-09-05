package com.r0adkll.ditto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.foundation.Icon
import com.r0adkll.ditto.foundation.ProvideTextStyle
import com.r0adkll.ditto.foundation.Surface
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.icons.DittoIcons
import com.r0adkll.ditto.theme.DittoTheme
import com.r0adkll.ditto.tokens.ElevationLevel

public enum class TopBarVariant {
  /** One row: navigation, title, actions. */
  Small,

  /** A large title below the row that collapses into the row as content scrolls (Android/Apple). */
  Large,
}

@Immutable
public class TopBarStyle(
  public val height: Dp,
  public val largeHeight: Dp,
  public val containerColor: Color,
  public val scrolledContainerColor: Color,
  public val titleStyle: TextStyle,
  public val largeTitleStyle: TextStyle,
  public val titleColor: Color,
  public val iconColor: Color,
  public val centerTitle: Boolean,
  public val horizontalPadding: Dp,
  public val scrolledElevation: ElevationLevel,
  /** Apple/Desktop: hairline under the bar once content has scrolled. */
  public val hairlineWhenScrolled: Boolean,
) {
  public fun copy(
    height: Dp = this.height,
    largeHeight: Dp = this.largeHeight,
    containerColor: Color = this.containerColor,
    scrolledContainerColor: Color = this.scrolledContainerColor,
    titleStyle: TextStyle = this.titleStyle,
    largeTitleStyle: TextStyle = this.largeTitleStyle,
    titleColor: Color = this.titleColor,
    iconColor: Color = this.iconColor,
    centerTitle: Boolean = this.centerTitle,
    horizontalPadding: Dp = this.horizontalPadding,
    scrolledElevation: ElevationLevel = this.scrolledElevation,
    hairlineWhenScrolled: Boolean = this.hairlineWhenScrolled,
  ): TopBarStyle = TopBarStyle(
    height, largeHeight, containerColor, scrolledContainerColor, titleStyle, largeTitleStyle, titleColor, iconColor,
    centerTitle, horizontalPadding, scrolledElevation, hairlineWhenScrolled,
  )

  override fun equals(other: Any?): Boolean = other is TopBarStyle && fields() == other.fields()
  override fun hashCode(): Int = fields().hashCode()
  override fun toString(): String = "TopBarStyle(height=$height)"
  private fun fields(): List<Any?> = listOf(
    height, largeHeight, containerColor, scrolledContainerColor, titleStyle, largeTitleStyle, titleColor, iconColor,
    centerTitle, horizontalPadding, scrolledElevation, hairlineWhenScrolled,
  )
}

public val LocalTopBarStyle: ProvidableCompositionLocal<TopBarStyle?> = staticCompositionLocalOf { null }

public object TopBarDefaults {
  @Composable
  @ReadOnlyComposable
  public fun style(idiom: Idiom = DittoTheme.idiom): TopBarStyle {
    val colors = DittoTheme.colors
    val type = DittoTheme.typography
    val spacing = DittoTheme.spacing
    return when (idiom) {
      Idiom.Android -> TopBarStyle(
        height = 64.dp,
        largeHeight = 152.dp,
        containerColor = colors.background,
        scrolledContainerColor = colors.surfaceOverlay,
        titleStyle = type.heading,
        largeTitleStyle = type.title,
        titleColor = colors.onSurface,
        iconColor = colors.onSurfaceVariant,
        centerTitle = false,
        horizontalPadding = spacing.xs,
        scrolledElevation = ElevationLevel.Level0,
        hairlineWhenScrolled = false,
      )
      Idiom.Apple -> TopBarStyle(
        height = 44.dp,
        largeHeight = 96.dp,
        containerColor = colors.background,
        scrolledContainerColor = colors.surfaceRaised,
        titleStyle = type.subheading,
        largeTitleStyle = type.display,
        titleColor = colors.onSurface,
        iconColor = colors.accent,
        centerTitle = true,
        horizontalPadding = spacing.sm,
        scrolledElevation = ElevationLevel.Level0,
        hairlineWhenScrolled = true,
      )
      Idiom.Desktop -> TopBarStyle(
        height = 48.dp,
        largeHeight = 96.dp,
        containerColor = colors.background,
        scrolledContainerColor = colors.background,
        titleStyle = type.subheading,
        largeTitleStyle = type.title,
        titleColor = colors.onSurface,
        iconColor = colors.onSurfaceVariant,
        centerTitle = false,
        horizontalPadding = spacing.sm,
        scrolledElevation = ElevationLevel.Level0,
        hairlineWhenScrolled = true,
      )
    }
  }
}

/**
 * Tracks how far the content under a [TopBar] has scrolled. Attach with
 * `Modifier.nestedScroll(behavior.connection)` on the scrolling content.
 */
@Stable
public class TopBarScrollBehavior internal constructor(initialOffset: Float, initialContentOffset: Float) {
  /** Collapsed amount of the large-title area, in px: 0 (expanded) .. -[limit]. */
  public var heightOffset: Float by mutableFloatStateOf(initialOffset)
    internal set

  /** Accumulated content scroll; anything below zero means the content is scrolled. */
  public var contentOffset: Float by mutableFloatStateOf(initialContentOffset)
    internal set

  internal var limit: Float = 0f

  public val collapsedFraction: Float
    get() = if (limit == 0f) 0f else (-heightOffset / limit).coerceIn(0f, 1f)

  public val isScrolled: Boolean get() = contentOffset < -0.5f || collapsedFraction > 0.99f

  public val connection: NestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
      if (available.y >= 0f || limit == 0f) return Offset.Zero
      val previous = heightOffset
      heightOffset = (heightOffset + available.y).coerceIn(-limit, 0f)
      val consumed = heightOffset - previous
      contentOffset += consumed
      return Offset(0f, consumed)
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
      contentOffset += consumed.y
      if (available.y <= 0f || limit == 0f) return Offset.Zero
      // Only re-expand once the content itself is back at the top.
      if (contentOffset < -0.5f) return Offset.Zero
      val previous = heightOffset
      heightOffset = (heightOffset + available.y).coerceIn(-limit, 0f)
      return Offset(0f, heightOffset - previous)
    }
  }
}

@Composable
public fun rememberTopBarScrollBehavior(): TopBarScrollBehavior {
  var offset by rememberSaveable { mutableFloatStateOf(0f) }
  var content by rememberSaveable { mutableFloatStateOf(0f) }
  val behavior = remember { TopBarScrollBehavior(offset, content) }
  offset = behavior.heightOffset
  content = behavior.contentOffset
  return behavior
}

/**
 * App bar with navigation, title and actions, drawn under the status bar. With a
 * [scrollBehavior] it shifts to [TopBarStyle.scrolledContainerColor] (Android) or grows a
 * hairline (Apple / Desktop) as content scrolls, and the [TopBarVariant.Large] title collapses.
 */
@Composable
public fun TopBar(
  title: String,
  modifier: Modifier = Modifier,
  navigationIcon: (@Composable () -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
  variant: TopBarVariant = TopBarVariant.Small,
  scrollBehavior: TopBarScrollBehavior? = null,
  windowInsets: WindowInsets = TopBarInsets,
  style: TopBarStyle? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalTopBarStyle.current ?: TopBarDefaults.style()
  TopBar(
    title = { Text(title, style = style.titleStyle, color = style.titleColor, maxLines = 1, overflow = TextOverflow.Ellipsis) },
    largeTitle = { Text(title, style = style.largeTitleStyle, color = style.titleColor, maxLines = 1, overflow = TextOverflow.Ellipsis) },
    modifier = modifier,
    navigationIcon = navigationIcon,
    actions = actions,
    variant = variant,
    scrollBehavior = scrollBehavior,
    windowInsets = windowInsets,
    style = style,
  )
}

/**
 * Slot-based variant: [title] is the row title, [largeTitle] the expanded title for
 * [TopBarVariant.Large] (defaults to [title]).
 */
@Composable
public fun TopBar(
  title: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  largeTitle: @Composable () -> Unit = title,
  navigationIcon: (@Composable () -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
  variant: TopBarVariant = TopBarVariant.Small,
  scrollBehavior: TopBarScrollBehavior? = null,
  windowInsets: WindowInsets = TopBarInsets,
  style: TopBarStyle? = null,
) {
  @Suppress("NAME_SHADOWING")
  val style = style ?: LocalTopBarStyle.current ?: TopBarDefaults.style()
  val motion = DittoTheme.motion
  val large = variant == TopBarVariant.Large
  val density = LocalDensity.current
  val limitPx = with(density) { (style.largeHeight - style.height).toPx() }
  if (scrollBehavior != null) scrollBehavior.limit = if (large) limitPx else 0f
  val fraction = if (large) scrollBehavior?.collapsedFraction ?: 0f else 1f
  val scrolled = scrollBehavior?.isScrolled ?: false
  val container by animateColorAsState(
    if (scrolled) style.scrolledContainerColor else style.containerColor,
    tween(motion.durationShort),
  )
  val extraHeight = if (large) (style.largeHeight - style.height) * (1f - fraction) else 0.dp

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = container,
    contentColor = style.titleColor,
    elevation = if (scrolled) style.scrolledElevation else ElevationLevel.Level0,
  ) {
    Column(Modifier.windowInsetsPadding(windowInsets)) {
      Row(
        Modifier.fillMaxWidth().height(style.height).padding(horizontal = style.horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        val iconStyle = IconButtonDefaults.style(IconButtonVariant.Standard).copy(contentColor = style.iconColor)
        CompositionLocalProvider(
          LocalIconButtonStyles provides IconButtonStyles(standard = iconStyle),
        ) {
          if (navigationIcon != null) Box { navigationIcon() }
          Box(
            Modifier.weight(1f).padding(horizontal = if (navigationIcon == null) DittoTheme.spacing.md else DittoTheme.spacing.xs),
            contentAlignment = if (style.centerTitle) Alignment.Center else Alignment.CenterStart,
          ) {
            // Small title: always shown for Small; fades in as the large title collapses.
            Box(Modifier.graphicsLayer { alpha = if (large) ((fraction - 0.5f) * 2f).coerceIn(0f, 1f) else 1f }) {
              ProvideTextStyle(style.titleStyle) { title() }
            }
          }
          Row(verticalAlignment = Alignment.CenterVertically) { actions() }
        }
      }
      if (large && extraHeight > 0.dp) {
        Box(
          Modifier.fillMaxWidth().height(extraHeight).padding(horizontal = DittoTheme.spacing.lg),
          contentAlignment = Alignment.BottomStart,
        ) {
          Box(
            Modifier
              .padding(bottom = DittoTheme.spacing.sm)
              .graphicsLayer { alpha = (1f - fraction * 2f).coerceIn(0f, 1f) },
          ) {
            ProvideTextStyle(style.largeTitleStyle) { largeTitle() }
          }
        }
      }
      if (style.hairlineWhenScrolled && scrolled) HorizontalDivider()
    }
  }
}

/** The conventional navigation-back affordance for a [TopBar]. */
@Composable
public fun BackButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  contentDescription: String = "Back",
) {
  IconButton(onClick = onClick, modifier = modifier) {
    Icon(DittoIcons.back, contentDescription)
  }
}
