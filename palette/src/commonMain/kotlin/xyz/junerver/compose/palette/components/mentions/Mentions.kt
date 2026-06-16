package xyz.junerver.compose.palette.components.mentions

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.textfield.TextArea
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PMentions(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<MentionsOption>,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    disabled: Boolean = false,
    prefix: String = "@",
    onSelect: ((MentionsOption) -> Unit)? = null,
) {
    val (mentionQuery, setMentionQuery) = useState("")
    val (mentionTriggerIndex, setMentionTriggerIndex) = useState(-1)
    val (showDropdown, setShowDropdown) = useState(false)
    val (anchorWidth, setAnchorWidth) = useState(0)

    val density = LocalDensity.current
    val dropdownWidth = with(density) { anchorWidth.toDp() }

    val filteredOptions = remember(options, mentionQuery, prefix) {
        if (mentionQuery.length < prefix.length) {
            options
        } else {
            val query = mentionQuery.removePrefix(prefix)
            options.filter {
                it.label.contains(query, ignoreCase = true) ||
                    it.value.contains(query, ignoreCase = true)
            }
        }
    }

    fun detectMentionTrigger(text: String) {
        val lastAtIndex = text.lastIndexOf(prefix)
        if (lastAtIndex == -1) {
            setShowDropdown(false)
            setMentionTriggerIndex(-1)
            setMentionQuery("")
            return
        }
        val textAfterAt = text.substring(lastAtIndex)
        val hasSpace = textAfterAt.drop(prefix.length).indexOf(' ')
        if (hasSpace != -1) {
            setShowDropdown(false)
            setMentionTriggerIndex(-1)
            setMentionQuery("")
            return
        }
        setMentionTriggerIndex(lastAtIndex)
        setMentionQuery(textAfterAt)
        setShowDropdown(filteredOptions.isNotEmpty())
    }

    Box(
        modifier = modifier.onSizeChanged { setAnchorWidth(it.width) }
    ) {
        TextArea(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                if (!disabled) {
                    detectMentionTrigger(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !disabled,
            placeholder = placeholder,
            minLines = MentionsDefaults.MinLines,
        )

        if (showDropdown && filteredOptions.isNotEmpty() && !disabled) {
            Popup(
                onDismissRequest = { setShowDropdown(false) },
                popupPositionProvider = object : PopupPositionProvider {
                    override fun calculatePosition(
                        anchorBounds: IntRect,
                        windowSize: IntSize,
                        layoutDirection: LayoutDirection,
                        popupContentSize: IntSize,
                    ): IntOffset {
                        val x = anchorBounds.left
                        val spaceBelow = windowSize.height - anchorBounds.bottom
                        val spaceAbove = anchorBounds.top
                        val y =
                            if (spaceBelow >= popupContentSize.height || spaceBelow >= spaceAbove) {
                                anchorBounds.bottom
                            } else {
                                anchorBounds.top - popupContentSize.height
                            }
                        return IntOffset(x, y)
                    }
                },
                properties = PopupProperties(focusable = true)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .width(dropdownWidth)
                        .heightIn(max = MentionsDefaults.DropdownMaxHeight)
                        .clip(RoundedCornerShape(MentionsDefaults.CornerRadius))
                        .background(PaletteTheme.colors.surface)
                ) {
                    items(
                        items = filteredOptions,
                        key = { it.value }
                    ) { option ->
                        MentionsOptionItem(
                            option = option,
                            onClick = {
                                if (!option.disabled) {
                                    onSelect?.invoke(option)
                                    val beforeMention =
                                        value.substring(0, mentionTriggerIndex)
                                    val afterMention = value.substring(
                                        mentionTriggerIndex + mentionQuery.length
                                    )
                                    val newValue =
                                        "$beforeMention${prefix}${option.label} $afterMention"
                                    onValueChange(newValue)
                                    setShowDropdown(false)
                                    setMentionTriggerIndex(-1)
                                    setMentionQuery("")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MentionsOptionItem(
    option: MentionsOption,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val backgroundColor = when {
        isHovered -> PaletteTheme.colors.border
        else -> PaletteTheme.colors.surface
    }
    val textColor = when {
        option.disabled -> PaletteTheme.colors.hint
        else -> MentionsDefaults.optionTextColor()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MentionsDefaults.CornerRadius))
            .background(backgroundColor)
            .clickable(
                enabled = !option.disabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(
                horizontal = MentionsDefaults.OptionPaddingHorizontal,
                vertical = MentionsDefaults.OptionHeight / 4,
            )
    ) {
        PText(
            text = option.label,
            fontSize = MentionsDefaults.FontSize,
            color = textColor,
            style = PaletteTheme.typography.body,
        )
    }
}
