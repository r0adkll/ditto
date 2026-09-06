package com.r0adkll.ditto.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** One destination for [NavigationSuite]. */
@Immutable
public data class NavigationDestination(
  val label: String,
  val icon: ImageVector,
  val badge: String? = null,
)

/** Which container [NavigationSuite] picked for the current width. */
public enum class NavigationSuiteType { Bar, Rail, Sidebar }

public object NavigationSuiteDefaults {
  public val RailBreakpoint: Dp = 600.dp
  public val SidebarBreakpoint: Dp = 1200.dp

  public fun typeFor(width: Dp): NavigationSuiteType = when {
    width < RailBreakpoint -> NavigationSuiteType.Bar
    width < SidebarBreakpoint -> NavigationSuiteType.Rail
    else -> NavigationSuiteType.Sidebar
  }
}

/**
 * Picks a [NavigationBar], [NavigationRail] or [Sidebar] from the available width and lays the
 * screen out around it. [content] receives the padding a [Scaffold] would (bar heights and
 * uncovered insets); the side containers sit beside the content, not under it.
 */
@Composable
public fun NavigationSuite(
  destinations: List<NavigationDestination>,
  selectedIndex: Int,
  onSelect: (Int) -> Unit,
  modifier: Modifier = Modifier,
  topBar: @Composable () -> Unit = {},
  snackbarHost: @Composable () -> Unit = {},
  floatingAction: @Composable () -> Unit = {},
  type: ((Dp) -> NavigationSuiteType) = NavigationSuiteDefaults::typeFor,
  content: @Composable (PaddingValues) -> Unit,
) {
  BoxWithConstraints(modifier.fillMaxSize()) {
    when (type(maxWidth)) {
      NavigationSuiteType.Bar -> Scaffold(
        topBar = topBar,
        snackbarHost = snackbarHost,
        floatingAction = floatingAction,
        bottomBar = {
          NavigationBar {
            destinations.forEachIndexed { i, d ->
              NavigationItem(selected = i == selectedIndex, onClick = { onSelect(i) }, icon = d.icon, label = d.label, badge = d.badge)
            }
          }
        },
        content = content,
      )
      NavigationSuiteType.Rail -> Row(Modifier.fillMaxSize()) {
        NavigationRail {
          destinations.forEachIndexed { i, d ->
            NavigationItem(selected = i == selectedIndex, onClick = { onSelect(i) }, icon = d.icon, label = d.label, badge = d.badge)
          }
        }
        Scaffold(topBar = topBar, snackbarHost = snackbarHost, floatingAction = floatingAction, content = content)
      }
      NavigationSuiteType.Sidebar -> Row(Modifier.fillMaxSize()) {
        Sidebar {
          destinations.forEachIndexed { i, d ->
            SidebarItem(selected = i == selectedIndex, onClick = { onSelect(i) }, label = d.label, icon = d.icon, badge = d.badge)
          }
        }
        Scaffold(topBar = topBar, snackbarHost = snackbarHost, floatingAction = floatingAction, content = content)
      }
    }
  }
}
