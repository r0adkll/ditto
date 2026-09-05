package com.r0adkll.ditto.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformParagraphStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.FontHinting
import androidx.compose.ui.text.FontRasterizationSettings
import androidx.compose.ui.text.FontSmoothing
import androidx.compose.runtime.CompositionLocalProvider
import com.r0adkll.ditto.input.InputCapabilities
import com.r0adkll.ditto.input.LocalInputCapabilities
import com.r0adkll.ditto.theme.LocalDittoTypographyTransform

/** Bundled Inter (OFL) so goldens do not depend on the host's system font. */
public val ScreenshotFontFamily: FontFamily by lazy {
  FontFamily(
    Font(resource = "fonts/Inter-Regular.ttf", weight = FontWeight.Normal),
    Font(resource = "fonts/Inter-Medium.ttf", weight = FontWeight.Medium),
    Font(resource = "fonts/Inter-SemiBold.ttf", weight = FontWeight.SemiBold),
    Font(resource = "fonts/Inter-SemiBold.ttf", weight = FontWeight.Bold),
  )
}

/** Compose Hot Reload's determinism recipe: no hinting, subpixel positioning, plain anti-aliasing. */
@OptIn(ExperimentalTextApi::class, ExperimentalComposeUiApi::class)
public val ScreenshotPlatformTextStyle: PlatformTextStyle = PlatformTextStyle(
  spanStyle = null,
  paragraphStyle = PlatformParagraphStyle(
    fontRasterizationSettings = FontRasterizationSettings(
      smoothing = FontSmoothing.AntiAlias,
      hinting = FontHinting.None,
      subpixelPositioning = true,
      autoHintingForced = false,
    ),
  ),
)

/**
 * Pins fonts, rasterization and input capabilities for every [com.r0adkll.ditto.theme.DittoTheme]
 * composed inside [content], including the ones [com.r0adkll.ditto.preview.DittoPreviewMatrix]
 * creates per cell.
 */
@Composable
internal fun screenshotTheme(content: @Composable () -> Unit) {
  CompositionLocalProvider(
    LocalDittoTypographyTransform provides { typography ->
      typography.map { it.copy(fontFamily = ScreenshotFontFamily, platformStyle = ScreenshotPlatformTextStyle) }
    },
    LocalInputCapabilities provides InputCapabilities.PointerAndKeyboard,
    content = content,
  )
}
