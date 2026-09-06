package com.r0adkll.ditto.catalog

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.r0adkll.ditto.Idiom
import com.r0adkll.ditto.tokens.ColorMode
import kotlinx.browser.window

/**
 * `?id=buttons` renders that one demo (this is what the docs site embeds in a lazy iframe);
 * with no id, the full catalog. `&idiom=apple&mode=dark` pins the idiom and colour mode, so a
 * docs page can show the same component in three idioms side by side.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val params = queryParams()
    val id = params["id"]
    val idiom = params["idiom"]?.let { value -> Idiom.entries.firstOrNull { it.name.equals(value, ignoreCase = true) } }
    val mode = params["mode"]?.let { value -> ColorMode.entries.firstOrNull { it.name.equals(value, ignoreCase = true) } }
    ComposeViewport {
        if (id != null) {
            DemoScreen(
                id = id,
                idiom = idiom ?: Idiom.Desktop,
                colorMode = mode ?: ColorMode.System,
            )
        } else {
            CatalogApp()
        }
    }
}

private fun queryParams(): Map<String, String> =
    window.location.search.removePrefix("?")
        .split("&")
        .filter { it.isNotEmpty() }
        .mapNotNull { pair ->
            val i = pair.indexOf('=')
            if (i <= 0) null else pair.substring(0, i) to pair.substring(i + 1)
        }
        .toMap()
