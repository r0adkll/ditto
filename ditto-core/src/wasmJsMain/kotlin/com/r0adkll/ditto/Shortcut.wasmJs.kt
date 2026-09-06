package com.r0adkll.ditto.input

import kotlinx.browser.window

public actual fun platformUsesMetaForShortcuts(): Boolean =
  window.navigator.platform.lowercase().contains("mac") || window.navigator.userAgent.lowercase().contains("mac os")
