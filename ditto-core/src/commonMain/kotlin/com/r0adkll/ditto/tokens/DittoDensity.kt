package com.r0adkll.ditto.tokens

import com.r0adkll.ditto.Idiom

/**
 * How tightly controls are sized. [Comfortable] is touch-first (mobile idioms). [Compact] is the
 * desktop tool-window density: 28dp controls, 13sp body text, 24–28dp rows. The Desktop idiom
 * defaults to Compact (Jewel comparison, 2026-09-06).
 */
public enum class DittoDensity {
  Comfortable,
  Compact,
  ;

  public companion object {
    public fun forIdiom(idiom: Idiom): DittoDensity = when (idiom) {
      Idiom.Android, Idiom.Apple -> Comfortable
      Idiom.Desktop -> Compact
    }
  }
}
