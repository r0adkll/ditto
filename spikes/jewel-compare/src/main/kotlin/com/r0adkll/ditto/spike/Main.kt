package com.r0adkll.ditto.spike

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
  Window(onCloseRequest = ::exitApplication, title = "Ditto vs Jewel", state = rememberWindowState(size = DpSize(960.dp, 900.dp))) {
    Row {
      DittoSampleForm(dark = false)
      JewelSampleForm(dark = false)
    }
  }
}
