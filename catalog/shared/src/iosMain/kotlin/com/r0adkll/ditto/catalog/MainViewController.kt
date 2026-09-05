package com.r0adkll.ditto.catalog

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

@Suppress("unused", "FunctionName")
fun MainViewController(): UIViewController = ComposeUIViewController { CatalogApp() }
