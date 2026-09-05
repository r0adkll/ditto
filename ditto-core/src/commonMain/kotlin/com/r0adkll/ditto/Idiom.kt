package com.r0adkll.ditto

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * A complete visual + interaction language a component can render in.
 *
 * Idioms are named by the platform family they feel at home on (ADR-013), not by a design
 * language: Ditto is its own design language, and [Android] is Material-*esque*, not Material.
 *
 * The active idiom is a runtime value ([LocalIdiom]) defaulting to [platformIdiom], so any idiom
 * can be previewed or screenshot-tested on any host (ADR-003).
 */
public enum class Idiom {
  /** Default on Android. Material-esque with moderated Expressive motion (ADR-018). */
  Android,

  /** Default on iOS. Flat iOS-style: SF, grouped lists, 44pt targets, tinted text buttons (ADR-023). */
  Apple,

  /** Default on Desktop JVM (all OSes) and Web. shadcn/Radix-style neutral web (ADR-023). */
  Desktop,
}

/** The idiom the current platform naturally renders in. */
public expect fun platformIdiom(): Idiom

/**
 * The active [Idiom]. Provided by [com.r0adkll.ditto.theme.DittoTheme]; override for a subtree
 * to render it in another idiom.
 */
public val LocalIdiom: ProvidableCompositionLocal<Idiom> = staticCompositionLocalOf { platformIdiom() }
