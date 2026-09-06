package com.r0adkll.ditto.spike.unstyled

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composeunstyled.DialogHost

/** Renders Unstyled modals (menus, dialogs, sheets) in-tree instead of in a platform window. */
@Composable
fun UHost(modifier: Modifier = Modifier, content: @Composable () -> Unit) = DialogHost(modifier) { content() }
