package xyz.junerver.compose.palette.components.checkbox

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults as M3CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.core.spec.ComponentInteraction
import xyz.junerver.compose.palette.core.spec.rememberComponentInteraction

@Composable
fun ColoredCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    color: Color = CheckboxDefaults.color(),
    interaction: ComponentInteraction = rememberComponentInteraction(),
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = interaction.enabled,
        interactionSource = interaction.interactionSource,
        colors = M3CheckboxDefaults.colors().copy(
            checkedBorderColor = color,
            checkedCheckmarkColor = color,
            checkedBoxColor = Color.Transparent,
            uncheckedBorderColor = color,
        )
    )
}
