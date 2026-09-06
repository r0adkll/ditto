package com.r0adkll.ditto.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.reflect.KClass

/**
 * App-wide style tweaks expressed as **transforms of the idiom default** (ADR-029), so they keep
 * tracking the platform when the idiom or density changes. Keyed by the style class and an optional
 * variant key; a general transform runs first, then the keyed one.
 *
 * ```
 * DittoTheme(styleOverrides = dittoStyleOverrides {
 *   override<ButtonStyle> { it.copy(shape = pill) }                       // every button
 *   override<ButtonStyle>(ButtonVariant.Text) { it.copy(minHeight = 32.dp) } // text buttons only
 * }) { ... }
 * ```
 */
@Immutable
public class DittoStyleOverrides private constructor(
  private val entries: Map<Key, (Any) -> Any>,
) {
  private data class Key(val type: KClass<*>, val variant: Any?)

  /** Applies the registered transforms for [default]'s class (general, then [variant]-keyed). */
  public fun <T : Any> resolve(default: T, variant: Any? = null): T {
    if (entries.isEmpty()) return default
    val type = default::class
    var value: Any = default
    entries[Key(type, null)]?.let { value = it(value) }
    if (variant != null) entries[Key(type, variant)]?.let { value = it(value) }
    @Suppress("UNCHECKED_CAST")
    return value as T
  }

  public val isEmpty: Boolean get() = entries.isEmpty()

  /** Returns a registry with [other]'s transforms applied after this one's. */
  public operator fun plus(other: DittoStyleOverrides): DittoStyleOverrides {
    if (other.isEmpty) return this
    if (isEmpty) return other
    val merged = entries.toMutableMap()
    other.entries.forEach { (key, transform) ->
      val existing = merged[key]
      merged[key] = if (existing == null) transform else { v -> transform(existing(v)) }
    }
    return DittoStyleOverrides(merged)
  }

  override fun equals(other: Any?): Boolean = other is DittoStyleOverrides && entries == other.entries
  override fun hashCode(): Int = entries.hashCode()
  override fun toString(): String = "DittoStyleOverrides(${entries.size} transforms)"

  public class Builder internal constructor() {
    @PublishedApi internal val entries: MutableMap<Pair<KClass<*>, Any?>, (Any) -> Any> = LinkedHashMap()

    /** Registers [transform] for style type [T], optionally only for [variant]. */
    public inline fun <reified T : Any> override(variant: Any? = null, noinline transform: (T) -> T) {
      val key = T::class to variant
      val existing = entries[key]
      @Suppress("UNCHECKED_CAST")
      val typed = transform as (Any) -> Any
      entries[key] = if (existing == null) typed else { v -> typed(existing(v)) }
    }

    @PublishedApi internal fun build(): DittoStyleOverrides =
      DittoStyleOverrides(entries.mapKeys { (k, _) -> Key(k.first, k.second) })
  }

  public companion object {
    public val Empty: DittoStyleOverrides = DittoStyleOverrides(emptyMap())
  }
}

public fun dittoStyleOverrides(build: DittoStyleOverrides.Builder.() -> Unit): DittoStyleOverrides =
  DittoStyleOverrides.Builder().apply(build).build()

public val LocalDittoStyleOverrides: ProvidableCompositionLocal<DittoStyleOverrides> =
  staticCompositionLocalOf { DittoStyleOverrides.Empty }
