package xyz.junerver.compose.palette.components.datepicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import xyz.junerver.compose.palette.components.textfield.BorderTextField
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.spec.ComponentStatus

@Composable
fun PDatePicker(
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = xyz.junerver.compose.palette.core.theme.PaletteTheme.strings.datePickerPlaceholder,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    size: ComponentSize = ComponentSize.Medium,
) {
    val textValue = value?.let(DatePickerDefaults::format).orEmpty()

    BorderTextField(
        value = textValue,
        onValueChange = { input ->
            val parsed = parseDateOrNull(input)
            if (parsed == null) {
                onValueChange(null)
                return@BorderTextField
            }
            if (!isDateWithinRange(parsed, minDate, maxDate)) return@BorderTextField
            onValueChange(parsed)
        },
        modifier = modifier,
        placeholder = placeholder,
        size = size,
        status = ComponentStatus.Default,
        trailingIcon = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(DatePickerDefaults.CalendarIconSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (value != null) {
                    Text(
                        text = DatePickerDefaults.format(value),
                        color = DatePickerDefaults.textColor()
                    )
                }
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = DatePickerDefaults.placeholderColor()
                )
            }
        }
    )
}
