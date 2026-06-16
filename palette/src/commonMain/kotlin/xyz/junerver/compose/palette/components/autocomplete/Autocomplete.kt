package xyz.junerver.compose.palette.components.autocomplete

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.textfield.BorderTextField
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.spec.ComponentStatus
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PAutocomplete(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<AutocompleteOption>,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    disabled: Boolean = false,
    size: ComponentSize = ComponentSize.Medium,
    status: ComponentStatus = ComponentStatus.Default,
    onSelect: ((AutocompleteOption) -> Unit)? = null,
    filterOption: ((String, AutocompleteOption) -> Boolean)? = null,
) {
    val (expanded, setExpanded) = useState(false)
    val (anchorWidth, setAnchorWidth) = useState(0)

    val filteredOptions = remember(options, value, filterOption) {
        if (value.isEmpty()) {
            emptyList()
        } else {
            val predicate = filterOption ?: { query, option ->
                option.label.contains(query, ignoreCase = true)
            }
            options.filter { predicate(value, it) }
        }
    }

    val density = LocalDensity.current
    val dropdownWidth = with(density) { anchorWidth.toDp() }

    val shouldShowDropdown = expanded && filteredOptions.isNotEmpty() && !disabled

    Box(
        modifier = modifier.onSizeChanged { setAnchorWidth(it.width) }
    ) {
        BorderTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                setExpanded(newValue.isNotEmpty())
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !disabled,
            size = size,
            status = status,
            placeholder = placeholder,
            clearable = !disabled,
        )

        DropdownMenu(
            expanded = shouldShowDropdown,
            onDismissRequest = { setExpanded(false) },
            modifier = Modifier
                .width(dropdownWidth)
                .background(PaletteTheme.colors.surface)
        ) {
            LazyColumn(
                modifier = Modifier.heightIn(max = AutocompleteDefaults.DropdownMaxHeight)
            ) {
                items(
                    items = filteredOptions,
                    key = { option -> option.value }
                ) { option ->
                    AutocompleteOptionItem(
                        option = option,
                        selected = option.label == value,
                        size = size,
                        onClick = {
                            if (!option.disabled) {
                                onSelect?.invoke(option)
                                onValueChange(option.label)
                                setExpanded(false)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AutocompleteOptionItem(
    option: AutocompleteOption,
    selected: Boolean,
    size: ComponentSize,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val backgroundColor = when {
        selected -> AutocompleteDefaults.selectedOptionColor().copy(alpha = 0.12f)
        isHovered -> AutocompleteDefaults.hoverOptionColor()
        else -> PaletteTheme.colors.surface
    }
    val textColor = when {
        option.disabled -> AutocompleteDefaults.disabledOptionColor()
        selected -> AutocompleteDefaults.selectedOptionColor()
        else -> AutocompleteDefaults.optionTextColor()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AutocompleteDefaults.CornerRadius))
            .background(backgroundColor)
            .clickable(
                enabled = !option.disabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(
                horizontal = AutocompleteDefaults.OptionPaddingHorizontal,
                vertical = size.verticalPadding,
            )
    ) {
        PText(
            text = option.label,
            fontSize = AutocompleteDefaults.FontSize,
            color = textColor,
            style = PaletteTheme.typography.body,
        )
    }
}
