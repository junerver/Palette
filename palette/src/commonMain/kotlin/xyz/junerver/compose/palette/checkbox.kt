package xyz.junerver.compose.palette

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Description:
 * @author Junerver
 * @date: 2024/3/29-13:04
 * @Email: junerver@gmail.com
 * @Version: v1.0
 */
@Composable
fun ColoredCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors().copy(
            checkedBorderColor = color,
            checkedCheckmarkColor = color,
            checkedBoxColor = Color.Transparent,
            uncheckedBorderColor = color,
        ),
        modifier = modifier
    )
}