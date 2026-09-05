package com.r0adkll.ditto.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import com.r0adkll.ditto.foundation.Text
import com.r0adkll.ditto.theme.DittoTheme

/**
 * Labeled radio options with proper group semantics: the whole row toggles, and the row (not the
 * circle) carries the `RadioButton` role, so screen readers announce "label, selected, 2 of 3".
 */
@Composable
public fun RadioGroup(
  options: List<String>,
  selectedIndex: Int,
  onSelect: (Int) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  val spacing = DittoTheme.spacing
  val haptics = rememberToggleHaptics()
  Column(modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
    options.forEachIndexed { index, label ->
      val selected = index == selectedIndex
      val interactionSource = remember { MutableInteractionSource() }
      Row(
        Modifier
          .fillMaxWidth()
          .clip(DittoTheme.shapes.small)
          .selectable(
            selected = selected,
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            enabled = enabled,
            role = Role.RadioButton,
            onClick = { if (!selected) { haptics.selected(); onSelect(index) } },
          )
          .defaultMinSize(minHeight = DittoTheme.dimens.minInteractiveSize)
          .padding(horizontal = spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        RadioButton(selected = selected, onClick = null, enabled = enabled, interactionSource = interactionSource)
        Spacer(Modifier.width(spacing.xs))
        Text(
          label,
          style = DittoTheme.typography.body,
          color = if (enabled) DittoTheme.colors.onSurface else DittoTheme.colors.onSurface.copy(alpha = DittoTheme.colors.disabledAlpha),
        )
      }
    }
  }
}
