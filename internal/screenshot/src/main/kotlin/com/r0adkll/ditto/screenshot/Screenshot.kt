package com.r0adkll.ditto.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.use
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Renders [content] headlessly at density 1 and compares it with the golden at
 * `screenshots/<os>/<name>.png` next to the test sources (ADR-016).
 *
 * - Missing golden: written, and the test fails so the recording is reviewed.
 * - `-Dditto.updateGoldens=true`: golden overwritten, test passes.
 * - Mismatch: `<name>-actual.png` and `<name>-diff.png` are written next to the golden.
 *
 * Text is rendered with the bundled Inter font and pinned rasterization settings via
 * [screenshotTheme], so same-OS runs are deterministic. Cross-OS glyph rendering differs
 * (CMP #3442), hence the per-OS golden directory; Linux CI is the source of truth.
 */
@OptIn(ExperimentalComposeUiApi::class)
public fun assertScreenshot(
  name: String,
  width: Int = 800,
  height: Int = 600,
  time: Duration = 0.milliseconds,
  goldenRoot: File = defaultGoldenRoot(),
  comparator: ImageComparator = ImageComparator(),
  content: @Composable () -> Unit,
) {
  val actual = render(width, height, time, content)
  val osDir = File(goldenRoot, osName()).apply { mkdirs() }
  val golden = File(osDir, "$name.png")
  val update = System.getProperty("ditto.updateGoldens") == "true"

  if (update || !golden.exists()) {
    golden.writeBytes(actual.png())
    if (!update) {
      throw AssertionError("No golden for '$name'. Recorded ${golden.path}; review it and re-run.")
    }
    return
  }

  val expected = Image.makeFromEncoded(golden.readBytes())
  val result = comparator.compare(expected, actual)
  if (!result.matches) {
    File(osDir, "$name-actual.png").writeBytes(actual.png())
    result.diff?.let { File(osDir, "$name-diff.png").writeBytes(it.png()) }
    throw AssertionError("Screenshot '$name' differs from golden: ${result.description}. See ${osDir.path}")
  } else {
    File(osDir, "$name-actual.png").delete()
    File(osDir, "$name-diff.png").delete()
  }
}

@OptIn(ExperimentalComposeUiApi::class)
public fun render(width: Int, height: Int, time: Duration = 0.milliseconds, content: @Composable () -> Unit): Image =
  ImageComposeScene(width = width, height = height, density = Density(1f)) {
    screenshotTheme { content() }
  }.use { scene ->
    // First frame lays out; render at [time] so animations settle deterministically.
    scene.render(0L)
    scene.render(time.inWholeNanoseconds)
  }

internal fun Image.png(): ByteArray =
  encodeToData(EncodedImageFormat.PNG)?.bytes ?: error("Failed to encode PNG")

private fun osName(): String {
  val os = System.getProperty("os.name").lowercase()
  return when {
    os.contains("mac") -> "macos"
    os.contains("win") -> "windows"
    else -> "linux"
  }
}

private fun defaultGoldenRoot(): File {
  // Gradle runs tests with the module directory as the working dir.
  val configured = System.getProperty("ditto.goldenRoot")
  return if (configured != null) File(configured) else File("src/jvmTest/screenshots")
}
