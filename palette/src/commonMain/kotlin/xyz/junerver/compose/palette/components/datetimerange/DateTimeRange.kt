package xyz.junerver.compose.palette.components.datetimerange

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xyz.junerver.compose.palette.components.textfield.BorderTextField
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PDateTimeRange(
    value: DateTimeRange?,
    onValueChange: (DateTimeRange?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = PaletteTheme.strings.dateTimeRangePlaceholder,
    size: ComponentSize = ComponentSize.Medium,
) {
    BorderTextField(
        value = value?.let(::formatDateTimeRange).orEmpty(),
        onValueChange = { input ->
            onValueChange(parseDateTimeRangeOrNull(input))
        },
        modifier = modifier,
        placeholder = placeholder,
        size = size,
    )
}
