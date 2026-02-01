package xyz.junerver.compose.palette.components.checkbox

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults as M3CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun ColoredCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    color: Color = CheckboxDefaults.color(),
    enabled: Boolean = true,
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = M3CheckboxDefaults.colors().copy(
            checkedBorderColor = color,
            checkedCheckmarkColor = color,
            checkedBoxColor = Color.Transparent,
            uncheckedBorderColor = color,
        )
    )
}
