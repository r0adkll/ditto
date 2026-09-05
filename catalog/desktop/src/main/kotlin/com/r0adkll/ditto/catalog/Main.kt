package com.r0adkll.ditto.catalog

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
  Window(
    onCloseRequest = ::exitApplication,
    title = "Ditto Catalog",
    state = rememberWindowState(position = WindowPosition.Aligned(androidx.compose.ui.Alignment.Center), size = DpSize(1100.dp, 800.dp)),
  ) {
    CatalogApp()
  }
}
