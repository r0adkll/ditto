package com.r0adkll.ditto.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.tokens.DittoColors

/**
 * Idiom extension tokens (ADR-010): values with no universal meaning, derived from the core
 * scheme by default. Each is provided only while its idiom is active; reading another idiom's
 * extension is a programming error and throws.
 */
@Immutable
public data class AndroidTokens(
  /** Alpha of the state layer drawn over a container on press. */
  val pressedStateAlpha: Float,
  val hoveredStateAlpha: Float,
  val focusedStateAlpha: Float,
) {
  public companion object {
    public fun from(colors: DittoColors): AndroidTokens =
      AndroidTokens(pressedStateAlpha = 0.12f, hoveredStateAlpha = 0.08f, focusedStateAlpha = 0.12f)

    public val current: AndroidTokens
      @Composable @ReadOnlyComposable get() = LocalAndroidTokens.current ?: notActive(Idiom.Android)
  }
}

@Immutable
public data class AppleTokens(
  /** Background behind grouped lists / settings cards. */
  val groupedBackground: Color,
  /** Hairline separator between list rows. */
  val separator: Color,
  /** Content opacity while pressed. */
  val pressedOpacity: Float,
) {
  public companion object {
    public fun from(colors: DittoColors): AppleTokens = AppleTokens(
      groupedBackground = if (colors.isDark) colors.neutrals[1] else colors.neutrals[2],
      separator = colors.outlineVariant,
      pressedOpacity = 0.55f,
    )

    public val current: AppleTokens
      @Composable @ReadOnlyComposable get() = LocalAppleTokens.current ?: notActive(Idiom.Apple)
  }
}

@Immutable
public data class DesktopTokens(
  /** Overlay drawn over a container on hover. */
  val hoverOverlay: Color,
  /** Overlay drawn over a container on press. */
  val pressedOverlay: Color,
  /** Keyboard focus ring color. */
  val focusRing: Color,
) {
  public companion object {
    public fun from(colors: DittoColors): DesktopTokens = DesktopTokens(
      hoverOverlay = colors.onSurface.copy(alpha = 0.06f),
      pressedOverlay = colors.onSurface.copy(alpha = 0.1f),
      focusRing = colors.accent,
    )

    public val current: DesktopTokens
      @Composable @ReadOnlyComposable get() = LocalDesktopTokens.current ?: notActive(Idiom.Desktop)
  }
}

public val LocalAndroidTokens: ProvidableCompositionLocal<AndroidTokens?> = staticCompositionLocalOf { null }
public val LocalAppleTokens: ProvidableCompositionLocal<AppleTokens?> = staticCompositionLocalOf { null }
public val LocalDesktopTokens: ProvidableCompositionLocal<DesktopTokens?> = staticCompositionLocalOf { null }

private fun notActive(idiom: Idiom): Nothing =
  error("${idiom.name} tokens were read while the $idiom idiom is not active. Check DittoTheme.idiom first.")

@Composable
internal fun ProvideIdiomTokens(idiom: Idiom, colors: DittoColors, content: @Composable () -> Unit) {
  when (idiom) {
    Idiom.Android -> CompositionLocalProvider(
      LocalAndroidTokens provides remember(colors) { AndroidTokens.from(colors) },
      LocalAppleTokens provides null,
      LocalDesktopTokens provides null,
      content = content,
    )
    Idiom.Apple -> CompositionLocalProvider(
      LocalAndroidTokens provides null,
      LocalAppleTokens provides remember(colors) { AppleTokens.from(colors) },
      LocalDesktopTokens provides null,
      content = content,
    )
    Idiom.Desktop -> CompositionLocalProvider(
      LocalAndroidTokens provides null,
      LocalAppleTokens provides null,
      LocalDesktopTokens provides remember(colors) { DesktopTokens.from(colors) },
      content = content,
    )
  }
}
