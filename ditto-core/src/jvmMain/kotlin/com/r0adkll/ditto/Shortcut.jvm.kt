package com.r0adkll.ditto.input

public actual fun platformUsesMetaForShortcuts(): Boolean =
  System.getProperty("os.name")?.lowercase()?.contains("mac") == true
