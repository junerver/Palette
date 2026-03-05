package xyz.junerver.compose.palette.components.timepicker

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalTime
import xyz.junerver.compose.palette.components.textfield.BorderTextField
import xyz.junerver.compose.palette.core.spec.ComponentSize

@Composable
fun PTimePicker(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = xyz.junerver.compose.palette.core.theme.PaletteTheme.strings.timePickerPlaceholder,
    minuteStep: Int = TimePickerDefaults.DefaultMinuteStep,
    size: ComponentSize = ComponentSize.Medium,
) {
    val textValue = value?.let(::formatTime).orEmpty()

    BorderTextField(
        value = textValue,
        onValueChange = { input ->
            val parsed = parseTimeOrNull(input)
            if (parsed == null) {
                onValueChange(null)
                return@BorderTextField
            }
            if (!isTimeStepAligned(parsed, minuteStep)) return@BorderTextField
            onValueChange(parsed)
        },
        modifier = modifier,
        placeholder = placeholder,
        size = size,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = TimePickerDefaults.placeholderColor()
            )
        }
    )
}
