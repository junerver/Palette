package xyz.junerver.compose.palette.components.select

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import xyz.junerver.compose.palette.components.textfield.BorderTextField
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.spec.ComponentStatus
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun <T> PSelect(
    options: List<SelectOption<T>>,
    value: T?,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: ComponentSize = ComponentSize.Medium,
    status: ComponentStatus = ComponentStatus.Default,
    placeholder: String = "",
    searchable: Boolean = false,
    searchPlaceholder: String = "",
    colors: SelectColors = SelectDefaults.colors(),
    optionContent: (@Composable (SelectOption<T>, Boolean) -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var anchorWidth by remember { mutableIntStateOf(0) }

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val selectedLabel = resolveSelectedLabel(
        options = options,
        value = value,
        placeholder = placeholder,
    )
    val filteredOptions = if (searchable) {
        filterSelectOptions(options, searchQuery)
    } else {
        options
    }
    val shape = RoundedCornerShape(size.cornerRadius)
    val borderColor = SelectDefaults.borderColor(
        status = status,
        isFocused = isFocused,
        isHovered = isHovered,
        enabled = enabled,
    )
    val density = LocalDensity.current
    val dropdownWidth = with(density) { anchorWidth.toDp() }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { anchorWidth = it.width }
                .height(size.height)
                .border(width = SelectDefaults.BorderWidth, color = borderColor, shape = shape)
                .clip(shape)
                .background(colors.containerColor)
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    expanded = shouldToggleExpanded(
                        currentExpanded = expanded,
                        enabled = enabled
                    )
                    if (!expanded) {
                        searchQuery = ""
                    }
                }
                .padding(horizontal = size.horizontalPadding, vertical = size.verticalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isPlaceholder = value == null || selectedLabel == placeholder
            Text(
                text = selectedLabel,
                modifier = Modifier.weight(1f),
                style = PaletteTheme.typography.body.copy(fontSize = size.fontSize),
                color = when {
                    !enabled -> colors.disabledTextColor
                    isPlaceholder -> colors.placeholderColor
                    else -> colors.textColor
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = if (enabled) colors.textColor else colors.disabledTextColor,
                modifier = Modifier.alpha(SelectDefaults.TrailingIconAlpha)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                searchQuery = ""
            },
            modifier = Modifier
                .width(dropdownWidth)
                .heightIn(max = SelectDefaults.DropdownMaxHeight)
                .background(colors.dropdownContainerColor)
        ) {
            if (searchable) {
                Box(
                    modifier = Modifier.padding(SelectDefaults.SearchFieldPadding)
                ) {
                    BorderTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = searchPlaceholder,
                        size = ComponentSize.Small,
                    )
                }
            }

            if (filteredOptions.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = SelectDefaults.noResultText(),
                            color = colors.placeholderColor
                        )
                    },
                    onClick = {},
                    enabled = false
                )
            } else {
                filteredOptions.forEach { option ->
                    val isSelected = value != null && option.value == value
                    val selectable = isOptionSelectable(option = option, enabled = enabled)

                    DropdownMenuItem(
                        text = {
                            if (optionContent != null) {
                                optionContent(option, isSelected)
                            } else {
                                Text(
                                    text = option.label,
                                    style = PaletteTheme.typography.body,
                                    color = when {
                                        !selectable -> colors.disabledOptionTextColor
                                        isSelected -> colors.selectedOptionTextColor
                                        else -> colors.optionTextColor
                                    }
                                )
                            }
                        },
                        onClick = {
                            if (selectable) {
                                onValueChange(option.value)
                                expanded = false
                                searchQuery = ""
                            }
                        },
                        enabled = selectable,
                        modifier = if (isSelected) {
                            Modifier
                                .padding(horizontal = SelectDefaults.SearchFieldPadding)
                                .clip(RoundedCornerShape(SelectDefaults.OptionCornerRadius))
                                .background(colors.selectedOptionContainerColor)
                        } else {
                            Modifier
                        }
                    )
                }
            }
        }
    }
}
